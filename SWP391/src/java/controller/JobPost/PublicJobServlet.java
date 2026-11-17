package controller.JobPost;

import dal.JobPostDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.JobPostModel;

/**
 * Public job board - anyone can browse without logging in.
 * URL: /jobs (list) or /jobs?id=X (detail + link to apply form)
 */
@WebServlet("/jobs")
public class PublicJobServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JobPostDAO jobPostDAO = new JobPostDAO();
        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.isBlank()) {
            int id = Integer.parseInt(idParam);
            JobPostModel job = jobPostDAO.getJobPostById(id);
            jobPostDAO.closeConnection();

            if (job == null || !job.isVisible()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Vị trí này không tồn tại hoặc đã đóng.");
                return;
            }
            request.setAttribute("job", job);
            request.getRequestDispatcher("/job_detail.jsp").forward(request, response);
        } else {
            request.setAttribute("jobList", jobPostDAO.getOpenJobPosts());
            jobPostDAO.closeConnection();
            request.getRequestDispatcher("/jobs.jsp").forward(request, response);
        }
    }
}
