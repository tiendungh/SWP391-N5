package controller.Interview;

import dal.BaremDAO;
import dal.JobPostDAO;
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
import model.JobPostModel;
import tool.SessionUtil;

/**
 * Lets HR/Manager quickly create an Interview Barem (with its scoring criteria)
 * for a specific open job post, and lists barems already created for that post.
 * URL: /barem?jobPostId=X (&returnApplyId=Y to bounce back into scheduling)
 */
@WebServlet("/barem")
public class BaremServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        JobPostDAO jobPostDAO = new JobPostDAO();
        BaremDAO baremDAO = new BaremDAO();

        List<JobPostModel> openJobs = jobPostDAO.getOpenJobPosts();
        request.setAttribute("openJobs", openJobs);

        String jobPostIdParam = request.getParameter("jobPostId");
        if (jobPostIdParam != null && !jobPostIdParam.isBlank()) {
            int jobPostId = Integer.parseInt(jobPostIdParam);
            request.setAttribute("selectedJobPost", jobPostDAO.getJobPostById(jobPostId));
            request.setAttribute("existingBarems", baremDAO.getBaremsByJobPost(jobPostId));
            request.setAttribute("jobPostId", jobPostId);
        }

        String returnApplyId = request.getParameter("returnApplyId");
        if (returnApplyId != null && !returnApplyId.isBlank()) {
            request.setAttribute("returnApplyId", returnApplyId);
        }

        jobPostDAO.closeConnection();
        baremDAO.closeConnection();

        request.getRequestDispatcher("/interview/barem_manage.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        request.setCharacterEncoding("UTF-8");

        int jobPostId = Integer.parseInt(request.getParameter("jobPostId"));
        String baremName = request.getParameter("baremName");
        String description = request.getParameter("description");
        String returnApplyId = request.getParameter("returnApplyId");

        String[] critNames = request.getParameterValues("criteriaName");
        String[] critDescs = request.getParameterValues("criteriaDescription");
        String[] critMax = request.getParameterValues("maxScore");
        String[] critWeight = request.getParameterValues("weight");

        List<CriteriaModel> criteriaList = new ArrayList<>();
        BigDecimal totalScore = BigDecimal.ZERO;

        if (critNames != null) {
            for (int i = 0; i < critNames.length; i++) {
                if (critNames[i] == null || critNames[i].isBlank()) continue;
                CriteriaModel cm = new CriteriaModel();
                cm.setCriteriaName(critNames[i]);
                cm.setDescription(critDescs != null && i < critDescs.length ? critDescs[i] : null);
                BigDecimal max = new BigDecimal(critMax[i]);
                BigDecimal weight = new BigDecimal(critWeight[i]);
                cm.setMaxScore(max);
                cm.setWeight(weight);
                criteriaList.add(cm);
                totalScore = totalScore.add(max);
            }
        }

        if (baremName == null || baremName.isBlank() || criteriaList.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập tên barem và ít nhất một tiêu chí.");
            doGet(request, response);
            return;
        }

        BaremModel barem = new BaremModel();
        barem.setJobPostId(jobPostId);
        barem.setBaremName(baremName);
        barem.setDescription(description);
        barem.setTotalScore(totalScore);
        barem.setCreatedBy(SessionUtil.getStaffId(request));

        BaremDAO baremDAO = new BaremDAO();
        int newBaremId = baremDAO.createBaremWithCriteria(barem, criteriaList);
        baremDAO.closeConnection();

        if (newBaremId == -1) {
            request.setAttribute("error", "Tạo barem thất bại, vui lòng thử lại.");
            doGet(request, response);
            return;
        }

        if (returnApplyId != null && !returnApplyId.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/interview-schedule?applyId=" + returnApplyId
                    + "&baremCreated=1");
        } else {
            response.sendRedirect(request.getContextPath() + "/barem?jobPostId=" + jobPostId + "&created=1");
        }
    }
}
