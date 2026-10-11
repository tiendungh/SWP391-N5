<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Barem đánh giá" scope="request"/>
<c:set var="currentNav" value="barem" scope="request"/>
<jsp:include page="staff_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Barem đánh giá phỏng vấn</h1>
        <p>Mỗi barem gắn với một vị trí đang mở, dùng để chấm điểm ứng viên khi phỏng vấn.</p>
    </div>
    <div class="iv-whoami">Xin chào, <b>${sessionScope.staffName}</b></div>
</div>

<c:if test="${not empty error}"><div class="iv-alert iv-alert-error">${error}</div></c:if>
<c:if test="${param.created == '1'}"><div class="iv-alert iv-alert-success">Đã tạo barem mới thành công.</div></c:if>

<c:choose>
    <%-- ================= No job post chosen yet: pick one ================= --%>
    <c:when test="${empty selectedJobPost}">
        <div class="iv-card">
            <h2>Chọn vị trí đang mở</h2>
            <c:choose>
                <c:when test="${empty openJobs}">
                    <div class="iv-empty">Hiện không có vị trí nào đang mở (Visible = 1).</div>
                </c:when>
                <c:otherwise>
                    <table class="iv-table">
                        <thead><tr><th>Vị trí</th><th>Phòng ban</th><th>Địa điểm</th><th>Hạn nộp</th><th></th></tr></thead>
                        <tbody>
                            <c:forEach var="j" items="${openJobs}">
                                <tr>
                                    <td><b>${j.title}</b></td>
                                    <td>${j.category}</td>
                                    <td>${j.location}</td>
                                    <td><fmt:formatDate value="${j.deadline}" pattern="dd/MM/yyyy"/></td>
                                    <td><a class="iv-btn iv-btn-sm" href="${pageContext.request.contextPath}/barem?jobPostId=${j.jobPostId}">Quản lý barem</a></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </c:when>

    <%-- ================= Job post chosen: list + create form ================= --%>
    <c:otherwise>
        <div class="iv-card">
            <h2>${selectedJobPost.title}</h2>
            <p class="iv-muted iv-small">${selectedJobPost.category} · ${selectedJobPost.location}</p>
            <a href="${pageContext.request.contextPath}/barem" class="iv-btn iv-btn-sm iv-btn-ghost">Đổi vị trí khác</a>
        </div>

        <div class="iv-card">
            <h2>Barem hiện có (${existingBarems.size()})</h2>
            <c:choose>
                <c:when test="${empty existingBarems}">
                    <div class="iv-empty">Vị trí này chưa có barem nào. Tạo barem mới bên dưới.</div>
                </c:when>
                <c:otherwise>
                    <table class="iv-table">
                        <thead><tr><th>Tên barem</th><th>Tổng điểm</th><th>Tạo lúc</th></tr></thead>
                        <tbody>
                            <c:forEach var="b" items="${existingBarems}">
                                <tr>
                                    <td><b>${b.baremName}</b><br><span class="iv-small iv-muted">${b.description}</span></td>
                                    <td>${b.totalScore}</td>
                                    <td><fmt:formatDate value="${b.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="iv-card">
            <h2>Tạo barem mới nhanh</h2>
            <form method="post" action="${pageContext.request.contextPath}/barem" id="baremForm">
                <input type="hidden" name="jobPostId" value="${jobPostId}"/>
                <c:if test="${not empty returnApplyId}"><input type="hidden" name="returnApplyId" value="${returnApplyId}"/></c:if>

                <div class="iv-form-row">
                    <div class="iv-field">
                        <label>Tên barem *</label>
                        <input type="text" name="baremName" placeholder="Vd: Barem vòng chuyên môn" required/>
                    </div>
                </div>
                <div class="iv-field" style="margin-bottom:14px;">
                    <label>Mô tả</label>
                    <textarea name="description" placeholder="Mô tả ngắn về barem này..."></textarea>
                </div>

                <h3>Tiêu chí chấm điểm</h3>
                <table class="iv-crit-table" id="critTable">
                    <thead>
                        <tr><th style="width:32%">Tên tiêu chí</th><th style="width:32%">Mô tả</th><th style="width:14%">Điểm tối đa</th><th style="width:14%">Trọng số (%)</th><th></th></tr>
                    </thead>
                    <tbody id="critBody">
                        <tr>
                            <td><input type="text" name="criteriaName" placeholder="Kiến thức chuyên môn" required/></td>
                            <td><input type="text" name="criteriaDescription" placeholder="Mô tả tiêu chí"/></td>
                            <td><input type="number" name="maxScore" min="1" step="1" value="20" required/></td>
                            <td><input type="number" name="weight" min="0" max="100" step="1" value="20" required/></td>
                            <td><button type="button" class="iv-crit-remove" onclick="removeCritRow(this)">×</button></td>
                        </tr>
                    </tbody>
                </table>
                <button type="button" class="iv-btn iv-btn-sm iv-btn-ghost" style="margin-top:10px;" onclick="addCritRow()">+ Thêm tiêu chí</button>
                <div id="weightSumBox"></div>

                <div style="margin-top:18px;">
                    <button type="submit" class="iv-btn">Tạo barem</button>
                    <c:choose>
                        <c:when test="${not empty returnApplyId}">
                            <a href="${pageContext.request.contextPath}/interview-schedule?applyId=${returnApplyId}" class="iv-btn iv-btn-ghost">Hủy, quay lại lên lịch</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/barem" class="iv-btn iv-btn-ghost">Hủy</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </form>
        </div>
    </c:otherwise>
</c:choose>

<script>
    function removeCritRow(btn) {
        var body = document.getElementById('critBody');
        if (body.rows.length > 1) {
            btn.closest('tr').remove();
        } else {
            alert('Barem cần ít nhất một tiêu chí.');
        }
        updateWeightSum();
    }

    function addCritRow() {
        var body = document.getElementById('critBody');
        var row = body.rows[0].cloneNode(true);
        row.querySelectorAll('input').forEach(function (input) {
            if (input.name === 'maxScore') { input.value = 20; }
            else if (input.name === 'weight') { input.value = 0; }
            else { input.value = ''; }
        });
        body.appendChild(row);
        updateWeightSum();
    }

    function updateWeightSum() {
        var weights = document.querySelectorAll('input[name="weight"]');
        var sum = 0;
        weights.forEach(function (w) { sum += parseFloat(w.value) || 0; });
        var box = document.getElementById('weightSumBox');
        box.textContent = 'Tổng trọng số: ' + sum + '% (nên bằng 100%)';
        box.className = (sum === 100) ? 'ok' : 'warn';
    }

    document.getElementById('critTable').addEventListener('input', function (e) {
        if (e.target.name === 'weight') updateWeightSum();
    });
    updateWeightSum();
</script>

<jsp:include page="staff_footer.jsp"/>
