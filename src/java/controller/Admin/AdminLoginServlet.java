package controller.Admin;

import dal.AdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.AdminModel;

@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            request.setAttribute("error", "Vui lòng nhập tên đăng nhập và mật khẩu.");
            doGet(request, response);
            return;
        }

        AdminDAO dao = new AdminDAO();
        AdminModel admin;
        try {
            admin = dao.authenticate(username.trim(), password);
        } finally {
            dao.closeConnection();
        }

        if (admin == null) {
            request.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu.");
            doGet(request, response);
            return;
        }

        // Regenerate the session on login to avoid session fixation.
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) oldSession.invalidate();

        HttpSession session = request.getSession(true);
        session.setAttribute("adminId", admin.getAdminId());
        session.setAttribute("adminUsername", admin.getUsername());
        session.setAttribute("adminRoles", admin.getRoleNames());

        response.sendRedirect(request.getContextPath() + "/admin/create-account");
    }
}
