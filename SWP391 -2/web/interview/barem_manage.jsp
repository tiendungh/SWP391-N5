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

<c:if test="${not empty errors}">
    <div class="iv-alert iv-alert-error">
        <ul style="margin:0;padding-left:18px;">
            <c:forEach var="e" items="${errors}"><li><c:out value="${e}"/></li></c:forEach>
        </ul>
    </div>
</c:if>
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
                    <table class="iv-table" data-page-size="10">
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
            <h2><c:out value="${selectedJobPost.title}"/></h2>
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
                    <table class="iv-table" data-page-size="3">
                        <thead><tr><th>Tên barem</th><th>Tổng điểm</th><th>Tạo lúc</th><th></th></tr></thead>
                        <tbody>
                            <c:forEach var="b" items="${existingBarems}">
                                <tr>
                                    <td><b><c:out value="${b.baremName}"/></b><br><span class="iv-small iv-muted"><c:out value="${b.description}"/></span></td>
                                    <td>${b.totalScore}</td>
                                    <td><fmt:formatDate value="${b.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                    <td><button type="button" class="iv-btn iv-btn-sm btn-barem-detail" data-target="baremDetail-${b.baremId}">Xem chi tiết</button></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <%-- Nội dung chi tiết từng barem (ẩn, được đưa vào popup khi bấm "Xem chi tiết") --%>
                    <c:forEach var="b" items="${existingBarems}">
                        <div id="baremDetail-${b.baremId}" hidden>
                            <dl class="bd-grid">
                                <div><dt>Tên barem</dt><dd><c:out value="${b.baremName}"/></dd></div>
                                <div><dt>Tổng điểm</dt><dd>${b.totalScore}</dd></div>
                                <div><dt>Người tạo</dt><dd><c:out value="${empty b.createdByName ? '—' : b.createdByName}"/></dd></div>
                                <div><dt>Tạo lúc</dt><dd><fmt:formatDate value="${b.createdAt}" pattern="dd/MM/yyyy HH:mm"/></dd></div>
                                <div class="bd-wide"><dt>Mô tả</dt><dd><c:out value="${empty b.description ? '—' : b.description}"/></dd></div>
                            </dl>
                            <div class="bd-section">Tiêu chí chấm điểm (${b.criteriaList.size()})</div>
                            <c:choose>
                                <c:when test="${empty b.criteriaList}">
                                    <div class="iv-empty">Barem này chưa có tiêu chí nào.</div>
                                </c:when>
                                <c:otherwise>
                                    <table class="iv-table">
                                        <thead><tr><th>#</th><th>Tiêu chí</th><th>Điểm tối đa</th><th>Trọng số</th></tr></thead>
                                        <tbody>
                                            <c:forEach var="cr" items="${b.criteriaList}" varStatus="st">
                                                <tr>
                                                    <td>${st.index + 1}</td>
                                                    <td><b><c:out value="${cr.criteriaName}"/></b><c:if test="${not empty cr.description}"><br><span class="iv-small iv-muted"><c:out value="${cr.description}"/></span></c:if></td>
                                                    <td>${cr.maxScore}</td>
                                                    <td>${cr.weight}%</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="iv-card">
            <h2>Tạo barem mới nhanh</h2>
            <form method="post" action="${pageContext.request.contextPath}/barem" id="baremForm" novalidate>
                <input type="hidden" name="jobPostId" value="${jobPostId}"/>
                <c:if test="${not empty returnApplyId}"><input type="hidden" name="returnApplyId" value="<c:out value='${returnApplyId}'/>"/></c:if>

                <div class="iv-form-row">
                    <div class="iv-field">
                        <label>Tên barem *</label>
                        <input type="text" name="baremName" maxlength="150" value="<c:out value='${formBaremName}'/>" placeholder="Vd: Barem vòng chuyên môn" required/>
                    </div>
                </div>
                <div class="iv-field" style="margin-bottom:14px;">
                    <label>Mô tả</label>
                    <textarea name="description" maxlength="500" placeholder="Mô tả ngắn về barem này..."><c:out value="${formDescription}"/></textarea>
                </div>

                <h3>Tiêu chí chấm điểm</h3>
                <table class="iv-crit-table" id="critTable">
                    <thead>
                        <tr><th style="width:32%">Tên tiêu chí</th><th style="width:32%">Mô tả</th><th style="width:14%">Điểm tối đa</th><th style="width:14%">Trọng số (%)</th><th></th></tr>
                    </thead>
                    <tbody id="critBody">
                        <c:choose>
                            <c:when test="${not empty formRows}">
                                <c:forEach var="r" items="${formRows}">
                                    <tr>
                                        <td><input type="text" name="criteriaName" maxlength="150" value="<c:out value='${r[0]}'/>" placeholder="Kiến thức chuyên môn" required/></td>
                                        <td><input type="text" name="criteriaDescription" maxlength="500" value="<c:out value='${r[1]}'/>" placeholder="Mô tả tiêu chí"/></td>
                                        <td><input type="number" name="maxScore" min="1" max="9999" step="1" value="<c:out value='${r[2]}'/>" required/></td>
                                        <td><input type="number" name="weight" min="0" max="100" step="1" value="<c:out value='${r[3]}'/>" required/></td>
                                        <td><button type="button" class="iv-crit-remove" onclick="removeCritRow(this)">×</button></td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td><input type="text" name="criteriaName" maxlength="150" placeholder="Kiến thức chuyên môn" required/></td>
                                    <td><input type="text" name="criteriaDescription" maxlength="500" placeholder="Mô tả tiêu chí"/></td>
                                    <td><input type="number" name="maxScore" min="1" max="9999" step="1" value="20" required/></td>
                                    <td><input type="number" name="weight" min="0" max="100" step="1" value="100" required/></td>
                                    <td><button type="button" class="iv-crit-remove" onclick="removeCritRow(this)">×</button></td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
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
        box.textContent = 'Tổng trọng số: ' + sum + '% (phải bằng 100%)';
        box.className = (sum === 100) ? 'ok' : 'warn';
    }

    document.getElementById('critTable').addEventListener('input', function (e) {
        if (e.target.name === 'weight') updateWeightSum();
    });
    updateWeightSum();

    // Client-side checks (the server re-validates everything).
    document.getElementById('baremForm').addEventListener('submit', function (e) {
        var errs = [];
        var name = this.elements['baremName'].value.trim();
        if (!name) errs.push('Vui lòng nhập tên barem.');
        else if (name.length > 150) errs.push('Tên barem không được dài quá 150 ký tự.');
        if (this.elements['description'].value.length > 500) errs.push('Mô tả barem không được dài quá 500 ký tự.');

        var seen = {};
        var sum = 0;
        var rows = document.getElementById('critBody').rows;
        for (var i = 0; i < rows.length; i++) {
            var cn = rows[i].querySelector('[name=criteriaName]').value.trim();
            var cd = rows[i].querySelector('[name=criteriaDescription]').value;
            var mx = parseFloat(rows[i].querySelector('[name=maxScore]').value);
            var w = parseFloat(rows[i].querySelector('[name=weight]').value);
            if (!cn) { errs.push('Tiêu chí #' + (i + 1) + ': vui lòng nhập tên.'); }
            else if (seen[cn.toLowerCase()]) { errs.push('Tiêu chí #' + (i + 1) + ': tên bị trùng trong barem.'); }
            seen[cn.toLowerCase()] = true;
            if (cn.length > 150) errs.push('Tiêu chí #' + (i + 1) + ': tên không được dài quá 150 ký tự.');
            if (cd.length > 500) errs.push('Tiêu chí #' + (i + 1) + ': mô tả không được dài quá 500 ký tự.');
            if (!(mx > 0)) errs.push('Tiêu chí #' + (i + 1) + ': điểm tối đa phải lớn hơn 0.');
            if (isNaN(w) || w < 0 || w > 100) errs.push('Tiêu chí #' + (i + 1) + ': trọng số phải từ 0 đến 100.');
            else sum += w;
        }
        if (Math.abs(sum - 100) > 1e-9) errs.push('Tổng trọng số phải bằng 100% (hiện là ' + sum + '%).');

        if (errs.length) { e.preventDefault(); alert(errs.join('\n')); }
    });
</script>

<%-- ================= Barem detail modal ================= --%>
<style>
    .bd-overlay { position: fixed; inset: 0; background: rgba(15, 23, 42, .55); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: 16px; }
    .bd-overlay[hidden] { display: none; }
    .bd-modal { background: #fff; border-radius: 12px; width: 100%; max-width: 760px; max-height: 90vh; display: flex; flex-direction: column; box-shadow: 0 20px 50px rgba(0, 0, 0, .3); }
    .bd-head { display: flex; align-items: center; justify-content: space-between; padding: 16px 20px; border-bottom: 1px solid #e2e8f0; }
    .bd-head h2 { margin: 0; font-size: 1.15rem; }
    .bd-close { border: 0; background: transparent; font-size: 1.6rem; line-height: 1; cursor: pointer; color: #64748b; }
    .bd-body { padding: 8px 20px 20px; overflow-y: auto; }
    .bd-section { margin: 16px 0 8px; font-weight: 700; color: #1e3a8a; font-size: .85rem; text-transform: uppercase; letter-spacing: .04em; }
    .bd-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px 20px; margin: 12px 0 0; }
    .bd-grid dt { font-size: .78rem; color: #64748b; margin: 0 0 2px; }
    .bd-grid dd { margin: 0; font-size: .95rem; color: #0f172a; word-break: break-word; white-space: pre-wrap; }
    .bd-wide { grid-column: 1 / -1; }
    @media (max-width: 600px) { .bd-grid { grid-template-columns: 1fr; } }
</style>
<div id="bdOverlay" class="bd-overlay" hidden>
    <div class="bd-modal" role="dialog" aria-modal="true" aria-labelledby="bdTitle">
        <div class="bd-head">
            <h2 id="bdTitle">Chi tiết barem</h2>
            <button type="button" class="bd-close" id="bdClose" aria-label="Đóng">&times;</button>
        </div>
        <div class="bd-body" id="bdBody"></div>
    </div>
</div>

<script>
    (function () {
        var overlay = document.getElementById('bdOverlay');
        var body = document.getElementById('bdBody');
        if (!overlay) return;
        document.querySelectorAll('.btn-barem-detail').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var src = document.getElementById(btn.getAttribute('data-target'));
                if (!src) return;
                body.innerHTML = src.innerHTML;
                overlay.hidden = false;
            });
        });
        function closeDetail() { overlay.hidden = true; }
        document.getElementById('bdClose').addEventListener('click', closeDetail);
        overlay.addEventListener('click', function (e) { if (e.target === overlay) closeDetail(); });
        document.addEventListener('keydown', function (e) { if (e.key === 'Escape') closeDetail(); });
    })();
</script>

<jsp:include page="staff_footer.jsp"/>
