package controller.Interview;

import dal.ApplyDAO;
import dal.BaremDAO;
import dal.InterviewDAO;
import dal.StaffDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import model.ApplyModel;
import model.InterviewModel;
import tool.SessionUtil;

/**
 * Two views in one servlet: - /interview-schedule -> worklist of applications
 * that can be scheduled - /interview-schedule?applyId=X -> scheduling form for
 * that specific application POST creates the Interview + its participant list.
 */
@WebServlet("/interview-schedule")
public class InterviewScheduleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) {
            return;
        }

        ApplyDAO applyDAO = new ApplyDAO();
        String applyIdParam = request.getParameter("applyId");

        if (applyIdParam != null && !applyIdParam.isBlank()) {
            int applyId = Integer.parseInt(applyIdParam);
            ApplyModel apply = applyDAO.getApplyById(applyId);
            applyDAO.closeConnection();

            if (apply == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hồ sơ ứng tuyển.");
                return;
            }

            BaremDAO baremDAO = new BaremDAO();
            StaffDAO staffDAO = new StaffDAO();
            InterviewDAO interviewDAO = new InterviewDAO();

            request.setAttribute("apply", apply);
            request.setAttribute("baremsForJob", baremDAO.getBaremsByJobPost(apply.getJobPostId()));
            request.setAttribute("allStaff", staffDAO.getActiveStaff());
            request.setAttribute("nextRound", interviewDAO.getNextRoundNumber(applyId));
            request.setAttribute("pastInterviews", interviewDAO.getInterviewsByApply(applyId));

            baremDAO.closeConnection();
            staffDAO.closeConnection();
            interviewDAO.closeConnection();

            request.getRequestDispatcher("/interview/interview_schedule.jsp").forward(request, response);
        } else {
            request.setAttribute("applies", applyDAO.getActiveApplies());
            applyDAO.closeConnection();
            request.getRequestDispatcher("/interview/interview_schedule.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");

        int applyId = Integer.parseInt(request.getParameter("applyId"));
        int baremId = Integer.parseInt(request.getParameter("baremId"));
        int round = Integer.parseInt(request.getParameter("interviewRound"));
        String dateStr = request.getParameter("interviewDate");
        String startStr = request.getParameter("startTime");
        String endStr = request.getParameter("endTime");
        String type = request.getParameter("interviewType");
        String location = request.getParameter("location");
        String meetingLink = request.getParameter("meetingLink");
        String note = request.getParameter("note");
        String[] participantIds = request.getParameterValues("participantIds");
        String leadIdStr = request.getParameter("leadInterviewerId");

        if (participantIds == null || participantIds.length == 0) {
            request.setAttribute("error", "Vui lòng chọn ít nhất một người phỏng vấn.");
            request.getRequestDispatcher("interview/interview-schedule.jsp").forward(request, response);
            return;
        }

        InterviewModel iv = new InterviewModel();
        iv.setApplyId(applyId);
        iv.setBaremId(baremId);
        iv.setInterviewRound(round);
        //iv.setInterviewDate(Date.valueOf(dateStr));
        //iv.setStartTime(Time.valueOf(startStr + ":00"));
        //iv.setEndTime(endStr == null || endStr.isBlank() ? null : Time.valueOf(endStr + ":00"));
        Date interviewDate = Date.valueOf(dateStr);
        Time startTime = Time.valueOf(startStr + ":00");
        Time endTime = endStr == null || endStr.isBlank()
                ? null
                : Time.valueOf(endStr + ":00");

        Date today = new Date(System.currentTimeMillis());

        if (interviewDate.before(today)) {
            request.setAttribute("error",
                    "Ngày phỏng vấn không được ở trong quá khứ.");
            request.getRequestDispatcher(
                    "/interview-schedule?applyId=" + applyId
            ).forward(request, response);
            return;
        }

        if (endTime != null && !endTime.after(startTime)) {
            request.setAttribute("error",
                    "Giờ kết thúc phải lớn hơn giờ bắt đầu.");
            request.getRequestDispatcher(
                    "/interview-schedule?applyId=" + applyId
            ).forward(request, response);
            return;
        }

        iv.setInterviewDate(interviewDate);
        iv.setStartTime(startTime);
        iv.setEndTime(endTime);
        iv.setInterviewType(type);
        iv.setLocation(location);
        iv.setMeetingLink(meetingLink);
        iv.setNote(note);
        iv.setCreatedBy(SessionUtil.getStaffId(request));

        List<Integer> ids = new ArrayList<>();
        for (String s : participantIds) {
            ids.add(Integer.parseInt(s));
        }
        Integer leadId = (leadIdStr != null && !leadIdStr.isBlank()) ? Integer.parseInt(leadIdStr) : null;

        InterviewDAO interviewDAO = new InterviewDAO();
        int newId = interviewDAO.scheduleInterview(iv, ids, leadId);
        interviewDAO.closeConnection();

        if (newId == -1) {
            request.setAttribute("error", "Đặt lịch phỏng vấn thất bại, vui lòng kiểm tra lại thông tin.");
            request.getRequestDispatcher("/interview-schedule?applyId=" + applyId).forward(request, response);
            return;
        }

        ApplyDAO applyDAO = new ApplyDAO();
        applyDAO.updateStatus(applyId, "Interviewing");
        applyDAO.closeConnection();

        response.sendRedirect(request.getContextPath() + "/interview-calendar?scheduled=1");
    }
}
