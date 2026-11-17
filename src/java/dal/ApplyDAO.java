package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.ApplyModel;

public class ApplyDAO extends DBContext {

    private static final String BASE_SELECT =
            "SELECT a.ApplyID, a.JobPostID, jp.Title AS JobTitle, "
            + "a.CandidateID, c.CandidateName, c.Email AS CandidateEmail, c.PhoneNumber AS CandidatePhone, "
            + "a.CVID, cv.Position AS CvPosition, cv.NumberExp AS CvNumberExp, cv.Education AS CvEducation, "
            + "cv.Field AS CvField, cv.CurrentSalary AS CvCurrentSalary, cv.Birthday AS CvBirthday, "
            + "cv.Nationality AS CvNationality, cv.Gender AS CvGender, cv.FileData AS CvFileData, "
            + "a.Status, a.Note, a.FinalResult, a.ConclusionNote, a.ConclusionBy, cb.CandidateName AS ConclusionByName, "
            + "a.ConclusionDate, a.DayCreate, "
            + "(SELECT COUNT(*) FROM Interview i WHERE i.ApplyID = a.ApplyID) AS ScheduledInterviewCount "
            + "FROM Apply a "
            + "JOIN Candidate c ON a.CandidateID = c.CandidateID "
            + "JOIN JobPost jp ON a.JobPostID = jp.JobPostID "
            + "JOIN CV cv ON a.CVID = cv.CVID "
            + "LEFT JOIN Employee ceb ON a.ConclusionBy = ceb.EmployeeID "
            + "LEFT JOIN Candidate cb ON ceb.CandidateID = cb.CandidateID ";

    /** Shortlisted (post-screening), not-yet-concluded applications - the interview scheduling worklist. */
    public List<ApplyModel> getActiveApplies() {
        List<ApplyModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = BASE_SELECT + "WHERE a.FinalResult IS NULL AND a.Status = 'Shortlisted' ORDER BY a.DayCreate DESC";

        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            System.err.println("ApplyDAO.getActiveApplies error: " + e.getMessage());
        }
        return list;
    }

    public ApplyModel getApplyById(int applyId) {
        if (c == null) return null;
        String sql = BASE_SELECT + "WHERE a.ApplyID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, applyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) {
            System.err.println("ApplyDAO.getApplyById error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Screening list for one JD, with optional filters. Any filter left null/blank is skipped.
     * statusFilter defaults to 'Pending' by the caller when the HR user hasn't decided otherwise.
     */
    public List<ApplyModel> getAppliesForScreening(int jobPostId, Integer minExp, String educationKeyword,
            java.math.BigDecimal minSalary, java.math.BigDecimal maxSalary, String statusFilter) {
        List<ApplyModel> list = new ArrayList<>();
        if (c == null) return list;

        StringBuilder sql = new StringBuilder(BASE_SELECT + "WHERE a.JobPostID = ? ");
        List<Object> params = new ArrayList<>();
        params.add(jobPostId);

        if (minExp != null) {
            sql.append("AND cv.NumberExp >= ? ");
            params.add(minExp);
        }
        if (educationKeyword != null && !educationKeyword.isBlank()) {
            sql.append("AND cv.Education LIKE ? ");
            params.add("%" + educationKeyword + "%");
        }
        if (minSalary != null) {
            sql.append("AND cv.CurrentSalary >= ? ");
            params.add(minSalary);
        }
        if (maxSalary != null) {
            sql.append("AND cv.CurrentSalary <= ? ");
            params.add(maxSalary);
        }
        if (statusFilter != null && !statusFilter.isBlank() && !"All".equals(statusFilter)) {
            sql.append("AND a.Status = ? ");
            params.add(statusFilter);
        }
        sql.append("ORDER BY a.DayCreate DESC");

        try (PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object p : params) {
                if (p instanceof Integer) ps.setInt(idx++, (Integer) p);
                else if (p instanceof java.math.BigDecimal) ps.setBigDecimal(idx++, (java.math.BigDecimal) p);
                else ps.setString(idx++, (String) p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            System.err.println("ApplyDAO.getAppliesForScreening error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Creates a new application from the public apply form. Returns the new ApplyID,
     * -2 if the candidate already applied to this JD (unique constraint), -1 on other failure.
     */
    public int createApply(int jobPostId, int candidateId, int cvId, String note) {
        if (c == null) return -1;
        String sql = "INSERT INTO Apply (JobPostID, CandidateID, CVID, Note) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, jobPostId);
            ps.setInt(2, candidateId);
            ps.setInt(3, cvId);
            ps.setString(4, note);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("UQ_Apply_Candidate_Job")) {
                return -2;
            }
            System.err.println("ApplyDAO.createApply error: " + e.getMessage());
        }
        return -1;
    }

    public boolean updateStatus(int applyId, String status) {
        if (c == null) return false;
        String sql = "UPDATE Apply SET Status = ? WHERE ApplyID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, applyId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("ApplyDAO.updateStatus error: " + e.getMessage());
            return false;
        }
    }

    /** Bulk screening decision: mark several applications Shortlisted/Rejected in one go. */
    public int bulkUpdateStatus(List<Integer> applyIds, String status) {
        if (c == null || applyIds == null || applyIds.isEmpty()) return 0;
        String sql = "UPDATE Apply SET Status = ? WHERE ApplyID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (Integer id : applyIds) {
                ps.setString(1, status);
                ps.setInt(2, id);
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            int count = 0;
            for (int r : results) if (r > 0) count++;
            return count;
        } catch (Exception e) {
            System.err.println("ApplyDAO.bulkUpdateStatus error: " + e.getMessage());
            return 0;
        }
    }

    /** Records the HR/Manager conclusion for a candidate's application after interviews are done. */
    public boolean concludeApply(int applyId, String finalResult, String conclusionNote, int concludedByStaffId, String newStatus) {
        if (c == null) return false;
        String sql = "UPDATE Apply SET FinalResult = ?, ConclusionNote = ?, ConclusionBy = ?, "
                + "ConclusionDate = ?, Status = ? WHERE ApplyID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, finalResult);
            ps.setString(2, conclusionNote);
            ps.setInt(3, concludedByStaffId);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.setString(5, newStatus);
            ps.setInt(6, applyId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("ApplyDAO.concludeApply error: " + e.getMessage());
            return false;
        }
    }

    private ApplyModel mapRow(ResultSet rs) throws Exception {
        ApplyModel a = new ApplyModel();
        a.setApplyId(rs.getInt("ApplyID"));
        a.setJobPostId(rs.getInt("JobPostID"));
        a.setJobTitle(rs.getString("JobTitle"));
        a.setCandidateId(rs.getInt("CandidateID"));
        a.setCandidateName(rs.getString("CandidateName"));
        a.setCandidateEmail(rs.getString("CandidateEmail"));
        a.setCandidatePhone(rs.getString("CandidatePhone"));
        a.setCvId(rs.getInt("CVID"));
        a.setCvPosition(rs.getString("CvPosition"));
        int exp = rs.getInt("CvNumberExp");
        a.setCvNumberExp(rs.wasNull() ? null : exp);
        a.setCvEducation(rs.getString("CvEducation"));
        a.setCvField(rs.getString("CvField"));
        a.setCvCurrentSalary(rs.getBigDecimal("CvCurrentSalary"));
        a.setCvBirthday(rs.getDate("CvBirthday"));
        a.setCvNationality(rs.getString("CvNationality"));
        a.setCvGender(rs.getString("CvGender"));
        a.setCvFileData(rs.getString("CvFileData"));
        a.setStatus(rs.getString("Status"));
        a.setNote(rs.getString("Note"));
        a.setFinalResult(rs.getString("FinalResult"));
        a.setConclusionNote(rs.getString("ConclusionNote"));
        int conclusionBy = rs.getInt("ConclusionBy");
        a.setConclusionBy(rs.wasNull() ? null : conclusionBy);
        a.setConclusionByName(rs.getString("ConclusionByName"));
        a.setConclusionDate(rs.getTimestamp("ConclusionDate"));
        a.setDayCreate(rs.getTimestamp("DayCreate"));
        a.setScheduledInterviewCount(rs.getInt("ScheduledInterviewCount"));
        return a;
    }
}
