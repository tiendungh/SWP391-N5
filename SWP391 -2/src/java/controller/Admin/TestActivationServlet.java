package controller.Admin;

import dal.AccountDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import tool.SessionUtil;

/**
 * Admin-only tool page: lets an admin check the current activation state of
 * an Employee by email, and directly test the /activate-account link flow
 * without depending on the mail server. Does not touch production auth
 * itself - just wraps AccountDAO.getActivationStatus for inspection.
 * URL: /admin/test-activation
 */
@WebServlet("/admin/test-activation")
public class TestActivationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireAdminAccess(request, response)) return;
        request.getRequestDispatcher("/admin/test_activation.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireAdminAccess(request, response)) return;

        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        if (email != null) email = email.trim();

        if (email == null || email.isEmpty()) {
            request.setAttribute("checkError", "Vui lòng nhập email cần kiểm tra.");
            request.getRequestDispatcher("/admin/test_activation.jsp").forward(request, response);
            return;
        }

        AccountDAO dao = new AccountDAO();
        Boolean isActive;
        try {
            isActive = dao.getActivationStatus(email);
        } finally {
            dao.closeConnection();
        }

        request.setAttribute("checkedEmail", email);
        request.setAttribute("isActive", isActive);
        request.getRequestDispatcher("/admin/test_activation.jsp").forward(request, response);
    }
}
