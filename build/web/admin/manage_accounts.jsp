<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Quản lý tài khoản" scope="request"/>
<c:set var="currentNav" value="manage-accounts" scope="request"/>
<jsp:include page="admin_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Quản lý tài khoản nhân viên</h1>
        <p>Sửa thông tin, kích hoạt/vô hiệu hoá, đặt lại mật khẩu, hoặc xoá tài khoản.</p>
    </div>
    <div class="iv-whoami">Xin chào, <c:out value="${sessionScope.adminUsername}"/></div>
</div>

<c:if test="${not empty param.error}"><div class="iv-alert iv-alert-error"><c:out value="${param.error}"/></div></c:if>
<c:if test="${param.updated == '1'}"><div class="iv-alert iv-alert-success">Đã cập nhật tài khoản.</div></c:if>
<c:if test="${param.activated == '1'}"><div class="iv-alert iv-alert-success">Đã kích hoạt tài khoản.</div></c:if>
<c:if test="${param.deactivated == '1'}"><div class="iv-alert iv-alert-success">Đã vô hiệu hoá tài khoản.</div></c:if>
<c:if test="${param.deleted == '1'}"><div class="iv-alert iv-alert-success">Đã xoá tài khoản.</div></c:if>
<c:if test="${param.resetPassword == '1'}">
    <div class="iv-alert iv-alert-success" style="word-break:break-all;">
        Đã đặt lại mật khẩu. Mật khẩu tạm thời mới: <code>${param.newPassword}</code><br/>
        <span style="font-size:12px;">Vui lòng gửi mật khẩu này cho nhân viên theo cách khác (email/chat nội bộ) - hệ thống chưa tự gửi email cho hành động này.</span>
    </div>
</c:if>

<c:if test="${not empty editAccount}">
<div class="iv-card">
    <h2>Sửa tài khoản: <c:out value="${editAccount.fullName}"/></h2>
    <form method="post" action="${pageContext.request.contextPath}/admin/manage-accounts">
        <input type="hidden" name="action" value="update"/>
        <input type="hidden" name="employeeId" value="${editAccount.employeeId}"/>
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Họ và tên *</label>
                <input type="text" name="fullName" value="${editAccount.fullName}" maxlength="100" required/>
            </div>
            <div class="iv-field">
                <label>Email *</label>
                <input type="email" name="email" value="${editAccount.email}" maxlength="100" required/>
            </div>
        </div>
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Số điện thoại *</label>
                <input type="text" name="phone" value="${editAccount.phone}"
                       pattern="\d{10}" title="Số điện thoại phải gồm đúng 10 chữ số" required/>
            </div>
            <div class="iv-field">
                <label>Vai trò *</label>
                <select name="roleId" required>
                    <c:forEach var="r" items="${roles}">
                        <option value="${r.roleId}" ${editAccount.roleId == r.roleId ? 'selected' : ''}>${r.roleName}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Phòng ban</label>
                <input type="text" name="department" value="${editAccount.department}" maxlength="100"/>
            </div>
            <div class="iv-field">
                <label>Chức danh</label>
                <input type="text" name="position" value="${editAccount.position}" maxlength="100"/>
            </div>
        </div>
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Lương</label>
                <input type="number" name="salary" min="0" step="0.01" value="${editAccount.salary}"/>
            </div>
        </div>
        <div class="iv-action-row" style="margin-top:14px;">
            <button type="submit" class="iv-btn">Lưu thay đổi</button>
            <a class="iv-btn iv-btn-ghost" href="${pageContext.request.contextPath}/admin/manage-accounts">Hủy</a>
        </div>
    </form>
</div>
</c:if>

<div class="iv-card">
    <h2>Tất cả tài khoản (${accounts.size()})</h2>
    <c:choose>
        <c:when test="${empty accounts}">
            <div class="iv-empty">Chưa có tài khoản nhân viên nào.</div>
        </c:when>
        <c:otherwise>
            <div class="iv-table-wrap">
                <table class="iv-table">
                    <thead>
                        <tr>
                            <th>Mã NV</th><th>Họ tên</th><th>Email</th><th>SĐT</th><th>Vai trò</th>
                            <th>Phòng ban / Chức danh</th><th>Trạng thái</th><th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="acc" items="${accounts}">
                            <tr>
                                <td><c:out value="${acc.employeeCode}"/></td>
                                <td><c:out value="${acc.fullName}"/></td>
                                <td><c:out value="${acc.email}"/></td>
                                <td><c:out value="${acc.phone}"/></td>
                                <td><c:out value="${acc.roleName}"/></td>
                                <td><c:out value="${acc.department}"/> / <c:out value="${acc.position}"/></td>
                                <td>
                                    <c:if test="${acc.active}"><span class="iv-badge iv-badge-green">Đang hoạt động</span></c:if>
                                    <c:if test="${!acc.active}"><span class="iv-badge iv-badge-gray">Đã vô hiệu hoá</span></c:if>
                                </td>
                                <td>
                                    <div class="iv-action-row">
                                        <a class="iv-btn iv-btn-sm iv-btn-ghost"
                                           href="${pageContext.request.contextPath}/admin/manage-accounts?editId=${acc.employeeId}">Sửa</a>

                                        <form method="post" style="display:inline;"
                                              action="${pageContext.request.contextPath}/admin/manage-accounts">
                                            <input type="hidden" name="employeeId" value="${acc.employeeId}"/>
                                            <c:if test="${acc.active}">
                                                <input type="hidden" name="action" value="deactivate"/>
                                                <button type="submit" class="iv-btn iv-btn-sm iv-btn-ghost">Vô hiệu hoá</button>
                                            </c:if>
                                            <c:if test="${!acc.active}">
                                                <input type="hidden" name="action" value="activate"/>
                                                <button type="submit" class="iv-btn iv-btn-sm">Kích hoạt</button>
                                            </c:if>
                                        </form>

                                        <form method="post" style="display:inline;"
                                              action="${pageContext.request.contextPath}/admin/manage-accounts"
                                              onsubmit="return confirm('Đặt lại mật khẩu cho ${acc.fullName}?');">
                                            <input type="hidden" name="action" value="resetPassword"/>
                                            <input type="hidden" name="employeeId" value="${acc.employeeId}"/>
                                            <button type="submit" class="iv-btn iv-btn-sm iv-btn-ghost">Đặt lại MK</button>
                                        </form>

                                        <form method="post" style="display:inline;"
                                              action="${pageContext.request.contextPath}/admin/manage-accounts"
                                              onsubmit="return confirm('Xoá vĩnh viễn tài khoản ${acc.fullName}? Hành động này không thể hoàn tác.');">
                                            <input type="hidden" name="action" value="delete"/>
                                            <input type="hidden" name="employeeId" value="${acc.employeeId}"/>
                                            <button type="submit" class="iv-btn iv-btn-sm iv-btn-danger">Xoá</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="admin_footer.jsp"/>
