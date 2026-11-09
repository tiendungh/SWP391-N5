<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<header>
    <div class="header-area">
        <div id="sticky-header" class="main-header-area">
            <div class="container">
                <div class="header_bottom_border">
                    <div class="row align-items-center">
                        <div class="col-xl-3 col-lg-2 col-7">
                            <div class="logo">
                                <c:choose>
                                    <c:when test="${sessionScope.role eq 'Employer'}">
                                        <a href="employerServices" class="brand-link">
                                            <img src="${pageContext.request.contextPath}/img/logo.svg" alt="Job Board Logo" class="brand-logo-img">
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="home" class="brand-link">
                                            <img src="${pageContext.request.contextPath}/img/logo.svg" alt="Job Board Logo" class="brand-logo-img">
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="col-xl-6 col-lg-7 d-none d-lg-block">
                            <div class="main-menu">
                                <nav>
                                    <ul id="navigation">
                                        <c:choose>
                                            <c:when test="${sessionScope.role eq 'Employer'}">
                                                <li><a href="employerServices">Dịch vụ</a></li>
                                                <li><a href="employerWall">Wall</a></li>
                                                <li><a href="job_add">Đăng công việc</a></li>
                                                <li><a href="employer_jobs">Xem công việc</a></li>
                                                <li><a href="viewPublicCVs">Tìm ứng viên</a></li>
                                                </c:when>
                                                <c:otherwise>
                                                <li><a href="home">Trang chủ</a></li>
                                                <li><a href="jobs">Việc làm</a>
                                                </li>
                                                <c:if test="${not empty sessionScope.user}">
                                                    <li><a href="${pageContext.request.contextPath}/leader-room?tab=progress">Tiến độ phòng ban</a></li>
                                                </c:if>
                                            </c:otherwise>
                                        </c:choose>
                                    </ul>
                                </nav>
                            </div>
                        </div>
                        <div class="col-xl-3 col-lg-3 col-5">
                            <div class="Appointment justify-content-end">
                                <c:if test="${empty sessionScope.user}">
                                    <div class="phone_num d-none d-sm-block"><a class="boxed-btn3" href="login.jsp">Đăng nhập</a></div>
                                </c:if>
                                <c:if test="${not empty sessionScope.user}">
                                    <div class="main-menu phone_num d-none d-sm-block">
                                        <nav>
                                            <ul id="navigation">
                                                <li>
                                                    <c:choose>
                                                        <c:when test="${sessionScope.role eq 'Candidate'}"><a href="candidateProfile"><b>Xin chào, ${sessionScope.user.candidateName}</b></a></c:when>
                                                        <c:when test="${sessionScope.role eq 'Employer'}"><a href="employerProfile"><b>Xin chào, ${sessionScope.user.employerName}</b></a></c:when>
                                                        <c:otherwise><a href="#"><b>${sessionScope.user.username}</b></a></c:otherwise>
                                                            </c:choose>
                                                    <ul class="submenu">
                                                        <c:choose>
                                                            <c:when test="${sessionScope.role eq 'Candidate'}"><li><a href="candidateProfile">Xem hồ sơ</a></li></c:when>
                                                            <c:when test="${sessionScope.role eq 'Employer'}"><li><a href="employerProfile">Xem hồ sơ</a></li><li><a href="payments_history">Lịch sử giao dịch</a></li></c:when>
                                                            </c:choose>
                                                        <%-- Tùy chọn Tiến độ phòng ban trong dropdown profile cá nhân --%>
                                                        <li><a href="${pageContext.request.contextPath}/leader-room?tab=progress">Tiến độ phòng ban</a></li>
                                                        <li><a href="logout">Đăng xuất</a></li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </nav>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                        <div class="col-12"><div class="mobile_menu d-block d-lg-none"></div></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</header>
