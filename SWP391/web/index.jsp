<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="x-ua-compatible" content="ie=edge">
        <title>Tuyển dụng - JOB BOARD</title>
        <meta name="description" content="JOB BOARD đang tuyển dụng nhân tài. Khám phá vị trí đang mở và gia nhập đội ngũ của chúng tôi.">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <!-- Favicon -->
        <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/img/favicon.svg">
        <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/img/favicon.svg">

        <!-- Google Fonts: Inter -->
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">

        <!-- CSS: Bootstrap first, then libraries, then main style -->
        <link rel="stylesheet" href="css/bootstrap.min.css">
        <link rel="stylesheet" href="css/font-awesome.min.css">
        <link rel="stylesheet" href="css/themify-icons.css">
        <link rel="stylesheet" href="css/flaticon.css">
        <link rel="stylesheet" href="css/owl.carousel.min.css">
        <link rel="stylesheet" href="css/magnific-popup.css">
        <link rel="stylesheet" href="css/nice-select.css">
        <link rel="stylesheet" href="css/gijgo.css">
        <link rel="stylesheet" href="css/animate.min.css">
        <link rel="stylesheet" href="css/slicknav.css">
        <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="css/custom.css">
    </head>

    <body>
        <!-- header_start -->
        <jsp:include page="header.jsp"/>
        <!-- header_end -->

        <!-- ================= HERO: company hiring banner ================= -->
        <section class="careers-hero">
            <div class="container">
                <div class="row align-items-center">
                    <div class="col-lg-7">
                        <span class="hero-kicker"><i class="fa fa-shield"></i> HỆ THỐNG TUYỂN DỤNG DOANH NGHIỆP</span>
                        <h1 class="hero-title">Xây dựng sự nghiệp vững bền<br><span>cùng JOB BOARD</span></h1>
                        <p class="hero-sub">
                            Môi trường chuyên nghiệp kết nối những chuyên viên xuất sắc với các cơ hội
                            nghề nghiệp hàng đầu. Quy trình tuyển dụng minh bạch, đánh giá năng lực chuẩn mực
                            và lộ trình thăng tiến rõ ràng.
                        </p>
                        <div class="hero-cta">
                            <a href="#open-positions" class="boxed-btn3 hero-primary-btn"><i class="fa fa-briefcase"></i> Khám phá vị trí đang tuyển</a>
                            <a href="#why-us" class="hero-link-btn"><i class="fa fa-compass"></i> Giá trị doanh nghiệp <i class="fa fa-angle-right"></i></a>
                        </div>
                        <div class="hero-stats">
                            <div class="stat-box"><strong>${openJobCount != null ? openJobCount : '25+'}</strong><span>Vị trí tuyển dụng</span></div>
                            <div class="stat-box"><strong>10+</strong><span>Năm phát triển</span></div>
                            <div class="stat-box"><strong>500+</strong><span>Nhân sự tài năng</span></div>
                            <div class="stat-box"><strong>98%</strong><span>Hài lòng nội bộ</span></div>
                        </div>
                    </div>
                    <div class="col-lg-5">
                        <div class="hero-visual-wrapper">
                            <img src="${pageContext.request.contextPath}/img/hero-illustration.svg" alt="Recruitment Pipeline Illustration" class="hero-illustration-img">
                        </div>
                    </div>
                </div>
            </div>
        </section>
        <!-- ================= /HERO ================= -->

        <div class="main-content">
            <div class="job-explore">
                <!-- search-area_start -->
                <div class="search_container">
                    <form action="search" method="get" class="search_bar">
                        <div class="search_inputs">
                            <!-- Keyword -->
                            <input type="text" name="keyword" placeholder="Vị trí tuyển dụng">

                            <!-- Occupations -->
                            <select name="category">
                                <option value="">Tất cả ngành nghề</option>
                                <option value="Công nghệ thông tin">Công nghệ thông tin</option>
                                <option value="Kinh doanh & Quản trị">Kinh doanh & Quản trị</option>
                                <option value="Thiết kế - Nghệ thuật - Truyền thông">Thiết kế - Nghệ thuật - Truyền thông</option>
                                <option value="Kỹ thuật – Cơ khí – Điện – Xây dựng">Kỹ thuật – Cơ khí – Điện – Xây dựng</option>
                                <option value="Y tế – Dược – Chăm sóc sức khỏe">Y tế – Dược – Chăm sóc sức khỏe</option>
                                <option value="Giáo dục – Đào tạo">Giáo dục – Đào tạo</option>
                                <option value="Du lịch – Nhà hàng – Khách sạn">Du lịch – Nhà hàng – Khách sạn</option>
                                <option value="Nông – Lâm – Ngư nghiệp">Nông – Lâm – Ngư nghiệp</option>
                                <option value="Luật – Hành chính – Chính trị">Luật – Hành chính – Chính trị</option>
                                <option value="Khác / Tự do">Khác / Tự do</option>
                            </select>

                            <!-- Location -->
                            <select name="location">
                                <option value="">Toàn quốc</option>
                                <option value="Hà Nội">TP. Hà Nội</option>
                                <option value="Hồ Chí Minh">TP. Hồ Chí Minh</option>
                                <option value="Đà Nẵng">TP. Đà Nẵng</option>
                                <option value="Hải Phòng">TP. Hải Phòng</option>
                                <option value="Huế">TP. Huế</option>
                                <option value="Cần Thơ">TP. Cần Thơ</option>
                                <option value="An Giang">An Giang</option>
                                <option value="Bắc Ninh">Bắc Ninh</option>
                                <option value="Cà Mau">Cà Mau</option>
                                <option value="Cao Bằng">Cao Bằng</option>
                                <option value="Đắk Lắk">Đắk Lắk</option>
                                <option value="Điện Biên">Điện Biên</option>
                                <option value="Đồng Nai">Đồng Nai</option>
                                <option value="Đồng Tháp">Đồng Tháp</option>
                                <option value="Gia Lai">Gia Lai</option>
                                <option value="Hà Tĩnh">Hà Tĩnh</option>
                                <option value="Hưng Yên">Hưng Yên</option>
                                <option value="Khánh Hòa">Khánh Hòa</option>
                                <option value="Lai Châu">Lai Châu</option>
                                <option value="Lâm Đồng">Lâm Đồng</option>
                                <option value="Lạng Sơn">Lạng Sơn</option>
                                <option value="Lào Cai">Lào Cai</option>
                                <option value="Ninh Bình">Ninh Bình</option>
                                <option value="Nghệ An">Nghệ An</option>
                                <option value="Phú Thọ">Phú Thọ</option>
                                <option value="Quảng Ngãi">Quảng Ngãi</option>
                                <option value="Quảng Ninh">Quảng Ninh</option>
                                <option value="Quảng Trị">Quảng Trị</option>
                                <option value="Sơn La">Sơn La</option>
                                <option value="Tây Ninh">Tây Ninh</option>
                                <option value="Thái Nguyên">Thái Nguyên</option>
                                <option value="Thanh Hóa">Thanh Hóa</option>
                                <option value="Tuyên Quang">Tuyên Quang</option>
                                <option value="Vĩnh Long">Vĩnh Long</option>
                            </select>

                        </div>

                        <div class="search_buttons">
                            <button type="submit" class="btn-find"><i class="ti-search"></i></button>
                        </div>
                    </form>
                </div>
                <!-- search-area-end -->

                <!-- job_highlight_slider_start: featured open positions -->
                <div class="job_highlight_slider position-relative">
                    <button class="job_nav_arrow prev" aria-label="Trước">&#10094;</button>

                    <div class="job_slide_container">
                        <c:forEach var="job" items="${featuredJobs}">
                            <div class="job_slide">
                                <div class="job_card_large white-bg d-flex align-items-center">
                                    <div class="job_logo_large text-center">
                                        <i class="fa fa-briefcase"></i>
                                    </div>

                                    <div class="job_info_large">
                                        <h3 class="job_title_large mb-2">${job.title}</h3>
                                        <p class="job_company_large mb-1">
                                            <i class="fa fa-tags"></i> ${job.category}
                                        </p>
                                        <p class="job_salary_large mb-1">
                                            <i class="fa fa-money"></i>
                                            <fmt:formatNumber value="${job.offerMin}" type="number" maxFractionDigits="0"/> -
                                            <fmt:formatNumber value="${job.offerMax}" type="number" maxFractionDigits="0"/> VNĐ
                                        </p>
                                        <p class="job_location_large mb-1">
                                            <i class="fa fa-map-marker"></i> ${job.location}
                                        </p>
                                        <p class="job_type_large mb-2">
                                            <i class="fa fa-clock-o"></i> ${job.typeJob}
                                        </p>
                                        <a href="job_details?id=${job.jobPostId}" class="boxed-btn3">
                                            XEM CHI TIẾT
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>

                        <c:if test="${empty featuredJobs}">
                            <div class="job_slide active">
                                <div class="job_card_large white-bg d-flex align-items-center">
                                    <div class="job_logo_large text-center"><i class="fa fa-briefcase"></i></div>
                                    <div class="job_info_large">
                                        <h3 class="job_title_large mb-2">Hiện chưa có vị trí nổi bật</h3>
                                        <p class="job_company_large mb-1">Hãy quay lại sau, chúng tôi luôn cập nhật cơ hội mới.</p>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>

                    <button class="job_nav_arrow next" aria-label="Sau">&#10095;</button>

                    <div class="job_dots my-2"></div>
                </div>
                <!-- job_highlight_slider_end -->
            </div>
        </div>

        <!-- ================= WHY JOIN US ================= -->
        <section id="why-us" class="why-us-section">
            <div class="container">
                <div class="section-heading">
                    <span class="section-kicker"><i class="fa fa-check-circle"></i> GIÁ TRỊ DOANH NGHIỆP</span>
                    <h2>Môi trường chuyên nghiệp để bạn phát triển</h2>
                    <p class="section-subtext">Chúng tôi cam kết xây dựng một môi trường làm việc chuẩn mực, minh bạch và tràn đầy cảm hứng sáng tạo.</p>
                </div>
                <div class="why-us-grid">
                    <div class="why-us-card">
                        <div class="why-us-icon"><i class="fa fa-line-chart"></i></div>
                        <h4>Lộ trình thăng tiến chuẩn mực</h4>
                        <p>Từ Employee đến Leader, từng nấc thang nghề nghiệp được đồng hành và ghi nhận qua các kỳ đánh giá năng lực minh bạch.</p>
                        <div class="card-accent-bar"></div>
                    </div>
                    <div class="why-us-card">
                        <div class="why-us-icon"><i class="fa fa-money"></i></div>
                        <h4>Thu nhập &amp; phúc lợi vượt trội</h4>
                        <p>Mức thu nhập tương xứng năng lực, thưởng hiệu quả kinh doanh, gói bảo hiểm sức khỏe VIP và các chế độ đãi ngộ toàn diện.</p>
                        <div class="card-accent-bar"></div>
                    </div>
                    <div class="why-us-card">
                        <div class="why-us-icon"><i class="fa fa-graduation-cap"></i></div>
                        <h4>Đào tạo &amp; phát triển liên tục</h4>
                        <p>Ngân sách học tập định kỳ, khóa đào tạo nâng cao nghiệp vụ và cơ hội tham gia các dự án công nghệ quy mô lớn.</p>
                        <div class="card-accent-bar"></div>
                    </div>
                    <div class="why-us-card">
                        <div class="why-us-icon"><i class="fa fa-heart"></i></div>
                        <h4>Văn hóa gắn kết, chuyên nghiệp</h4>
                        <p>Không gian làm việc năng động, tôn trọng sáng kiến cá nhân và xây dựng tinh thần đồng đội bền vững.</p>
                        <div class="card-accent-bar"></div>
                    </div>
                </div>
            </div>
        </section>
        <!-- ================= /WHY JOIN US ================= -->

        <!-- job_listing_area_start  -->
        <div class="job_listing_area" id="open-positions">
            <div class="job_listing_container">
                <div class="job_listing_header">
                    <div class="job_listing_title">Vị Trí Đang Tuyển</div>
                    <a href="jobs" class="job_listing_viewall">Xem Thêm</a>
                </div>
                <div class="job_listing_nav">
                    <div class="job_listing_grid">
                        <c:forEach var="job" items="${jobs}">
                            <div class="job_card">
                                <a href="job_details?id=${job.jobPostId}" class="job_card_link"></a>
                                <div class="my-thumb-2">
                                    <i class="fa fa-briefcase"></i>
                                </div>
                                <div class="job_card_info">
                                    <div class="job_card_title">${job.title}</div>
                                    <div class="job_card_company">${job.category}</div>
                                    <div class="job_card_salary">
                                        <fmt:formatNumber value="${job.offerMin}" type="number" maxFractionDigits="0"/> -
                                        <fmt:formatNumber value="${job.offerMax}" type="number" maxFractionDigits="0"/> VNĐ
                                    </div>
                                    <div class="job_card_location">${job.location}</div>
                                </div>
                            </div>
                        </c:forEach>

                        <c:if test="${empty jobs}">
                            <p class="no-jobs-msg">Hiện chưa có vị trí nào đang mở. Vui lòng quay lại sau.</p>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
        <!-- job_listing_area_end  -->

        <!-- job_searcing_wrap  -->
        <div class="job_searcing_wrap overlay career-growth-wrap">
            <div class="container">
                <div class="row align-items-center">
                    <div class="col-lg-6 col-md-6">
                        <div class="searching_text career-searching-text">
                            <span class="career-label">LỘ TRÌNH SỰ NGHIỆP</span>
                            <h3>Định hướng tương lai<br>và phát triển bản thân</h3>
                            <p>Khám phá lộ trình thăng tiến trong công việc phù hợp với đúng vị trí, năng lực và mục tiêu của bạn.</p>
                            <a href="jobs" class="boxed-btn3">KHÁM PHÁ LỘ TRÌNH</a>
                        </div>
                    </div>
                    <div class="col-lg-6 col-md-6">
                        <div class="career-roadmap-card">
                            <div class="roadmap-card-header">
                                <div>
                                    <span class="roadmap-kicker">CAREER ROADMAP</span>
                                    <h4>Lộ trình thăng tiến</h4>
                                </div>
                                <span class="roadmap-icon">↗</span>
                            </div>
                            <div class="roadmap-steps">
                                <div class="roadmap-step active"><span><strong>Employee</strong></span><div><small>Phát triển nền tảng</small></div></div>
                                <div class="roadmap-line"></div>
                                <div class="roadmap-step"><span><strong>Manager</strong></span><div><small>Nâng cao chuyên môn</small></div></div>
                                <div class="roadmap-line"></div>
                                <div class="roadmap-step"><span><strong>Leader</strong></span><div><small>Dẫn dắt & quản lý</small></div></div>
                            </div>
                            <div class="roadmap-card-footer">
                                <span style="display: block;">✓ Đánh giá năng lực</span>
                                                        <span>✓ Mục tiêu cá nhân</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!--job_searcing_wrap end-->

        <!-- ================= FINAL CTA ================= -->
        <section class="apply-cta">
            <div class="container text-center">
                <h2>Không tìm thấy vị trí phù hợp?</h2>
                <p>Gửi CV ứng tuyển ngay hôm nay, chúng tôi sẽ liên hệ khi có cơ hội phù hợp với bạn.</p>
                <a href="jobs" class="boxed-btn3">Nộp CV ứng tuyển</a>
            </div>
        </section>
        <!-- ================= /FINAL CTA ================= -->

        <!-- footer -->
        <jsp:include page="footer.jsp"/>
        <!-- footer -->

        <script>
            document.addEventListener("DOMContentLoaded", () => {

                const slides = Array.from(document.querySelectorAll(".job_slide"));
                const dotsContainer = document.querySelector(".job_dots");
                const prevBtn = document.querySelector(".job_nav_arrow.prev");
                const nextBtn = document.querySelector(".job_nav_arrow.next");

                let dots = [];
                let currentIndex = 0;
                let intervalId = null;
                const AUTO_MS = 6000;

                if (slides.length === 0)
                    return;

                // Tạo dots
                function buildDots() {
                    dotsContainer.innerHTML = "";
                    dots = [];

                    slides.forEach((_, i) => {
                        const dot = document.createElement("span");
                        dot.className = i === 0 ? "active" : "";
                        dot.addEventListener("click", () => goToSlide(i));
                        dotsContainer.appendChild(dot);
                        dots.push(dot);
                    });
                }

                function updateSlides() {
                    slides.forEach((slide, i) => {
                        slide.classList.toggle("active", i === currentIndex);
                    });
                    dots.forEach((dot, i) => {
                        dot.classList.toggle("active", i === currentIndex);
                    });
                }

                function goToSlide(index) {
                    currentIndex = index;
                    updateSlides();
                }

                function nextSlide() {
                    currentIndex = (currentIndex + 1) % slides.length;
                    updateSlides();
                }

                function prevSlide() {
                    currentIndex = (currentIndex - 1 + slides.length) % slides.length;
                    updateSlides();
                }

                function startAuto() {
                    stopAuto();
                    if (slides.length > 1)
                        intervalId = setInterval(nextSlide, AUTO_MS);
                }

                function stopAuto() {
                    if (intervalId)
                        clearInterval(intervalId);
                    intervalId = null;
                }

                // Khởi tạo
                buildDots();
                updateSlides();
                startAuto();

                // Nút điều hướng
                prevBtn?.addEventListener("click", prevSlide);
                nextBtn?.addEventListener("click", nextSlide);

                // Hover để dừng
                const slider = document.querySelector(".job_highlight_slider");
                slider?.addEventListener("mouseenter", stopAuto);
                slider?.addEventListener("mouseleave", startAuto);

            });
        </script>
    </body>
</html>
