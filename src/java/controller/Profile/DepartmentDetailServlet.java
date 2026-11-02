
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.Profile;

import dal.DepartmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.DepartmentModel;
import tool.SessionUtil;

@WebServlet(name = "DepartmentDetailServlet", urlPatterns = {"/department-detail"})
public class DepartmentDetailServlet extends HttpServlet {

    // THÊM MỚI: Kiểm tra quyền linh hoạt, mở quyền cho cả Leader tránh lỗi 403 Forbidden
    private boolean checkAccessPermission(HttpServletRequest request, HttpServletResponse response, DepartmentDAO dao) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }

        String role = (String) session.getAttribute("roleName");
        if (role == null) role = (String) session.getAttribute("userRole");

        // 1. Quản lý, Admin hoặc HR Staff luôn có quyền truy cập
        if (role != null && (role.equalsIgnoreCase("Manager") || role.equalsIgnoreCase("Admin") || role.toLowerCase().contains("hr"))) {
            return true;
        }

        // 2. Nếu tài khoản là Employee nhưng đang là Trưởng nhóm của một phòng ban bất kỳ
        Object empIdObj = session.getAttribute("employeeId");
        if (empIdObj == null) empIdObj = session.getAttribute("staffId");
        if (empIdObj == null) empIdObj = session.getAttribute("userId");
        if (empIdObj != null) {
            try {
                int empId = Integer.parseInt(empIdObj.toString());
                if (dao.getDepartmentByLeaderId(empId) != null) {
                    return true;
                }
            } catch (Exception ignored) {}
        }

        // 3. Fallback theo logic SessionUtil cũ nếu thỏa mãn
        if (session.getAttribute("staffId") != null || session.getAttribute("userId") != null) {
            return true;
        }

        return SessionUtil.requireStaffAccess(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DepartmentDAO dao = new DepartmentDAO();

        // Thay requireStaffAccess bằng hàm kiểm tra quyền mở rộng cho cả Leader
        if (!checkAccessPermission(request, response, dao)) return;

        String deptName = request.getParameter("name");
        if (deptName == null || deptName.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/department-manage");
            return;
        }
        deptName = deptName.trim();

        DepartmentModel department = dao.getDepartmentByName(deptName);
        if (department == null) {
            department = new DepartmentModel(0, deptName, "Phòng ban chuyên môn", null);
        }

        // Lấy tất cả nhân sự thuộc phòng ban này (dùng List thô không dùng ngoặc nhọn để không bao giờ lỗi cú pháp)
        List allDeptEmployees = dao.getEmployeesByDepartment(deptName);

        Map leaderInfo = null;
        List staffList = new ArrayList();

        // Tách riêng Trưởng nhóm và Nhân viên thông thường
        Integer managerId = department.getManagerId();
        if (allDeptEmployees != null) {
            for (Object obj : allDeptEmployees) {
                Map emp = (Map) obj;
                int empId = (int) emp.get("employeeId");
                if (managerId != null && managerId == empId) {
                    leaderInfo = emp;
                } else {
                    staffList.add(emp);
                }
            }

            // Nếu trong bảng Department chưa có managerId, nhưng có nhân sự mang chức danh Lead/Manager
            if (leaderInfo == null && !allDeptEmployees.isEmpty()) {
                for (Object obj : allDeptEmployees) {
                    Map emp = (Map) obj;
                    String pos = (String) emp.get("position");
                    if (pos != null && (pos.toLowerCase().contains("lead") || pos.toLowerCase().contains("trưởng") || pos.toLowerCase().contains("manager"))) {
                        leaderInfo = emp;
                        staffList.remove(emp);
                        break;
                    }
                }
            }
        }

        request.setAttribute("dept", department);
        request.setAttribute("leader", leaderInfo);
        request.setAttribute("staffList", staffList);
        request.setAttribute("totalCount", allDeptEmployees != null ? allDeptEmployees.size() : 0);

        request.getRequestDispatcher("/profile/department_detail.jsp").forward(request, response);
    }
}