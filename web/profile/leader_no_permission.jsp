<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Không có thẩm quyền thao tác" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<style>
    .no-perm-wrapper {
        max-width: 680px;
        margin: 60px auto;
        background: #ffffff;
        border: 1px solid #e2e8f0;
        border-radius: 16px;
        padding: 48px 36px;
        text-align: center;
        box-shadow: 0 10px 25px -5px rgba(15, 23, 42, 0.06);
    }
    .no-perm-icon {
        width: 76px;
        height: 76px;
        margin: 0 auto 20px auto;
        background: #fef2f2;
        color: #dc2626;
        border: 2px solid #fecaca;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 2.2rem;
    }
    .no-perm-title {
        margin: 0 0 10px 0;
        font-size: 1.55rem;
        font-weight: 800;
        color: #0f172a;
    }
    .no-perm-desc {
        margin: 0 0 28px 0;
        font-size: 0.98rem;
        color: #64748b;
        line-height: 1.6;
    }
    .btn-back-home {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        background: #2563eb;
        color: #ffffff;
        padding: 12px 26px;
        border-radius: 10px;
        text-decoration: none;
        font-weight: 700;
        font-size: 0.95rem;
        transition: background 0.2s ease, transform 0.1s ease;
        box-shadow: 0 4px 12px rgba(37, 99, 235, 0.25);
    }
    .btn-back-home:hover {
        background: #1d4ed8;
        transform: translateY(-1px);
    }
</style>

<div class="no-perm-wrapper">
    <div class="no-perm-icon">
        <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
            <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
        </svg>
    </div>
    
    <h2 class="no-perm-title">Bạn không có thẩm quyền thao tác</h2>
    <p class="no-perm-desc">
        Chức năng này chỉ dành cho <b>Trưởng phòng (Leader)</b> hoặc <b>Quản lý (Manager)</b>.<br/>
        Tài khoản nhân viên của bạn chỉ có quyền theo dõi mục <b>Tổng kết tiến độ</b> của phòng ban.
    </p>

    <a href="${pageContext.request.contextPath}/leader-room?tab=progress" class="btn-back-home">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <line x1="19" y1="12" x2="5" y2="12"></line>
            <polyline points="12 19 5 12 12 5"></polyline>
        </svg>
        Quay lại
    </a>
</div>

<jsp:include page="/interview/staff_footer.jsp"/>