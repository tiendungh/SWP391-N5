<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Tổng kết tiến độ phòng ban" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<style>
    .room-shell { max-width: 1200px; margin: 0 auto; padding: 20px; }
    .room-tabs { display: flex; gap: 10px; margin-bottom: 20px; border-bottom: 2px solid #e2e8f0; padding-bottom: 10px; }
    .room-tab-item { padding: 10px 20px; text-decoration: none; font-weight: 600; color: #64748b; border-radius: 8px; }
    .room-tab-item.active { background: #2563eb; color: #fff; }
    
    .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
    .stat-box { background: #fff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 18px; box-shadow: 0 1px 3px rgba(0,0,0,0.04); }
    .stat-val { font-size: 1.8rem; font-weight: bold; margin-bottom: 4px; }
    
    .progress-bar-wrap { width: 100%; height: 10px; background: #e2e8f0; border-radius: 5px; overflow: hidden; margin-top: 6px; }
    .progress-bar-fill { height: 100%; background: #10b981; transition: width 0.3s ease; }

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
    <%-- TIÊU ĐỀ PHÒNG BAN & BỘ CHỌN CHUYỂN 16 PHÒNG DÀNH CHO ADMIN --%>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; flex-wrap: wrap; gap: 12px;">
        <div>
            <h1 style="margin: 0; font-size: 1.6rem; color: #0f172a;">Phòng: ${leaderDept.departmentName}</h1>
            <p style="margin: 4px 0 0 0; color: #64748b;">${leaderDept.description}</p>
        </div>
        
        <div style="display: flex; align-items: center; gap: 12px;">
            <c:if test="${isManagerRole}">
                <div class="dept-switcher-box">
                    <span style="font-size: 0.85rem; font-weight: bold; color: #475569;">Đổi phòng xem:</span>
                    <select class="dept-switcher-select" onchange="window.location.href='${pageContext.request.contextPath}/leader-room?tab=progress&deptId=' + this.value">
                        <c:forEach var="d" items="${allDepartments}">
                            <option value="${d.departmentId}" ${leaderDept.departmentId == d.departmentId ? 'selected' : ''}>
                                ${d.departmentName} (${d.employeeCount} NV)
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </c:if>

            <span style="background: #e0e7ff; color: #3730a3; padding: 8px 16px; border-radius: 20px; font-weight: 700; font-size: 0.95rem;">
                Tổng số việc: ${not empty totalTasks ? totalTasks : 0}
            </span>
        </div>
    </div>

    <!-- 4 TASKBARS ĐIỀU HƯỚNG: BỔ SUNG "KHO LƯU TRỮ" CHO TRƯỞNG PHÒNG VÀ ADMIN -->
    <c:if test="${canEdit}">
        <div class="room-tabs">
            <a href="${pageContext.request.contextPath}/leader-room?tab=members&deptId=${leaderDept.departmentId}" class="room-tab-item">Quản lý nhân viên</a>
            <a href="${pageContext.request.contextPath}/leader-room?tab=topics&deptId=${leaderDept.departmentId}" class="room-tab-item">Chủ đề + Phân công</a>
            <a href="${pageContext.request.contextPath}/leader-room?tab=progress&deptId=${leaderDept.departmentId}" class="room-tab-item active">Tổng kết tiến độ</a>
            <a href="${pageContext.request.contextPath}/leader-room?tab=archive&deptId=${leaderDept.departmentId}" class="room-tab-item">Kho lưu trữ</a>
        </div>
    </c:if>

    <!-- 4 HỘP THỐNG KÊ TIẾN ĐỘ TỔNG HỢP -->
    <div class="stats-grid">
        <div class="stat-box">
            <div class="stat-val" style="color: #2563eb;">${not empty totalTasks ? totalTasks : 0}</div>
            <div style="color: #64748b; font-size: 0.85rem;">Tổng số đầu việc đã giao</div>
        </div>
        <div class="stat-box">
            <div class="stat-val" style="color: #16a34a;">${not empty completedTasks ? completedTasks : 0}</div>
            <div style="color: #64748b; font-size: 0.85rem;">Đã hoàn thành (Trưởng phòng duyệt)</div>
        </div>
        <div class="stat-box">
            <div class="stat-val" style="color: #ea580c;">${not empty pendingTasks ? pendingTasks : 0}</div>
            <div style="color: #64748b; font-size: 0.85rem;">Đang xử lý / Chưa đạt</div>
        </div>
        <div class="stat-box">
            <div class="stat-val" style="color: #0284c7;">${not empty avgPercent ? avgPercent : 0}%</div>
            <div style="color: #64748b; font-size: 0.85rem;">Tiến độ trung bình toàn phòng</div>
            <div class="progress-bar-wrap">
                <div class="progress-bar-fill" style="width: ${not empty avgPercent ? avgPercent : 0}%; background: ${(not empty avgPercent && avgPercent >= 100) ? '#10b981' : '#0284c7'};"></div>
            </div>
        </div>
    </div>

    <!-- BẢNG TỔNG HỢP CHI TIẾT CỦA CẢ PHÒNG -->
    <div class="iv-card" style="background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 20px;">
        <h3 style="margin: 0 0 16px 0;">Theo dõi tiến độ chi tiết toàn bộ nhân sự</h3>
        <table class="iv-table" style="width: 100%;">
            <thead>
                <tr>
                    <th>Mã NV</th>
                    <th>Nhân sự</th>
                    <th>Nội dung công việc</th>
                    <th>Deadline</th>
                    <th>Tiến độ</th>
                    <th>Ghi chú của Trưởng phòng</th>
                    <th>Trạng thái</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty allTasks}">
                        <tr><td colspan="7" style="text-align: center; color: #64748b; padding: 24px;">Phòng ban chưa có nhiệm vụ nào được phân công.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="t" items="${allTasks}">
                            <tr>
                                <td><b><c:out value="${not empty t.employeeCode ? t.employeeCode : '—'}"/></b></td>
                                <td><b><c:out value="${not empty t.employeeName ? t.employeeName : 'Nhân sự'}"/></b></td>
                                <td><c:out value="${t.taskContent}"/></td>
                                <td>
                                    <span style="color: #dc2626; font-weight: 600;">
                                        ${not empty t.deadline ? t.deadline : '—'}
                                    </span>
                                </td>
                                <td>
                                    <b>${t.progress}%</b>
                                    <div class="progress-bar-wrap" style="width: 90px;">
                                        <div class="progress-bar-fill" style="width: ${t.progress}%; background: ${(t.progress >= 100) ? '#10b981' : '#3b82f6'};"></div>
                                    </div>
                                </td>
                                <td><c:out value="${empty t.leaderNote ? '—' : t.leaderNote}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${t.progress >= 100}">
                                            <span style="background: #dcfce7; color: #15803d; padding: 4px 8px; border-radius: 12px; font-weight: bold; font-size: 0.8rem;">
                                                ✓ Đã duyệt xong
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="background: #fef3c7; color: #b45309; padding: 4px 8px; border-radius: 12px; font-weight: bold; font-size: 0.8rem;">
                                                ⏳ Đang tiến hành (${t.progress}%)
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>

        <!-- PHẦN CUỐI GIAO DIỆN: ICON VÀ NÚT LƯU CHỦ ĐỀ VÀO KHO LƯU TRỮ -->
        <c:if test="${canEdit && not empty allTasks}">
            <div style="margin-top: 24px; padding-top: 18px; border-top: 1.5px dashed #cbd5e1; display: flex; justify-content: flex-end; align-items: center;">
                <form method="post" action="${pageContext.request.contextPath}/leader-room" style="display: inline-flex; align-items: center; gap: 10px; margin: 0;">
                    <input type="hidden" name="action" value="saveArchive"/>
                    <input type="hidden" name="deptId" value="${leaderDept.departmentId}"/>
                    <input type="text" name="projectName" placeholder="Nhập tên dự án / đợt lưu trữ (VD: Dự án Lego Q4)..." required style="padding: 10px 14px; border: 1.5px solid #94a3b8; border-radius: 8px; width: 340px; font-size: 0.9rem; outline: none;"/>
                    <button type="submit" style="background: #059669; color: #fff; border: none; padding: 10px 20px; border-radius: 8px; font-weight: bold; font-size: 0.95rem; cursor: pointer; display: inline-flex; align-items: center; gap: 6px;">
                        Lưu chủ đề
                    </button>
                </form>
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="/interview/staff_footer.jsp"/>
