package controller.Admin;

import dal.AccountDAO;
import dal.AccountRequestDAO;
import dal.RoleDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import model.AccountRequestModel;
import tool.EmailSender;
import tool.EncodePassword;
import tool.InputValidator;
import tool.PasswordGenerator;
import tool.SessionUtil;

/**
 * Admin-only: create a new Employee account. The account is created inactive
 * (Employee.IsActive = 0); the candidate receives an email with a temporary
 * password and an "Activate Account" link (see ActivateAccountServlet).
 *
 * Can also be opened as /admin/create-account?requestId=X to prefill the form
 * from a Manager's pending account request (see AccountRequestsServlet /
 * RequestAccountServlet). Submitting the form in that case both creates the
 * account and marks the originating request as Approved.
 * URL: /admin/create-account
 */
@WebServlet("/admin/create-account")
public class CreateAccountServlet extends HttpServlet {

    private static final long ACTIVATION_VALID_MS = 3L * 24 * 60 * 60 * 1000;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireAdminAccess(request, response)) return;

        RoleDAO roleDAO = new RoleDAO();
        request.setAttribute("roles", roleDAO.getAssignableEmployeeRoles());
        roleDAO.closeConnection();

        String requestIdRaw = request.getParameter("requestId");
        if (requestIdRaw != null && !requestIdRaw.isBlank()) {
            try {
                int requestId = Integer.parseInt(requestIdRaw.trim());
                AccountRequestDAO requestDAO = new AccountRequestDAO();
                AccountRequestModel accountRequest = requestDAO.getRequestById(requestId);
                requestDAO.closeConnection();

                if (accountRequest != null && "Pending".equals(accountRequest.getStatus())) {
                    request.setAttribute("prefill", accountRequest);
                    request.setAttribute("requestId", requestId);
                }
            } catch (NumberFormatException ignored) {
                // Bad requestId param - just render the blank form.
            }
        }

        request.getRequestDispatcher("/admin/create_account.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireAdminAccess(request, response)) return;

        request.setCharacterEncoding("UTF-8");

        String fullName = clean(request.getParameter("fullName"));
        String email = clean(request.getParameter("email"));
        String phone = clean(request.getParameter("phone"));
        String address = clean(request.getParameter("address"));
        String nationality = clean(request.getParameter("nationality"));
        String department = clean(request.getParameter("department"));
        String position = clean(request.getParameter("position"));
        String salaryRaw = clean(request.getParameter("salary"));
        String roleIdRaw = clean(request.getParameter("roleId"));
        String requestIdRaw = clean(request.getParameter("requestId"));

        if (fullName == null || email == null || phone == null || roleIdRaw == null) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ họ tên, email, số điện thoại và vai trò.");
            doGet(request, response);
            return;
        }

        if (!InputValidator.isValidPhone(phone)) {
            request.setAttribute("error", "Số điện thoại phải gồm đúng 10 chữ số.");
            doGet(request, response);
            return;
        }

        if (!InputValidator.isValidLength(fullName) || !InputValidator.isValidLength(email)
                || !InputValidator.isValidLength(address) || !InputValidator.isValidLength(nationality)
                || !InputValidator.isValidLength(department) || !InputValidator.isValidLength(position)) {
            request.setAttribute("error", "Các trường văn bản không được vượt quá 100 ký tự.");
            doGet(request, response);
            return;
        }

        Integer sourceRequestId = null;
        if (requestIdRaw != null) {
            try {
                sourceRequestId = Integer.parseInt(requestIdRaw);
            } catch (NumberFormatException ignored) {
                // Ignore a bad requestId - the account can still be created standalone.
            }
        }

        BigDecimal salary = null;
        if (salaryRaw != null) {
            try {
                salary = new BigDecimal(salaryRaw);
                if (salary.signum() < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Lương không hợp lệ.");
                doGet(request, response);
                return;
            }
        }

        int roleId;
        try {
            roleId = Integer.parseInt(roleIdRaw);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Vai trò không hợp lệ.");
            doGet(request, response);
            return;
        }

        String tempPassword = PasswordGenerator.generate(10);
        String passwordHash = EncodePassword.encodePasswordbyHash(tempPassword);

        AccountDAO accountDAO = new AccountDAO();
        int result = accountDAO.createEmployeeAccount(fullName, email, phone, address, nationality,
                department, position, salary, roleId, passwordHash);

        if (result == -2) {
            accountDAO.closeConnection();
            request.setAttribute("error", "Email này đã có tài khoản trong hệ thống.");
            doGet(request, response);
            return;
        }
        if (result < 0) {
            accountDAO.closeConnection();
            request.setAttribute("error", "Tạo tài khoản thất bại, vui lòng thử lại.");
            doGet(request, response);
            return;
        }

        if (sourceRequestId != null) {
            AccountRequestDAO requestDAO = new AccountRequestDAO();
            requestDAO.markApproved(sourceRequestId, SessionUtil.getAdminId(request), result);
            requestDAO.closeConnection();
        }

        String rawToken = PasswordGenerator.generateRawToken();
        String tokenHash = PasswordGenerator.hashToken(rawToken);
        Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + ACTIVATION_VALID_MS);
        boolean tokenSaved = accountDAO.insertActivationToken(email, tokenHash, expiresAt);
        accountDAO.closeConnection();

        if (!tokenSaved) {
            // The account itself was created fine - just the token row failed.
            response.sendRedirect(request.getContextPath() + "/admin/create-account?created=1&tokenFailed=1");
            return;
        }

        String activationLink = buildActivationLink(request, rawToken);
        boolean emailSent = EmailSender.sendActivationEmail(email, fullName, tempPassword, activationLink);

        // For testing/dev purposes (SMTP may not be configured in this environment),
        // always surface the activation link and temp password back to the admin so
        // the activation flow can be exercised end-to-end without a working mailbox.
        String linkParam = encode(activationLink);
        String tempParam = encode(tempPassword);

        if (!emailSent) {
            response.sendRedirect(request.getContextPath()
                    + "/admin/create-account?created=1&mailFailed=1&link=" + linkParam + "&temp=" + tempParam);
            return;
        }

        response.sendRedirect(request.getContextPath()
                + "/admin/create-account?created=1&link=" + linkParam + "&temp=" + tempParam);
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private String buildActivationLink(HttpServletRequest request, String token) {
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        boolean isDefaultPort = (("http".equals(scheme) && port == 80) || ("https".equals(scheme) && port == 443));

        StringBuilder base = new StringBuilder();
        base.append(scheme).append("://").append(host);
        if (!isDefaultPort) base.append(":").append(port);
        base.append(request.getContextPath()).append("/activate-account?token=").append(token);
        return base.toString();
    }

    private String clean(String value) {
        if (value == null) return null;
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }
}
