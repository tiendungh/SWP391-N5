package controller.Interview;

import dal.ApplyDAO;
import dal.EvaluationDAO;
import dal.InterviewDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.ApplyModel;
import model.EvaluationModel;
import model.InterviewModel;
import tool.SessionUtil;

/**
 * HR/Manager conclusion step: review all interviews + evaluations for a
 * candidate's application, then record Pass/Fail/On hold with a note.
 * URL: /apply-conclusion?applyId=X
 */
@WebServlet("/apply-conclusion")
public class ApplyConclusionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        int applyId = Integer.parseInt(request.getParameter("applyId"));

        ApplyDAO applyDAO = new ApplyDAO();
        ApplyModel apply = applyDAO.getApplyById(applyId);
        applyDAO.closeConnection();

        if (apply == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hồ sơ ứng tuyển.");
            return;
        }

        InterviewDAO interviewDAO = new InterviewDAO();
        List<InterviewModel> interviews = interviewDAO.getInterviewsByApply(applyId);
        interviewDAO.closeConnection();

        EvaluationDAO evaluationDAO = new EvaluationDAO();
        Map<Integer, List<EvaluationModel>> evaluationsMap = new HashMap<>();
        for (InterviewModel iv : interviews) {
            evaluationsMap.put(iv.getInterviewId(), evaluationDAO.getEvaluationsByInterview(iv.getInterviewId()));
        }
        evaluationDAO.closeConnection();

        request.setAttribute("apply", apply);
        request.setAttribute("interviews", interviews);
        request.setAttribute("evaluationsMap", evaluationsMap);
        request.getRequestDispatcher("/interview/apply_conclusion.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        request.setCharacterEncoding("UTF-8");

        int applyId = Integer.parseInt(request.getParameter("applyId"));
        String finalResult = request.getParameter("finalResult");
        String conclusionNote = request.getParameter("conclusionNote");
        int staffId = SessionUtil.getStaffId(request);

        String newStatus;
        switch (finalResult) {
            case "Pass": newStatus = "Trúng tuyển"; break;
            case "Fail": newStatus = "Từ chối"; break;
            default: newStatus = "Đang xem xét"; break;
        }

        ApplyDAO applyDAO = new ApplyDAO();
        boolean ok = applyDAO.concludeApply(applyId, finalResult, conclusionNote, staffId, newStatus);
        applyDAO.closeConnection();

        if (!ok) {
            request.setAttribute("error", "Lưu kết luận thất bại, vui lòng thử lại.");
            doGet(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/interview-schedule?concluded=1");
    }
}
