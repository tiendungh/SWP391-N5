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
 * HR/Manager tool to create and manage Job Descriptions (JobPost).
 * URL: /job-post
 */
@WebServlet("/job-post")
public class JobPostServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        JobPostDAO jobPostDAO = new JobPostDAO();
        request.setAttribute("jobPosts", jobPostDAO.getAllJobPosts());
        jobPostDAO.closeConnection();

        request.getRequestDispatcher("/interview/jobpost_manage.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        JobPostDAO jobPostDAO = new JobPostDAO();

        if ("toggle".equals(action)) {
            int jobPostId = Integer.parseInt(request.getParameter("jobPostId"));
            boolean newVisible = "1".equals(request.getParameter("newVisible"));
            jobPostDAO.setVisible(jobPostId, newVisible);
            jobPostDAO.closeConnection();
            response.sendRedirect(request.getContextPath() + "/job-post");
            return;
        }

        // action == create (default)
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String category = request.getParameter("category");
        String position = request.getParameter("position");
        String location = request.getParameter("location");
        String offerMinStr = request.getParameter("offerMin");
        String offerMaxStr = request.getParameter("offerMax");
        String numberExpStr = request.getParameter("numberExp");
        String typeJob = request.getParameter("typeJob");
        String deadlineStr = request.getParameter("deadline");
        boolean visible = "on".equals(request.getParameter("visible"));

        if (title == null || title.isBlank() || description == null || description.isBlank()
                || location == null || location.isBlank()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ Tiêu đề, Mô tả và Địa điểm.");
            request.setAttribute("jobPosts", jobPostDAO.getAllJobPosts());
            jobPostDAO.closeConnection();
            request.getRequestDispatcher("/interview/jobpost_manage.jsp").forward(request, response);
            return;
        }

        JobPostModel j = new JobPostModel();
        j.setTitle(title);
        j.setDescription(description);
        j.setCategory(category);
        j.setPosition(position);
        j.setLocation(location);
        j.setOfferMin(offerMinStr == null || offerMinStr.isBlank() ? null : new BigDecimal(offerMinStr));
        j.setOfferMax(offerMaxStr == null || offerMaxStr.isBlank() ? null : new BigDecimal(offerMaxStr));
        j.setNumberExp(numberExpStr == null || numberExpStr.isBlank() ? null : Integer.parseInt(numberExpStr));
        j.setTypeJob(typeJob);
        j.setDeadline(deadlineStr == null || deadlineStr.isBlank() ? null : Date.valueOf(deadlineStr));
        j.setVisible(visible);
        j.setCreatedBy(SessionUtil.getStaffId(request));

        int newId = jobPostDAO.createJobPost(j);
        jobPostDAO.closeConnection();

        if (newId == -1) {
            request.setAttribute("error", "Tạo JD thất bại, vui lòng thử lại.");
            request.getRequestDispatcher("/job-post").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/job-post?created=1");
    }
}
