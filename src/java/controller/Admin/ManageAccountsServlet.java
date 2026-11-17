package controller.Admin;

import dal.AccountDAO;
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
import model.EmployeeAccountModel;
import tool.EncodePassword;
import tool.InputValidator;
import tool.PasswordGenerator;
import tool.SessionUtil;

/**
 * Admin-only: manage every Employee account - list, edit profile fields,
 * activate/deactivate, reset password, and delete (blocked if the account has
 * related history rows, e.g. interviews/evaluations/job posts it created).
 * URL: /admin/manage-accounts
 */
@WebServlet("/admin/manage-accounts")
public class ManageAccountsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireAdminAccess(request, response)) return;

        String editIdRaw = request.getParameter("editId");
        if (editIdRaw != null && !editIdRaw.isBlank()) {
            try {
                int editId = Integer.parseInt(editIdRaw.trim());
                EmployeeAccountModel account = new AccountDAO().getEmployeeAccountById(editId);
                if (account != null) request.setAttribute("editAccount", account);
            } catch (NumberFormatException ignored) {
                // Bad editId - just render the list without an edit form open.
            }
        }

        render(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireAdminAccess(request, response)) return;

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String employeeIdRaw = request.getParameter("employeeId");

        int employeeId;
        try {
            employeeId = Integer.parseInt(employeeIdRaw);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Yêu cầu không hợp lệ.");
            return;
        }

        AccountDAO accountDAO = new AccountDAO();

        switch (action == null ? "" : action) {
            case "update":
                handleUpdate(request, response, accountDAO, employeeId);
                return;
            case "activate":
                accountDAO.setActive(employeeId, true);
                accountDAO.closeConnection();
                redirect(response, request, "activated=1");
                return;
            case "deactivate":
                accountDAO.setActive(employeeId, false);
                accountDAO.closeConnection();
                redirect(response, request, "deactivated=1");
                return;
            case "resetPassword":
                handleResetPassword(request, response, accountDAO, employeeId);
                return;
            case "delete":
                handleDelete(request, response, accountDAO, employeeId);
                return;
            default:
                accountDAO.closeConnection();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thao tác không hợp lệ.");
        }
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response,
            AccountDAO accountDAO, int employeeId) throws IOException {

        String fullName = clean(request.getParameter("fullName"));
        String email = clean(request.getParameter("email"));
        String phone = clean(request.getParameter("phone"));
        String department = clean(request.getParameter("department"));
        String position = clean(request.getParameter("position"));
        String salaryRaw = clean(request.getParameter("salary"));
        String roleIdRaw = clean(request.getParameter("roleId"));

        if (fullName == null || email == null || phone == null || roleIdRaw == null) {
            accountDAO.closeConnection();
            redirect(response, request, "editId=" + employeeId
                    + "&error=" + encode("Vui lòng nhập đầy đủ họ tên, email, số điện thoại và vai trò."));
            return;
        }

        if (!InputValidator.isValidPhone(phone)) {
            accountDAO.closeConnection();
            redirect(response, request, "editId=" + employeeId
                    + "&error=" + encode("Số điện thoại phải gồm đúng 10 chữ số."));
            return;
        }

        if (!InputValidator.isValidLength(fullName) || !InputValidator.isValidLength(email)
                || !InputValidator.isValidLength(department) || !InputValidator.isValidLength(position)) {
            accountDAO.closeConnection();
            redirect(response, request, "editId=" + employeeId
                    + "&error=" + encode("Các trường văn bản không được vượt quá 100 ký tự."));
            return;
        }

        if (accountDAO.emailUsedByAnother(email, employeeId)) {
            accountDAO.closeConnection();
            redirect(response, request, "editId=" + employeeId
                    + "&error=" + encode("Email này đã được dùng bởi một tài khoản khác."));
            return;
        }

        int roleId;
        BigDecimal salary = null;
        try {
            roleId = Integer.parseInt(roleIdRaw);
            if (salaryRaw != null) {
                salary = new BigDecimal(salaryRaw);
                if (salary.signum() < 0) throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            accountDAO.closeConnection();
            redirect(response, request, "editId=" + employeeId
                    + "&error=" + encode("Vai trò hoặc lương không hợp lệ."));
            return;
        }

        boolean ok = accountDAO.updateEmployeeAccount(employeeId, fullName, email, phone,
                department, position, salary, roleId);
        accountDAO.closeConnection();

        redirect(response, request, ok ? "updated=1" : "error=" + encode("Cập nhật thất bại, vui lòng thử lại."));
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response,
            AccountDAO accountDAO, int employeeId) throws IOException {

        String tempPassword = PasswordGenerator.generate(10);
        String hash = EncodePassword.encodePasswordbyHash(tempPassword);
        boolean ok = accountDAO.resetPassword(employeeId, hash);
        accountDAO.closeConnection();

        if (!ok) {
            redirect(response, request, "error=" + encode("Đặt lại mật khẩu thất bại."));
            return;
        }
        // Surfaced back to the admin directly (dev/test visibility) since there is
        // no "email the new password" flow wired up for this action yet.
        redirect(response, request, "resetPassword=1&newPassword=" + encode(tempPassword));
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response,
            AccountDAO accountDAO, int employeeId) throws IOException {

        int result = accountDAO.deleteEmployeeAccount(employeeId);
        accountDAO.closeConnection();

        if (result == -2) {
            redirect(response, request, "error=" + encode(
                    "Không thể xoá: tài khoản này còn dữ liệu liên quan (lịch phỏng vấn, đánh giá, hồ sơ...). "
                    + "Hãy vô hiệu hoá tài khoản thay vì xoá."));
            return;
        }
        if (result != 1) {
            redirect(response, request, "error=" + encode("Xoá tài khoản thất bại."));
            return;
        }
        redirect(response, request, "deleted=1");
    }

    private void render(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AccountDAO accountDAO = new AccountDAO();
        request.setAttribute("accounts", accountDAO.getAllEmployeeAccounts());
        accountDAO.closeConnection();

        RoleDAO roleDAO = new RoleDAO();
        request.setAttribute("roles", roleDAO.getAssignableEmployeeRoles());
        roleDAO.closeConnection();

        request.getRequestDispatcher("/admin/manage_accounts.jsp").forward(request, response);
    }

    private void redirect(HttpServletResponse response, HttpServletRequest request, String query)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/admin/manage-accounts?" + query);
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private String clean(String value) {
        if (value == null) return null;
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }
}
