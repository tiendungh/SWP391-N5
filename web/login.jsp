<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>Đăng nhập & Đăng ký cho Ứng Viên</title>
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <style>
*{box-sizing:border-box}html,body{min-height:100%;margin:0}body{font-family:"Roboto",Arial,sans-serif;background:radial-gradient(circle at 15% 15%,rgba(37,99,235,.22),transparent 30%),radial-gradient(circle at 85% 85%,rgba(20,184,166,.18),transparent 30%),#0f172a;overflow-x:hidden}.container{width:900px;max-width:94vw;min-height:560px;margin:7vh auto;background:#fff;border-radius:24px;overflow:hidden;position:relative;box-shadow:0 30px 80px rgba(0,0,0,.30)}.form-container{position:absolute;top:0;height:100%;transition:.6s ease-in-out}.sign-in-container{left:0;width:50%;z-index:2}.container.right-panel-active .sign-in-container{transform:translateX(100%)}.sign-up-container{left:0;width:50%;opacity:0;z-index:1}.container.right-panel-active .sign-up-container{transform:translateX(100%);opacity:1;z-index:5;animation:show .6s}@keyframes show{0%,49.99%{opacity:0;z-index:1}50%,100%{opacity:1;z-index:5}}.overlay-container{position:absolute;top:0;left:50%;width:50%;height:100%;overflow:hidden;transition:.6s ease-in-out;z-index:100}.container.right-panel-active .overlay-container{transform:translateX(-100%)}.overlay{background:linear-gradient(145deg,#2563eb,#0f766e);position:relative;left:-100%;height:100%;width:200%;transform:translateX(0);transition:.6s ease-in-out;color:#fff}.container.right-panel-active .overlay{transform:translateX(50%)}.overlay-panel{position:absolute;display:flex;align-items:center;justify-content:center;flex-direction:column;padding:40px;text-align:center;top:0;height:100%;width:50%;transform:translateX(0);transition:.6s ease-in-out}.overlay-panel h1{color:#fff;font-size:32px;margin:0 0 14px;font-weight:900}.overlay-panel p{color:rgba(255,255,255,.84);line-height:1.7}.overlay-left{transform:translateX(-20%);left:0}.container.right-panel-active .overlay-left{transform:translateX(0)}.overlay-right{right:0}.container.right-panel-active .overlay-right{transform:translateX(20%)}form{background:#fff;display:flex;align-items:center;justify-content:center;flex-direction:column;padding:35px 55px;height:100%;text-align:center}form h1{font-size:30px;margin:0 0 8px;color:#0f172a;font-weight:900}form p{font-size:14px;color:#64748b;margin:0 0 16px}input,select{background:#f8fafc;border:1px solid #e2e8f0;padding:13px 15px;margin:6px 0;width:100%;border-radius:10px;font-size:14px;color:#334155;outline:none;transition:.2s}input:focus,select:focus{background:#fff;border-color:#60a5fa;box-shadow:0 0 0 4px rgba(37,99,235,.10)}button{border-radius:10px;border:0;background:linear-gradient(135deg,#2563eb,#3b82f6);color:#fff;font-size:12px;font-weight:800;padding:13px 30px;letter-spacing:.7px;text-transform:uppercase;transition:.2s;cursor:pointer;margin:12px 0;box-shadow:0 8px 18px rgba(37,99,235,.22)}button:hover{transform:translateY(-1px);box-shadow:0 12px 24px rgba(37,99,235,.28)}button.ghost{background:transparent;border:1px solid rgba(255,255,255,.65);box-shadow:none}button.ghost:hover{background:#fff;color:#2563eb}a{color:#2563eb;font-size:14px;text-decoration:none;margin:8px 0}a:hover{text-decoration:underline}#passwordError{color:#ef4444!important;font-size:12px!important;margin:4px 0!important}#notificationTab{position:fixed;top:22px;right:22px;background:#0f766e;color:#fff;padding:13px 18px;border-radius:12px;box-shadow:0 12px 30px rgba(15,118,110,.28);z-index:1000}#phoneContainer{width:100%}.validation-message{font-size:11px!important;margin:0!important;min-height:15px}.validation-message.success{color:#16a34a}.validation-message.error{color:#dc2626}@media(max-width:760px){.container{min-height:760px;margin:20px auto}.form-container,.sign-in-container,.sign-up-container{width:100%;position:absolute}.sign-in-container{height:58%}.sign-up-container{height:58%;top:42%}.overlay-container{left:0;top:58%;width:100%;height:42%}.overlay{left:0;width:100%}.overlay-panel{width:100%;padding:24px}.overlay-left{display:none}.container.right-panel-active .sign-in-container{transform:none;opacity:0}.container.right-panel-active .sign-up-container{transform:none;opacity:1}.container.right-panel-active .overlay-container{transform:none}.container.right-panel-active .overlay{transform:none}form{padding:28px 30px}form h1{font-size:25px}}
</style>
    </head>
    <body>
        <c:if test="${not empty param.success}">
            <div id="notificationTab">${param.success}</div>  
        </c:if>
        <div class="container" id="container">
            <div class="form-container sign-up-container">
                <form id="signupForm" action="register" method="post" autocomplete="off">
                    <input type="text" name="name" placeholder="Tên" value="${name}" required />
                    <input id="phone" type="text" name="phone" placeholder="Số điện thoại" value="${phone}" required />
                    <p id="phoneResult" class="validation-message"></p>
                    <input id="email" type="email" name="email" placeholder="Email" value="${email}" required />
                    <p id="emailResult" class="validation-message"></p>
                    <input type="password" id="password" name="password" placeholder="Mật khẩu" required />
                    <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Xác nhận mật khẩu" required />
                    <input type="hidden" name="role" value="candidate"/>
                    <div id="passwordError" style="display:none;">Mật khẩu không trùng khớp!</div>
                    <button type="submit">Đăng ký</button>
                </form>
            </div>
            <div class="form-container sign-in-container">
                <form action="login" method="post" autocomplete="off">
                    <input type="hidden" name="role" value="candidate"/>
                    <input type="text" name="email" placeholder="Email" value="${username}" required />
                    <input type="password" name="password" placeholder="Mật khẩu" required />
                    <button type="submit">Đăng nhập</button>
                    <a href="<%= request.getContextPath() %>/forget_password.jsp">Quên mật khẩu?</a>
                </form>
            </div>
            <div class="overlay-container">
                <div class="overlay">
                    <div class="overlay-panel overlay-left">
                        <h1>Chào mừng trở lại!</h1>
                        <button class="ghost" id="signIn">Đăng nhập</button>
                    </div>
                    <div class="overlay-panel overlay-right">
                        <h1>Xin chào!</h1>
                        <button class="ghost" id="signUp">Đăng ký</button>
                    </div>
                </div>
            </div>
        </div>
        <c:if test="${status!=null}">
            <div id="notificationTab">${status}</div>
        </c:if>
        <script>
            // Notification auto-hide
            window.addEventListener('DOMContentLoaded', function () {
                var noti = document.getElementById('notificationTab');
                if (noti) {
                    setTimeout(function () {
                        noti.style.display = "none";
                    }, 4000);
                }
            });

            // Toggle between sign in and sign up
            const signUpButton = document.getElementById('signUp');
            const signInButton = document.getElementById('signIn');
            const container = document.getElementById('container');
            
            signUpButton.addEventListener('click', () => {
                container.classList.add('right-panel-active');
            });
            
            signInButton.addEventListener('click', () => {
                container.classList.remove('right-panel-active');
            });

            // Password confirmation validation
            const signupForm = document.getElementById('signupForm');
            const password = document.getElementById('password');
            const confirmPassword = document.getElementById('confirmPassword');
            const passwordError = document.getElementById('passwordError');
            
            signupForm.addEventListener('submit', function (e) {
                if (password.value !== confirmPassword.value) {
                    passwordError.style.display = 'block';
                    e.preventDefault();
                    confirmPassword.focus();
                } else {
                    passwordError.style.display = 'none';
                }
            });
            
            // AJAX Email validation
            document.getElementById("email").addEventListener("keyup", function () {
                let email = this.value.trim();
                let resultElement = document.getElementById("emailResult");
                
                if (email.length === 0) {
                    resultElement.innerText = "";
                    resultElement.className = "validation-message";
                    return;
                }

                let xhr = new XMLHttpRequest();
                xhr.open("GET", "checkInput?type=email&value=" + encodeURIComponent(email), true);
                xhr.onload = function () {
                    if (xhr.status === 200) {
                        let response = xhr.responseText.trim();
                        resultElement.innerText = response;
                        
                        // Add appropriate class based on response
                        if (response.includes("đã tồn tại") || response.includes("không hợp lệ")) {
                            resultElement.className = "validation-message error";
                        } else {
                            resultElement.className = "validation-message success";
                        }
                    }
                };
                xhr.onerror = function() {
                    resultElement.innerText = "Lỗi kết nối";
                    resultElement.className = "validation-message error";
                };
                xhr.send();
            });

            // AJAX Phone validation
            document.getElementById("phone").addEventListener("keyup", function () {
                let phone = this.value.trim();
                let resultElement = document.getElementById("phoneResult");
                
                if (phone.length === 0) {
                    resultElement.innerText = "";
                    resultElement.className = "validation-message";
                    return;
                }

                let xhr = new XMLHttpRequest();
                xhr.open("GET", "checkInput?type=phone&value=" + encodeURIComponent(phone), true);
                xhr.onload = function () {
                    if (xhr.status === 200) {
                        let response = xhr.responseText.trim();
                        resultElement.innerText = response;
                        
                        // Add appropriate class based on response
                        if (response.includes("đã tồn tại") || response.includes("không hợp lệ")) {
                            resultElement.className = "validation-message error";
                        } else {
                            resultElement.className = "validation-message success";
                        }
                    }
                };
                xhr.onerror = function() {
                    resultElement.innerText = "Lỗi kết nối";
                    resultElement.className = "validation-message error";
                };
                xhr.send();
            });
        </script>
    </body>
</html>