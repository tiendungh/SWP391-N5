package controller.Profile;

import dal.ProfileDocumentDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import model.ProfileDocumentFileModel;
import tool.SessionUtil;

@WebServlet("/profile-document-download")
public class ProfileDocumentDownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!SessionUtil.requireEmployeeAccess(request, response)) return;

        Integer documentId = parsePositiveInt(request.getParameter("id"));
        if (documentId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Hồ sơ không hợp lệ.");
            return;
        }

        ProfileDocumentDAO dao = new ProfileDocumentDAO();
        ProfileDocumentFileModel file = dao.getDocumentFile(documentId);
        dao.closeConnection();
        if (file == null || file.getFileData() == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy tệp hồ sơ.");
            return;
        }

        boolean ownsDocument = file.getEmployeeId() == SessionUtil.getStaffId(request);
        if (!ownsDocument && !SessionUtil.isHrOrManager(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền xem tệp này.");
            return;
        }

        String originalFileName = file.getOriginalFileName() == null
                ? "document" : file.getOriginalFileName();
        String encodedName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8)
                .replace("+", "%20");
        response.setContentType(file.getContentType());
        response.setContentLengthLong(file.getFileData().length);
        response.setHeader("Cache-Control", "private, no-store, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + encodedName);
        response.getOutputStream().write(file.getFileData());
    }

    private Integer parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } catch (Exception e) {
            return null;
        }
    }
}
