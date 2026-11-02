package controller.Career;

import dal.CareerPathDAO;
import dal.PromotionRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import model.CareerPathModel;
import model.EmployeeCareerModel;
import tool.SessionUtil;

@WebServlet("/promotion-requests")
public class PromotionRequestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;
        renderPage(request, response, parsePositiveInt(request.getParameter("employeeId")));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;
        request.setCharacterEncoding("UTF-8");

        Integer employeeId = parsePositiveInt(request.getParameter("employeeId"));
        Integer proposedPositionId = parsePositiveInt(request.getParameter("proposedPositionId"));
        String reason = clean(request.getParameter("reason"));
        String effectiveDateValue = clean(request.getParameter("effectiveDate"));
        String proposedSalaryValue = clean(request.getParameter("proposedSalary"));

        if (employeeId == null || proposedPositionId == null) {
            request.setAttribute("error", "Vui lòng chọn nhân viên và vị trí đề nghị.");
            renderPage(request, response, employeeId);
            return;
        }
        if (reason == null || reason.length() > 2000) {
            request.setAttribute("error", "Lý do là bắt buộc và không được vượt quá 2000 ký tự.");
            renderPage(request, response, employeeId);
            return;
        }

        Date effectiveDate;
        try {
            effectiveDate = Date.valueOf(effectiveDateValue);
        } catch (Exception e) {
            request.setAttribute("error", "Ngày hiệu lực không hợp lệ.");
            renderPage(request, response, employeeId);
            return;
        }
        if (effectiveDate.toLocalDate().isAfter(LocalDate.now())) {
            request.setAttribute("error", "Ngày hiệu lực không được nằm trong tương lai.");
            renderPage(request, response, employeeId);
            return;
        }

        BigDecimal proposedSalary = null;
        if (proposedSalaryValue != null) {
            try {
                proposedSalary = new BigDecimal(proposedSalaryValue).setScale(2);
                int integerDigits = proposedSalary.precision() - proposedSalary.scale();
                if (proposedSalary.signum() < 0 || integerDigits > 16) {
                    throw new NumberFormatException();
                }
            } catch (Exception e) {
                request.setAttribute("error",
                        "Lương đề xuất phải là số không âm, tối đa 16 chữ số và 2 số thập phân.");
                renderPage(request, response, employeeId);
                return;
            }
        }

        PromotionRequestDAO dao = new PromotionRequestDAO();
        int result = dao.createRequest(employeeId, proposedPositionId, proposedSalary,
                effectiveDate, reason, SessionUtil.getStaffId(request));
        dao.closeConnection();

        if (result > 0) {
            response.sendRedirect(request.getContextPath() + "/promotion-requests?employeeId="
                    + employeeId + "&created=" + result);
            return;
        }

        request.setAttribute("error", getCreateError(result));
        renderPage(request, response, employeeId);
    }

    private void renderPage(HttpServletRequest request, HttpServletResponse response,
            Integer employeeId) throws ServletException, IOException {
        PromotionRequestDAO requestDao = new PromotionRequestDAO();
        request.setAttribute("promotionCandidates", requestDao.getPromotionCandidates());
        request.setAttribute("promotionRequests", requestDao.getRequestsForStaff());

        if (employeeId != null) {
            CareerPathDAO careerDao = new CareerPathDAO();
            EmployeeCareerModel selectedCareer = careerDao.getEmployeeCareer(employeeId);
            careerDao.closeConnection();

            if (selectedCareer == null || !selectedCareer.isActive()) {
                if (request.getAttribute("error") == null) {
                    request.setAttribute("error", "Không tìm thấy nhân viên đang hoạt động.");
                }
            } else {
                boolean hasPendingRequest = requestDao.hasPendingRequest(employeeId);
                boolean canCreatePromotion = false;
                for (CareerPathModel path : selectedCareer.getNextPaths()) {
                    if (path.isEligibleByTenure()
                            && path.getToPosition().getJobLevel().getRankOrder()
                            > selectedCareer.getCurrentPosition().getJobLevel().getRankOrder()) {
                        canCreatePromotion = true;
                        break;
                    }
                }
                LocalDate minEffectiveDate = selectedCareer.getPositionStartDate().toLocalDate();
                request.setAttribute("selectedCareer", selectedCareer);
                request.setAttribute("hasPendingRequest", hasPendingRequest);
                request.setAttribute("canCreatePromotion", canCreatePromotion);
                request.setAttribute("minEffectiveDate", minEffectiveDate.toString());
            }
        }

        requestDao.closeConnection();
        request.setAttribute("today", LocalDate.now().toString());
        request.getRequestDispatcher("/career/promotion_requests.jsp").forward(request, response);
    }

    private String getCreateError(int result) {
        switch (result) {
            case PromotionRequestDAO.CREATE_EMPLOYEE_INVALID:
                return "Nhân viên không tồn tại, đã ngừng hoạt động hoặc chưa có vị trí chuẩn hóa.";
            case PromotionRequestDAO.CREATE_PATH_INVALID:
                return "Vị trí đề xuất không thuộc lộ trình thăng tiến hợp lệ hoặc không cao hơn vị trí hiện tại.";
            case PromotionRequestDAO.CREATE_TENURE_NOT_MET:
                return "Nhân viên chưa đạt thời gian tối thiểu ở vị trí hiện tại.";
            case PromotionRequestDAO.CREATE_PENDING_EXISTS:
                return "Nhân viên đã có một đề nghị thăng chức đang chờ xử lý.";
            case PromotionRequestDAO.CREATE_EFFECTIVE_DATE_INVALID:
                return "Ngày hiệu lực phải từ ngày bắt đầu vị trí hiện tại đến hôm nay.";
            case PromotionRequestDAO.CREATE_FORBIDDEN:
                return "Tài khoản hiện tại không có quyền tạo đề nghị thăng chức.";
            case PromotionRequestDAO.CREATE_SALARY_INVALID:
                return "Lương đề xuất không hợp lệ.";
            case PromotionRequestDAO.CREATE_REASON_INVALID:
                return "Lý do đề nghị không hợp lệ.";
            default:
                return "Không thể tạo đề nghị. Hãy kiểm tra database và thử lại.";
        }
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
