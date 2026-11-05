package controller.Career;

import dal.PromotionRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.PromotionRequestModel;
import tool.SessionUtil;

@WebServlet("/promotion-request")
public class PromotionReviewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireEmployeeAccess(request, response)) return;
        Integer requestId = parsePositiveInt(request.getParameter("id"));
        if (requestId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã đề nghị không hợp lệ.");
            return;
        }
        renderPage(request, response, requestId);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;
        request.setCharacterEncoding("UTF-8");

        Integer requestId = parsePositiveInt(request.getParameter("id"));
        String action = clean(request.getParameter("action"));
        String note = clean(request.getParameter("note"));
        if (requestId == null || action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thông tin xử lý không hợp lệ.");
            return;
        }
        if (note != null && note.length() > 2000) {
            request.setAttribute("error", "Nhận xét không được vượt quá 2000 ký tự.");
            renderPage(request, response, requestId);
            return;
        }
        if ("reject".equals(action) && note == null) {
            request.setAttribute("error", "Vui lòng nhập lý do từ chối.");
            renderPage(request, response, requestId);
            return;
        }

        PromotionRequestDAO dao = new PromotionRequestDAO();
        int actorId = SessionUtil.getStaffId(request);
        int result;
        String processed;
        if ("approve".equals(action)) {
            result = dao.approveRequest(requestId, actorId, note);
            processed = "approved";
        } else if ("reject".equals(action)) {
            result = dao.rejectRequest(requestId, actorId, note);
            processed = "rejected";
        } else if ("cancel".equals(action)) {
            result = dao.cancelRequest(requestId, actorId, note);
            processed = "cancelled";
        } else {
            dao.closeConnection();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thao tác không hợp lệ.");
            return;
        }
        dao.closeConnection();

        if (result == PromotionRequestDAO.ACTION_SUCCESS) {
            response.sendRedirect(request.getContextPath() + "/promotion-request?id="
                    + requestId + "&processed=" + processed);
            return;
        }

        request.setAttribute("error", getActionError(result));
        renderPage(request, response, requestId);
    }

    private void renderPage(HttpServletRequest request, HttpServletResponse response, int requestId)
            throws ServletException, IOException {
        PromotionRequestDAO dao = new PromotionRequestDAO();
        PromotionRequestModel promotion = dao.getRequestById(requestId);
        if (promotion == null) {
            dao.closeConnection();
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy đề nghị thăng chức.");
            return;
        }

        int actorId = SessionUtil.getStaffId(request);
        boolean staffAccess = SessionUtil.isHrOrManager(request);
        if (!staffAccess && promotion.getEmployeeId() != actorId) {
            dao.closeConnection();
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Bạn không có quyền xem đề nghị của nhân viên khác.");
            return;
        }

        request.setAttribute("promotion", promotion);
        request.setAttribute("promotionHistory", dao.getRequestHistory(requestId));
        request.setAttribute("staffAccess", staffAccess);
        request.setAttribute("canReview", staffAccess && "Pending".equals(promotion.getStatus())
                && promotion.getProposedBy() != actorId);
        request.setAttribute("canCancel", staffAccess && "Pending".equals(promotion.getStatus())
                && promotion.getProposedBy() == actorId);
        dao.closeConnection();
        request.getRequestDispatcher("/career/promotion_request_detail.jsp").forward(request, response);
    }

    private String getActionError(int result) {
        switch (result) {
            case PromotionRequestDAO.ACTION_NOT_FOUND:
                return "Không tìm thấy đề nghị thăng chức.";
            case PromotionRequestDAO.ACTION_NOT_PENDING:
                return "Đề nghị đã được xử lý và không còn ở trạng thái Pending.";
            case PromotionRequestDAO.ACTION_FORBIDDEN:
                return "Tài khoản hiện tại không có quyền xử lý đề nghị.";
            case PromotionRequestDAO.ACTION_SELF_REVIEW:
                return "Người tạo đề nghị không được tự phê duyệt hoặc từ chối.";
            case PromotionRequestDAO.ACTION_EMPLOYEE_CHANGED:
                return "Vị trí hoặc trạng thái nhân viên đã thay đổi. Hãy hủy đề nghị và tạo lại.";
            case PromotionRequestDAO.ACTION_EFFECTIVE_DATE_INVALID:
                return "Ngày hiệu lực không còn hợp lệ với lịch sử vị trí hiện tại.";
            case PromotionRequestDAO.ACTION_NOTE_REQUIRED:
                return "Nhận xét không hợp lệ; từ chối bắt buộc phải có lý do.";
            case PromotionRequestDAO.ACTION_NOT_OWNER:
                return "Chỉ người tạo mới được hủy đề nghị này.";
            default:
                return "Không thể xử lý đề nghị. Hãy kiểm tra database và thử lại.";
        }
    }

    private Integer parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String clean(String value) {
        if (value == null) return null;
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }
}
