<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Yêu cầu tạo tài khoản" scope="request"/>
<c:set var="currentNav" value="account-requests" scope="request"/>
<jsp:include page="admin_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Yêu cầu tạo tài khoản từ Manager</h1>
        <p>Manager không thể tự tạo tài khoản - họ gửi yêu cầu ở đây để Admin xem xét và tạo tài khoản thật.</p>
    </div>
    <div class="iv-whoami">Xin chào, <c:out value="${sessionScope.adminUsername}"/></div>
</div>

<c:if test="${param.rejected == '1'}"><div class="iv-alert iv-alert-success">Đã từ chối yêu cầu.</div></c:if>
<c:if test="${not empty param.error}"><div class="iv-alert iv-alert-error"><c:out value="${param.error}"/></div></c:if>

<div class="iv-card">
    <h2>Đang chờ xử lý (${pendingRequests.size()})</h2>
    <c:choose>
        <c:when test="${empty pendingRequests}">
            <div class="iv-empty">Không có yêu cầu nào đang chờ.</div>
        </c:when>
        <c:otherwise>
            <table class="iv-table">
                <thead>
                    <tr>
                        <th>Email</th><th>Họ tên</th><th>Vai trò</th><th>Phòng ban / Chức danh</th>
                        <th>Người gửi</th><th>Ngày gửi</th><th>Ghi chú</th><th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="req" items="${pendingRequests}">
                        <tr>
                            <td><b><c:out value="${req.requestedEmail}"/></b></td>
                            <td><c:out value="${req.fullName}"/></td>
                            <td><c:out value="${req.roleName}"/></td>
                            <td><c:out value="${req.department}"/> / <c:out value="${req.position}"/></td>
                            <td><c:out value="${req.requestedByName}"/></td>
                            <td><fmt:formatDate value="${req.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td class="iv-small"><c:out value="${req.note}"/></td>
                            <td>
                                <div class="iv-action-row">
                                    <a class="iv-btn iv-btn-sm" href="${pageContext.request.contextPath}/admin/create-account?requestId=${req.requestId}">Tạo tài khoản</a>
                                    <button type="button" class="iv-btn iv-btn-sm iv-btn-danger" onclick="openRejectForm(${req.requestId})">Từ chối</button>
                                </div>
                                <form method="post" id="rejectForm-${req.requestId}" class="iv-review-form"
                                      action="${pageContext.request.contextPath}/admin/account-requests" style="display:none;margin-top:8px;">
                                    <input type="hidden" name="action" value="reject"/>
                                    <input type="hidden" name="requestId" value="${req.requestId}"/>
                                    <textarea name="rejectionNote" placeholder="Lý do từ chối..." required></textarea>
                                    <button type="submit" class="iv-btn iv-btn-sm iv-btn-danger">Xác nhận từ chối</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<div class="iv-card">
    <h2>Lịch sử tất cả yêu cầu (${allRequests.size()})</h2>
    <c:choose>
        <c:when test="${empty allRequests}">
            <div class="iv-empty">Chưa có yêu cầu nào.</div>
        </c:when>
        <c:otherwise>
            <table class="iv-table">
                <thead><tr><th>Email</th><th>Người gửi</th><th>Trạng thái</th><th>Ngày gửi</th><th>Xử lý bởi</th><th>Chi tiết</th></tr></thead>
                <tbody>
                    <c:forEach var="req" items="${allRequests}">
                        <tr>
                            <td><c:out value="${req.requestedEmail}"/></td>
                            <td><c:out value="${req.requestedByName}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${req.status == 'Pending'}"><span class="iv-badge iv-badge-amber">Đang chờ</span></c:when>
                                    <c:when test="${req.status == 'Approved'}"><span class="iv-badge iv-badge-green">Đã duyệt</span></c:when>
                                    <c:otherwise><span class="iv-badge iv-badge-red">Đã từ chối</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td><fmt:formatDate value="${req.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td><c:out value="${req.reviewedByAdminName}"/></td>
                            <td class="iv-small">
                                <c:if test="${req.status == 'Rejected'}"><c:out value="${req.rejectionNote}"/></c:if>
                                <c:if test="${req.status == 'Approved'}">EmployeeID: ${req.fulfilledEmployeeId}</c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<script>
    function openRejectForm(requestId) {
        var form = document.getElementById('rejectForm-' + requestId);
        form.style.display = form.style.display === 'none' ? 'block' : 'none';
    }
</script>

<jsp:include page="admin_footer.jsp"/>
