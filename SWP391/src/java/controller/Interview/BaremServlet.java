package controller.Interview;

import dal.BaremDAO;
import dal.JobPostDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import model.BaremModel;
import model.CriteriaModel;
import model.JobPostModel;
import tool.SessionUtil;

/**
 * Lets HR/Manager quickly create an Interview Barem (with its scoring criteria)
 * for a specific open job post, and lists barems already created for that post.
 * URL: /barem?jobPostId=X (&returnApplyId=Y to bounce back into scheduling)
 *
 * jobPostId handling: missing/blank -> job picker; not a number -> 400;
 * number but no such job post (0, negative, unknown) -> 404.
 */
@WebServlet("/barem")
public class BaremServlet extends HttpServlet {

    static final int MAX_BAREM_NAME = 150;   // = InterviewBarem.BaremName NVARCHAR(150)
    static final int MAX_BAREM_DESC = 500;
    static final int MAX_CRIT_NAME = 150;
    static final int MAX_CRIT_DESC = 500;    // = BaremCriteria.Description NVARCHAR(500)
    static final BigDecimal MAX_SCORE_LIMIT = new BigDecimal("9999.99"); // DECIMAL(6,2)
    static final BigDecimal HUNDRED = new BigDecimal("100");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        String param = request.getParameter("jobPostId");
        if (param == null || param.isBlank()) {
            render(request, response, null);
            return;
        }
        Integer jobPostId = parseId(param);
        if (jobPostId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã tin tuyển dụng không hợp lệ.");
            return;
        }
        render(request, response, jobPostId);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireStaffAccess(request, response)) return;

        request.setCharacterEncoding("UTF-8");

        Integer jobPostId = parseId(request.getParameter("jobPostId"));
        if (jobPostId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã tin tuyển dụng không hợp lệ.");
            return;
        }

        JobPostDAO jobPostDAO = new JobPostDAO();
        JobPostModel jobPost;
        try {
            jobPost = jobPostDAO.getJobPostById(jobPostId);
        } finally {
            jobPostDAO.closeConnection();
        }
        if (jobPost == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy tin tuyển dụng.");
            return;
        }

        String baremName = trim(request.getParameter("baremName"));
        String description = trim(request.getParameter("description"));
        String returnApplyId = request.getParameter("returnApplyId");

        String[] critNames = request.getParameterValues("criteriaName");
        String[] critDescs = request.getParameterValues("criteriaDescription");
        String[] critMax = request.getParameterValues("maxScore");
        String[] critWeight = request.getParameterValues("weight");

        // Keep what the user typed so the form can be re-filled if validation fails.
        List<String[]> formRows = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<CriteriaModel> criteriaList = new ArrayList<>();
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;
        Set<String> seenNames = new HashSet<>();

        // ---- Barem header ----
        if (baremName.isEmpty()) {
            errors.add("Vui lòng nhập tên barem.");
        } else if (baremName.length() > MAX_BAREM_NAME) {
            errors.add("Tên barem không được dài quá " + MAX_BAREM_NAME + " ký tự (hiện có " + baremName.length() + ").");
        }
        if (description.length() > MAX_BAREM_DESC) {
            errors.add("Mô tả barem không được dài quá " + MAX_BAREM_DESC + " ký tự (hiện có " + description.length() + ").");
        }

        // ---- Criteria ----
        int n = critNames == null ? 0 : critNames.length;
        int rowNo = 0;
        for (int i = 0; i < n; i++) {
            String name = trim(critNames[i]);
            String desc = trim(at(critDescs, i));
            String maxStr = trim(at(critMax, i));
            String weightStr = trim(at(critWeight, i));
            formRows.add(new String[]{name, desc, maxStr, weightStr});

            if (name.isEmpty() && desc.isEmpty()) continue; // completely empty row: ignore
            rowNo++;
            String label = "Tiêu chí #" + rowNo;

            if (name.isEmpty()) {
                errors.add(label + ": vui lòng nhập tên tiêu chí.");
                continue;
            }
            if (name.length() > MAX_CRIT_NAME) {
                errors.add(label + ": tên tiêu chí không được dài quá " + MAX_CRIT_NAME + " ký tự.");
            }
            if (desc.length() > MAX_CRIT_DESC) {
                errors.add(label + ": mô tả tiêu chí không được dài quá " + MAX_CRIT_DESC
                        + " ký tự (hiện có " + desc.length() + ").");
            }
            if (!seenNames.add(name.toLowerCase(Locale.ROOT))) {
                errors.add(label + ": tên tiêu chí bị trùng trong cùng một barem.");
            }

            BigDecimal max = parseDecimal(maxStr);
            BigDecimal weight = parseDecimal(weightStr);
            if (max == null || max.signum() <= 0 || max.compareTo(MAX_SCORE_LIMIT) > 0) {
                errors.add(label + ": điểm tối đa phải là số lớn hơn 0 và không quá " + MAX_SCORE_LIMIT.toPlainString() + ".");
                max = null;
            }
            if (weight == null || weight.signum() < 0 || weight.compareTo(HUNDRED) > 0) {
                errors.add(label + ": trọng số phải là số từ 0 đến 100.");
                weight = null;
            }
            if (max == null || weight == null) continue;

            CriteriaModel cm = new CriteriaModel();
            cm.setCriteriaName(name);
            cm.setDescription(desc.isEmpty() ? null : desc);
            cm.setMaxScore(max);
            cm.setWeight(weight);
            criteriaList.add(cm);
            totalScore = totalScore.add(max);
            totalWeight = totalWeight.add(weight);
        }

        if (n == 0 || rowNo == 0) {
            errors.add("Barem cần có ít nhất một tiêu chí.");
        } else if (errors.isEmpty() && totalWeight.compareTo(HUNDRED) != 0) {
            errors.add("Tổng trọng số phải bằng 100% (hiện là " + totalWeight.stripTrailingZeros().toPlainString() + "%).");
        }
        if (totalScore.compareTo(MAX_SCORE_LIMIT) > 0) {
            errors.add("Tổng điểm tối đa của các tiêu chí không được vượt quá " + MAX_SCORE_LIMIT.toPlainString() + ".");
        }

        BaremDAO baremDAO = new BaremDAO();
        try {
            // Duplicate barem name within the same JD (only worth checking once the rest is fine)
            if (errors.isEmpty() && baremDAO.existsBaremName(jobPostId, baremName)) {
                errors.add("Vị trí này đã có barem tên \"" + baremName + "\". Vui lòng chọn tên khác.");
            }

            if (errors.isEmpty()) {
                BaremModel barem = new BaremModel();
                barem.setJobPostId(jobPostId);
                barem.setBaremName(baremName);
                barem.setDescription(description.isEmpty() ? null : description);
                barem.setTotalScore(totalScore);
                barem.setCreatedBy(SessionUtil.getStaffId(request));

                int newBaremId = baremDAO.createBaremWithCriteria(barem, criteriaList);
                if (newBaremId > 0) {
                    if (returnApplyId != null && !returnApplyId.isBlank()) {
                        response.sendRedirect(request.getContextPath() + "/interview-schedule?applyId="
                                + java.net.URLEncoder.encode(returnApplyId.trim(), "UTF-8") + "&baremCreated=1");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/barem?jobPostId=" + jobPostId + "&created=1");
                    }
                    return;
                }
                if (newBaremId == BaremDAO.DUPLICATE) {
                    errors.add("Tên barem hoặc tên tiêu chí bị trùng. Vui lòng kiểm tra lại.");
                } else {
                    errors.add("Tạo barem thất bại, vui lòng thử lại.");
                }
            }
        } finally {
            baremDAO.closeConnection();
        }

        request.setAttribute("errors", errors);
        request.setAttribute("formBaremName", baremName);
        request.setAttribute("formDescription", description);
        request.setAttribute("formRows", formRows);
        render(request, response, jobPostId);
    }

    /** Loads page data and forwards to the JSP. jobPostId == null -> job picker. */
    private void render(HttpServletRequest request, HttpServletResponse response, Integer jobPostId)
            throws ServletException, IOException {
        JobPostDAO jobPostDAO = new JobPostDAO();
        BaremDAO baremDAO = new BaremDAO();
        try {
            if (jobPostId == null) {
                request.setAttribute("openJobs", jobPostDAO.getOpenJobPosts());
            } else {
                JobPostModel jobPost = jobPostDAO.getJobPostById(jobPostId);
                if (jobPost == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy tin tuyển dụng.");
                    return;
                }
                request.setAttribute("selectedJobPost", jobPost);
                request.setAttribute("existingBarems", baremDAO.getBaremsByJobPost(jobPostId));
                request.setAttribute("jobPostId", jobPostId);
            }
        } finally {
            jobPostDAO.closeConnection();
            baremDAO.closeConnection();
        }

        String returnApplyId = request.getParameter("returnApplyId");
        if (returnApplyId != null && !returnApplyId.isBlank()) {
            request.setAttribute("returnApplyId", returnApplyId.trim());
        }

        request.getRequestDispatcher("/interview/barem_manage.jsp").forward(request, response);
    }

    private static Integer parseId(String s) {
        if (s == null) return null;
        try {
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static BigDecimal parseDecimal(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private static String at(String[] arr, int i) {
        return arr != null && i < arr.length ? arr[i] : null;
    }
}
