package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.JobPostModel;

public class JobPostDAO extends DBContext {

    private static final String PUBLIC_SELECT =
            "SELECT JobPostID, Title, Description, Category, Position, Location, "
            + "OfferMin, OfferMax, NumberExp, Visible, TypeJob, Deadline, DayCreate, CreatedBy "
            + "FROM JobPost ";

    /** Open job posts visible to the public (used by /jobs and interview scheduling). */
    public List<JobPostModel> getOpenJobPosts() {
        List<JobPostModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = PUBLIC_SELECT + "WHERE Visible = 1 ORDER BY DayCreate DESC";

        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            System.err.println("JobPostDAO.getOpenJobPosts error: " + e.getMessage());
        }
        return list;
    }

    /** All job posts (open + closed) for the HR management screen, newest first. */
    public List<JobPostModel> getAllJobPosts() {
        List<JobPostModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = "SELECT jp.JobPostID, jp.Title, jp.Description, jp.Category, jp.Position, jp.Location, "
                + "jp.OfferMin, jp.OfferMax, jp.NumberExp, jp.Visible, jp.TypeJob, jp.Deadline, jp.DayCreate, "
                + "jp.CreatedBy, c.CandidateName AS CreatedByName, "
                + "(SELECT COUNT(*) FROM Apply a WHERE a.JobPostID = jp.JobPostID) AS ApplyCount "
                + "FROM JobPost jp "
                + "LEFT JOIN Employee e ON jp.CreatedBy = e.EmployeeID "
                + "LEFT JOIN Candidate c ON e.CandidateID = c.CandidateID "
                + "ORDER BY jp.DayCreate DESC";

        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JobPostModel j = mapRow(rs);
                j.setCreatedByName(rs.getString("CreatedByName"));
                j.setApplyCount(rs.getInt("ApplyCount"));
                list.add(j);
            }
        } catch (Exception e) {
            System.err.println("JobPostDAO.getAllJobPosts error: " + e.getMessage());
        }
        return list;
    }

    public JobPostModel getJobPostById(int jobPostId) {
        if (c == null) return null;
        String sql = PUBLIC_SELECT + "WHERE JobPostID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, jobPostId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) {
            System.err.println("JobPostDAO.getJobPostById error: " + e.getMessage());
        }
        return null;
    }

    /** Creates a new JD. Returns the new JobPostID, or -1 on failure. */
    public int createJobPost(JobPostModel j) {
        if (c == null) return -1;

        String sql = "INSERT INTO JobPost (Title, Description, Category, Position, Location, "
                + "OfferMin, OfferMax, NumberExp, Visible, TypeJob, Deadline, CreatedBy) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, j.getTitle());
            ps.setString(2, j.getDescription());
            ps.setString(3, j.getCategory());
            ps.setString(4, j.getPosition());
            ps.setString(5, j.getLocation());
            ps.setBigDecimal(6, j.getOfferMin());
            ps.setBigDecimal(7, j.getOfferMax());
            if (j.getNumberExp() != null) ps.setInt(8, j.getNumberExp()); else ps.setNull(8, java.sql.Types.INTEGER);
            ps.setBoolean(9, j.isVisible());
            ps.setString(10, j.getTypeJob());
            ps.setDate(11, j.getDeadline());
            if (j.getCreatedBy() != null) ps.setInt(12, j.getCreatedBy()); else ps.setNull(12, java.sql.Types.INTEGER);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("JobPostDAO.createJobPost error: " + e.getMessage());
        }
        return -1;
    }

    /** Opens or closes a JD for new applications (Visible flag). */
    public boolean setVisible(int jobPostId, boolean visible) {
        if (c == null) return false;
        String sql = "UPDATE JobPost SET Visible = ? WHERE JobPostID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, visible);
            ps.setInt(2, jobPostId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("JobPostDAO.setVisible error: " + e.getMessage());
            return false;
        }
    }

    private JobPostModel mapRow(ResultSet rs) throws Exception {
        JobPostModel j = new JobPostModel();
        j.setJobPostId(rs.getInt("JobPostID"));
        j.setTitle(rs.getString("Title"));
        j.setDescription(rs.getString("Description"));
        j.setCategory(rs.getString("Category"));
        j.setPosition(rs.getString("Position"));
        j.setLocation(rs.getString("Location"));
        j.setOfferMin(rs.getBigDecimal("OfferMin"));
        j.setOfferMax(rs.getBigDecimal("OfferMax"));
        int exp = rs.getInt("NumberExp");
        j.setNumberExp(rs.wasNull() ? null : exp);
        j.setVisible(rs.getBoolean("Visible"));
        j.setTypeJob(rs.getString("TypeJob"));
        j.setDeadline(rs.getDate("Deadline"));
        j.setDayCreate(rs.getTimestamp("DayCreate"));
        int createdBy = rs.getInt("CreatedBy");
        j.setCreatedBy(rs.wasNull() ? null : createdBy);
        return j;
    }
}
