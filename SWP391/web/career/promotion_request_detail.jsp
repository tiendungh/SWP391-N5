<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Chi tiết đề nghị thăng chức" scope="request"/>
<c:set var="currentNav" value="promotionRequests" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Đề nghị thăng chức #${promotion.promotionRequestId}</h1>
        <p>Thông tin đề nghị, quyết định xử lý và toàn bộ lịch sử thao tác.</p>
    </div>
    <div class="iv-whoami">
        <c:choose>
            <c:when test="${promotion.status == 'Pending'}"><span class="iv-badge iv-badge-blue">Chờ duyệt</span></c:when>
            <c:when test="${promotion.status == 'Approved'}"><span class="iv-badge iv-badge-green">Đã duyệt</span></c:when>
            <c:when test="${promotion.status == 'Rejected'}"><span class="iv-badge iv-badge-red">Từ chối</span></c:when>
            <c:otherwise><span class="iv-badge iv-badge-gray">Đã hủy</span></c:otherwise>
        </c:choose>
    </div>
</div>

<c:if test="${not empty error}">
    <div class="iv-alert iv-alert-error"><c:out value="${error}"/></div>
</c:if>
<c:if test="${param.processed == 'approved'}">
    <div class="iv-alert iv-alert-success">Đã phê duyệt và cập nhật vị trí nhân viên.</div>
</c:if>
<c:if test="${param.processed == 'rejected'}">
    <div class="iv-alert iv-alert-success">Đã từ chối đề nghị.</div>
</c:if>
<c:if test="${param.processed == 'cancelled'}">
    <div class="iv-alert iv-alert-success">Đã hủy đề nghị.</div>
</c:if>

<div class="iv-card iv-profile-hero">
    <div>
        <c:choose>
            <c:when test="${staffAccess}">
                <a class="iv-back-link" href="${pageContext.request.contextPath}/promotion-requests">← Danh sách đề nghị</a>
            </c:when>
            <c:otherwise>
                <a class="iv-back-link" href="${pageContext.request.contextPath}/my-career-path">← Lộ trình của tôi</a>
            </c:otherwise>
        </c:choose>
        <h2><c:out value="${promotion.employeeName}"/></h2>
        <p class="iv-muted"><c:out value="${promotion.employeeCode}"/></p>
    </div>
    <div class="iv-profile-score">
        <strong><fmt:formatDate value="${promotion.effectiveDate}" pattern="dd/MM/yyyy"/></strong>
        <span>Ngày hiệu lực dự kiến</span>
    </div>
</div>

<div class="iv-card">
    <h2>Nội dung đề nghị</h2>
    <div class="iv-document-list">
        <article class="iv-document-card status-${promotion.status}">
            <div class="iv-document-head">
                <div>
                    <span class="iv-small iv-muted">Vị trí hiện tại</span>
                    <h3><c:out value="${promotion.currentPosition.positionName}"/></h3>
                    <span class="iv-small">Cấp <c:out value="${promotion.currentPosition.jobLevel.levelName}"/></span>
                </div>
                <div>
                    <span class="iv-small iv-muted">Vị trí đề xuất</span>
                    <h3><c:out value="${promotion.proposedPosition.positionName}"/></h3>
                    <span class="iv-small">Cấp <c:out value="${promotion.proposedPosition.jobLevel.levelName}"/></span>
                </div>
            </div>
            <div class="iv-document-meta">
                <span><b>Phòng ban:</b> <c:out value="${promotion.proposedDepartment}"/></span>
                <span>
                    <b>Lương hiện tại:</b>
                    <c:choose>
                        <c:when test="${not empty promotion.currentSalary}"><fmt:formatNumber value="${promotion.currentSalary}" pattern="#0.00"/></c:when>
                        <c:otherwise>Chưa thiết lập</c:otherwise>
                    </c:choose>
                </span>
                <span>
                    <b>Lương đề xuất:</b>
                    <c:choose>
                        <c:when test="${not empty promotion.proposedSalary}"><fmt:formatNumber value="${promotion.proposedSalary}" pattern="#0.00"/></c:when>
                        <c:otherwise>Giữ nguyên</c:otherwise>
                    </c:choose>
                </span>
            </div>
            <p><b>Lý do:</b> <c:out value="${promotion.reason}"/></p>
            <p class="iv-small iv-muted">
                Đề nghị bởi <b><c:out value="${promotion.proposedByName}"/></b>
                lúc <fmt:formatDate value="${promotion.proposedAt}" pattern="dd/MM/yyyy HH:mm"/>.
            </p>
            <c:if test="${not empty promotion.reviewedBy}">
                <p class="iv-small">
                    <b>Người xử lý:</b> <c:out value="${promotion.reviewedByName}"/> ·
                    <fmt:formatDate value="${promotion.reviewedAt}" pattern="dd/MM/yyyy HH:mm"/>
                </p>
            </c:if>
            <c:if test="${not empty promotion.reviewNote}">
                <p><b>Nhận xét xử lý:</b> <c:out value="${promotion.reviewNote}"/></p>
            </c:if>
        </article>
    </div>
</div>

<c:if test="${canReview}">
    <div class="iv-card">
        <h2>Phê duyệt đề nghị</h2>
        <p class="iv-small iv-muted">Khi duyệt, vị trí, lương và lịch sử nghề nghiệp của nhân viên được cập nhật trong cùng transaction.</p>
        <form method="post" action="${pageContext.request.contextPath}/promotion-request">
            <input type="hidden" name="id" value="${promotion.promotionRequestId}"/>
            <input type="hidden" name="action" value="approve"/>
            <div class="iv-field">
                <label>Nhận xét phê duyệt</label>
                <textarea name="note" maxlength="2000" placeholder="Nhận xét tùy chọn"></textarea>
            </div>
            <button class="iv-btn" type="submit">Phê duyệt và áp dụng</button>
        </form>
    </div>

    <div class="iv-card">
        <h2>Từ chối đề nghị</h2>
        <form method="post" action="${pageContext.request.contextPath}/promotion-request">
            <input type="hidden" name="id" value="${promotion.promotionRequestId}"/>
            <input type="hidden" name="action" value="reject"/>
            <div class="iv-field">
                <label>Lý do từ chối *</label>
                <textarea name="note" maxlength="2000" required placeholder="Giải thích rõ lý do từ chối"></textarea>
            </div>
            <button class="iv-btn iv-btn-danger" type="submit">Từ chối đề nghị</button>
        </form>
    </div>
</c:if>

<c:if test="${canCancel}">
    <div class="iv-card">
        <h2>Hủy đề nghị</h2>
        <p class="iv-small iv-muted">Chỉ người tạo mới được hủy khi đề nghị còn Pending.</p>
        <form method="post" action="${pageContext.request.contextPath}/promotion-request">
            <input type="hidden" name="id" value="${promotion.promotionRequestId}"/>
            <input type="hidden" name="action" value="cancel"/>
            <div class="iv-field">
                <label>Lý do hủy</label>
                <textarea name="note" maxlength="2000" placeholder="Lý do tùy chọn"></textarea>
            </div>
            <button class="iv-btn iv-btn-danger" type="submit">Hủy đề nghị</button>
        </form>
    </div>
</c:if>

<c:if test="${promotion.status == 'Pending' && !canReview && !canCancel}">
    <div class="iv-card">
        <div class="iv-empty">Đề nghị đang chờ một HR Staff hoặc Manager khác với người tạo xử lý.</div>
    </div>
</c:if>

<div class="iv-card">
    <h2>Lịch sử xử lý</h2>
    <c:choose>
        <c:when test="${empty promotionHistory}">
            <div class="iv-empty">Chưa có lịch sử xử lý.</div>
        </c:when>
        <c:otherwise>
            <div class="iv-timeline">
                <c:forEach var="history" items="${promotionHistory}">
                    <div class="iv-timeline-item">
                        <span class="iv-timeline-dot"></span>
                        <div>
                            <b>
                                <c:choose>
                                    <c:when test="${history.action == 'Created'}">Đã tạo đề nghị</c:when>
                                    <c:when test="${history.action == 'Approved'}">Đã phê duyệt</c:when>
                                    <c:when test="${history.action == 'Rejected'}">Đã từ chối</c:when>
                                    <c:otherwise>Đã hủy</c:otherwise>
                                </c:choose>
                            </b>
                            · <c:out value="${history.actionByName}"/>
                        </div>
                        <div class="iv-small iv-muted">
                            <fmt:formatDate value="${history.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                            · <c:out value="${history.fromStatus}"/> → <c:out value="${history.toStatus}"/>
                        </div>
                        <c:if test="${not empty history.comment}">
                            <div class="iv-small"><c:out value="${history.comment}"/></div>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/interview/staff_footer.jsp"/>
