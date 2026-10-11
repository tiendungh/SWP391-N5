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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.DepartmentModel;
import tool.SessionUtil;

@WebServlet(name = "DepartmentDetailServlet", urlPatterns = {"/department-detail"})
public class DepartmentDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        String deptName = request.getParameter("name");
        if (deptName == null || deptName.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/department-manage");
            return;
        }
        deptName = deptName.trim();

        DepartmentDAO dao = new DepartmentDAO();
        DepartmentModel department = dao.getDepartmentByName(deptName);
        if (department == null) {
            department = new DepartmentModel(0, deptName, "Phòng ban chuyên môn", null);
        }

        // Lấy tất cả nhân sự thuộc phòng ban này
        List<Map<String, Object>> allDeptEmployees = dao.getEmployeesByDepartment(deptName);

        Map<String, Object> leaderInfo = null;
        List<Map<String, Object>> staffList = new ArrayList<>();

        // Tách riêng Trưởng nhóm và Nhân viên thông thường
        Integer managerId = department.getManagerId();
        for (Map<String, Object> emp : allDeptEmployees) {
            int empId = (int) emp.get("employeeId");
            if (managerId != null && managerId == empId) {
                leaderInfo = emp;
            } else {
                staffList.add(emp);
            }
        }

        // Nếu trong bảng Department chưa có managerId, nhưng có nhân sự mang chức danh Lead/Manager
        if (leaderInfo == null && !allDeptEmployees.isEmpty()) {
            for (Map<String, Object> emp : allDeptEmployees) {
                String pos = (String) emp.get("position");
                if (pos != null && (pos.toLowerCase().contains("lead") || pos.toLowerCase().contains("trưởng") || pos.toLowerCase().contains("manager"))) {
                    leaderInfo = emp;
                    staffList.remove(emp);
                    break;
                }
            }
        }

        request.setAttribute("dept", department);
        request.setAttribute("leader", leaderInfo);
        request.setAttribute("staffList", staffList);
        request.setAttribute("totalCount", allDeptEmployees.size());

        request.getRequestDispatcher("/profile/department_detail.jsp").forward(request, response);
    }
}
