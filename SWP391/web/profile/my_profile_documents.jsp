<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Hồ sơ của tôi" scope="request"/>
<c:set var="currentNav" value="myProfile" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<div class="iv-topbar">
    <div>
        <h1>Hồ sơ của tôi</h1>
        <p>Nộp các giấy tờ công ty yêu cầu và theo dõi kết quả phê duyệt.</p>
    </div>
    <div class="iv-whoami"><b><c:out value="${sessionScope.staffName}"/></b><br><c:out value="${sessionScope.roleName}"/></div>
</div>

<c:if test="${not empty error}"><div class="iv-alert iv-alert-error"><c:out value="${error}"/></div></c:if>
<c:if test="${param.submitted == '1'}"><div class="iv-alert iv-alert-success">Đã nộp hồ sơ. HR/Manager sẽ kiểm tra và phản hồi.</div></c:if>

<c:if test="${not empty profileSummary}">
    <div class="iv-card iv-profile-hero">
        <div>
            <h2><c:out value="${profileSummary.fullName}"/></h2>
            <p class="iv-muted"><c:out value="${profileSummary.email}"/> · <c:out value="${profileSummary.roleName}"/></p>
        </div>
        <div class="iv-profile-score">
            <strong>${profileSummary.completionPercentage}%</strong>
            <span>${profileSummary.approvedRequired}/${profileSummary.totalRequired} hồ sơ bắt buộc đã duyệt</span>
            <div class="iv-progress iv-progress-lg"><span style="width:${profileSummary.completionPercentage}%"></span></div>
        </div>
    </div>
</c:if>

<%-- THÊM MỚI: Khối hiển thị thông tin Phòng ban & Vị trí công tác của nhân viên --%>
<div class="iv-card">
    <div class="iv-section-head">
        <div>
            <h2>Thông tin công tác & Phòng ban</h2>
            <span class="iv-small iv-muted">Phòng ban và chức danh chính thức do cấp Quản lý phân công.</span>
        </div>
    </div>
    <div class="iv-table-wrap">
        <table class="iv-table">
            <thead>
                <tr>
                    <th>Mã nhân viên</th>
                    <th>Phòng ban</th>
                    <th>Chức danh / Vị trí</th>
                    <th>Ngày vào làm</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><b><c:out value="${not empty empCode ? empCode : '—'}"/></b></td>
                    <td>
                        <span class="iv-badge iv-badge-blue">
                            <c:out value="${not empty empDepartment ? empDepartment : 'Chưa phân công'}"/>
                        </span>
                    </td>
                    <td><b><c:out value="${not empty empPosition ? empPosition : 'Chưa thiết lập'}"/></b></td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty empHireDate}">
                                <fmt:formatDate value="${empHireDate}" pattern="dd/MM/yyyy"/>
                            </c:when>
                            <c:otherwise>—</c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <%-- Bảng lịch sử điều chuyển vị trí & phòng ban --%>
    <c:if test="${not empty posHistoryList}">
        <div style="margin-top: 24px;">
            <div class="iv-section-head">
                <div>
                    <h3 style="font-size: 1.05rem;">Lịch sử thay đổi vị trí công tác</h3>
                </div>
            </div>
            <div class="iv-table-wrap">
                <table class="iv-table">
                    <thead>
                        <tr>
                            <th>Phòng ban</th>
                            <th>Vị trí</th>
                            <th>Thời gian bắt đầu</th>
                            <th>Thời gian kết thúc</th>
                            <th>Ghi chú điều chuyển</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="h" items="${posHistoryList}">
                            <tr>
                                <td><b><c:out value="${h.department}"/></b></td>
                                <td><c:out value="${h.position}"/></td>
                                <td><fmt:formatDate value="${h.startDate}" pattern="dd/MM/yyyy"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty h.endDate}">
                                            <fmt:formatDate value="${h.endDate}" pattern="dd/MM/yyyy"/>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="iv-badge iv-badge-green">Hiện tại</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td><c:out value="${not empty h.note ? h.note : '—'}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:if>
</div>
<%-- KẾT THÚC KHỐI BỔ SUNG --%>

<div class="iv-card">
    <h2>Danh sách cần hoàn thành (${documents.size()})</h2>
    <c:choose>
        <c:when test="${empty documents}">
            <div class="iv-empty">Công ty chưa yêu cầu bạn bổ sung hồ sơ nào.</div>
        </c:when>
        <c:otherwise>
            <div class="iv-document-list">
                <c:forEach var="doc" items="${documents}">
                    <article class="iv-document-card status-${doc.status}">
                        <div class="iv-document-head">
                            <div>
                                <h3><c:out value="${doc.documentName}"/></h3>
                                <c:if test="${doc.required}"><span class="iv-required">Bắt buộc</span></c:if>
                            </div>
                            <c:choose>
                                <c:when test="${doc.status == 'Pending'}"><span class="iv-badge iv-badge-gray">Chưa nộp</span></c:when>
                                <c:when test="${doc.status == 'Submitted'}"><span class="iv-badge iv-badge-blue">Đang chờ duyệt</span></c:when>
                                <c:when test="${doc.status == 'Approved'}"><span class="iv-badge iv-badge-green">Đã duyệt</span></c:when>
                                <c:otherwise><span class="iv-badge iv-badge-red">Cần bổ sung lại</span></c:otherwise>
                            </c:choose>
                        </div>

                        <c:if test="${not empty doc.documentDescription}"><p class="iv-muted"><c:out value="${doc.documentDescription}"/></p></c:if>
                        <div class="iv-document-meta">
                            <span><b>Hạn nộp:</b> <c:choose><c:when test="${not empty doc.dueDate}"><fmt:formatDate value="${doc.dueDate}" pattern="dd/MM/yyyy"/></c:when><c:otherwise>Không giới hạn</c:otherwise></c:choose></span>
                            <c:if test="${not empty doc.submittedAt}"><span><b>Đã nộp:</b> <fmt:formatDate value="${doc.submittedAt}" pattern="dd/MM/yyyy HH:mm"/></span></c:if>
                        </div>

                        <c:if test="${not empty doc.originalFileName}">
                            <a class="iv-file-link" target="_blank" href="${pageContext.request.contextPath}/profile-document-download?id=${doc.employeeDocumentId}">📎 Xem <c:out value="${doc.originalFileName}"/></a>
                        </c:if>

                        <c:if test="${doc.status == 'Rejected'}">
                            <div class="iv-rejection-note"><b>Lý do cần bổ sung:</b> <c:out value="${doc.reviewNote}"/></div>
                        </c:if>
                        <c:if test="${doc.status == 'Approved' && not empty doc.reviewNote}">
                            <div class="iv-approval-note"><b>Nhận xét:</b> <c:out value="${doc.reviewNote}"/></div>
                        </c:if>

                        <c:if test="${doc.status == 'Pending' || doc.status == 'Rejected'}">
                            <form method="post" enctype="multipart/form-data" action="${pageContext.request.contextPath}/my-profile-documents" class="iv-upload-form">
                                <input type="hidden" name="documentId" value="${doc.employeeDocumentId}"/>
                                <div class="iv-form-row">
                                    <div class="iv-field">
                                        <label>Chọn tài liệu PDF/JPG/PNG, tối đa 10 MB *</label>
                                        <input type="file" name="documentFile" accept=".pdf,.jpg,.jpeg,.png,application/pdf,image/jpeg,image/png" required/>
                                    </div>
                                    <div class="iv-field">
                                        <label>Ghi chú</label>
                                        <input type="text" name="employeeNote" maxlength="1000" placeholder="Thông tin thêm cho người duyệt"/>
                                    </div>
                                </div>
                                <button class="iv-btn" type="submit">${doc.status == 'Rejected' ? 'Nộp lại hồ sơ' : 'Nộp hồ sơ'}</button>
                            </form>
                        </c:if>
                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<c:if test="${not empty documentHistory}">
    <div class="iv-card">
        <h2>Lịch sử gần đây</h2>
        <div class="iv-timeline">
            <c:forEach var="history" items="${documentHistory}">
                <div class="iv-timeline-item">
                    <span class="iv-timeline-dot"></span>
                    <div>
                        <b><c:out value="${history.documentName}"/></b> ·
                        <c:choose>
                            <c:when test="${history.action == 'Assigned'}">Đã được giao</c:when>
                            <c:when test="${history.action == 'Submitted'}">Đã nộp</c:when>
                            <c:when test="${history.action == 'Resubmitted'}">Đã nộp lại</c:when>
                            <c:when test="${history.action == 'Approved'}">Đã được duyệt</c:when>
                            <c:otherwise>Cần bổ sung lại</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="iv-small iv-muted"><c:out value="${history.actionByName}"/> · <fmt:formatDate value="${history.createdAt}" pattern="dd/MM/yyyy HH:mm"/></div>
                    <c:if test="${not empty history.comment}"><div class="iv-small"><c:out value="${history.comment}"/></div></c:if>
                </div>
            </c:forEach>
        </div>
    </div>
</c:if>

<jsp:include page="/interview/staff_footer.jsp"/>