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
//import java.util.Map;
//
//@WebServlet(name = "DepartmentManageServlet", urlPatterns = {"/department-manage"})
//public class DepartmentManageServlet extends HttpServlet {
//
//    private final DepartmentDAO departmentDAO = new DepartmentDAO();
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
//        // 2. Lấy dữ liệu danh sách phòng ban kèm số lượng nhân sự
//        List<DepartmentModel> departments = departmentDAO.getAllDepartmentsWithCount();
//        if (departments == null || departments.isEmpty()) {
//            departments = departmentDAO.getAllDepartments();
//        }
//        request.setAttribute("departments", departments);
//
//        // THÊM MỚI: Đón nhận tham số lọc phòng ban từ dropdown icon "⌄"
//        String selectedDept = request.getParameter("dept");
//        if (selectedDept != null) {
//            selectedDept = selectedDept.trim();
//        }
//        request.setAttribute("selectedDept", selectedDept);
//
//        // THÊM MỚI: Lấy danh sách nhân sự trực thuộc phòng ban đang chọn (hoặc tất cả nếu chưa chọn)
//        List<Map<String, Object>> employees = departmentDAO.getEmployeesByDepartment(selectedDept);
//        request.setAttribute("employees", employees);
//
//        // THÊM MỚI: Tính tổng số nhân sự đang hoạt động
//        int totalEmployees = 0;
//        if (departments != null) {
//            for (DepartmentModel d : departments) {
//                totalEmployees += d.getEmployeeCount();
//            }
//        }
//        request.setAttribute("totalEmployees", totalEmployees);
//
//        // THÊM MỚI: Lấy danh sách nhân viên từ StaffDAO để phục vụ form chọn nhân sự cần phân công
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
//        String action = request.getParameter("action");
//        if ("createDepartment".equals(action)) {
//            String name = request.getParameter("departmentName");
//            String desc = request.getParameter("description");
//            departmentDAO.addDepartment(name, desc, null);
//            response.sendRedirect(request.getContextPath() + "/profile/department-manage?status=created");
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
//                String redirectUrl = request.getContextPath() + "/profile/department-manage?status=assigned";
//                if (departmentName != null && !departmentName.isEmpty()) {
//                    redirectUrl += "&dept=" + URLEncoder.encode(departmentName, StandardCharsets.UTF_8);
//                }
//                response.sendRedirect(redirectUrl);
//                return;
//            } catch (Exception e) {
//                response.sendRedirect(request.getContextPath() + "/profile/department-manage?error=invalid_data");
//                return;
//            }
//        }
//
//        response.sendRedirect(request.getContextPath() + "/profile/department-manage?status=success");
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
import java.util.Map;

@WebServlet(name = "DepartmentManageServlet", urlPatterns = {"/department-manage"})
public class DepartmentManageServlet extends HttpServlet {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();

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

        // 2. Lấy dữ liệu danh sách phòng ban kèm số lượng nhân sự
        List<DepartmentModel> departments = departmentDAO.getAllDepartmentsWithCount();
        if (departments == null || departments.isEmpty()) {
            departments = departmentDAO.getAllDepartments();
        }
        request.setAttribute("departments", departments);
        request.setAttribute("selectedDept", selectedDept);

        // Lấy danh sách nhân sự trực thuộc phòng ban đang chọn (hoặc tất cả nếu chưa chọn)
        List<Map<String, Object>> employees = departmentDAO.getEmployeesByDepartment(selectedDept);
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

        String action = request.getParameter("action");
        if ("createDepartment".equals(action)) {
            String name = request.getParameter("departmentName");
            String desc = request.getParameter("description");
            departmentDAO.addDepartment(name, desc, null);
            response.sendRedirect(request.getContextPath() + "/department-manage?status=created");
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
        }

        response.sendRedirect(request.getContextPath() + "/department-manage?status=success");
    }
}