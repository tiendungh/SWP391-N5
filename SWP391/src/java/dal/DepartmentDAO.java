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
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.DepartmentModel;

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
}