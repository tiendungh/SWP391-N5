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
<c:if test="${param.updated == '1'}"><div class="iv-alert iv-alert-success">Đã cập nhật JD thành công.</div></c:if>

<div class="iv-grid-2" style="grid-template-columns: 1.7fr 1fr;">
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
                                    <div style="display:flex; flex-direction:column; gap:8px; align-items:stretch; min-width:140px;">
                                        <a href="${pageContext.request.contextPath}/job-post?editId=${j.jobPostId}#jd-form" class="iv-btn iv-btn-sm" style="justify-content:center; width:100%; box-sizing:border-box; white-space:nowrap; margin:0;">Sửa</a>
                                        <form method="post" action="${pageContext.request.contextPath}/job-post" style="margin:0;">
                                            <input type="hidden" name="action" value="toggle"/>
                                            <input type="hidden" name="jobPostId" value="${j.jobPostId}"/>
                                            <input type="hidden" name="newVisible" value="${j.visible ? 0 : 1}"/>
                                            <button type="submit" class="iv-btn iv-btn-sm" style="justify-content:center; width:100%; box-sizing:border-box; white-space:nowrap; margin:0;">${j.visible ? 'Đóng ứng tuyển' : 'Mở ứng tuyển'}</button>
                                        </form>
                                        <a href="${pageContext.request.contextPath}/jobs?id=${j.jobPostId}" target="_blank" class="iv-btn iv-btn-sm" style="justify-content:center; width:100%; box-sizing:border-box; white-space:nowrap; margin:0;">Xem public</a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="iv-card" id="jd-form">
        <h2>${editMode ? 'Sửa JD' : 'Tạo JD mới'}</h2>
        <form method="post" action="${pageContext.request.contextPath}/job-post">
            <input type="hidden" name="action" value="${editMode ? 'update' : 'create'}"/>
            <c:if test="${editMode}"><input type="hidden" name="jobPostId" value="${formJob.jobPostId}"/></c:if>
            <div class="iv-field" style="margin-bottom:14px;">
                <label>Tiêu đề *</label>
                <input type="text" name="title" placeholder="Vd: Tuyển Java Developer" value="<c:out value='${formJob.title}'/>" required/>
            </div>
            <div class="iv-field" style="margin-bottom:14px;">
                <label>Mô tả công việc *</label>
                <textarea name="description" placeholder="Mô tả chi tiết công việc, yêu cầu..." style="min-height:110px;" required><c:out value="${formJob.description}"/></textarea>
            </div>
            <div class="iv-form-row">
                <div class="iv-field">
                    <label>Phòng ban / Category</label>
                    <input type="text" name="category" placeholder="IT, Human Resources, Finance..." value="<c:out value='${formJob.category}'/>"/>
                </div>
                <div class="iv-field">
                    <label>Vị trí (Position)</label>
                    <input type="text" name="position" placeholder="Java Developer" value="<c:out value='${formJob.position}'/>"/>
                </div>
            </div>
            <div class="iv-form-row">
                <div class="iv-field">
                    <label>Địa điểm *</label>
                    <input type="text" name="location" placeholder="Hà Nội, Việt Nam" value="<c:out value='${formJob.location}'/>" required/>
                </div>
                <div class="iv-field">
                    <label>Loại hình</label>
                    <select name="typeJob">
                        <option value="Full-time" ${formJob.typeJob == 'Full-time' ? 'selected' : ''}>Full-time</option>
                        <option value="Part-time" ${formJob.typeJob == 'Part-time' ? 'selected' : ''}>Part-time</option>
                        <option value="Internship" ${formJob.typeJob == 'Internship' ? 'selected' : ''}>Thực tập</option>
                        <option value="Remote" ${formJob.typeJob == 'Remote' ? 'selected' : ''}>Remote</option>
                    </select>
                </div>
            </div>
            <div class="iv-form-row">
                <div class="iv-field">
                    <label>Lương tối thiểu</label>
                    <input type="number" name="offerMin" min="0" step="0.01" placeholder="1000" value="${formJob.offerMin}"/>
                </div>
                <div class="iv-field">
                    <label>Lương tối đa</label>
                    <input type="number" name="offerMax" min="0" step="0.01" placeholder="2000" value="${formJob.offerMax}"/>
                </div>
                <div class="iv-field">
                    <label>Kinh nghiệm tối thiểu (năm)</label>
                    <input type="number" name="numberExp" min="0" step="1" placeholder="1" value="${formJob.numberExp}"/>
                </div>
            </div>
            <div class="iv-form-row">
                <div class="iv-field">
                    <label>Hạn nộp hồ sơ</label>
                    <input type="date" name="deadline" value="<fmt:formatDate value='${formJob.deadline}' pattern='yyyy-MM-dd'/>"/>
                </div>
                <div class="iv-field" style="justify-content:center;">
                    <label style="display:flex;align-items:center;gap:8px;margin-top:14px;">
                        <input type="checkbox" name="visible" ${empty formJob || formJob.visible ? 'checked' : ''} style="width:auto;"/> Hiển thị công khai
                    </label>
                </div>
            </div>
            <button type="submit" class="iv-btn">${editMode ? 'Lưu thay đổi' : 'Tạo JD'}</button>
            <c:if test="${editMode}">
                <a href="${pageContext.request.contextPath}/job-post" class="iv-btn iv-btn-ghost">Hủy sửa</a>
            </c:if>
        </form>
    </div>
</div>

<jsp:include page="staff_footer.jsp"/>
