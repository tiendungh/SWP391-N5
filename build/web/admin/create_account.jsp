<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Tạo tài khoản" scope="request"/>
<c:set var="currentNav" value="create-account" scope="request"/>
<jsp:include page="admin_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Tạo tài khoản nhân viên mới</h1>
        <p>Tài khoản sẽ ở trạng thái chưa kích hoạt cho đến khi người nhận bấm nút kích hoạt trong email.</p>
    </div>
    <div class="iv-whoami">Xin chào, <c:out value="${sessionScope.adminUsername}"/></div>
</div>

<c:if test="${not empty error}"><div class="iv-alert iv-alert-error">${error}</div></c:if>
<c:if test="${param.created == '1' && param.mailFailed != '1' && param.tokenFailed != '1'}">
    <div class="iv-alert iv-alert-success">Đã tạo tài khoản và gửi email kích hoạt thành công.</div>
</c:if>
<c:if test="${param.created == '1' && param.mailFailed == '1'}">
    <div class="iv-alert iv-alert-error">
        Đã tạo tài khoản, nhưng gửi email thất bại (kiểm tra cấu hình SMTP trong EmailSender.java).
        Vui lòng gửi thông tin đăng nhập cho người dùng theo cách khác.
    </div>
</c:if>
<c:if test="${param.created == '1' && param.tokenFailed == '1'}">
    <div class="iv-alert iv-alert-error">
        Đã tạo tài khoản, nhưng không lưu được liên kết kích hoạt. Vui lòng thử tạo lại hoặc kiểm tra database.
    </div>
</c:if>
<c:if test="${param.created == '1' && not empty param.link}">
    <div class="iv-alert iv-alert-success" style="word-break:break-all;">
        <b>Chỉ dùng để test (SMTP có thể chưa được cấu hình):</b><br/>
        Mật khẩu tạm thời: <code>${param.temp}</code><br/>
        Liên kết kích hoạt: <a href="${param.link}" target="_blank">${param.link}</a>
    </div>
</c:if>
<c:if test="${not empty prefill}">
    <div class="iv-alert iv-alert-success">
        Đang tạo tài khoản từ yêu cầu của Manager <b><c:out value="${prefill.requestedByName}"/></b>.
        Sau khi tạo, yêu cầu này sẽ được đánh dấu <b>Đã duyệt</b>.
    </div>
</c:if>

<div class="iv-card">
    <h2>Thông tin tài khoản</h2>
    <form method="post" action="${pageContext.request.contextPath}/admin/create-account">
        <c:if test="${not empty requestId}"><input type="hidden" name="requestId" value="${requestId}"/></c:if>
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Họ và tên *</label>
                <input type="text" name="fullName" placeholder="Nguyễn Văn A" value="${prefill.fullName}" maxlength="100" required/>
            </div>
            <div class="iv-field">
                <label>Email *</label>
                <input type="email" name="email" placeholder="employee@email.com" value="${prefill.requestedEmail}" maxlength="100" required/>
            </div>
        </div>
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Số điện thoại *</label>
                <input type="text" name="phone" placeholder="09xxxxxxxx" value="${prefill.phone}"
                       pattern="\d{10}" title="Số điện thoại phải gồm đúng 10 chữ số" required/>
            </div>
            <div class="iv-field">
                <label>Vai trò *</label>
                <select name="roleId" required>
                    <option value="">-- Chọn vai trò --</option>
                    <c:forEach var="r" items="${roles}">
                        <option value="${r.roleId}" ${not empty prefill && prefill.roleId == r.roleId ? 'selected' : ''}>${r.roleName}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Địa chỉ</label>
                <input type="text" name="address" placeholder="Vd: 123 Nguyễn Trãi, Hà Nội" value="${prefill.address}" maxlength="100"/>
            </div>
            <div class="iv-field">
                <label>Quốc tịch</label>
                <input type="text" name="nationality" placeholder="Vd: Việt Nam" value="${prefill.nationality}" maxlength="100"/>
            </div>
        </div>
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Phòng ban</label>
                <input type="text" name="department" placeholder="Vd: IT, Human Resources" value="${prefill.department}" maxlength="100"/>
            </div>
            <div class="iv-field">
                <label>Chức danh</label>
                <input type="text" name="position" placeholder="Vd: Software Engineer" value="${prefill.position}" maxlength="100"/>
            </div>
        </div>
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Lương khởi điểm (tùy chọn)</label>
                <input type="number" name="salary" min="0" step="0.01" placeholder="Vd: 1500" value="${prefill.salary}"/>
            </div>
        </div>
        <c:if test="${not empty prefill.note}">
            <div class="iv-field" style="margin-bottom:14px;">
                <label>Ghi chú từ Manager</label>
                <div class="iv-hint" style="font-size:13px;color:#334155;"><c:out value="${prefill.note}"/></div>
            </div>
        </c:if>

        <div style="margin-top:18px;">
            <button type="submit" class="iv-btn">Tạo tài khoản &amp; gửi email</button>
        </div>
    </form>
</div>

<jsp:include page="admin_footer.jsp"/>
