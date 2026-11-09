<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Sơ lọc hồ sơ" scope="request"/>
<c:set var="currentNav" value="screening" scope="request"/>
<jsp:include page="staff_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Sơ lọc hồ sơ</h1>
        <p>Lọc ứng viên theo kinh nghiệm / học vấn / mức lương mong muốn, rồi duyệt Pass hoặc Loại hàng loạt.</p>
    </div>
    <div class="iv-whoami">Xin chào, <b>${sessionScope.staffName}</b></div>
</div>

<c:if test="${param.updated == '1'}"><div class="iv-alert iv-alert-success">Đã cập nhật trạng thái các hồ sơ đã chọn.</div></c:if>

<c:choose>
    <c:when test="${empty jobPostId}">
        <div class="iv-card">
            <h2>Chọn JD cần sơ lọc</h2>
            <c:choose>
                <c:when test="${empty jobPosts}">
                    <div class="iv-empty">Chưa có JD nào. Vào mục "Tạo JD" để tạo tin tuyển dụng.</div>
                </c:when>
                <c:otherwise>
                    <table class="iv-table">
                        <thead><tr><th>Vị trí</th><th>Số hồ sơ</th><th>Trạng thái JD</th><th></th></tr></thead>
                        <tbody>
                            <c:forEach var="j" items="${jobPosts}">
                                <tr>
                                    <td><b>${j.title}</b><br><span class="iv-small iv-muted">${j.category} · ${j.location}</span></td>
                                    <td><span class="iv-badge iv-badge-blue">${j.applyCount}</span></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${j.visible}"><span class="iv-badge iv-badge-green">Đang mở</span></c:when>
                                            <c:otherwise><span class="iv-badge iv-badge-gray">Đã đóng</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><a class="iv-btn iv-btn-sm" href="${pageContext.request.contextPath}/screening?jobPostId=${j.jobPostId}">Sơ lọc</a></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </c:when>

    <c:otherwise>
        <div class="iv-card">
            <a href="${pageContext.request.contextPath}/screening" class="iv-btn iv-btn-sm iv-btn-ghost">← Chọn JD khác</a>
            <form method="get" action="${pageContext.request.contextPath}/screening" class="iv-form-row" style="margin-top:14px;margin-bottom:0;">
                <input type="hidden" name="jobPostId" value="${jobPostId}"/>
                <div class="iv-field">
                    <label>Kinh nghiệm tối thiểu (năm)</label>
                    <input type="number" name="minExp" min="0" value="${selectedMinExp}"/>
                </div>
                <div class="iv-field">
                    <label>Học vấn chứa từ khóa</label>
                    <input type="text" name="education" placeholder="Vd: Bách Khoa" value="${selectedEducation}"/>
                </div>
                <div class="iv-field">
                    <label>Lương hiện tại từ</label>
                    <input type="number" name="minSalary" min="0" step="0.01" value="${selectedMinSalary}"/>
                </div>
                <div class="iv-field">
                    <label>Đến</label>
                    <input type="number" name="maxSalary" min="0" step="0.01" value="${selectedMaxSalary}"/>
                </div>
                <div class="iv-field">
                    <label>Trạng thái</label>
                    <select name="status">
                        <option value="Pending" ${selectedStatus == 'Pending' ? 'selected' : ''}>Chưa xử lý</option>
                        <option value="Shortlisted" ${selectedStatus == 'Shortlisted' ? 'selected' : ''}>Đã Pass</option>
                        <option value="Rejected" ${selectedStatus == 'Rejected' ? 'selected' : ''}>Đã loại</option>
                        <option value="All" ${selectedStatus == 'All' ? 'selected' : ''}>Tất cả</option>
                    </select>
                </div>
                <div class="iv-field" style="justify-content:flex-end;">
                    <button type="submit" class="iv-btn">Lọc</button>
                </div>
            </form>
        </div>

        <div class="iv-card">
            <h2>Kết quả (${applies.size()} hồ sơ)</h2>
            <c:choose>
                <c:when test="${empty applies}">
                    <div class="iv-empty">Không có hồ sơ nào khớp bộ lọc.</div>
                </c:when>
                <c:otherwise>
                    <form method="post" action="${pageContext.request.contextPath}/screening" id="screenForm">
                        <input type="hidden" name="jobPostId" value="${jobPostId}"/>
                        <table class="iv-table">
                            <thead>
                                <tr>
                                    <th><input type="checkbox" id="checkAll"/></th>
                                    <th>Ứng viên</th>
                                    <th>Kinh nghiệm</th>
                                    <th>Học vấn</th>
                                    <th>Lương mong muốn</th>
                                    <th>CV</th>
                                    <th>Trạng thái</th>
                                    <th>Ngày nộp</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="a" items="${applies}">
                                    <tr>
                                        <td><input type="checkbox" name="applyIds" value="${a.applyId}" class="rowCheck"/></td>
                                        <td>
                                            <b><c:out value="${a.candidateName}"/></b><br>
                                            <span class="iv-small iv-muted"><c:out value="${a.candidateEmail}"/> · <c:out value="${a.candidatePhone}"/></span>
                                        </td>
                                        <td>${a.cvNumberExp} năm</td>
                                        <td class="iv-small"><c:out value="${a.cvEducation}"/><br><span class="iv-muted"><c:out value="${a.cvField}"/></span></td>
                                        <td>${a.cvCurrentSalary}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty a.cvFileData}">
                                                    <a href="${pageContext.request.contextPath}/uploads/cv/${a.cvFileData}" target="_blank" class="iv-btn iv-btn-sm iv-btn-ghost">Xem file</a>
                                                </c:when>
                                                <c:otherwise><span class="iv-muted iv-small">Không có file</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${a.status == 'Shortlisted'}"><span class="iv-badge iv-badge-green">Đã Pass</span></c:when>
                                                <c:when test="${a.status == 'Rejected'}"><span class="iv-badge iv-badge-red">Đã loại</span></c:when>
                                                <c:otherwise><span class="iv-badge iv-badge-gray">${a.status}</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="iv-small"><fmt:formatDate value="${a.dayCreate}" pattern="dd/MM/yyyy"/></td>
                                        <td>
                                            <button type="button" class="iv-btn iv-btn-sm btn-detail"
                                                    data-name="<c:out value='${empty a.cvFullName ? a.candidateName : a.cvFullName}'/>"
                                                    data-email="<c:out value='${empty a.cvEmail ? a.candidateEmail : a.cvEmail}'/>"
                                                    data-phone="<c:out value='${a.candidatePhone}'/>"
                                                    data-address="<c:out value='${a.cvAddress}'/>"
                                                    data-nationality="<c:out value='${a.cvNationality}'/>"
                                                    data-position="<c:out value='${a.cvPosition}'/>"
                                                    data-exp="<c:out value='${a.cvNumberExp}'/>"
                                                    data-salary="<c:out value='${a.cvCurrentSalary}'/>"
                                                    data-education="<c:out value='${a.cvEducation}'/>"
                                                    data-field="<c:out value='${a.cvField}'/>"
                                                    data-birthday="<fmt:formatDate value='${a.cvBirthday}' pattern='dd/MM/yyyy'/>"
                                                    data-gender="<c:out value='${a.cvGender}'/>"
                                                    data-note="<c:out value='${a.note}'/>"
                                                    data-file="<c:if test='${not empty a.cvFileData}'>${pageContext.request.contextPath}/uploads/cv/<c:out value='${a.cvFileData}'/></c:if>"
                                                    data-status="<c:out value='${a.status}'/>"
                                                    data-created="<fmt:formatDate value='${a.dayCreate}' pattern='dd/MM/yyyy HH:mm'/>">Xem chi tiết</button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>

                        <div style="margin-top:14px;">
                            <button type="submit" name="decision" value="Shortlisted" class="iv-btn">Pass các hồ sơ đã chọn</button>
                            <button type="submit" name="decision" value="Rejected" class="iv-btn iv-btn-danger">Loại các hồ sơ đã chọn</button>
                        </div>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </c:otherwise>
</c:choose>

<style>
    .cd-overlay { position: fixed; inset: 0; background: rgba(15, 23, 42, .55); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: 16px; }
    .cd-overlay[hidden] { display: none; }
    .cd-modal { background: #fff; border-radius: 12px; width: 100%; max-width: 720px; max-height: 90vh; display: flex; flex-direction: column; box-shadow: 0 20px 50px rgba(0, 0, 0, .3); }
    .cd-head { display: flex; align-items: center; justify-content: space-between; padding: 16px 20px; border-bottom: 1px solid #e2e8f0; }
    .cd-head h2 { margin: 0; font-size: 1.15rem; }
    .cd-close { border: 0; background: transparent; font-size: 1.6rem; line-height: 1; cursor: pointer; color: #64748b; }
    .cd-body { padding: 8px 20px 20px; overflow-y: auto; }
    .cd-section { margin: 16px 0 8px; font-weight: 700; color: #1e3a8a; font-size: .85rem; text-transform: uppercase; letter-spacing: .04em; }
    .cd-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px 20px; margin: 0; }
    .cd-grid dt { font-size: .78rem; color: #64748b; margin: 0 0 2px; }
    .cd-grid dd { margin: 0; font-size: .95rem; color: #0f172a; word-break: break-word; white-space: pre-wrap; }
    .cd-wide { grid-column: 1 / -1; }
    @media (max-width: 600px) { .cd-grid { grid-template-columns: 1fr; } }
</style>
<div id="cdOverlay" class="cd-overlay" hidden>
    <div class="cd-modal" role="dialog" aria-modal="true" aria-labelledby="cdTitle">
        <div class="cd-head">
            <h2 id="cdTitle">Thông tin ứng viên đã điền</h2>
            <button type="button" class="cd-close" id="cdClose" aria-label="Đóng">&times;</button>
        </div>
        <div class="cd-body">
            <div class="cd-section">Thông tin liên hệ</div>
            <dl class="cd-grid">
                <div><dt>Họ và tên</dt><dd data-f="name"></dd></div>
                <div><dt>Email</dt><dd data-f="email"></dd></div>
                <div><dt>Số điện thoại</dt><dd data-f="phone"></dd></div>
                <div><dt>Quốc tịch</dt><dd data-f="nationality"></dd></div>
                <div class="cd-wide"><dt>Địa chỉ</dt><dd data-f="address"></dd></div>
            </dl>
            <div class="cd-section">Thông tin ứng tuyển</div>
            <dl class="cd-grid">
                <div><dt>Vị trí ứng tuyển</dt><dd data-f="position"></dd></div>
                <div><dt>Số năm kinh nghiệm</dt><dd data-f="exp"></dd></div>
                <div><dt>Lương hiện tại / mong muốn</dt><dd data-f="salary"></dd></div>
                <div><dt>Ngày sinh</dt><dd data-f="birthday"></dd></div>
                <div><dt>Giới tính</dt><dd data-f="gender"></dd></div>
                <div><dt>Học vấn</dt><dd data-f="education"></dd></div>
                <div class="cd-wide"><dt>Chuyên ngành / Lĩnh vực</dt><dd data-f="field"></dd></div>
                <div class="cd-wide"><dt>Lời nhắn / Cover note</dt><dd data-f="note"></dd></div>
            </dl>
            <div class="cd-section">Hồ sơ</div>
            <dl class="cd-grid">
                <div><dt>File CV</dt><dd><a id="cdFile" href="#" target="_blank" rel="noopener">Xem file</a><span id="cdNoFile">Không có file</span></dd></div>
                <div><dt>Trạng thái</dt><dd data-f="status"></dd></div>
                <div><dt>Ngày nộp</dt><dd data-f="created"></dd></div>
            </dl>
        </div>
    </div>
</div>

<script>
    var checkAll = document.getElementById('checkAll');
    if (checkAll) {
        checkAll.addEventListener('change', function () {
            document.querySelectorAll('.rowCheck').forEach(function (cb) { cb.checked = checkAll.checked; });
        });
    }

    (function () {
        var overlay = document.getElementById('cdOverlay');
        if (!overlay) return;
        var statusLabels = {Pending: 'Chưa xử lý', Shortlisted: 'Đã Pass', Rejected: 'Đã loại'};

        function openDetail(btn) {
            var d = btn.dataset;
            overlay.querySelectorAll('[data-f]').forEach(function (el) {
                var key = el.getAttribute('data-f');
                var v = (d[key] || '').trim();
                if (key === 'status') v = statusLabels[v] || v;
                if (key === 'exp' && v) v += ' năm';
                el.textContent = v || '—';
            });
            var link = document.getElementById('cdFile');
            var noFile = document.getElementById('cdNoFile');
            if (d.file) {
                link.href = d.file;
                link.style.display = '';
                noFile.style.display = 'none';
            } else {
                link.style.display = 'none';
                noFile.style.display = '';
            }
            overlay.hidden = false;
        }
        function closeDetail() { overlay.hidden = true; }

        document.querySelectorAll('.btn-detail').forEach(function (btn) {
            btn.addEventListener('click', function () { openDetail(btn); });
        });
        document.getElementById('cdClose').addEventListener('click', closeDetail);
        overlay.addEventListener('click', function (e) { if (e.target === overlay) closeDetail(); });
        document.addEventListener('keydown', function (e) { if (e.key === 'Escape') closeDetail(); });
    })();
</script>

<jsp:include page="staff_footer.jsp"/>
