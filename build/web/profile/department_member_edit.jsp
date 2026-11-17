<%--<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Cập nhật nhân sự phòng ban" scope="request"/>
<c:set var="currentNav" value="deptOrg" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<style>
    .edit-box { max-width: 800px; margin: 0 auto; background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 30px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.06); }
    .save-btn-footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #e2e8f0; display: flex; justify-content: flex-end; gap: 12px; }
    .btn-save-main { background: #2563eb; color: #fff; border: none; padding: 12px 36px; border-radius: 8px; font-weight: 700; font-size: 1rem; cursor: pointer; text-transform: uppercase; letter-spacing: 0.5px; }
    .btn-save-main:hover { background: #1d4ed8; }
</style>

<div class="iv-shell-content">
    <a href="${pageContext.request.contextPath}/department-detail?name=${deptName}" style="display:inline-flex; align-items:center; gap:8px; font-weight:600; color:#2563eb; text-decoration:none; margin-bottom:20px;">
        ‹ Quay lại phòng: ${deptName}
    </a>

    <div class="edit-box">
        <c:choose>
             FORM A: THAY ĐỔI / CHỈ ĐỊNH TRƯỞNG NHÓM 
            <c:when test="${isLeaderChange}">
                <div class="iv-section-head" style="margin-bottom: 24px;">
                    <div>
                        <h2 style="margin: 0; color: #1e3a8a;">Chỉ định Trưởng nhóm cho phòng: ${deptName}</h2>
                        <span class="iv-small iv-muted">Chọn một nhân sự trong danh sách để bổ nhiệm làm Trưởng nhóm mới.</span>
                    </div>
                </div>

                <form method="post" action="${pageContext.request.contextPath}/department-member-edit">
                    <input type="hidden" name="mode" value="changeLeader"/>
                    <input type="hidden" name="deptId" value="${currentDept.departmentId}"/>
                    <input type="hidden" name="deptName" value="${deptName}"/>

                    <div class="iv-field" style="margin-bottom: 20px;">
                        <label>Chọn nhân sự làm Trưởng nhóm *</label>
                        <select name="newLeaderId" required style="width: 100%; padding: 10px; border-radius: 8px; border: 1.5px solid #cbd5e1;">
                            <option value="">-- Chọn nhân sự từ danh sách --</option>
                            <c:forEach var="c" items="${candidateLeaders}">
                                <option value="${c.employeeId}" ${currentDept.managerId == c.employeeId ? 'selected' : ''}>
                                    ${c.fullName} (${c.employeeCode}) - Hiện tại: ${c.position}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="save-btn-footer">
                        <a href="${pageContext.request.contextPath}/department-detail?name=${deptName}" class="iv-btn iv-btn-gray">Hủy bỏ</a>
                        <button type="submit" class="btn-save-main">LƯU</button>
                    </div>
                </form>
            </c:when>

             FORM B: CHỈNH SỬA THÔNG TIN & ĐIỀU CHUYỂN NHÂN SỰ 
            <c:otherwise>
                <div class="iv-section-head" style="margin-bottom: 24px;">
                    <div>
                        <h2 style="margin: 0; color: #1e3a8a;">Cập nhật thông tin nhân sự: ${targetEmp.fullName}</h2>
                        <span class="iv-small iv-muted">Mã nhân sự: <b>${targetEmp.employeeCode}</b> · Email: ${targetEmp.email}</span>
                    </div>
                </div>

                <form method="post" action="${pageContext.request.contextPath}/department-member-edit">
                    <input type="hidden" name="mode" value="updateMember"/>
                    <input type="hidden" name="empId" value="${targetEmp.employeeId}"/>
                    <input type="hidden" name="deptName" value="${deptName}"/>

                    <div class="iv-form-row">
                        <div class="iv-field">
                            <label>Họ và tên</label>
                            <input type="text" value="${targetEmp.fullName}" disabled style="background: #f8fafc;"/>
                        </div>
                        <div class="iv-field">
                            <label>Phòng ban trực thuộc *</label>
                            <select name="targetDept" required>
                                <c:forEach var="d" items="${departments}">
                                    <option value="${d.departmentName}" ${targetEmp.department eq d.departmentName ? 'selected' : ''}>
                                        ${d.departmentName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="iv-form-row">
                        <div class="iv-field">
                            <label>Chức danh / Vị trí *</label>
                            <input type="text" name="position" value="${targetEmp.position}" required placeholder="Ví dụ: Team Leader, Senior Dev..."/>
                        </div>
                        <div class="iv-field">
                            <label>Lý do / Ghi chú thay đổi</label>
                            <input type="text" name="note" placeholder="Quyết định luân chuyển, bổ nhiệm..."/>
                        </div>
                    </div>

                    <div class="save-btn-footer">
                        <a href="${pageContext.request.contextPath}/department-detail?name=${deptName}" class="iv-btn iv-btn-gray">Hủy bỏ</a>
                        <button type="submit" class="btn-save-main">LƯU</button>
                    </div>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/interview/staff_footer.jsp"/>--%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Cập nhật nhân sự phòng ban" scope="request"/>
<c:set var="currentNav" value="deptOrg" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<style>
    .edit-box { max-width: 800px; margin: 0 auto; background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 30px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.06); }
    .save-btn-footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #e2e8f0; display: flex; justify-content: flex-end; gap: 12px; }
    .btn-save-main { background: #2563eb; color: #fff; border: none; padding: 12px 36px; border-radius: 8px; font-weight: 700; font-size: 1rem; cursor: pointer; text-transform: uppercase; letter-spacing: 0.5px; }
    .btn-save-main:hover { background: #1d4ed8; }
</style>

<div class="iv-shell-content">
    <a href="${pageContext.request.contextPath}/department-detail?name=${deptName}" style="display:inline-flex; align-items:center; gap:8px; font-weight:600; color:#2563eb; text-decoration:none; margin-bottom:20px;">
        ‹ Quay lại phòng: ${deptName}
    </a>

    <div class="edit-box">
        <c:choose>
            <%-- FORM A: THAY ĐỔI / CHỈ ĐỊNH TRƯỞNG NHÓM --%>
            <c:when test="${isLeaderChange}">
                <div class="iv-section-head" style="margin-bottom: 24px;">
                    <div>
                        <h2 style="margin: 0; color: #1e3a8a;">Chỉ định Trưởng nhóm cho phòng: ${deptName}</h2>
                        <span class="iv-small iv-muted">Chọn một nhân sự trong danh sách để bổ nhiệm làm Trưởng nhóm mới.</span>
                    </div>
                </div>

                <form method="post" action="${pageContext.request.contextPath}/department-member-edit">
                    <input type="hidden" name="mode" value="changeLeader"/>
                    <input type="hidden" name="deptId" value="${currentDept.departmentId}"/>
                    <input type="hidden" name="deptName" value="${deptName}"/>

                    <div class="iv-field" style="margin-bottom: 20px;">
                        <label>Chọn nhân sự làm Trưởng nhóm *</label>
                        <select name="newLeaderId" required style="width: 100%; padding: 10px; border-radius: 8px; border: 1.5px solid #cbd5e1;">
                            <option value="">-- Chọn nhân sự từ danh sách --</option>
                            <c:forEach var="c" items="${candidateLeaders}">
                                <option value="${c.employeeId}" ${currentDept.managerId == c.employeeId ? 'selected' : ''}>
                                    ${c.fullName} (${c.employeeCode}) - Hiện tại: ${c.position}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="save-btn-footer">
                        <a href="${pageContext.request.contextPath}/department-detail?name=${deptName}" class="iv-btn iv-btn-gray">Hủy bỏ</a>
                        <button type="submit" class="btn-save-main">LƯU</button>
                    </div>
                </form>
            </c:when>

            <%-- FORM B: CHỈNH SỬA THÔNG TIN & ĐIỀU CHUYỂN NHÂN SỰ --%>
            <c:otherwise>
                <div class="iv-section-head" style="margin-bottom: 24px;">
                    <div>
                        <h2 style="margin: 0; color: #1e3a8a;">Cập nhật thông tin nhân sự: ${targetEmp.fullName}</h2>
                        <span class="iv-small iv-muted">Mã nhân sự: <b>${targetEmp.employeeCode}</b> · Vai trò: ${targetEmp.roleName}</span>
                    </div>
                </div>

                <form method="post" action="${pageContext.request.contextPath}/department-member-edit">
                    <input type="hidden" name="mode" value="updateMember"/>
                    <input type="hidden" name="empId" value="${targetEmp.employeeId}"/>
                    <input type="hidden" name="deptName" value="${deptName}"/>

                    <%-- DÒNG 1: Họ tên & Chức danh --%>
                    <div class="iv-form-row">
                        <div class="iv-field">
                            <label>Họ và tên</label>
                            <input type="text" value="${targetEmp.fullName}" disabled style="background: #f8fafc;"/>
                        </div>
                        <div class="iv-field">
                            <label>Chức danh / Vị trí *</label>
                            <input type="text" name="position" value="${targetEmp.position}" required placeholder="Ví dụ: Nhân viên, Team Leader, Senior Dev..."/>
                        </div>
                    </div>

                    <%-- THÊM MỚI: DÒNG 2: Chỉnh sửa Email & Số điện thoại --%>
                    <div class="iv-form-row">
                        <div class="iv-field">
                            <label>Email liên hệ *</label>
                            <input type="email" name="email" value="${targetEmp.email}" required placeholder="ten@gmail.com"/>
                        </div>
                        <div class="iv-field">
                            <label>Số điện thoại *</label>
                            <input type="text" name="phone" value="${targetEmp.phone}" required placeholder="09xxxxxxxx"/>
                        </div>
                    </div>

                    <%-- DÒNG 3: Phòng ban trực thuộc & Ghi chú điều chuyển --%>
                    <div class="iv-form-row">
                        <div class="iv-field">
                            <label>Phòng ban trực thuộc *</label>
                            <select name="targetDept" required>
                                <c:forEach var="d" items="${departments}">
                                    <option value="${d.departmentName}" ${targetEmp.department eq d.departmentName ? 'selected' : ''}>
                                        ${d.departmentName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="iv-field">
                            <label>Lý do / Ghi chú thay đổi</label>
                            <input type="text" name="note" placeholder="Quyết định luân chuyển, bổ nhiệm..."/>
                        </div>
                    </div>

                    <div class="save-btn-footer">
                        <a href="${pageContext.request.contextPath}/department-detail?name=${deptName}" class="iv-btn iv-btn-gray">Hủy bỏ</a>
                        <button type="submit" class="btn-save-main">LƯU</button>
                    </div>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/interview/staff_footer.jsp"/>