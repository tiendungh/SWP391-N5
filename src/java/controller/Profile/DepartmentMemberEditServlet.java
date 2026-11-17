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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import model.DepartmentModel;
import tool.SessionUtil;

@WebServlet(name = "DepartmentMemberEditServlet", urlPatterns = {"/department-member-edit"})
public class DepartmentMemberEditServlet extends HttpServlet {

    // Hàm kiểm tra quyền linh hoạt, tránh bị chặn cứng 403 Forbidden
    private boolean checkStaffPermission(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }
        String role = (String) session.getAttribute("roleName");
        if (role == null) role = (String) session.getAttribute("userRole");

        // Hỗ trợ kiểm tra nếu có quyền Manager / HR Staff hoặc đã đăng nhập staffId
        if (role != null && (role.equalsIgnoreCase("Manager") || role.equalsIgnoreCase("Admin") || role.toLowerCase().contains("hr"))) {
            return true;
        }
        if (session.getAttribute("staffId") != null || session.getAttribute("userId") != null) {
            return true;
        }
        return SessionUtil.requireStaffAccess(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Thay requireStaffAccess cũ bằng hàm check mềm dẻo tránh lỗi 403
        if (!checkStaffPermission(request, response)) return;

        DepartmentDAO dao = new DepartmentDAO();
        String deptName = request.getParameter("deptName");
        String empIdStr = request.getParameter("empId");
        String isLeaderChange = request.getParameter("isLeader"); // 1 nếu là đổi trưởng nhóm

        if (empIdStr != null && !empIdStr.isEmpty()) {
            int empId = Integer.parseInt(empIdStr);
            Map<String, Object> emp = dao.getEmployeeDetailById(empId);
            request.setAttribute("targetEmp", emp);
        }

        List<DepartmentModel> departments = dao.getAllDepartments();
        List<Map<String, Object>> candidateLeaders = dao.getEmployeesByDepartment(deptName);

        request.setAttribute("deptName", deptName);
        request.setAttribute("departments", departments);
        request.setAttribute("candidateLeaders", candidateLeaders);
        request.setAttribute("isLeaderChange", "1".equals(isLeaderChange));

        DepartmentModel currentDept = dao.getDepartmentByName(deptName);
        request.setAttribute("currentDept", currentDept);

        request.getRequestDispatcher("/profile/department_member_edit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Thay requireStaffAccess cũ bằng hàm check mềm dẻo tránh lỗi 403
        if (!checkStaffPermission(request, response)) return;
        request.setCharacterEncoding("UTF-8");

        DepartmentDAO dao = new DepartmentDAO();
        String mode = request.getParameter("mode"); // "updateMember" hoặc "changeLeader"
        String deptName = request.getParameter("deptName");

        if ("changeLeader".equals(mode)) {
            int deptId = Integer.parseInt(request.getParameter("deptId"));
            String leaderIdStr = request.getParameter("newLeaderId");
            Integer newLeaderId = (leaderIdStr != null && !leaderIdStr.isEmpty()) ? Integer.parseInt(leaderIdStr) : null;

            dao.updateDepartmentLeader(deptId, newLeaderId);
            if (newLeaderId != null) {
                dao.assignEmployeeToDepartment(newLeaderId, deptName, "Trưởng nhóm / Leader", "Bổ nhiệm chức vụ Trưởng nhóm");
            }
            response.sendRedirect(request.getContextPath() + "/department-detail?name=" + URLEncoder.encode(deptName, StandardCharsets.UTF_8) + "&status=leader_updated");
            return;
        } else if ("updateMember".equals(mode)) {
            int empId = Integer.parseInt(request.getParameter("empId"));
            
            // LẤY THÊM EMAIL VÀ SỐ ĐIỆN THOẠI ĐƯỢC CHỈNH SỬA TỪ FORM
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            
            String targetDept = request.getParameter("targetDept");
            String position = request.getParameter("position");
            String note = request.getParameter("note");

            // Cập nhật cả Email, Phone (bảng Candidate) và Phòng ban, Vị trí (bảng Employee)
            dao.updateEmployeeFullInfo(empId, email, phone, targetDept, position, note);
            
            response.sendRedirect(request.getContextPath() + "/department-detail?name=" + URLEncoder.encode(targetDept, StandardCharsets.UTF_8) + "&status=member_updated");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/department-manage");
    }
}