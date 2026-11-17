package controller.Admin;

import dal.AccountRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import tool.SessionUtil;

/**
 * Admin-only: review the queue of account-creation requests filed by
 * Managers. Approving a request just forwards the admin to
 * /admin/create-account?requestId=X with the fields prefilled - the actual
 * account creation (and marking the request Approved) happens there once
 * submitted, so we never have two different code paths creating an Employee.
 * URL: /admin/account-requests
 */
@WebServlet("/admin/account-requests")
public class AccountRequestsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireAdminAccess(request, response)) return;

        AccountRequestDAO dao = new AccountRequestDAO();
        request.setAttribute("pendingRequests", dao.getPendingRequests());
        request.setAttribute("allRequests", dao.getAllRequests());
        dao.closeConnection();

        request.getRequestDispatcher("/admin/account_requests.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireAdminAccess(request, response)) return;

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String requestIdRaw = request.getParameter("requestId");

        int requestId;
        try {
            requestId = Integer.parseInt(requestIdRaw);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Yêu cầu không hợp lệ.");
            return;
        }

        if ("reject".equals(action)) {
            String note = request.getParameter("rejectionNote");
            if (note == null || note.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath()
                        + "/admin/account-requests?error=" + java.net.URLEncoder.encode(
                                "Vui lòng nhập lý do từ chối.", java.nio.charset.StandardCharsets.UTF_8));
                return;
            }

            AccountRequestDAO dao = new AccountRequestDAO();
            boolean ok = dao.markRejected(requestId, SessionUtil.getAdminId(request), note.trim());
            dao.closeConnection();

            response.sendRedirect(request.getContextPath() + "/admin/account-requests"
                    + (ok ? "?rejected=1" : "?error=1"));
            return;
        }

        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thao tác không hợp lệ.");
    }
}
