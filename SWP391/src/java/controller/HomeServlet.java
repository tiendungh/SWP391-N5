package controller;

import dal.JobPostDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import jakarta.servlet.ServletException;
import java.util.List;
import model.JobPostModel;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<JobPostModel> jobs = new JobPostDAO().getOpenJobPosts();
        request.setAttribute("jobs", jobs);
        request.setAttribute("featuredJobs", jobs.size() > 6 ? jobs.subList(0, 6) : jobs);
        request.setAttribute("openJobCount", jobs.size());
        request.getRequestDispatcher("/home.jsp").forward(request, response);
    }
}
