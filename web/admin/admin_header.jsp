<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>${empty pageTitle ? 'Quản trị' : pageTitle} - Admin</title>
        <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/img/favicon.svg">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/interview.css">
    </head>
    <body class="iv-body">
        <div class="iv-shell">
            <aside class="iv-sidebar">
                <div class="iv-logo">
                    <a href="${pageContext.request.contextPath}/home" style="text-decoration:none;">
                        <img src="${pageContext.request.contextPath}/img/logo-white.svg" alt="Job Board Logo" class="iv-logo-img">
                    </a>
                    <span class="iv-logo-badge admin">ADMIN PORTAL</span>
                </div>
                <nav class="iv-nav">
                    <a href="${pageContext.request.contextPath}/admin/create-account"
                       class="${currentNav == 'create-account' ? 'active' : ''}">Tạo tài khoản</a>
                    <a href="${pageContext.request.contextPath}/admin/manage-accounts"
                       class="${currentNav == 'manage-accounts' ? 'active' : ''}">Quản lý tài khoản</a>
                    <a href="${pageContext.request.contextPath}/admin/account-requests"
                       class="${currentNav == 'account-requests' ? 'active' : ''}">Yêu cầu tạo tài khoản</a>
                    <a href="${pageContext.request.contextPath}/admin/test-activation"
                       class="${currentNav == 'test-activation' ? 'active' : ''}">Test kích hoạt</a>
                </nav>
                <div class="iv-sidebar-footer">
                    Đăng nhập với<br>
                    <b style="color:#fff"><c:out value="${sessionScope.adminUsername}"/></b><br>
                    <a href="${pageContext.request.contextPath}/admin/logout">Đăng xuất</a>
                </div>
            </aside>
            <main class="iv-main">
