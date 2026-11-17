<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Lịch phỏng vấn" scope="request"/>
<c:set var="currentNav" value="calendar" scope="request"/>
<jsp:include page="staff_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Lịch phỏng vấn</h1>
        <p>Toàn bộ lịch phỏng vấn của tất cả ứng viên, có thể lọc theo ngày và người tham gia.</p>
    </div>
    <div class="iv-whoami">Xin chào, <b>${sessionScope.staffName}</b></div>
</div>

<c:if test="${param.scheduled == '1'}"><div class="iv-alert iv-alert-success">Đã đặt lịch phỏng vấn thành công.</div></c:if>
<c:if test="${param.evaluated == '1'}"><div class="iv-alert iv-alert-success">Đã lưu điểm đánh giá.</div></c:if>

<div class="iv-card">
    <form method="get" action="${pageContext.request.contextPath}/interview-calendar" class="iv-form-row" style="margin-bottom:0;">
        <div class="iv-field">
            <label>Ngày phỏng vấn</label>
            <input type="date" name="date" value="${selectedDate}"/>
        </div>
        <div class="iv-field">
            <label>Người tham gia phỏng vấn</label>
            <select name="staffId">
                <option value="">Tất cả</option>
                <c:forEach var="s" items="${allStaff}">
                    <option value="${s.staffId}" ${selectedStaffId == s.staffId ? 'selected' : ''}>${s.fullName} (${s.roleName})</option>
                </c:forEach>
            </select>
        </div>
        <div class="iv-field" style="justify-content:flex-end;">
            <button type="submit" class="iv-btn">Lọc</button>
        </div>
        <div class="iv-field" style="justify-content:flex-end;">
            <a class="iv-btn iv-btn-ghost" href="${pageContext.request.contextPath}/interview-calendar">Xóa lọc</a>
        </div>
    </form>
</div>

<div class="iv-card">
    <h2>Kết quả (${interviews.size()} lịch phỏng vấn)</h2>
    <c:choose>
        <c:when test="${empty interviews}">
            <div class="iv-empty">Không có lịch phỏng vấn nào khớp bộ lọc.</div>
        </c:when>
        <c:otherwise>
            <table class="iv-table">
                <thead>
                    <tr>
                        <th>Giờ</th>
                        <th>Ứng viên</th>
                        <th>Vị trí</th>
                        <th>Vòng</th>
                        <th>Hình thức</th>
                        <th>Địa điểm / Link</th>
                        <th>Người phỏng vấn</th>
                        <th>Trạng thái</th>
                        <th>Đã chấm</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:set var="lastDate" value=""/>
                    <c:forEach var="iv" items="${interviews}">
                        <fmt:formatDate var="rowDateKey" value="${iv.interviewDate}" pattern="yyyy-MM-dd"/>
                        <fmt:formatDate var="rowDateLabel" value="${iv.interviewDate}" pattern="EEEE, dd/MM/yyyy"/>
                        <c:if test="${rowDateKey != lastDate}">
                            <tr class="iv-date-heading"><td colspan="10">${rowDateLabel}</td></tr>
                            <c:set var="lastDate" value="${rowDateKey}"/>
                        </c:if>
                        <tr>
                            <td><fmt:formatDate value="${iv.startTime}" pattern="HH:mm"/><c:if test="${not empty iv.endTime}"> - <fmt:formatDate value="${iv.endTime}" pattern="HH:mm"/></c:if></td>
                            <td><b>${iv.candidateName}</b></td>
                            <td>${iv.jobTitle}</td>
                            <td>Vòng ${iv.interviewRound}</td>
                            <td><span class="iv-badge iv-badge-blue">${iv.interviewType}</span></td>
                            <td>
                                <c:if test="${not empty iv.location}">${iv.location}</c:if>
                                <c:if test="${not empty iv.meetingLink}"><br><a href="${iv.meetingLink}" target="_blank" class="iv-small">${iv.meetingLink}</a></c:if>
                            </td>
                            <td>
                                <c:forEach var="p" items="${iv.participants}">
                                    <span class="iv-chip ${p.lead ? 'lead' : ''}"><span class="iv-avatar">${p.fullName.substring(0,1)}</span>${p.fullName}</span>
                                </c:forEach>
                            </td>
                            <td><span class="iv-badge iv-badge-teal">${iv.status}</span></td>
                            <td>
                                <c:choose>
                                    <c:when test="${iv.evaluationCount > 0}"><span class="iv-badge iv-badge-green">${iv.evaluationCount}/${iv.participants.size()}</span></c:when>
                                    <c:otherwise><span class="iv-badge iv-badge-gray">0/${iv.participants.size()}</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a class="iv-btn iv-btn-sm iv-btn-ghost" href="${pageContext.request.contextPath}/interview-evaluation?interviewId=${iv.interviewId}">Chấm điểm</a>
                                <a class="iv-btn iv-btn-sm iv-btn-ghost" href="${pageContext.request.contextPath}/apply-conclusion?applyId=${iv.applyId}">Kết luận</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="staff_footer.jsp"/>
