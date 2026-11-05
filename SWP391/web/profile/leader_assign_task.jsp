<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="pageTitle" value="Phân công nhiệm vụ chi tiết" scope="request"/>
<jsp:include page="/interview/staff_header.jsp"/>

<style>
    .task-shell { max-width: 1100px; margin: 0 auto; padding: 20px; }
    .card-assign { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 24px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.06); }
    .grid-task-form { display: grid; grid-template-columns: 2fr 1.2fr 1fr 1.5fr auto; gap: 10px; align-items: flex-end; }
</style>

<div class="task-shell">
    <a href="${pageContext.request.contextPath}/leader-room?tab=topics<c:if test='${not empty leaderDept}'>&deptId=${leaderDept.departmentId}</c:if>" style="display:inline-flex; align-items:center; gap:6px; font-weight:600; color:#2563eb; text-decoration:none; margin-bottom:16px;">
        ‹ Quay lại danh sách nhân sự
    </a>

    <%-- KHỐI THÔNG BÁO FLASH MESSAGE TRỰC QUAN --%>
    <c:if test="${param.msg == 'assigned'}">
        <div style="background: #dcfce7; border: 1.5px solid #86efac; color: #166534; padding: 12px 18px; border-radius: 8px; margin-bottom: 18px; font-weight: 600; display: flex; align-items: center; gap: 8px;">
            ✅ <span>Đã giao nội dung công việc mới thành công cho nhân sự <b>${targetEmp.fullName}</b>!</span>
        </div>
    </c:if>
    <c:if test="${param.msg == 'updated'}">
        <div style="background: #e0f2fe; border: 1.5px solid #7dd3fc; color: #0369a1; padding: 12px 18px; border-radius: 8px; margin-bottom: 18px; font-weight: 600; display: flex; align-items: center; gap: 8px;">
            ✅ <span>Cập nhật tiến độ & đánh giá công việc thành công!</span>
        </div>
    </c:if>

    <div class="card-assign">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 1px solid #f1f5f9; padding-bottom: 14px;">
            <div>
                <h2 style="margin: 0; color: #1e3a8a;">Bảng phân công nhiệm vụ: ${targetEmp.fullName}</h2>
                <span style="color: #64748b; font-size: 0.88rem;">Mã NV: <b>${targetEmp.employeeCode}</b> · Email: ${targetEmp.email}</span>
            </div>
            <span style="background: #dbeafe; color: #1e40af; padding: 6px 14px; border-radius: 16px; font-weight: bold; font-size: 0.85rem;">
                Phòng: ${leaderDept.departmentName}
            </span>
        </div>

        <!-- FORM TẠO NỘI DUNG MỚI (CHỈ HIỂN THỊ KHI CÓ QUYỀN SỬA canEdit) -->
        <c:if test="${canEdit}">
            <h4 style="margin: 0 0 10px 0; color: #334155;">Giao nội dung công việc mới</h4>
            <form method="post" action="${pageContext.request.contextPath}/leader-room">
                <input type="hidden" name="action" value="assignTask"/>
                <input type="hidden" name="employeeId" value="${targetEmp.employeeId}"/>
                <input type="hidden" name="deptId" value="${leaderDept.departmentId}"/>

                <div class="grid-task-form" style="margin-bottom: 24px; background: #f8fafc; padding: 16px; border-radius: 8px;">
                    <div>
                        <label style="font-size: 0.8rem; font-weight: bold;">Nội dung công việc *</label>
                        <input type="text" name="taskContent" placeholder="Nhập từng nội dung riêng..." required style="width: 100%; padding: 8px; border: 1.5px solid #cbd5e1; border-radius: 6px;"/>
                    </div>
                    <div>
                        <label style="font-size: 0.8rem; font-weight: bold;">Chủ đề liên quan</label>
                        <select name="topicId" required style="width: 100%; padding: 8px; border: 1.5px solid #cbd5e1; border-radius: 6px;">
                            <c:forEach var="top" items="${topics}">
                                <option value="${top.topicId}">${top.topicTitle}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label style="font-size: 0.8rem; font-weight: bold;">Thời hạn (Deadline) *</label>
                        <input type="date" name="deadline" required style="width: 100%; padding: 7px; border: 1.5px solid #cbd5e1; border-radius: 6px;"/>
                    </div>
                    <div>
                        <label style="font-size: 0.8rem; font-weight: bold;">Ghi chú trưởng phòng</label>
                        <input type="text" name="leaderNote" placeholder="Yêu cầu cụ thể..." style="width: 100%; padding: 8px; border: 1.5px solid #cbd5e1; border-radius: 6px;"/>
                    </div>
                    <div>
                        <input type="hidden" name="progress" value="0"/>
                        <button type="submit" class="iv-btn" style="background: #2563eb; color: #fff; padding: 9px 18px; border-radius: 6px; font-weight: bold; border: none; cursor: pointer;">
                            LƯU VIỆC
                        </button>
                    </div>
                </div>
            </form>
        </c:if>

        <!-- LIST TỪNG NỘI DUNG ĐÃ GIAO KÈM Ô ĐÁNH GIÁ % VÀ Ô TÍCH HOÀN THÀNH -->
        <h4 style="margin: 0 0 10px 0; color: #334155;">Danh sách công việc đang thực hiện</h4>
        <table class="iv-table" style="width: 100%;">
            <thead>
                <tr>
                    <th style="width: 30%;">Nội dung công việc</th>
                    <th style="width: 15%;">Deadline</th>
                    <th style="width: 12%;">Tiến độ (%)</th>
                    <th style="width: 25%;">Ghi chú</th>
                    <th style="width: 10%; text-align: center;">Hoàn thành</th>
                    <th style="width: 8%;">Cập nhật</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty taskList}">
                        <tr><td colspan="6" style="text-align: center; color: #64748b; padding: 20px;">Chưa có đầu việc nào được giao cho nhân sự này.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="t" items="${taskList}">
                            <c:if test="${canEdit}">
                                <%-- Form độc lập liên kết qua thuộc tính form của HTML5 khi có quyền chỉnh sửa --%>
                                <form id="form_task_${t.taskId}" method="post" action="${pageContext.request.contextPath}/leader-room">
                                    <input type="hidden" name="action" value="updateTask"/>
                                    <input type="hidden" name="taskId" value="${t.taskId}"/>
                                    <input type="hidden" name="employeeId" value="${targetEmp.employeeId}"/>
                                    <input type="hidden" name="deptId" value="${leaderDept.departmentId}"/>
                                </form>
                            </c:if>

                            <tr>
                                <td><b>${t.taskContent}</b></td>
                                <td><span style="color: #dc2626; font-weight: 600;">${t.deadline}</span></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${canEdit}">
                                            <div style="display: flex; align-items: center; gap: 4px;">
                                                <input form="form_task_${t.taskId}" type="number" name="progress" min="0" max="100" value="${t.progress}" style="width: 60px; padding: 4px 6px; text-align: center; border: 1.5px solid #cbd5e1; border-radius: 6px; font-weight: bold;"/> %
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <b>${t.progress}%</b>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${canEdit}">
                                            <input form="form_task_${t.taskId}" type="text" name="leaderNote" value="${t.leaderNote}" placeholder="Đánh giá nhận xét..." style="width: 100%; padding: 4px 8px; border: 1.5px solid #cbd5e1; border-radius: 6px; font-size: 0.85rem;"/>
                                        </c:when>
                                        <c:otherwise>
                                            <span>${empty t.leaderNote ? '—' : t.leaderNote}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="text-align: center;">
                                    <c:choose>
                                        <c:when test="${canEdit}">
                                            <input form="form_task_${t.taskId}" type="checkbox" name="isCompleted" value="true" ${(t.completed || t.progress >= 100) ? 'checked' : ''} style="width: 20px; height: 20px; cursor: pointer; accent-color: #16a34a;"/>
                                        </c:when>
                                        <c:otherwise>
                                            <c:choose>
                                                <c:when test="${t.completed || t.progress >= 100}">
                                                    <span style="color: #16a34a; font-weight: bold;">✓ Đã xong</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: #ea580c; font-size: 0.85rem;">⏳ Đang làm</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${canEdit}">
                                            <button form="form_task_${t.taskId}" type="submit" style="background: #2563eb; color: #fff; border: none; padding: 6px 12px; border-radius: 6px; cursor: pointer; font-size: 0.8rem; font-weight: bold;">
                                                LƯU
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: #94a3b8; font-size: 0.8rem;">Chỉ xem</span>
                                        </c:otherwise>
                                    </c:choose>
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
