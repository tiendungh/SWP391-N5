<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1"/>
        <title>Tuyển dụng - Vị trí đang mở</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/public.css">
    </head>
    <body class="pb-body">
        <div class="pb-topnav">
            <a class="pb-logo" href="${pageContext.request.contextPath}/jobs">JOB<span>BOARD</span></a>
            <a class="pb-nav-link" href="${pageContext.request.contextPath}/login.jsp">Đăng nhập HR</a>
        </div>
        <div class="pb-hero">
            <h1>Cơ hội nghề nghiệp đang mở</h1>
            <p>Khám phá các vị trí đang tuyển và ứng tuyển trực tiếp, không cần tạo tài khoản.</p>
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
