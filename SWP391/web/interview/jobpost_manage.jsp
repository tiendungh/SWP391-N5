<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Tạo JD" scope="request"/>
<c:set var="currentNav" value="jobpost" scope="request"/>
<jsp:include page="staff_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Job Description (JD)</h1>
        <p>Tạo tin tuyển dụng mới và quản lý các JD đang mở / đã đóng.</p>
    </div>
    <div class="iv-whoami">Xin chào, <b>${sessionScope.staffName}</b></div>
</div>

<c:if test="${not empty error}"><div class="iv-alert iv-alert-error">${error}</div></c:if>
<c:if test="${param.created == '1'}"><div class="iv-alert iv-alert-success">Đã tạo JD mới thành công.</div></c:if>

<div class="iv-grid-2">
    <div class="iv-card">
        <h2>Danh sách JD (${jobPosts.size()})</h2>
        <c:choose>
            <c:when test="${empty jobPosts}">
                <div class="iv-empty">Chưa có JD nào. Tạo JD đầu tiên bên phải.</div>
            </c:when>
            <c:otherwise>
                <table class="iv-table">
                    <thead><tr><th>Vị trí</th><th>Lương</th><th>Hạn nộp</th><th>Ứng tuyển</th><th>Trạng thái</th><th></th></tr></thead>
                    <tbody>
                        <c:forEach var="j" items="${jobPosts}">
                            <tr>
                                <td><b>${j.title}</b><br><span class="iv-small iv-muted">${j.category} · ${j.location}</span></td>
                                <td class="iv-small">
                                    <c:if test="${not empty j.offerMin}">${j.offerMin} - ${j.offerMax}</c:if>
                                    <c:if test="${empty j.offerMin}">Thỏa thuận</c:if>
                                </td>
                                <td class="iv-small"><fmt:formatDate value="${j.deadline}" pattern="dd/MM/yyyy"/></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/screening?jobPostId=${j.jobPostId}" class="iv-badge iv-badge-blue" style="text-decoration:none;">${j.applyCount} hồ sơ</a>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${j.visible}"><span class="iv-badge iv-badge-green">Đang mở</span></c:when>
                                        <c:otherwise><span class="iv-badge iv-badge-gray">Đã đóng</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <form method="post" action="${pageContext.request.contextPath}/job-post" style="display:inline;">
                                        <input type="hidden" name="action" value="toggle"/>
                                        <input type="hidden" name="jobPostId" value="${j.jobPostId}"/>
                                        <input type="hidden" name="newVisible" value="${j.visible ? 0 : 1}"/>
                                        <button type="submit" class="iv-btn iv-btn-sm iv-btn-ghost">${j.visible ? 'Đóng tuyển' : 'Mở lại'}</button>
                                    </form>
                                    <a href="${pageContext.request.contextPath}/jobs?id=${j.jobPostId}" target="_blank" class="iv-btn iv-btn-sm iv-btn-ghost">Xem public</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="iv-card">
        <h2>Tạo JD mới</h2>
        <form method="post" action="${pageContext.request.contextPath}/job-post">
            <input type="hidden" name="action" value="create"/>
            <div class="iv-field" style="margin-bottom:14px;">
                <label>Tiêu đề *</label>
                <input type="text" name="title" placeholder="Vd: Tuyển Java Developer" required/>
            </div>
            <div class="iv-field" style="margin-bottom:14px;">
                <label>Mô tả công việc *</label>
                <textarea name="description" placeholder="Mô tả chi tiết công việc, yêu cầu..." style="min-height:110px;" required></textarea>
            </div>
            <div class="iv-form-row">
                <div class="iv-field">
                    <label>Phòng ban / Category</label>
                    <input type="text" name="category" placeholder="IT, Human Resources, Finance..."/>
                </div>
                <div class="iv-field">
                    <label>Vị trí (Position)</label>
                    <input type="text" name="position" placeholder="Java Developer"/>
                </div>
            </div>
            <div class="iv-form-row">
                <div class="iv-field">
                    <label>Địa điểm *</label>
                    <input type="text" name="location" placeholder="Hà Nội, Việt Nam" required/>
                </div>
                <div class="iv-field">
                    <label>Loại hình</label>
                    <select name="typeJob">
                        <option value="Full-time">Full-time</option>
                        <option value="Part-time">Part-time</option>
                        <option value="Internship">Thực tập</option>
                        <option value="Remote">Remote</option>
                    </select>
                </div>
            </div>
            <div class="iv-form-row">
                <div class="iv-field">
                    <label>Lương tối thiểu</label>
                    <input type="number" name="offerMin" min="0" step="0.01" placeholder="1000"/>
                </div>
                <div class="iv-field">
                    <label>Lương tối đa</label>
                    <input type="number" name="offerMax" min="0" step="0.01" placeholder="2000"/>
                </div>
                <div class="iv-field">
                    <label>Kinh nghiệm tối thiểu (năm)</label>
                    <input type="number" name="numberExp" min="0" step="1" placeholder="1"/>
                </div>
            </div>
            <div class="iv-form-row">
                <div class="iv-field">
                    <label>Hạn nộp hồ sơ</label>
                    <input type="date" name="deadline"/>
                </div>
                <div class="iv-field" style="justify-content:center;">
                    <label style="display:flex;align-items:center;gap:8px;margin-top:14px;">
                        <input type="checkbox" name="visible" checked style="width:auto;"/> Hiển thị công khai ngay
                    </label>
                </div>
            </div>
            <button type="submit" class="iv-btn">Tạo JD</button>
        </form>
    </div>
</div>

<jsp:include page="staff_footer.jsp"/>
