<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1"/>
        <title>Nộp hồ sơ thành công - JOB BOARD</title>
        <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/img/favicon.svg">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/public.css">
    </head>
    <body class="pb-body">
        <div class="pb-topnav">
            <a class="pb-logo" href="${pageContext.request.contextPath}/home">
                <img src="${pageContext.request.contextPath}/img/logo-white.svg" alt="Job Board Logo">
            </a>
            <div class="pb-nav-links">
                <a class="pb-nav-link" href="${pageContext.request.contextPath}/home">&larr; Về trang chủ</a>
                <a class="pb-nav-btn" href="${pageContext.request.contextPath}/jobs">Xem vị trí khác</a>
            </div>
        </div>
        <div class="pb-hero">
            <div class="pb-hero-content" style="text-align: center; margin: 0 auto;">
                <span class="pb-hero-kicker">HOÀN TẤT ỨNG TUYỂN</span>
                <h1>Hồ sơ của bạn đã được ghi nhận thành công!</h1>
                <p>Cảm ơn bạn đã quan tâm và nộp hồ sơ. Đội ngũ nhân sự sẽ tiến hành thẩm định và liên hệ qua email/điện thoại trong vòng 3-5 ngày làm việc.</p>
            </div>
        </div>
        <div class="pb-wrap">
            <div class="pb-card" style="text-align:center; padding: 48px 30px;">
                <div style="width: 72px; height: 72px; border-radius: 50%; background: rgba(63, 136, 197, 0.15); border: 2px solid rgba(63, 136, 197, 0.35); color: #3F88C5; font-size: 32px; display: flex; align-items: center; justify-content: center; margin: 0 auto 20px; box-shadow: 0 8px 24px rgba(63, 136, 197, 0.25);">
                    ✓
                </div>
                <h2 style="font-size: 22px; color: #0f2742; margin-bottom: 10px; font-weight: 800;">Ứng tuyển thành công</h2>
                <p style="color: #64748b; font-size: 15px; max-width: 500px; margin: 0 auto 26px; line-height: 1.6;">
                    Bạn có thể theo dõi hòm thư điện tử để cập nhật lịch phỏng vấn và trạng thái sơ loại mới nhất.
                </p>
                <div style="display: flex; gap: 14px; justify-content: center; flex-wrap: wrap;">
                    <a class="pb-btn" href="${pageContext.request.contextPath}/jobs">Khám phá vị trí khác</a>
                    <a class="pb-btn pb-btn-ghost" href="${pageContext.request.contextPath}/home">Quay lại Trang chủ</a>
                </div>
            </div>
        </div>
    </body>
</html>
