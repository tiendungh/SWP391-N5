<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Test kích hoạt" scope="request"/>
<c:set var="currentNav" value="test-activation" scope="request"/>
<jsp:include page="admin_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Kiểm tra kích hoạt tài khoản</h1>
        <p>Dùng để test luồng kích hoạt (activate-account) khi chưa cấu hình được SMTP hoặc muốn kiểm tra nhanh trạng thái tài khoản.</p>
    </div>
    <div class="iv-whoami">Xin chào, <c:out value="${sessionScope.adminUsername}"/></div>
</div>

<c:if test="${not empty checkError}"><div class="iv-alert iv-alert-error">${checkError}</div></c:if>

<div class="iv-card">
    <h2>1. Kiểm tra trạng thái theo email</h2>
    <form method="post" action="${pageContext.request.contextPath}/admin/test-activation">
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Email nhân viên</label>
                <input type="email" name="email" placeholder="employee@email.com" value="${checkedEmail}" required/>
            </div>
        </div>
        <div style="margin-top:18px;">
            <button type="submit" class="iv-btn">Kiểm tra</button>
        </div>
    </form>

    <c:if test="${not empty checkedEmail}">
        <div style="margin-top:16px;">
            <c:choose>
                <c:when test="${isActive == null}">
                    <div class="iv-alert iv-alert-error">Không tìm thấy Employee với email "<c:out value="${checkedEmail}"/>".</div>
                </c:when>
                <c:when test="${isActive}">
                    <div class="iv-alert iv-alert-success">Tài khoản "<c:out value="${checkedEmail}"/>" đang <b>ĐÃ KÍCH HOẠT</b>.</div>
                </c:when>
                <c:otherwise>
                    <div class="iv-alert iv-alert-error">Tài khoản "<c:out value="${checkedEmail}"/>" đang <b>CHƯA KÍCH HOẠT</b>.</div>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>
</div>

<div class="iv-card" style="margin-top:20px;">
    <h2>2. Test trực tiếp bằng token</h2>
    <p style="color:#64748b;font-size:13px;">
        Nếu bạn có link kích hoạt (hiển thị sau khi tạo tài khoản ở trang "Tạo tài khoản" khi email chưa gửi được),
        dán token vào đây để mở thẳng trang activate-account.
    </p>
    <form method="get" action="${pageContext.request.contextPath}/activate-account" target="_blank">
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Token kích hoạt</label>
                <input type="text" name="token" placeholder="token từ link email" required/>
            </div>
        </div>
        <div style="margin-top:18px;">
            <button type="submit" class="iv-btn">Mở trang kích hoạt</button>
        </div>
    </form>
</div>

<jsp:include page="admin_footer.jsp"/>
