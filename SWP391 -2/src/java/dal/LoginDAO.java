package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import tool.EncodePassword;

public class LoginDAO extends DBContext {

    // Returned after a successful login. Only an active Employee (HR Staff /
    // Manager) can use the interview-scheduling tools. A plain Candidate who
    // was never hired (no Employee row) cannot log in here.
    public static class UserSession {
        public int staffId;   // Employee.EmployeeID
        public int roleId;
        public String roleName;
        public String fullName;
        public String email;

        public UserSession(int staffId, int roleId, String roleName, String fullName, String email) {
            this.staffId = staffId;
            this.roleId = roleId;
            this.roleName = roleName;
            this.fullName = fullName;
            this.email = email;
        }
    }

    public UserSession loginUser(String email, String password) {
        if (c == null) return null;

        String sql = "SELECT e.EmployeeID, e.RoleID, r.RoleName, c.CandidateName, c.Email, c.PasswordHash "
                + "FROM Candidate c "
                + "JOIN Employee e ON c.CandidateID = e.CandidateID "
                + "JOIN Roles r ON e.RoleID = r.RoleID "
                + "WHERE c.Email = ? AND e.IsActive = 1";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String storedHash = rs.getString("PasswordHash");
                String inputHash = EncodePassword.encodePasswordbyHash(password);

                if (inputHash != null && inputHash.equals(storedHash)) {
                    return new UserSession(
                            rs.getInt("EmployeeID"),
                            rs.getInt("RoleID"),
                            rs.getString("RoleName"),
                            rs.getString("CandidateName"),
                            rs.getString("Email")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
        }
        return null;
    }
}
