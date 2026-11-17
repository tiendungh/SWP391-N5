package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.AdminModel;
import tool.EncodePassword;

/**
 * Admin accounts live in their own table (dbo.Admin), completely separate
 * from Candidate/Employee - there is no CandidateID/EmployeeID involved here.
 * dbo.Role_Table is a many-to-many link from Admin to Roles, in case an
 * Admin ever needs more than one tag (e.g. "Admin" + some future scope).
 */
public class AdminDAO extends DBContext {

    /** Verifies username/password against dbo.Admin. Returns the AdminModel
     *  (with its Role_Table role names loaded) on success, or null otherwise. */
    public AdminModel authenticate(String username, String password) {
        if (c == null) return null;

        String sql = "SELECT AdminID, Username, PasswordHash FROM Admin WHERE Username = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String storedHash = rs.getString("PasswordHash");
                String inputHash = EncodePassword.encodePasswordbyHash(password);
                if (inputHash == null || !inputHash.equals(storedHash)) return null;

                AdminModel admin = new AdminModel();
                admin.setAdminId(rs.getInt("AdminID"));
                admin.setUsername(rs.getString("Username"));
                admin.setRoleNames(getRoleNames(admin.getAdminId()));
                return admin;
            }
        } catch (Exception e) {
            System.err.println("AdminDAO.authenticate error: " + e.getMessage());
            return null;
        }
    }

    private List<String> getRoleNames(int adminId) {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT r.RoleName FROM Role_Table rt "
                + "JOIN Roles r ON rt.RoleID = r.RoleID WHERE rt.AdminID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) roles.add(rs.getString("RoleName"));
            }
        } catch (Exception e) {
            System.err.println("AdminDAO.getRoleNames error: " + e.getMessage());
        }
        return roles;
    }
}
