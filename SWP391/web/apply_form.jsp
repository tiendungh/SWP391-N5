<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1"/>
        <title>Ứng tuyển - ${job.title}</title>
        <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/img/favicon.svg">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/public.css">
    </head>
    <body class="pb-body">
        <div class="pb-topnav">
            <a class="pb-logo" href="${pageContext.request.contextPath}/home">
                <img src="${pageContext.request.contextPath}/img/logo-white.svg" alt="Job Board Logo">
            </a>
            <div class="pb-nav-links">
                <a class="pb-nav-link" href="${pageContext.request.contextPath}/jobs?id=${job.jobPostId}">&larr; Chi tiết vị trí</a>
                <a class="pb-nav-btn" href="${pageContext.request.contextPath}/jobs">Tất cả vị trí</a>
            </div>
        </div>
        <div class="pb-hero">
            <h1>Ứng tuyển: ${job.title}</h1>
            <p>Điền thông tin bên dưới, không cần tạo tài khoản. Chúng tôi sẽ liên hệ qua email/số điện thoại bạn cung cấp.</p>
        </div>

        <div class="pb-wrap">
            <div class="pb-card">
                <c:if test="${not empty error}"><div class="pb-alert pb-alert-error">${error}</div></c:if>

                <c:set var="firstVisit" value="${empty param.candidateName}"/>
                <c:set var="natVal" value="${firstVisit ? 'Việt Nam' : param.nationality}"/>
                <c:set var="posVal" value="${firstVisit ? job.position : param.position}"/>

                <form method="post" action="${pageContext.request.contextPath}/apply" enctype="multipart/form-data" id="applyForm">
                    <input type="hidden" name="jobPostId" value="${job.jobPostId}"/>
                    <p style="margin:0 0 16px; color:#64748b; font-size:0.9rem;">Tất cả các mục đánh dấu <b style="color:#dc2626;">*</b> đều bắt buộc phải điền.</p>

                    <div class="pb-section-title">Thông tin liên hệ</div>
                    <div class="pb-form-row">
                        <div class="pb-field">
                            <label>Họ và tên *</label>
                            <input type="text" name="candidateName" value="<c:out value='${param.candidateName}'/>" required/>
                        </div>
                        <div class="pb-field">
                            <label>Email *</label>
                            <input type="email" name="email" value="<c:out value='${param.email}'/>" required/>
                        </div>
                    </div>
                    <div class="pb-form-row">
                        <div class="pb-field">
                            <label>Số điện thoại *</label>
                            <input type="text" name="phone" id="phone" value="<c:out value='${param.phone}'/>"
                                   inputmode="numeric" maxlength="10" pattern="0[0-9]{9}" placeholder="09xxxxxxxx"
                                   title="Số điện thoại gồm đúng 10 chữ số và bắt đầu bằng số 0" required/>
                        </div>
                        <div class="pb-field">
                            <label>Địa chỉ *</label>
                            <input type="text" name="address" value="<c:out value='${param.address}'/>" required/>
                        </div>
                        <div class="pb-field">
                            <label>Quốc tịch *</label>
                            <input type="text" name="nationality" value="<c:out value='${natVal}'/>" required/>
                        </div>
                    </div>

                    <div class="pb-section-title">Thông tin ứng tuyển</div>
                    <div class="pb-form-row">
                        <div class="pb-field">
                            <label>Vị trí ứng tuyển *</label>
                            <input type="text" name="position" value="<c:out value='${posVal}'/>" required/>
                        </div>
                        <div class="pb-field">
                            <label>Số năm kinh nghiệm *</label>
                            <input type="number" name="numberExp" min="0" step="1" value="<c:out value='${param.numberExp}'/>" required/>
                        </div>
                        <div class="pb-field">
                            <label>Lương hiện tại / mong muốn *</label>
                            <input type="number" name="currentSalary" min="0" step="0.01" value="<c:out value='${param.currentSalary}'/>" required/>
                        </div>
                    </div>
                    <div class="pb-form-row">
                        <div class="pb-field">
                            <label>Học vấn *</label>
                            <input type="text" name="education" placeholder="Vd: Đại học Bách Khoa Hà Nội" value="<c:out value='${param.education}'/>" required/>
                        </div>
                        <div class="pb-field">
                            <label>Chuyên ngành / Lĩnh vực *</label>
                            <input type="text" name="field" placeholder="Vd: Công nghệ thông tin" value="<c:out value='${param.field}'/>" required/>
                        </div>
                    </div>
                    <div class="pb-form-row">
                        <div class="pb-field">
                            <label>Ngày sinh *</label>
                            <input type="date" name="birthday" id="birthday" value="<c:out value='${param.birthday}'/>" required/>
                        </div>
                        <div class="pb-field">
                            <label>Giới tính *</label>
                            <select name="gender" required>
                                <option value="" disabled ${empty param.gender ? 'selected' : ''}>-- Chọn --</option>
                                <option value="Nam" ${param.gender == 'Nam' ? 'selected' : ''}>Nam</option>
                                <option value="Nữ" ${param.gender == 'Nữ' ? 'selected' : ''}>Nữ</option>
                                <option value="Khác" ${param.gender == 'Khác' ? 'selected' : ''}>Khác</option>
                            </select>
                        </div>
                        <div class="pb-field">
                            <label>File CV (PDF/Word, tối đa 10 MB) *</label>
                            <input type="file" name="cvFile" id="cvFile" accept=".pdf,.doc,.docx" required/>
                        </div>
                    </div>

                    <div class="pb-field" style="margin-bottom:20px;">
                        <label>Lời nhắn / Cover note *</label>
                        <textarea name="note" placeholder="Giới thiệu ngắn về bản thân..." required><c:out value="${param.note}"/></textarea>
                    </div>

                    <button type="submit" class="pb-btn">Nộp hồ sơ ứng tuyển</button>
                </form>
            </div>
        </div>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                // Birthday cannot be in the future
                const birthdayInput = document.getElementById("birthday");
                const today = new Date();
                const year = today.getFullYear();
                const month = String(today.getMonth() + 1).padStart(2, "0");
                const day = String(today.getDate()).padStart(2, "0");
                birthdayInput.max = year + "-" + month + "-" + day;

                // Phone: digits only, exactly 10 digits, must start with 0
                const phoneInput = document.getElementById("phone");
                const phoneMsg = "Số điện thoại phải gồm đúng 10 chữ số và bắt đầu bằng số 0";
                function checkPhone() {
                    phoneInput.value = phoneInput.value.replace(/\D/g, "");
                    phoneInput.setCustomValidity(/^0\d{9}$/.test(phoneInput.value) ? "" : phoneMsg);
                }
                phoneInput.addEventListener("input", checkPhone);
                checkPhone();

                // CV: PDF/DOC/DOCX, max 10 MB
                const cvInput = document.getElementById("cvFile");
                cvInput.addEventListener("change", function () {
                    const f = cvInput.files[0];
                    let msg = "";
                    if (f) {
                        if (!/\.(pdf|docx?)$/i.test(f.name)) msg = "File CV chỉ nhận định dạng PDF, DOC hoặc DOCX";
                        else if (f.size > 10 * 1024 * 1024) msg = "File CV tối đa 10 MB";
                    }
                    cvInput.setCustomValidity(msg);
                });
            });
        </script>
    </body>
</html>
