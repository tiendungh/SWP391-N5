package controller.Interview;

import dal.InterviewDAO;
import dal.StaffDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import tool.SessionUtil;

/**
 * HR-facing schedule of all interviews across all candidates, optionally
 * filtered by exact date and/or by the staff member taking part.
 * URL: /interview-calendar?date=YYYY-MM-DD&staffId=X
 */
@WebServlet("/interview-calendar")
public class InterviewCalendarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        String dateParam = request.getParameter("date");
        String staffIdParam = request.getParameter("staffId");

        Date date = (dateParam != null && !dateParam.isBlank()) ? Date.valueOf(dateParam) : null;
        Integer staffId = (staffIdParam != null && !staffIdParam.isBlank()) ? Integer.parseInt(staffIdParam) : null;

        InterviewDAO interviewDAO = new InterviewDAO();
        StaffDAO staffDAO = new StaffDAO();

        request.setAttribute("interviews", interviewDAO.getInterviewsForCalendar(date, staffId));
        request.setAttribute("allStaff", staffDAO.getActiveStaff());
        request.setAttribute("selectedDate", dateParam);
        request.setAttribute("selectedStaffId", staffIdParam);

        interviewDAO.closeConnection();
        staffDAO.closeConnection();

        request.getRequestDispatcher("/interview/interview_calendar.jsp").forward(request, response);
    }
}
