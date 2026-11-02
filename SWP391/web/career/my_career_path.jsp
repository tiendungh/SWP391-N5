<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Lộ trình thăng tiến" scope="request"/>
<c:set var="currentNav" value="myCareer" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>${viewingOwnCareer ? 'Lộ trình của tôi' : 'Lộ trình nhân viên'}</h1>
        <p>Theo dõi vị trí hiện tại, bước phát triển tiếp theo và lịch sử nghề nghiệp.</p>
    </div>
    <div class="iv-whoami">
        <b><c:out value="${career.fullName}"/></b><br>
        <c:out value="${career.employeeCode}"/>
    </div>
</div>

<div class="iv-card iv-profile-hero">
    <div>
        <span class="iv-badge ${career.active ? 'iv-badge-green' : 'iv-badge-gray'}">
            ${career.active ? 'Đang làm việc' : 'Ngừng hoạt động'}
        </span>
        <h2><c:out value="${career.currentPosition.positionName}"/></h2>
        <p class="iv-muted">
            <c:out value="${career.currentPosition.department}"/>
            <c:if test="${not empty career.currentPosition.jobLevel}">
                · Cấp <c:out value="${career.currentPosition.jobLevel.levelName}"/>
            </c:if>
        </p>
        <div class="iv-document-meta">
            <span><b>Ngày vào làm:</b> <fmt:formatDate value="${career.hireDate}" pattern="dd/MM/yyyy"/></span>
            <span><b>Bắt đầu vị trí:</b> <fmt:formatDate value="${career.positionStartDate}" pattern="dd/MM/yyyy"/></span>
            <span><b>Vai trò hệ thống:</b> <c:out value="${career.roleName}"/></span>
        </div>
    </div>
    <div class="iv-profile-score">
        <strong>${career.monthsInPosition} tháng</strong>
        <span>Thời gian đã đảm nhiệm vị trí hiện tại</span>
    </div>
</div>

<div class="iv-card">
    <h2>Bước phát triển tiếp theo (${career.nextPaths.size()})</h2>
    <c:choose>
        <c:when test="${empty career.nextPaths}">
            <div class="iv-empty">
                Chưa có lộ trình tiếp theo được thiết lập cho vị trí hiện tại.
            </div>
        </c:when>
        <c:otherwise>
            <div class="iv-document-list">
                <c:forEach var="path" items="${career.nextPaths}">
                    <article class="iv-document-card ${path.eligibleByTenure ? 'status-Approved' : 'status-Submitted'}">
                        <div class="iv-document-head">
                            <div>
                                <h3><c:out value="${path.toPosition.positionName}"/></h3>
                                <span class="iv-small iv-muted">
                                    <c:out value="${path.toPosition.department}"/>
                                    · Cấp <c:out value="${path.toPosition.jobLevel.levelName}"/>
                                </span>
                            </div>
                            <c:choose>
                                <c:when test="${path.eligibleByTenure}">
                                    <span class="iv-badge iv-badge-green">Đủ thời gian</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="iv-badge iv-badge-amber">Còn ${path.remainingMonths} tháng</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="iv-document-meta">
                            <span><b>Tối thiểu:</b> ${path.minMonthsInPosition} tháng ở vị trí hiện tại</span>
                            <span><b>Đã đạt:</b> ${path.employeeMonthsInPosition} tháng</span>
                        </div>
                        <c:if test="${not empty path.requirementNote}">
                            <p><b>Yêu cầu tham khảo:</b> <c:out value="${path.requirementNote}"/></p>
                        </c:if>
                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<div class="iv-card">
    <h2>Đề nghị thăng chức (${employeePromotionRequests.size()})</h2>
    <c:choose>
        <c:when test="${empty employeePromotionRequests}">
            <div class="iv-empty">Chưa có đề nghị thăng chức nào.</div>
        </c:when>
        <c:otherwise>
            <div class="iv-table-wrap">
                <table class="iv-table">
                    <thead>
                        <tr><th>Vị trí đề xuất</th><th>Người đề nghị</th><th>Ngày hiệu lực</th><th>Trạng thái</th><th></th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="promotion" items="${employeePromotionRequests}">
                            <tr>
                                <td><b><c:out value="${promotion.proposedPosition.positionName}"/></b></td>
                                <td>
                                    <c:out value="${promotion.proposedByName}"/><br>
                                    <span class="iv-small iv-muted"><fmt:formatDate value="${promotion.proposedAt}" pattern="dd/MM/yyyy HH:mm"/></span>
                                </td>
                                <td><fmt:formatDate value="${promotion.effectiveDate}" pattern="dd/MM/yyyy"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${promotion.status == 'Pending'}"><span class="iv-badge iv-badge-blue">Chờ duyệt</span></c:when>
                                        <c:when test="${promotion.status == 'Approved'}"><span class="iv-badge iv-badge-green">Đã duyệt</span></c:when>
                                        <c:when test="${promotion.status == 'Rejected'}"><span class="iv-badge iv-badge-red">Từ chối</span></c:when>
                                        <c:otherwise><span class="iv-badge iv-badge-gray">Đã hủy</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a class="iv-btn iv-btn-sm"
                                       href="${pageContext.request.contextPath}/promotion-request?id=${promotion.promotionRequestId}">Xem</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<div class="iv-card">
    <h2>Lịch sử nghề nghiệp</h2>
    <c:choose>
        <c:when test="${empty career.positionHistory}">
            <div class="iv-empty">Chưa có lịch sử vị trí.</div>
        </c:when>
        <c:otherwise>
            <div class="iv-timeline">
                <c:forEach var="history" items="${career.positionHistory}">
                    <div class="iv-timeline-item">
                        <span class="iv-timeline-dot"></span>
                        <div>
                            <b><c:out value="${history.positionName}"/></b>
                            <c:if test="${history.current}">
                                <span class="iv-badge iv-badge-green">Hiện tại</span>
                            </c:if>
                        </div>
                        <div class="iv-small iv-muted">
                            <c:out value="${history.department}"/> ·
                            <fmt:formatDate value="${history.startDate}" pattern="dd/MM/yyyy"/> -
                            <c:choose>
                                <c:when test="${empty history.endDate}">Hiện tại</c:when>
                                <c:otherwise><fmt:formatDate value="${history.endDate}" pattern="dd/MM/yyyy"/></c:otherwise>
                            </c:choose>
                        </div>
                        <c:if test="${not empty history.note}">
                            <div class="iv-small"><c:out value="${history.note}"/></div>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/interview/staff_footer.jsp"/>
