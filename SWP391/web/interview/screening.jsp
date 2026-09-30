<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Sơ lọc hồ sơ" scope="request"/>
<c:set var="currentNav" value="screening" scope="request"/>
<jsp:include page="staff_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Sơ lọc hồ sơ</h1>
        <p>Lọc ứng viên theo kinh nghiệm / học vấn / mức lương mong muốn, rồi duyệt Pass hoặc Loại hàng loạt.</p>
    </div>
    <div class="iv-whoami">Xin chào, <b>${sessionScope.staffName}</b></div>
</div>

<c:if test="${param.updated == '1'}"><div class="iv-alert iv-alert-success">Đã cập nhật trạng thái các hồ sơ đã chọn.</div></c:if>

<c:choose>
    <%-- ================= No JD chosen: pick one ================= --%>
    <c:when test="${empty jobPostId}">
        <div class="iv-card">
            <h2>Chọn JD cần sơ lọc</h2>
            <c:choose>
                <c:when test="${empty jobPosts}">
                    <div class="iv-empty">Chưa có JD nào. Vào mục "Tạo JD" để tạo tin tuyển dụng.</div>
                </c:when>
                <c:otherwise>
                    <table class="iv-table">
                        <thead><tr><th>Vị trí</th><th>Số hồ sơ</th><th>Trạng thái JD</th><th></th></tr></thead>
                        <tbody>
                            <c:forEach var="j" items="${jobPosts}">
                                <tr>
                                    <td><b>${j.title}</b><br><span class="iv-small iv-muted">${j.category} · ${j.location}</span></td>
                                    <td><span class="iv-badge iv-badge-blue">${j.applyCount}</span></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${j.visible}"><span class="iv-badge iv-badge-green">Đang mở</span></c:when>
                                            <c:otherwise><span class="iv-badge iv-badge-gray">Đã đóng</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><a class="iv-btn iv-btn-sm" href="${pageContext.request.contextPath}/screening?jobPostId=${j.jobPostId}">Sơ lọc</a></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </c:when>

    <%-- ================= JD chosen: filter + list ================= --%>
    <c:otherwise>
        <div class="iv-card">
            <a href="${pageContext.request.contextPath}/screening" class="iv-btn iv-btn-sm iv-btn-ghost">← Chọn JD khác</a>
            <form method="get" action="${pageContext.request.contextPath}/screening" class="iv-form-row" style="margin-top:14px;margin-bottom:0;">
                <input type="hidden" name="jobPostId" value="${jobPostId}"/>
                <div class="iv-field">
                    <label>Kinh nghiệm tối thiểu (năm)</label>
                    <input type="number" name="minExp" min="0" value="${selectedMinExp}"/>
                </div>
                <div class="iv-field">
                    <label>Học vấn chứa từ khóa</label>
                    <input type="text" name="education" placeholder="Vd: Bách Khoa" value="${selectedEducation}"/>
                </div>
                <div class="iv-field">
                    <label>Lương hiện tại từ</label>
                    <input type="number" name="minSalary" min="0" step="0.01" value="${selectedMinSalary}"/>
                </div>
                <div class="iv-field">
                    <label>Đến</label>
                    <input type="number" name="maxSalary" min="0" step="0.01" value="${selectedMaxSalary}"/>
                </div>
                <div class="iv-field">
                    <label>Trạng thái</label>
                    <select name="status">
                        <option value="Pending" ${selectedStatus == 'Pending' ? 'selected' : ''}>Chưa xử lý</option>
                        <option value="Shortlisted" ${selectedStatus == 'Shortlisted' ? 'selected' : ''}>Đã Pass</option>
                        <option value="Rejected" ${selectedStatus == 'Rejected' ? 'selected' : ''}>Đã loại</option>
                        <option value="All" ${selectedStatus == 'All' ? 'selected' : ''}>Tất cả</option>
                    </select>
                </div>
                <div class="iv-field" style="justify-content:flex-end;">
                    <button type="submit" class="iv-btn">Lọc</button>
                </div>
            </form>
        </div>

        <div class="iv-card">
            <h2>Kết quả (${applies.size()} hồ sơ)</h2>
            <c:choose>
                <c:when test="${empty applies}">
                    <div class="iv-empty">Không có hồ sơ nào khớp bộ lọc.</div>
                </c:when>
                <c:otherwise>
                    <form method="post" action="${pageContext.request.contextPath}/screening" id="screenForm">
                        <input type="hidden" name="jobPostId" value="${jobPostId}"/>
                        <table class="iv-table">
                            <thead>
                                <tr>
                                    <th><input type="checkbox" id="checkAll"/></th>
                                    <th>Ứng viên</th>
                                    <th>Kinh nghiệm</th>
                                    <th>Học vấn</th>
                                    <th>Lương mong muốn</th>
                                    <th>CV</th>
                                    <th>Trạng thái</th>
                                    <th>Ngày nộp</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="a" items="${applies}">
                                    <tr>
                                        <td><input type="checkbox" name="applyIds" value="${a.applyId}" class="rowCheck"/></td>
                                        <td>
                                            <b>${a.candidateName}</b><br>
                                            <span class="iv-small iv-muted">${a.candidateEmail} · ${a.candidatePhone}</span>
                                        </td>
                                        <td>${a.cvNumberExp} năm</td>
                                        <td class="iv-small">${a.cvEducation}<br><span class="iv-muted">${a.cvField}</span></td>
                                        <td>${a.cvCurrentSalary}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty a.cvFileData}">
                                                    <a href="${pageContext.request.contextPath}/uploads/cv/${a.cvFileData}" target="_blank" class="iv-btn iv-btn-sm iv-btn-ghost">Xem file</a>
                                                </c:when>
                                                <c:otherwise><span class="iv-muted iv-small">Không có file</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${a.status == 'Shortlisted'}"><span class="iv-badge iv-badge-green">Đã Pass</span></c:when>
                                                <c:when test="${a.status == 'Rejected'}"><span class="iv-badge iv-badge-red">Đã loại</span></c:when>
                                                <c:otherwise><span class="iv-badge iv-badge-gray">${a.status}</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="iv-small"><fmt:formatDate value="${a.dayCreate}" pattern="dd/MM/yyyy"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>

                        <div style="margin-top:14px;">
                            <button type="submit" name="decision" value="Shortlisted" class="iv-btn">✓ Pass các hồ sơ đã chọn</button>
                            <button type="submit" name="decision" value="Rejected" class="iv-btn iv-btn-danger">✕ Loại các hồ sơ đã chọn</button>
                        </div>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </c:otherwise>
</c:choose>

<script>
    var checkAll = document.getElementById('checkAll');
    if (checkAll) {
        checkAll.addEventListener('change', function () {
            document.querySelectorAll('.rowCheck').forEach(function (cb) { cb.checked = checkAll.checked; });
        });
    }
</script>

<jsp:include page="staff_footer.jsp"/>
