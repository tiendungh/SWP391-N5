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
import java.net.URI;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.ApplyModel;
import model.InterviewModel;
import tool.SessionUtil;

/**
 * Two views in one servlet: - /interview-schedule -> worklist of applications
 * that can be scheduled - /interview-schedule?applyId=X -> scheduling form for
 * that specific application POST creates the Interview + its participant list.
 *
 * Meeting rules: Online needs a meeting link, Offline needs an address, Phone needs neither.
 */
@WebServlet("/interview-schedule")
public class InterviewScheduleServlet extends HttpServlet {

    static final List<String> TYPES = List.of("Offline", "Online", "Phone");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) {
            return;
        }

        String applyIdParam = request.getParameter("applyId");

        if (applyIdParam != null && !applyIdParam.isBlank()) {
            int applyId;
            try {
                applyId = Integer.parseInt(applyIdParam.trim());
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hồ sơ ứng tuyển.");
                return;
            }
            showForm(request, response, applyId);
        } else {
            ApplyDAO applyDAO = new ApplyDAO();
            request.setAttribute("applies", applyDAO.getActiveApplies());
            applyDAO.closeConnection();
            request.getRequestDispatcher("/interview/interview_schedule.jsp").forward(request, response);
        }
    }

    /** Loads everything the scheduling form needs and shows it (also used to re-show it after an error). */
    private void showForm(HttpServletRequest request, HttpServletResponse response, int applyId)
            throws ServletException, IOException {
        ApplyDAO applyDAO = new ApplyDAO();
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
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");

        int applyId;
        try {
            applyId = Integer.parseInt(request.getParameter("applyId"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu mã hồ sơ ứng tuyển.");
            return;
        }

        String baremIdStr = trim(request.getParameter("baremId"));
        String roundStr = trim(request.getParameter("interviewRound"));
        String dateStr = trim(request.getParameter("interviewDate"));
        String startStr = trim(request.getParameter("startTime"));
        String endStr = trim(request.getParameter("endTime"));
        String type = trim(request.getParameter("interviewType"));
        String location = trim(request.getParameter("location"));
        String meetingLink = trim(request.getParameter("meetingLink"));
        String note = trim(request.getParameter("note"));
        String[] participantIds = request.getParameterValues("participantIds");
        String leadIdStr = trim(request.getParameter("leadInterviewerId"));

        String error = validate(baremIdStr, roundStr, dateStr, startStr, endStr, type,
                location, meetingLink, participantIds, leadIdStr, LocalDate.now());

        if (error != null) {
            // keep what the user selected in the participant list, then show the form again with the error
            Set<Integer> selected = new HashSet<>();
            if (participantIds != null) {
                for (String s : participantIds) {
                    try {
                        selected.add(Integer.parseInt(s));
                    } catch (NumberFormatException ignored) {
                        // ignore garbage
                    }
                }
            }
            request.setAttribute("selectedParticipantIds", selected);
            request.setAttribute("selectedLeadId", leadIdStr);
            request.setAttribute("error", error);
            showForm(request, response, applyId);
            return;
        }

        // ---- build the interview (values are validated, so parsing cannot fail here) ----
        InterviewModel iv = new InterviewModel();
        iv.setApplyId(applyId);
        iv.setBaremId(Integer.parseInt(baremIdStr));
        iv.setInterviewRound(Integer.parseInt(roundStr));
        iv.setInterviewDate(Date.valueOf(LocalDate.parse(dateStr)));
        iv.setStartTime(Time.valueOf(LocalTime.parse(startStr)));
        iv.setEndTime(endStr.isEmpty() ? null : Time.valueOf(LocalTime.parse(endStr)));
        iv.setInterviewType(type);
        // only the field that belongs to the chosen format is stored
        iv.setLocation("Offline".equals(type) ? location : null);
        iv.setMeetingLink("Online".equals(type) ? meetingLink : null);
        iv.setNote(note);
        iv.setCreatedBy(SessionUtil.getStaffId(request));

        List<Integer> ids = new ArrayList<>();
        for (String s : participantIds) {
            ids.add(Integer.parseInt(s));
        }
        Integer leadId = leadIdStr.isEmpty() ? null : Integer.parseInt(leadIdStr);

        InterviewDAO interviewDAO = new InterviewDAO();
        int newId = interviewDAO.scheduleInterview(iv, ids, leadId);
        interviewDAO.closeConnection();

        if (newId == -1) {
            request.setAttribute("error", "Đặt lịch phỏng vấn thất bại, vui lòng kiểm tra lại thông tin.");
            showForm(request, response, applyId);
            return;
        }

        ApplyDAO applyDAO = new ApplyDAO();
        applyDAO.updateStatus(applyId, "Interviewing");
        applyDAO.closeConnection();

        response.sendRedirect(request.getContextPath() + "/interview-calendar?scheduled=1");
    }

    /**
     * Validates the scheduling form. Returns an error message, or null when everything is valid.
     * Online needs a valid http(s) meeting link, Offline needs an address, Phone needs neither.
     */
    static String validate(String baremId, String round, String date, String start, String end, String type,
            String location, String meetingLink, String[] participantIds, String leadId, LocalDate today) {

        if (!isInt(baremId)) {
            return "Vui lòng chọn barem đánh giá.";
        }
        if (!isInt(round) || Integer.parseInt(round) < 1) {
            return "Vòng phỏng vấn không hợp lệ.";
        }

        if (date.isEmpty()) {
            return "Vui lòng chọn ngày phỏng vấn.";
        }
        LocalDate interviewDate;
        try {
            interviewDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return "Ngày phỏng vấn không hợp lệ.";
        }
        if (interviewDate.isBefore(today)) {
            return "Ngày phỏng vấn không được ở trong quá khứ.";
        }

        if (start.isEmpty()) {
            return "Vui lòng chọn giờ bắt đầu.";
        }
        LocalTime startTime;
        try {
            startTime = LocalTime.parse(start);
        } catch (DateTimeParseException e) {
            return "Giờ bắt đầu không hợp lệ.";
        }
        if (!end.isEmpty()) {
            try {
                if (!LocalTime.parse(end).isAfter(startTime)) {
                    return "Giờ kết thúc phải lớn hơn giờ bắt đầu.";
                }
            } catch (DateTimeParseException e) {
                return "Giờ kết thúc không hợp lệ.";
            }
        }

        if (!TYPES.contains(type)) {
            return "Vui lòng chọn hình thức phỏng vấn.";
        }
        if ("Online".equals(type)) {
            if (meetingLink.isEmpty()) {
                return "Phỏng vấn trực tuyến: vui lòng nhập link họp.";
            }
            if (!isHttpUrl(meetingLink)) {
                return "Link họp không hợp lệ. Link phải bắt đầu bằng http:// hoặc https://";
            }
        }
        if ("Offline".equals(type) && location.isEmpty()) {
            return "Phỏng vấn trực tiếp: vui lòng nhập địa chỉ.";
        }

        if (participantIds == null || participantIds.length == 0) {
            return "Vui lòng chọn ít nhất một người phỏng vấn.";
        }
        for (String id : participantIds) {
            if (!isInt(id)) {
                return "Danh sách người phỏng vấn không hợp lệ.";
            }
        }
        if (!leadId.isEmpty()) {
            if (!isInt(leadId) || !List.of(participantIds).contains(leadId)) {
                return "Trưởng nhóm phải nằm trong danh sách người phỏng vấn đã chọn.";
            }
        }
        return null;
    }

    private static boolean isInt(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isHttpUrl(String s) {
        try {
            URI uri = new URI(s);
            String scheme = uri.getScheme();
            return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    && uri.getHost() != null && !uri.getHost().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
