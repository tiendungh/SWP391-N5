package controller.Interview;

import dal.AccountRequestDAO;
import dal.RoleDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import tool.InputValidator;
import tool.SessionUtil;

/**
 * HR Staff-only: ask Admin to create an Employee account for a given email.
 * HR Staff cannot create the account themselves - this just files a
 * request that shows up in Admin's queue (/admin/account-requests).
 * URL: /request-account
 */
@WebServlet("/request-account")
public class RequestAccountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireHrStaffAccess(request, response)) return;
        renderPage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireHrStaffAccess(request, response)) return;

        request.setCharacterEncoding("UTF-8");

        String email = clean(request.getParameter("requestedEmail"));
        String fullName = clean(request.getParameter("fullName"));
        String phone = clean(request.getParameter("phone"));
        String address = clean(request.getParameter("address"));
        String nationality = clean(request.getParameter("nationality"));
        String department = clean(request.getParameter("department"));
        String position = clean(request.getParameter("position"));
        String roleIdRaw = clean(request.getParameter("roleId"));
        String salaryRaw = clean(request.getParameter("salary"));
        String note = clean(request.getParameter("note"));

        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            request.setAttribute("error", "Vui lòng nhập một địa chỉ email hợp lệ.");
            renderPage(request, response);
            return;
        }

        if (phone != null && !InputValidator.isValidPhone(phone)) {
            request.setAttribute("error", "Số điện thoại phải gồm đúng 10 chữ số.");
            renderPage(request, response);
            return;
        }

        if (!InputValidator.isValidLength(email) || !InputValidator.isValidLength(fullName)
                || !InputValidator.isValidLength(address) || !InputValidator.isValidLength(nationality)
                || !InputValidator.isValidLength(department) || !InputValidator.isValidLength(position)
                || !InputValidator.isValidLength(note)) {
            request.setAttribute("error", "Các trường văn bản không được vượt quá 100 ký tự.");
            renderPage(request, response);
            return;
        }

        Integer roleId = null;
        if (roleIdRaw != null) {
            try {
                roleId = Integer.parseInt(roleIdRaw);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Vai trò không hợp lệ.");
                renderPage(request, response);
                return;
            }
        }

        BigDecimal salary = null;
        if (salaryRaw != null) {
            try {
                salary = new BigDecimal(salaryRaw);
                if (salary.signum() < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Lương không hợp lệ.");
                renderPage(request, response);
                return;
            }
        }

        AccountRequestDAO dao = new AccountRequestDAO();
        int newId = dao.createRequest(email, fullName, phone, address, nationality, department, position,
                roleId, salary, note, SessionUtil.getStaffId(request));
        dao.closeConnection();

        if (newId < 0) {
            request.setAttribute("error", "Gửi yêu cầu thất bại, vui lòng thử lại.");
            renderPage(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/request-account?sent=1");
    }

    private void renderPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RoleDAO roleDAO = new RoleDAO();
        request.setAttribute("roles", roleDAO.getAssignableEmployeeRoles());
        roleDAO.closeConnection();

        AccountRequestDAO dao = new AccountRequestDAO();
        request.setAttribute("myRequests", dao.getRequestsByManager(SessionUtil.getStaffId(request)));
        dao.closeConnection();

        request.getRequestDispatcher("/interview/request_account.jsp").forward(request, response);
    }

    private String clean(String value) {
        if (value == null) return null;
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }
}
