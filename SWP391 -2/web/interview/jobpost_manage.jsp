<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Tạo JD" scope="request"/>
<c:set var="currentNav" value="jobpost" scope="request"/>
<jsp:include page="staff_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Job Description (JD)</h1>
        <p>Tạo tin tuyển dụng mới và quản lý các JD đang mở / đã đóng.</p>
    </div>
    <div class="iv-whoami">Xin chào, <b>${sessionScope.staffName}</b></div>
</div>

<c:if test="${not empty error}"><div class="iv-alert iv-alert-error">${error}</div></c:if>
<c:if test="${param.created == '1'}"><div class="iv-alert iv-alert-success">Đã tạo JD mới thành công.</div></c:if>
<c:if test="${param.updated == '1'}"><div class="iv-alert iv-alert-success">Đã cập nhật JD thành công.</div></c:if>

<%-- Stacked layout: list (full width) + form (full width). While editing, the form moves to the top. --%>
<div style="display:flex; flex-direction:column;">
    <div class="iv-card" style="order:1;">
        <div style="display:flex; align-items:center; justify-content:space-between; gap:12px; flex-wrap:wrap; margin-bottom:6px;">
            <h2 style="margin:0;">Danh sách JD (${jobPosts.size()})</h2>
            <a href="${pageContext.request.contextPath}/job-post#jd-form" class="iv-btn iv-btn-sm">+ Tạo JD mới</a>
        </div>
        <c:choose>
            <c:when test="${empty jobPosts}">
                <div class="iv-empty">Chưa có JD nào. Tạo JD đầu tiên ở form bên dưới.</div>
            </c:when>
            <c:otherwise>
                <table class="iv-table" data-page-size="3">
                    <thead><tr><th>Vị trí</th><th>Lương</th><th>Hạn nộp</th><th>Ứng tuyển</th><th>Trạng thái</th><th></th></tr></thead>
                    <tbody>
                        <c:forEach var="j" items="${jobPosts}">
                            <tr>
                                <td><b>${j.title}</b><br><span class="iv-small iv-muted">${j.category} · ${j.location}</span></td>
                                <td class="iv-small">
                                    <c:if test="${not empty j.offerMin}">${j.offerMin} - ${j.offerMax}</c:if>
                                    <c:if test="${empty j.offerMin}">Thỏa thuận</c:if>
                                </td>
                                <td class="iv-small"><fmt:formatDate value="${j.deadline}" pattern="dd/MM/yyyy"/></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/screening?jobPostId=${j.jobPostId}" class="iv-badge iv-badge-blue" style="text-decoration:none;">${j.applyCount} hồ sơ</a>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${j.visible}"><span class="iv-badge iv-badge-green">Đang mở</span></c:when>
                                        <c:otherwise><span class="iv-badge iv-badge-gray">Đã đóng</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div style="display:flex; flex-direction:column; gap:8px; align-items:stretch; min-width:140px;">
                                        <a href="${pageContext.request.contextPath}/job-post?editId=${j.jobPostId}#jd-form" class="iv-btn iv-btn-sm" style="justify-content:center; width:100%; box-sizing:border-box; white-space:nowrap; margin:0;">Sửa</a>
                                        <form method="post" action="${pageContext.request.contextPath}/job-post" style="margin:0;">
                                            <input type="hidden" name="action" value="toggle"/>
                                            <input type="hidden" name="jobPostId" value="${j.jobPostId}"/>
                                            <input type="hidden" name="newVisible" value="${j.visible ? 0 : 1}"/>
                                            <button type="submit" class="iv-btn iv-btn-sm" style="justify-content:center; width:100%; box-sizing:border-box; white-space:nowrap; margin:0;">${j.visible ? 'Đóng ứng tuyển' : 'Mở ứng tuyển'}</button>
                                        </form>
                                        <a href="${pageContext.request.contextPath}/jobs?id=${j.jobPostId}" target="_blank" class="iv-btn iv-btn-sm" style="justify-content:center; width:100%; box-sizing:border-box; white-space:nowrap; margin:0;">Xem public</a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="iv-card" id="jd-form" style="order:${editMode ? 0 : 2};">
        <h2>${editMode ? 'Sửa JD' : 'Tạo JD mới'}</h2>
        <p style="margin:0 0 14px; color:#64748b; font-size:0.9rem;">Tất cả các mục đánh dấu <b style="color:#dc2626;">*</b> đều bắt buộc phải điền.</p>
        <form method="post" action="${pageContext.request.contextPath}/job-post">
            <input type="hidden" name="action" value="${editMode ? 'update' : 'create'}"/>
            <c:if test="${editMode}"><input type="hidden" name="jobPostId" value="${formJob.jobPostId}"/></c:if>

            <div class="iv-form-row">
                <div class="iv-field" style="flex:2;">
                    <label>Tiêu đề *</label>
                    <input type="text" name="title" placeholder="Vd: Tuyển Java Developer" value="<c:out value='${formJob.title}'/>" required/>
                </div>
                <div class="iv-field">
                    <label>Địa điểm *</label>
                    <input type="text" name="location" placeholder="Hà Nội, Việt Nam" value="<c:out value='${formJob.location}'/>" required/>
                </div>
            </div>
            <div class="iv-form-row">
                <div class="iv-field">
                    <label>Phòng ban / Category *</label>
                    <select id="categorySelect" name="category" data-initial="<c:out value='${formJob.category}'/>" required></select>
                </div>
                <div class="iv-field">
                    <label>Vị trí (Position) *</label>
                    <select id="positionSelect" name="position" data-initial="<c:out value='${formJob.position}'/>" required></select>
                </div>
                <div class="iv-field">
                    <label>Loại hình *</label>
                    <select name="typeJob" required>
                        <option value="Full-time" ${formJob.typeJob == 'Full-time' ? 'selected' : ''}>Full-time</option>
                        <option value="Part-time" ${formJob.typeJob == 'Part-time' ? 'selected' : ''}>Part-time</option>
                        <option value="Internship" ${formJob.typeJob == 'Internship' ? 'selected' : ''}>Thực tập</option>
                        <option value="Remote" ${formJob.typeJob == 'Remote' ? 'selected' : ''}>Remote</option>
                    </select>
                </div>
            </div>
            <div class="iv-form-row">
                <div class="iv-field">
                    <label>Lương tối thiểu *</label>
                    <input type="number" name="offerMin" min="0" step="0.01" placeholder="1000" value="${formJob.offerMin}" required/>
                </div>
                <div class="iv-field">
                    <label>Lương tối đa *</label>
                    <input type="number" name="offerMax" min="0" step="0.01" placeholder="2000" value="${formJob.offerMax}" required/>
                </div>
                <div class="iv-field">
                    <label>Kinh nghiệm tối thiểu (năm) *</label>
                    <input type="number" name="numberExp" min="0" step="1" placeholder="1" value="${formJob.numberExp}" required/>
                </div>
                <div class="iv-field">
                    <label>Hạn nộp hồ sơ *</label>
                    <input type="date" name="deadline" ${editMode ? '' : 'min="'.concat(today).concat('"')} value="<fmt:formatDate value='${formJob.deadline}' pattern='yyyy-MM-dd'/>" required/>
                </div>
            </div>
            <div class="iv-field" style="margin-bottom:14px;">
                <label>Mô tả công việc *</label>
                <textarea name="description" placeholder="Mô tả chi tiết công việc, yêu cầu..." style="min-height:150px;" required><c:out value="${formJob.description}"/></textarea>
            </div>
            <div style="display:flex; align-items:center; gap:16px; flex-wrap:wrap;">
                <button type="submit" class="iv-btn">${editMode ? 'Lưu thay đổi' : 'Tạo JD'}</button>
                <c:if test="${editMode}">
                    <a href="${pageContext.request.contextPath}/job-post" class="iv-btn iv-btn-ghost">Hủy sửa</a>
                </c:if>
                <label style="display:flex; align-items:center; gap:8px; margin:0;">
                    <input type="checkbox" name="visible" ${empty formJob || formJob.visible ? 'checked' : ''} style="width:auto;"/> Hiển thị công khai
                </label>
            </div>
        </form>
    </div>
</div>

<script>
    // Salary range: minimum must be lower than maximum
    (function () {
        var min = document.querySelector('[name=offerMin]');
        var max = document.querySelector('[name=offerMax]');
        if (!min || !max) return;
        function check() {
            var a = parseFloat(min.value), b = parseFloat(max.value);
            max.setCustomValidity(!isNaN(a) && !isNaN(b) && a >= b ? 'Lương tối đa phải lớn hơn lương tối thiểu' : '');
        }
        min.addEventListener('input', check);
        max.addEventListener('input', check);
        check();
    })();
</script>


<%-- Vị trí: bảng Employee (cột Position), lọc theo phòng ban đã chọn (Employee.Department) --%>
<%-- Phòng ban: bảng Department (cột DepartmentName) --%>
<div id="deptData" hidden>
    <c:forEach var="d" items="${departmentNames}"><span data-name="<c:out value='${d}'/>"></span></c:forEach>
</div>
<div id="catPosData" hidden>
    <c:forEach var="p" items="${deptPositions}"><span data-cat="<c:out value='${p[0]}'/>" data-pos="<c:out value='${p[1]}'/>"></span></c:forEach>
</div>
<script>
    (function () {
        var catSel = document.getElementById('categorySelect');
        var posSel = document.getElementById('positionSelect');
        if (!catSel || !posSel) return;

        var data = Array.prototype.map.call(document.querySelectorAll('#catPosData span'), function (el) {
            return {cat: el.getAttribute('data-cat'), pos: el.getAttribute('data-pos')};
        });

        function uniqSorted(arr) {
            var seen = {};
            return arr.filter(function (v) {
                if (!v || seen[v]) return false;
                seen[v] = true;
                return true;
            }).sort(function (a, b) { return a.localeCompare(b); });
        }

        // Positions of the chosen department (all positions while no department is chosen).
        function positionsFor(cat) {
            return uniqSorted(data.filter(function (d) { return !cat || d.cat === cat; })
                                  .map(function (d) { return d.pos; }));
        }

        // Fill a <select> and pick `selected`. A value that is not in the list (e.g. an old JD)
        // is added so that editing a JD never loses its current value.
        function fill(sel, values, placeholder, selected) {
            values = values.slice();
            if (selected && values.indexOf(selected) < 0) values.push(selected);
            sel.innerHTML = '';
            var ph = document.createElement('option');
            ph.value = '';
            ph.textContent = placeholder;
            sel.appendChild(ph);
            values.forEach(function (v) {
                var o = document.createElement('option');
                o.value = v;
                o.textContent = v;
                sel.appendChild(o);
            });
            sel.value = selected || '';
        }

        // Departments come from the Department table (DepartmentName).
        var categories = uniqSorted(Array.prototype.map.call(document.querySelectorAll('#deptData span'), function (el) {
            return el.getAttribute('data-name');
        }));
        var initCat = (catSel.getAttribute('data-initial') || '').trim();
        var initPos = (posSel.getAttribute('data-initial') || '').trim();

        fill(catSel, categories, '-- Chọn phòng ban --', initCat);
        fill(posSel, positionsFor(initCat), '-- Chọn vị trí --', initPos);

        // Narrow the position list to the chosen department; keep the current position if it still fits.
        catSel.addEventListener('change', function () {
            var values = positionsFor(catSel.value);
            var prev = posSel.value;
            fill(posSel, values, '-- Chọn vị trí --', values.indexOf(prev) >= 0 ? prev : '');
        });
    })();
</script>

<jsp:include page="staff_footer.jsp"/>
