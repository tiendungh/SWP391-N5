package dal;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.EmployeeAccountModel;

/**
 * Handles admin-created Employee accounts. A new account is a Candidate row
 * (name/email/phone/password) plus an Employee row (role, department,
 * position, salary), created inactive (IsActive = 0) until the candidate
 * clicks the emailed activation link.
 *
 * The activation token itself is stored in the existing dbo.PasswordResetToken
 * table rather than a new table - it already has exactly the shape we need
 * (Email, TokenHash, ExpiresAt, Used, Attempts). We tag our rows with
 * Role = 'EmployeeActivation' so they don't get confused with an actual
 * password-reset flow if one gets built later against the same table.
 */
public class AccountDAO extends DBContext {

    private static final String ACTIVATION_ROLE = "EmployeeActivation";

    /**
     * Testing helper: current IsActive state for the Employee tied to this
     * email. Returns null if no Employee exists for that email at all.
     */
    public Boolean getActivationStatus(String email) {
        if (c == null || email == null) return null;
        String sql = "SELECT e.IsActive FROM Employee e "
                + "JOIN Candidate c ON e.CandidateID = c.CandidateID WHERE c.Email = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getBoolean("IsActive");
            }
        } catch (Exception e) {
            System.err.println("AccountDAO.getActivationStatus error: " + e.getMessage());
            return null;
        }
    }

    public boolean emailExists(String email) {
        if (c == null) return false;
        String sql = "SELECT 1 FROM Candidate WHERE Email = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.err.println("AccountDAO.emailExists error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Creates the Candidate + Employee rows in one transaction. The Employee
     * row starts inactive (IsActive = 0). Returns the new EmployeeID, -2 if
     * the email is already used, -1 on any other failure.
     */
    public int createEmployeeAccount(String fullName, String email, String phone, String address,
            String nationality, String department, String position, BigDecimal salary, int roleId,
            String passwordHash) {
        if (c == null) return -1;
        if (emailExists(email)) return -2;

        String candidateSql = "INSERT INTO Candidate (CandidateName, Email, PhoneNumber, Address, Nationality, PasswordHash) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String employeeSql = "INSERT INTO Employee "
                + "(CandidateID, RoleID, EmployeeCode, HireDate, Department, Position, Salary, IsActive) "
                + "VALUES (?, ?, ?, CAST(GETDATE() AS DATE), ?, ?, ?, 0)";

        try {
            c.setAutoCommit(false);

            int candidateId;
            try (PreparedStatement ps = c.prepareStatement(candidateSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, fullName);
                ps.setString(2, email);
                ps.setString(3, phone);
                ps.setString(4, address);
                ps.setString(5, nationality);
                ps.setString(6, passwordHash);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Không lấy được CandidateID vừa tạo.");
                    candidateId = keys.getInt(1);
                }
            }

            int employeeId = -1;
            String employeeCode = generateEmployeeCode();
            for (int attempt = 0; attempt < 3; attempt++) {
                try (PreparedStatement ps = c.prepareStatement(employeeSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, candidateId);
                    ps.setInt(2, roleId);
                    ps.setString(3, employeeCode);
                    ps.setString(4, department);
                    ps.setString(5, position);
                    if (salary == null) ps.setNull(6, Types.DECIMAL);
                    else ps.setBigDecimal(6, salary);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Không lấy được EmployeeID vừa tạo.");
                        employeeId = keys.getInt(1);
                    }
                    break;
                } catch (SQLException dup) {
                    // EmployeeCode is UNIQUE; on the (extremely unlikely) collision, mutate and retry.
                    if (isDuplicateKey(dup) && attempt < 2) {
                        employeeCode = generateEmployeeCode() + attempt;
                        continue;
                    }
                    throw dup;
                }
            }

            c.commit();
            return employeeId;
        } catch (Exception e) {
            System.err.println("AccountDAO.createEmployeeAccount error: " + e.getMessage());
            rollbackQuietly();
            return -1;
        } finally {
            restoreAutoCommit();
        }
    }

    /** Compact, effectively-unique code that comfortably fits EmployeeCode's VARCHAR(20). */
    private String generateEmployeeCode() {
        return "EMP" + Long.toString(System.currentTimeMillis(), 36).toUpperCase();
    }

    // ============================================================
    // Admin account management (list / edit / activate / delete)
    // ============================================================

    private static final String ACCOUNT_SELECT =
            "SELECT e.EmployeeID, e.EmployeeCode, c.CandidateName AS FullName, c.Email, c.PhoneNumber, "
            + "e.RoleID, r.RoleName, e.Department, e.Position, e.Salary, e.HireDate, e.IsActive "
            + "FROM Employee e "
            + "JOIN Candidate c ON e.CandidateID = c.CandidateID "
            + "JOIN Roles r ON e.RoleID = r.RoleID ";

    /** Every Employee account (active or not), for Admin's management screen. */
    public List<EmployeeAccountModel> getAllEmployeeAccounts() {
        List<EmployeeAccountModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = ACCOUNT_SELECT + "ORDER BY c.CandidateName";
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapAccountRow(rs));
        } catch (Exception e) {
            System.err.println("AccountDAO.getAllEmployeeAccounts error: " + e.getMessage());
        }
        return list;
    }

    public EmployeeAccountModel getEmployeeAccountById(int employeeId) {
        if (c == null) return null;
        String sql = ACCOUNT_SELECT + "WHERE e.EmployeeID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapAccountRow(rs);
            }
        } catch (Exception e) {
            System.err.println("AccountDAO.getEmployeeAccountById error: " + e.getMessage());
        }
        return null;
    }

    /** True if some other Employee already uses this email (used when editing). */
    public boolean emailUsedByAnother(String email, int employeeId) {
        if (c == null) return false;
        String sql = "SELECT 1 FROM Candidate c "
                + "JOIN Employee e ON e.CandidateID = c.CandidateID "
                + "WHERE c.Email = ? AND e.EmployeeID <> ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.err.println("AccountDAO.emailUsedByAnother error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the editable profile fields for an Employee account (name/email/phone
     * live on Candidate, the rest on Employee). Does not touch password or IsActive -
     * see setActive()/resetPassword() for those. Returns true on success.
     */
    public boolean updateEmployeeAccount(int employeeId, String fullName, String email, String phone,
            String department, String position, BigDecimal salary, int roleId) {
        if (c == null) return false;

        String candidateSql = "UPDATE Candidate SET CandidateName = ?, Email = ?, PhoneNumber = ? "
                + "WHERE CandidateID = (SELECT CandidateID FROM Employee WHERE EmployeeID = ?)";
        String employeeSql = "UPDATE Employee SET RoleID = ?, Department = ?, Position = ?, Salary = ? "
                + "WHERE EmployeeID = ?";

        try {
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(candidateSql)) {
                ps.setString(1, fullName);
                ps.setString(2, email);
                ps.setString(3, phone);
                ps.setInt(4, employeeId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(employeeSql)) {
                ps.setInt(1, roleId);
                ps.setString(2, department);
                ps.setString(3, position);
                if (salary == null) ps.setNull(4, Types.DECIMAL);
                else ps.setBigDecimal(4, salary);
                ps.setInt(5, employeeId);
                ps.executeUpdate();
            }

            c.commit();
            return true;
        } catch (Exception e) {
            System.err.println("AccountDAO.updateEmployeeAccount error: " + e.getMessage());
            rollbackQuietly();
            return false;
        } finally {
            restoreAutoCommit();
        }
    }

    /** Flips Employee.IsActive - used for both "deactivate" and "reactivate". */
    public boolean setActive(int employeeId, boolean active) {
        if (c == null) return false;
        String sql = "UPDATE Employee SET IsActive = ? WHERE EmployeeID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, employeeId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            System.err.println("AccountDAO.setActive error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Permanently deletes the Employee row (and its Candidate row, since a
     * Candidate created purely as an Employee login has no other purpose once
     * the account is gone). Blocked if the Employee has related history rows
     * (interviews created/participated, evaluations, job posts, applies
     * concluded, profile documents) so we don't silently orphan real records -
     * deactivating is the safe alternative for that case.
     */
    public int deleteEmployeeAccount(int employeeId) {
        if (c == null) return -1;

        String candidateIdSql = "SELECT CandidateID FROM Employee WHERE EmployeeID = ?";
        int candidateIdForChecks;
        try (PreparedStatement ps = c.prepareStatement(candidateIdSql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return -1; // no such employee
                candidateIdForChecks = rs.getInt("CandidateID");
            }
        } catch (Exception e) {
            System.err.println("AccountDAO.deleteEmployeeAccount lookup error: " + e.getMessage());
            return -1;
        }

        String[] employeeBlockingChecks = {
            "SELECT 1 FROM Interview WHERE CreatedBy = ?",
            "SELECT 1 FROM InterviewParticipant WHERE EmployeeID = ?",
            "SELECT 1 FROM InterviewEvaluation WHERE InterviewerID = ?",
            "SELECT 1 FROM InterviewBarem WHERE CreatedBy = ?",
            "SELECT 1 FROM JobPost WHERE CreatedBy = ?",
            "SELECT 1 FROM Apply WHERE ConclusionBy = ?",
            "SELECT 1 FROM EmployeeProfileDocument WHERE EmployeeID = ? OR CreatedBy = ? OR ReviewedBy = ?",
            "SELECT 1 FROM ProfileDocumentType WHERE CreatedBy = ?",
            // Note: AccountRequest.ReviewedByAdminId references Admin, not Employee - not checked here.
            "SELECT 1 FROM AccountRequest WHERE RequestedBy = ?"
        };
        // Apply.CandidateID has no ON DELETE CASCADE/SET NULL, so a leftover
        // job application under this same Candidate row would otherwise crash
        // the DELETE FROM Candidate step below instead of failing cleanly.
        // (CV and SavedJob both cascade on Candidate delete, so no check needed there.)
        String[] candidateBlockingChecks = {
            "SELECT 1 FROM Apply WHERE CandidateID = ?"
        };

        try {
            for (String checkSql : employeeBlockingChecks) {
                try (PreparedStatement ps = c.prepareStatement(checkSql)) {
                    for (int i = 1; i <= ps.getParameterMetaData().getParameterCount(); i++) {
                        ps.setInt(i, employeeId);
                    }
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) return -2; // has related history - block delete
                    }
                }
            }
            for (String checkSql : candidateBlockingChecks) {
                try (PreparedStatement ps = c.prepareStatement(checkSql)) {
                    ps.setInt(1, candidateIdForChecks);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) return -2; // has related history - block delete
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("AccountDAO.deleteEmployeeAccount check warning: " + e.getMessage());
        }

        String clearFulfilledBySql = "UPDATE AccountRequest SET FulfilledEmployeeID = NULL WHERE FulfilledEmployeeID = ?";
        String deleteEmployeeSql = "DELETE FROM Employee WHERE EmployeeID = ?";
        String deleteCandidateSql = "DELETE FROM Candidate WHERE CandidateID = ?";

        try {
            c.setAutoCommit(false);

            int candidateId;
            try (PreparedStatement ps = c.prepareStatement(candidateIdSql)) {
                ps.setInt(1, employeeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        rollbackQuietly();
                        return -1;
                    }
                    candidateId = rs.getInt("CandidateID");
                }
            }

            // Clear the FulfilledEmployeeID reference on any AccountRequest this
            // employee fulfilled, so its history (who requested it, status, etc.)
            // survives the employee being removed instead of blocking the delete.
            try (PreparedStatement ps = c.prepareStatement(clearFulfilledBySql)) {
                ps.setInt(1, employeeId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement(deleteEmployeeSql)) {
                ps.setInt(1, employeeId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(deleteCandidateSql)) {
                ps.setInt(1, candidateId);
                ps.executeUpdate();
            }

            c.commit();
            return 1;
        } catch (Exception e) {
            System.err.println("AccountDAO.deleteEmployeeAccount error: " + e.getMessage());
            rollbackQuietly();
            return -1;
        } finally {
            restoreAutoCommit();
        }
    }

    /** Admin-triggered password reset: sets a brand-new hash directly, no token flow involved. */
    public boolean resetPassword(int employeeId, String newPasswordHash) {
        if (c == null) return false;
        String sql = "UPDATE Candidate SET PasswordHash = ? "
                + "WHERE CandidateID = (SELECT CandidateID FROM Employee WHERE EmployeeID = ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, employeeId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            System.err.println("AccountDAO.resetPassword error: " + e.getMessage());
            return false;
        }
    }

    private EmployeeAccountModel mapAccountRow(ResultSet rs) throws SQLException {
        EmployeeAccountModel m = new EmployeeAccountModel();
        m.setEmployeeId(rs.getInt("EmployeeID"));
        m.setEmployeeCode(rs.getString("EmployeeCode"));
        m.setFullName(rs.getString("FullName"));
        m.setEmail(rs.getString("Email"));
        m.setPhone(rs.getString("PhoneNumber"));
        m.setRoleId(rs.getInt("RoleID"));
        m.setRoleName(rs.getString("RoleName"));
        m.setDepartment(rs.getString("Department"));
        m.setPosition(rs.getString("Position"));
        m.setSalary(rs.getBigDecimal("Salary"));
        m.setHireDate(rs.getDate("HireDate"));
        m.setActive(rs.getBoolean("IsActive"));
        return m;
    }

    // ============================================================
    // Activation tokens (stored in dbo.PasswordResetToken)
    // ============================================================

    public boolean insertActivationToken(String email, String tokenHash, Timestamp expiresAt) {
        if (c == null) return false;
        String sql = "INSERT INTO PasswordResetToken (Email, TokenHash, ExpiresAt, Role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, tokenHash);
            ps.setTimestamp(3, expiresAt);
            ps.setString(4, ACTIVATION_ROLE);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            System.err.println("AccountDAO.insertActivationToken error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Activates the Employee tied to this token's email, if the token exists,
     * hasn't been used, and hasn't expired. Returns 1 on success, 0 otherwise
     * (invalid / already used / expired - all reported the same way to the
     * candidate so we don't leak which case it was).
     */
    public int activateByTokenHash(String tokenHash) {
        if (c == null || tokenHash == null || tokenHash.isBlank()) return 0;

        String selectSql = "SELECT Id, Email, ExpiresAt, Used FROM PasswordResetToken "
                + "WHERE TokenHash = ? AND Role = ?";
        String markUsedSql = "UPDATE PasswordResetToken SET Used = 1, Attempts = Attempts + 1 WHERE Id = ?";
        String activateSql = "UPDATE Employee SET IsActive = 1 "
                + "WHERE CandidateID = (SELECT CandidateID FROM Candidate WHERE Email = ?)";

        try {
            c.setAutoCommit(false);

            long tokenId;
            String email;
            boolean used;
            Timestamp expiresAt;
            try (PreparedStatement ps = c.prepareStatement(selectSql)) {
                ps.setString(1, tokenHash);
                ps.setString(2, ACTIVATION_ROLE);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        rollbackQuietly();
                        return 0;
                    }
                    tokenId = rs.getLong("Id");
                    email = rs.getString("Email");
                    used = rs.getBoolean("Used");
                    expiresAt = rs.getTimestamp("ExpiresAt");
                }
            }

            if (used || expiresAt == null || expiresAt.before(new Timestamp(System.currentTimeMillis()))) {
                rollbackQuietly();
                return 0;
            }

            try (PreparedStatement ps = c.prepareStatement(activateSql)) {
                ps.setString(1, email);
                if (ps.executeUpdate() != 1) throw new SQLException("Không tìm thấy Employee để kích hoạt.");
            }
            try (PreparedStatement ps = c.prepareStatement(markUsedSql)) {
                ps.setLong(1, tokenId);
                ps.executeUpdate();
            }

            c.commit();
            return 1;
        } catch (Exception e) {
            System.err.println("AccountDAO.activateByTokenHash error: " + e.getMessage());
            rollbackQuietly();
            return 0;
        } finally {
            restoreAutoCommit();
        }
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
