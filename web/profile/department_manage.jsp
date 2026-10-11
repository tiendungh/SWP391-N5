<%--<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Tổ chức phòng ban" scope="request"/>
<c:set var="currentNav" value="deptOrg" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<style>
    .dept-container { display: flex; flex-direction: column; gap: 20px; }
    .dept-stats-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 15px; }
    .dept-stat-card {
        background: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 18px 20px;
        box-shadow: 0 1px 3px rgba(0,0,0,0.04); display: flex; align-items: center; justify-content: space-between;
    }
    .dept-stat-card .val { font-size: 1.7rem; font-weight: 700; color: #1e293b; }
    .dept-stat-card .lbl { font-size: 0.85rem; color: #64748b; margin-top: 4px; }
    .dept-filter-bar {
        background: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 15px 20px;
        display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 15px;
    }
    .dept-select-wrap { position: relative; width: 380px; max-width: 100%; }
    .dept-select-custom {
        width: 100%; padding: 10px 40px 10px 14px; font-size: 0.95rem; border: 1.5px solid #cbd5e1;
        border-radius: 8px; appearance: none; -webkit-appearance: none; background: #fff; cursor: pointer;
        font-weight: 500; color: #1e293b; outline: none; transition: border-color .2s;
    }
    .dept-select-custom:focus { border-color: #3b82f6; box-shadow: 0 0 0 3px rgba(59,130,246,0.15); }
    .dept-select-icon {
        position: absolute; right: 14px; top: 50%; transform: translateY(-50%);
        pointer-events: none; font-size: 0.85rem; font-weight: bold; color: #475569;
    }
    .dept-assign-box {
        background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 10px; padding: 20px; margin-bottom: 20px;
    }
</style>

<div class="iv-topbar">
    <div>
        <h1>Tổ chức & Phân bổ phòng ban</h1>
        <p>Quản lý sơ đồ cơ cấu tổ chức doanh nghiệp và điều chuyển vị trí nhân sự.</p>
    </div>
    <div class="iv-whoami">Xin chào, <b><c:out value="${sessionScope.staffName}"/></b></div>
</div>

<c:if test="${not empty error}"><div class="iv-alert iv-alert-error"><c:out value="${error}"/></div></c:if>
<c:if test="${param.assigned == '1'}"><div class="iv-alert iv-alert-success">Cập nhật và gán nhân sự vào phòng ban thành công!</div></c:if>

<div class="dept-container">

    <!-- KHỐI 1: BỘ CHỈ SỐ THỐNG KÊ (DASHBOARD WIDGETS) -->
    <div class="dept-stats-row">
        <div class="dept-stat-card">
            <div>
                <div class="val">${departments.size()}</div>
                <div class="lbl">Tổng số phòng ban</div>
            </div>
            <div style="font-size: 2rem;">🏢</div>
        </div>
        <div class="dept-stat-card">
            <div>
                <div class="val">${totalEmployees}</div>
                <div class="lbl">Tổng nhân sự chính thức</div>
            </div>
            <div style="font-size: 2rem;">👥</div>
        </div>
        <div class="dept-stat-card">
            <div>
                <div class="val">${employees.size()}</div>
                <div class="lbl">Nhân sự trong bộ lọc</div>
            </div>
            <div style="font-size: 2rem;">🎯</div>
        </div>
    </div>

    <!-- KHỐI 2: THANH LỌC VỚI DROPDOWN ICON "V" & CÔNG CỤ NHANH -->
    <div class="dept-filter-bar">
        <div style="display: flex; align-items: center; gap: 15px; flex-wrap: wrap;">
            <label style="font-weight: 600; color: #334155; margin: 0;">Chọn phòng ban:</label>
            <div class="dept-select-wrap">
                <select class="dept-select-custom" onchange="location.href='${pageContext.request.contextPath}/department-organization' + (this.value ? '?dept=' + encodeURIComponent(this.value) : '')">
                    <option value="">-- Tất cả phòng ban (${departments.size()}) --</option>
                    <c:forEach var="dept" items="${departments}">
                        <option value="${dept.departmentName}" ${selectedDept eq dept.departmentName ? 'selected' : ''}>
                            ${dept.departmentName} (${dept.employeeCount} nhân sự)
                        </option>
                    </c:forEach>
                </select>
                <span class="dept-select-icon">⌄</span>
            </div>
        </div>

        <button type="button" class="iv-btn" onclick="toggleAssignForm()" style="display: flex; align-items: center; gap: 6px;">
            <span>➕</span> Phân công / Điều chuyển nhân sự
        </button>
    </div>

    <!-- KHỐI 3: FORM ĐIỀU CHUYỂN NHÂN SỰ VÀO PHÒNG BAN -->
    <div id="assignFormBlock" class="dept-assign-box" style="display: none;">
        <div class="iv-section-head" style="margin-bottom: 15px;">
            <div>
                <h3 style="margin: 0; font-size: 1.15rem;">Bổ nhiệm & Điều chuyển công tác</h3>
                <span class="iv-small iv-muted">Gán nhân viên vào phòng ban trực thuộc và lưu lại lịch sử luân chuyển.</span>
            </div>
            <button type="button" class="iv-btn iv-btn-sm iv-btn-gray" onclick="toggleAssignForm()">Đóng</button>
        </div>

        <form method="post" action="${pageContext.request.contextPath}/department-organization">
            <input type="hidden" name="action" value="assign"/>
            <div class="iv-form-row">
                <div class="iv-field">
                    <label>Nhân sự *</label>
                    <select name="employeeId" required>
                        <option value="">-- Chọn nhân sự cần phân công --</option>
                        <c:forEach var="emp" items="${allEmployeeList}">
                            <option value="${emp.employeeId}"><c:out value="${emp.fullName}"/> (<c:out value="${emp.email}"/>)</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="iv-field">
                    <label>Phòng ban chỉ định *</label>
                    <select name="department" required>
                        <option value="">-- Chọn phòng ban --</option>
                        <c:forEach var="dept" items="${departments}">
                            <option value="${dept.departmentName}" ${selectedDept eq dept.departmentName ? 'selected' : ''}>
                                ${dept.departmentName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="iv-field">
                    <label>Chức danh / Vị trí *</label>
                    <input type="text" name="position" placeholder="Ví dụ: Trưởng nhóm, Developer, Chuyên viên..." required/>
                </div>
            </div>
            <div class="iv-form-row">
                <div class="iv-field" style="flex: 2;">
                    <label>Ghi chú điều chuyển</label>
                    <input type="text" name="note" placeholder="Lý do bổ nhiệm, quyết định số..."/>
                </div>
                <div class="iv-field" style="flex: 1; align-self: flex-end;">
                    <button class="iv-btn" style="width: 100%;" type="submit">Xác nhận phân công</button>
                </div>
            </div>
        </form>
    </div>

    <!-- KHỐI 4: DANH SÁCH NHÂN SỰ THEO PHÒNG BAN -->
    <div class="iv-card">
        <div class="iv-section-head">
            <div>
                <h2>
                    <c:choose>
                        <c:when test="${not empty selectedDept}">Nhân sự phòng: <span style="color: #2563eb;">${selectedDept}</span></c:when>
                        <c:otherwise>Toàn bộ danh sách nhân sự (${employees.size()})</c:otherwise>
                    </c:choose>
                </h2>
                <span class="iv-small iv-muted">Danh sách nhân sự hiện đang công tác theo cơ cấu tổ chức.</span>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty employees}">
                <div class="iv-empty">Phòng ban này hiện chưa có nhân sự nào được phân công. Hãy bấm nút <b>"Phân công / Điều chuyển nhân sự"</b> để bắt đầu.</div>
            </c:when>
            <c:otherwise>
                <div class="iv-table-wrap">
                    <table class="iv-table">
                        <thead>
                            <tr>
                                <th>Mã NV</th>
                                <th>Họ và tên</th>
                                <th>Phòng ban</th>
                                <th>Chức danh / Vị trí</th>
                                <th>Liên hệ</th>
                                <th>Vai trò</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="e" items="${employees}">
                                <tr>
                                    <td><b><c:out value="${not empty e.employeeCode ? e.employeeCode : '—'}"/></b></td>
                                    <td><b><c:out value="${e.fullName}"/></b></td>
                                    <td>
                                        <span class="iv-badge iv-badge-blue">
                                            <c:out value="${not empty e.department ? e.department : 'Chưa phân công'}"/>
                                        </span>
                                    </td>
                                    <td><b><c:out value="${not empty e.position ? e.position : '—'}"/></b></td>
                                    <td>
                                        <c:out value="${e.email}"/><br>
                                        <span class="iv-small iv-muted"><c:out value="${e.phone}"/></span>
                                    </td>
                                    <td><span class="iv-badge iv-badge-gray"><c:out value="${e.roleName}"/></span></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

</div>

<script>
    function toggleAssignForm() {
        var form = document.getElementById("assignFormBlock");
        if (form.style.display === "none" || form.style.display === "") {
            form.style.display = "block";
            form.scrollIntoView({ behavior: 'smooth' });
        } else {
            form.style.display = "none";
        }
    }
</script>

<jsp:include page="/interview/staff_footer.jsp"/>--%>


<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Tổ chức phòng ban" scope="request"/>
<c:set var="currentNav" value="deptOrg" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<style>
    .dept-container { display: flex; flex-direction: column; gap: 20px; }
    .dept-stats-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 15px; }
    .dept-stat-card {
        background: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 18px 20px;
        box-shadow: 0 1px 3px rgba(0,0,0,0.04); display: flex; align-items: center; justify-content: space-between;
    }
    .dept-stat-card .val { font-size: 1.7rem; font-weight: 700; color: #1e293b; }
    .dept-stat-card .lbl { font-size: 0.85rem; color: #64748b; margin-top: 4px; }
    .dept-filter-bar {
        background: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 15px 20px;
        display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 15px;
    }
    .dept-select-wrap { position: relative; width: 380px; max-width: 100%; }
    .dept-select-custom {
        width: 100%; padding: 10px 40px 10px 14px; font-size: 0.95rem; border: 1.5px solid #cbd5e1;
        border-radius: 8px; appearance: none; -webkit-appearance: none; background: #fff; cursor: pointer;
        font-weight: 500; color: #1e293b; outline: none; transition: border-color .2s;
    }
    .dept-select-custom:focus { border-color: #3b82f6; box-shadow: 0 0 0 3px rgba(59,130,246,0.15); }
    .dept-select-icon {
        position: absolute; right: 14px; top: 50%; transform: translateY(-50%);
        pointer-events: none; font-size: 0.85rem; font-weight: bold; color: #475569;
    }
    .dept-assign-box {
        background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 10px; padding: 20px; margin-bottom: 20px;
    }
    .dept-link-badge {
        text-decoration: none; cursor: pointer; display: inline-flex; align-items: center; gap: 4px;
        transition: transform 0.15s, opacity 0.15s;
    }
    .dept-link-badge:hover {
        transform: translateY(-1px);
        opacity: 0.9;
        text-decoration: underline;
    }
</style>

<div class="iv-topbar">
    <div>
        <h1>Tổ chức & Phân bổ phòng ban</h1>
        <p>Quản lý sơ đồ cơ cấu tổ chức doanh nghiệp và điều chuyển vị trí nhân sự.</p>
    </div>
    <div class="iv-whoami">Xin chào, <b><c:out value="${sessionScope.staffName}"/></b></div>
</div>

<c:if test="${not empty error}"><div class="iv-alert iv-alert-error"><c:out value="${error}"/></div></c:if>
<c:if test="${param.assigned == '1' || param.status == 'assigned'}"><div class="iv-alert iv-alert-success">Cập nhật và gán nhân sự vào phòng ban thành công!</div></c:if>

<div class="dept-container">

    <!-- KHỐI 1: BỘ CHỈ SỐ THỐNG KÊ (DASHBOARD WIDGETS) -->
    <div class="dept-stats-row">
        <div class="dept-stat-card">
            <div>
                <div class="val">${departments.size()}</div>
                <div class="lbl">Tổng số phòng ban</div>
            </div>
            <div style="font-size: 2rem;">🏢</div>
        </div>
        <div class="dept-stat-card">
            <div>
                <div class="val">${totalEmployees}</div>
                <div class="lbl">Tổng nhân sự chính thức</div>
            </div>
            <div style="font-size: 2rem;">👥</div>
        </div>
        <div class="dept-stat-card">
            <div>
                <div class="val">${employees.size()}</div>
                <div class="lbl">Nhân sự trong bộ lọc</div>
            </div>
            <div style="font-size: 2rem;">🎯</div>
        </div>
    </div>

    <!-- KHỐI 2: THANH LỌC VỚI DROPDOWN ICON "V" & CÔNG CỤ NHANH -->
    <div class="dept-filter-bar">
        <div style="display: flex; align-items: center; gap: 15px; flex-wrap: wrap;">
            <label style="font-weight: 600; color: #334155; margin: 0;">Chọn phòng ban:</label>
            <div class="dept-select-wrap">
                <%-- Khi chọn 1 phòng ban cụ thể -> tự động mở trang web riêng của phòng ban đó --%>
                <select class="dept-select-custom" onchange="onSelectDepartment(this.value)">
                    <option value="">-- Tất cả phòng ban (${departments.size()}) --</option>
                    <c:forEach var="dept" items="${departments}">
                        <option value="${dept.departmentName}" ${selectedDept eq dept.departmentName ? 'selected' : ''}>
                            ${dept.departmentName} (${dept.employeeCount} nhân sự)
                        </option>
                    </c:forEach>
                </select>
                <span class="dept-select-icon">⌄</span>
            </div>
        </div>

        <button type="button" class="iv-btn" onclick="toggleAssignForm()" style="display: flex; align-items: center; gap: 6px;">
            <span>➕</span> Phân công / Điều chuyển nhân sự
        </button>
    </div>

    <!-- KHỐI 3: FORM ĐIỀU CHUYỂN NHÂN SỰ VÀO PHÒNG BAN -->
    <div id="assignFormBlock" class="dept-assign-box" style="display: none;">
        <div class="iv-section-head" style="margin-bottom: 15px;">
            <div>
                <h3 style="margin: 0; font-size: 1.15rem;">Bổ nhiệm & Điều chuyển công tác</h3>
                <span class="iv-small iv-muted">Gán nhân viên vào phòng ban trực thuộc và lưu lại lịch sử luân chuyển.</span>
            </div>
            <button type="button" class="iv-btn iv-btn-sm iv-btn-gray" onclick="toggleAssignForm()">Đóng</button>
        </div>

        <form method="post" action="${pageContext.request.contextPath}/department-manage">
            <input type="hidden" name="action" value="assignMember"/>
            <div class="iv-form-row">
                <div class="iv-field">
                    <label>Nhân sự *</label>
                    <select name="employeeId" required>
                        <option value="">-- Chọn nhân sự cần phân công --</option>
                        <c:forEach var="emp" items="${not empty staffList ? staffList : allEmployeeList}">
                            <option value="${not empty emp.staffId ? emp.staffId : emp.employeeId}">
                                <c:out value="${emp.fullName}"/> (<c:out value="${emp.email}"/>)
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="iv-field">
                    <label>Phòng ban chỉ định *</label>
                    <select name="departmentName" required>
                        <option value="">-- Chọn phòng ban --</option>
                        <c:forEach var="dept" items="${departments}">
                            <option value="${dept.departmentName}" ${selectedDept eq dept.departmentName ? 'selected' : ''}>
                                ${dept.departmentName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="iv-field">
                    <label>Chức danh / Vị trí *</label>
                    <input type="text" name="position" placeholder="Ví dụ: Trưởng nhóm, Developer, Chuyên viên..." required/>
                </div>
            </div>
            <div class="iv-form-row">
                <div class="iv-field" style="flex: 2;">
                    <label>Ghi chú điều chuyển</label>
                    <input type="text" name="note" placeholder="Lý do bổ nhiệm, quyết định số..."/>
                </div>
                <div class="iv-field" style="flex: 1; align-self: flex-end;">
                    <button class="iv-btn" style="width: 100%;" type="submit">Xác nhận phân công</button>
                </div>
            </div>
        </form>
    </div>

    <!-- KHỐI 4: DANH SÁCH NHÂN SỰ THEO PHÒNG BAN -->
    <div class="iv-card">
        <div class="iv-section-head">
            <div>
                <h2>
                    <c:choose>
                        <c:when test="${not empty selectedDept}">Nhân sự phòng: <span style="color: #2563eb;">${selectedDept}</span></c:when>
                        <c:otherwise>Toàn bộ danh sách nhân sự (${employees.size()})</c:otherwise>
                    </c:choose>
                </h2>
                <span class="iv-small iv-muted">Danh sách nhân sự hiện đang công tác theo cơ cấu tổ chức (nhấp vào tên phòng ban để xem chi tiết phòng).</span>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty employees}">
                <div class="iv-empty">Phòng ban này hiện chưa có nhân sự nào được phân công. Hãy bấm nút <b>"Phân công / Điều chuyển nhân sự"</b> để bắt đầu.</div>
            </c:when>
            <c:otherwise>
                <div class="iv-table-wrap">
                    <table class="iv-table">
                        <thead>
                            <tr>
                                <th>Mã NV</th>
                                <th>Họ và tên</th>
                                <th>Phòng ban</th>
                                <th>Chức danh / Vị trí</th>
                                <th>Liên hệ</th>
                                <th>Vai trò</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="e" items="${employees}">
                                <tr>
                                    <td><b><c:out value="${not empty e.employeeCode ? e.employeeCode : '—'}"/></b></td>
                                    <td><b><c:out value="${e.fullName}"/></b></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty e.department}">
                                                <a class="dept-link-badge" 
                                                   href="${pageContext.request.contextPath}/department-detail?name=${e.department}" 
                                                   title="Bấm để xem trang riêng của phòng ${e.department}">
                                                    <span class="iv-badge iv-badge-blue">
                                                        🏢 <c:out value="${e.department}"/>
                                                    </span>
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="iv-badge iv-badge-gray">Chưa phân công</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><b><c:out value="${not empty e.position ? e.position : '—'}"/></b></td>
                                    <td>
                                        <c:out value="${e.email}"/><br>
                                        <span class="iv-small iv-muted"><c:out value="${e.phone}"/></span>
                                    </td>
                                    <td><span class="iv-badge iv-badge-gray"><c:out value="${e.roleName}"/></span></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

</div>

<script>
    function onSelectDepartment(deptName) {
        if (!deptName || deptName.trim() === '') {
            location.href = '${pageContext.request.contextPath}/department-manage';
        } else {
            // Chuyển ngay sang trang web riêng của phòng ban được chọn
            location.href = '${pageContext.request.contextPath}/department-detail?name=' + encodeURIComponent(deptName.trim());
        }
    }

    function toggleAssignForm() {
        var form = document.getElementById("assignFormBlock");
        if (form.style.display === "none" || form.style.display === "") {
            form.style.display = "block";
            form.scrollIntoView({ behavior: 'smooth' });
        } else {
            form.style.display = "none";
        }
    }
</script>

<jsp:include page="/interview/staff_footer.jsp"/>