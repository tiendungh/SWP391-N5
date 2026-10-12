package controller.Profile;

import dal.ProfileDocumentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.Locale;
import tool.SessionUtil;

@WebServlet("/my-profile-documents")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024, maxRequestSize = 11 * 1024 * 1024)
public class MyProfileDocumentsServlet extends HttpServlet {

    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireEmployeeAccess(request, response)) return;
        renderPage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireEmployeeAccess(request, response)) return;
        request.setCharacterEncoding("UTF-8");

        Integer documentId = parsePositiveInt(request.getParameter("documentId"));
        String note = clean(request.getParameter("employeeNote"));
        if (documentId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Hồ sơ không hợp lệ.");
            return;
        }
        if (note != null && note.length() > 1000) {
            request.setAttribute("error", "Ghi chú không được vượt quá 1000 ký tự.");
            renderPage(request, response);
            return;
        }

        try {
            Part part = request.getPart("documentFile");
            if (part == null || part.getSize() == 0) {
                request.setAttribute("error", "Vui lòng chọn tệp cần nộp.");
                renderPage(request, response);
                return;
            }
            if (part.getSize() > MAX_FILE_SIZE) {
                request.setAttribute("error", "Tệp không được lớn hơn 10 MB.");
                renderPage(request, response);
                return;
            }

            String fileName = safeFileName(part.getSubmittedFileName());
            byte[] data = part.getInputStream().readAllBytes();
            String contentType = detectAllowedContentType(fileName, data);
            if (contentType == null) {
                request.setAttribute("error", "Chỉ chấp nhận tệp PDF, JPG, JPEG hoặc PNG hợp lệ.");
                renderPage(request, response);
                return;
            }

            ProfileDocumentDAO dao = new ProfileDocumentDAO();
            boolean success = dao.submitDocument(documentId, SessionUtil.getStaffId(request),
                    fileName, contentType, data, note);
            dao.closeConnection();

            if (!success) {
                request.setAttribute("error", "Không thể nộp hồ sơ. Hồ sơ có thể đang chờ duyệt hoặc đã được duyệt.");
                renderPage(request, response);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/my-profile-documents?submitted=1");
        } catch (IllegalStateException e) {
            request.setAttribute("error", "Tệp tải lên vượt quá giới hạn 10 MB.");
            renderPage(request, response);
        }
    }

    private void renderPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int employeeId = SessionUtil.getStaffId(request);
        ProfileDocumentDAO dao = new ProfileDocumentDAO();
        request.setAttribute("profileSummary", dao.getEmployeeSummary(employeeId));
        request.setAttribute("documents", dao.getDocumentsByEmployee(employeeId));
        request.setAttribute("documentHistory", dao.getHistoryByEmployee(employeeId));
        dao.closeConnection();

        // THÊM MỚI: Tải thông tin phòng ban, vị trí và lịch sử công tác của Employee
        loadWorkDepartmentInfo(request, employeeId);

        request.getRequestDispatcher("/profile/my_profile_documents.jsp").forward(request, response);
    }

    // THÊM MỚI: Hàm đọc dữ liệu phòng ban từ database SWP391
    // THÊM MỚI: Hàm đọc dữ liệu phòng ban từ database SWP391 qua DAO
    private void loadWorkDepartmentInfo(HttpServletRequest request, int employeeId) {
        try {
            dal.StaffDAO staffDAO = new dal.StaffDAO();

            // 1. Lấy thông tin phòng ban, chức vụ, mã nhân viên
            java.util.Map<String, Object> workInfo = staffDAO.getEmployeeWorkInfo(employeeId);
            if (workInfo != null && !workInfo.isEmpty()) {
                request.setAttribute("empDepartment", workInfo.get("department"));
                request.setAttribute("empPosition", workInfo.get("position"));
                request.setAttribute("empCode", workInfo.get("employeeCode"));
                request.setAttribute("empHireDate", workInfo.get("hireDate"));
            }

            // 2. Lấy danh sách lịch sử công tác
            java.util.List<java.util.Map<String, Object>> posHistoryList = staffDAO.getPositionHistory(employeeId);
            request.setAttribute("posHistoryList", posHistoryList);
        } catch (Exception e) {
            System.err.println("MyProfileDocumentsServlet.loadWorkDepartmentInfo error: " + e.getMessage());
        }
    }

    private String safeFileName(String submittedName) {
        if (submittedName == null) return "document";
        String normalized = submittedName.replace('\\', '/');
        int slash = normalized.lastIndexOf('/');
        String fileName = slash >= 0 ? normalized.substring(slash + 1) : normalized;
        fileName = fileName.replaceAll("[\\r\\n\\t]", "_").trim();
        if (fileName.isEmpty()) fileName = "document";
        return fileName.length() > 255 ? fileName.substring(fileName.length() - 255) : fileName;
    }

    private String detectAllowedContentType(String fileName, byte[] data) {
        String lowerName = fileName.toLowerCase(Locale.ROOT);
        if (lowerName.endsWith(".pdf") && data.length >= 4
                && data[0] == '%' && data[1] == 'P' && data[2] == 'D' && data[3] == 'F') {
            return "application/pdf";
        }
        if ((lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) && data.length >= 3
                && (data[0] & 0xff) == 0xff && (data[1] & 0xff) == 0xd8 && (data[2] & 0xff) == 0xff) {
            return "image/jpeg";
        }
        byte[] png = {(byte) 0x89, 'P', 'N', 'G', 0x0d, 0x0a, 0x1a, 0x0a};
        if (lowerName.endsWith(".png") && data.length >= png.length) {
            for (int i = 0; i < png.length; i++) {
                if (data[i] != png[i]) return null;
            }
            return "image/png";
        }
        return null;
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