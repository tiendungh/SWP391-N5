<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Chi tiết dự án đã lưu trữ" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<style>
    .room-shell { max-width: 1200px; margin: 0 auto; padding: 20px; }
    .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
    .stat-box { background: #fff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 18px; box-shadow: 0 1px 3px rgba(0,0,0,0.04); }
    .stat-val { font-size: 1.8rem; font-weight: bold; margin-bottom: 4px; }
    .progress-bar-wrap { width: 100%; height: 10px; background: #e2e8f0; border-radius: 5px; overflow: hidden; margin-top: 6px; }
    .progress-bar-fill { height: 100%; background: #10b981; }
</style>

<div class="room-shell">
    <a href="${pageContext.request.contextPath}/leader-room?tab=archive&deptId=${leaderDept.departmentId}" style="display:inline-flex; align-items:center; gap:6px; font-weight:600; color:#2563eb; text-decoration:none; margin-bottom:16px;">
        ‹ Quay lại danh sách Kho lưu trữ
    </a>

    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; flex-wrap: wrap;">
        <div>
            <h1 style="margin: 0; font-size: 1.6rem; color: #0f172a;">📁 Bản lưu: ${archiveDetail.projectName}</h1>
            <p style="margin: 4px 0 0 0; color: #64748b;">
                Lưu vào ngày: <b><fmt:formatDate value="${archiveDetail.archiveDate}" pattern="dd/MM/yyyy HH:mm:ss"/></b> · Phòng: <b>${leaderDept.departmentName}</b>
            </p>
        </div>
        <span style="background: #fef3c7; color: #92400e; padding: 6px 14px; border-radius: 16px; font-weight: bold; font-size: 0.85rem;">
            🔒 Bản snapshot lưu trữ (Chỉ xem)
        </span>
    </div>

    <!-- 4 THẺ THỐNG KÊ LỊCH SỬ -->
    <div class="stats-grid">
        <div class="stat-box">
            <div class="stat-val" style="color: #2563eb;">${archiveDetail.totalTasks}</div>
            <div style="color: #64748b; font-size: 0.85rem;">Tổng số đầu việc đã lưu</div>
        </div>
        <div class="stat-box">
            <div class="stat-val" style="color: #16a34a;">${archiveDetail.completedTasks}</div>
            <div style="color: #64748b; font-size: 0.85rem;">Đã hoàn thành</div>
        </div>
        <div class="stat-box">
            <div class="stat-val" style="color: #ea580c;">${archiveDetail.totalTasks - archiveDetail.completedTasks}</div>
            <div style="color: #64748b; font-size: 0.85rem;">Đang xử lý / Chưa đạt</div>
        </div>
        <div class="stat-box">
            <div class="stat-val" style="color: #0284c7;">${archiveDetail.avgPercent}%</div>
            <div style="color: #64748b; font-size: 0.85rem;">Tiến độ trung bình ghi nhận</div>
            <div class="progress-bar-wrap">
                <div class="progress-bar-fill" style="width: ${archiveDetail.avgPercent}%;"></div>
            </div>
        </div>
    </div>

    <!-- BẢNG CHI TIẾT TỪNG NHIỆM VỤ CỦA BẢN LƯU -->
    <div class="iv-card" style="background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 20px;">
        <h3 style="margin: 0 0 16px 0;">Nội dung chi tiết tiến độ tại thời điểm lưu</h3>
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
                <c:forEach var="t" items="${archiveDetail.taskList}">
                    <tr>
                        <td><b>${t.employeeCode}</b></td>
                        <td><b>${t.employeeName}</b></td>
                        <td>${t.taskContent}</td>
                        <td><span style="color: #dc2626; font-weight: 600;">${t.deadline}</span></td>
                        <td>
                            <b>${t.progress}%</b>
                            <div class="progress-bar-wrap" style="width: 90px;">
                                <div class="progress-bar-fill" style="width: ${t.progress}%; background: ${(t.progress >= 100) ? '#10b981' : '#3b82f6'};"></div>
                            </div>
                        </td>
                        <td>${empty t.leaderNote ? '—' : t.leaderNote}</td>
                        <td>
                            <c:choose>
                                <c:when test="${t.progress >= 100}">
                                    <span style="background: #dcfce7; color: #15803d; padding: 4px 8px; border-radius: 12px; font-weight: bold; font-size: 0.8rem;">
                                        ✓ Đã hoàn thành
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
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/interview/staff_footer.jsp"/>