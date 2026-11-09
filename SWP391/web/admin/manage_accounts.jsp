<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Quản lý tài khoản" scope="request"/>
<c:set var="currentNav" value="manage-accounts" scope="request"/>
<c:set var="activeCount" value="0"/>
<c:forEach var="acc" items="${accounts}">
    <c:if test="${acc.active}"><c:set var="activeCount" value="${activeCount + 1}"/></c:if>
</c:forEach>
<jsp:include page="admin_header.jsp"/>

<style>
    .ma-stats {
        display: grid;
        grid-template-columns: repeat(3, minmax(0, 1fr));
        gap: 14px;
        margin-bottom: 22px;
    }
    .ma-stat {
        background: var(--iv-card);
        border: 1px solid var(--iv-card-border);
        border-radius: var(--iv-radius);
        box-shadow: var(--iv-shadow);
        padding: 16px 18px;
    }
    .ma-stat span {
        display: block;
        color: var(--iv-muted);
        font-size: 12px;
        font-weight: 700;
        letter-spacing: 0.3px;
        text-transform: uppercase;
    }
    .ma-stat strong {
        display: block;
        margin-top: 6px;
        color: var(--iv-brand-deep);
        font-size: 28px;
        line-height: 1;
    }
    .ma-toolbar {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 16px;
        margin-bottom: 16px;
    }
    .ma-search {
        flex: 1;
        max-width: 360px;
        background: #fff;
        border: 1px solid rgba(63, 136, 197, 0.22);
        border-radius: 10px;
        padding: 10px 12px;
        font: inherit;
        color: var(--iv-text);
        outline: none;
    }
    .ma-search:focus {
        border-color: var(--iv-brand);
        box-shadow: 0 0 0 3px var(--iv-brand-glow);
    }
    .ma-person { min-width: 180px; }
    .ma-person b { display: block; color: var(--iv-text); font-size: 14.5px; }
    .ma-sub { display: block; margin-top: 3px; color: var(--iv-muted); font-size: 12px; }
    .ma-actions {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 6px;
        min-width: 196px;
    }
    .ma-actions form, .ma-actions a { display: flex; margin: 0; }
    .ma-actions .iv-btn { width: 100%; justify-content: center; white-space: nowrap; }
    .ma-edit {
        border-color: rgba(63, 136, 197, 0.35);
        box-shadow: var(--iv-shadow-lg);
    }
    .ma-kicker {
        margin: 0 0 6px;
        color: var(--iv-brand);
        font-size: 12px;
        font-weight: 800;
        letter-spacing: 0.4px;
        text-transform: uppercase;
    }
    .iv-action-row { display: flex; flex-wrap: wrap; align-items: center; gap: 8px; }
    .iv-action-row form { display: inline-flex; margin: 0; }
    tr.is-hidden { display: none; }
    @media (max-width: 800px) {
        .ma-stats, .ma-toolbar { grid-template-columns: 1fr; display: grid; }
        .ma-search { max-width: none; }
    }
</style>

<div class="iv-topbar">
    <div>
        <h1>Quản lý tài khoản nhân viên</h1>
        <p>Sửa thông tin, kích hoạt hoặc vô hiệu hoá, đặt lại mật khẩu, hoặc xoá tài khoản.</p>
    </div>
    <a class="iv-btn" href="${pageContext.request.contextPath}/admin/create-account">Tạo tài khoản mới</a>
</div>

<c:if test="${not empty param.error}"><div class="iv-alert iv-alert-error"><c:out value="${param.error}"/></div></c:if>
<c:if test="${param.updated == '1'}"><div class="iv-alert iv-alert-success">Đã cập nhật tài khoản.</div></c:if>
<c:if test="${param.activated == '1'}"><div class="iv-alert iv-alert-success">Đã kích hoạt tài khoản.</div></c:if>
<c:if test="${param.deactivated == '1'}"><div class="iv-alert iv-alert-success">Đã vô hiệu hoá tài khoản.</div></c:if>
<c:if test="${param.deleted == '1'}"><div class="iv-alert iv-alert-success">Đã xoá tài khoản.</div></c:if>
<c:if test="${param.resetPassword == '1'}">
    <div class="iv-alert iv-alert-success" style="word-break:break-all;">
        Đã đặt lại mật khẩu. Mật khẩu tạm thời mới: <code><c:out value="${param.newPassword}"/></code><br/>
        <span style="font-size:12px;">Gửi mật khẩu này cho nhân viên qua kênh nội bộ. Hệ thống chưa tự gửi email cho thao tác này.</span>
    </div>
</c:if>

<div class="ma-stats">
    <div class="ma-stat"><span>Tổng tài khoản</span><strong>${empty accounts ? 0 : accounts.size()}</strong></div>
    <div class="ma-stat"><span>Đang hoạt động</span><strong>${activeCount}</strong></div>
    <div class="ma-stat"><span>Đã vô hiệu hoá</span><strong>${empty accounts ? 0 : accounts.size() - activeCount}</strong></div>
</div>

<c:if test="${not empty editAccount}">
<div class="iv-card ma-edit">
    <p class="ma-kicker">Đang sửa</p>
    <h2 style="margin-top:0;"><c:out value="${editAccount.fullName}"/></h2>
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
                       inputmode="numeric" maxlength="10" pattern="0[0-9]{9}" title="Số điện thoại gồm đúng 10 chữ số và bắt đầu bằng số 0" required/>
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
            <a class="iv-btn iv-btn-ghost" href="${pageContext.request.contextPath}/admin/manage-accounts">Huỷ</a>
        </div>
    </form>
</div>
</c:if>

<div class="iv-card">
    <div class="ma-toolbar">
        <h2 style="margin:0;">Danh sách tài khoản</h2>
        <input id="accountSearch" class="ma-search" type="search" placeholder="Tìm theo tên, email, mã NV..."/>
    </div>
    <c:choose>
        <c:when test="${empty accounts}">
            <div class="iv-empty">Chưa có tài khoản nhân viên nào.</div>
        </c:when>
        <c:otherwise>
            <div class="iv-table-wrap">
                <table class="iv-table" id="accountTable">
                    <thead>
                        <tr>
                            <th>Nhân viên</th>
                            <th>Liên hệ</th>
                            <th>Vai trò</th>
                            <th>Phòng ban</th>
                            <th>Trạng thái</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="acc" items="${accounts}">
                            <tr data-search="${acc.employeeCode} ${acc.fullName} ${acc.email} ${acc.phone} ${acc.roleName} ${acc.department} ${acc.position}">
                                <td class="ma-person">
                                    <b><c:out value="${acc.fullName}"/></b>
                                    <span class="ma-sub"><c:out value="${empty acc.employeeCode ? 'Chưa có mã' : acc.employeeCode}"/></span>
                                </td>
                                <td>
                                    <c:out value="${acc.email}"/>
                                    <span class="ma-sub"><c:out value="${acc.phone}"/></span>
                                </td>
                                <td><span class="iv-badge iv-badge-blue"><c:out value="${acc.roleName}"/></span></td>
                                <td>
                                    <c:out value="${empty acc.department ? '—' : acc.department}"/>
                                    <span class="ma-sub"><c:out value="${empty acc.position ? 'Chưa có chức danh' : acc.position}"/></span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${acc.active}"><span class="iv-badge iv-badge-green">Đang hoạt động</span></c:when>
                                        <c:otherwise><span class="iv-badge iv-badge-gray">Đã vô hiệu hoá</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="ma-actions">
                                        <a class="iv-btn iv-btn-sm iv-btn-ghost"
                                           href="${pageContext.request.contextPath}/admin/manage-accounts?editId=${acc.employeeId}">Sửa</a>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/manage-accounts">
                                            <input type="hidden" name="employeeId" value="${acc.employeeId}"/>
                                            <c:choose>
                                                <c:when test="${acc.active}">
                                                    <input type="hidden" name="action" value="deactivate"/>
                                                    <button type="submit" class="iv-btn iv-btn-sm iv-btn-ghost">Vô hiệu hoá</button>
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="hidden" name="action" value="activate"/>
                                                    <button type="submit" class="iv-btn iv-btn-sm">Kích hoạt</button>
                                                </c:otherwise>
                                            </c:choose>
                                        </form>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/manage-accounts"
                                              onsubmit="return confirm('Đặt lại mật khẩu cho tài khoản này?');">
                                            <input type="hidden" name="action" value="resetPassword"/>
                                            <input type="hidden" name="employeeId" value="${acc.employeeId}"/>
                                            <button type="submit" class="iv-btn iv-btn-sm iv-btn-ghost">Đặt lại MK</button>
                                        </form>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/manage-accounts"
                                              onsubmit="return confirm('Xoá vĩnh viễn tài khoản này? Không thể hoàn tác.');">
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
            <p id="accountEmpty" class="iv-empty" style="display:none;">Không có tài khoản khớp từ khoá.</p>
        </c:otherwise>
    </c:choose>
</div>

<script>
    var search = document.getElementById('accountSearch');
    if (search) {
        search.addEventListener('input', function () {
            var q = search.value.trim().toLowerCase();
            var rows = document.querySelectorAll('#accountTable tbody tr');
            var shown = 0;
            rows.forEach(function (row) {
                var hit = !q || (row.getAttribute('data-search') || '').toLowerCase().indexOf(q) >= 0;
                row.classList.toggle('is-hidden', !hit);
                if (hit) shown++;
            });
            var empty = document.getElementById('accountEmpty');
            if (empty) empty.style.display = shown ? 'none' : 'block';
        });
    }
</script>

<jsp:include page="admin_footer.jsp"/>
