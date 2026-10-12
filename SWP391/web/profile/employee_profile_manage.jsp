<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Hồ sơ nhân viên" scope="request"/>
<c:set var="currentNav" value="employeeProfiles" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Hồ sơ nhân viên</h1>
        <p>Giao danh sách giấy tờ bắt buộc, theo dõi tiến độ và phê duyệt hồ sơ.</p>
    </div>
    <div class="iv-whoami">Xin chào, <b><c:out value="${sessionScope.staffName}"/></b></div>
</div>

<c:if test="${not empty error}"><div class="iv-alert iv-alert-error"><c:out value="${error}"/></div></c:if>
<c:if test="${param.typeCreated == '1'}"><div class="iv-alert iv-alert-success">Đã tạo loại hồ sơ mới.</div></c:if>
<c:if test="${param.assigned == '1'}"><div class="iv-alert iv-alert-success">Đã giao yêu cầu hồ sơ cho nhân viên.</div></c:if>
<c:if test="${param.reviewed == 'approved'}"><div class="iv-alert iv-alert-success">Hồ sơ đã được phê duyệt.</div></c:if>
<c:if test="${param.reviewed == 'rejected'}"><div class="iv-alert iv-alert-success">Đã trả hồ sơ để nhân viên bổ sung lại.</div></c:if>

<c:choose>
    <c:when test="${empty selectedEmployee}">
        <div class="iv-card">
            <div class="iv-section-head">
                <div>
                    <h2>Danh sách nhân viên (${employeeSummaries.size()})</h2>
                    <span class="iv-small iv-muted">Chọn một nhân viên để thiết lập và xử lý hồ sơ.</span>
                </div>
            </div>
            <c:choose>
                <c:when test="${empty employeeSummaries}">
                    <div class="iv-empty">Không có nhân viên đang hoạt động hoặc chưa chạy migration database.</div>
                </c:when>
                <c:otherwise>
                    <div class="iv-table-wrap">
                        <table class="iv-table">
                            <thead>
                                <tr><th>Nhân viên</th><th>Vai trò</th><th>Tiến độ bắt buộc</th><th>Chờ duyệt</th><th>Quá hạn</th><th></th></tr>
                            </thead>
                            <tbody>
                                <c:forEach var="employee" items="${employeeSummaries}">
                                    <tr>
                                        <td><b><c:out value="${employee.fullName}"/></b><br><span class="iv-small iv-muted"><c:out value="${employee.email}"/></span></td>
                                        <td><span class="iv-badge iv-badge-gray"><c:out value="${employee.roleName}"/></span></td>
                                        <td class="iv-progress-cell">
                                            <c:choose>
                                                <c:when test="${employee.totalRequired == 0}">
                                                    <span class="iv-muted">Chưa thiết lập</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="iv-progress"><span style="width:${employee.completionPercentage}%"></span></div>
                                                    <span class="iv-small">${employee.approvedRequired}/${employee.totalRequired} · ${employee.completionPercentage}%</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><c:if test="${employee.pendingReview > 0}"><span class="iv-badge iv-badge-blue">${employee.pendingReview}</span></c:if><c:if test="${employee.pendingReview == 0}">0</c:if></td>
                                        <td><c:if test="${employee.overdue > 0}"><span class="iv-badge iv-badge-red">${employee.overdue}</span></c:if><c:if test="${employee.overdue == 0}">0</c:if></td>
                                        <td><a class="iv-btn iv-btn-sm" href="${pageContext.request.contextPath}/employee-profiles?employeeId=${employee.employeeId}">Quản lý</a></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </c:when>

    <c:otherwise>
        <div class="iv-card iv-profile-hero">
            <div>
                <a class="iv-back-link" href="${pageContext.request.contextPath}/employee-profiles">← Danh sách nhân viên</a>
                <h2><c:out value="${selectedEmployee.fullName}"/></h2>
                <p class="iv-muted"><c:out value="${selectedEmployee.email}"/> · <c:out value="${selectedEmployee.roleName}"/></p>
            </div>
            <div class="iv-profile-score">
                <strong>${selectedEmployee.completionPercentage}%</strong>
                <span>${selectedEmployee.approvedRequired}/${selectedEmployee.totalRequired} hồ sơ bắt buộc đã duyệt</span>
                <div class="iv-progress iv-progress-lg"><span style="width:${selectedEmployee.completionPercentage}%"></span></div>
            </div>
        </div>

        <div class="iv-card">
            <h2>Giao yêu cầu hồ sơ</h2>
            <c:choose>
                <c:when test="${empty documentTypes}">
                    <div class="iv-empty">Hãy tạo ít nhất một loại hồ sơ ở phần cuối trang trước khi giao cho nhân viên.</div>
                </c:when>
                <c:otherwise>
                    <form method="post" action="${pageContext.request.contextPath}/employee-profiles">
                        <input type="hidden" name="action" value="assign"/>
                        <input type="hidden" name="employeeId" value="${selectedEmployee.employeeId}"/>
                        <div class="iv-form-row">
                            <div class="iv-field">
                                <label>Loại hồ sơ *</label>
                                <select name="documentTypeId" required>
                                    <option value="">-- Chọn loại hồ sơ --</option>
                                    <c:forEach var="type" items="${documentTypes}">
                                        <option value="${type.documentTypeId}"><c:out value="${type.documentName}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="iv-field">
                                <label>Hạn nộp</label>
                                <input type="date" name="dueDate"/>
                            </div>
                            <div class="iv-field iv-check-field">
                                <label><input type="checkbox" name="required" checked/> Hồ sơ bắt buộc</label>
                            </div>
                        </div>
                        <button class="iv-btn" type="submit">Giao yêu cầu</button>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="iv-card">
            <h2>Danh sách hồ sơ (${documents.size()})</h2>
            <c:choose>
                <c:when test="${empty documents}"><div class="iv-empty">Nhân viên chưa được giao hồ sơ nào.</div></c:when>
                <c:otherwise>
                    <div class="iv-table-wrap">
                        <table class="iv-table iv-document-table">
                            <thead><tr><th>Hồ sơ</th><th>Hạn nộp</th><th>Trạng thái</th><th>Tệp đã nộp</th><th>Xử lý</th></tr></thead>
                            <tbody>
                                <c:forEach var="doc" items="${documents}">
                                    <tr>
                                        <td>
                                            <b><c:out value="${doc.documentName}"/></b>
                                            <c:if test="${doc.required}"><span class="iv-required">Bắt buộc</span></c:if>
                                            <br><span class="iv-small iv-muted"><c:out value="${doc.documentDescription}"/></span>
                                        </td>
                                        <td><c:choose><c:when test="${not empty doc.dueDate}"><fmt:formatDate value="${doc.dueDate}" pattern="dd/MM/yyyy"/></c:when><c:otherwise>—</c:otherwise></c:choose></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${doc.status == 'Pending'}"><span class="iv-badge iv-badge-gray">Chưa nộp</span></c:when>
                                                <c:when test="${doc.status == 'Submitted'}"><span class="iv-badge iv-badge-blue">Chờ duyệt</span></c:when>
                                                <c:when test="${doc.status == 'Approved'}"><span class="iv-badge iv-badge-green">Đã duyệt</span></c:when>
                                                <c:otherwise><span class="iv-badge iv-badge-red">Cần bổ sung</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty doc.originalFileName}">
                                                    <a class="iv-file-link" target="_blank" href="${pageContext.request.contextPath}/profile-document-download?id=${doc.employeeDocumentId}">📎 <c:out value="${doc.originalFileName}"/></a>
                                                    <c:if test="${not empty doc.employeeNote}"><div class="iv-small iv-muted"><c:out value="${doc.employeeNote}"/></div></c:if>
                                                </c:when>
                                                <c:otherwise><span class="iv-muted">Chưa có tệp</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="iv-review-cell">
                                            <c:choose>
                                                <c:when test="${doc.status == 'Submitted'}">
                                                    <form method="post" action="${pageContext.request.contextPath}/employee-profiles" class="iv-review-form">
                                                        <input type="hidden" name="action" value="review"/>
                                                        <input type="hidden" name="employeeId" value="${selectedEmployee.employeeId}"/>
                                                        <input type="hidden" name="documentId" value="${doc.employeeDocumentId}"/>
                                                        <textarea name="reviewNote" maxlength="1000" placeholder="Nhận xét hoặc lý do từ chối"></textarea>
                                                        <div class="iv-action-row">
                                                            <button class="iv-btn iv-btn-sm" type="submit" name="decision" value="Approved">Duyệt</button>
                                                            <button class="iv-btn iv-btn-sm iv-btn-danger" type="submit" name="decision" value="Rejected">Từ chối</button>
                                                        </div>
                                                    </form>
                                                </c:when>
                                                <c:when test="${not empty doc.reviewNote}">
                                                    <span class="iv-small"><b>Nhận xét:</b> <c:out value="${doc.reviewNote}"/></span>
                                                </c:when>
                                                <c:otherwise><span class="iv-muted">—</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="iv-card">
            <h2>Lịch sử xử lý</h2>
            <c:choose>
                <c:when test="${empty documentHistory}"><div class="iv-empty">Chưa có lịch sử xử lý.</div></c:when>
                <c:otherwise>
                    <div class="iv-timeline">
                        <c:forEach var="history" items="${documentHistory}">
                            <div class="iv-timeline-item">
                                <span class="iv-timeline-dot"></span>
                                <div>
                                    <b><c:out value="${history.documentName}"/></b> ·
                                    <c:choose>
                                        <c:when test="${history.action == 'Assigned'}">Đã giao</c:when>
                                        <c:when test="${history.action == 'Submitted'}">Đã nộp</c:when>
                                        <c:when test="${history.action == 'Resubmitted'}">Đã nộp lại</c:when>
                                        <c:when test="${history.action == 'Approved'}">Đã duyệt</c:when>
                                        <c:otherwise>Đã từ chối</c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="iv-small iv-muted"><c:out value="${history.actionByName}"/> · <fmt:formatDate value="${history.createdAt}" pattern="dd/MM/yyyy HH:mm"/></div>
                                <c:if test="${not empty history.comment}"><div class="iv-small"><c:out value="${history.comment}"/></div></c:if>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </c:otherwise>
</c:choose>

<div class="iv-card">
    <h2>Tạo loại hồ sơ</h2>
    <p class="iv-small iv-muted">Danh mục dùng chung cho toàn công ty, ví dụ CCCD, bằng cấp hoặc giấy khám sức khỏe.</p>
    <form method="post" action="${pageContext.request.contextPath}/employee-profiles">
        <input type="hidden" name="action" value="createType"/>
        <c:if test="${not empty selectedEmployee}"><input type="hidden" name="employeeId" value="${selectedEmployee.employeeId}"/></c:if>
        <div class="iv-form-row">
            <div class="iv-field">
                <label>Tên loại hồ sơ *</label>
                <input type="text" name="documentName" maxlength="150" required placeholder="Ví dụ: Căn cước công dân"/>
            </div>
            <div class="iv-field">
                <label>Mô tả</label>
                <input type="text" name="description" maxlength="500" placeholder="Yêu cầu hoặc lưu ý cho nhân viên"/>
            </div>
        </div>
        <button class="iv-btn" type="submit">Thêm loại hồ sơ</button>
    </form>
</div>

<jsp:include page="/interview/staff_footer.jsp"/>