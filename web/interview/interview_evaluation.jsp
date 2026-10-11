<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Chấm điểm phỏng vấn" scope="request"/>
<c:set var="currentNav" value="calendar" scope="request"/>
<jsp:include page="staff_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Chấm điểm phỏng vấn</h1>
        <p>${interview.candidateName} — ${interview.jobTitle} — Vòng ${interview.interviewRound}</p>
    </div>
    <div class="iv-whoami">Người chấm: <b>${sessionScope.staffName}</b></div>
</div>

<c:if test="${not empty error}"><div class="iv-alert iv-alert-error">${error}</div></c:if>

<div class="iv-grid-2">
    <div class="iv-card">
        <h2>Barem: ${barem.baremName} <span class="iv-muted iv-small">(tổng ${barem.totalScore} điểm)</span></h2>
        <c:if test="${not empty barem.description}"><p class="iv-muted iv-small">${barem.description}</p></c:if>

        <c:if test="${empty barem.criteriaList}">
            <div class="iv-empty">Barem này chưa có tiêu chí nào.</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/interview-evaluation">
            <input type="hidden" name="interviewId" value="${interview.interviewId}"/>

            <c:forEach var="cri" items="${barem.criteriaList}">
                <div class="iv-score-item">
                    <div class="iv-score-head">
                        <b>${cri.criteriaName}</b>
                        <span>Tối đa ${cri.maxScore} điểm · Trọng số ${cri.weight}</span>
                    </div>
                    <c:if test="${not empty cri.description}"><p class="iv-small iv-muted" style="margin:0 0 8px;">${cri.description}</p></c:if>
                    <input type="hidden" name="criteriaId" value="${cri.criteriaId}"/>
                    <div class="iv-form-row" style="margin-bottom:0;">
                        <div class="iv-field" style="flex:0 0 120px;">
                            <label>Điểm</label>
                            <input class="iv-score-input" type="number" name="score" min="0" max="${cri.maxScore}" step="0.5"
                                   value="${cri.enteredScore}" required/>
                        </div>
                        <div class="iv-field">
                            <label>Nhận xét tiêu chí</label>
                            <input type="text" name="detailComment" value="${cri.enteredComment}" placeholder="Nhận xét ngắn gọn..."/>
                        </div>
                    </div>
                </div>
            </c:forEach>

            <h3>Kết luận của bạn</h3>
            <div class="iv-form-row">
                <div class="iv-field">
                    <label>Đề xuất</label>
                    <select name="recommendation">
                        <option value="Pass" ${existing.recommendation == 'Pass' ? 'selected' : ''}>Đề xuất Pass</option>
                        <option value="Consider" ${existing.recommendation == 'Consider' ? 'selected' : ''}>Cân nhắc thêm</option>
                        <option value="Fail" ${existing.recommendation == 'Fail' ? 'selected' : ''}>Đề xuất Fail</option>
                    </select>
                </div>
            </div>
            <div class="iv-field" style="margin-bottom:16px;">
                <label>Nhận xét tổng quan</label>
                <textarea name="comment" placeholder="Nhận xét chung về ứng viên...">${existing.comment}</textarea>
            </div>

            <button type="submit" class="iv-btn" ${empty barem.criteriaList ? 'disabled' : ''}>
                ${empty existing ? 'Lưu đánh giá' : 'Cập nhật đánh giá'}
            </button>
            <a href="${pageContext.request.contextPath}/interview-calendar" class="iv-btn iv-btn-ghost">Quay lại lịch</a>
        </form>
    </div>

    <div>
        <div class="iv-card">
            <h2>Thông tin buổi phỏng vấn</h2>
            <p><b>Ngày:</b> <fmt:formatDate value="${interview.interviewDate}" pattern="dd/MM/yyyy"/></p>
            <p><b>Giờ:</b> <fmt:formatDate value="${interview.startTime}" pattern="HH:mm"/>
                <c:if test="${not empty interview.endTime}"> - <fmt:formatDate value="${interview.endTime}" pattern="HH:mm"/></c:if>
            </p>
            <p><b>Hình thức:</b> ${interview.interviewType}</p>
            <c:if test="${not empty interview.location}"><p><b>Địa điểm:</b> ${interview.location}</p></c:if>
            <c:if test="${not empty interview.meetingLink}"><p><b>Link:</b> <a href="${interview.meetingLink}" target="_blank">${interview.meetingLink}</a></p></c:if>
            <p><b>Trạng thái:</b> <span class="iv-badge iv-badge-teal">${interview.status}</span></p>
        </div>
        <div class="iv-card">
            <h2>Người tham gia (${interview.participants.size()})</h2>
            <c:forEach var="p" items="${interview.participants}">
                <div style="margin-bottom:6px;">
                    <span class="iv-chip ${p.lead ? 'lead' : ''}"><span class="iv-avatar">${p.fullName.substring(0,1)}</span>${p.fullName}</span>
                    <c:if test="${p.lead}"><span class="iv-badge iv-badge-amber iv-small">Trưởng nhóm</span></c:if>
                </div>
            </c:forEach>
        </div>
    </div>
</div>

<jsp:include page="staff_footer.jsp"/>
