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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import model.CVModel;
import model.CandidateModel;
import model.JobPostModel;
import tool.EncodePassword;
import tool.InputValidator;

/**
 * Public "ứng tuyển" form - no login required. Looks up (or creates) the
 * Candidate by email, creates a fresh CV row from the submitted info, then
 * creates the Apply record linking them to the chosen JD. URL:
 * /apply?jobPostId=X
 */
@WebServlet("/apply")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024) // 10 MB
public class ApplyPublicServlet extends HttpServlet {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final List<String> GENDERS = List.of("Nam", "Nữ", "Khác");
    private static final List<String> CV_EXTENSIONS = List.of(".pdf", ".doc", ".docx");

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
        String numberExpStr = trim(request.getParameter("numberExp"));
        String education = trim(request.getParameter("education"));
        String field = trim(request.getParameter("field"));
        String currentSalaryStr = trim(request.getParameter("currentSalary"));
        String birthdayStr = trim(request.getParameter("birthday"));
        String gender = trim(request.getParameter("gender"));
        String note = trim(request.getParameter("note"));

        JobPostDAO jobPostDAO = new JobPostDAO();
        JobPostModel job = jobPostDAO.getJobPostById(jobPostId);
        jobPostDAO.closeConnection();

        if (job == null || !job.isVisible()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Vị trí này không tồn tại hoặc đã đóng.");
            return;
        }

        Part cvPart = request.getPart("cvFile");
        List<String> missing = new ArrayList<>();
        if (candidateName.isEmpty()) missing.add("Họ và tên");
        if (email.isEmpty()) missing.add("Email");
        if (phone.isEmpty()) missing.add("Số điện thoại");
        if (address.isEmpty()) missing.add("Địa chỉ");
        if (nationality.isEmpty()) missing.add("Quốc tịch");
        if (position.isEmpty()) missing.add("Vị trí ứng tuyển");
        if (numberExpStr.isEmpty()) missing.add("Số năm kinh nghiệm");
        if (currentSalaryStr.isEmpty()) missing.add("Lương hiện tại / mong muốn");
        if (education.isEmpty()) missing.add("Học vấn");
        if (field.isEmpty()) missing.add("Chuyên ngành / Lĩnh vực");
        if (birthdayStr.isEmpty()) missing.add("Ngày sinh");
        if (gender.isEmpty()) missing.add("Giới tính");
        if (cvPart == null || cvPart.getSize() == 0) missing.add("File CV");
        if (note.isEmpty()) missing.add("Lời nhắn / Cover note");
        if (!missing.isEmpty()) {
            forwardWithError(request, response, job,
                    "Vui lòng điền đầy đủ các mục bắt buộc: " + String.join(", ", missing) + ".");
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            forwardWithError(request, response, job, "Email không hợp lệ.");
            return;
        }
        if (!InputValidator.isValidPhone(phone)) {
            forwardWithError(request, response, job, "Số điện thoại phải gồm đúng 10 chữ số và bắt đầu bằng số 0.");
            return;
        }
        if (!GENDERS.contains(gender)) {
            forwardWithError(request, response, job, "Giới tính không hợp lệ.");
            return;
        }

        Integer numberExp;
        try {
            numberExp = Integer.valueOf(numberExpStr);
        } catch (NumberFormatException e) {
            numberExp = null;
        }
        if (numberExp == null || numberExp < 0) {
            forwardWithError(request, response, job, "Số năm kinh nghiệm phải là số nguyên không âm.");
            return;
        }

        BigDecimal currentSalary;
        try {
            currentSalary = new BigDecimal(currentSalaryStr);
        } catch (NumberFormatException e) {
            currentSalary = null;
        }
        if (currentSalary == null || currentSalary.signum() < 0) {
            forwardWithError(request, response, job, "Lương phải là số không âm.");
            return;
        }

        LocalDate birthday;
        try {
            birthday = LocalDate.parse(birthdayStr);
        } catch (DateTimeParseException e) {
            forwardWithError(request, response, job, "Ngày sinh không hợp lệ.");
            return;
        }
        if (birthday.isAfter(LocalDate.now())) {
            forwardWithError(request, response, job, "Ngày sinh không được lớn hơn ngày hiện tại.");
            return;
        }

        String cvName = cvPart.getSubmittedFileName() == null ? "" : cvPart.getSubmittedFileName().toLowerCase();
        if (CV_EXTENSIONS.stream().noneMatch(cvName::endsWith)) {
            forwardWithError(request, response, job, "File CV chỉ nhận định dạng PDF, DOC hoặc DOCX.");
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
        cv.setPosition(position);
        cv.setNumberExp(numberExp);
        cv.setEducation(education);
        cv.setField(field);
        cv.setCurrentSalary(currentSalary);
        cv.setBirthday(Date.valueOf(birthday));
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
     * Saves the uploaded CV file under /uploads/cv/ inside the webapp.
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
