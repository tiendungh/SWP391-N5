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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.JobPostModel;
import tool.SessionUtil;

/**
 * HR/Manager tool to create, edit and manage Job Descriptions (JobPost).
 * URL: /job-post  (GET ?editId=X opens the edit form for that JD)
 */
@WebServlet("/job-post")
public class JobPostServlet extends HttpServlet {

    private static final String VIEW = "/interview/jobpost_manage.jsp";
    private static final List<String> JOB_TYPES = List.of("Full-time", "Part-time", "Internship", "Remote");

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
            j.setTitle(trim(request.getParameter("title")).replaceAll("\\s+", " "));
            j.setDescription(trim(request.getParameter("description")));
            j.setCategory(trim(request.getParameter("category")));
            j.setPosition(trim(request.getParameter("position")));
            j.setLocation(trim(request.getParameter("location")));
            j.setTypeJob(trim(request.getParameter("typeJob")));
            j.setVisible("on".equals(request.getParameter("visible")));

            String offerMinStr = trim(request.getParameter("offerMin"));
            String offerMaxStr = trim(request.getParameter("offerMax"));
            String numberExpStr = trim(request.getParameter("numberExp"));
            String deadlineStr = trim(request.getParameter("deadline"));

            // ---- 1. every field is required ----
            String error = null;
            List<String> missing = missingFields(j, offerMinStr, offerMaxStr, numberExpStr, deadlineStr);
            if (!missing.isEmpty()) {
                error = "Vui lòng điền đầy đủ các mục bắt buộc: " + String.join(", ", missing) + ".";
            }

            // ---- 2. parse numbers and date ----
            if (error == null) {
                try {
                    j.setOfferMin(new BigDecimal(offerMinStr));
                    j.setOfferMax(new BigDecimal(offerMaxStr));
                    j.setNumberExp(Integer.parseInt(numberExpStr));
                    j.setDeadline(Date.valueOf(deadlineStr));
                } catch (IllegalArgumentException e) {   // NumberFormatException is a subclass
                    error = "Lương, kinh nghiệm hoặc hạn nộp không hợp lệ.";
                }
            }

            // ---- 3. which JD is being edited ----
            JobPostModel existing = null;
            if (isUpdate) {
                try {
                    j.setJobPostId(Integer.parseInt(request.getParameter("jobPostId")));
                    existing = jobPostDAO.getJobPostById(j.getJobPostId());
                } catch (NumberFormatException e) {
                    // handled below
                }
                if (existing == null && error == null) {
                    error = "Không tìm thấy JD cần sửa.";
                }
            }

            // ---- 4. business rules (salary range, deadline, job type) ----
            if (error == null) {
                error = ruleError(j, LocalDate.now(), existing == null ? null : existing.getDeadline());
            }

            // ---- 5. the title must be unique among all JDs ----
            if (error == null && jobPostDAO.titleExists(j.getTitle(), isUpdate ? j.getJobPostId() : -1)) {
                error = "Tiêu đề \"" + j.getTitle() + "\" đã tồn tại. Vui lòng chọn tiêu đề khác.";
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

    /** Names of the required fields that were left empty (every field of the form is required). */
    static List<String> missingFields(JobPostModel j, String offerMin, String offerMax, String numberExp, String deadline) {
        List<String> missing = new ArrayList<>();
        if (j.getTitle().isEmpty()) missing.add("Tiêu đề");
        if (j.getDescription().isEmpty()) missing.add("Mô tả công việc");
        if (j.getCategory().isEmpty()) missing.add("Phòng ban / Category");
        if (j.getPosition().isEmpty()) missing.add("Vị trí");
        if (j.getLocation().isEmpty()) missing.add("Địa điểm");
        if (j.getTypeJob().isEmpty()) missing.add("Loại hình");
        if (offerMin.isEmpty()) missing.add("Lương tối thiểu");
        if (offerMax.isEmpty()) missing.add("Lương tối đa");
        if (numberExp.isEmpty()) missing.add("Kinh nghiệm tối thiểu");
        if (deadline.isEmpty()) missing.add("Hạn nộp hồ sơ");
        return missing;
    }

    /**
     * Business rules on a fully parsed JD. Returns an error message, or null when valid.
     * @param existingDeadline stored deadline when editing (an unchanged past deadline is tolerated), null when creating.
     */
    static String ruleError(JobPostModel j, LocalDate today, Date existingDeadline) {
        if (!JOB_TYPES.contains(j.getTypeJob())) {
            return "Loại hình không hợp lệ.";
        }
        if (j.getNumberExp() < 0) {
            return "Kinh nghiệm tối thiểu phải là số không âm.";
        }
        if (j.getOfferMin().signum() < 0 || j.getOfferMax().signum() < 0) {
            return "Mức lương không được âm.";
        }
        if (j.getOfferMin().compareTo(j.getOfferMax()) >= 0) {
            return "Lương tối thiểu phải nhỏ hơn lương tối đa.";
        }
        LocalDate deadline = j.getDeadline().toLocalDate();
        boolean unchanged = existingDeadline != null && existingDeadline.toLocalDate().equals(deadline);
        if (deadline.isBefore(today) && !unchanged) {
            return "Hạn nộp hồ sơ không được nằm trong quá khứ.";
        }
        return null;
    }

    private void render(HttpServletRequest request, HttpServletResponse response, JobPostDAO jobPostDAO)
            throws ServletException, IOException {
        request.setAttribute("today", LocalDate.now().toString());
        request.setAttribute("jobPosts", jobPostDAO.getAllJobPosts());
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
