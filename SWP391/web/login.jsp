<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>Đăng nhập - JOB BOARD</title>
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/img/favicon.svg">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/font-awesome.min.css">
        <style>
            * { box-sizing: border-box; margin: 0; padding: 0; }
            html, body { min-height: 100%; }
            body {
                font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                background: linear-gradient(145deg, #071524 0%, #0d2238 50%, #132f4c 100%);
                position: relative;
                overflow-x: hidden;
                display: flex;
                align-items: center;
                justify-content: center;
                min-height: 100vh;
                padding: 24px;
                letter-spacing: -0.2px;
            }
            /* Ambient glowing spheres for glassmorphism */
            body:before {
                content: "";
                position: absolute;
                top: 15%;
                left: 20%;
                width: 450px;
                height: 450px;
                border-radius: 50%;
                background: radial-gradient(circle, rgba(63, 136, 197, 0.32) 0%, transparent 65%);
                filter: blur(50px);
                pointer-events: none;
            }
            body:after {
                content: "";
                position: absolute;
                bottom: 15%;
                right: 20%;
                width: 400px;
                height: 400px;
                border-radius: 50%;
                background: radial-gradient(circle, rgba(91, 160, 214, 0.22) 0%, transparent 70%);
                filter: blur(60px);
                pointer-events: none;
            }
            .login-container {
                position: relative;
                z-index: 2;
                width: 440px;
                max-width: 94vw;
            }
            /* Frosted Glass Login Card */
            .login-card {
                background: rgba(255, 255, 255, 0.85);
                backdrop-filter: blur(24px) saturate(180%);
                -webkit-backdrop-filter: blur(24px) saturate(180%);
                border: 1px solid rgba(255, 255, 255, 0.85);
                border-radius: 26px;
                padding: 44px 40px;
                box-shadow: 0 30px 60px rgba(7, 21, 36, 0.45), inset 0 1px 1.5px rgba(255, 255, 255, 0.95);
            }
            .login-brand {
                display: flex;
                align-items: center;
                justify-content: center;
                margin-bottom: 24px;
            }
            .login-brand img {
                height: 42px;
                width: auto;
            }
            .login-header {
                text-align: center;
                margin-bottom: 26px;
            }
            .login-header h1 {
                font-size: 24px;
                color: #0f2742;
                font-weight: 900;
                letter-spacing: -0.5px;
                margin-bottom: 8px;
            }
            .login-header p {
                font-size: 13.5px;
                color: #56728f;
                line-height: 1.6;
            }
            .input-group {
                margin-bottom: 16px;
            }
            .input-group label {
                display: block;
                font-size: 12.5px;
                font-weight: 700;
                color: #1e3a5a;
                margin-bottom: 6px;
            }
            input {
                background: rgba(255, 255, 255, 0.9);
                border: 1.5px solid rgba(63, 136, 197, 0.22);
                padding: 13px 16px;
                width: 100%;
                border-radius: 12px;
                font-size: 14px;
                font-family: inherit;
                color: #0f2742;
                outline: none;
                transition: 0.2s ease;
            }
            input:focus {
                background: #ffffff;
                border-color: #3F88C5;
                box-shadow: 0 0 0 3.5px rgba(63, 136, 197, 0.25);
            }
            /* Glass Action Button */
            button.btn-login {
                width: 100%;
                border-radius: 12px;
                border: 1px solid rgba(255, 255, 255, 0.35);
                background: linear-gradient(135deg, #3F88C5, #2a6ea8);
                color: #ffffff;
                font-size: 13.5px;
                font-weight: 800;
                padding: 14px;
                letter-spacing: 0.5px;
                text-transform: uppercase;
                transition: all 0.25s ease;
                cursor: pointer;
                margin: 18px 0 10px;
                box-shadow: 0 10px 24px rgba(63, 136, 197, 0.35);
            }
            button.btn-login:hover {
                background: linear-gradient(135deg, #5ba0d6, #3F88C5);
                transform: translateY(-2px);
                box-shadow: 0 14px 30px rgba(63, 136, 197, 0.42);
            }
            .login-links {
                display: flex;
                flex-direction: column;
                gap: 10px;
                align-items: center;
                margin-top: 14px;
                padding-top: 16px;
                border-top: 1px solid rgba(63, 136, 197, 0.15);
            }
            .login-links a {
                color: #3F88C5;
                font-size: 13px;
                font-weight: 600;
                text-decoration: none;
                transition: 0.15s ease;
            }
            .login-links a:hover {
                color: #2a6ea8;
                text-decoration: underline;
            }
            .admin-link {
                color: #56728f !important;
                font-size: 13px;
            }
            .admin-link:hover { color: #0f2742 !important; }
            #notificationTab {
                position: fixed;
                top: 24px;
                right: 24px;
                background: rgba(42, 110, 168, 0.95);
                backdrop-filter: blur(12px);
                border: 1px solid rgba(255, 255, 255, 0.2);
                color: #ffffff;
                padding: 14px 20px;
                border-radius: 14px;
                box-shadow: 0 14px 35px rgba(7, 21, 36, 0.3);
                z-index: 1000;
                font-size: 14px;
                font-weight: 600;
            }
        </style>
    </head>
    <body>
        <c:if test="${not empty param.success}">
            <div id="notificationTab"><i class="fa fa-check-circle"></i> ${param.success}</div>
        </c:if>
        <c:if test="${status!=null}">
            <div id="notificationTab"><i class="fa fa-info-circle"></i> ${status}</div>
        </c:if>

        <div class="login-container">
            <div class="login-card">
                <div class="login-brand">
                    <img src="${pageContext.request.contextPath}/img/logo.svg" alt="Job Board Logo">
                </div>
                <div class="login-header">
                    <h1>Cổng Đăng Nhập Nhân Sự</h1>
                    <p>Dành cho HR Staff &amp; Manager. Vui lòng sử dụng tài khoản được cấp bởi Quản trị viên.</p>
                </div>
                <form action="login" method="post" autocomplete="off">
                    <input type="hidden" name="role" value="candidate"/>
                    <div class="input-group">
                        <label>Địa chỉ Email</label>
                        <input type="text" name="email" placeholder="tennhanvien@company.com" value="${username}" required />
                    </div>
                    <div class="input-group">
                        <label>Mật khẩu</label>
                        <input type="password" name="password" placeholder="••••••••" required />
                    </div>
                    <button type="submit" class="btn-login">Đăng nhập hệ thống</button>
                </form>
                <div class="login-links">
                    <a href="<%= request.getContextPath() %>/forget_password.jsp">Quên mật khẩu?</a>
                    <a class="admin-link" href="<%= request.getContextPath() %>/admin/login">Cổng đăng nhập Quản trị viên (Admin)</a>
                    <a class="admin-link" href="<%= request.getContextPath() %>/home">&larr; Quay về Trang chủ</a>
                </div>
            </div>
        </div>

        <script>
            window.addEventListener('DOMContentLoaded', function () {
                var noti = document.getElementById('notificationTab');
                if (noti) {
                    setTimeout(function () {
                        noti.style.display = "none";
                    }, 4000);
                }
            });
        </script>
    </body>
</html>
