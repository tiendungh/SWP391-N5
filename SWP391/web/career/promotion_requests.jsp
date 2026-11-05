<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Đề nghị thăng chức" scope="request"/>
<c:set var="currentNav" value="promotionRequests" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Đề nghị thăng chức</h1>
        <p>Chọn nhân viên, kiểm tra lộ trình hợp lệ và tạo đề nghị chờ phê duyệt.</p>
    </div>
    <div class="iv-whoami">Người tạo: <b><c:out value="${sessionScope.staffName}"/></b></div>
</div>

<c:if test="${not empty error}">
    <div class="iv-alert iv-alert-error"><c:out value="${error}"/></div>
</c:if>
<c:if test="${not empty param.created}">
    <div class="iv-alert iv-alert-success">Đã tạo đề nghị thăng chức #<c:out value="${param.created}"/>.</div>
</c:if>

<div class="iv-card">
    <div class="iv-section-head">
        <div>
            <h2>Chọn nhân viên</h2>
            <span class="iv-small iv-muted">Chỉ hiển thị nhân viên đang hoạt động và đã có Position/JobLevel.</span>
        </div>
    </div>
    <c:choose>
        <c:when test="${empty promotionCandidates}">
            <div class="iv-empty">Không có nhân viên phù hợp hoặc database chưa được cập nhật.</div>
        </c:when>
        <c:otherwise>
            <form method="get" action="${pageContext.request.contextPath}/promotion-requests">
                <div class="iv-form-row">
                    <div class="iv-field">
                        <label>Nhân viên *</label>
                        <select name="employeeId" required>
                            <option value="">-- Chọn nhân viên --</option>
                            <c:forEach var="employee" items="${promotionCandidates}">
                                <option value="${employee.employeeId}"
                                        <c:if test="${not empty selectedCareer && selectedCareer.employeeId == employee.employeeId}">selected</c:if>>
                                    <c:out value="${employee.fullName}"/> - <c:out value="${employee.employeeCode}"/>
                                    (<c:out value="${employee.currentPosition.positionName}"/>)
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <button class="iv-btn" type="submit">Kiểm tra lộ trình</button>
            </form>
        </c:otherwise>
    </c:choose>
</div>

<c:if test="${not empty selectedCareer}">
    <div class="iv-card iv-profile-hero">
        <div>
            <a class="iv-back-link" href="${pageContext.request.contextPath}/my-career-path?employeeId=${selectedCareer.employeeId}">Xem toàn bộ lộ trình →</a>
            <h2><c:out value="${selectedCareer.fullName}"/></h2>
            <p class="iv-muted">
                <c:out value="${selectedCareer.employeeCode}"/> ·
                <c:out value="${selectedCareer.currentPosition.positionName}"/> ·
                Cấp <c:out value="${selectedCareer.currentPosition.jobLevel.levelName}"/>
            </p>
        </div>
        <div class="iv-profile-score">
            <strong>${selectedCareer.monthsInPosition} tháng</strong>
            <span>Thời gian ở vị trí hiện tại</span>
        </div>
    </div>

    <div class="iv-card">
        <h2>Tạo đề nghị mới</h2>
        <c:choose>
            <c:when test="${hasPendingRequest}">
                <div class="iv-empty">Nhân viên đang có một đề nghị Pending. Hãy xử lý hoặc hủy đề nghị đó trước.</div>
            </c:when>
            <c:when test="${empty selectedCareer.nextPaths}">
                <div class="iv-empty">Chưa thiết lập bước thăng tiến tiếp theo cho vị trí hiện tại.</div>
            </c:when>
            <c:when test="${!canCreatePromotion}">
                <div class="iv-empty">Nhân viên chưa đủ thời gian hoặc chưa có vị trí mục tiêu cấp cao hơn.</div>
            </c:when>
            <c:otherwise>
                <form method="post" action="${pageContext.request.contextPath}/promotion-requests">
                    <input type="hidden" name="employeeId" value="${selectedCareer.employeeId}"/>
                    <div class="iv-form-row">
                        <div class="iv-field">
                            <label>Vị trí đề xuất *</label>
                            <select name="proposedPositionId" required>
                                <option value="">-- Chọn vị trí --</option>
                                <c:forEach var="path" items="${selectedCareer.nextPaths}">
                                    <c:set var="higherLevel" value="${path.toPosition.jobLevel.rankOrder > selectedCareer.currentPosition.jobLevel.rankOrder}"/>
                                    <option value="${path.toPosition.positionId}"
                                            <c:if test="${!path.eligibleByTenure || !higherLevel}">disabled</c:if>>
                                        <c:out value="${path.toPosition.positionName}"/> -
                                        Cấp <c:out value="${path.toPosition.jobLevel.levelName}"/>
                                        <c:if test="${!path.eligibleByTenure}">(còn ${path.remainingMonths} tháng)</c:if>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="iv-field">
                            <label>Lương đề xuất</label>
                            <input type="number" name="proposedSalary" min="0" max="9999999999999999.99"
                                   step="0.01" placeholder="Hiện tại: ${selectedCareer.salary}"/>
                        </div>
                        <div class="iv-field">
                            <label>Ngày hiệu lực *</label>
                            <input type="date" name="effectiveDate" value="${today}"
                                   min="${minEffectiveDate}" max="${today}" required/>
                        </div>
                    </div>
                    <div class="iv-field">
                        <label>Lý do đề nghị *</label>
                        <textarea name="reason" maxlength="2000" required
                                  placeholder="Nêu kết quả công việc, năng lực và lý do đề xuất"><c:out value="${param.reason}"/></textarea>
                    </div>
                    <button class="iv-btn" type="submit">Tạo đề nghị Pending</button>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</c:if>

<div class="iv-card">
    <div class="iv-section-head">
        <div>
            <h2>Danh sách đề nghị (${promotionRequests.size()})</h2>
            <span class="iv-small iv-muted">Các đề nghị Pending được xếp trước.</span>
        </div>
    </div>
    <c:choose>
        <c:when test="${empty promotionRequests}">
            <div class="iv-empty">Chưa có đề nghị thăng chức nào.</div>
        </c:when>
        <c:otherwise>
            <div class="iv-table-wrap">
                <table class="iv-table">
                    <thead>
                        <tr>
                            <th>Mã</th>
                            <th>Nhân viên</th>
                            <th>Lộ trình đề nghị</th>
                            <th>Người đề nghị</th>
                            <th>Hiệu lực</th>
                            <th>Lương đề xuất</th>
                            <th>Trạng thái</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="promotion" items="${promotionRequests}">
                            <tr>
                                <td>#${promotion.promotionRequestId}</td>
                                <td>
                                    <b><c:out value="${promotion.employeeName}"/></b><br>
                                    <span class="iv-small iv-muted"><c:out value="${promotion.employeeCode}"/></span>
                                </td>
                                <td>
                                    <c:out value="${promotion.currentPosition.positionName}"/> →
                                    <b><c:out value="${promotion.proposedPosition.positionName}"/></b>
                                </td>
                                <td>
                                    <c:out value="${promotion.proposedByName}"/><br>
                                    <span class="iv-small iv-muted"><fmt:formatDate value="${promotion.proposedAt}" pattern="dd/MM/yyyy HH:mm"/></span>
                                </td>
                                <td><fmt:formatDate value="${promotion.effectiveDate}" pattern="dd/MM/yyyy"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty promotion.proposedSalary}">
                                            <fmt:formatNumber value="${promotion.proposedSalary}" pattern="#0.00"/>
                                        </c:when>
                                        <c:otherwise>Giữ nguyên</c:otherwise>
                                    </c:choose>
                                </td>
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

<jsp:include page="/interview/staff_footer.jsp"/>
