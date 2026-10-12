<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Phòng ${dept.departmentName}" scope="request"/>
<c:set var="currentNav" value="deptOrg" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<style>
    .dept-detail-header {
        display: flex; align-items: center; justify-content: space-between;
        margin-bottom: 24px; padding-bottom: 16px; border-bottom: 1px solid #e2e8f0;
    }
    .back-btn {
        display: inline-flex; align-items: center; gap: 8px; font-weight: 600;
        color: #2563eb; text-decoration: none; font-size: 1rem; margin-bottom: 8px;
    }
    .back-btn:hover { text-decoration: underline; }
    .leader-card {
        background: linear-gradient(135deg, #eff6ff 0%, #f8fafc 100%);
        border: 1.5px solid #bfdbfe; border-radius: 12px; padding: 22px 26px;
        margin-bottom: 28px; position: relative; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);
    }
    .leader-badge {
        background: #2563eb; color: #fff; padding: 4px 12px; border-radius: 20px;
        font-size: 0.8rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px;
    }
    .btn-action-icon {
        background: #ffffff; border: 1.5px solid #cbd5e1; border-radius: 8px;
        padding: 6px 14px; font-size: 0.9rem; font-weight: 600; color: #1e293b;
        cursor: pointer; text-decoration: none; display: inline-flex; align-items: center; gap: 6px;
        transition: all 0.2s;
    }
    .btn-action-icon:hover { background: #f1f5f9; border-color: #3b82f6; color: #2563eb; transform: translateY(-1px); }
</style>

<div class="iv-shell-content">
    <a href="${pageContext.request.contextPath}/department-manage" class="back-btn">
        <span style="font-size: 1.3rem; font-weight: bold;">‹</span> Quay lại Tổ chức & Phân bổ phòng ban
    </a>

    <div class="dept-detail-header">
        <div>
            <h1 style="margin: 0; font-size: 1.8rem; color: #0f172a;">🏢 Phòng: <c:out value="${dept.departmentName}"/></h1>
            <p style="margin: 6px 0 0 0; color: #64748b;"><c:out value="${dept.description}"/></p>
        </div>
        <div>
            <span class="iv-badge iv-badge-blue" style="font-size: 0.95rem; padding: 6px 14px;">
                Tổng quy mô: <b>${totalCount} nhân sự</b>
            </span>
        </div>
    </div>

    <c:if test="${param.status == 'leader_updated'}">
        <div class="iv-alert iv-alert-success">Cập nhật Trưởng nhóm thành công!</div>
    </c:if>
    <c:if test="${param.status == 'member_updated'}">
        <div class="iv-alert iv-alert-success">Đã cập nhật thông tin và điều chuyển nhân sự thành công!</div>
    </c:if>

    <!-- BẢNG 1: TRƯỞNG NHÓM (TÁCH RIÊNG) -->
    <div class="leader-card">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
            <div style="display: flex; align-items: center; gap: 10px;">
                <span class="leader-badge">⭐ Trưởng nhóm / Leader</span>
                <span class="iv-small iv-muted">Phụ trách điều hành & quản lý chất trị phòng ban</span>
            </div>
            <!-- ICON THAY ĐỔI TRƯỞNG NHÓM -->
            <a href="${pageContext.request.contextPath}/department-member-edit?deptName=${dept.departmentName}&isLeader=1" class="btn-action-icon">
                ✏️ Thay đổi Trưởng nhóm
            </a>
        </div>

        <c:choose>
            <c:when test="${not empty leader}">
                <div class="iv-table-wrap" style="background: transparent; box-shadow: none;">
                    <table class="iv-table" style="background: #fff; border-radius: 8px;">
                        <thead>
                            <tr>
                                <th>Mã NV</th>
                                <th>Họ và tên</th>
                                <th>Chức vụ đảm nhiệm</th>
                                <th>Email liên hệ</th>
                                <th>Số điện thoại</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td><b><c:out value="${leader.employeeCode}"/></b></td>
                                <td><b style="color: #1e3a8a; font-size: 1.05rem;"><c:out value="${leader.fullName}"/></b></td>
                                <td><span class="iv-badge iv-badge-green"><c:out value="${leader.position}"/></span></td>
                                <td><c:out value="${leader.email}"/></td>
                                <td><c:out value="${leader.phone}"/></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/department-member-edit?deptName=${dept.departmentName}&empId=${leader.employeeId}" 
                                       class="btn-action-icon" style="padding: 4px 10px; font-size: 0.85rem;">
                                        ⚙️ Sửa thông tin
                                    </a>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div style="padding: 16px; background: #fff; border-radius: 8px; color: #dc2626; font-weight: 500;">
                    ⚠️ Phòng ban này hiện chưa bổ nhiệm Trưởng nhóm. Vui lòng bấm <b>"Thay đổi Trưởng nhóm"</b> để chỉ định.
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- BẢNG 2: DANH SÁCH NHÂN SỰ -->
    <div class="iv-card">
        <div class="iv-section-head">
            <div>
                <h2>Danh sách thành viên nhân sự (${staffList.size()})</h2>
                <span class="iv-small iv-muted">Các nhân viên thuộc biên chế hoạt động trong phòng ban.</span>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty staffList}">
                <div class="iv-empty">Chưa có thành viên nhân sự nào khác trong phòng ban này.</div>
            </c:when>
            <c:otherwise>
                <div class="iv-table-wrap">
                    <table class="iv-table">
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
                            <c:forEach var="m" items="${staffList}">
                                <tr>
                                    <td><b><c:out value="${not empty m.employeeCode ? m.employeeCode : '—'}"/></b></td>
                                    <td><b><c:out value="${m.fullName}"/></b></td>
                                    <td><span class="iv-badge iv-badge-blue"><c:out value="${m.position}"/></span></td>
                                    <td><c:out value="${m.email}"/></td>
                                    <td><c:out value="${m.phone}"/></td>
                                    <td><fmt:formatDate value="${m.hireDate}" pattern="dd/MM/yyyy"/></td>
                                    <td>
                                        <!-- NÚT THAY ĐỔI NHÂN SỰ BẤM ĐƯỢC -->
                                        <a href="${pageContext.request.contextPath}/department-member-edit?deptName=${dept.departmentName}&empId=${m.employeeId}" 
                                           class="btn-action-icon">
                                            ✏️ Thay đổi
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/interview/staff_footer.jsp"/>