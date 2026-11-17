<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Kết luận hồ sơ" scope="request"/>
<c:set var="currentNav" value="schedule" scope="request"/>
<jsp:include page="staff_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Kết luận hồ sơ</h1>
        <p>${apply.candidateName} — ${apply.jobTitle}</p>
    </div>
    <div class="iv-whoami">Xin chào, <b>${sessionScope.staffName}</b></div>
</div>

<c:if test="${not empty error}"><div class="iv-alert iv-alert-error">${error}</div></c:if>

<div class="iv-grid-2">
    <div>
        <div class="iv-card">
            <h2>Các vòng phỏng vấn (${interviews.size()})</h2>
            <c:choose>
                <c:when test="${empty interviews}">
                    <div class="iv-empty">Ứng viên chưa có vòng phỏng vấn nào.</div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="iv" items="${interviews}">
                        <div class="iv-score-item">
                            <div class="iv-score-head">
                                <b>Vòng ${iv.interviewRound} — ${iv.baremName}</b>
                                <span class="iv-badge iv-badge-teal">${iv.status}</span>
                            </div>
                            <span class="iv-small iv-muted">
                                <fmt:formatDate value="${iv.interviewDate}" pattern="dd/MM/yyyy"/> ·
                                <fmt:formatDate value="${iv.startTime}" pattern="HH:mm"/> · ${iv.interviewType}
                            </span>

                            <c:set var="evals" value="${evaluationsMap[iv.interviewId]}"/>
                            <c:choose>
                                <c:when test="${empty evals}">
                                    <p class="iv-small iv-muted" style="margin-top:8px;">Chưa có ai chấm điểm vòng này.</p>
                                </c:when>
                                <c:otherwise>
                                    <table class="iv-table" style="margin-top:10px;">
                                        <thead><tr><th>Người chấm</th><th>Điểm</th><th>Đề xuất</th><th>Nhận xét</th></tr></thead>
                                        <tbody>
                                            <c:forEach var="e" items="${evals}">
                                                <tr>
                                                    <td>${e.interviewerName}</td>
                                                    <td><b>${e.totalScore}</b></td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${e.recommendation == 'Pass'}"><span class="iv-badge iv-badge-green">Pass</span></c:when>
                                                            <c:when test="${e.recommendation == 'Fail'}"><span class="iv-badge iv-badge-red">Fail</span></c:when>
                                                            <c:otherwise><span class="iv-badge iv-badge-amber">${e.recommendation}</span></c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>${e.comment}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:otherwise>
                            </c:choose>
                            <a class="iv-btn iv-btn-sm iv-btn-ghost" style="margin-top:8px;"
                               href="${pageContext.request.contextPath}/interview-evaluation?interviewId=${iv.interviewId}">Chấm / sửa điểm</a>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div>
        <div class="iv-card">
            <h2>Thông tin hồ sơ</h2>
            <p><b>Ứng viên:</b> ${apply.candidateName}</p>
            <p><b>Email:</b> ${apply.candidateEmail}</p>
            <p><b>Vị trí:</b> ${apply.jobTitle}</p>
            <p><b>Trạng thái hiện tại:</b> <span class="iv-badge iv-badge-blue">${apply.status}</span></p>
            <c:if test="${not empty apply.finalResult}">
                <p><b>Kết luận trước:</b> ${apply.finalResult} bởi ${apply.conclusionByName}
                    (<fmt:formatDate value="${apply.conclusionDate}" pattern="dd/MM/yyyy HH:mm"/>)</p>
            </c:if>
        </div>

        <div class="iv-card">
            <h2>Ghi nhận kết luận</h2>
            <form method="post" action="${pageContext.request.contextPath}/apply-conclusion">
                <input type="hidden" name="applyId" value="${apply.applyId}"/>
                <div class="iv-field" style="margin-bottom:14px;">
                    <label>Kết quả cuối cùng *</label>
                    <select name="finalResult" required>
                        <option value="Pass" ${apply.finalResult == 'Pass' ? 'selected' : ''}>Trúng tuyển</option>
                        <option value="OnHold" ${apply.finalResult == 'OnHold' ? 'selected' : ''}>Đang xem xét</option>
                        <option value="Fail" ${apply.finalResult == 'Fail' ? 'selected' : ''}>Từ chối</option>
                    </select>
                </div>
                <div class="iv-field" style="margin-bottom:14px;">
                    <label>Ghi chú kết luận</label>
                    <textarea name="conclusionNote" placeholder="Tóm tắt lý do kết luận...">${apply.conclusionNote}</textarea>
                </div>
                <button type="submit" class="iv-btn">Lưu kết luận</button>
            </form>
        </div>
    </div>
</div>

<jsp:include page="staff_footer.jsp"/>
