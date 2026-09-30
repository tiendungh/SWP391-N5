<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1"/>
        <title>${job.title} - Tuyển dụng</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/public.css">
    </head>
    <body class="pb-body">
        <div class="pb-topnav">
            <a class="pb-logo" href="${pageContext.request.contextPath}/jobs">JOB<span>BOARD</span></a>
            <a class="pb-nav-link" href="${pageContext.request.contextPath}/jobs">← Tất cả vị trí</a>
        </div>
        <div class="pb-hero">
            <h1>${job.title}</h1>
            <p>${job.category} · ${job.location}</p>
        </div>

        <div class="pb-wrap">
            <div class="pb-card pb-detail">
                <div class="pb-meta">
                    <span class="pb-tag">${job.category}</span>
                    <c:if test="${not empty job.typeJob}"><span class="pb-tag teal">${job.typeJob}</span></c:if>
                    &nbsp;&nbsp;Hạn nộp: <fmt:formatDate value="${job.deadline}" pattern="dd/MM/yyyy"/>
                    <c:if test="${not empty job.numberExp}"> · Yêu cầu ≥ ${job.numberExp} năm kinh nghiệm</c:if>
                </div>

                <c:if test="${not empty job.offerMin}">
                    <p class="pb-salary">${job.offerMin} - ${job.offerMax}</p>
                </c:if>

                <div class="pb-section-title">Mô tả công việc</div>
                <div class="pb-desc">${job.description}</div>

                <div style="margin-top:26px;">
                    <a class="pb-btn" href="${pageContext.request.contextPath}/apply?jobPostId=${job.jobPostId}">Ứng tuyển ngay</a>
                </div>
            </div>
        </div>
    </body>
</html>
