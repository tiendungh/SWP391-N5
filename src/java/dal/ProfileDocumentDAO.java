package dal;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.EmployeeProfileDocumentModel;
import model.EmployeeProfileSummaryModel;
import model.ProfileDocumentFileModel;
import model.ProfileDocumentHistoryModel;
import model.ProfileDocumentTypeModel;

public class ProfileDocumentDAO extends DBContext {

    private static final String DOCUMENT_SELECT =
            "SELECT d.EmployeeDocumentID, d.EmployeeID, employeeCandidate.CandidateName AS EmployeeName, "
            + "d.DocumentTypeID, dt.DocumentName, dt.Description AS DocumentDescription, "
            + "d.IsRequired, d.DueDate, d.Status, d.OriginalFileName, d.ContentType, "
            + "d.EmployeeNote, d.ReviewNote, d.SubmittedAt, d.ReviewedBy, "
            + "reviewerCandidate.CandidateName AS ReviewedByName, d.ReviewedAt, "
            + "d.CreatedBy, creatorCandidate.CandidateName AS CreatedByName, d.CreatedAt, d.UpdatedAt "
            + "FROM EmployeeProfileDocument d "
            + "JOIN ProfileDocumentType dt ON d.DocumentTypeID = dt.DocumentTypeID "
            + "JOIN Employee employeeRecord ON d.EmployeeID = employeeRecord.EmployeeID "
            + "JOIN Candidate employeeCandidate ON employeeRecord.CandidateID = employeeCandidate.CandidateID "
            + "JOIN Employee creatorRecord ON d.CreatedBy = creatorRecord.EmployeeID "
            + "JOIN Candidate creatorCandidate ON creatorRecord.CandidateID = creatorCandidate.CandidateID "
            + "LEFT JOIN Employee reviewerRecord ON d.ReviewedBy = reviewerRecord.EmployeeID "
            + "LEFT JOIN Candidate reviewerCandidate ON reviewerRecord.CandidateID = reviewerCandidate.CandidateID ";

    public List<EmployeeProfileSummaryModel> getEmployeeSummaries() {
        List<EmployeeProfileSummaryModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = "SELECT e.EmployeeID, candidate.CandidateName, candidate.Email, role.RoleName, "
                + "COALESCE(SUM(CASE WHEN d.IsRequired = 1 THEN 1 ELSE 0 END), 0) AS TotalRequired, "
                + "COALESCE(SUM(CASE WHEN d.IsRequired = 1 AND d.Status = 'Approved' THEN 1 ELSE 0 END), 0) AS ApprovedRequired, "
                + "COALESCE(SUM(CASE WHEN d.Status = 'Submitted' THEN 1 ELSE 0 END), 0) AS PendingReview, "
                + "COALESCE(SUM(CASE WHEN d.DueDate < CAST(GETDATE() AS DATE) AND d.Status <> 'Approved' THEN 1 ELSE 0 END), 0) AS Overdue "
                + "FROM Employee e "
                + "JOIN Candidate candidate ON e.CandidateID = candidate.CandidateID "
                + "JOIN Roles role ON e.RoleID = role.RoleID "
                + "LEFT JOIN EmployeeProfileDocument d ON e.EmployeeID = d.EmployeeID "
                + "WHERE e.IsActive = 1 "
                + "AND role.RoleName = 'Employee' "
                + "GROUP BY e.EmployeeID, candidate.CandidateName, candidate.Email, role.RoleName "
                + "ORDER BY candidate.CandidateName";

        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapSummary(rs));
        } catch (Exception e) {
            System.err.println("ProfileDocumentDAO.getEmployeeSummaries error: " + e.getMessage());
        }
        return list;
    }

    public EmployeeProfileSummaryModel getEmployeeSummary(int employeeId) {
        if (c == null) return null;
        String sql = "SELECT e.EmployeeID, candidate.CandidateName, candidate.Email, role.RoleName, "
                + "COALESCE(SUM(CASE WHEN d.IsRequired = 1 THEN 1 ELSE 0 END), 0) AS TotalRequired, "
                + "COALESCE(SUM(CASE WHEN d.IsRequired = 1 AND d.Status = 'Approved' THEN 1 ELSE 0 END), 0) AS ApprovedRequired, "
                + "COALESCE(SUM(CASE WHEN d.Status = 'Submitted' THEN 1 ELSE 0 END), 0) AS PendingReview, "
                + "COALESCE(SUM(CASE WHEN d.DueDate < CAST(GETDATE() AS DATE) AND d.Status <> 'Approved' THEN 1 ELSE 0 END), 0) AS Overdue "
                + "FROM Employee e "
                + "JOIN Candidate candidate ON e.CandidateID = candidate.CandidateID "
                + "JOIN Roles role ON e.RoleID = role.RoleID "
                + "LEFT JOIN EmployeeProfileDocument d ON e.EmployeeID = d.EmployeeID "
                + "WHERE e.EmployeeID = ? AND e.IsActive = 1 "
                + "GROUP BY e.EmployeeID, candidate.CandidateName, candidate.Email, role.RoleName";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapSummary(rs);
            }
        } catch (Exception e) {
            System.err.println("ProfileDocumentDAO.getEmployeeSummary error: " + e.getMessage());
        }
        return null;
    }

    public List<ProfileDocumentTypeModel> getActiveDocumentTypes() {
        List<ProfileDocumentTypeModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = "SELECT dt.DocumentTypeID, dt.DocumentName, dt.Description, dt.IsActive, "
                + "dt.CreatedBy, candidate.CandidateName AS CreatedByName, dt.CreatedAt "
                + "FROM ProfileDocumentType dt "
                + "JOIN Employee employeeRecord ON dt.CreatedBy = employeeRecord.EmployeeID "
                + "JOIN Candidate candidate ON employeeRecord.CandidateID = candidate.CandidateID "
                + "WHERE dt.IsActive = 1 ORDER BY dt.DocumentName";

        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ProfileDocumentTypeModel type = new ProfileDocumentTypeModel();
                type.setDocumentTypeId(rs.getInt("DocumentTypeID"));
                type.setDocumentName(rs.getString("DocumentName"));
                type.setDescription(rs.getString("Description"));
                type.setActive(rs.getBoolean("IsActive"));
                type.setCreatedBy(rs.getInt("CreatedBy"));
                type.setCreatedByName(rs.getString("CreatedByName"));
                type.setCreatedAt(rs.getTimestamp("CreatedAt"));
                list.add(type);
            }
        } catch (Exception e) {
            System.err.println("ProfileDocumentDAO.getActiveDocumentTypes error: " + e.getMessage());
        }
        return list;
    }

    public int createDocumentType(String documentName, String description, int createdBy) {
        if (c == null) return -1;
        String sql = "INSERT INTO ProfileDocumentType (DocumentName, Description, CreatedBy) VALUES (?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, documentName);
            ps.setString(2, description);
            ps.setInt(3, createdBy);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            if (isDuplicateKey(e)) return -2;
            System.err.println("ProfileDocumentDAO.createDocumentType error: " + e.getMessage());
        }
        return -1;
    }

    public int assignDocument(int employeeId, int documentTypeId, boolean required, Date dueDate, int createdBy) {
        if (c == null) return -1;
        String sql = "INSERT INTO EmployeeProfileDocument "
                + "(EmployeeID, DocumentTypeID, IsRequired, DueDate, CreatedBy) "
                + "SELECT employee.EmployeeID, documentType.DocumentTypeID, ?, ?, ? "
                + "FROM Employee employee CROSS JOIN ProfileDocumentType documentType "
                + "WHERE employee.EmployeeID = ? AND employee.IsActive = 1 "
                + "AND documentType.DocumentTypeID = ? AND documentType.IsActive = 1";

        try {
            c.setAutoCommit(false);
            int employeeDocumentId;
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setBoolean(1, required);
                if (dueDate == null) ps.setNull(2, Types.DATE);
                else ps.setDate(2, dueDate);
                ps.setInt(3, createdBy);
                ps.setInt(4, employeeId);
                ps.setInt(5, documentTypeId);
                if (ps.executeUpdate() != 1) {
                    rollbackQuietly();
                    return -3;
                }
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Cannot read generated EmployeeDocumentID");
                    employeeDocumentId = keys.getInt(1);
                }
            }

            insertHistory(employeeDocumentId, "Assigned", null, "Pending", null, createdBy);
            c.commit();
            return employeeDocumentId;
        } catch (SQLException e) {
            rollbackQuietly();
            if (isDuplicateKey(e)) return -2;
            System.err.println("ProfileDocumentDAO.assignDocument error: " + e.getMessage());
            return -1;
        } finally {
            restoreAutoCommit();
        }
    }

    public List<EmployeeProfileDocumentModel> getDocumentsByEmployee(int employeeId) {
        List<EmployeeProfileDocumentModel> list = new ArrayList<>();
        if (c == null) return list;
        String sql = DOCUMENT_SELECT + "WHERE d.EmployeeID = ? "
                + "ORDER BY d.IsRequired DESC, d.DueDate, dt.DocumentName";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapDocument(rs));
            }
        } catch (Exception e) {
            System.err.println("ProfileDocumentDAO.getDocumentsByEmployee error: " + e.getMessage());
        }
        return list;
    }

    public EmployeeProfileDocumentModel getDocumentById(int employeeDocumentId) {
        if (c == null) return null;
        String sql = DOCUMENT_SELECT + "WHERE d.EmployeeDocumentID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeDocumentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapDocument(rs);
            }
        } catch (Exception e) {
            System.err.println("ProfileDocumentDAO.getDocumentById error: " + e.getMessage());
        }
        return null;
    }

    public boolean submitDocument(int employeeDocumentId, int employeeId, String fileName,
            String contentType, byte[] fileData, String employeeNote) {
        if (c == null) return false;

        try {
            c.setAutoCommit(false);
            String oldStatus = lockDocumentStatus(employeeDocumentId, employeeId);
            if (!"Pending".equals(oldStatus) && !"Rejected".equals(oldStatus)) {
                rollbackQuietly();
                return false;
            }

            String sql = "UPDATE EmployeeProfileDocument SET Status = 'Submitted', OriginalFileName = ?, "
                    + "ContentType = ?, FileData = ?, EmployeeNote = ?, SubmittedAt = SYSDATETIME(), "
                    + "ReviewedBy = NULL, ReviewedAt = NULL, ReviewNote = NULL, UpdatedAt = SYSDATETIME() "
                    + "WHERE EmployeeDocumentID = ? AND EmployeeID = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, fileName);
                ps.setString(2, contentType);
                ps.setBytes(3, fileData);
                ps.setString(4, employeeNote);
                ps.setInt(5, employeeDocumentId);
                ps.setInt(6, employeeId);
                if (ps.executeUpdate() != 1) throw new SQLException("Document submission was not updated");
            }

            String action = "Rejected".equals(oldStatus) ? "Resubmitted" : "Submitted";
            insertHistory(employeeDocumentId, action, oldStatus, "Submitted", employeeNote, employeeId);
            c.commit();
            return true;
        } catch (Exception e) {
            rollbackQuietly();
            System.err.println("ProfileDocumentDAO.submitDocument error: " + e.getMessage());
            return false;
        } finally {
            restoreAutoCommit();
        }
    }

    public boolean reviewDocument(int employeeDocumentId, int reviewedBy, String decision, String reviewNote) {
        if (c == null || (!"Approved".equals(decision) && !"Rejected".equals(decision))) return false;

        try {
            c.setAutoCommit(false);
            String oldStatus = lockDocumentStatus(employeeDocumentId, null);
            if (!"Submitted".equals(oldStatus)) {
                rollbackQuietly();
                return false;
            }

            String sql = "UPDATE EmployeeProfileDocument SET Status = ?, ReviewNote = ?, ReviewedBy = ?, "
                    + "ReviewedAt = SYSDATETIME(), UpdatedAt = SYSDATETIME() WHERE EmployeeDocumentID = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, decision);
                ps.setString(2, reviewNote);
                ps.setInt(3, reviewedBy);
                ps.setInt(4, employeeDocumentId);
                if (ps.executeUpdate() != 1) throw new SQLException("Document review was not updated");
            }

            insertHistory(employeeDocumentId, decision, oldStatus, decision, reviewNote, reviewedBy);
            c.commit();
            return true;
        } catch (Exception e) {
            rollbackQuietly();
            System.err.println("ProfileDocumentDAO.reviewDocument error: " + e.getMessage());
            return false;
        } finally {
            restoreAutoCommit();
        }
    }

    public List<ProfileDocumentHistoryModel> getHistoryByEmployee(int employeeId) {
        List<ProfileDocumentHistoryModel> list = new ArrayList<>();
        if (c == null) return list;
        String sql = "SELECT h.HistoryID, h.EmployeeDocumentID, dt.DocumentName, h.Action, "
                + "h.FromStatus, h.ToStatus, h.Comment, h.ActionBy, "
                + "candidate.CandidateName AS ActionByName, h.CreatedAt "
                + "FROM ProfileDocumentHistory h "
                + "JOIN EmployeeProfileDocument d ON h.EmployeeDocumentID = d.EmployeeDocumentID "
                + "JOIN ProfileDocumentType dt ON d.DocumentTypeID = dt.DocumentTypeID "
                + "JOIN Employee employeeRecord ON h.ActionBy = employeeRecord.EmployeeID "
                + "JOIN Candidate candidate ON employeeRecord.CandidateID = candidate.CandidateID "
                + "WHERE d.EmployeeID = ? ORDER BY h.CreatedAt DESC";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProfileDocumentHistoryModel history = new ProfileDocumentHistoryModel();
                    history.setHistoryId(rs.getInt("HistoryID"));
                    history.setEmployeeDocumentId(rs.getInt("EmployeeDocumentID"));
                    history.setDocumentName(rs.getString("DocumentName"));
                    history.setAction(rs.getString("Action"));
                    history.setFromStatus(rs.getString("FromStatus"));
                    history.setToStatus(rs.getString("ToStatus"));
                    history.setComment(rs.getString("Comment"));
                    history.setActionBy(rs.getInt("ActionBy"));
                    history.setActionByName(rs.getString("ActionByName"));
                    history.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    list.add(history);
                }
            }
        } catch (Exception e) {
            System.err.println("ProfileDocumentDAO.getHistoryByEmployee error: " + e.getMessage());
        }
        return list;
    }

    public ProfileDocumentFileModel getDocumentFile(int employeeDocumentId) {
        if (c == null) return null;
        String sql = "SELECT EmployeeDocumentID, EmployeeID, OriginalFileName, ContentType, FileData "
                + "FROM EmployeeProfileDocument WHERE EmployeeDocumentID = ? AND FileData IS NOT NULL";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeDocumentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProfileDocumentFileModel file = new ProfileDocumentFileModel();
                    file.setEmployeeDocumentId(rs.getInt("EmployeeDocumentID"));
                    file.setEmployeeId(rs.getInt("EmployeeID"));
                    file.setOriginalFileName(rs.getString("OriginalFileName"));
                    file.setContentType(rs.getString("ContentType"));
                    file.setFileData(rs.getBytes("FileData"));
                    return file;
                }
            }
        } catch (Exception e) {
            System.err.println("ProfileDocumentDAO.getDocumentFile error: " + e.getMessage());
        }
        return null;
    }

    private String lockDocumentStatus(int employeeDocumentId, Integer employeeId) throws SQLException {
        String sql = "SELECT Status FROM EmployeeProfileDocument WITH (UPDLOCK, ROWLOCK) "
                + "WHERE EmployeeDocumentID = ?" + (employeeId == null ? "" : " AND EmployeeID = ?");
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeDocumentId);
            if (employeeId != null) ps.setInt(2, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("Status") : null;
            }
        }
    }

    private void insertHistory(int employeeDocumentId, String action, String fromStatus,
            String toStatus, String comment, int actionBy) throws SQLException {
        String sql = "INSERT INTO ProfileDocumentHistory "
                + "(EmployeeDocumentID, Action, FromStatus, ToStatus, Comment, ActionBy) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeDocumentId);
            ps.setString(2, action);
            if (fromStatus == null) ps.setNull(3, Types.VARCHAR);
            else ps.setString(3, fromStatus);
            ps.setString(4, toStatus);
            ps.setString(5, comment);
            ps.setInt(6, actionBy);
            ps.executeUpdate();
        }
    }

    private EmployeeProfileSummaryModel mapSummary(ResultSet rs) throws SQLException {
        EmployeeProfileSummaryModel summary = new EmployeeProfileSummaryModel();
        summary.setEmployeeId(rs.getInt("EmployeeID"));
        summary.setFullName(rs.getString("CandidateName"));
        summary.setEmail(rs.getString("Email"));
        summary.setRoleName(rs.getString("RoleName"));
        summary.setTotalRequired(rs.getInt("TotalRequired"));
        summary.setApprovedRequired(rs.getInt("ApprovedRequired"));
        summary.setPendingReview(rs.getInt("PendingReview"));
        summary.setOverdue(rs.getInt("Overdue"));
        return summary;
    }

    private EmployeeProfileDocumentModel mapDocument(ResultSet rs) throws SQLException {
        EmployeeProfileDocumentModel document = new EmployeeProfileDocumentModel();
        document.setEmployeeDocumentId(rs.getInt("EmployeeDocumentID"));
        document.setEmployeeId(rs.getInt("EmployeeID"));
        document.setEmployeeName(rs.getString("EmployeeName"));
        document.setDocumentTypeId(rs.getInt("DocumentTypeID"));
        document.setDocumentName(rs.getString("DocumentName"));
        document.setDocumentDescription(rs.getString("DocumentDescription"));
        document.setRequired(rs.getBoolean("IsRequired"));
        document.setDueDate(rs.getDate("DueDate"));
        document.setStatus(rs.getString("Status"));
        document.setOriginalFileName(rs.getString("OriginalFileName"));
        document.setContentType(rs.getString("ContentType"));
        document.setEmployeeNote(rs.getString("EmployeeNote"));
        document.setReviewNote(rs.getString("ReviewNote"));
        document.setSubmittedAt(rs.getTimestamp("SubmittedAt"));
        int reviewedBy = rs.getInt("ReviewedBy");
        document.setReviewedBy(rs.wasNull() ? null : reviewedBy);
        document.setReviewedByName(rs.getString("ReviewedByName"));
        document.setReviewedAt(rs.getTimestamp("ReviewedAt"));
        document.setCreatedBy(rs.getInt("CreatedBy"));
        document.setCreatedByName(rs.getString("CreatedByName"));
        document.setCreatedAt(rs.getTimestamp("CreatedAt"));
        document.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
        return document;
    }

    private boolean isDuplicateKey(SQLException e) {
        return e.getErrorCode() == 2601 || e.getErrorCode() == 2627;
    }

    private void rollbackQuietly() {
        try {
            c.rollback();
        } catch (Exception ignored) {
        }
    }

    private void restoreAutoCommit() {
        try {
            c.setAutoCommit(true);
        } catch (Exception ignored) {
        }
    }
}
