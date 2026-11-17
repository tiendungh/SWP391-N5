<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Lên lịch phỏng vấn" scope="request"/>
<c:set var="currentNav" value="schedule" scope="request"/>
<jsp:include page="staff_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Lên lịch phỏng vấn</h1>
        <p>Chọn hồ sơ ứng tuyển cần phỏng vấn, gắn barem và mời người tham gia.</p>
    </div>
    <div class="iv-whoami">Xin chào, <b>${sessionScope.staffName}</b></div>
</div>

<c:if test="${not empty error}">
    <div class="iv-alert iv-alert-error">${error}</div>
</c:if>
<c:if test="${param.baremCreated == '1'}">
    <div class="iv-alert iv-alert-success">Đã tạo barem mới. Chọn barem bên dưới để tiếp tục lên lịch.</div>
</c:if>

<c:choose>
    <%-- ================= MODE 1: worklist of applications ================= --%>
    <c:when test="${empty apply}">
        <div class="iv-card">
            <h2>Hồ sơ đang chờ xử lý (${applies.size()})</h2>
            <c:choose>
                <c:when test="${empty applies}">
                    <div class="iv-empty">Không có hồ sơ nào đang chờ lên lịch phỏng vấn.</div>
                </c:when>
                <c:otherwise>
                    <table class="iv-table">
                        <thead>
                            <tr>
                                <th>Ứng viên</th>
                                <th>Vị trí ứng tuyển</th>
                                <th>Trạng thái</th>
                                <th>Đã lên lịch</th>
                                <th>Ngày nộp</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="a" items="${applies}">
                                <tr>
                                    <td><b>${a.candidateName}</b><br><span class="iv-small iv-muted">${a.candidateEmail}</span></td>
                                    <td>${a.jobTitle}</td>
                                    <td><span class="iv-badge iv-badge-blue">${a.status}</span></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${a.scheduledInterviewCount > 0}">
                                                <span class="iv-badge iv-badge-teal">${a.scheduledInterviewCount} vòng</span>
                                            </c:when>
                                            <c:otherwise><span class="iv-badge iv-badge-gray">Chưa có</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><fmt:formatDate value="${a.dayCreate}" pattern="dd/MM/yyyy"/></td>
                                    <td>
                                        <a class="iv-btn iv-btn-sm" href="${pageContext.request.contextPath}/interview-schedule?applyId=${a.applyId}">Lên lịch</a>
                                        <a class="iv-btn iv-btn-sm iv-btn-ghost" href="${pageContext.request.contextPath}/apply-conclusion?applyId=${a.applyId}">Kết luận</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </c:when>

    <%-- ================= MODE 2: schedule form for one application ================= --%>
    <c:otherwise>
        <div class="iv-grid-2">
            <div class="iv-card">
                <h2>Đặt lịch phỏng vấn — ${apply.candidateName} (${apply.jobTitle})</h2>

                <c:if test="${empty baremsForJob}">
                    <div class="iv-alert iv-alert-error">
                        Vị trí <b>${apply.jobTitle}</b> chưa có barem đánh giá nào.
                        <a href="${pageContext.request.contextPath}/barem?jobPostId=${apply.jobPostId}&returnApplyId=${apply.applyId}">Tạo barem ngay</a>
                        để có thể lên lịch phỏng vấn.
                    </div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/interview-schedule">
                    <input type="hidden" name="applyId" value="${apply.applyId}"/>

                    <div class="iv-form-row">
                        <div class="iv-field">
                            <label>Barem đánh giá *</label>
                            <select name="baremId" required ${empty baremsForJob ? 'disabled' : ''}>
                                <c:forEach var="b" items="${baremsForJob}">
                                    <option value="${b.baremId}">${b.baremName} (tổng ${b.totalScore} điểm)</option>
                                </c:forEach>
                            </select>
                            <span class="iv-hint">
                                <a href="${pageContext.request.contextPath}/barem?jobPostId=${apply.jobPostId}&returnApplyId=${apply.applyId}">+ Tạo barem mới cho vị trí này</a>
                            </span>
                        </div>
                        <div class="iv-field">
                            <label>Vòng phỏng vấn</label>
                            <input type="number" name="interviewRound" min="1" value="${nextRound}" required ${empty baremsForJob ? 'disabled' : ''}/>
                        </div>
                    </div>

                    <div class="iv-form-row">
                        <div class="iv-field">
                            <label>Ngày phỏng vấn *</label>
                            <input type="date"
                                   name="interviewDate"
                                   id="interviewDate"
                                   required
                                   ${empty baremsForJob ? 'disabled' : ''}/>
                        </div>
                        <div class="iv-field">
                            <label>Giờ bắt đầu *</label>
                            <input type="time"
                                   name="startTime"
                                   id="startTime"
                                   required
                                   ${empty baremsForJob ? 'disabled' : ''}/>
                        </div>
                        <div class="iv-field">
                            <label>Giờ kết thúc *</label>
                            <input type="time"
                                   name="endTime"
                                   id="endTime"
                                   required
                                   ${empty baremsForJob ? 'disabled' : ''}/>
                        </div>
                    </div>

                    <div class="iv-form-row">
                        <div class="iv-field">
                            <label>Hình thức *</label>
                            <select name="interviewType" required ${empty baremsForJob ? 'disabled' : ''}>
                                <option value="Offline">Trực tiếp (Offline)</option>
                                <option value="Online">Trực tuyến (Online)</option>
                                <option value="Phone">Điện thoại</option>
                            </select>
                        </div>
                        <div class="iv-field">
                            <label>Địa điểm</label>
                            <input type="text" name="location" placeholder="Phòng họp / Địa chỉ" ${empty baremsForJob ? 'disabled' : ''}/>
                        </div>
                        <div class="iv-field">
                            <label>Link họp trực tuyến</label>
                            <input type="text" name="meetingLink" placeholder="https://meet..." ${empty baremsForJob ? 'disabled' : ''}/>
                        </div>
                    </div>

                    <div class="iv-field" style="margin-bottom:14px;">
                        <label>Ghi chú</label>
                        <textarea name="note" placeholder="Ghi chú cho buổi phỏng vấn..." ${empty baremsForJob ? 'disabled' : ''}></textarea>
                    </div>

                    <h3>Người tham gia phỏng vấn *</h3>
                    <p class="iv-hint" style="margin-top:-8px;">Chọn các thành viên tham gia. Tick "Trưởng nhóm" cho đúng 1 người chủ trì.</p>
                    <table class="iv-table" style="margin-bottom:16px;">
                        <thead><tr><th>Chọn</th><th>Họ tên</th><th>Vai trò</th><th>Trưởng nhóm</th></tr></thead>
                        <tbody>
                            <c:forEach var="s" items="${allStaff}">
                                <tr>
                                    <td><input type="checkbox" name="participantIds" value="${s.staffId}" ${empty baremsForJob ? 'disabled' : ''}/></td>
                                    <td>${s.fullName}</td>
                                    <td><span class="iv-badge iv-badge-gray">${s.roleName}</span></td>
                                    <td><input type="radio" name="leadInterviewerId" value="${s.staffId}" ${empty baremsForJob ? 'disabled' : ''}/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <button type="submit" class="iv-btn" ${empty baremsForJob ? 'disabled' : ''}>Xác nhận đặt lịch</button>
                    <a href="${pageContext.request.contextPath}/interview-schedule" class="iv-btn iv-btn-ghost">Quay lại danh sách</a>
                </form>
            </div>

            <div>
                <div class="iv-card">
                    <h2>Thông tin hồ sơ</h2>
                    <p><b>Ứng viên:</b> ${apply.candidateName}</p>
                    <p><b>Email:</b> ${apply.candidateEmail}</p>
                    <p><b>SĐT:</b> ${apply.candidatePhone}</p>
                    <p><b>Vị trí CV:</b> ${apply.cvPosition}</p>
                    <p><b>Trạng thái:</b> <span class="iv-badge iv-badge-blue">${apply.status}</span></p>
                </div>

                <div class="iv-card">
                    <h2>Lịch sử phỏng vấn (${pastInterviews.size()})</h2>
                    <c:choose>
                        <c:when test="${empty pastInterviews}">
                            <div class="iv-empty">Chưa có vòng phỏng vấn nào.</div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="iv" items="${pastInterviews}">
                                <div class="iv-score-item">
                                    <div class="iv-score-head">
                                        <b>Vòng ${iv.interviewRound} — ${iv.baremName}</b>
                                        <span class="iv-badge iv-badge-teal">${iv.status}</span>
                                    </div>
                                    <span class="iv-small iv-muted">
                                        <fmt:formatDate value="${iv.interviewDate}" pattern="dd/MM/yyyy"/> ·
                                        <fmt:formatDate value="${iv.startTime}" pattern="HH:mm"/> · ${iv.interviewType}
                                    </span>
                                    <div style="margin-top:8px;">
                                        <c:forEach var="p" items="${iv.participants}">
                                            <span class="iv-chip ${p.lead ? 'lead' : ''}"><span class="iv-avatar">${p.fullName.substring(0,1)}</span>${p.fullName}</span>
                                            </c:forEach>
                                    </div>
                                    <a class="iv-btn iv-btn-sm iv-btn-ghost" style="margin-top:8px;"
                                       href="${pageContext.request.contextPath}/interview-evaluation?interviewId=${iv.interviewId}">Chấm điểm</a>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        const dateInput = document.getElementById("interviewDate");
        const startInput = document.getElementById("startTime");
        const endInput = document.getElementById("endTime");

        const now = new Date();

        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, "0");
        const day = String(now.getDate()).padStart(2, "0");

        const today = year + "-" + month + "-" + day;

        dateInput.min = today;

        function validateTime() {
            if (!startInput.value || !endInput.value) {
                endInput.setCustomValidity("");
                return;
            }

            if (endInput.value <= startInput.value) {
                endInput.setCustomValidity(
                        "Giờ kết thúc phải lớn hơn giờ bắt đầu."
                        );
            } else {
                endInput.setCustomValidity("");
            }
        }

        startInput.addEventListener("change", validateTime);
        endInput.addEventListener("change", validateTime);

        dateInput.addEventListener("change", function () {
            if (dateInput.value < today) {
                dateInput.setCustomValidity(
                        "Ngày phỏng vấn không được ở trong quá khứ."
                        );
            } else {
                dateInput.setCustomValidity("");
            }
        });
    });
</script>
<jsp:include page="staff_footer.jsp"/>
