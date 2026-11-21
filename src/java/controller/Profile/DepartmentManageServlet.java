///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package controller.Profile;
//
//import dal.DepartmentDAO;
//import dal.StaffDAO; // Có sẵn trong dự án để lấy list nhân viên
//import model.DepartmentModel;
//import model.StaffModel;
//import tool.SessionUtil; // Có sẵn trong dự án
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//@WebServlet(name = "DepartmentManageServlet", urlPatterns = {"/department-manage"})
//public class DepartmentManageServlet extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        HttpSession session = request.getSession();
//
//        // 1. Phân quyền Manager: Kiểm tra session đăng nhập (giữ nguyên logic gốc)
//        String role = (String) session.getAttribute("userRole"); // Hoặc dùng hàm check trong SessionUtil
//        if (role == null || (!role.equalsIgnoreCase("Manager") && !role.equalsIgnoreCase("Admin"))) {
//            // Hỗ trợ kiểm tra dự phòng nếu hệ thống lưu role qua SessionUtil
//            if (!SessionUtil.isHrOrManager(request)) {
//                response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
//                return;
//            }
//        }
//
//        // BỔ SUNG: Nếu người dùng chọn 1 phòng ban từ dropdown -> chuyển hướng sang trang chi tiết phòng ban đó
//        String selectedDept = request.getParameter("dept");
//        if (selectedDept != null && !selectedDept.trim().isEmpty()) {
//            response.sendRedirect(request.getContextPath() + "/department-detail?name=" 
//                    + URLEncoder.encode(selectedDept.trim(), StandardCharsets.UTF_8));
//            return;
//        }
//
//        DepartmentDAO departmentDAO = new DepartmentDAO();
//
//        // 2. Lấy dữ liệu danh sách phòng ban kèm số lượng nhân sự
//        List<DepartmentModel> departments = departmentDAO.getAllDepartmentsWithCount();
//        if (departments == null || departments.isEmpty()) {
//            departments = departmentDAO.getAllDepartments();
//        }
//        request.setAttribute("departments", departments);
//        request.setAttribute("selectedDept", selectedDept);
//
//        // Lấy danh sách nhân sự trực thuộc phòng ban đang chọn (hoặc tất cả nếu chưa chọn)
//        List employees = departmentDAO.getEmployeesByDepartment(selectedDept);
//        request.setAttribute("employees", employees);
//
//        // Tính tổng số nhân sự đang hoạt động
//        int totalEmployees = 0;
//        if (departments != null) {
//            for (DepartmentModel d : departments) {
//                totalEmployees += d.getEmployeeCount();
//            }
//        }
//        request.setAttribute("totalEmployees", totalEmployees);
//
//        // Lấy danh sách nhân viên từ StaffDAO để phục vụ form chọn nhân sự cần phân công
//        StaffDAO staffDAO = new StaffDAO();
//        List<StaffModel> staffList = staffDAO.getActiveStaff();
//        request.setAttribute("staffList", staffList);
//
//        // Chuyển tiếp tới giao diện JSP
//        request.getRequestDispatcher("/profile/department_manage.jsp").forward(request, response);
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        request.setCharacterEncoding("UTF-8");
//        HttpSession session = request.getSession();
//
//        String role = (String) session.getAttribute("userRole");
//        if (role == null || (!role.equalsIgnoreCase("Manager") && !role.equalsIgnoreCase("Admin"))) {
//            if (!SessionUtil.isHrOrManager(request)) {
//                response.sendError(HttpServletResponse.SC_FORBIDDEN);
//                return;
//            }
//        }
//
//        DepartmentDAO departmentDAO = new DepartmentDAO();
//        String action = request.getParameter("action");
//        if ("createDepartment".equals(action)) {
//            String name = request.getParameter("departmentName");
//            String desc = request.getParameter("description");
//            String managerIdStr = request.getParameter("managerId");
//
//            if (name == null || name.trim().isEmpty()) {
//                response.sendRedirect(request.getContextPath() + "/department-manage?error=empty_name");
//                return;
//            }
//            if (departmentDAO.isDepartmentNameExists(name)) {
//                response.sendRedirect(request.getContextPath() + "/department-manage?error=duplicate_name");
//                return;
//            }
//
//            Integer leaderEmpId = null;
//            if (managerIdStr != null && !managerIdStr.trim().isEmpty()) {
//                try {
//                    leaderEmpId = Integer.parseInt(managerIdStr.trim());
//                } catch (NumberFormatException ignored) {}
//            }
//
//            int newDeptId = departmentDAO.createDepartmentWithLeader(name, desc, leaderEmpId);
//            if (newDeptId > 0) {
//                // Đồng bộ ngay sang session của LeaderRoom để khi chuyển qua Quản lý phòng thấy phòng mới ngay
//                session.setAttribute("currentViewDeptId", newDeptId);
//                response.sendRedirect(request.getContextPath() + "/department-manage?status=created");
//            } else {
//                response.sendRedirect(request.getContextPath() + "/department-manage?error=create_failed");
//            }
//            return;
//        } else if ("assignMember".equals(action)) {
//            try {
//                int employeeId = Integer.parseInt(request.getParameter("employeeId"));
//                String departmentName = request.getParameter("departmentName");
//                String position = request.getParameter("position");
//                String note = request.getParameter("note");
//
//                departmentDAO.assignEmployeeToDepartment(employeeId, departmentName, position, note);
//
//                String redirectUrl = request.getContextPath() + "/department-manage?status=assigned";
//                if (departmentName != null && !departmentName.isEmpty()) {
//                    redirectUrl += "&dept=" + URLEncoder.encode(departmentName, StandardCharsets.UTF_8);
//                }
//                response.sendRedirect(redirectUrl);
//                return;
//            } catch (Exception e) {
//                response.sendRedirect(request.getContextPath() + "/department-manage?error=invalid_data");
//                return;
//            }
//        }
//
//        response.sendRedirect(request.getContextPath() + "/department-manage?status=success");
//    }
//}


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.Profile;

import dal.DepartmentDAO;
import dal.StaffDAO; // Có sẵn trong dự án để lấy list nhân viên
import model.DepartmentModel;
import model.StaffModel;
import tool.SessionUtil; // Có sẵn trong dự án

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

@WebServlet(name = "DepartmentManageServlet", urlPatterns = {"/department-manage"})
public class DepartmentManageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        // 1. Phân quyền Manager: Kiểm tra session đăng nhập (giữ nguyên logic gốc)
        String role = (String) session.getAttribute("userRole"); // Hoặc dùng hàm check trong SessionUtil
        if (role == null || (!role.equalsIgnoreCase("Manager") && !role.equalsIgnoreCase("Admin"))) {
            // Hỗ trợ kiểm tra dự phòng nếu hệ thống lưu role qua SessionUtil
            if (!SessionUtil.isHrOrManager(request)) {
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
                return;
            }
        }

        // BỔ SUNG: Nếu người dùng chọn 1 phòng ban từ dropdown -> chuyển hướng sang trang chi tiết phòng ban đó
        String selectedDept = request.getParameter("dept");
        if (selectedDept != null && !selectedDept.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/department-detail?name=" 
                    + URLEncoder.encode(selectedDept.trim(), StandardCharsets.UTF_8));
            return;
        }

        DepartmentDAO departmentDAO = new DepartmentDAO();

        // 2. Lấy dữ liệu danh sách phòng ban kèm số lượng nhân sự
        List<DepartmentModel> departments = departmentDAO.getAllDepartmentsWithCount();
        if (departments == null || departments.isEmpty()) {
            departments = departmentDAO.getAllDepartments();
        }
        request.setAttribute("departments", departments);
        request.setAttribute("selectedDept", selectedDept);

        // Lấy danh sách nhân sự trực thuộc phòng ban đang chọn (hoặc tất cả nếu chưa chọn)
        List employees = departmentDAO.getEmployeesByDepartment(selectedDept);
        request.setAttribute("employees", employees);

        // Tính tổng số nhân sự đang hoạt động
        int totalEmployees = 0;
        if (departments != null) {
            for (DepartmentModel d : departments) {
                totalEmployees += d.getEmployeeCount();
            }
        }
        request.setAttribute("totalEmployees", totalEmployees);

        // Lấy danh sách nhân viên từ StaffDAO để phục vụ form chọn nhân sự cần phân công
        StaffDAO staffDAO = new StaffDAO();
        List<StaffModel> staffList = staffDAO.getActiveStaff();
        request.setAttribute("staffList", staffList);

        // Chuyển tiếp tới giao diện JSP
        request.getRequestDispatcher("/profile/department_manage.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        String role = (String) session.getAttribute("userRole");
        if (role == null || (!role.equalsIgnoreCase("Manager") && !role.equalsIgnoreCase("Admin"))) {
            if (!SessionUtil.isHrOrManager(request)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        DepartmentDAO departmentDAO = new DepartmentDAO();
        String action = request.getParameter("action");
        if ("createDepartment".equals(action)) {
            String name = request.getParameter("departmentName");
            String desc = request.getParameter("description");
            String managerIdStr = request.getParameter("managerId");

            if (name == null || name.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/department-manage?error=empty_name");
                return;
            }
            if (departmentDAO.isDepartmentNameExists(name)) {
                response.sendRedirect(request.getContextPath() + "/department-manage?error=duplicate_name");
                return;
            }

            Integer leaderEmpId = null;
            if (managerIdStr != null && !managerIdStr.trim().isEmpty()) {
                try {
                    leaderEmpId = Integer.parseInt(managerIdStr.trim());
                } catch (NumberFormatException ignored) {}
            }

            int newDeptId = departmentDAO.createDepartmentWithLeader(name, desc, leaderEmpId);
            if (newDeptId > 0) {
                // Đồng bộ ngay sang session của LeaderRoom để khi chuyển qua Quản lý phòng thấy phòng mới ngay
                session.setAttribute("currentViewDeptId", newDeptId);
                response.sendRedirect(request.getContextPath() + "/department-manage?status=created");
            } else {
                response.sendRedirect(request.getContextPath() + "/department-manage?error=create_failed");
            }
            return;
        } else if ("assignMember".equals(action)) {
            try {
                int employeeId = Integer.parseInt(request.getParameter("employeeId"));
                String departmentName = request.getParameter("departmentName");
                String position = request.getParameter("position");
                String note = request.getParameter("note");

                departmentDAO.assignEmployeeToDepartment(employeeId, departmentName, position, note);

                String redirectUrl = request.getContextPath() + "/department-manage?status=assigned";
                if (departmentName != null && !departmentName.isEmpty()) {
                    redirectUrl += "&dept=" + URLEncoder.encode(departmentName, StandardCharsets.UTF_8);
                }
                response.sendRedirect(redirectUrl);
                return;
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/department-manage?error=invalid_data");
                return;
            }
        } else if ("deleteDepartment".equals(action)) {
            try {
                String deptIdStr = request.getParameter("departmentId");
                if (deptIdStr == null || deptIdStr.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/department-manage?error=missing_dept");
                    return;
                }

                int deptId = Integer.parseInt(deptIdStr.trim());
                int empCount = departmentDAO.countEmployeesInDepartment(deptId);

                if (empCount > 0) {
                    response.sendRedirect(request.getContextPath() + "/department-manage?error=has_employees");
                    return;
                }
                if (empCount < 0) {
                    response.sendRedirect(request.getContextPath() + "/department-manage?error=delete_failed");
                    return;
                }

                boolean deleted = departmentDAO.deleteDepartmentIfEmpty(deptId);
                if (deleted) {
                    Object curViewId = session.getAttribute("currentViewDeptId");
                    if (curViewId != null && curViewId.toString().equals(String.valueOf(deptId))) {
                        session.removeAttribute("currentViewDeptId");
                    }
                    response.sendRedirect(request.getContextPath() + "/department-manage?status=deleted");
                } else {
                    response.sendRedirect(request.getContextPath() + "/department-manage?error=has_employees");
                }
                return;
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/department-manage?error=delete_failed");
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/department-manage?status=success");
    }
}