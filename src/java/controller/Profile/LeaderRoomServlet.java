//package controller.Profile;
//
//import dal.DepartmentDAO;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import model.DepartmentModel;
//import model.DepartmentTaskModel;
//import model.DepartmentTopicModel;
//
//@WebServlet(name = "LeaderRoomServlet", urlPatterns = {"/leader-room", "/leader/room"})
//public class LeaderRoomServlet extends HttpServlet {
//
//    private final DepartmentDAO dao = new DepartmentDAO();
//
//    private Integer getCurrentEmployeeId(HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//        if (session == null) return null;
//        Object empIdObj = session.getAttribute("employeeId");
//        if (empIdObj == null) empIdObj = session.getAttribute("staffId");
//        if (empIdObj == null) empIdObj = session.getAttribute("userId");
//        if (empIdObj == null) empIdObj = session.getAttribute("candidateId");
//        
//        if (empIdObj instanceof Integer) return (Integer) empIdObj;
//        if (empIdObj instanceof String) {
//            try { return Integer.parseInt((String) empIdObj); } catch (Exception ignored) {}
//        }
//        return null;
//    }
//
//    // THÊM MỚI: Kiểm tra xem tài khoản có quyền Admin hoặc Manager không
//    private boolean isAdminOrManager(HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//        if (session == null) return false;
//        String roleName = (String) session.getAttribute("roleName");
//        String userRole = (String) session.getAttribute("userRole");
//        return "Manager".equalsIgnoreCase(roleName) || "Admin".equalsIgnoreCase(roleName)
//                || "Manager".equalsIgnoreCase(userRole) || "Admin".equalsIgnoreCase(userRole);
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        Integer empId = getCurrentEmployeeId(request);
//        if (empId == null) {
//            response.sendRedirect(request.getContextPath() + "/login.jsp");
//            return;
//        }
//
//        // THÊM MỚI: Lấy danh sách toàn bộ phòng ban và cờ quyền quản trị
//        boolean isManager = isAdminOrManager(request);
//        request.setAttribute("isManagerRole", isManager);
//        List allDepts = dao.getAllDepartmentsWithCount();
//        request.setAttribute("allDepartments", allDepts);
//
//        DepartmentModel leaderDept = null;
//        String reqDeptId = request.getParameter("deptId");
//
//        // THÊM MỚI: Nếu là Admin/Manager và có truyền tham số deptId trên URL thì ưu tiên lấy phòng đó
//        if (isManager && reqDeptId != null && !reqDeptId.trim().isEmpty()) {
//            try {
//                int dId = Integer.parseInt(reqDeptId.trim());
//                if (allDepts != null) {
//                    for (Object obj : allDepts) {
//                        DepartmentModel dm = (DepartmentModel) obj;
//                        if (dm.getDepartmentId() == dId) {
//                            leaderDept = dm;
//                            break;
//                        }
//                    }
//                }
//            } catch (Exception ignored) {}
//        }
//
//        // Nếu không có deptId hoặc là nhân viên thông thường: lấy phòng ban mà người này làm Leader
//        if (leaderDept == null) {
//            leaderDept = dao.getDepartmentByLeaderId(empId);
//        }
//
//        // THÊM MỚI: Nếu là Admin/Manager nhưng bản thân chưa được gán làm Leader phòng nào, mặc định hiển thị phòng đầu tiên
//        if (leaderDept == null && isManager && allDepts != null && !allDepts.isEmpty()) {
//            leaderDept = (DepartmentModel) allDepts.get(0);
//        }
//
//        if (leaderDept == null) {
//            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn chưa được bổ nhiệm làm Trưởng nhóm phòng ban nào.");
//            return;
//        }
//
//        request.setAttribute("leaderDept", leaderDept);
//        String tab = request.getParameter("tab");
//        if (tab == null || tab.isEmpty()) tab = "members";
//        request.setAttribute("activeTab", tab);
//
//        if ("members".equals(tab)) {
//            // Màn hình 1: Quản lý nhân viên (Giao diện y hệt ảnh 1)
//            List members = dao.getEmployeesByDepartment(leaderDept.getDepartmentName());
//            request.setAttribute("members", members);
//            request.getRequestDispatcher("/profile/leader_members.jsp").forward(request, response);
//        } else if ("topics".equals(tab)) {
//            // Màn hình 2: Chủ đề + Phân công
//            List<DepartmentTopicModel> topics = dao.getTopicsByDepartment(leaderDept.getDepartmentId());
//            List members = dao.getEmployeesByDepartment(leaderDept.getDepartmentName());
//            request.setAttribute("topics", topics);
//            request.setAttribute("members", members);
//
//            String empParam = request.getParameter("empId");
//            if (empParam != null && !empParam.isEmpty()) {
//                int targetEmpId = Integer.parseInt(empParam);
//                request.setAttribute("targetEmp", dao.getEmployeeDetailById(targetEmpId));
//                request.setAttribute("taskList", dao.getTasksByEmployee(targetEmpId));
//                request.getRequestDispatcher("/profile/leader_assign_task.jsp").forward(request, response);
//                return;
//            }
//
//            request.getRequestDispatcher("/profile/leader_topics.jsp").forward(request, response);
//        } else if ("progress".equals(tab)) {
//            // Màn hình 3: Tổng kết tiến độ
//            List<DepartmentTaskModel> allTasks = dao.getAllDepartmentTasks(leaderDept.getDepartmentId());
//            request.setAttribute("allTasks", allTasks);
//
//            // Thống kê chuẩn xác tiến độ toàn phòng
//            int total = (allTasks != null) ? allTasks.size() : 0;
//            int completed = 0;
//            int totalPercent = 0;
//
//            if (allTasks != null) {
//                for (DepartmentTaskModel t : allTasks) {
//                    boolean isDone = t.isCompleted() || t.getProgress() >= 100;
//                    if (isDone) {
//                        completed++;
//                    }
//                    // Nếu đã hoàn thành tính 100%, nếu đang làm thì cộng % tiến độ hiện tại
//                    totalPercent += (isDone ? 100 : t.getProgress());
//                }
//            }
//
//            // Tính % tiến độ trung bình toàn phòng làm tròn chuẩn
//            int avgPercent = total > 0 ? Math.round((float) totalPercent / total) : 0;
//
//            request.setAttribute("totalTasks", total);
//            request.setAttribute("completedTasks", completed);
//            request.setAttribute("pendingTasks", total - completed);
//            request.setAttribute("avgPercent", avgPercent);
//
//            request.getRequestDispatcher("/profile/leader_progress.jsp").forward(request, response);
//        }
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        request.setCharacterEncoding("UTF-8");
//        Integer empId = getCurrentEmployeeId(request);
//        if (empId == null) {
//            response.sendRedirect(request.getContextPath() + "/login.jsp");
//            return;
//        }
//
//        // THÊM MỚI: Xác định đúng phòng ban đang thao tác (hỗ trợ cả Admin chọn phòng qua deptId)
//        boolean isManager = isAdminOrManager(request);
//        String reqDeptId = request.getParameter("deptId");
//        DepartmentModel leaderDept = null;
//        if (isManager && reqDeptId != null && !reqDeptId.trim().isEmpty()) {
//            try {
//                int dId = Integer.parseInt(reqDeptId.trim());
//                List allDepts = dao.getAllDepartmentsWithCount();
//                if (allDepts != null) {
//                    for (Object obj : allDepts) {
//                        DepartmentModel dm = (DepartmentModel) obj;
//                        if (dm.getDepartmentId() == dId) {
//                            leaderDept = dm;
//                            break;
//                        }
//                    }
//                }
//            } catch (Exception ignored) {}
//        }
//        if (leaderDept == null) {
//            leaderDept = dao.getDepartmentByLeaderId(empId);
//        }
//
//        if (leaderDept == null) {
//            response.sendError(HttpServletResponse.SC_FORBIDDEN);
//            return;
//        }
//
//        // Chuỗi query param lưu lại phòng ban hiện tại khi redirect
//        String deptParamUrl = "&deptId=" + leaderDept.getDepartmentId();
//
//        String action = request.getParameter("action");
//        if ("createTopic".equals(action)) {
//            String title = request.getParameter("topicTitle");
//            String desc = request.getParameter("description");
//            dao.addTopic(leaderDept.getDepartmentId(), title, desc, empId);
//            // THÊM: Gửi param msg=topic_created thông báo tạo chủ đề thành công
//            response.sendRedirect(request.getContextPath() + "/leader-room?tab=topics" + deptParamUrl + "&msg=topic_created");
//        } else if ("assignTask".equals(action)) {
//            int topicId = Integer.parseInt(request.getParameter("topicId"));
//            int targetEmpId = Integer.parseInt(request.getParameter("employeeId"));
//            String content = request.getParameter("taskContent");
//            String deadline = request.getParameter("deadline");
//            int progress = 0;
//            try {
//                progress = Integer.parseInt(request.getParameter("progress"));
//            } catch (Exception ignored) {}
//            String note = request.getParameter("leaderNote");
//            boolean completed = "on".equals(request.getParameter("isCompleted")) 
//                             || "true".equals(request.getParameter("isCompleted"))
//                             || progress >= 100;
//
//            dao.addTask(topicId, targetEmpId, content, deadline, progress, note, completed);
//            // THÊM: Gửi param msg=assigned để leader_assign_task.jsp hiện banner thông báo xanh
//            response.sendRedirect(request.getContextPath() + "/leader-room?tab=topics&empId=" + targetEmpId + deptParamUrl + "&msg=assigned");
//        } else if ("updateTask".equals(action)) {
//            int taskId = Integer.parseInt(request.getParameter("taskId"));
//            int targetEmpId = Integer.parseInt(request.getParameter("employeeId"));
//            int progress = 0;
//            try {
//                progress = Integer.parseInt(request.getParameter("progress"));
//            } catch (Exception ignored) {}
//            String note = request.getParameter("leaderNote");
//            boolean completed = "on".equals(request.getParameter("isCompleted")) 
//                             || "true".equals(request.getParameter("isCompleted"))
//                             || progress >= 100;
//
//            dao.updateTaskStatus(taskId, progress, note, completed);
//            // THÊM: Gửi param msg=updated để leader_assign_task.jsp hiện banner thông báo cập nhật thành công
//            response.sendRedirect(request.getContextPath() + "/leader-room?tab=topics&empId=" + targetEmpId + deptParamUrl + "&msg=updated");
//        }
//    }
//}



package controller.Profile;

import dal.DepartmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import model.DepartmentModel;
import model.DepartmentTaskModel;
import model.DepartmentTopicModel;

@WebServlet(name = "LeaderRoomServlet", urlPatterns = {"/leader-room", "/leader/room"})
public class LeaderRoomServlet extends HttpServlet {

    private final DepartmentDAO dao = new DepartmentDAO();

    private Integer getCurrentEmployeeId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object empIdObj = session.getAttribute("employeeId");
        if (empIdObj == null) empIdObj = session.getAttribute("staffId");
        if (empIdObj == null) empIdObj = session.getAttribute("userId");
        if (empIdObj == null) empIdObj = session.getAttribute("candidateId");
        
        if (empIdObj instanceof Integer) return (Integer) empIdObj;
        if (empIdObj instanceof String) {
            try { return Integer.parseInt((String) empIdObj); } catch (Exception ignored) {}
        }
        return null;
    }

    // THÊM MỚI: Kiểm tra xem tài khoản có quyền Admin hoặc Manager không
    private boolean isAdminOrManager(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        String roleName = (String) session.getAttribute("roleName");
        String userRole = (String) session.getAttribute("userRole");
        return "Manager".equalsIgnoreCase(roleName) || "Admin".equalsIgnoreCase(roleName)
                || "Manager".equalsIgnoreCase(userRole) || "Admin".equalsIgnoreCase(userRole);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer empId = getCurrentEmployeeId(request);
        if (empId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Lấy danh sách toàn bộ phòng ban và cờ quyền quản trị
        boolean isManager = isAdminOrManager(request);
        request.setAttribute("isManagerRole", isManager);
        List allDepts = dao.getAllDepartmentsWithCount();
        request.setAttribute("allDepartments", allDepts);

        DepartmentModel leaderDept = null;
        String reqDeptId = request.getParameter("deptId");

        // THÊM MỚI 1: Bất kỳ ai (Manager hoặc Leader) nếu có deptId trên URL thì ưu tiên lấy đúng phòng đó
        if (reqDeptId != null && !reqDeptId.trim().isEmpty()) {
            try {
                int dId = Integer.parseInt(reqDeptId.trim());
                if (allDepts != null) {
                    for (Object obj : allDepts) {
                        DepartmentModel dm = (DepartmentModel) obj;
                        if (dm.getDepartmentId() == dId) {
                            leaderDept = dm;
                            request.getSession().setAttribute("currentViewDeptId", dId);
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        // THÊM MỚI 2: Nếu URL không có deptId, kiểm tra Session trước đó đã từng chọn xem phòng nào không
        if (leaderDept == null) {
            Object sessDeptId = request.getSession().getAttribute("currentViewDeptId");
            if (sessDeptId != null) {
                try {
                    int sId = Integer.parseInt(sessDeptId.toString());
                    if (allDepts != null) {
                        for (Object obj : allDepts) {
                            DepartmentModel dm = (DepartmentModel) obj;
                            if (dm.getDepartmentId() == sId) {
                                leaderDept = dm;
                                break;
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }
        }

        // 3. Nếu vẫn chưa có: lấy phòng ban mà người này làm Leader qua DAO
        if (leaderDept == null) {
            leaderDept = dao.getDepartmentByLeaderId(empId);
        }

        // THÊM MỚI 3: Nếu getDepartmentByLeaderId vẫn null, tìm theo cột Department của Employee
        if (leaderDept == null) {
            try {
                Map<String, Object> empInfo = dao.getEmployeeDetailById(empId);
                if (empInfo != null && empInfo.get("department") != null) {
                    String dName = empInfo.get("department").toString();
                    leaderDept = dao.getDepartmentByName(dName);
                }
            } catch (Exception ignored) {}
        }

        // 4. Nếu là Admin/Manager nhưng bản thân chưa được gán phòng nào, mặc định phòng đầu tiên
        if (leaderDept == null && isManager && allDepts != null && !allDepts.isEmpty()) {
            leaderDept = (DepartmentModel) allDepts.get(0);
        }

        if (leaderDept == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn chưa được bổ nhiệm làm Trưởng nhóm phòng ban nào.");
            return;
        }

        // Lưu lại deptId chuẩn vào session để các tab khác tự nhớ
        request.getSession().setAttribute("currentViewDeptId", leaderDept.getDepartmentId());
        request.setAttribute("leaderDept", leaderDept);

        String tab = request.getParameter("tab");
        if (tab == null || tab.isEmpty()) tab = "members";
        request.setAttribute("activeTab", tab);

        if ("members".equals(tab)) {
            // Màn hình 1: Quản lý nhân viên (Giao diện y hệt ảnh 1)
            List members = dao.getEmployeesByDepartment(leaderDept.getDepartmentName());
            request.setAttribute("members", members);
            request.getRequestDispatcher("/profile/leader_members.jsp").forward(request, response);
        } else if ("topics".equals(tab)) {
            // Màn hình 2: Chủ đề + Phân công
            List<DepartmentTopicModel> topics = dao.getTopicsByDepartment(leaderDept.getDepartmentId());
            List members = dao.getEmployeesByDepartment(leaderDept.getDepartmentName());
            request.setAttribute("topics", topics);
            request.setAttribute("members", members);

            String empParam = request.getParameter("empId");
            if (empParam != null && !empParam.isEmpty()) {
                int targetEmpId = Integer.parseInt(empParam);
                request.setAttribute("targetEmp", dao.getEmployeeDetailById(targetEmpId));
                request.setAttribute("taskList", dao.getTasksByEmployee(targetEmpId));
                request.getRequestDispatcher("/profile/leader_assign_task.jsp").forward(request, response);
                return;
            }

            request.getRequestDispatcher("/profile/leader_topics.jsp").forward(request, response);
        } else if ("progress".equals(tab)) {
            // Màn hình 3: Tổng kết tiến độ
            System.out.println("[LeaderRoomServlet] Querying all tasks for Dept: " + leaderDept.getDepartmentName() + " (ID=" + leaderDept.getDepartmentId() + ")");
            List<DepartmentTaskModel> allTasks = dao.getAllDepartmentTasks(leaderDept.getDepartmentId());
            request.setAttribute("allTasks", allTasks);

            // Thống kê chuẩn xác tiến độ toàn phòng
            int total = (allTasks != null) ? allTasks.size() : 0;
            int completed = 0;
            int totalPercent = 0;

            if (allTasks != null) {
                for (DepartmentTaskModel t : allTasks) {
                    boolean isDone = t.isCompleted() || t.getProgress() >= 100;
                    if (isDone) {
                        completed++;
                    }
                    // Nếu đã hoàn thành tính 100%, nếu đang làm thì cộng % tiến độ hiện tại
                    totalPercent += (isDone ? 100 : t.getProgress());
                }
            }

            // Tính % tiến độ trung bình toàn phòng làm tròn chuẩn
            int avgPercent = total > 0 ? Math.round((float) totalPercent / total) : 0;

            request.setAttribute("totalTasks", total);
            request.setAttribute("completedTasks", completed);
            request.setAttribute("pendingTasks", total - completed);
            request.setAttribute("avgPercent", avgPercent);

            request.getRequestDispatcher("/profile/leader_progress.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Integer empId = getCurrentEmployeeId(request);
        if (empId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Xác định đúng phòng ban đang thao tác (hỗ trợ đọc deptId từ form submit)
        boolean isManager = isAdminOrManager(request);
        String reqDeptId = request.getParameter("deptId");
        DepartmentModel leaderDept = null;
        
        // THÊM MỚI: Đọc deptId submit từ form cho cả Leader và Manager
        if (reqDeptId != null && !reqDeptId.trim().isEmpty()) {
            try {
                int dId = Integer.parseInt(reqDeptId.trim());
                List allDepts = dao.getAllDepartmentsWithCount();
                if (allDepts != null) {
                    for (Object obj : allDepts) {
                        DepartmentModel dm = (DepartmentModel) obj;
                        if (dm.getDepartmentId() == dId) {
                            leaderDept = dm;
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        
        if (leaderDept == null) {
            leaderDept = dao.getDepartmentByLeaderId(empId);
        }

        if (leaderDept == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Chuỗi query param lưu lại phòng ban hiện tại khi redirect
        String deptParamUrl = "&deptId=" + leaderDept.getDepartmentId();

        String action = request.getParameter("action");
        if ("createTopic".equals(action)) {
            String title = request.getParameter("topicTitle");
            String desc = request.getParameter("description");
            dao.addTopic(leaderDept.getDepartmentId(), title, desc, empId);
            response.sendRedirect(request.getContextPath() + "/leader-room?tab=topics" + deptParamUrl + "&msg=topic_created");
        } else if ("assignTask".equals(action)) {
            int topicId = Integer.parseInt(request.getParameter("topicId"));
            int targetEmpId = Integer.parseInt(request.getParameter("employeeId"));
            String content = request.getParameter("taskContent");
            String deadline = request.getParameter("deadline");
            int progress = 0;
            try {
                progress = Integer.parseInt(request.getParameter("progress"));
            } catch (Exception ignored) {}
            String note = request.getParameter("leaderNote");
            boolean completed = "on".equals(request.getParameter("isCompleted")) 
                             || "true".equals(request.getParameter("isCompleted"))
                             || progress >= 100;

            dao.addTask(topicId, targetEmpId, content, deadline, progress, note, completed);
            response.sendRedirect(request.getContextPath() + "/leader-room?tab=topics&empId=" + targetEmpId + deptParamUrl + "&msg=assigned");
        } else if ("updateTask".equals(action)) {
            int taskId = Integer.parseInt(request.getParameter("taskId"));
            int targetEmpId = Integer.parseInt(request.getParameter("employeeId"));
            int progress = 0;
            try {
                progress = Integer.parseInt(request.getParameter("progress"));
            } catch (Exception ignored) {}
            String note = request.getParameter("leaderNote");
            boolean completed = "on".equals(request.getParameter("isCompleted")) 
                             || "true".equals(request.getParameter("isCompleted"))
                             || progress >= 100;

            dao.updateTaskStatus(taskId, progress, note, completed);
            response.sendRedirect(request.getContextPath() + "/leader-room?tab=topics&empId=" + targetEmpId + deptParamUrl + "&msg=updated");
        }
    }
}