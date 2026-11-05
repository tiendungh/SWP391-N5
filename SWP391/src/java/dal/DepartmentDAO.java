//package dal;
//
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.Types;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import model.DepartmentModel;
//
//public class DepartmentDAO extends DBContext {
//
//    // Lấy danh sách tất cả phòng ban
//    public List<DepartmentModel> getAllDepartments() {
//        List<DepartmentModel> list = new ArrayList<>();
//        if (c == null) return list;
//
//        String sql = "SELECT d.DepartmentID, d.DepartmentName, d.Description, d.ManagerID, c.CandidateName AS ManagerName "
//                   + "FROM dbo.Department d "
//                   + "LEFT JOIN dbo.Employee e ON d.ManagerID = e.EmployeeID "
//                   + "LEFT JOIN dbo.Candidate c ON e.CandidateID = c.CandidateID "
//                   + "ORDER BY d.DepartmentName";
//
//        try (PreparedStatement ps = c.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//            while (rs.next()) {
//                DepartmentModel d = new DepartmentModel();
//                d.setDepartmentId(rs.getInt("DepartmentID"));
//                d.setDepartmentName(rs.getNString("DepartmentName"));
//                d.setDescription(rs.getNString("Description"));
//                d.setManagerId((Integer) rs.getObject("ManagerID"));
//                d.setManagerName(rs.getNString("ManagerName"));
//                list.add(d);
//            }
//        } catch (Exception e) {
//            System.err.println("DepartmentDAO.getAllDepartments error: " + e.getMessage());
//        }
//        return list;
//    }
//
//    // Thêm mới phòng ban
//    public boolean addDepartment(String departmentName, String description, Integer managerId) {
//        if (c == null) return false;
//
//        String sql = "INSERT INTO dbo.Department (DepartmentName, Description, ManagerID) VALUES (?, ?, ?)";
//        try (PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setNString(1, departmentName);
//            ps.setNString(2, description);
//            if (managerId != null && managerId > 0) {
//                ps.setInt(3, managerId);
//            } else {
//                ps.setNull(3, Types.INTEGER);
//            }
//            return ps.executeUpdate() > 0;
//        } catch (Exception e) {
//            System.err.println("DepartmentDAO.addDepartment error: " + e.getMessage());
//            return false;
//        }
//    }
//
//    // Gán nhân sự vào phòng ban và ghi nhận lịch sử vào EmployeePositionHistory
//    public boolean assignEmployeeToDepartment(int employeeId, String departmentName, String position, String note) {
//        if (c == null) return false;
//
//        String closeHistorySql = "UPDATE dbo.EmployeePositionHistory SET EndDate = CAST(GETDATE() AS DATE) "
//                               + "WHERE EmployeeID = ? AND EndDate IS NULL";
//        String updateEmpSql = "UPDATE dbo.Employee SET Department = ?, Position = ? WHERE EmployeeID = ?";
//        String insertHistorySql = "INSERT INTO dbo.EmployeePositionHistory (EmployeeID, RoleID, Position, Department, StartDate, Note) "
//                                + "SELECT EmployeeID, RoleID, ?, ?, CAST(GETDATE() AS DATE), ? "
//                                + "FROM dbo.Employee WHERE EmployeeID = ?";
//
//        boolean originalAutoCommit = true;
//        try {
//            originalAutoCommit = c.getAutoCommit();
//            c.setAutoCommit(false); // Bắt đầu transaction
//
//            // 1. Đóng vị trí cũ
//            try (PreparedStatement psClose = c.prepareStatement(closeHistorySql)) {
//                psClose.setInt(1, employeeId);
//                psClose.executeUpdate();
//            }
//
//            // 2. Cập nhật phòng ban mới cho Employee
//            try (PreparedStatement psEmp = c.prepareStatement(updateEmpSql)) {
//                psEmp.setNString(1, departmentName);
//                psEmp.setNString(2, position);
//                psEmp.setInt(3, employeeId);
//                psEmp.executeUpdate();
//            }
//
//            // 3. Ghi lịch sử mới vào EmployeePositionHistory
//            try (PreparedStatement psHist = c.prepareStatement(insertHistorySql)) {
//                psHist.setNString(1, position);
//                psHist.setNString(2, departmentName);
//                psHist.setNString(3, note != null ? note : "Gán phòng ban bởi Manager");
//                psHist.setInt(4, employeeId);
//                psHist.executeUpdate();
//            }
//
//            c.commit();
//            return true;
//        } catch (Exception e) {
//            try {
//                if (c != null) c.rollback();
//            } catch (Exception ex) {
//                System.err.println("DepartmentDAO rollback error: " + ex.getMessage());
//            }
//            System.err.println("DepartmentDAO.assignEmployeeToDepartment error: " + e.getMessage());
//            return false;
//        } finally {
//            try {
//                if (c != null) c.setAutoCommit(originalAutoCommit);
//            } catch (Exception ex) {
//                System.err.println("DepartmentDAO reset autoCommit error: " + ex.getMessage());
//            }
//        }
//    }
//
//    // =========================================================================
//    // THÊM MỚI: PHỤC VỤ RIÊNG CHO TASKBAR "TỔ CHỨC PHÒNG BAN"
//    // =========================================================================
//
//    // Lấy danh sách phòng ban kèm đếm số lượng nhân sự đang hoạt động
//    public List<DepartmentModel> getAllDepartmentsWithCount() {
//        List<DepartmentModel> list = new ArrayList<>();
//        if (c == null) return list;
//
//        String sql = "SELECT d.DepartmentID, d.DepartmentName, d.Description, d.ManagerID, "
//                   + "       c.CandidateName AS ManagerName, "
//                   + "       (SELECT COUNT(*) FROM dbo.Employee e WHERE e.Department = d.DepartmentName AND e.IsActive = 1) AS TotalEmp "
//                   + "FROM dbo.Department d "
//                   + "LEFT JOIN dbo.Employee m ON d.ManagerID = m.EmployeeID "
//                   + "LEFT JOIN dbo.Candidate c ON m.CandidateID = c.CandidateID "
//                   + "ORDER BY d.DepartmentName";
//
//        try (PreparedStatement ps = c.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//            while (rs.next()) {
//                DepartmentModel d = new DepartmentModel();
//                d.setDepartmentId(rs.getInt("DepartmentID"));
//                d.setDepartmentName(rs.getNString("DepartmentName"));
//                d.setDescription(rs.getNString("Description"));
//                d.setManagerId((Integer) rs.getObject("ManagerID"));
//                d.setManagerName(rs.getNString("ManagerName"));
//                d.setEmployeeCount(rs.getInt("TotalEmp"));
//                list.add(d);
//            }
//        } catch (Exception e) {
//            System.err.println("DepartmentDAO.getAllDepartmentsWithCount error: " + e.getMessage());
//        }
//        return list;
//    }
//
//    // Lấy danh sách nhân viên theo phòng ban (nếu deptName rỗng thì lấy tất cả nhân sự active)
//    public List<Map<String, Object>> getEmployeesByDepartment(String deptName) {
//        List<Map<String, Object>> list = new ArrayList<>();
//        if (c == null) return list;
//
//        StringBuilder sql = new StringBuilder();
//        sql.append("SELECT e.EmployeeID, e.Position, e.EmployeeCode, e.Department, ")
//           .append("       c.CandidateName AS FullName, c.Email, c.PhoneNumber, r.RoleName ")
//           .append("FROM dbo.Employee e ")
//           .append("JOIN dbo.Candidate c ON e.CandidateID = c.CandidateID ")
//           .append("JOIN dbo.Roles r ON e.RoleID = r.RoleID ")
//           .append("WHERE e.IsActive = 1 ");
//
//        if (deptName != null && !deptName.trim().isEmpty()) {
//            sql.append("AND e.Department = ? ");
//        }
//        sql.append("ORDER BY c.CandidateName");
//
//        try (PreparedStatement ps = c.prepareStatement(sql.toString())) {
//            if (deptName != null && !deptName.trim().isEmpty()) {
//                ps.setNString(1, deptName.trim());
//            }
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    Map<String, Object> emp = new HashMap<>();
//                    emp.put("employeeId", rs.getInt("EmployeeID"));
//                    emp.put("position", rs.getNString("Position"));
//                    emp.put("employeeCode", rs.getString("EmployeeCode"));
//                    emp.put("department", rs.getNString("Department"));
//                    emp.put("fullName", rs.getNString("FullName"));
//                    emp.put("email", rs.getString("Email"));
//                    emp.put("phone", rs.getString("PhoneNumber"));
//                    emp.put("roleName", rs.getString("RoleName"));
//                    list.add(emp);
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("DepartmentDAO.getEmployeesByDepartment error: " + e.getMessage());
//        }
//        return list;
//    }
//
//    // =========================================================================
//    // THÊM MỚI: XỬ LÝ TRANG CHI TIẾT PHÒNG BAN & TRƯỞNG NHÓM
//    // =========================================================================
//
//    /** Lấy thông tin phòng ban theo tên */
//    public DepartmentModel getDepartmentByName(String deptName) {
//        if (c == null || deptName == null) return null;
//        String sql = "SELECT d.DepartmentID, d.DepartmentName, d.Description, d.ManagerID, "
//                   + "       c.CandidateName AS ManagerName "
//                   + "FROM dbo.Department d "
//                   + "LEFT JOIN dbo.Employee m ON d.ManagerID = m.EmployeeID "
//                   + "LEFT JOIN dbo.Candidate c ON m.CandidateID = c.CandidateID "
//                   + "WHERE d.DepartmentName = ?";
//        try (PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setNString(1, deptName);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    DepartmentModel d = new DepartmentModel();
//                    d.setDepartmentId(rs.getInt("DepartmentID"));
//                    d.setDepartmentName(rs.getNString("DepartmentName"));
//                    d.setDescription(rs.getNString("Description"));
//                    d.setManagerId((Integer) rs.getObject("ManagerID"));
//                    d.setManagerName(rs.getNString("ManagerName"));
//                    return d;
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("DepartmentDAO.getDepartmentByName error: " + e.getMessage());
//        }
//        return null;
//    }
//
//    /** Lấy chi tiết thông tin nhân viên theo EmployeeID */
//    public Map<String, Object> getEmployeeDetailById(int employeeId) {
//        Map<String, Object> map = new HashMap<>();
//        if (c == null) return map;
//        String sql = "SELECT e.EmployeeID, e.EmployeeCode, e.Department, e.Position, e.HireDate, "
//                   + "       c.CandidateName AS FullName, c.Email, c.PhoneNumber, r.RoleName "
//                   + "FROM dbo.Employee e "
//                   + "JOIN dbo.Candidate c ON e.CandidateID = c.CandidateID "
//                   + "JOIN dbo.Roles r ON e.RoleID = r.RoleID "
//                   + "WHERE e.EmployeeID = ?";
//        try (PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setInt(1, employeeId);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    map.put("employeeId", rs.getInt("EmployeeID"));
//                    map.put("employeeCode", rs.getString("EmployeeCode"));
//                    map.put("department", rs.getNString("Department"));
//                    map.put("position", rs.getNString("Position"));
//                    map.put("hireDate", rs.getDate("HireDate"));
//                    map.put("fullName", rs.getNString("FullName"));
//                    map.put("email", rs.getString("Email"));
//                    map.put("phone", rs.getString("PhoneNumber"));
//                    map.put("roleName", rs.getString("RoleName"));
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("DepartmentDAO.getEmployeeDetailById error: " + e.getMessage());
//        }
//        return map;
//    }
//
//    /** Cập nhật Trưởng nhóm cho phòng ban trong bảng Department */
//    public boolean updateDepartmentLeader(int departmentId, Integer managerEmployeeId) {
//        if (c == null) return false;
//        String sql = "UPDATE dbo.Department SET ManagerID = ? WHERE DepartmentID = ?";
//        try (PreparedStatement ps = c.prepareStatement(sql)) {
//            if (managerEmployeeId != null && managerEmployeeId > 0) {
//                ps.setInt(1, managerEmployeeId);
//            } else {
//                ps.setNull(1, Types.INTEGER);
//            }
//            ps.setInt(2, departmentId);
//            return ps.executeUpdate() > 0;
//        } catch (Exception e) {
//            System.err.println("DepartmentDAO.updateDepartmentLeader error: " + e.getMessage());
//            return false;
//        }
//    }
//}


package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.DepartmentArchiveModel;
import model.DepartmentModel;
import model.DepartmentTopicModel;
import model.DepartmentTaskModel;

public class DepartmentDAO extends DBContext {

    // Lấy danh sách tất cả phòng ban
    public List<DepartmentModel> getAllDepartments() {
        List<DepartmentModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = "SELECT d.DepartmentID, d.DepartmentName, d.Description, d.ManagerID, c.CandidateName AS ManagerName "
                   + "FROM dbo.Department d "
                   + "LEFT JOIN dbo.Employee e ON d.ManagerID = e.EmployeeID "
                   + "LEFT JOIN dbo.Candidate c ON e.CandidateID = c.CandidateID "
                   + "ORDER BY d.DepartmentName";

        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DepartmentModel d = new DepartmentModel();
                d.setDepartmentId(rs.getInt("DepartmentID"));
                d.setDepartmentName(rs.getNString("DepartmentName"));
                d.setDescription(rs.getNString("Description"));
                d.setManagerId((Integer) rs.getObject("ManagerID"));
                d.setManagerName(rs.getNString("ManagerName"));
                list.add(d);
            }
        } catch (Exception e) {
            System.err.println("DepartmentDAO.getAllDepartments error: " + e.getMessage());
        }
        return list;
    }

    // Thêm mới phòng ban
    public boolean addDepartment(String departmentName, String description, Integer managerId) {
        if (c == null) return false;

        String sql = "INSERT INTO dbo.Department (DepartmentName, Description, ManagerID) VALUES (?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setNString(1, departmentName);
            ps.setNString(2, description);
            if (managerId != null && managerId > 0) {
                ps.setInt(3, managerId);
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("DepartmentDAO.addDepartment error: " + e.getMessage());
            return false;
        }
    }

    // Gán nhân sự vào phòng ban và ghi nhận lịch sử vào EmployeePositionHistory
    public boolean assignEmployeeToDepartment(int employeeId, String departmentName, String position, String note) {
        if (c == null) return false;

        String closeHistorySql = "UPDATE dbo.EmployeePositionHistory SET EndDate = CAST(GETDATE() AS DATE) "
                               + "WHERE EmployeeID = ? AND EndDate IS NULL";
        String updateEmpSql = "UPDATE dbo.Employee SET Department = ?, Position = ? WHERE EmployeeID = ?";
        String insertHistorySql = "INSERT INTO dbo.EmployeePositionHistory (EmployeeID, RoleID, Position, Department, StartDate, Note) "
                                + "SELECT EmployeeID, RoleID, ?, ?, CAST(GETDATE() AS DATE), ? "
                                + "FROM dbo.Employee WHERE EmployeeID = ?";

        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = c.getAutoCommit();
            c.setAutoCommit(false); // Bắt đầu transaction

            // 1. Đóng vị trí cũ
            try (PreparedStatement psClose = c.prepareStatement(closeHistorySql)) {
                psClose.setInt(1, employeeId);
                psClose.executeUpdate();
            }

            // 2. Cập nhật phòng ban mới cho Employee
            try (PreparedStatement psEmp = c.prepareStatement(updateEmpSql)) {
                psEmp.setNString(1, departmentName);
                psEmp.setNString(2, position);
                psEmp.setInt(3, employeeId);
                psEmp.executeUpdate();
            }

            // 3. Ghi lịch sử mới vào EmployeePositionHistory
            try (PreparedStatement psHist = c.prepareStatement(insertHistorySql)) {
                psHist.setNString(1, position);
                psHist.setNString(2, departmentName);
                psHist.setNString(3, note != null ? note : "Gán phòng ban bởi Manager");
                psHist.setInt(4, employeeId);
                psHist.executeUpdate();
            }

            c.commit();
            return true;
        } catch (Exception e) {
            try {
                if (c != null) c.rollback();
            } catch (Exception ex) {
                System.err.println("DepartmentDAO rollback error: " + ex.getMessage());
            }
            System.err.println("DepartmentDAO.assignEmployeeToDepartment error: " + e.getMessage());
            return false;
        } finally {
            try {
                if (c != null) c.setAutoCommit(originalAutoCommit);
            } catch (Exception ex) {
                System.err.println("DepartmentDAO reset autoCommit error: " + ex.getMessage());
            }
        }
    }

    // =========================================================================
    // THÊM MỚI: PHỤC VỤ RIÊNG CHO TASKBAR "TỔ CHỨC PHÒNG BAN"
    // =========================================================================

    // Lấy danh sách phòng ban kèm đếm số lượng nhân sự đang hoạt động
    public List<DepartmentModel> getAllDepartmentsWithCount() {
        List<DepartmentModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = "SELECT d.DepartmentID, d.DepartmentName, d.Description, d.ManagerID, "
                   + "       c.CandidateName AS ManagerName, "
                   + "       (SELECT COUNT(*) FROM dbo.Employee e WHERE e.Department = d.DepartmentName AND e.IsActive = 1) AS TotalEmp "
                   + "FROM dbo.Department d "
                   + "LEFT JOIN dbo.Employee m ON d.ManagerID = m.EmployeeID "
                   + "LEFT JOIN dbo.Candidate c ON m.CandidateID = c.CandidateID "
                   + "ORDER BY d.DepartmentName";

        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DepartmentModel d = new DepartmentModel();
                d.setDepartmentId(rs.getInt("DepartmentID"));
                d.setDepartmentName(rs.getNString("DepartmentName"));
                d.setDescription(rs.getNString("Description"));
                d.setManagerId((Integer) rs.getObject("ManagerID"));
                d.setManagerName(rs.getNString("ManagerName"));
                d.setEmployeeCount(rs.getInt("TotalEmp"));
                list.add(d);
            }
        } catch (Exception e) {
            System.err.println("DepartmentDAO.getAllDepartmentsWithCount error: " + e.getMessage());
        }
        return list;
    }

    // Lấy danh sách nhân viên theo phòng ban (nếu deptName rỗng thì lấy tất cả nhân sự active)
    public List<Map<String, Object>> getEmployeesByDepartment(String deptName) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (c == null) return list;

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT e.EmployeeID, e.Position, e.EmployeeCode, e.Department, ")
           .append("       c.CandidateName AS FullName, c.Email, c.PhoneNumber, r.RoleName ")
           .append("FROM dbo.Employee e ")
           .append("JOIN dbo.Candidate c ON e.CandidateID = c.CandidateID ")
           .append("JOIN dbo.Roles r ON e.RoleID = r.RoleID ")
           .append("WHERE e.IsActive = 1 ");

        if (deptName != null && !deptName.trim().isEmpty()) {
            sql.append("AND e.Department = ? ");
        }
        sql.append("ORDER BY c.CandidateName");

        try (PreparedStatement ps = c.prepareStatement(sql.toString())) {
            if (deptName != null && !deptName.trim().isEmpty()) {
                ps.setNString(1, deptName.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> emp = new HashMap<>();
                    emp.put("employeeId", rs.getInt("EmployeeID"));
                    emp.put("position", rs.getNString("Position"));
                    emp.put("employeeCode", rs.getString("EmployeeCode"));
                    emp.put("department", rs.getNString("Department"));
                    emp.put("fullName", rs.getNString("FullName"));
                    emp.put("email", rs.getString("Email"));
                    emp.put("phone", rs.getString("PhoneNumber"));
                    emp.put("roleName", rs.getString("RoleName"));
                    list.add(emp);
                }
            }
        } catch (Exception e) {
            System.err.println("DepartmentDAO.getEmployeesByDepartment error: " + e.getMessage());
        }
        return list;
    }

    // =========================================================================
    // THÊM MỚI: XỬ LÝ TRANG CHI TIẾT PHÒNG BAN & TRƯỞNG NHÓM
    // =========================================================================

    /** Lấy thông tin phòng ban theo tên */
    public DepartmentModel getDepartmentByName(String deptName) {
        if (c == null || deptName == null) return null;
        String sql = "SELECT d.DepartmentID, d.DepartmentName, d.Description, d.ManagerID, "
                   + "       c.CandidateName AS ManagerName "
                   + "FROM dbo.Department d "
                   + "LEFT JOIN dbo.Employee m ON d.ManagerID = m.EmployeeID "
                   + "LEFT JOIN dbo.Candidate c ON m.CandidateID = c.CandidateID "
                   + "WHERE d.DepartmentName = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setNString(1, deptName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DepartmentModel d = new DepartmentModel();
                    d.setDepartmentId(rs.getInt("DepartmentID"));
                    d.setDepartmentName(rs.getNString("DepartmentName"));
                    d.setDescription(rs.getNString("Description"));
                    d.setManagerId((Integer) rs.getObject("ManagerID"));
                    d.setManagerName(rs.getNString("ManagerName"));
                    return d;
                }
            }
        } catch (Exception e) {
            System.err.println("DepartmentDAO.getDepartmentByName error: " + e.getMessage());
        }
        return null;
    }

    /** Lấy chi tiết thông tin nhân viên theo EmployeeID */
    public Map<String, Object> getEmployeeDetailById(int employeeId) {
        Map<String, Object> map = new HashMap<>();
        if (c == null) return map;
        String sql = "SELECT e.EmployeeID, e.EmployeeCode, e.Department, e.Position, e.HireDate, "
                   + "       c.CandidateName AS FullName, c.Email, c.PhoneNumber, r.RoleName "
                   + "FROM dbo.Employee e "
                   + "JOIN dbo.Candidate c ON e.CandidateID = c.CandidateID "
                   + "JOIN dbo.Roles r ON e.RoleID = r.RoleID "
                   + "WHERE e.EmployeeID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    map.put("employeeId", rs.getInt("EmployeeID"));
                    map.put("employeeCode", rs.getString("EmployeeCode"));
                    map.put("department", rs.getNString("Department"));
                    map.put("position", rs.getNString("Position"));
                    map.put("hireDate", rs.getDate("HireDate"));
                    map.put("fullName", rs.getNString("FullName"));
                    map.put("email", rs.getString("Email"));
                    map.put("phone", rs.getString("PhoneNumber"));
                    map.put("roleName", rs.getString("RoleName"));
                }
            }
        } catch (Exception e) {
            System.err.println("DepartmentDAO.getEmployeeDetailById error: " + e.getMessage());
        }
        return map;
    }

    /** Cập nhật Trưởng nhóm cho phòng ban trong bảng Department */
    public boolean updateDepartmentLeader(int departmentId, Integer managerEmployeeId) {
        if (c == null) return false;
        String sql = "UPDATE dbo.Department SET ManagerID = ? WHERE DepartmentID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            if (managerEmployeeId != null && managerEmployeeId > 0) {
                ps.setInt(1, managerEmployeeId);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, departmentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("DepartmentDAO.updateDepartmentLeader error: " + e.getMessage());
            return false;
        }
    }

    /** Cập nhật thông tin đầy đủ của nhân sự: Email, SĐT, Vị trí và Phòng ban mới */
    public boolean updateEmployeeFullInfo(int employeeId, String email, String phone, String department, String position, String note) {
        if (c == null) return false;

        // Cập nhật Email và Số điện thoại vào bảng Candidate
        String updateCandidateSql = "UPDATE dbo.Candidate SET Email = ?, PhoneNumber = ? "
                                  + "WHERE CandidateID = (SELECT CandidateID FROM dbo.Employee WHERE EmployeeID = ?)";
        try (PreparedStatement ps = c.prepareStatement(updateCandidateSql)) {
            ps.setString(1, email);
            ps.setString(2, phone);
            ps.setInt(3, employeeId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("DepartmentDAO.updateEmployeeFullInfo Candidate error: " + e.getMessage());
        }

        // Tái sử dụng logic điều chuyển phòng ban và ghi log lịch sử
        return assignEmployeeToDepartment(employeeId, department, position, note);
    }

    // =========================================================================
    // PHỤC VỤ CHO 16 TRƯỞNG NHÓM (LEADER ROOM MODULE)
    // =========================================================================

    public DepartmentModel getDepartmentByLeaderId(int employeeId) {
        if (c == null) return null;

        // SỬA: Dùng LEFT JOIN cho Candidate và hỗ trợ tìm theo ManagerID HOẶC phòng ban hiện tại của nhân sự
        String sql = "SELECT TOP 1 d.DepartmentID, d.DepartmentName, d.Description, d.ManagerID, "
                   + "       COALESCE(c.CandidateName, e.EmployeeCode, N'Trưởng phòng') AS ManagerName "
                   + "FROM dbo.Department d "
                   + "LEFT JOIN dbo.Employee m ON d.ManagerID = m.EmployeeID "
                   + "LEFT JOIN dbo.Employee e ON (e.EmployeeID = ? AND (d.ManagerID = e.EmployeeID OR d.DepartmentName = e.Department)) "
                   + "LEFT JOIN dbo.Candidate c ON (m.CandidateID = c.CandidateID OR e.CandidateID = c.CandidateID) "
                   + "WHERE d.ManagerID = ? OR e.EmployeeID = ? "
                   + "ORDER BY CASE WHEN d.ManagerID = ? THEN 1 ELSE 2 END";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, employeeId);
            ps.setInt(3, employeeId);
            ps.setInt(4, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DepartmentModel d = new DepartmentModel();
                    d.setDepartmentId(rs.getInt("DepartmentID"));

                    try {
                        d.setDepartmentName(rs.getNString("DepartmentName"));
                    } catch (Exception ex) {
                        d.setDepartmentName(rs.getString("DepartmentName"));
                    }

                    try {
                        d.setDescription(rs.getNString("Description"));
                    } catch (Exception ex) {
                        d.setDescription(rs.getString("Description"));
                    }

                    d.setManagerId((Integer) rs.getObject("ManagerID"));

                    try {
                        d.setManagerName(rs.getNString("ManagerName"));
                    } catch (Exception ex) {
                        d.setManagerName(rs.getString("ManagerName"));
                    }

                    return d;
                }
            }
        } catch (Exception e) {
            System.err.println("DepartmentDAO.getDepartmentByLeaderId error: " + e.getMessage());
        }
        return null;
    }

    /** 2. Lấy danh sách chủ đề theo DepartmentID */
    public List<DepartmentTopicModel> getTopicsByDepartment(int departmentId) {
        List<DepartmentTopicModel> list = new ArrayList<>();
        if (c == null) return list;
        String sql = "SELECT TopicID, DepartmentID, TopicTitle, Description, CreatedAt, CreatedBy "
                   + "FROM dbo.DepartmentTopic WHERE DepartmentID = ? ORDER BY CreatedAt DESC";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DepartmentTopicModel t = new DepartmentTopicModel();
                    t.setTopicId(rs.getInt("TopicID"));
                    t.setDepartmentId(rs.getInt("DepartmentID"));
                    t.setTopicTitle(rs.getNString("TopicTitle"));
                    t.setDescription(rs.getNString("Description"));
                    t.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    t.setCreatedBy(rs.getInt("CreatedBy"));
                    list.add(t);
                }
            }
        } catch (Exception e) {
            System.err.println("DepartmentDAO.getTopicsByDepartment error: " + e.getMessage());
        }
        return list;
    }

    /** 3. Thêm mới chủ đề giao việc */
    public boolean addTopic(int deptId, String title, String desc, int createdBy) {
        if (c == null) return false;
        String sql = "INSERT INTO dbo.DepartmentTopic (DepartmentID, TopicTitle, Description, CreatedBy) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            ps.setNString(2, title);
            ps.setNString(3, desc);
            ps.setInt(4, createdBy);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("DepartmentDAO.addTopic error: " + e.getMessage());
            return false;
        }
    }

    /** 4. Lấy danh sách nhiệm vụ của một nhân viên cụ thể */
    public List<DepartmentTaskModel> getTasksByEmployee(int employeeId) {
        List<DepartmentTaskModel> list = new ArrayList<>();
        if (c == null) return list;
        String sql = "SELECT t.TaskID, t.TopicID, t.EmployeeID, t.TaskContent, t.Deadline, t.Progress, "
                   + "       t.LeaderNote, t.IsCompleted, "
                   + "       COALESCE(c.CandidateName, e.EmployeeCode, N'Nhân viên') AS CandidateName, "
                   + "       COALESCE(e.EmployeeCode, CONCAT('EMP', t.EmployeeID)) AS EmployeeCode "
                   + "FROM dbo.DepartmentTask t "
                   + "LEFT JOIN dbo.Employee e ON t.EmployeeID = e.EmployeeID "
                   + "LEFT JOIN dbo.Candidate c ON e.CandidateID = c.CandidateID "
                   + "WHERE t.EmployeeID = ? "
                   + "ORDER BY t.TaskID DESC";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DepartmentTaskModel task = new DepartmentTaskModel();
                    task.setTaskId(rs.getInt("TaskID"));
                    task.setTopicId(rs.getInt("TopicID"));
                    task.setEmployeeId(rs.getInt("EmployeeID"));

                    Object contentObj = rs.getObject("TaskContent");
                    task.setTaskContent(contentObj != null ? contentObj.toString() : "");

                    try {
                        task.setDeadline(rs.getDate("Deadline"));
                    } catch (Exception ex) {
                        try {
                            String dStr = rs.getString("Deadline");
                            if (dStr != null && dStr.trim().length() >= 10) {
                                task.setDeadline(java.sql.Date.valueOf(dStr.trim().substring(0, 10)));
                            }
                        } catch (Exception ignored) {}
                    }

                    task.setProgress(rs.getInt("Progress"));

                    Object noteObj = rs.getObject("LeaderNote");
                    task.setLeaderNote(noteObj != null ? noteObj.toString() : "");

                    task.setCompleted(rs.getBoolean("IsCompleted"));

                    Object nameObj = rs.getObject("CandidateName");
                    task.setEmployeeName(nameObj != null ? nameObj.toString() : "Nhân viên");

                    Object codeObj = rs.getObject("EmployeeCode");
                    task.setEmployeeCode(codeObj != null ? codeObj.toString() : "EMP" + task.getEmployeeId());

                    list.add(task);
                }
            }
        } catch (Exception e) {
            System.err.println("DepartmentDAO.getTasksByEmployee error: " + e.getMessage());
        }
        return list;
    }

    /** 5. Thêm mới nhiệm vụ phân công cho nhân sự */
    public boolean addTask(int topicId, int empId, String content, String deadline, int progress, String note, boolean completed) {
        if (c == null) return false;
        String sql = "INSERT INTO dbo.DepartmentTask (TopicID, EmployeeID, TaskContent, Deadline, Progress, LeaderNote, IsCompleted) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, topicId);
            ps.setInt(2, empId);
            ps.setNString(3, content);
            ps.setDate(4, java.sql.Date.valueOf(deadline));
            ps.setInt(5, progress);
            ps.setNString(6, note);
            ps.setBoolean(7, completed);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("DepartmentDAO.addTask error: " + e.getMessage());
            return false;
        }
    }

    /** 6. Cập nhật tiến độ, ghi chú và trạng thái tích hoàn thành của nhiệm vụ */
    public boolean updateTaskStatus(int taskId, int progress, String note, boolean completed) {
        if (c == null) return false;
        String sql = "UPDATE dbo.DepartmentTask SET Progress = ?, LeaderNote = ?, IsCompleted = ? WHERE TaskID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, progress);
            ps.setNString(2, note);
            ps.setBoolean(3, completed);
            ps.setInt(4, taskId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("DepartmentDAO.updateTaskStatus error: " + e.getMessage());
            return false;
        }
    }

    /** 7. Lấy toàn bộ danh sách Task của phòng ban phục vụ màn hình Tổng kết tiến độ */
    public List<DepartmentTaskModel> getAllDepartmentTasks(int departmentId) {
        List<DepartmentTaskModel> list = new ArrayList<>();
        if (c == null) return list;

        // Đã đổi alias 'top' thành 'dt' để không trùng từ khóa TOP của SQL Server
        String sql = "SELECT t.TaskID, t.TopicID, t.EmployeeID, t.TaskContent, t.Deadline, t.Progress, "
                   + "       t.LeaderNote, t.IsCompleted, "
                   + "       COALESCE(c.CandidateName, e.EmployeeCode, N'Nhân sự') AS EmpFullName, "
                   + "       COALESCE(e.EmployeeCode, CONCAT('EMP', t.EmployeeID)) AS EmpCode "
                   + "FROM dbo.DepartmentTask t "
                   + "JOIN dbo.DepartmentTopic dt ON t.TopicID = dt.TopicID "
                   + "LEFT JOIN dbo.Employee e ON t.EmployeeID = e.EmployeeID "
                   + "LEFT JOIN dbo.Candidate c ON e.CandidateID = c.CandidateID "
                   + "WHERE dt.DepartmentID = ? "
                   + "ORDER BY t.TaskID DESC";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, departmentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DepartmentTaskModel task = new DepartmentTaskModel();
                    task.setTaskId(rs.getInt("TaskID"));
                    task.setTopicId(rs.getInt("TopicID"));
                    task.setEmployeeId(rs.getInt("EmployeeID"));

                    // 1. TaskContent
                    Object contentObj = rs.getObject("TaskContent");
                    task.setTaskContent(contentObj != null ? contentObj.toString() : "");

                    // 2. Deadline
                    try {
                        task.setDeadline(rs.getDate("Deadline"));
                    } catch (Exception ex) {
                        try {
                            Object dObj = rs.getObject("Deadline");
                            if (dObj instanceof java.util.Date) {
                                task.setDeadline((java.util.Date) dObj);
                            }
                        } catch (Exception ignored) {}
                    }

                    // 3. Tiến độ & Ghi chú
                    task.setProgress(rs.getInt("Progress"));
                    Object noteObj = rs.getObject("LeaderNote");
                    task.setLeaderNote(noteObj != null ? noteObj.toString() : "");

                    // 4. Trạng thái Hoàn thành
                    task.setCompleted(rs.getBoolean("IsCompleted"));

                    // 5. Tên nhân sự & Mã nhân sự
                    Object nameObj = rs.getObject("EmpFullName");
                    task.setEmployeeName(nameObj != null ? nameObj.toString() : "Nhân sự");

                    Object codeObj = rs.getObject("EmpCode");
                    task.setEmployeeCode(codeObj != null ? codeObj.toString() : "EMP" + task.getEmployeeId());

                    list.add(task);
                }
            }
        } catch (Exception e) {
            System.err.println("DepartmentDAO.getAllDepartmentTasks error: " + e.getMessage());
        }
        return list;
    }

    // =========================================================================
    // PHỤC VỤ TÍNH NĂNG "KHO LƯU TRỮ" & "RESET DỰ ÁN"
    // =========================================================================

    /** 8. Lưu toàn bộ nội dung Tổng kết tiến độ của phòng ban vào Kho lưu trữ */
    public boolean archiveCurrentProject(int departmentId, String projectName, int createdBy) {
        if (c == null) return false;
        List<DepartmentTaskModel> currentTasks = getAllDepartmentTasks(departmentId);
        if (currentTasks == null || currentTasks.isEmpty()) return false;

        int total = currentTasks.size();
        int completed = 0;
        int totalPercent = 0;
        for (DepartmentTaskModel t : currentTasks) {
            int p = Math.max(0, Math.min(100, t.getProgress()));
            totalPercent += p;
            if (p >= 100) completed++;
        }
        int avgPercent = total > 0 ? Math.round((float) totalPercent / total) : 0;

        String insertArchiveSql = "INSERT INTO dbo.DepartmentArchive (DepartmentID, ProjectName, ArchiveDate, TotalTasks, CompletedTasks, AvgPercent, CreatedBy) "
                                + "VALUES (?, ?, GETDATE(), ?, ?, ?, ?)";
        String insertTaskSql = "INSERT INTO dbo.DepartmentArchiveTask (ArchiveID, EmployeeCode, EmployeeName, TaskContent, Deadline, Progress, LeaderNote, IsCompleted) "
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = c.getAutoCommit();
            c.setAutoCommit(false); // Bắt đầu transaction đảm bảo toàn vẹn
            int archiveId = 0;

            try (PreparedStatement ps = c.prepareStatement(insertArchiveSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, departmentId);
                ps.setNString(2, projectName);
                ps.setInt(3, total);
                ps.setInt(4, completed);
                ps.setInt(5, avgPercent);
                ps.setInt(6, createdBy);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) archiveId = rs.getInt(1);
                }
            }

            if (archiveId > 0) {
                try (PreparedStatement psTask = c.prepareStatement(insertTaskSql)) {
                    for (DepartmentTaskModel t : currentTasks) {
                        psTask.setInt(1, archiveId);
                        psTask.setString(2, t.getEmployeeCode());
                        psTask.setNString(3, t.getEmployeeName());
                        psTask.setNString(4, t.getTaskContent());
                        psTask.setString(5, t.getDeadline() != null ? t.getDeadline().toString() : "");
                        psTask.setInt(6, t.getProgress());
                        psTask.setNString(7, t.getLeaderNote());
                        psTask.setBoolean(8, t.isCompleted());
                        psTask.addBatch();
                    }
                    psTask.executeBatch();
                }
                c.commit();
                return true;
            }
        } catch (Exception ex) {
            try {
                if (c != null) c.rollback();
            } catch (Exception ignored) {}
            System.err.println("DepartmentDAO.archiveCurrentProject error: " + ex.getMessage());
        } finally {
            try {
                if (c != null) c.setAutoCommit(originalAutoCommit);
            } catch (Exception ignored) {}
        }
        return false;
    }

    /** 9. Lấy danh sách các dự án đã lưu trong Kho lưu trữ theo từng phòng ban riêng biệt */
    public List<DepartmentArchiveModel> getArchivesByDepartment(int departmentId) {
        List<DepartmentArchiveModel> list = new ArrayList<>();
        if (c == null) return list;
        String sql = "SELECT ArchiveID, DepartmentID, ProjectName, ArchiveDate, TotalTasks, CompletedTasks, AvgPercent "
                   + "FROM dbo.DepartmentArchive WHERE DepartmentID = ? ORDER BY ArchiveID DESC";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DepartmentArchiveModel a = new DepartmentArchiveModel();
                    a.setArchiveId(rs.getInt("ArchiveID"));
                    a.setDepartmentId(rs.getInt("DepartmentID"));
                    a.setProjectName(rs.getNString("ProjectName"));
                    a.setArchiveDate(rs.getTimestamp("ArchiveDate"));
                    a.setTotalTasks(rs.getInt("TotalTasks"));
                    a.setCompletedTasks(rs.getInt("CompletedTasks"));
                    a.setAvgPercent(rs.getInt("AvgPercent"));
                    list.add(a);
                }
            }
        } catch (Exception e) {
            System.err.println("DepartmentDAO.getArchivesByDepartment error: " + e.getMessage());
        }
        return list;
    }

    /** 10. Lấy chi tiết một dự án trong Kho lưu trữ (kèm danh sách nhiệm vụ đã snapshot) */
    public DepartmentArchiveModel getArchiveDetailById(int archiveId) {
        if (c == null) return null;
        DepartmentArchiveModel a = null;
        String sql = "SELECT ArchiveID, DepartmentID, ProjectName, ArchiveDate, TotalTasks, CompletedTasks, AvgPercent "
                   + "FROM dbo.DepartmentArchive WHERE ArchiveID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, archiveId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    a = new DepartmentArchiveModel();
                    a.setArchiveId(rs.getInt("ArchiveID"));
                    a.setDepartmentId(rs.getInt("DepartmentID"));
                    a.setProjectName(rs.getNString("ProjectName"));
                    a.setArchiveDate(rs.getTimestamp("ArchiveDate"));
                    a.setTotalTasks(rs.getInt("TotalTasks"));
                    a.setCompletedTasks(rs.getInt("CompletedTasks"));
                    a.setAvgPercent(rs.getInt("AvgPercent"));
                }
            }
        } catch (Exception e) {
            System.err.println("DepartmentDAO.getArchiveDetailById error: " + e.getMessage());
        }

        if (a != null) {
            List<DepartmentTaskModel> tasks = new ArrayList<>();
            String sqlTasks = "SELECT ArchiveTaskID, EmployeeCode, EmployeeName, TaskContent, Deadline, Progress, LeaderNote, IsCompleted "
                            + "FROM dbo.DepartmentArchiveTask WHERE ArchiveID = ? ORDER BY ArchiveTaskID ASC";
            try (PreparedStatement ps2 = c.prepareStatement(sqlTasks)) {
                ps2.setInt(1, archiveId);
                try (ResultSet rs2 = ps2.executeQuery()) {
                    while (rs2.next()) {
                        DepartmentTaskModel t = new DepartmentTaskModel();
                        t.setTaskId(rs2.getInt("ArchiveTaskID"));
                        t.setEmployeeCode(rs2.getString("EmployeeCode"));
                        t.setEmployeeName(rs2.getNString("EmployeeName"));
                        t.setTaskContent(rs2.getNString("TaskContent"));
                        try {
                            String dl = rs2.getString("Deadline");
                            if (dl != null && !dl.isEmpty()) {
                                if (dl.trim().length() >= 10) {
                                    t.setDeadline(java.sql.Date.valueOf(dl.trim().substring(0, 10)));
                                }
                            }
                        } catch (Exception ignored) {}
                        t.setProgress(rs2.getInt("Progress"));
                        t.setLeaderNote(rs2.getNString("LeaderNote"));
                        t.setCompleted(rs2.getBoolean("IsCompleted"));
                        tasks.add(t);
                    }
                }
            } catch (Exception e) {
                System.err.println("DepartmentDAO.getArchiveDetailById tasks error: " + e.getMessage());
            }
            a.setTaskList(tasks);
        }
        return a;
    }

    /** 11. Reset trắng trơn Chủ đề & Nhiệm vụ hiện tại của phòng ban để bắt đầu đợt mới */
    public boolean resetCurrentDepartmentTopicsAndTasks(int departmentId) {
        if (c == null) return false;
        String sqlDeleteTasks = "DELETE t FROM dbo.DepartmentTask t JOIN dbo.DepartmentTopic dt ON t.TopicID = dt.TopicID WHERE dt.DepartmentID = ?";
        String sqlDeleteTopics = "DELETE FROM dbo.DepartmentTopic WHERE DepartmentID = ?";
        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = c.getAutoCommit();
            c.setAutoCommit(false);
            try (PreparedStatement ps1 = c.prepareStatement(sqlDeleteTasks)) {
                ps1.setInt(1, departmentId);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = c.prepareStatement(sqlDeleteTopics)) {
                ps2.setInt(1, departmentId);
                ps2.executeUpdate();
            }
            c.commit();
            return true;
        } catch (Exception e) {
            try {
                if (c != null) c.rollback();
            } catch (Exception ignored) {}
            System.err.println("DepartmentDAO.resetCurrentDepartmentTopicsAndTasks error: " + e.getMessage());
        } finally {
            try {
                if (c != null) c.setAutoCommit(originalAutoCommit);
            } catch (Exception ignored) {}
        }
        return false;
    }
}