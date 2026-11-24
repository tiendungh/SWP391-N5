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
import model.DepartmentArchiveModel;
import model.DepartmentModel;
import model.DepartmentTaskModel;
import model.DepartmentTopicModel;

@WebServlet(name = "LeaderRoomServlet", urlPatterns = {"/leader-room", "/leader/room"})
public class LeaderRoomServlet extends HttpServlet {

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

    private DepartmentModel findDepartment(List allDepts, int departmentId) {
        if (allDepts == null) return null;
        for (Object obj : allDepts) {
            DepartmentModel dm = (DepartmentModel) obj;
            if (dm.getDepartmentId() == departmentId) return dm;
        }
        return null;
    }

    private DepartmentModel resolveOwnDepartment(DepartmentDAO dao, Integer empId) {
        if (empId == null) return null;
        try {
            DepartmentModel led = dao.getDepartmentByLeaderId(empId);
            if (led != null) return led;
            Map empInfo = dao.getEmployeeDetailById(empId);
            if (empInfo != null && empInfo.get("department") != null) {
                return dao.getDepartmentByName(empInfo.get("department").toString());
            }
        } catch (Exception ignored) {}
        return null;
    }

    // Admin/Manager xem mọi phòng. Người khác chỉ xem phòng mình làm leader hoặc phòng mình thuộc về.
    private boolean canAccessDepartment(boolean isManager, Integer empId, DepartmentModel ownDept, DepartmentModel target) {
        if (target == null) return false;
        if (isManager) return true;
        if (empId != null && target.getManagerId() != null && target.getManagerId().equals(empId)) return true;
        return ownDept != null && ownDept.getDepartmentId() == target.getDepartmentId();
    }

    // Kiểm tra xem tài khoản có quyền Admin hoặc Manager không
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

        DepartmentDAO dao = new DepartmentDAO();
        try {
            // Lấy danh sách toàn bộ phòng ban và cờ quyền quản trị
            boolean isManager = isAdminOrManager(request);
            request.setAttribute("isManagerRole", isManager);
            List allDepts = dao.getAllDepartmentsWithCount();
            request.setAttribute("allDepartments", allDepts);

            DepartmentModel ownDept = resolveOwnDepartment(dao, empId);
            DepartmentModel leaderDept = null;
            String reqDeptId = request.getParameter("deptId");
            boolean deptRequested = reqDeptId != null && !reqDeptId.trim().isEmpty();

            // 1. deptId trên URL chỉ được tin nếu người này được xem phòng đó
            if (deptRequested) {
                try {
                    int dId = Integer.parseInt(reqDeptId.trim());
                    DepartmentModel requested = findDepartment(allDepts, dId);
                    if (requested != null && canAccessDepartment(isManager, empId, ownDept, requested)) {
                        leaderDept = requested;
                    } else if (!isManager) {
                        request.getRequestDispatcher("/profile/leader_no_permission.jsp").forward(request, response);
                        return;
                    }
                } catch (Exception ignored) {
                    if (!isManager) {
                        request.getRequestDispatcher("/profile/leader_no_permission.jsp").forward(request, response);
                        return;
                    }
                }
            }

            // 2. Session chỉ nhớ phòng đã được phép xem, không phải phòng gõ tay trên URL
            if (leaderDept == null && !deptRequested) {
                Object sessDeptId = request.getSession().getAttribute("currentViewDeptId");
                if (sessDeptId != null) {
                    try {
                        int sId = Integer.parseInt(sessDeptId.toString());
                        DepartmentModel remembered = findDepartment(allDepts, sId);
                        if (remembered != null && canAccessDepartment(isManager, empId, ownDept, remembered)) {
                            leaderDept = remembered;
                        }
                    } catch (Exception ignored) {}
                }
            }

            // 3. Mặc định: phòng mình làm leader, rồi phòng mình thuộc về
            if (leaderDept == null) {
                leaderDept = dao.getDepartmentByLeaderId(empId);
            }
            if (leaderDept == null) {
                leaderDept = ownDept;
            }

            // 4. Admin/Manager chưa gán phòng thì mở phòng đầu tiên
            if (leaderDept == null && isManager && allDepts != null && !allDepts.isEmpty()) {
                leaderDept = (DepartmentModel) allDepts.get(0);
            }

            if (leaderDept == null || !canAccessDepartment(isManager, empId, ownDept, leaderDept)) {
                request.getRequestDispatcher("/profile/leader_no_permission.jsp").forward(request, response);
                return;
            }

            request.getSession().setAttribute("currentViewDeptId", leaderDept.getDepartmentId());
            request.setAttribute("leaderDept", leaderDept);

            // Quyền chỉnh sửa chỉ dành cho Admin, Manager hoặc Trưởng phòng thực tế của đúng phòng đang xem
            boolean canEdit = isManager || (leaderDept.getManagerId() != null && leaderDept.getManagerId().equals(empId));
            request.setAttribute("canEdit", canEdit);

            String tab = request.getParameter("tab");
            // Nếu không truyền tab: Trưởng phòng/Admin vào 'members', Employee bình thường tự động vào xem 'progress'
            if (tab == null || tab.isEmpty()) {
                tab = canEdit ? "members" : "progress";
            }
            request.setAttribute("activeTab", tab);

            if ("members".equals(tab)) {
                // THÊM MỚI: Chặn Employee thường truy cập tab Quản lý nhân viên -> hiện giao diện thông báo quyền
                if (!canEdit) {
                    request.getRequestDispatcher("/profile/leader_no_permission.jsp").forward(request, response);
                    return;
                }
                // Màn hình 1: Quản lý nhân viên
                List members = dao.getEmployeesByDepartment(leaderDept.getDepartmentName());
                request.setAttribute("members", members);
                request.getRequestDispatcher("/profile/leader_members.jsp").forward(request, response);
            } else if ("topics".equals(tab)) {
                // THÊM MỚI: Chặn Employee thường truy cập tab Chủ đề + Phân công -> hiện giao diện thông báo quyền
                if (!canEdit) {
                    request.getRequestDispatcher("/profile/leader_no_permission.jsp").forward(request, response);
                    return;
                }
                // Màn hình 2: Chủ đề + Phân công
                List topics = dao.getTopicsByDepartment(leaderDept.getDepartmentId());
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
                // Chỉ xem tiến độ phòng của mình. Admin/Manager và leader của phòng đó vẫn xem được.
                if (!canAccessDepartment(isManager, empId, ownDept, leaderDept)) {
                    request.getRequestDispatcher("/profile/leader_no_permission.jsp").forward(request, response);
                    return;
                }
                List allTasks = dao.getAllDepartmentTasks(leaderDept.getDepartmentId());
                request.setAttribute("allTasks", allTasks);

                int total = (allTasks != null) ? allTasks.size() : 0;
                int completed = 0;
                int totalPercent = 0;

                if (allTasks != null) {
                    for (Object obj : allTasks) {
                        DepartmentTaskModel t = (DepartmentTaskModel) obj;
                        int p = t.getProgress();
                        if (p < 0) p = 0;
                        if (p > 100) p = 100;
                        totalPercent += p;
                        if (p >= 100) {
                            completed++;
                        }
                    }
                }

                int avgPercent = total > 0 ? Math.round((float) totalPercent / total) : 0;

                request.setAttribute("totalTasks", total);
                request.setAttribute("completedTasks", completed);
                request.setAttribute("pendingTasks", total - completed);
                request.setAttribute("avgPercent", avgPercent);

                request.getRequestDispatcher("/profile/leader_progress.jsp").forward(request, response);
            } else if ("archive".equals(tab)) {
                // Màn hình Kho lưu trữ: Nếu không có quyền -> hiện giao diện "Bạn không có thẩm quyền thao tác"
                if (!canEdit) {
                    request.getRequestDispatcher("/profile/leader_no_permission.jsp").forward(request, response);
                    return;
                }

                String archiveIdParam = request.getParameter("archiveId");
                if (archiveIdParam != null && !archiveIdParam.trim().isEmpty()) {
                    try {
                        int archId = Integer.parseInt(archiveIdParam.trim());
                        DepartmentArchiveModel archiveDetail = dao.getArchiveDetailById(archId);
                        if (!isManager && archiveDetail != null && archiveDetail.getDepartmentId() != leaderDept.getDepartmentId()) {
                            request.getRequestDispatcher("/profile/leader_no_permission.jsp").forward(request, response);
                            return;
                        }
                        request.setAttribute("archiveDetail", archiveDetail);
                        request.getRequestDispatcher("/profile/leader_archive_detail.jsp").forward(request, response);
                        return;
                    } catch (Exception ignored) {}
                }

                // Phân tách Kho lưu trữ & Tìm kiếm cho Manager
                String keyword = request.getParameter("keyword");
                request.setAttribute("searchKeyword", keyword);

                List archiveList;
                if (isManager) {
                    String filterDept = request.getParameter("filterDeptId");
                    if (filterDept != null && !filterDept.trim().isEmpty() && !"all".equalsIgnoreCase(filterDept.trim())) {
                        int fDeptId = Integer.parseInt(filterDept.trim());
                        archiveList = dao.getArchivesByDepartment(fDeptId, keyword);
                        request.setAttribute("selectedFilterDeptId", String.valueOf(fDeptId));
                    } else {
                        archiveList = dao.getAllArchivesWithSearch(keyword);
                        request.setAttribute("selectedFilterDeptId", "all");
                    }
                } else {
                    DepartmentModel myOwnDept = dao.getDepartmentByLeaderId(empId);
                    int targetDeptId = (myOwnDept != null) ? myOwnDept.getDepartmentId() : leaderDept.getDepartmentId();
                    archiveList = dao.getArchivesByDepartment(targetDeptId, keyword);
                }

                request.setAttribute("archiveList", archiveList);
                request.getRequestDispatcher("/profile/leader_archive.jsp").forward(request, response);
            }
        } finally {
            dao.closeConnection();
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

        DepartmentDAO dao = new DepartmentDAO();
        try {
            boolean isManager = isAdminOrManager(request);
            String reqDeptId = request.getParameter("deptId");
            DepartmentModel ownDept = resolveOwnDepartment(dao, empId);
            DepartmentModel leaderDept = null;
            List allDepts = dao.getAllDepartmentsWithCount();

            // POST không được nhận deptId của phòng mình không phải leader
            if (reqDeptId != null && !reqDeptId.trim().isEmpty()) {
                try {
                    int dId = Integer.parseInt(reqDeptId.trim());
                    DepartmentModel requested = findDepartment(allDepts, dId);
                    if (requested != null && (isManager
                            || (requested.getManagerId() != null && requested.getManagerId().equals(empId)))) {
                        leaderDept = requested;
                    }
                } catch (Exception ignored) {}
            }

            if (leaderDept == null && (reqDeptId == null || reqDeptId.trim().isEmpty())) {
                leaderDept = dao.getDepartmentByLeaderId(empId);
            }

            if (leaderDept == null) {
                request.getRequestDispatcher("/profile/leader_no_permission.jsp").forward(request, response);
                return;
            }

            // Kiểm tra quyền chỉnh sửa ở doPost
            boolean canEdit = isManager || (leaderDept.getManagerId() != null && leaderDept.getManagerId().equals(empId));
            if (!canEdit) {
                request.getRequestDispatcher("/profile/leader_no_permission.jsp").forward(request, response);
                return;
            }

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
                boolean completed = progress >= 100;

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
                boolean completed = progress >= 100;

                dao.updateTaskStatus(taskId, progress, note, completed);
                response.sendRedirect(request.getContextPath() + "/leader-room?tab=topics&empId=" + targetEmpId + deptParamUrl + "&msg=updated");
            } else if ("saveArchive".equals(action)) {
                // Lưu toàn bộ tiến độ dự án hiện tại vào Kho lưu trữ của phòng ban
                String projectName = request.getParameter("projectName");
                if (projectName == null || projectName.trim().isEmpty()) {
                    projectName = "Dự án hoàn thành ngày " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date());
                }
                dao.archiveCurrentProject(leaderDept.getDepartmentId(), projectName.trim(), empId);
                response.sendRedirect(request.getContextPath() + "/leader-room?tab=archive" + deptParamUrl + "&msg=archived");
            } else if ("resetProject".equals(action)) {
                // Reset trắng trơn Chủ đề & Nhiệm vụ của phòng ban khi bấm "Rồi"
                dao.resetCurrentDepartmentTopicsAndTasks(leaderDept.getDepartmentId());
                response.sendRedirect(request.getContextPath() + "/leader-room?tab=topics" + deptParamUrl + "&msg=reset_ok");
            } else if ("deleteArchive".equals(action)) {
                // Xóa vĩnh viễn dự án lưu trữ khỏi Kho lưu trữ
                int archiveId = Integer.parseInt(request.getParameter("archiveId"));
                Integer restrictDeptId = isManager ? null : leaderDept.getDepartmentId();
                dao.deleteArchiveById(archiveId, restrictDeptId);
                response.sendRedirect(request.getContextPath() + "/leader-room?tab=archive" + deptParamUrl + "&msg=deleted");
            }
        } finally {
            dao.closeConnection();
        }
    }
}