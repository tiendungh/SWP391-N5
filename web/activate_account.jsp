<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Kích hoạt tài khoản - JOB BOARD Enterprise</title>
        <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/img/favicon.svg">
        <!-- Google Fonts: Inter -->
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">

        <link rel="stylesheet" href="css/bootstrap.min.css">
        <link rel="stylesheet" href="css/font-awesome.min.css">
        <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="css/custom.css">
    </head>
    <body>
        <jsp:include page="header.jsp"/>

        <div class="container" style="max-width:580px;padding:120px 20px 100px;text-align:center;">
            <c:choose>
                <c:when test="${activated}">
                    <div style="font-size:64px;color:#3F88C5;margin-bottom:18px;"><i class="fa fa-check-circle"></i></div>
                    <h2 style="font-weight:900;color:#0f2742;margin-bottom:12px;">Kích hoạt tài khoản thành công!</h2>
                    <p style="color:#56728f;margin-bottom:28px;line-height:1.65;">
                        Tài khoản nhân sự của bạn đã sẵn sàng. Bạn có thể đăng nhập ngay bằng email và mật khẩu tạm thời đã được gửi trong hòm thư.
                    </p>
                    <a href="login.jsp" class="boxed-btn3">Đăng nhập cổng nhân sự</a>
                </c:when>
                <c:otherwise>
                    <div style="font-size:56px;color:#ef4444;margin-bottom:18px;"><i class="fa fa-times-circle"></i></div>
                    <h2 style="font-weight:900;color:#0f172a;margin-bottom:12px;">Liên kết không hợp lệ hoặc đã hết hạn</h2>
                    <p style="color:#64748b;margin-bottom:28px;">
                        Liên kết kích hoạt chỉ có hiệu lực trong 3 ngày và chỉ dùng được một lần.
                        Vui lòng liên hệ quản trị viên để được cấp lại tài khoản.
                    </p>
                    <a href="home" class="boxed-btn3">Về trang chủ</a>
                </c:otherwise>
            </c:choose>
        </div>

        <jsp:include page="footer.jsp"/>
    </body>
</html>
