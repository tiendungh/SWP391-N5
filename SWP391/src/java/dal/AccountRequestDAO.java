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
import model.AccountRequestModel;

/**
 * HR Staff -> Admin account creation requests (dbo.AccountRequest). HR Staff
 * fills in an email (and optionally the rest of the profile) and submits it;
 * Admin reviews the queue and either creates the account from a request
 * (marking it Approved + linking the new EmployeeID) or rejects it with a
 * note the requester can see.
 */
public class AccountRequestDAO extends DBContext {

    private static final String BASE_SELECT =
            "SELECT ar.RequestID, ar.RequestedEmail, ar.FullName, ar.Phone, ar.Address, ar.Nationality, "
            + "ar.Department, ar.Position, "
            + "ar.RoleID, r.RoleName, ar.Salary, ar.Note, ar.RequestedBy, reqCandidate.CandidateName AS RequestedByName, "
            + "ar.Status, ar.CreatedAt, ar.ReviewedByAdminId, a.Username AS ReviewedByAdminName, ar.ReviewedAt, "
            + "ar.RejectionNote, ar.FulfilledEmployeeID "
            + "FROM AccountRequest ar "
            + "LEFT JOIN Roles r ON ar.RoleID = r.RoleID "
            + "JOIN Employee reqEmp ON ar.RequestedBy = reqEmp.EmployeeID "
            + "JOIN Candidate reqCandidate ON reqEmp.CandidateID = reqCandidate.CandidateID "
            + "LEFT JOIN Admin a ON ar.ReviewedByAdminId = a.AdminID ";

    /** All requests submitted by this Manager, newest first. */
    public List<AccountRequestModel> getRequestsByManager(int managerStaffId) {
        List<AccountRequestModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = BASE_SELECT + "WHERE ar.RequestedBy = ? ORDER BY ar.CreatedAt DESC";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, managerStaffId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            System.err.println("AccountRequestDAO.getRequestsByManager error: " + e.getMessage());
        }
        return list;
    }

    /** All requests still awaiting Admin review, oldest first (FIFO queue). */
    public List<AccountRequestModel> getPendingRequests() {
        List<AccountRequestModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = BASE_SELECT + "WHERE ar.Status = 'Pending' ORDER BY ar.CreatedAt ASC";
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            System.err.println("AccountRequestDAO.getPendingRequests error: " + e.getMessage());
        }
        return list;
    }

    /** All requests regardless of status, newest first - for Admin's full history view. */
    public List<AccountRequestModel> getAllRequests() {
        List<AccountRequestModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = BASE_SELECT + "ORDER BY ar.CreatedAt DESC";
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            System.err.println("AccountRequestDAO.getAllRequests error: " + e.getMessage());
        }
        return list;
    }

    public AccountRequestModel getRequestById(int requestId) {
        if (c == null) return null;
        String sql = BASE_SELECT + "WHERE ar.RequestID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) {
            System.err.println("AccountRequestDAO.getRequestById error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Creates a new Pending request. Returns the new RequestID, or -1 on failure.
     * Does not block duplicate emails here - CreateAccountServlet already checks
     * emailExists() when Admin actually creates the account, which is the point
     * that matters; a Manager is still allowed to ask about an email that's
     * already pending review from someone else.
     */
    public int createRequest(String requestedEmail, String fullName, String phone, String address,
            String nationality, String department, String position, Integer roleId, BigDecimal salary,
            String note, int requestedBy) {
        if (c == null) return -1;

        String sql = "INSERT INTO AccountRequest "
                + "(RequestedEmail, FullName, Phone, Address, Nationality, Department, Position, RoleID, Salary, Note, RequestedBy, Status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Pending')";

        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, requestedEmail);
            ps.setString(2, fullName);
            ps.setString(3, phone);
            ps.setString(4, address);
            ps.setString(5, nationality);
            ps.setString(6, department);
            ps.setString(7, position);
            if (roleId == null) ps.setNull(8, Types.INTEGER); else ps.setInt(8, roleId);
            if (salary == null) ps.setNull(9, Types.DECIMAL); else ps.setBigDecimal(9, salary);
            ps.setString(10, note);
            ps.setInt(11, requestedBy);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) return -1;
                return keys.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("AccountRequestDAO.createRequest error: " + e.getMessage());
            return -1;
        }
    }

    /** Marks a Pending request Approved and links the EmployeeID that was created from it. */
    public boolean markApproved(int requestId, int adminId, int fulfilledEmployeeId) {
        if (c == null) return false;
        String sql = "UPDATE AccountRequest SET Status = 'Approved', ReviewedByAdminId = ?, "
                + "ReviewedAt = GETDATE(), FulfilledEmployeeID = ? WHERE RequestID = ? AND Status = 'Pending'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ps.setInt(2, fulfilledEmployeeId);
            ps.setInt(3, requestId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            System.err.println("AccountRequestDAO.markApproved error: " + e.getMessage());
            return false;
        }
    }

    /** Marks a Pending request Rejected with a note the Manager can see. */
    public boolean markRejected(int requestId, int adminId, String rejectionNote) {
        if (c == null) return false;
        String sql = "UPDATE AccountRequest SET Status = 'Rejected', ReviewedByAdminId = ?, "
                + "ReviewedAt = GETDATE(), RejectionNote = ? WHERE RequestID = ? AND Status = 'Pending'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ps.setString(2, rejectionNote);
            ps.setInt(3, requestId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            System.err.println("AccountRequestDAO.markRejected error: " + e.getMessage());
            return false;
        }
    }

    public int countPending() {
        if (c == null) return 0;
        String sql = "SELECT COUNT(*) FROM AccountRequest WHERE Status = 'Pending'";
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("AccountRequestDAO.countPending error: " + e.getMessage());
        }
        return 0;
    }

    private AccountRequestModel mapRow(ResultSet rs) throws SQLException {
        AccountRequestModel m = new AccountRequestModel();
        m.setRequestId(rs.getInt("RequestID"));
        m.setRequestedEmail(rs.getString("RequestedEmail"));
        m.setFullName(rs.getString("FullName"));
        m.setPhone(rs.getString("Phone"));
        m.setAddress(rs.getString("Address"));
        m.setNationality(rs.getString("Nationality"));
        m.setDepartment(rs.getString("Department"));
        m.setPosition(rs.getString("Position"));
        int roleId = rs.getInt("RoleID");
        m.setRoleId(rs.wasNull() ? null : roleId);
        m.setRoleName(rs.getString("RoleName"));
        m.setSalary(rs.getBigDecimal("Salary"));
        m.setNote(rs.getString("Note"));
        m.setRequestedBy(rs.getInt("RequestedBy"));
        m.setRequestedByName(rs.getString("RequestedByName"));
        m.setStatus(rs.getString("Status"));
        m.setCreatedAt(rs.getTimestamp("CreatedAt"));
        int reviewedBy = rs.getInt("ReviewedByAdminId");
        m.setReviewedByAdminId(rs.wasNull() ? null : reviewedBy);
        m.setReviewedByAdminName(rs.getString("ReviewedByAdminName"));
        m.setReviewedAt(rs.getTimestamp("ReviewedAt"));
        m.setRejectionNote(rs.getString("RejectionNote"));
        int fulfilledId = rs.getInt("FulfilledEmployeeID");
        m.setFulfilledEmployeeId(rs.wasNull() ? null : fulfilledId);
        return m;
    }
}
