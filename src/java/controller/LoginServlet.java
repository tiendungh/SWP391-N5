/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.LoginDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 *
 * @author admin
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null || email.trim().isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email và mật khẩu.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        LoginDAO.UserSession userSession;
        LoginDAO dao = new LoginDAO();
        try {
            userSession = dao.loginUser(email.trim(), password);
        } finally {
            dao.closeConnection();
        }

        if (userSession != null) {
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) oldSession.invalidate();

            HttpSession session = request.getSession(true);
            session.setAttribute("staffId", userSession.staffId);
            session.setAttribute("staffName", userSession.fullName);
            session.setAttribute("email", userSession.email);
            session.setAttribute("roleId", userSession.roleId);
            session.setAttribute("roleName", userSession.roleName);

            if ("HR Staff".equals(userSession.roleName) || "Manager".equals(userSession.roleName)) {
                response.sendRedirect(request.getContextPath() + "/interview-schedule");
            } else {
                response.sendRedirect(request.getContextPath() + "/my-profile-documents");
            }
        } else {
            request.setAttribute("error", "Email hoặc mật khẩu không chính xác.");
            request.setAttribute("emailInput", email.trim());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
