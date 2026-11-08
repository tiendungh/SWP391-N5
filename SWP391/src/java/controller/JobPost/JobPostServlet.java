package controller.JobPost;

import dal.JobPostDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import model.JobPostModel;
import tool.SessionUtil;

/**
 * HR/Manager tool to create, edit and manage Job Descriptions (JobPost).
 * URL: /job-post  (GET ?editId=X opens the edit form for that JD)
 */
@WebServlet("/job-post")
public class JobPostServlet extends HttpServlet {

    private static final String VIEW = "/interview/jobpost_manage.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        JobPostDAO jobPostDAO = new JobPostDAO();
        try {
            String editIdParam = request.getParameter("editId");
            if (editIdParam != null && !editIdParam.isBlank()) {
                JobPostModel job = null;
                try {
                    job = jobPostDAO.getJobPostById(Integer.parseInt(editIdParam.trim()));
                } catch (NumberFormatException ignored) {
                    // falls through to "not found"
                }
                if (job == null) {
                    request.setAttribute("error", "Không tìm thấy JD cần sửa.");
                } else {
                    request.setAttribute("formJob", job);
                    request.setAttribute("editMode", true);
                }
            }
            render(request, response, jobPostDAO);
        } finally {
            jobPostDAO.closeConnection();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        JobPostDAO jobPostDAO = new JobPostDAO();

        try {
            if ("toggle".equals(action)) {
                int jobPostId = Integer.parseInt(request.getParameter("jobPostId"));
                boolean newVisible = "1".equals(request.getParameter("newVisible"));
                jobPostDAO.setVisible(jobPostId, newVisible);
                response.sendRedirect(request.getContextPath() + "/job-post");
                return;
            }

            boolean isUpdate = "update".equals(action);   // anything else = create (default)

            // ---- read the form ----
            JobPostModel j = new JobPostModel();
            j.setTitle(trim(request.getParameter("title")));
            j.setDescription(trim(request.getParameter("description")));
            j.setCategory(trim(request.getParameter("category")));
            j.setPosition(trim(request.getParameter("position")));
            j.setLocation(trim(request.getParameter("location")));
            j.setTypeJob(trim(request.getParameter("typeJob")));
            j.setVisible("on".equals(request.getParameter("visible")));

            String error = null;
            try {
                String offerMinStr = trim(request.getParameter("offerMin"));
                String offerMaxStr = trim(request.getParameter("offerMax"));
                String numberExpStr = trim(request.getParameter("numberExp"));
                String deadlineStr = trim(request.getParameter("deadline"));
                j.setOfferMin(offerMinStr.isEmpty() ? null : new BigDecimal(offerMinStr));
                j.setOfferMax(offerMaxStr.isEmpty() ? null : new BigDecimal(offerMaxStr));
                j.setNumberExp(numberExpStr.isEmpty() ? null : Integer.parseInt(numberExpStr));
                j.setDeadline(deadlineStr.isEmpty() ? null : Date.valueOf(deadlineStr));
            } catch (IllegalArgumentException e) {   // NumberFormatException is a subclass
                error = "Lương, kinh nghiệm hoặc hạn nộp không hợp lệ.";
            }

            if (isUpdate) {
                try {
                    j.setJobPostId(Integer.parseInt(request.getParameter("jobPostId")));
                } catch (NumberFormatException e) {
                    error = "Không xác định được JD cần sửa.";
                }
            }

            // ---- validate ----
            if (error == null && (j.getTitle().isEmpty() || j.getDescription().isEmpty() || j.getLocation().isEmpty())) {
                error = "Vui lòng nhập đầy đủ Tiêu đề, Mô tả và Địa điểm.";
            }
            if (error == null && j.getOfferMin() != null && j.getOfferMax() != null
                    && j.getOfferMin().compareTo(j.getOfferMax()) > 0) {
                error = "Lương tối thiểu không được lớn hơn lương tối đa.";
            }

            if (error == null) {
                if (isUpdate) {
                    if (jobPostDAO.updateJobPost(j)) {
                        response.sendRedirect(request.getContextPath() + "/job-post?updated=1");
                        return;
                    }
                    error = "Cập nhật JD thất bại, vui lòng thử lại.";
                } else {
                    j.setCreatedBy(SessionUtil.getStaffId(request));
                    if (jobPostDAO.createJobPost(j) != -1) {
                        response.sendRedirect(request.getContextPath() + "/job-post?created=1");
                        return;
                    }
                    error = "Tạo JD thất bại, vui lòng thử lại.";
                }
            }

            // ---- error: show the form again with what the user typed ----
            request.setAttribute("error", error);
            request.setAttribute("formJob", j);
            request.setAttribute("editMode", isUpdate);
            render(request, response, jobPostDAO);
        } finally {
            jobPostDAO.closeConnection();
        }
    }

    private void render(HttpServletRequest request, HttpServletResponse response, JobPostDAO jobPostDAO)
            throws ServletException, IOException {
        request.setAttribute("jobPosts", jobPostDAO.getAllJobPosts());
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
