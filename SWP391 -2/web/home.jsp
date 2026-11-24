<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
        <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/img/favicon.svg">
        <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/img/favicon.svg">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/font-awesome.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/themify-icons.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/flaticon.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/owl.carousel.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/magnific-popup.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nice-select.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/gijgo.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/animate.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/slicknav.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/custom.css">
    </head>
    <body>
        <jsp:include page="header.jsp"/>

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
                            <div class="stat-box"><strong>${openJobCount != null ? openJobCount : 0}</strong><span>Vị trí tuyển dụng</span></div>
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

        <div class="main-content">
            <div class="job-explore">
                <div class="job_highlight_slider position-relative">
                    <button class="job_nav_arrow prev" aria-label="Trước">&#10094;</button>
                    <div class="job_slide_container">
                        <c:forEach var="job" items="${featuredJobs}">
                            <div class="job_slide">
                                <div class="job_card_large white-bg d-flex align-items-center">
                                    <div class="job_logo_large text-center"><i class="fa fa-briefcase"></i></div>
                                    <div class="job_info_large">
                                        <h3 class="job_title_large mb-2">${job.title}</h3>
                                        <p class="job_company_large mb-1"><i class="fa fa-tags"></i> ${job.category}</p>
                                        <p class="job_salary_large mb-1">
                                            <i class="fa fa-money"></i>
                                            <fmt:formatNumber value="${job.offerMin}" type="number" maxFractionDigits="0"/> -
                                            <fmt:formatNumber value="${job.offerMax}" type="number" maxFractionDigits="0"/> $
                                        </p>
                                        <p class="job_location_large mb-1"><i class="fa fa-map-marker"></i> ${job.location}</p>
                                        <p class="job_type_large mb-2"><i class="fa fa-clock-o"></i> ${job.typeJob}</p>
                                        <a href="${pageContext.request.contextPath}/jobs?id=${job.jobPostId}" class="boxed-btn3">XEM CHI TIẾT</a>
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
            </div>
        </div>

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
                    </div>
                    <div class="why-us-card">
                        <div class="why-us-icon"><i class="fa fa-money"></i></div>
                        <h4>Thu nhập &amp; phúc lợi vượt trội</h4>
                        <p>Mức thu nhập tương xứng năng lực, thưởng hiệu quả kinh doanh, gói bảo hiểm sức khỏe VIP và các chế độ đãi ngộ toàn diện.</p>
                    </div>
                    <div class="why-us-card">
                        <div class="why-us-icon"><i class="fa fa-graduation-cap"></i></div>
                        <h4>Đào tạo &amp; phát triển liên tục</h4>
                        <p>Ngân sách học tập định kỳ, khóa đào tạo nâng cao nghiệp vụ và cơ hội tham gia các dự án công nghệ quy mô lớn.</p>
                    </div>
                    <div class="why-us-card">
                        <div class="why-us-icon"><i class="fa fa-heart"></i></div>
                        <h4>Văn hóa gắn kết, chuyên nghiệp</h4>
                        <p>Không gian làm việc năng động, tôn trọng sáng kiến cá nhân và xây dựng tinh thần đồng đội bền vững.</p>
                    </div>
                </div>
            </div>
        </section>

        <div class="job_listing_area" id="open-positions">
            <div class="job_listing_container">
                <div class="job_listing_header">
                    <div class="job_listing_title">Vị trí đang tuyển</div>
                    <a href="${pageContext.request.contextPath}/jobs" class="job_listing_viewall">Xem thêm</a>
                </div>
                <div class="job_listing_nav">
                    <div class="job_listing_grid">
                        <c:forEach var="job" items="${jobs}">
                            <div class="job_card">
                                <a href="${pageContext.request.contextPath}/jobs?id=${job.jobPostId}" class="job_card_link"></a>
                                <div class="my-thumb-2"><i class="fa fa-briefcase"></i></div>
                                <div class="job_card_info">
                                    <div class="job_card_title">${job.title}</div>
                                    <div class="job_card_company">${job.category}</div>
                                    <div class="job_card_salary">
                                        <fmt:formatNumber value="${job.offerMin}" type="number" maxFractionDigits="0"/> -
                                        <fmt:formatNumber value="${job.offerMax}" type="number" maxFractionDigits="0"/> $
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

        <section class="apply-cta">
            <div class="container text-center">
                <h2>Không tìm thấy vị trí phù hợp?</h2>
                <p>Gửi CV ứng tuyển ngay hôm nay, chúng tôi sẽ liên hệ khi có cơ hội phù hợp với bạn.</p>
                <a href="${pageContext.request.contextPath}/jobs" class="boxed-btn3">Nộp CV ứng tuyển</a>
            </div>
        </section>

        <jsp:include page="footer.jsp"/>

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
                if (slides.length === 0) return;

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
                    slides.forEach((slide, i) => slide.classList.toggle("active", i === currentIndex));
                    dots.forEach((dot, i) => dot.classList.toggle("active", i === currentIndex));
                }
                function goToSlide(index) { currentIndex = index; updateSlides(); }
                function nextSlide() { currentIndex = (currentIndex + 1) % slides.length; updateSlides(); }
                function prevSlide() { currentIndex = (currentIndex - 1 + slides.length) % slides.length; updateSlides(); }
                function startAuto() {
                    stopAuto();
                    if (slides.length > 1) intervalId = setInterval(nextSlide, AUTO_MS);
                }
                function stopAuto() {
                    if (intervalId) clearInterval(intervalId);
                    intervalId = null;
                }
                buildDots();
                updateSlides();
                startAuto();
                prevBtn?.addEventListener("click", prevSlide);
                nextBtn?.addEventListener("click", nextSlide);
                const slider = document.querySelector(".job_highlight_slider");
                slider?.addEventListener("mouseenter", stopAuto);
                slider?.addEventListener("mouseleave", startAuto);
            });
        </script>
    </body>
</html>
