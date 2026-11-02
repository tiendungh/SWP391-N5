package controller.Career;

import dal.CareerPathDAO;
import dal.PromotionRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.EmployeeCareerModel;
import tool.SessionUtil;

@WebServlet("/my-career-path")
public class MyCareerPathServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireEmployeeAccess(request, response)) return;

        int loggedInEmployeeId = SessionUtil.getStaffId(request);
        int targetEmployeeId = loggedInEmployeeId;
        String requestedEmployeeId = request.getParameter("employeeId");

        if (requestedEmployeeId != null && !requestedEmployeeId.isBlank()) {
            Integer parsedEmployeeId = parsePositiveInt(requestedEmployeeId);
            if (parsedEmployeeId == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nhân viên không hợp lệ.");
                return;
            }
            if (parsedEmployeeId != loggedInEmployeeId && !SessionUtil.isHrOrManager(request)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Bạn không có quyền xem lộ trình của nhân viên khác.");
                return;
            }
            targetEmployeeId = parsedEmployeeId;
        }

        CareerPathDAO dao = new CareerPathDAO();
        EmployeeCareerModel career;
        try {
            career = dao.getEmployeeCareer(targetEmployeeId);
        } finally {
            dao.closeConnection();
        }

        if (career == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Không tìm thấy thông tin lộ trình của nhân viên.");
            return;
        }

        request.setAttribute("career", career);
        request.setAttribute("viewingOwnCareer", targetEmployeeId == loggedInEmployeeId);
        PromotionRequestDAO promotionDao = new PromotionRequestDAO();
        request.setAttribute("employeePromotionRequests",
                promotionDao.getRequestsByEmployee(targetEmployeeId));
        promotionDao.closeConnection();
        request.getRequestDispatcher("/career/my_career_path.jsp").forward(request, response);
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
