<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Quản lý nhân viên phòng" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<style>
    .room-shell { max-width: 1200px; margin: 0 auto; padding: 20px; }
    .leader-banner { background: #eff6ff; border: 1.5px solid #bfdbfe; border-radius: 12px; padding: 20px; margin-bottom: 24px; }
    .room-tabs { display: flex; gap: 10px; margin-bottom: 20px; border-bottom: 2px solid #e2e8f0; padding-bottom: 10px; }
    .room-tab-item { padding: 10px 20px; text-decoration: none; font-weight: 600; color: #64748b; border-radius: 8px; }
    .room-tab-item.active { background: #2563eb; color: #fff; }
    .btn-edit-member { display: inline-flex; align-items: center; gap: 4px; border: 1px solid #cbd5e1; padding: 6px 14px; border-radius: 6px; text-decoration: none; font-weight: 600; color: #334155; }
    .btn-edit-member:hover { background: #f1f5f9; color: #2563eb; }

    /* Style bổ sung cho Dropdown chuyển 16 phòng của Admin trên giao diện */
    .dept-switcher-box {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        background: #f8fafc;
        border: 1.5px solid #cbd5e1;
        padding: 5px 12px;
        border-radius: 8px;
    }
    .dept-switcher-select {
        border: none;
        background: transparent;
        font-weight: 700;
        color: #2563eb;
        font-size: 0.9rem;
        cursor: pointer;
        outline: none;
    }
</style>

<div class="room-shell">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; flex-wrap: wrap; gap: 12px;">
        <div>
            <h1 style="margin: 0; font-size: 1.6rem; color: #0f172a;">🏢 Phòng: ${leaderDept.departmentName}</h1>
            <p style="margin: 4px 0 0 0; color: #64748b;">${leaderDept.description}</p>
        </div>
        
        <div style="display: flex; align-items: center; gap: 12px;">
            <%-- THÊM MỚI: BỘ CHỌN CHUYỂN NHANH 16 PHÒNG BAN DÀNH CHO ADMIN / MANAGER --%>
            <c:if test="${isManagerRole}">
                <div class="dept-switcher-box">
                    <span style="font-size: 0.85rem; font-weight: bold; color: #475569;">🔄 Đổi phòng xem:</span>
                    <select class="dept-switcher-select" onchange="window.location.href='${pageContext.request.contextPath}/leader-room?tab=members&deptId=' + this.value">
                        <c:forEach var="d" items="${allDepartments}">
                            <option value="${d.departmentId}" ${leaderDept.departmentId == d.departmentId ? 'selected' : ''}>
                                ${d.departmentName} (${d.employeeCount} NV)
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </c:if>

            <span style="background: #e0e7ff; color: #3730a3; padding: 8px 16px; border-radius: 20px; font-weight: 700; font-size: 0.95rem;">
                Tổng quy mô: ${members.size()} nhân sự
            </span>
        </div>
    </div>

    <!-- 3 TASKBAR NHỎ ĐIỀU HƯỚNG (BỔ SUNG deptId ĐỂ GIỮ ĐÚNG PHÒNG ĐANG CHỌN) -->
    <div class="room-tabs">
        <a href="${pageContext.request.contextPath}/leader-room?tab=members&deptId=${leaderDept.departmentId}" class="room-tab-item active">👥 Quản lý nhân viên</a>
        <a href="${pageContext.request.contextPath}/leader-room?tab=topics&deptId=${leaderDept.departmentId}" class="room-tab-item">📋 Chủ đề + Phân công</a>
        <a href="${pageContext.request.contextPath}/leader-room?tab=progress&deptId=${leaderDept.departmentId}" class="room-tab-item">📊 Tổng kết tiến độ</a>
    </div>

    <!-- KHUNG THÔNG TIN TRƯỞNG NHÓM (Y HỆT ẢNH 1) -->
    <div class="leader-banner">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
            <div style="display: flex; align-items: center; gap: 8px;">
                <span style="background: #2563eb; color: #fff; font-size: 0.8rem; font-weight: bold; padding: 4px 10px; border-radius: 12px;">
                    ⭐ TRƯỞNG NHÓM / LEADER
                </span>
                <span style="color: #64748b; font-size: 0.88rem;">Phụ trách điều hành & quản lý chất trị phòng ban</span>
            </div>
        </div>
        <table class="iv-table" style="background: #fff; border-radius: 8px; overflow: hidden;">
            <thead>
                <tr>
                    <th>Mã NV</th>
                    <th>Họ và tên</th>
                    <th>Chức vụ đảm nhiệm</th>
                    <th>Email liên hệ</th>
                    <th>Số điện thoại</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><b>EMP${leaderDept.managerId}</b></td>
                    <td><b style="color: #1e3a8a;">${leaderDept.managerName}</b></td>
                    <td><span class="iv-badge iv-badge-green" style="background:#dcfce7; color:#166534; font-weight:bold; padding:4px 8px; border-radius:12px;">Trưởng nhóm / Leader</span></td>
                    <td><c:out value="${sessionScope.userEmail}"/></td>
                    <td>0916666666</td>
                </tr>
            </tbody>
        </table>
    </div>

    <!-- DANH SÁCH THÀNH VIÊN NHÂN SỰ (Y HỆT ẢNH 1) -->
    <div class="iv-card" style="background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 20px;">
        <h3 style="margin: 0 0 4px 0;">Danh sách thành viên nhân sự (${members.size()})</h3>
        <p style="margin: 0 0 16px 0; color: #64748b; font-size: 0.9rem;">Các nhân viên thuộc biên chế hoạt động trong phòng ban.</p>

        <table class="iv-table" style="width: 100%;">
            <thead>
                <tr>
                    <th>Mã NV</th>
                    <th>Họ và tên</th>
                    <th>Vị trí / Chức danh</th>
                    <th>Email</th>
                    <th>Số điện thoại</th>
                    <th>Ngày vào làm</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="m" items="${members}">
                    <tr>
                        <td><b>${m.employeeCode}</b></td>
                        <td><b>${m.fullName}</b></td>
                        <td><span style="background: #e0e7ff; color: #3730a3; padding: 2px 8px; border-radius: 10px; font-weight: 700; font-size: 0.8rem;">NV</span></td>
                        <td>${m.email}</td>
                        <td>${m.phone}</td>
                        <td>—</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/department-member-edit?deptName=${leaderDept.departmentName}&empId=${m.employeeId}&from=leader" class="btn-edit-member">
    ✏️ Thay đổi
</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/interview/staff_footer.jsp"/>