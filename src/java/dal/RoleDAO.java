package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.RoleModel;

public class RoleDAO extends DBContext {

    /**
     * Roles that make sense to hand to a brand-new Employee row. Deliberately
     * excludes 'Admin' (its own separate login system, dbo.Admin) and
     * 'Candidate' (self-registered job seekers, not created from this screen).
     */
    public List<RoleModel> getAssignableEmployeeRoles() {
        List<RoleModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = "SELECT RoleID, RoleName FROM Roles "
                + "WHERE RoleName IN ('HR Staff', 'Manager', 'Employee') ORDER BY RoleName";
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RoleModel r = new RoleModel();
                r.setRoleId(rs.getInt("RoleID"));
                r.setRoleName(rs.getString("RoleName"));
                list.add(r);
            }
        } catch (Exception e) {
            System.err.println("RoleDAO.getAssignableEmployeeRoles error: " + e.getMessage());
        }
        return list;
    }
}
