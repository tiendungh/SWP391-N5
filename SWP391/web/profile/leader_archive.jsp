<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Kho lưu trữ dự án phòng ban" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<style>
    .room-shell { max-width: 1200px; margin: 0 auto; padding: 20px; }
    .room-tabs { display: flex; gap: 10px; margin-bottom: 20px; border-bottom: 2px solid #e2e8f0; padding-bottom: 10px; }
    .room-tab-item { padding: 10px 20px; text-decoration: none; font-weight: 600; color: #64748b; border-radius: 8px; }
    .room-tab-item.active { background: #2563eb; color: #fff; }
    .archive-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
    .project-link { font-size: 1.05rem; font-weight: 700; color: #2563eb; text-decoration: none; }
    .project-link:hover { text-decoration: underline; color: #1d4ed8; }
</style>

<div class="room-shell">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; flex-wrap: wrap;">
        <div>
            <h1 style="margin: 0; font-size: 1.6rem; color: #0f172a;">Phòng: ${leaderDept.departmentName}</h1>
            <p style="margin: 4px 0 0 0; color: #64748b;">${leaderDept.description}</p>
        </div>
        <span style="background: #f1f5f9; color: #475569; padding: 8px 16px; border-radius: 20px; font-weight: 700; font-size: 0.95rem;">
            Đã lưu trữ: ${archiveList != null ? archiveList.size() : 0} dự án
        </span>
    </div>

    <!-- 4 TABS ĐIỀU HƯỚNG -->
    <div class="room-tabs">
        <a href="${pageContext.request.contextPath}/leader-room?tab=members&deptId=${leaderDept.departmentId}" class="room-tab-item">Quản lý nhân viên</a>
        <a href="${pageContext.request.contextPath}/leader-room?tab=topics&deptId=${leaderDept.departmentId}" class="room-tab-item">Chủ đề + Phân công</a>
        <a href="${pageContext.request.contextPath}/leader-room?tab=progress&deptId=${leaderDept.departmentId}" class="room-tab-item">Tổng kết tiến độ</a>
        <a href="${pageContext.request.contextPath}/leader-room?tab=archive&deptId=${leaderDept.departmentId}" class="room-tab-item active">Kho lưu trữ</a>
    </div>

    <c:if test="${param.msg == 'archived'}">
        <div style="background: #dcfce7; border: 1.5px solid #86efac; color: #166534; padding: 12px 18px; border-radius: 8px; margin-bottom: 18px; font-weight: 600;">
            ✅ Đã lưu toàn bộ tiến độ của dự án vào Kho lưu trữ thành công!
        </div>
    </c:if>

    <div class="archive-card">
        <h3 style="margin: 0 0 16px 0; color: #1e3a8a;">DANH SÁCH DỰ ÁN ĐÃ LƯU TRỮ TRONG PHÒNG BAN</h3>
        <table class="iv-table" style="width: 100%;">
            <thead>
                <tr>
                    <th style="width: 40%;">Tên dự án / Chủ đề lưu trữ</th>
                    <th style="width: 20%;">Ngày lưu trữ</th>
                    <th style="width: 15%; text-align: center;">Tổng số việc</th>
                    <th style="width: 15%; text-align: center;">Tiến độ đạt</th>
                    <th style="width: 10%; text-align: center;">Xem lại</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty archiveList}">
                        <tr>
                            <td colspan="5" style="text-align: center; color: #94a3b8; padding: 32px;">
                                Kho lưu trữ của phòng ban hiện chưa có dự án nào được đóng.
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="a" items="${archiveList}">
                            <tr>
                                <td>
                                    <a class="project-link" href="${pageContext.request.contextPath}/leader-room?tab=archive&archiveId=${a.archiveId}&deptId=${leaderDept.departmentId}">
                                        📁 ${a.projectName}
                                    </a>
                                </td>
                                <td>
                                    <fmt:formatDate value="${a.archiveDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                </td>
                                <td style="text-align: center;">
                                    <b>${a.completedTasks} / ${a.totalTasks} việc</b>
                                </td>
                                <td style="text-align: center;">
                                    <span style="background: #e0f2fe; color: #0369a1; padding: 4px 10px; border-radius: 12px; font-weight: bold;">
                                        ${a.avgPercent}%
                                    </span>
                                </td>
                                <td style="text-align: center;">
                                    <a href="${pageContext.request.contextPath}/leader-room?tab=archive&archiveId=${a.archiveId}&deptId=${leaderDept.departmentId}" style="background: #2563eb; color: #fff; padding: 6px 12px; border-radius: 6px; text-decoration: none; font-weight: bold; font-size: 0.8rem;">
                                        Mở xem
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/interview/staff_footer.jsp"/>
