package controller.Profile;

import dal.ProfileDocumentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import model.EmployeeProfileDocumentModel;
import tool.SessionUtil;

@WebServlet("/employee-profiles")
public class EmployeeProfileManageServlet extends HttpServlet {

    @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        // THÊM MỚI: Nạp danh sách tên phòng ban vào request attribute cho dropdown
        dal.DepartmentDAO deptDAO = new dal.DepartmentDAO();
        java.util.List<model.DepartmentModel> allDepts = deptDAO.getAllDepartments();
        java.util.List<String> departmentNames = new java.util.ArrayList<>();
        if (allDepts != null) {
            for (model.DepartmentModel d : allDepts) {
                if (d.getDepartmentName() != null && !d.getDepartmentName().trim().isEmpty()) {
                    departmentNames.add(d.getDepartmentName());
                }
            }
        }
        request.setAttribute("departmentList", departmentNames);

        renderPage(request, response, parsePositiveInt(request.getParameter("employeeId")));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;
        request.setCharacterEncoding("UTF-8");

        String action = clean(request.getParameter("action"));
        Integer employeeId = parsePositiveInt(request.getParameter("employeeId"));

        if ("createType".equals(action)) {
            createDocumentType(request, response, employeeId);
        } else if ("assign".equals(action)) {
            assignDocument(request, response, employeeId);
        } else if ("review".equals(action)) {
            reviewDocument(request, response, employeeId);
        } else if ("assignDepartment".equals(action)) {
            assignDepartment(request, response, employeeId);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thao tác không hợp lệ.");
        }
    }

    // THÊM MỚI: Hàm xử lý gán phòng ban cho nhân sự
    private void assignDepartment(HttpServletRequest request, HttpServletResponse response, Integer employeeId)
            throws IOException {
        if (employeeId == null || employeeId <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nhân viên không hợp lệ.");
            return;
        }

        String department = clean(request.getParameter("department"));
        String newDepartment = clean(request.getParameter("newDepartment"));
        String position = clean(request.getParameter("position"));
        String note = clean(request.getParameter("note"));

        // Ưu tiên lấy tên phòng ban mới nếu người dùng nhập tay
        if (newDepartment != null && !newDepartment.isEmpty()) {
            department = newDepartment;
        }

        if (department == null || department.isEmpty() || position == null || position.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/employee-profiles?error=Vui lòng nhập đầy đủ phòng ban và vị trí");
            return;
        }

        dal.DepartmentDAO deptDAO = new dal.DepartmentDAO();
        boolean ok = deptDAO.assignEmployeeToDepartment(employeeId, department, position, note);

        if (ok) {
            response.sendRedirect(request.getContextPath() + "/employee-profiles?deptAssigned=1");
        } else {
            response.sendRedirect(request.getContextPath() + "/employee-profiles?error=Lỗi khi cập nhật phòng ban");
        }
    }

    private void createDocumentType(HttpServletRequest request, HttpServletResponse response,
            Integer employeeId) throws ServletException, IOException {
        String name = clean(request.getParameter("documentName"));
        String description = clean(request.getParameter("description"));

        if (name == null || name.length() > 150) {
            request.setAttribute("error", "Tên loại hồ sơ là bắt buộc và không được vượt quá 150 ký tự.");
            renderPage(request, response, employeeId);
            return;
        }
        if (description != null && description.length() > 500) {
            request.setAttribute("error", "Mô tả không được vượt quá 500 ký tự.");
            renderPage(request, response, employeeId);
            return;
        }

        ProfileDocumentDAO dao = new ProfileDocumentDAO();
        int result = dao.createDocumentType(name, description, SessionUtil.getStaffId(request));
        dao.closeConnection();

        if (result == -2) {
            request.setAttribute("error", "Loại hồ sơ này đã tồn tại.");
            renderPage(request, response, employeeId);
        } else if (result < 0) {
            request.setAttribute("error", "Không thể tạo loại hồ sơ. Hãy kiểm tra database và thử lại.");
            renderPage(request, response, employeeId);
        } else {
            redirect(response, request, employeeId, "typeCreated=1");
        }
    }

    private void assignDocument(HttpServletRequest request, HttpServletResponse response,
            Integer employeeId) throws ServletException, IOException {
        Integer documentTypeId = parsePositiveInt(request.getParameter("documentTypeId"));
        Date dueDate = null;
        String dueDateValue = clean(request.getParameter("dueDate"));

        if (employeeId == null || documentTypeId == null) {
            request.setAttribute("error", "Vui lòng chọn nhân viên và loại hồ sơ.");
            renderPage(request, response, employeeId);
            return;
        }

        if (dueDateValue != null) {
            try {
                dueDate = Date.valueOf(dueDateValue);
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", "Hạn nộp không hợp lệ.");
                renderPage(request, response, employeeId);
                return;
            }
            Date today = new Date(System.currentTimeMillis());
            if (dueDate.before(today)) {
                request.setAttribute("error", "Hạn nộp không được nằm trong quá khứ.");
                renderPage(request, response, employeeId);
                return;
            }
        }

        boolean required = "on".equals(request.getParameter("required"));
        ProfileDocumentDAO dao = new ProfileDocumentDAO();
        int result = dao.assignDocument(employeeId, documentTypeId, required, dueDate,
                SessionUtil.getStaffId(request));
        dao.closeConnection();

        if (result == -2) {
            request.setAttribute("error", "Nhân viên đã được yêu cầu loại hồ sơ này.");
            renderPage(request, response, employeeId);
        } else if (result < 0) {
            request.setAttribute("error", "Không thể giao yêu cầu hồ sơ. Hãy thử lại.");
            renderPage(request, response, employeeId);
        } else {
            redirect(response, request, employeeId, "assigned=1");
        }
    }

    private void reviewDocument(HttpServletRequest request, HttpServletResponse response,
            Integer requestedEmployeeId) throws ServletException, IOException {
        Integer documentId = parsePositiveInt(request.getParameter("documentId"));
        String decision = clean(request.getParameter("decision"));
        String reviewNote = clean(request.getParameter("reviewNote"));

        if (documentId == null || (!"Approved".equals(decision) && !"Rejected".equals(decision))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thông tin phê duyệt không hợp lệ.");
            return;
        }
        if ("Rejected".equals(decision) && reviewNote == null) {
            request.setAttribute("error", "Vui lòng nhập lý do từ chối để nhân viên biết cần bổ sung gì.");
            renderPage(request, response, requestedEmployeeId);
            return;
        }
        if (reviewNote != null && reviewNote.length() > 1000) {
            request.setAttribute("error", "Nhận xét không được vượt quá 1000 ký tự.");
            renderPage(request, response, requestedEmployeeId);
            return;
        }

        ProfileDocumentDAO dao = new ProfileDocumentDAO();
        EmployeeProfileDocumentModel document = dao.getDocumentById(documentId);
        if (document == null) {
            dao.closeConnection();
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hồ sơ cần duyệt.");
            return;
        }

        boolean success = dao.reviewDocument(documentId, SessionUtil.getStaffId(request), decision, reviewNote);
        dao.closeConnection();
        if (!success) {
            request.setAttribute("error", "Chỉ hồ sơ đang chờ duyệt mới có thể được xử lý.");
            renderPage(request, response, document.getEmployeeId());
            return;
        }

        redirect(response, request, document.getEmployeeId(),
                "reviewed=" + ("Approved".equals(decision) ? "approved" : "rejected"));
    }

    private void renderPage(HttpServletRequest request, HttpServletResponse response, Integer employeeId)
            throws ServletException, IOException {
        ProfileDocumentDAO dao = new ProfileDocumentDAO();
        request.setAttribute("employeeSummaries", dao.getEmployeeSummaries());
        request.setAttribute("documentTypes", dao.getActiveDocumentTypes());

        if (employeeId != null) {
            request.setAttribute("selectedEmployee", dao.getEmployeeSummary(employeeId));
            request.setAttribute("documents", dao.getDocumentsByEmployee(employeeId));
            request.setAttribute("documentHistory", dao.getHistoryByEmployee(employeeId));
        }

        dao.closeConnection();
        request.getRequestDispatcher("/profile/employee_profile_manage.jsp").forward(request, response);
    }

    private void redirect(HttpServletResponse response, HttpServletRequest request,
            Integer employeeId, String query) throws IOException {
        String target = request.getContextPath() + "/employee-profiles";
        if (employeeId != null) target += "?employeeId=" + employeeId + "&" + query;
        else target += "?" + query;
        response.sendRedirect(target);
    }

    private Integer parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String clean(String value) {
        if (value == null) return null;
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }
}
