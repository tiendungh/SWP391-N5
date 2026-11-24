<%@page contentType="text/html" pageEncoding="UTF-8"%>
<footer class="modern-footer">
    <div class="container">
        <div class="footer-main">
            <div class="footer-brand">
                <a href="${pageContext.request.contextPath}/home" class="footer-logo">
                    <img src="${pageContext.request.contextPath}/img/logo-white.svg" alt="Job Board Logo" class="footer-logo-img">
                </a>
                <p class="footer-description">Hệ thống tuyển dụng và quản trị nhân sự chuẩn mực doanh nghiệp. Kết nối nhân tài với cơ hội nghề nghiệp hàng đầu và lộ trình phát triển minh bạch.</p>
                <div class="footer-badges">
                    <span class="footer-pill"><i class="fa fa-shield"></i> Tiêu chuẩn ISO HR</span>
                    <span class="footer-pill"><i class="fa fa-lock"></i> Bảo mật dữ liệu</span>
                </div>
                <div class="footer-socials">
                    <a href="#" aria-label="Facebook"><i class="fa fa-facebook"></i></a>
                    <a href="#" aria-label="LinkedIn"><i class="fa fa-linkedin"></i></a>
                    <a href="#" aria-label="Instagram"><i class="fa fa-instagram"></i></a>
                </div>
            </div>
            <div class="footer-links">
                <div class="footer-column">
                    <h4>Cơ hội nghề nghiệp</h4>
                    <a href="${pageContext.request.contextPath}/home">Trang chủ tuyển dụng</a>
                    <a href="${pageContext.request.contextPath}/jobs">Tìm việc làm</a>
                    <a href="${pageContext.request.contextPath}/login.jsp">Cổng nội bộ nhân sự</a>
                </div>
                <div class="footer-column">
                    <h4>Văn hóa &amp; Hỗ trợ</h4>
                    <a href="#why-us">Giá trị cốt lõi</a>
                    <a href="#open-positions">Vị trí đang mở</a>
                    <a href="${pageContext.request.contextPath}/prepare">Chính sách bảo mật</a>
                </div>
                <div class="footer-contact">
                    <div class="footer-contact-icon"><i class="ti-headphone-alt"></i></div>
                    <div>
                        <span>Bộ phận Tuyển dụng &amp; Nhân sự</span>
                        <strong>Hotline: (024) 7300 8888</strong>
                        <p>Đội ngũ tuyển dụng luôn sẵn sàng giải đáp mọi thắc mắc của bạn.</p>
                        <form action="prepare" method="get">
                            <button class="footer-contact-btn" type="submit">Gửi phản hồi <i class="fa fa-arrow-right"></i></button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <div class="footer-bottom">
            <p>&copy; 2026 JOB BOARD Enterprise Recruitment System. Tất cả các quyền được bảo lưu.</p>
            <div class="footer-bottom-links">
                <a href="${pageContext.request.contextPath}/prepare">Điều khoản sử dụng</a>
                <a href="${pageContext.request.contextPath}/prepare">Quy chuẩn ứng viên</a>
                <a href="${pageContext.request.contextPath}/prepare">Bảo mật thông tin</a>
            </div>
        </div>
    </div>
</footer>
