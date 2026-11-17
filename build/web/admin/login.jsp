<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Đăng nhập Quản trị viên - JOB BOARD Admin</title>
        <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/img/favicon.svg">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">
        <style>
            * { box-sizing: border-box; margin: 0; padding: 0; }
            html, body { min-height: 100%; }
            body {
                font-family: 'Be Vietnam Pro', 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                background: linear-gradient(145deg, #071524 0%, #0d2238 50%, #132f4c 100%);
                display: flex;
                align-items: center;
                justify-content: center;
                min-height: 100vh;
                padding: 24px;
                position: relative;
                letter-spacing: -0.2px;
            }
            body:before {
                content: "";
                position: absolute;
                top: 20%;
                left: 20%;
                width: 450px;
                height: 450px;
                border-radius: 50%;
                background: radial-gradient(circle, rgba(63, 136, 197, 0.3) 0%, transparent 65%);
                filter: blur(50px);
                pointer-events: none;
            }
            .admin-login-container {
                position: relative;
                z-index: 2;
                width: 420px;
                max-width: 94vw;
            }
            .admin-login-card {
                background: rgba(255, 255, 255, 0.85);
                backdrop-filter: blur(24px) saturate(180%);
                -webkit-backdrop-filter: blur(24px) saturate(180%);
                border: 1px solid rgba(255, 255, 255, 0.85);
                border-radius: 26px;
                box-shadow: 0 30px 60px rgba(7, 21, 36, 0.45), inset 0 1px 1.5px rgba(255, 255, 255, 0.95);
                padding: 42px 38px;
            }
            .admin-brand {
                display: flex;
                align-items: center;
                justify-content: center;
                margin-bottom: 22px;
            }
            .admin-brand img {
                height: 40px;
                width: auto;
            }
            .admin-badge {
                display: inline-flex;
                align-items: center;
                gap: 5px;
                padding: 4px 12px;
                border-radius: 999px;
                background: rgba(63, 136, 197, 0.15);
                color: #2a6ea8;
                border: 1px solid rgba(63, 136, 197, 0.25);
                font-size: 11px;
                font-weight: 800;
                letter-spacing: 0.8px;
                text-transform: uppercase;
                margin-bottom: 8px;
            }
            .admin-login-card h1 {
                margin: 0 0 6px;
                font-size: 22px;
                font-weight: 900;
                color: #0f2742;
                letter-spacing: -0.4px;
            }
            .admin-login-card p {
                margin: 0 0 22px;
                font-size: 13px;
                color: #56728f;
                line-height: 1.55;
            }
            .input-group {
                margin-bottom: 14px;
            }
            .input-group label {
                display: block;
                font-size: 12.5px;
                font-weight: 700;
                color: #1e3a5a;
                margin-bottom: 6px;
            }
            .admin-login-card input {
                width: 100%;
                padding: 13px 15px;
                border-radius: 12px;
                border: 1.5px solid rgba(63, 136, 197, 0.22);
                background: rgba(255, 255, 255, 0.9);
                font-size: 14px;
                font-family: inherit;
                color: #0f2742;
                outline: none;
                transition: 0.2s ease;
            }
            .admin-login-card input:focus {
                border-color: #3F88C5;
                background: #ffffff;
                box-shadow: 0 0 0 3.5px rgba(63, 136, 197, 0.25);
            }
            .admin-login-card button {
                width: 100%;
                padding: 14px;
                border: 1px solid rgba(255, 255, 255, 0.35);
                border-radius: 12px;
                margin-top: 10px;
                background: linear-gradient(135deg, #3F88C5, #2a6ea8);
                color: #ffffff;
                font-weight: 800;
                font-size: 13px;
                letter-spacing: 0.5px;
                text-transform: uppercase;
                cursor: pointer;
                transition: all 0.25s ease;
                box-shadow: 0 10px 24px rgba(63, 136, 197, 0.35);
            }
            .admin-login-card button:hover {
                background: linear-gradient(135deg, #5ba0d6, #3F88C5);
                transform: translateY(-2px);
                box-shadow: 0 14px 30px rgba(63, 136, 197, 0.42);
            }
            .admin-error {
                background: #fef2f2;
                color: #dc2626;
                border: 1px solid #fecaca;
                border-radius: 10px;
                padding: 11px 14px;
                font-size: 13px;
                font-weight: 600;
                margin-bottom: 16px;
            }
            .back-link {
                margin-top: 18px;
                text-align: center;
                padding-top: 16px;
                border-top: 1px solid rgba(63, 136, 197, 0.15);
            }
            .back-link a {
                color: #56728f;
                font-size: 13px;
                font-weight: 600;
                text-decoration: none;
                transition: 0.15s ease;
            }
            .back-link a:hover {
                color: #0f2742;
                text-decoration: underline;
            }
        </style>
    </head>
    <body>
        <div class="admin-login-container">
            <div class="admin-login-card">
                <div class="admin-brand">
                    <img src="${pageContext.request.contextPath}/img/logo.svg" alt="Job Board Logo">
                </div>
                <div style="text-align:center;">
                    <span class="admin-badge">Admin Portal</span>
                    <h1>Cổng Quản Trị Hệ Thống</h1>
                    <p>Xác thực quyền quản trị viên cao nhất để quản lý người dùng và phân quyền hệ thống.</p>
                </div>

                <c:if test="${not empty error}">
                    <div class="admin-error">${error}</div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/admin/login">
                    <div class="input-group">
                        <label>Tên đăng nhập Admin</label>
                        <input type="text" name="username" placeholder="admin" value="${param.username}" required autofocus/>
                    </div>
                    <div class="input-group">
                        <label>Mật khẩu</label>
                        <input type="password" name="password" placeholder="••••••••" required/>
                    </div>
                    <button type="submit">Đăng nhập Quản trị</button>
                </form>
                <div class="back-link">
                    <a href="${pageContext.request.contextPath}/home">&larr; Quay về Trang chủ Tuyển dụng</a>
                </div>
            </div>
        </div>
    </body>
</html>
