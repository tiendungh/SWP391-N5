package controller.JobPost;

import dal.ApplyDAO;
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
import tool.SessionUtil;

/**
 * HR/Manager screening workspace: pick a JD, filter its applications by
 * experience / education / expected salary, then Pass or Reject in bulk.
 * URL: /screening (pick a JD) or /screening?jobPostId=X (screen its applies)
 */
@WebServlet("/screening")
public class ScreeningServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        JobPostDAO jobPostDAO = new JobPostDAO();
        request.setAttribute("jobPosts", jobPostDAO.getAllJobPosts());
        jobPostDAO.closeConnection();

        String jobPostIdParam = request.getParameter("jobPostId");
        if (jobPostIdParam != null && !jobPostIdParam.isBlank()) {
            int jobPostId = Integer.parseInt(jobPostIdParam);

            Integer minExp = parseIntOrNull(request.getParameter("minExp"));
            String education = request.getParameter("education");
            BigDecimal minSalary = parseDecimalOrNull(request.getParameter("minSalary"));
            BigDecimal maxSalary = parseDecimalOrNull(request.getParameter("maxSalary"));
            String status = request.getParameter("status");
            if (status == null || status.isBlank()) status = "Pending";

            ApplyDAO applyDAO = new ApplyDAO();
            request.setAttribute("applies", applyDAO.getAppliesForScreening(jobPostId, minExp, education, minSalary, maxSalary, status));
            applyDAO.closeConnection();

            request.setAttribute("jobPostId", jobPostId);
            request.setAttribute("selectedMinExp", request.getParameter("minExp"));
            request.setAttribute("selectedEducation", education);
            request.setAttribute("selectedMinSalary", request.getParameter("minSalary"));
            request.setAttribute("selectedMaxSalary", request.getParameter("maxSalary"));
            request.setAttribute("selectedStatus", status);
        }

        request.getRequestDispatcher("/interview/screening.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        request.setCharacterEncoding("UTF-8");

        int jobPostId = Integer.parseInt(request.getParameter("jobPostId"));
        String decision = request.getParameter("decision"); // "Shortlisted" or "Rejected"
        String[] applyIdParams = request.getParameterValues("applyIds");

        if (applyIdParams != null && applyIdParams.length > 0
                && ("Shortlisted".equals(decision) || "Rejected".equals(decision))) {
            List<Integer> ids = new ArrayList<>();
            for (String s : applyIdParams) ids.add(Integer.parseInt(s));

            ApplyDAO applyDAO = new ApplyDAO();
            applyDAO.bulkUpdateStatus(ids, decision);
            applyDAO.closeConnection();
        }

        response.sendRedirect(request.getContextPath() + "/screening?jobPostId=" + jobPostId + "&updated=1");
    }

    private Integer parseIntOrNull(String s) {
        return (s == null || s.isBlank()) ? null : Integer.parseInt(s);
    }

    private BigDecimal parseDecimalOrNull(String s) {
        return (s == null || s.isBlank()) ? null : new BigDecimal(s);
    }
}
