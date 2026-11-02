<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Chủ đề & Phân công công việc" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<style>
    .room-shell { max-width: 1200px; margin: 0 auto; padding: 20px; }
    .room-tabs { display: flex; gap: 10px; margin-bottom: 20px; border-bottom: 2px solid #e2e8f0; padding-bottom: 10px; }
    .room-tab-item { padding: 10px 20px; text-decoration: none; font-weight: 600; color: #64748b; border-radius: 8px; }
    .room-tab-item.active { background: #2563eb; color: #fff; }
    
    .topic-card { background: #ffffff; border: 1.5px dashed #93c5fd; border-radius: 12px; padding: 24px; margin-bottom: 24px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); }
    .btn-action-task { display: inline-flex; align-items: center; gap: 4px; padding: 6px 12px; border-radius: 6px; font-weight: 600; text-decoration: none; font-size: 0.85rem; }
    .btn-assign { background: #10b981; color: #fff; border: 1px solid #059669; }
    .btn-assign:hover { background: #059669; }
    .btn-change { border: 1px solid #cbd5e1; color: #334155; }
    .btn-change:hover { background: #f1f5f9; }

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
    <%-- THÊM MỚI: TIÊU ĐỀ PHÒNG BAN & BỘ CHỌN CHUYỂN 16 PHÒNG DÀNH CHO ADMIN --%>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; flex-wrap: wrap; gap: 12px;">
        <div>
            <h1 style="margin: 0; font-size: 1.6rem; color: #0f172a;">🏢 Phòng: ${leaderDept.departmentName}</h1>
            <p style="margin: 4px 0 0 0; color: #64748b;">${leaderDept.description}</p>
        </div>
        
        <div style="display: flex; align-items: center; gap: 12px;">
            <c:if test="${isManagerRole}">
                <div class="dept-switcher-box">
                    <span style="font-size: 0.85rem; font-weight: bold; color: #475569;">🔄 Đổi phòng xem:</span>
                    <select class="dept-switcher-select" onchange="window.location.href='${pageContext.request.contextPath}/leader-room?tab=topics&deptId=' + this.value">
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
        <a href="${pageContext.request.contextPath}/leader-room?tab=members&deptId=${leaderDept.departmentId}" class="room-tab-item">👥 Quản lý nhân viên</a>
        <a href="${pageContext.request.contextPath}/leader-room?tab=topics&deptId=${leaderDept.departmentId}" class="room-tab-item active">📋 Chủ đề + Phân công</a>
        <a href="${pageContext.request.contextPath}/leader-room?tab=progress&deptId=${leaderDept.departmentId}" class="room-tab-item">📊 Tổng kết tiến độ</a>
    </div>

    <!-- KHUNG SÁNG TẠO: TẠO CHỦ ĐỀ GIAO VIỆC -->
    <div class="topic-card">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px;">
            <h3 style="margin: 0; color: #1e3a8a;">💡 Khởi tạo Chủ đề / Dự án phòng ban</h3>
            <span style="font-size: 0.85rem; color: #64748b;">Chủ đề dùng để nhóm các đầu việc phân công cho nhân sự</span>
        </div>
        <form method="post" action="${pageContext.request.contextPath}/leader-room">
            <input type="hidden" name="action" value="createTopic"/>
            <input type="hidden" name="deptId" value="${leaderDept.departmentId}"/>
            <div style="display: grid; grid-template-columns: 1fr 2fr auto; gap: 12px;">
                <input type="text" name="topicTitle" placeholder="Tên chủ đề (Ví dụ: Chiến dịch Q4, Tối ưu SEO...)" required style="padding: 10px; border: 1.5px solid #cbd5e1; border-radius: 8px; outline: none;"/>
                <input type="text" name="description" placeholder="Mục tiêu hoặc hướng dẫn triển khai..." style="padding: 10px; border: 1.5px solid #cbd5e1; border-radius: 8px; outline: none;"/>
                <button type="submit" class="iv-btn" style="background: #2563eb; color: #fff; border: none; border-radius: 8px; padding: 10px 20px; font-weight: bold; cursor: pointer;">
                    ➕ Thêm chủ đề
                </button>
            </div>
        </form>

        <c:if test="${not empty topics}">
            <div style="display: flex; gap: 8px; flex-wrap: wrap; margin-top: 15px;">
                <c:forEach var="t" items="${topics}">
                    <span style="background: #eff6ff; border: 1px solid #bfdbfe; color: #1e40af; padding: 6px 12px; border-radius: 20px; font-size: 0.85rem; font-weight: 600;">
                        📌 <b>${t.topicTitle}</b>: ${t.description}
                    </span>
                </c:forEach>
            </div>
        </c:if>
    </div>

    <!-- BẢNG NHÂN VIÊN VỚI 2 NÚT THAO TÁC: THAY ĐỔI & GIAO VIỆC -->
    <div class="iv-card" style="background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 20px;">
        <h3 style="margin: 0 0 16px 0;">Danh sách nhân sự phân công nhiệm vụ</h3>
        <table class="iv-table" style="width: 100%;">
            <thead>
                <tr>
                    <th>Mã NV</th>
                    <th>Họ và tên</th>
                    <th>Chức danh</th>
                    <th>Email</th>
                    <th>Số điện thoại</th>
                    <th>Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="m" items="${members}">
                    <tr>
                        <td><b>${m.employeeCode}</b></td>
                        <td><b>${m.fullName}</b></td>
                        <td>${m.position}</td>
                        <td>${m.email}</td>
                        <td>${m.phone}</td>
                        <td>
                            <div style="display: flex; gap: 8px;">
                                <a href="${pageContext.request.contextPath}/department-member-edit?deptName=${leaderDept.departmentName}&empId=${m.employeeId}" class="btn-action-task btn-change">
                                    ✏️ Thay đổi
                                </a>
                                <a href="${pageContext.request.contextPath}/leader-room?tab=topics&empId=${m.employeeId}&deptId=${leaderDept.departmentId}" class="btn-action-task btn-assign">
                                    📋 Giao việc
                                </a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/interview/staff_footer.jsp"/>