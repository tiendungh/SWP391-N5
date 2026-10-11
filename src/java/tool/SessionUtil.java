package tool;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/** Session and authorization helpers for authenticated employees. */
public class SessionUtil {

    public static Integer getStaffId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object value = session.getAttribute("staffId");
        return value instanceof Number ? ((Number) value).intValue() : null;
    }

    public static String getStaffName(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (String) session.getAttribute("staffName");
    }

    public static String getRoleName(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (String) session.getAttribute("roleName");
    }

    public static boolean isHrOrManager(HttpServletRequest request) {
        String roleName = getRoleName(request);
        return "HR Staff".equals(roleName) || "Manager".equals(roleName);
    }

    public static boolean requireEmployeeAccess(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        if (getStaffId(request) == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Chức năng này chỉ dành cho nhân viên.");
            return false;
        }
        return true;
    }

    /**
     * Returns true and lets the caller continue if the user is a logged-in
     * HR Staff or Manager. Otherwise redirects to login (or shows 403) and
     * returns false - the calling servlet must stop processing in that case.
     */
    public static boolean requireStaffAccess(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer staffId = getStaffId(request);
        if (staffId == null) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("email") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Chức năng này chỉ dành cho nhân viên.");
            }
            return false;
        }

        if (!isHrOrManager(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Chỉ HR Staff hoặc Manager mới được truy cập chức năng này.");
            return false;
        }
        return true;
    }
}
