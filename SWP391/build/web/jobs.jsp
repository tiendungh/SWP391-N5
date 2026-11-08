<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1"/>
        <title>Tuyển dụng - Vị trí đang mở</title>
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
                <a class="pb-nav-link" href="${pageContext.request.contextPath}/home">&larr; Về trang chủ</a>
                <a class="pb-nav-btn" href="${pageContext.request.contextPath}/login.jsp">Cổng nội bộ HR</a>
            </div>
        </div>
        <div class="pb-hero">
            <div class="pb-hero-content">
                <span class="pb-hero-kicker">CƠ HỘI NGHỀ NGHIỆP</span>
                <h1>Các vị trí tuyển dụng đang mở</h1>
                <p>Khám phá môi trường làm việc chuẩn mực và ứng tuyển trực tiếp hồ sơ nhanh chóng.</p>
            </div>
        </div>

        <div class="pb-wrap">
            <div class="pb-card">
                <c:choose>
                    <c:when test="${empty jobList}">
                        <div class="pb-empty">Hiện chưa có vị trí nào đang mở. Vui lòng quay lại sau.</div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="j" items="${jobList}">
                            <div class="pb-job-item">
                                <div>
                                    <h3>${j.title}</h3>
                                    <div class="pb-meta">${j.location} · Hạn nộp: <fmt:formatDate value="${j.deadline}" pattern="dd/MM/yyyy"/></div>
                                    <div class="pb-tags">
                                        <span class="pb-tag">${j.category}</span>
                                        <c:if test="${not empty j.typeJob}"><span class="pb-tag teal">${j.typeJob}</span></c:if>
                                    </div>
                                </div>
                                <a class="pb-btn" href="${pageContext.request.contextPath}/jobs?id=${j.jobPostId}">Xem chi tiết</a>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </body>
</html>
