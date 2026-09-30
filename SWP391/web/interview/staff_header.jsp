<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>${empty pageTitle ? 'Nhân sự' : pageTitle} - HR Tools</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/interview.css">
    </head>
    <body class="iv-body">
        <div class="iv-shell">
            <aside class="iv-sidebar">
                <div class="iv-logo">JOB<span>BOARD</span> · HR</div>
                <nav class="iv-nav">
                    <c:if test="${sessionScope.roleName == 'HR Staff' || sessionScope.roleName == 'Manager'}">
                        <a href="${pageContext.request.contextPath}/job-post"
                           class="${currentNav == 'jobpost' ? 'active' : ''}">📝 Tạo JD</a>
                        <a href="${pageContext.request.contextPath}/screening"
                           class="${currentNav == 'screening' ? 'active' : ''}">🔎 Sơ lọc hồ sơ</a>
                        <a href="${pageContext.request.contextPath}/interview-schedule"
                           class="${currentNav == 'schedule' ? 'active' : ''}">📅 Lên lịch phỏng vấn</a>
                        <a href="${pageContext.request.contextPath}/interview-calendar"
                           class="${currentNav == 'calendar' ? 'active' : ''}">🗓️ Lịch phỏng vấn</a>
                        <a href="${pageContext.request.contextPath}/barem"
                           class="${currentNav == 'barem' ? 'active' : ''}">📋 Barem đánh giá</a>
                        <a href="${pageContext.request.contextPath}/employee-profiles"
                           class="${currentNav == 'employeeProfiles' ? 'active' : ''}">🗂️ Hồ sơ nhân viên</a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/my-profile-documents"
                       class="${currentNav == 'myProfile' ? 'active' : ''}">👤 Hồ sơ của tôi</a>
                </nav>
                <div class="iv-sidebar-footer">
                    Đăng nhập với<br>
                    <b style="color:#fff"><c:out value="${sessionScope.staffName}"/></b>
                    (<c:out value="${sessionScope.roleName}"/>)<br>
                    <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                </div>
            </aside>
            <main class="iv-main">
