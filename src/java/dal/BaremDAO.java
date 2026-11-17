package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.BaremModel;
import model.CriteriaModel;

public class BaremDAO extends DBContext {

    /** Active barems already created for a given job post (used when scheduling an interview). */
    public List<BaremModel> getBaremsByJobPost(int jobPostId) {
        List<BaremModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = "SELECT BaremID, JobPostID, BaremName, Description, TotalScore, CreatedBy, CreatedAt, IsActive "
                + "FROM InterviewBarem WHERE JobPostID = ? AND IsActive = 1 ORDER BY CreatedAt DESC";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, jobPostId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("BaremDAO.getBaremsByJobPost error: " + e.getMessage());
        }
        return list;
    }

    /** Full barem including its ordered criteria list - needed to render the scoring form. */
    public BaremModel getBaremWithCriteria(int baremId) {
        if (c == null) return null;
        BaremModel barem = null;

        String sql = "SELECT b.BaremID, b.JobPostID, jp.Title AS JobTitle, b.BaremName, b.Description, "
                + "b.TotalScore, b.CreatedBy, c.CandidateName AS CreatedByName, b.CreatedAt, b.IsActive "
                + "FROM InterviewBarem b "
                + "JOIN JobPost jp ON b.JobPostID = jp.JobPostID "
                + "JOIN Employee e ON b.CreatedBy = e.EmployeeID "
                + "JOIN Candidate c ON e.CandidateID = c.CandidateID "
                + "WHERE b.BaremID = ?";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, baremId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    barem = new BaremModel();
                    barem.setBaremId(rs.getInt("BaremID"));
                    barem.setJobPostId(rs.getInt("JobPostID"));
                    barem.setJobTitle(rs.getString("JobTitle"));
                    barem.setBaremName(rs.getString("BaremName"));
                    barem.setDescription(rs.getString("Description"));
                    barem.setTotalScore(rs.getBigDecimal("TotalScore"));
                    barem.setCreatedBy(rs.getInt("CreatedBy"));
                    barem.setCreatedByName(rs.getString("CreatedByName"));
                    barem.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    barem.setActive(rs.getBoolean("IsActive"));
                }
            }
        } catch (Exception e) {
            System.err.println("BaremDAO.getBaremWithCriteria error: " + e.getMessage());
            return null;
        }

        if (barem == null) return null;

        String critSql = "SELECT CriteriaID, BaremID, CriteriaName, Description, MaxScore, Weight, DisplayOrder "
                + "FROM BaremCriteria WHERE BaremID = ? ORDER BY DisplayOrder";
        try (PreparedStatement ps = c.prepareStatement(critSql)) {
            ps.setInt(1, baremId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CriteriaModel cm = new CriteriaModel();
                    cm.setCriteriaId(rs.getInt("CriteriaID"));
                    cm.setBaremId(rs.getInt("BaremID"));
                    cm.setCriteriaName(rs.getString("CriteriaName"));
                    cm.setDescription(rs.getString("Description"));
                    cm.setMaxScore(rs.getBigDecimal("MaxScore"));
                    cm.setWeight(rs.getBigDecimal("Weight"));
                    cm.setDisplayOrder(rs.getInt("DisplayOrder"));
                    barem.getCriteriaList().add(cm);
                }
            }
        } catch (Exception e) {
            System.err.println("BaremDAO.getBaremWithCriteria criteria error: " + e.getMessage());
        }
        return barem;
    }

    /**
     * Quick-create: inserts the InterviewBarem header plus all of its criteria rows
     * in a single transaction. Returns the new BaremID, or -1 on failure.
     */
    public int createBaremWithCriteria(BaremModel barem, List<CriteriaModel> criteriaList) {
        if (c == null) return -1;

        String baremSql = "INSERT INTO InterviewBarem (JobPostID, BaremName, Description, TotalScore, CreatedBy) "
                + "VALUES (?, ?, ?, ?, ?)";
        String critSql = "INSERT INTO BaremCriteria (BaremID, CriteriaName, Description, MaxScore, Weight, DisplayOrder) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            c.setAutoCommit(false);

            int newBaremId;
            try (PreparedStatement ps = c.prepareStatement(baremSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, barem.getJobPostId());
                ps.setString(2, barem.getBaremName());
                ps.setString(3, barem.getDescription());
                ps.setBigDecimal(4, barem.getTotalScore());
                ps.setInt(5, barem.getCreatedBy());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new Exception("Không lấy được BaremID vừa tạo.");
                    newBaremId = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = c.prepareStatement(critSql)) {
                int order = 1;
                for (CriteriaModel cm : criteriaList) {
                    ps.setInt(1, newBaremId);
                    ps.setString(2, cm.getCriteriaName());
                    ps.setString(3, cm.getDescription());
                    ps.setBigDecimal(4, cm.getMaxScore());
                    ps.setBigDecimal(5, cm.getWeight());
                    ps.setInt(6, order++);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            c.commit();
            return newBaremId;
        } catch (Exception e) {
            System.err.println("BaremDAO.createBaremWithCriteria error: " + e.getMessage());
            try { c.rollback(); } catch (Exception ignored) {}
            return -1;
        } finally {
            try { c.setAutoCommit(true); } catch (Exception ignored) {}
        }
    }

    private BaremModel mapRow(ResultSet rs) throws Exception {
        BaremModel b = new BaremModel();
        b.setBaremId(rs.getInt("BaremID"));
        b.setJobPostId(rs.getInt("JobPostID"));
        b.setBaremName(rs.getString("BaremName"));
        b.setDescription(rs.getString("Description"));
        b.setTotalScore(rs.getBigDecimal("TotalScore"));
        b.setCreatedBy(rs.getInt("CreatedBy"));
        b.setCreatedAt(rs.getTimestamp("CreatedAt"));
        b.setActive(rs.getBoolean("IsActive"));
        return b;
    }
}
