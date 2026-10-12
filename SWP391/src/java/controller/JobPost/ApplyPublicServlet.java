package controller;

import dal.ApplyDAO;
import dal.CVDAO;
import dal.CandidateDAO;
import dal.JobPostDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.UUID;
import model.CVModel;
import model.CandidateModel;
import model.JobPostModel;
import tool.EncodePassword;
import java.time.LocalDate;

/**
 * Public "ứng tuyển" form - no login required. Looks up (or creates) the
 * Candidate by email, creates a fresh CV row from the submitted info, then
 * creates the Apply record linking them to the chosen JD. URL:
 * /apply?jobPostId=X
 */
@WebServlet("/apply")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024) // 10 MB
public class ApplyPublicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int jobPostId = Integer.parseInt(request.getParameter("jobPostId"));
        JobPostDAO jobPostDAO = new JobPostDAO();
        JobPostModel job = jobPostDAO.getJobPostById(jobPostId);
        jobPostDAO.closeConnection();

        if (job == null || !job.isVisible()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Vị trí này không tồn tại hoặc đã đóng.");
            return;
        }

        request.setAttribute("job", job);
        request.getRequestDispatcher("/apply_form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        int jobPostId = Integer.parseInt(request.getParameter("jobPostId"));
        String candidateName = trim(request.getParameter("candidateName"));
        String address = trim(request.getParameter("address"));
        String email = trim(request.getParameter("email"));
        String phone = trim(request.getParameter("phone"));
        String nationality = trim(request.getParameter("nationality"));
        String position = trim(request.getParameter("position"));
        String numberExpStr = request.getParameter("numberExp");
        String education = trim(request.getParameter("education"));
        String field = trim(request.getParameter("field"));
        String currentSalaryStr = request.getParameter("currentSalary");
        String birthdayStr = request.getParameter("birthday");
        String gender = request.getParameter("gender");
        String note = trim(request.getParameter("note"));

        JobPostDAO jobPostDAO = new JobPostDAO();
        JobPostModel job = jobPostDAO.getJobPostById(jobPostId);
        jobPostDAO.closeConnection();

        if (job == null || !job.isVisible()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Vị trí này không tồn tại hoặc đã đóng.");
            return;
        }

        if (candidateName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            forwardWithError(request, response, job, "Vui lòng nhập đầy đủ Họ tên, Email và Số điện thoại.");
            return;
        }

        CandidateDAO candidateDAO = new CandidateDAO();
        CandidateModel existing = candidateDAO.findByEmail(email);
        int candidateId;

        if (existing != null) {
            candidateId = existing.getCandidateId();
        } else {
            if (candidateDAO.isPhoneTaken(phone, email)) {
                candidateDAO.closeConnection();
                forwardWithError(request, response, job, "Số điện thoại này đã được dùng cho một email khác.");
                return;
            }
            CandidateModel cm = new CandidateModel();
            cm.setCandidateName(candidateName);
            cm.setAddress(address);
            cm.setEmail(email);
            cm.setPhoneNumber(phone);
            cm.setNationality(nationality);
            // No candidate login flow yet - this random hash is unusable as a real password.
            String unusableHash = EncodePassword.encodePasswordbyHash(UUID.randomUUID().toString());
            candidateId = candidateDAO.createCandidate(cm, unusableHash);
        }
        candidateDAO.closeConnection();

        if (candidateId == -1) {
            forwardWithError(request, response, job, "Không thể lưu thông tin ứng viên, vui lòng thử lại.");
            return;
        }

        String savedFileName = saveUploadedCv(request);

        CVModel cv = new CVModel();
        cv.setCandidateId(candidateId);
        cv.setFullName(candidateName);
        cv.setAddress(address);
        cv.setEmail(email);
        cv.setPosition(position.isEmpty() ? job.getPosition() : position);
        cv.setNumberExp(numberExpStr == null || numberExpStr.isBlank() ? null : Integer.parseInt(numberExpStr));
        cv.setEducation(education);
        cv.setField(field);
        cv.setCurrentSalary(currentSalaryStr == null || currentSalaryStr.isBlank() ? null : new BigDecimal(currentSalaryStr));
        if (birthdayStr != null && !birthdayStr.isBlank()) {
            LocalDate birthday = LocalDate.parse(birthdayStr);
            LocalDate today = LocalDate.now();

            if (birthday.isAfter(today)) {
                forwardWithError(request, response, job, "Ngày sinh không được lớn hơn ngày hiện tại.");
                return;
            }

            cv.setBirthday(Date.valueOf(birthday));
        } else {
            cv.setBirthday(null);
        }
        cv.setNationality(nationality);
        cv.setGender(gender);
        cv.setFileData(savedFileName);

        CVDAO cvDAO = new CVDAO();
        int cvId = cvDAO.createCV(cv);
        cvDAO.closeConnection();

        if (cvId == -1) {
            forwardWithError(request, response, job, "Không thể lưu CV, vui lòng thử lại.");
            return;
        }

        ApplyDAO applyDAO = new ApplyDAO();
        int applyId = applyDAO.createApply(jobPostId, candidateId, cvId, note);
        applyDAO.closeConnection();

        if (applyId == -2) {
            forwardWithError(request, response, job, "Email này đã ứng tuyển vị trí trên rồi. Vui lòng chờ phản hồi từ nhà tuyển dụng.");
            return;
        }
        if (applyId == -1) {
            forwardWithError(request, response, job, "Nộp hồ sơ thất bại, vui lòng thử lại.");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/apply_thanks.jsp?job=" + job.getJobPostId());
    }

    /**
     * Saves the optional uploaded CV file under /uploads/cv/ inside the webapp.
     * Returns the stored file name, or null.
     */
    private String saveUploadedCv(HttpServletRequest request) throws IOException, ServletException {
        Part filePart = request.getPart("cvFile");
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }

        String original = filePart.getSubmittedFileName();
        String ext = (original != null && original.contains(".")) ? original.substring(original.lastIndexOf('.')) : "";
        String storedName = UUID.randomUUID() + ext;

        String uploadRealPath = getServletContext().getRealPath("/uploads/cv");
        File uploadDir = new File(uploadRealPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        filePart.write(uploadRealPath + File.separator + storedName);
        return storedName;
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, JobPostModel job, String error)
            throws ServletException, IOException {
        request.setAttribute("job", job);
        request.setAttribute("error", error);
        request.getRequestDispatcher("/apply_form.jsp").forward(request, response);
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
