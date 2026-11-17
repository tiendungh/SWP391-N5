<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Yêu cầu tạo tài khoản" scope="request"/>
<c:set var="currentNav" value="requestAccount" scope="request"/>
<jsp:include page="staff_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Yêu cầu Admin tạo tài khoản</h1>
        <p>Chỉ HR Staff mới có thể gửi yêu cầu này. Bạn không thể tự tạo tài khoản nhân viên - hãy gửi yêu cầu kèm email, Admin sẽ xem xét và tạo tài khoản.</p>
    </div>
    <div class="iv-whoami">Xin chào, <b>${sessionScope.staffName}</b></div>
</div>

<c:if test="${not empty error}"><div class="iv-alert iv-alert-error"><c:out value="${error}"/></div></c:if>
<c:if test="${param.sent == '1'}"><div class="iv-alert iv-alert-success">Đã gửi yêu cầu tới Admin. Bạn có thể theo dõi trạng thái ở bảng bên dưới.</div></c:if>

<div class="iv-card">
    <h2>Gửi yêu cầu mới</h2>
    <form method="post" action="${pageContext.request.contextPath}/request-account">
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Email cần tạo tài khoản *</label>
                <input type="email" name="requestedEmail" placeholder="hegaf39403@art2mart.com" maxlength="100" required/>
            </div>
            <div class="iv-field">
                <label>Họ và tên</label>
                <input type="text" name="fullName" placeholder="Nguyễn Văn A" maxlength="100"/>
            </div>
        </div>
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Số điện thoại</label>
                <input type="text" name="phone" placeholder="09xxxxxxxx" pattern="\d{10}" title="Số điện thoại phải gồm đúng 10 chữ số"/>
            </div>
            <div class="iv-field">
                <label>Vai trò đề xuất</label>
                <select name="roleId">
                    <option value="">-- Để Admin chọn --</option>
                    <c:forEach var="r" items="${roles}">
                        <option value="${r.roleId}">${r.roleName}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Địa chỉ</label>
                <input type="text" name="address" placeholder="Vd: 123 Nguyễn Trãi, Hà Nội" maxlength="100"/>
            </div>
            <div class="iv-field">
                <label>Quốc tịch</label>
                <input type="text" name="nationality" placeholder="Vd: Việt Nam" maxlength="100"/>
            </div>
        </div>
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Phòng ban</label>
                <input type="text" name="department" placeholder="Vd: IT, Human Resources" maxlength="100"/>
            </div>
            <div class="iv-field">
                <label>Chức danh</label>
                <input type="text" name="position" placeholder="Vd: Software Engineer" maxlength="100"/>
            </div>
        </div>
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Lương khởi điểm (tùy chọn)</label>
                <input type="number" name="salary" min="0" step="0.01" placeholder="Vd: 1500"/>
            </div>
        </div>
        <div class="iv-field" style="margin-bottom:14px;">
            <label>Ghi chú cho Admin</label>
            <textarea name="note" placeholder="Vd: nhân viên mới của phòng Kinh doanh, cần tài khoản trước thứ Hai" maxlength="100"></textarea>
        </div>

        <div style="margin-top:18px;">
            <button type="submit" class="iv-btn">Gửi yêu cầu</button>
        </div>
    </form>
</div>

<div class="iv-card">
    <h2>Yêu cầu của tôi (${myRequests.size()})</h2>
    <c:choose>
        <c:when test="${empty myRequests}">
            <div class="iv-empty">Bạn chưa gửi yêu cầu nào.</div>
        </c:when>
        <c:otherwise>
            <table class="iv-table">
                <thead><tr><th>Email</th><th>Trạng thái</th><th>Ngày gửi</th><th>Phản hồi từ Admin</th></tr></thead>
                <tbody>
                    <c:forEach var="req" items="${myRequests}">
                        <tr>
                            <td><c:out value="${req.requestedEmail}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${req.status == 'Pending'}"><span class="iv-badge iv-badge-amber">Đang chờ</span></c:when>
                                    <c:when test="${req.status == 'Approved'}"><span class="iv-badge iv-badge-green">Đã duyệt</span></c:when>
                                    <c:otherwise><span class="iv-badge iv-badge-red">Đã từ chối</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td><fmt:formatDate value="${req.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td class="iv-small"><c:out value="${req.rejectionNote}"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="staff_footer.jsp"/>
