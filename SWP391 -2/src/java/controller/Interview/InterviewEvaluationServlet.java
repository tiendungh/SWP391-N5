package controller.Interview;

import dal.BaremDAO;
import dal.EvaluationDAO;
import dal.InterviewDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import model.BaremModel;
import model.CriteriaModel;
import model.EvaluationDetailModel;
import model.EvaluationModel;
import model.InterviewModel;
import model.StaffModel;
import tool.SessionUtil;

/**
 * Lets an assigned interviewer record scores per Barem criteria for one interview.
 * URL: /interview-evaluation?interviewId=X
 */
@WebServlet("/interview-evaluation")
public class InterviewEvaluationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        int interviewId = Integer.parseInt(request.getParameter("interviewId"));
        int staffId = SessionUtil.getStaffId(request);

        InterviewDAO interviewDAO = new InterviewDAO();
        InterviewModel interview = interviewDAO.getInterviewById(interviewId);
        interviewDAO.closeConnection();

        if (interview == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy lịch phỏng vấn.");
            return;
        }

        boolean isParticipant = false;
        for (StaffModel s : interview.getParticipants()) {
            if (s.getStaffId() == staffId) { isParticipant = true; break; }
        }
        if (!isParticipant) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không nằm trong danh sách phỏng vấn này.");
            return;
        }

        BaremDAO baremDAO = new BaremDAO();
        BaremModel barem = baremDAO.getBaremWithCriteria(interview.getBaremId());
        baremDAO.closeConnection();

        EvaluationDAO evaluationDAO = new EvaluationDAO();
        EvaluationModel existing = evaluationDAO.getEvaluation(interviewId, staffId);
        evaluationDAO.closeConnection();

        if (existing != null && barem != null) {
            for (CriteriaModel cm : barem.getCriteriaList()) {
                for (EvaluationDetailModel d : existing.getDetails()) {
                    if (d.getCriteriaId() == cm.getCriteriaId()) {
                        cm.setEnteredScore(d.getScore());
                        cm.setEnteredComment(d.getComment());
                    }
                }
            }
        }

        request.setAttribute("interview", interview);
        request.setAttribute("barem", barem);
        request.setAttribute("existing", existing);
        request.getRequestDispatcher("/interview/interview_evaluation.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        request.setCharacterEncoding("UTF-8");

        int interviewId = Integer.parseInt(request.getParameter("interviewId"));
        int staffId = SessionUtil.getStaffId(request);
        String recommendation = request.getParameter("recommendation");
        String comment = request.getParameter("comment");

        String[] criteriaIds = request.getParameterValues("criteriaId");
        String[] scores = request.getParameterValues("score");
        String[] comments = request.getParameterValues("detailComment");

        List<EvaluationDetailModel> details = new ArrayList<>();
        if (criteriaIds != null) {
            for (int i = 0; i < criteriaIds.length; i++) {
                EvaluationDetailModel d = new EvaluationDetailModel();
                d.setCriteriaId(Integer.parseInt(criteriaIds[i]));
                d.setScore(scores[i] == null || scores[i].isBlank() ? BigDecimal.ZERO : new BigDecimal(scores[i]));
                d.setComment(comments != null && i < comments.length ? comments[i] : null);
                details.add(d);
            }
        }

        EvaluationDAO evaluationDAO = new EvaluationDAO();
        boolean ok = evaluationDAO.submitEvaluation(interviewId, staffId, recommendation, comment, details);
        evaluationDAO.closeConnection();

        if (!ok) {
            request.setAttribute("error", "Lưu điểm đánh giá thất bại, vui lòng thử lại.");
            doGet(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/interview-calendar?evaluated=1");
    }
}
