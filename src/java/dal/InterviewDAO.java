package dal;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.InterviewModel;
import model.StaffModel;

public class InterviewDAO extends DBContext {

    private static final String BASE_SELECT =
            "SELECT i.InterviewID, i.ApplyID, a.JobPostID, jp.Title AS JobTitle, "
            + "a.CandidateID, cand.CandidateName, "
            + "i.BaremID, b.BaremName, i.InterviewRound, i.InterviewDate, i.StartTime, i.EndTime, "
            + "i.InterviewType, i.Location, i.MeetingLink, i.Status, i.Note, "
            + "i.CreatedBy, ecand.CandidateName AS CreatedByName, i.CreatedAt, "
            + "(SELECT COUNT(*) FROM InterviewEvaluation ie WHERE ie.InterviewID = i.InterviewID) AS EvaluationCount "
            + "FROM Interview i "
            + "JOIN Apply a ON i.ApplyID = a.ApplyID "
            + "JOIN JobPost jp ON a.JobPostID = jp.JobPostID "
            + "JOIN Candidate cand ON a.CandidateID = cand.CandidateID "
            + "JOIN InterviewBarem b ON i.BaremID = b.BaremID "
            + "JOIN Employee e ON i.CreatedBy = e.EmployeeID "
            + "JOIN Candidate ecand ON e.CandidateID = ecand.CandidateID ";

    public int getNextRoundNumber(int applyId) {
        if (c == null) return 1;
        String sql = "SELECT ISNULL(MAX(InterviewRound), 0) + 1 AS NextRound FROM Interview WHERE ApplyID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, applyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("NextRound");
            }
        } catch (Exception e) {
            System.err.println("InterviewDAO.getNextRoundNumber error: " + e.getMessage());
        }
        return 1;
    }

    /**
     * Creates the Interview row plus its InterviewParticipant rows in one transaction.
     * Returns the new InterviewID, or -1 on failure.
     */
    public int scheduleInterview(InterviewModel iv, List<Integer> participantStaffIds, Integer leadStaffId) {
        if (c == null) return -1;

        String ivSql = "INSERT INTO Interview "
                + "(ApplyID, BaremID, InterviewRound, InterviewDate, StartTime, EndTime, InterviewType, "
                + "Location, MeetingLink, Note, CreatedBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String partSql = "INSERT INTO InterviewParticipant (InterviewID, EmployeeID, ParticipantRole, IsLeadInterviewer) "
                + "VALUES (?, ?, ?, ?)";

        try {
            c.setAutoCommit(false);

            int newInterviewId;
            try (PreparedStatement ps = c.prepareStatement(ivSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, iv.getApplyId());
                ps.setInt(2, iv.getBaremId());
                ps.setInt(3, iv.getInterviewRound());
                ps.setDate(4, iv.getInterviewDate());
                ps.setTime(5, iv.getStartTime());
                ps.setTime(6, iv.getEndTime());
                ps.setString(7, iv.getInterviewType());
                ps.setString(8, iv.getLocation());
                ps.setString(9, iv.getMeetingLink());
                ps.setString(10, iv.getNote());
                ps.setInt(11, iv.getCreatedBy());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new Exception("Không lấy được InterviewID vừa tạo.");
                    newInterviewId = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = c.prepareStatement(partSql)) {
                for (Integer staffId : participantStaffIds) {
                    ps.setInt(1, newInterviewId);
                    ps.setInt(2, staffId);
                    ps.setString(3, "Interviewer");
                    ps.setBoolean(4, leadStaffId != null && leadStaffId.equals(staffId));
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            c.commit();
            return newInterviewId;
        } catch (Exception e) {
            System.err.println("InterviewDAO.scheduleInterview error: " + e.getMessage());
            try { c.rollback(); } catch (Exception ignored) {}
            return -1;
        } finally {
            try { c.setAutoCommit(true); } catch (Exception ignored) {}
        }
    }

    public InterviewModel getInterviewById(int interviewId) {
        if (c == null) return null;
        String sql = BASE_SELECT + "WHERE i.InterviewID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, interviewId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    InterviewModel iv = mapRow(rs);
                    iv.setParticipants(getParticipants(interviewId));
                    return iv;
                }
            }
        } catch (Exception e) {
            System.err.println("InterviewDAO.getInterviewById error: " + e.getMessage());
        }
        return null;
    }

    public List<InterviewModel> getInterviewsByApply(int applyId) {
        List<InterviewModel> list = new ArrayList<>();
        if (c == null) return list;
        String sql = BASE_SELECT + "WHERE i.ApplyID = ? ORDER BY i.InterviewRound";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, applyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InterviewModel iv = mapRow(rs);
                    iv.setParticipants(getParticipants(iv.getInterviewId()));
                    list.add(iv);
                }
            }
        } catch (Exception e) {
            System.err.println("InterviewDAO.getInterviewsByApply error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Calendar/schedule view for HR: all interviews, optionally filtered by exact
     * date and/or by a participating staff member. Ordered so the JSP can group
     * rows by date easily.
     */
    public List<InterviewModel> getInterviewsForCalendar(Date date, Integer staffId) {
        List<InterviewModel> list = new ArrayList<>();
        if (c == null) return list;

        StringBuilder sql = new StringBuilder(BASE_SELECT);
        List<Object> params = new ArrayList<>();

        boolean hasStaffFilter = staffId != null;

        sql.append(hasStaffFilter
                ? "WHERE EXISTS (SELECT 1 FROM InterviewParticipant ip WHERE ip.InterviewID = i.InterviewID AND ip.EmployeeID = ?) "
                : "WHERE 1 = 1 ");
        if (hasStaffFilter) params.add(staffId);

        if (date != null) {
            sql.append("AND i.InterviewDate = ? ");
            params.add(date);
        }

        sql.append("ORDER BY i.InterviewDate, i.StartTime");

        try (PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object p : params) {
                if (p instanceof Integer) ps.setInt(idx++, (Integer) p);
                else if (p instanceof Date) ps.setDate(idx++, (Date) p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InterviewModel iv = mapRow(rs);
                    iv.setParticipants(getParticipants(iv.getInterviewId()));
                    list.add(iv);
                }
            }
        } catch (Exception e) {
            System.err.println("InterviewDAO.getInterviewsForCalendar error: " + e.getMessage());
        }
        return list;
    }

    private List<StaffModel> getParticipants(int interviewId) {
        List<StaffModel> list = new ArrayList<>();
        String sql = "SELECT e.EmployeeID AS StaffID, e.RoleID, r.RoleName, c.CandidateName AS FullName, "
                + "c.Email, c.PhoneNumber, ip.IsLeadInterviewer "
                + "FROM InterviewParticipant ip "
                + "JOIN Employee e ON ip.EmployeeID = e.EmployeeID "
                + "JOIN Candidate c ON e.CandidateID = c.CandidateID "
                + "JOIN Roles r ON e.RoleID = r.RoleID "
                + "WHERE ip.InterviewID = ? ORDER BY ip.IsLeadInterviewer DESC, c.CandidateName";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, interviewId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StaffModel s = new StaffModel();
                    s.setStaffId(rs.getInt("StaffID"));
                    s.setRoleId(rs.getInt("RoleID"));
                    s.setRoleName(rs.getString("RoleName"));
                    s.setFullName(rs.getString("FullName"));
                    s.setEmail(rs.getString("Email"));
                    s.setPhoneNumber(rs.getString("PhoneNumber"));
                    s.setLead(rs.getBoolean("IsLeadInterviewer"));
                    list.add(s);
                }
            }
        } catch (Exception e) {
            System.err.println("InterviewDAO.getParticipants error: " + e.getMessage());
        }
        return list;
    }

    private InterviewModel mapRow(ResultSet rs) throws Exception {
        InterviewModel iv = new InterviewModel();
        iv.setInterviewId(rs.getInt("InterviewID"));
        iv.setApplyId(rs.getInt("ApplyID"));
        iv.setJobPostId(rs.getInt("JobPostID"));
        iv.setJobTitle(rs.getString("JobTitle"));
        iv.setCandidateId(rs.getInt("CandidateID"));
        iv.setCandidateName(rs.getString("CandidateName"));
        iv.setBaremId(rs.getInt("BaremID"));
        iv.setBaremName(rs.getString("BaremName"));
        iv.setInterviewRound(rs.getInt("InterviewRound"));
        iv.setInterviewDate(rs.getDate("InterviewDate"));
        iv.setStartTime(rs.getTime("StartTime"));
        iv.setEndTime(rs.getTime("EndTime"));
        iv.setInterviewType(rs.getString("InterviewType"));
        iv.setLocation(rs.getString("Location"));
        iv.setMeetingLink(rs.getString("MeetingLink"));
        iv.setStatus(rs.getString("Status"));
        iv.setNote(rs.getString("Note"));
        iv.setCreatedBy(rs.getInt("CreatedBy"));
        iv.setCreatedByName(rs.getString("CreatedByName"));
        iv.setCreatedAt(rs.getTimestamp("CreatedAt"));
        iv.setEvaluationCount(rs.getInt("EvaluationCount"));
        return iv;
    }
}
