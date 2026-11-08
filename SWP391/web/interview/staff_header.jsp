<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>${empty pageTitle ? 'Nhân sự' : pageTitle} - HR Tools</title>
        <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/img/favicon.svg">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&family=Inter:wght@300;400;500;600;700;800;900&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/interview.css">
        <style>
            .leader-menu-group {
                margin: 10px 0;
                padding-top: 10px;
                border-top: 1px solid rgba(255, 255, 255, 0.15);
            }
            .leader-menu-title {
                display: block;
                padding: 6px 12px;
                font-size: 0.8rem;
                text-transform: uppercase;
                letter-spacing: 0.5px;
                color: #93c5fd;
                font-weight: 700;
            }
            .leader-sub-nav a {
                padding-left: 20px !important;
                font-size: 0.88rem !important;
            }
            /* Style sáng tạo bổ sung cho Dropdown chọn 16 phòng của Admin trên Taskbar */
            .leader-dept-select {
                width: calc(100% - 24px);
                margin: 4px 12px 8px 12px;
                background: #1e293b;
                color: #38bdf8;
                border: 1px solid #0284c7;
                padding: 6px 8px;
                border-radius: 6px;
                font-size: 0.8rem;
                font-weight: 600;
                cursor: pointer;
                outline: none;
            }
        </style>
    </head>
    <body class="iv-body">
        <div class="iv-shell">
            <aside class="iv-sidebar">
                <div class="iv-logo">
                    <a href="${pageContext.request.contextPath}/home" style="text-decoration:none;">
                        <img src="${pageContext.request.contextPath}/img/logo-white.svg" alt="Job Board Logo" class="iv-logo-img">
                    </a>
                    <span class="iv-logo-badge">HR PORTAL</span>
                </div>
                <nav class="iv-nav">
                    <c:if test="${sessionScope.roleName == 'HR Staff' || sessionScope.roleName == 'Manager'}">
                        <a href="${pageContext.request.contextPath}/job-post"
                           class="${currentNav == 'jobpost' ? 'active' : ''}">Tạo JD</a>
                        <a href="${pageContext.request.contextPath}/screening"
                           class="${currentNav == 'screening' ? 'active' : ''}">Sơ lọc hồ sơ</a>
                        <a href="${pageContext.request.contextPath}/interview-schedule"
                           class="${currentNav == 'schedule' ? 'active' : ''}">Lên lịch phỏng vấn</a>
                        <a href="${pageContext.request.contextPath}/interview-calendar"
                           class="${currentNav == 'calendar' ? 'active' : ''}">Lịch phỏng vấn</a>
                        <a href="${pageContext.request.contextPath}/barem"
                           class="${currentNav == 'barem' ? 'active' : ''}">Barem đánh giá</a>
                        <a href="${pageContext.request.contextPath}/employee-profiles"
                           class="${currentNav == 'employeeProfiles' ? 'active' : ''}">Hồ sơ nhân viên</a>
                        <%-- Taskbar Tổ chức phòng ban & Gán nhân sự dành cho Manager --%>
                        <c:if test="${sessionScope.roleName == 'Manager' || sessionScope.userRole == 'Manager' || sessionScope.userRole == 'Admin'}">
                            <a href="${pageContext.request.contextPath}/department-manage"
                               class="${currentNav == 'deptOrg' ? 'active' : ''}">Tổ chức phòng ban</a>
                        </c:if>
                        <%-- Yêu cầu tạo tài khoản: chỉ dành cho HR Staff - Manager không được tự yêu cầu tạo tài khoản --%>
                        <c:if test="${sessionScope.roleName == 'HR Staff'}">
                            <a href="${pageContext.request.contextPath}/request-account"
                               class="${currentNav == 'requestAccount' ? 'active' : ''}">Yêu cầu tạo tài khoản</a>
                        </c:if>
                    </c:if>

                    <%-- ========================================================================= --%>
                    <%-- TASKBAR "QUẢN LÝ PHÒNG" VÀ CÁC TASKBAR CON (LEADER ROOM) --%>
                    <%-- ========================================================================= --%>
                    <%
                        dal.DepartmentDAO headerDeptDao = new dal.DepartmentDAO();
                        Object curEmpIdObj = session.getAttribute("employeeId");
                        if (curEmpIdObj == null) curEmpIdObj = session.getAttribute("staffId");
                        if (curEmpIdObj == null) curEmpIdObj = session.getAttribute("userId");

                        // Kiểm tra quyền Admin/Manager để kích hoạt bộ chuyển 16 phòng
                        String rName = (String) session.getAttribute("roleName");
                        String uRole = (String) session.getAttribute("userRole");
                        boolean isSidebarMgr = "Manager".equalsIgnoreCase(rName) || "Admin".equalsIgnoreCase(rName)
                                            || "Manager".equalsIgnoreCase(uRole) || "Admin".equalsIgnoreCase(uRole);
                        request.setAttribute("isSidebarManager", isSidebarMgr);

                        if (isSidebarMgr) {
                            request.setAttribute("sidebarAllDepts", headerDeptDao.getAllDepartmentsWithCount());
                        } else if (curEmpIdObj != null) {
                            try {
                                int leaderEmpId = Integer.parseInt(curEmpIdObj.toString());
                                model.DepartmentModel myLeaderDept = headerDeptDao.getDepartmentByLeaderId(leaderEmpId);
                                if (myLeaderDept != null) {
                                    request.setAttribute("myLeaderDept", myLeaderDept);
                                }
                            } catch (Exception ignored) {}
                        }
                    %>

                    <%-- TRƯỜNG HỢP 1: ADMIN / MANAGER -> CÓ DROPDOWN CHUYỂN ĐỔI 16 PHÒNG BAN --%>
                    <c:if test="${isSidebarManager}">
                        <div class="leader-menu-group">
                            <span class="leader-menu-title">Quản lý phòng:</span>
                            <select class="leader-dept-select" onchange="onSidebarSwitchDept(this.value)">
                                <c:forEach var="d" items="${sidebarAllDepts}">
                                    <option value="${d.departmentId}" ${leaderDept.departmentId == d.departmentId ? 'selected' : ''}>
                                        ${d.departmentName} (${d.employeeCount} NV)
                                    </option>
                                </c:forEach>
                            </select>
                            <div class="leader-sub-nav">
                                <a href="${pageContext.request.contextPath}/leader-room?tab=members&deptId=${not empty leaderDept ? leaderDept.departmentId : (sidebarAllDepts[0].departmentId)}"
                                   class="${currentNav == 'leaderMembers' ? 'active' : ''}">Quản lý nhân viên</a>
                                <a href="${pageContext.request.contextPath}/leader-room?tab=topics&deptId=${not empty leaderDept ? leaderDept.departmentId : (sidebarAllDepts[0].departmentId)}"
                                   class="${currentNav == 'leaderTopics' ? 'active' : ''}">Chủ đề + Phân công</a>
                                <a href="${pageContext.request.contextPath}/leader-room?tab=progress&deptId=${not empty leaderDept ? leaderDept.departmentId : (sidebarAllDepts[0].departmentId)}"
                                   class="${currentNav == 'leaderProgress' ? 'active' : ''}">Tổng kết tiến độ</a>
                                <a href="${pageContext.request.contextPath}/leader-room?tab=archive&deptId=${not empty leaderDept ? leaderDept.departmentId : (sidebarAllDepts[0].departmentId)}"
                                   class="${currentNav == 'leaderArchive' ? 'active' : ''}">Kho lưu trữ</a>
                            </div>
                        </div>
                    </c:if>

                    <%-- TRƯỜNG HỢP 2: EMPLOYEE ĐƯỢC CHỈ ĐỊNH LÀM LEADER 1 PHÒNG CỤ THỂ --%>
                    <c:if test="${not isSidebarManager && not empty myLeaderDept}">
                        <div class="leader-menu-group">
                            <span class="leader-menu-title">Quản lý phòng: ${myLeaderDept.departmentName}</span>
                            <div class="leader-sub-nav">
                                <a href="${pageContext.request.contextPath}/leader-room?tab=members"
                                   class="${currentNav == 'leaderMembers' ? 'active' : ''}">Quản lý nhân viên</a>
                                <a href="${pageContext.request.contextPath}/leader-room?tab=topics"
                                   class="${currentNav == 'leaderTopics' ? 'active' : ''}">Chủ đề + Phân công</a>
                                <a href="${pageContext.request.contextPath}/leader-room?tab=progress"
                                   class="${currentNav == 'leaderProgress' ? 'active' : ''}">Tổng kết tiến độ</a>
                                <a href="${pageContext.request.contextPath}/leader-room?tab=archive"
                                   class="${currentNav == 'leaderArchive' ? 'active' : ''}">Kho lưu trữ</a>
                            </div>
                        </div>
                    </c:if>

                    <%-- TRƯỜNG HỢP 3: EMPLOYEE BÌNH THƯỜNG -> CHỈ XEM TỔNG KẾT TIẾN ĐỘ PHÒNG MÌNH --%>
                    <c:if test="${not isSidebarManager && empty myLeaderDept}">
                        <div class="leader-menu-group">
                            <span class="leader-menu-title">Phòng ban của tôi</span>
                            <div class="leader-sub-nav">
                                <a href="${pageContext.request.contextPath}/leader-room?tab=progress"
                                   class="${currentNav == 'leaderProgress' ? 'active' : ''}">Tổng kết tiến độ</a>
                            </div>
                        </div>
                    </c:if>
                    <%-- ========================================================================= --%>

                    <a href="${pageContext.request.contextPath}/my-profile-documents"
                       class="${currentNav == 'myProfile' ? 'active' : ''}">Hồ sơ của tôi</a>
                </nav>
                <div class="iv-sidebar-footer">
                    Đăng nhập với<br>
                    <b style="color:#fff"><c:out value="${sessionScope.staffName}"/></b>
                    (<c:out value="${sessionScope.roleName}"/>)<br>
                    <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                </div>
            </aside>
            <main class="iv-main">

            <script>
                function onSidebarSwitchDept(deptId) {
                    var currentUrl = new URL(window.location.href);
                    var currentTab = currentUrl.searchParams.get("tab") || "members";
                    window.location.href = '${pageContext.request.contextPath}/leader-room?tab=' + currentTab + '&deptId=' + deptId;
                }
            </script>