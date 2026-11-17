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

    /* THÊM MỚI: CSS cho ô tìm kiếm của Manager và nút Xóa */
    .search-bar-box {
        display: flex; gap: 10px; align-items: center; flex-wrap: wrap;
        background: #f8fafc; padding: 12px 16px; border-radius: 10px;
        border: 1px solid #e2e8f0; margin-bottom: 18px;
    }
    .search-input {
        flex: 1; min-width: 240px; padding: 9px 14px;
        border: 1.5px solid #cbd5e1; border-radius: 8px; font-size: 0.92rem; outline: none;
    }
    .search-input:focus { border-color: #2563eb; }
    .filter-select {
        padding: 9px 14px; border: 1.5px solid #cbd5e1; border-radius: 8px;
        font-size: 0.9rem; font-weight: 600; color: #334155; background: #fff; outline: none;
    }
    .btn-action-group { display: inline-flex; gap: 6px; align-items: center; justify-content: center; }
    .btn-del-arch {
        background: #fee2e2; color: #dc2626; border: 1px solid #fca5a5;
        padding: 5px 10px; border-radius: 6px; font-weight: bold; font-size: 0.8rem;
        cursor: pointer; white-space: nowrap;
    }
    .btn-del-arch:hover { background: #dc2626; color: #fff; }
</style>

<div class="room-shell">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; flex-wrap: wrap;">
        <div>
            <c:choose>
                <c:when test="${isManagerRole}">
                    <h1 style="margin: 0; font-size: 1.6rem; color: #0f172a;">🗄️ Kho lưu trữ tổng hợp (Tất cả phòng ban)</h1>
                    <p style="margin: 4px 0 0 0; color: #64748b;">Tra cứu và quản lý các dự án đã lưu trữ của toàn bộ các phòng</p>
                </c:when>
                <c:otherwise>
                    <h1 style="margin: 0; font-size: 1.6rem; color: #0f172a;">Phòng: ${leaderDept.departmentName}</h1>
                    <p style="margin: 4px 0 0 0; color: #64748b;">${leaderDept.description}</p>
                </c:otherwise>
            </c:choose>
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

    <%-- THÊM MỚI: Thông báo khi xóa thành công --%>
    <c:if test="${param.msg == 'deleted'}">
        <div style="background: #fee2e2; border: 1.5px solid #fca5a5; color: #991b1b; padding: 12px 18px; border-radius: 8px; margin-bottom: 18px; font-weight: 600;">
            🗑️ Đã xóa vĩnh viễn dự án khỏi Kho lưu trữ!
        </div>
    </c:if>

    <div class="archive-card">
        <h3 style="margin: 0 0 16px 0; color: #1e3a8a;">
            ${isManagerRole ? 'DANH SÁCH DỰ ÁN ĐÃ LƯU TRỮ TOÀN CÔNG TY' : 'DANH SÁCH DỰ ÁN ĐÃ LƯU TRỮ TRONG PHÒNG BAN'}
        </h3>

        <%-- THÊM MỚI: Ô tìm kiếm dự án dành riêng cho Manager --%>
        <c:if test="${isManagerRole}">
            <form method="get" action="${pageContext.request.contextPath}/leader-room" class="search-bar-box">
                <input type="hidden" name="tab" value="archive"/>
                <input type="hidden" name="deptId" value="${leaderDept.departmentId}"/>
                
                <input type="text" name="keyword" value="<c:out value='${searchKeyword}'/>" 
                       placeholder="🔍 Nhập tên dự án cần tìm kiếm..." class="search-input"/>

                <%-- SỬA LỖI TRẮNG TRANG: Ép kiểu về chuỗi String thuần túy qua nội dung thẻ c:set --%>
                <select name="filterDeptId" class="filter-select">
                    <option value="all" ${selectedFilterDeptId == 'all' ? 'selected' : ''}>-- Tất cả phòng ban --</option>
                    <c:forEach var="d" items="${allDepartments}">
                        <c:set var="deptIdStr">${d.departmentId}</c:set>
                        <option value="${d.departmentId}" ${selectedFilterDeptId == deptIdStr ? 'selected' : ''}>
                            ${d.departmentName}
                        </option>
                    </c:forEach>
                </select>

                <button type="submit" style="background: #2563eb; color: #fff; border: none; padding: 9px 18px; border-radius: 8px; font-weight: 700; cursor: pointer;">
                    Tìm kiếm
                </button>
                <c:if test="${not empty searchKeyword || (selectedFilterDeptId != null && selectedFilterDeptId ne 'all')}">
                    <a href="${pageContext.request.contextPath}/leader-room?tab=archive&deptId=${leaderDept.departmentId}" 
                       style="background: #e2e8f0; color: #475569; padding: 9px 14px; border-radius: 8px; text-decoration: none; font-weight: 600; font-size: 0.88rem;">
                        Đặt lại
                    </a>
                </c:if>
            </form>
        </c:if>

        <table class="iv-table" style="width: 100%;">
            <thead>
                <tr>
                    <th style="width: ${isManagerRole ? '30%' : '38%'};">Tên dự án / Chủ đề lưu trữ</th>
                    <c:if test="${isManagerRole}">
                        <th style="width: 16%;">Phòng ban</th>
                    </c:if>
                    <th style="width: 18%;">Ngày lưu trữ</th>
                    <th style="width: 12%; text-align: center;">Tổng số việc</th>
                    <th style="width: 10%; text-align: center;">Tiến độ đạt</th>
                    <th style="width: 14%; text-align: center;">Xem lại</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty archiveList}">
                        <tr>
                            <td colspan="${isManagerRole ? 6 : 5}" style="text-align: center; color: #94a3b8; padding: 32px;">
                                Kho lưu trữ hiện chưa có dự án nào được đóng.
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
                                <c:if test="${isManagerRole}">
                                    <td>
                                        <span style="background: #eef2ff; color: #4338ca; padding: 4px 10px; border-radius: 6px; font-weight: 700; font-size: 0.82rem;">
                                            🏢 <c:out value="${a.departmentName}"/>
                                        </span>
                                    </td>
                                </c:if>
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
                                    <div class="btn-action-group">
                                        <a href="${pageContext.request.contextPath}/leader-room?tab=archive&archiveId=${a.archiveId}&deptId=${leaderDept.departmentId}" style="background: #2563eb; color: #fff; padding: 6px 12px; border-radius: 6px; text-decoration: none; font-weight: bold; font-size: 0.8rem; white-space: nowrap;">
                                            Mở xem
                                        </a>
                                        
                                        <%-- THÊM MỚI: Nút Xóa hẳn dự án lưu trữ bên cạnh nút Mở xem --%>
                                        <form method="post" action="${pageContext.request.contextPath}/leader-room" style="margin: 0; display: inline;"
                                              onsubmit="return confirm('Bạn có chắc chắn muốn XÓA VĨNH VIỄN dự án lưu trữ này không? Dữ liệu sẽ không thể khôi phục!');">
                                            <input type="hidden" name="action" value="deleteArchive"/>
                                            <input type="hidden" name="archiveId" value="${a.archiveId}"/>
                                            <input type="hidden" name="deptId" value="${leaderDept.departmentId}"/>
                                            <button type="submit" class="btn-del-arch">Xóa</button>
                                        </form>
                                    </div>
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