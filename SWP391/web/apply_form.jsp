<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1"/>
        <title>Ứng tuyển - ${job.title}</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/public.css">
    </head>
    <body class="pb-body">
        <div class="pb-topnav">
<<<<<<< ours
            <a class="pb-logo" href="${pageContext.request.contextPath}/jobs">JOB<span>BOARD</span></a>
=======
            <a class="pb-logo" href="${pageContext.request.contextPath}/jobs">${initParam.companyName}</a>
>>>>>>> theirs
            <a class="pb-nav-link" href="${pageContext.request.contextPath}/jobs?id=${job.jobPostId}">← Quay lại tin tuyển dụng</a>
        </div>
        <div class="pb-hero">
            <h1>Ứng tuyển: ${job.title}</h1>
            <p>Điền thông tin bên dưới, không cần tạo tài khoản. Chúng tôi sẽ liên hệ qua email/số điện thoại bạn cung cấp.</p>
        </div>

        <div class="pb-wrap">
            <div class="pb-card">
                <c:if test="${not empty error}"><div class="pb-alert pb-alert-error">${error}</div></c:if>

<<<<<<< ours
                <form method="post" action="${pageContext.request.contextPath}/apply" enctype="multipart/form-data">
=======
                    <form method="post" action="${pageContext.request.contextPath}/apply" enctype="multipart/form-data">
>>>>>>> theirs
                    <input type="hidden" name="jobPostId" value="${job.jobPostId}"/>

                    <div class="pb-section-title">Thông tin liên hệ</div>
                    <div class="pb-form-row">
                        <div class="pb-field">
                            <label>Họ và tên *</label>
                            <input type="text" name="candidateName" required/>
                        </div>
                        <div class="pb-field">
                            <label>Email *</label>
                            <input type="email" name="email" required/>
                        </div>
                    </div>
                    <div class="pb-form-row">
                        <div class="pb-field">
                            <label>Số điện thoại *</label>
                            <input type="text" name="phone" required/>
                        </div>
                        <div class="pb-field">
                            <label>Địa chỉ</label>
                            <input type="text" name="address"/>
                        </div>
                        <div class="pb-field">
                            <label>Quốc tịch</label>
                            <input type="text" name="nationality" value="Việt Nam"/>
                        </div>
                    </div>

                    <div class="pb-section-title">Thông tin ứng tuyển</div>
                    <div class="pb-form-row">
                        <div class="pb-field">
                            <label>Vị trí ứng tuyển</label>
                            <input type="text" name="position" value="${job.position}"/>
                        </div>
                        <div class="pb-field">
                            <label>Số năm kinh nghiệm</label>
                            <input type="number" name="numberExp" min="0" step="1"/>
                        </div>
                        <div class="pb-field">
                            <label>Lương hiện tại / mong muốn</label>
                            <input type="number" name="currentSalary" min="0" step="0.01"/>
                        </div>
                    </div>
                    <div class="pb-form-row">
                        <div class="pb-field">
                            <label>Học vấn</label>
                            <input type="text" name="education" placeholder="Vd: Đại học Bách Khoa Hà Nội"/>
                        </div>
                        <div class="pb-field">
                            <label>Chuyên ngành / Lĩnh vực</label>
                            <input type="text" name="field" placeholder="Vd: Công nghệ thông tin"/>
                        </div>
                    </div>
                    <div class="pb-form-row">
                        <div class="pb-field">
                            <label>Ngày sinh</label>
<<<<<<< ours
                            <input type="date" name="birthday"/>
=======
                            <input type="date" name="birthday" id="birthday"/>
>>>>>>> theirs
                        </div>
                        <div class="pb-field">
                            <label>Giới tính</label>
                            <select name="gender">
                                <option value="Nam">Nam</option>
                                <option value="Nữ">Nữ</option>
                                <option value="Khác">Khác</option>
                            </select>
                        </div>
                        <div class="pb-field">
                            <label>File CV (PDF/Word, tùy chọn)</label>
                            <input type="file" name="cvFile" accept=".pdf,.doc,.docx"/>
                        </div>
                    </div>

                    <div class="pb-field" style="margin-bottom:20px;">
                        <label>Lời nhắn / Cover note</label>
                        <textarea name="note" placeholder="Giới thiệu ngắn về bản thân..."></textarea>
                    </div>

                    <button type="submit" class="pb-btn">Nộp hồ sơ ứng tuyển</button>
                </form>
            </div>
        </div>
<<<<<<< ours
=======
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const birthdayInput = document.getElementById("birthday");
                const today = new Date();

                const year = today.getFullYear();
                const month = String(today.getMonth() + 1).padStart(2, "0");
                const day = String(today.getDate()).padStart(2, "0");

                birthdayInput.max = year + "-" + month + "-" + day;
            });
        </script>
>>>>>>> theirs
    </body>
</html>
