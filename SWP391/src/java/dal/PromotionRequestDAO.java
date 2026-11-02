package dal;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import model.EmployeeCareerModel;
import model.JobLevelModel;
import model.PositionModel;
import model.PromotionRequestHistoryModel;
import model.PromotionRequestModel;

public class PromotionRequestDAO extends DBContext {

    public static final int CREATE_DATABASE_ERROR = -1;
    public static final int CREATE_EMPLOYEE_INVALID = -2;
    public static final int CREATE_PATH_INVALID = -3;
    public static final int CREATE_TENURE_NOT_MET = -4;
    public static final int CREATE_PENDING_EXISTS = -5;
    public static final int CREATE_EFFECTIVE_DATE_INVALID = -6;
    public static final int CREATE_FORBIDDEN = -7;
    public static final int CREATE_SALARY_INVALID = -8;
    public static final int CREATE_REASON_INVALID = -9;

    public static final int ACTION_SUCCESS = 1;
    public static final int ACTION_DATABASE_ERROR = -1;
    public static final int ACTION_NOT_FOUND = -2;
    public static final int ACTION_NOT_PENDING = -3;
    public static final int ACTION_FORBIDDEN = -4;
    public static final int ACTION_SELF_REVIEW = -5;
    public static final int ACTION_EMPLOYEE_CHANGED = -6;
    public static final int ACTION_EFFECTIVE_DATE_INVALID = -7;
    public static final int ACTION_NOTE_REQUIRED = -8;
    public static final int ACTION_NOT_OWNER = -9;

    private static final String REQUEST_SELECT =
            "SELECT promotionRequest.PromotionRequestID, promotionRequest.EmployeeID, "
            + "employee.EmployeeCode, employeeCandidate.CandidateName AS EmployeeName, "
            + "promotionRequest.CurrentPositionID, currentPosition.PositionName AS CurrentPositionName, "
            + "currentPosition.Department AS CurrentDepartment, currentLevel.LevelName AS CurrentLevelName, "
            + "currentLevel.RankOrder AS CurrentRankOrder, "
            + "promotionRequest.ProposedPositionID, proposedPosition.PositionName AS ProposedPositionName, "
            + "proposedPosition.Department AS ProposedPositionDepartment, "
            + "proposedLevel.LevelName AS ProposedLevelName, proposedLevel.RankOrder AS ProposedRankOrder, "
            + "promotionRequest.CurrentSalary, promotionRequest.ProposedSalary, "
            + "promotionRequest.ProposedDepartment, promotionRequest.EffectiveDate, "
            + "promotionRequest.Reason, promotionRequest.Status, promotionRequest.ProposedBy, "
            + "proposerCandidate.CandidateName AS ProposedByName, promotionRequest.ProposedAt, "
            + "promotionRequest.ReviewedBy, reviewerCandidate.CandidateName AS ReviewedByName, "
            + "promotionRequest.ReviewedAt, promotionRequest.ReviewNote, "
            + "promotionRequest.CreatedAt, promotionRequest.UpdatedAt "
            + "FROM PromotionRequest promotionRequest "
            + "JOIN Employee employee ON promotionRequest.EmployeeID = employee.EmployeeID "
            + "JOIN Candidate employeeCandidate ON employee.CandidateID = employeeCandidate.CandidateID "
            + "JOIN [Position] currentPosition ON promotionRequest.CurrentPositionID = currentPosition.PositionID "
            + "JOIN JobLevel currentLevel ON currentPosition.JobLevelID = currentLevel.JobLevelID "
            + "JOIN [Position] proposedPosition ON promotionRequest.ProposedPositionID = proposedPosition.PositionID "
            + "JOIN JobLevel proposedLevel ON proposedPosition.JobLevelID = proposedLevel.JobLevelID "
            + "JOIN Employee proposer ON promotionRequest.ProposedBy = proposer.EmployeeID "
            + "JOIN Candidate proposerCandidate ON proposer.CandidateID = proposerCandidate.CandidateID "
            + "LEFT JOIN Employee reviewer ON promotionRequest.ReviewedBy = reviewer.EmployeeID "
            + "LEFT JOIN Candidate reviewerCandidate ON reviewer.CandidateID = reviewerCandidate.CandidateID ";

    public List<EmployeeCareerModel> getPromotionCandidates() {
        List<EmployeeCareerModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = "SELECT employee.EmployeeID, employee.EmployeeCode, candidate.CandidateName, "
                + "candidate.Email, role.RoleName, employee.HireDate, "
                + "employee.Department AS EmployeeDepartment, employee.Salary, employee.IsActive, "
                + "position.PositionID, position.PositionName, position.Department AS PositionDepartment, "
                + "position.Description AS PositionDescription, position.IsActive AS PositionIsActive, "
                + "position.CreatedAt AS PositionCreatedAt, level.JobLevelID, level.LevelName, "
                + "level.RankOrder, level.Description AS LevelDescription, "
                + "level.IsActive AS LevelIsActive, level.CreatedAt AS LevelCreatedAt, "
                + "COALESCE(currentHistory.StartDate, employee.HireDate) AS PositionStartDate "
                + "FROM Employee employee "
                + "JOIN Candidate candidate ON employee.CandidateID = candidate.CandidateID "
                + "JOIN Roles role ON employee.RoleID = role.RoleID "
                + "JOIN [Position] position ON employee.PositionID = position.PositionID "
                + "JOIN JobLevel level ON position.JobLevelID = level.JobLevelID "
                + "OUTER APPLY ("
                + "    SELECT TOP (1) history.StartDate "
                + "    FROM EmployeePositionHistory history "
                + "    WHERE history.EmployeeID = employee.EmployeeID AND history.EndDate IS NULL "
                + "    ORDER BY history.StartDate DESC, history.HistoryID DESC"
                + ") currentHistory "
                + "WHERE employee.IsActive = 1 AND position.IsActive = 1 AND level.IsActive = 1 "
                + "ORDER BY candidate.CandidateName";

        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapPromotionCandidate(rs));
        } catch (Exception e) {
            System.err.println("PromotionRequestDAO.getPromotionCandidates error: " + e.getMessage());
        }
        return list;
    }

    public List<PromotionRequestModel> getRequestsForStaff() {
        List<PromotionRequestModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = REQUEST_SELECT
                + "ORDER BY CASE WHEN promotionRequest.Status = 'Pending' THEN 0 ELSE 1 END, "
                + "promotionRequest.ProposedAt DESC, promotionRequest.PromotionRequestID DESC";

        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapPromotionRequest(rs));
        } catch (Exception e) {
            System.err.println("PromotionRequestDAO.getRequestsForStaff error: " + e.getMessage());
        }
        return list;
    }

    public List<PromotionRequestModel> getRequestsByEmployee(int employeeId) {
        List<PromotionRequestModel> list = new ArrayList<>();
        if (c == null) return list;
        String sql = REQUEST_SELECT + "WHERE promotionRequest.EmployeeID = ? "
                + "ORDER BY promotionRequest.ProposedAt DESC, promotionRequest.PromotionRequestID DESC";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPromotionRequest(rs));
            }
        } catch (Exception e) {
            System.err.println("PromotionRequestDAO.getRequestsByEmployee error: " + e.getMessage());
        }
        return list;
    }

    public PromotionRequestModel getRequestById(int requestId) {
        if (c == null) return null;
        String sql = REQUEST_SELECT + "WHERE promotionRequest.PromotionRequestID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapPromotionRequest(rs) : null;
            }
        } catch (Exception e) {
            System.err.println("PromotionRequestDAO.getRequestById error: " + e.getMessage());
            return null;
        }
    }

    public List<PromotionRequestHistoryModel> getRequestHistory(int requestId) {
        List<PromotionRequestHistoryModel> list = new ArrayList<>();
        if (c == null) return list;
        String sql = "SELECT history.PromotionHistoryID, history.PromotionRequestID, "
                + "history.Action, history.FromStatus, history.ToStatus, history.Comment, "
                + "history.ActionBy, candidate.CandidateName AS ActionByName, history.CreatedAt "
                + "FROM PromotionRequestHistory history "
                + "JOIN Employee employee ON history.ActionBy = employee.EmployeeID "
                + "JOIN Candidate candidate ON employee.CandidateID = candidate.CandidateID "
                + "WHERE history.PromotionRequestID = ? "
                + "ORDER BY history.CreatedAt DESC, history.PromotionHistoryID DESC";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PromotionRequestHistoryModel history = new PromotionRequestHistoryModel();
                    history.setPromotionHistoryId(rs.getInt("PromotionHistoryID"));
                    history.setPromotionRequestId(rs.getInt("PromotionRequestID"));
                    history.setAction(rs.getString("Action"));
                    history.setFromStatus(rs.getString("FromStatus"));
                    history.setToStatus(rs.getString("ToStatus"));
                    history.setComment(rs.getString("Comment"));
                    history.setActionBy(rs.getInt("ActionBy"));
                    history.setActionByName(rs.getString("ActionByName"));
                    history.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    list.add(history);
                }
            }
        } catch (Exception e) {
            System.err.println("PromotionRequestDAO.getRequestHistory error: " + e.getMessage());
        }
        return list;
    }

    public boolean hasPendingRequest(int employeeId) {
        if (c == null) return false;
        String sql = "SELECT TOP (1) PromotionRequestID FROM PromotionRequest "
                + "WHERE EmployeeID = ? AND Status = 'Pending'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.err.println("PromotionRequestDAO.hasPendingRequest error: " + e.getMessage());
            return false;
        }
    }

    public int createRequest(int employeeId, int proposedPositionId, BigDecimal proposedSalary,
            Date effectiveDate, String reason, int proposedBy) {
        if (c == null) return CREATE_DATABASE_ERROR;

        String cleanedReason = reason == null ? null : reason.trim();
        if (cleanedReason == null || cleanedReason.isEmpty() || cleanedReason.length() > 2000) {
            return CREATE_REASON_INVALID;
        }
        if (proposedSalary != null && proposedSalary.signum() < 0) return CREATE_SALARY_INVALID;
        if (effectiveDate == null) return CREATE_EFFECTIVE_DATE_INVALID;

        try {
            c.setAutoCommit(false);

            if (!isAuthorizedCreator(proposedBy)) {
                rollbackQuietly();
                return CREATE_FORBIDDEN;
            }

            if (lockPendingRequest(employeeId)) {
                rollbackQuietly();
                return CREATE_PENDING_EXISTS;
            }

            EmployeeSnapshot employee = lockEmployee(employeeId);
            if (employee == null || !employee.active || employee.currentPositionId == null) {
                rollbackQuietly();
                return CREATE_EMPLOYEE_INVALID;
            }

            LocalDate effectiveLocalDate = effectiveDate.toLocalDate();
            LocalDate today = LocalDate.now();
            if (effectiveLocalDate.isAfter(today)
                    || (employee.positionStartDate != null
                    && effectiveLocalDate.isBefore(employee.positionStartDate.toLocalDate()))) {
                rollbackQuietly();
                return CREATE_EFFECTIVE_DATE_INVALID;
            }

            CareerPathSnapshot path = getActiveCareerPath(employee.currentPositionId, proposedPositionId);
            if (path == null || path.proposedRankOrder <= path.currentRankOrder) {
                rollbackQuietly();
                return CREATE_PATH_INVALID;
            }

            int monthsInPosition = calculateCompleteMonths(employee.positionStartDate);
            if (monthsInPosition < path.minMonthsInPosition) {
                rollbackQuietly();
                return CREATE_TENURE_NOT_MET;
            }

            int requestId = insertRequest(employeeId, employee.currentPositionId,
                    proposedPositionId, employee.currentSalary, proposedSalary,
                    path.proposedDepartment, effectiveDate, cleanedReason, proposedBy);
            insertHistory(requestId, "Created", null, "Pending", cleanedReason, proposedBy);
            c.commit();
            return requestId;
        } catch (SQLException e) {
            rollbackQuietly();
            if (isDuplicateKey(e)) return CREATE_PENDING_EXISTS;
            System.err.println("PromotionRequestDAO.createRequest error: " + e.getMessage());
            return CREATE_DATABASE_ERROR;
        } finally {
            restoreAutoCommit();
        }
    }

    public int approveRequest(int requestId, int reviewedBy, String reviewNote) {
        String cleanedNote = cleanNote(reviewNote);
        if (cleanedNote != null && cleanedNote.length() > 2000) return ACTION_NOTE_REQUIRED;
        if (c == null) return ACTION_DATABASE_ERROR;

        try {
            c.setAutoCommit(false);
            if (!isAuthorizedCreator(reviewedBy)) {
                rollbackQuietly();
                return ACTION_FORBIDDEN;
            }

            PromotionRequestSnapshot request = lockRequest(requestId);
            int validation = validateReview(request, reviewedBy);
            if (validation != ACTION_SUCCESS) {
                rollbackQuietly();
                return validation;
            }

            EmployeeSnapshot employee = lockEmployeeForApproval(request.employeeId);
            if (employee == null || !employee.active || employee.currentPositionId == null
                    || employee.currentPositionId != request.currentPositionId) {
                rollbackQuietly();
                return ACTION_EMPLOYEE_CHANGED;
            }

            CurrentHistorySnapshot currentHistory = lockCurrentHistory(request.employeeId);
            LocalDate effectiveDate = request.effectiveDate.toLocalDate();
            LocalDate positionStartDate = currentHistory == null
                    ? employee.positionStartDate.toLocalDate()
                    : currentHistory.startDate.toLocalDate();
            if (effectiveDate.isAfter(LocalDate.now()) || effectiveDate.isBefore(positionStartDate)) {
                rollbackQuietly();
                return ACTION_EFFECTIVE_DATE_INVALID;
            }

            PositionSnapshot proposedPosition = getActivePosition(request.proposedPositionId);
            if (proposedPosition == null) {
                rollbackQuietly();
                return ACTION_EMPLOYEE_CHANGED;
            }

            if (currentHistory == null) {
                currentHistory = insertAndLockCurrentHistory(employee, request.employeeId);
            }
            closeCurrentHistory(currentHistory, request.effectiveDate);

            BigDecimal newSalary = request.proposedSalary == null
                    ? employee.currentSalary : request.proposedSalary;
            String newDepartment = request.proposedDepartment == null
                    ? proposedPosition.department : request.proposedDepartment;
            insertPromotedHistory(request, employee.roleId, proposedPosition.positionName,
                    newDepartment, newSalary);
            updateEmployeeAfterApproval(request.employeeId, proposedPosition,
                    newDepartment, newSalary);
            updateReviewedRequest(requestId, "Approved", reviewedBy, cleanedNote);
            insertHistory(requestId, "Approved", "Pending", "Approved", cleanedNote, reviewedBy);
            c.commit();
            return ACTION_SUCCESS;
        } catch (Exception e) {
            rollbackQuietly();
            System.err.println("PromotionRequestDAO.approveRequest error: " + e.getMessage());
            return ACTION_DATABASE_ERROR;
        } finally {
            restoreAutoCommit();
        }
    }

    public int rejectRequest(int requestId, int reviewedBy, String reviewNote) {
        String cleanedNote = cleanNote(reviewNote);
        if (cleanedNote == null || cleanedNote.length() > 2000) return ACTION_NOTE_REQUIRED;
        if (c == null) return ACTION_DATABASE_ERROR;

        try {
            c.setAutoCommit(false);
            if (!isAuthorizedCreator(reviewedBy)) {
                rollbackQuietly();
                return ACTION_FORBIDDEN;
            }

            PromotionRequestSnapshot request = lockRequest(requestId);
            int validation = validateReview(request, reviewedBy);
            if (validation != ACTION_SUCCESS) {
                rollbackQuietly();
                return validation;
            }

            updateReviewedRequest(requestId, "Rejected", reviewedBy, cleanedNote);
            insertHistory(requestId, "Rejected", "Pending", "Rejected", cleanedNote, reviewedBy);
            c.commit();
            return ACTION_SUCCESS;
        } catch (Exception e) {
            rollbackQuietly();
            System.err.println("PromotionRequestDAO.rejectRequest error: " + e.getMessage());
            return ACTION_DATABASE_ERROR;
        } finally {
            restoreAutoCommit();
        }
    }

    public int cancelRequest(int requestId, int actionBy, String comment) {
        String cleanedComment = cleanNote(comment);
        if (cleanedComment != null && cleanedComment.length() > 2000) return ACTION_NOTE_REQUIRED;
        if (c == null) return ACTION_DATABASE_ERROR;

        try {
            c.setAutoCommit(false);
            if (!isAuthorizedCreator(actionBy)) {
                rollbackQuietly();
                return ACTION_FORBIDDEN;
            }

            PromotionRequestSnapshot request = lockRequest(requestId);
            if (request == null) {
                rollbackQuietly();
                return ACTION_NOT_FOUND;
            }
            if (!"Pending".equals(request.status)) {
                rollbackQuietly();
                return ACTION_NOT_PENDING;
            }
            if (request.proposedBy != actionBy) {
                rollbackQuietly();
                return ACTION_NOT_OWNER;
            }

            String sql = "UPDATE PromotionRequest SET Status = 'Cancelled', UpdatedAt = SYSDATETIME() "
                    + "WHERE PromotionRequestID = ? AND Status = 'Pending'";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, requestId);
                if (ps.executeUpdate() != 1) throw new SQLException("Promotion request was not cancelled");
            }
            insertHistory(requestId, "Cancelled", "Pending", "Cancelled",
                    cleanedComment, actionBy);
            c.commit();
            return ACTION_SUCCESS;
        } catch (Exception e) {
            rollbackQuietly();
            System.err.println("PromotionRequestDAO.cancelRequest error: " + e.getMessage());
            return ACTION_DATABASE_ERROR;
        } finally {
            restoreAutoCommit();
        }
    }

    private boolean isAuthorizedCreator(int employeeId) throws SQLException {
        String sql = "SELECT 1 FROM Employee employee "
                + "JOIN Roles role ON employee.RoleID = role.RoleID "
                + "WHERE employee.EmployeeID = ? AND employee.IsActive = 1 "
                + "AND role.RoleName IN ('HR Staff', 'Manager')";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private PromotionRequestSnapshot lockRequest(int requestId) throws SQLException {
        String sql = "SELECT PromotionRequestID, EmployeeID, CurrentPositionID, ProposedPositionID, "
                + "ProposedSalary, ProposedDepartment, EffectiveDate, Status, ProposedBy "
                + "FROM PromotionRequest WITH (UPDLOCK, ROWLOCK) WHERE PromotionRequestID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                PromotionRequestSnapshot request = new PromotionRequestSnapshot();
                request.requestId = rs.getInt("PromotionRequestID");
                request.employeeId = rs.getInt("EmployeeID");
                request.currentPositionId = rs.getInt("CurrentPositionID");
                request.proposedPositionId = rs.getInt("ProposedPositionID");
                request.proposedSalary = rs.getBigDecimal("ProposedSalary");
                request.proposedDepartment = rs.getString("ProposedDepartment");
                request.effectiveDate = rs.getDate("EffectiveDate");
                request.status = rs.getString("Status");
                request.proposedBy = rs.getInt("ProposedBy");
                return request;
            }
        }
    }

    private int validateReview(PromotionRequestSnapshot request, int reviewedBy) {
        if (request == null) return ACTION_NOT_FOUND;
        if (!"Pending".equals(request.status)) return ACTION_NOT_PENDING;
        if (request.proposedBy == reviewedBy) return ACTION_SELF_REVIEW;
        return ACTION_SUCCESS;
    }

    private EmployeeSnapshot lockEmployeeForApproval(int employeeId) throws SQLException {
        String sql = "SELECT employee.RoleID, employee.PositionID, employee.Position, "
                + "employee.Department, employee.Salary, employee.HireDate, employee.IsActive, "
                + "COALESCE(currentHistory.StartDate, employee.HireDate) AS PositionStartDate "
                + "FROM Employee employee WITH (UPDLOCK, ROWLOCK) "
                + "OUTER APPLY ("
                + "    SELECT TOP (1) history.StartDate "
                + "    FROM EmployeePositionHistory history "
                + "    WHERE history.EmployeeID = employee.EmployeeID AND history.EndDate IS NULL "
                + "    ORDER BY history.StartDate DESC, history.HistoryID DESC"
                + ") currentHistory "
                + "WHERE employee.EmployeeID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                EmployeeSnapshot employee = new EmployeeSnapshot();
                employee.roleId = rs.getInt("RoleID");
                int positionId = rs.getInt("PositionID");
                employee.currentPositionId = rs.wasNull() ? null : positionId;
                employee.positionName = rs.getString("Position");
                employee.department = rs.getString("Department");
                employee.currentSalary = rs.getBigDecimal("Salary");
                employee.hireDate = rs.getDate("HireDate");
                employee.active = rs.getBoolean("IsActive");
                employee.positionStartDate = rs.getDate("PositionStartDate");
                return employee;
            }
        }
    }

    private CurrentHistorySnapshot lockCurrentHistory(int employeeId) throws SQLException {
        String sql = "SELECT TOP (1) HistoryID, StartDate "
                + "FROM EmployeePositionHistory WITH (UPDLOCK, ROWLOCK) "
                + "WHERE EmployeeID = ? AND EndDate IS NULL "
                + "ORDER BY StartDate DESC, HistoryID DESC";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                CurrentHistorySnapshot history = new CurrentHistorySnapshot();
                history.historyId = rs.getInt("HistoryID");
                history.startDate = rs.getDate("StartDate");
                return history;
            }
        }
    }

    private CurrentHistorySnapshot insertAndLockCurrentHistory(EmployeeSnapshot employee,
            int employeeId) throws SQLException {
        String sql = "INSERT INTO EmployeePositionHistory "
                + "(EmployeeID, RoleID, Position, Department, StartDate, EndDate, "
                + "Note, PositionID, Salary) VALUES (?, ?, ?, ?, ?, NULL, ?, ?, ?)";
        int historyId;
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, employee.roleId);
            ps.setString(3, employee.positionName);
            ps.setString(4, employee.department);
            ps.setDate(5, employee.positionStartDate == null ? employee.hireDate : employee.positionStartDate);
            ps.setString(6, "Bổ sung lịch sử vị trí trước khi áp dụng thăng chức");
            ps.setInt(7, employee.currentPositionId);
            if (employee.currentSalary == null) ps.setNull(8, Types.DECIMAL);
            else ps.setBigDecimal(8, employee.currentSalary);
            if (ps.executeUpdate() != 1) throw new SQLException("Current position history was not inserted");
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Cannot read generated HistoryID");
                historyId = keys.getInt(1);
            }
        }
        CurrentHistorySnapshot history = new CurrentHistorySnapshot();
        history.historyId = historyId;
        history.startDate = employee.positionStartDate == null
                ? employee.hireDate : employee.positionStartDate;
        return history;
    }

    private PositionSnapshot getActivePosition(int positionId) throws SQLException {
        String sql = "SELECT PositionID, PositionName, Department FROM [Position] "
                + "WHERE PositionID = ? AND IsActive = 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, positionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                PositionSnapshot position = new PositionSnapshot();
                position.positionId = rs.getInt("PositionID");
                position.positionName = rs.getString("PositionName");
                position.department = rs.getString("Department");
                return position;
            }
        }
    }

    private void closeCurrentHistory(CurrentHistorySnapshot history, Date effectiveDate) throws SQLException {
        LocalDate effective = effectiveDate.toLocalDate();
        LocalDate start = history.startDate.toLocalDate();
        // Same-day promotions keep a valid snapshot without violating the history date constraint.
        Date endDate = Date.valueOf(effective.isAfter(start) ? effective.minusDays(1) : effective);
        String sql = "UPDATE EmployeePositionHistory SET EndDate = ? "
                + "WHERE HistoryID = ? AND EndDate IS NULL";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, endDate);
            ps.setInt(2, history.historyId);
            if (ps.executeUpdate() != 1) throw new SQLException("Current position history was not closed");
        }
    }

    private void insertPromotedHistory(PromotionRequestSnapshot request, int roleId,
            String positionName, String department, BigDecimal salary) throws SQLException {
        String sql = "INSERT INTO EmployeePositionHistory "
                + "(EmployeeID, RoleID, Position, Department, StartDate, EndDate, Note, "
                + "PositionID, Salary, PromotionRequestID) "
                + "VALUES (?, ?, ?, ?, ?, NULL, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, request.employeeId);
            ps.setInt(2, roleId);
            ps.setString(3, positionName);
            ps.setString(4, department);
            ps.setDate(5, request.effectiveDate);
            ps.setString(6, "Thăng chức theo đề nghị #" + request.requestId);
            ps.setInt(7, request.proposedPositionId);
            if (salary == null) ps.setNull(8, Types.DECIMAL);
            else ps.setBigDecimal(8, salary);
            ps.setInt(9, request.requestId);
            if (ps.executeUpdate() != 1) throw new SQLException("Promoted position history was not inserted");
        }
    }

    private void updateEmployeeAfterApproval(int employeeId, PositionSnapshot position,
            String department, BigDecimal salary) throws SQLException {
        String sql = "UPDATE Employee SET PositionID = ?, Position = ?, Department = ?, Salary = ? "
                + "WHERE EmployeeID = ? AND IsActive = 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, position.positionId);
            ps.setString(2, position.positionName);
            ps.setString(3, department);
            if (salary == null) ps.setNull(4, Types.DECIMAL);
            else ps.setBigDecimal(4, salary);
            ps.setInt(5, employeeId);
            if (ps.executeUpdate() != 1) throw new SQLException("Employee position was not updated");
        }
    }

    private void updateReviewedRequest(int requestId, String status, int reviewedBy,
            String reviewNote) throws SQLException {
        String sql = "UPDATE PromotionRequest SET Status = ?, ReviewedBy = ?, "
                + "ReviewedAt = SYSDATETIME(), ReviewNote = ?, UpdatedAt = SYSDATETIME() "
                + "WHERE PromotionRequestID = ? AND Status = 'Pending'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reviewedBy);
            ps.setString(3, reviewNote);
            ps.setInt(4, requestId);
            if (ps.executeUpdate() != 1) throw new SQLException("Promotion request was not reviewed");
        }
    }

    private String cleanNote(String value) {
        if (value == null) return null;
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }

    private EmployeeSnapshot lockEmployee(int employeeId) throws SQLException {
        String sql = "SELECT employee.PositionID, employee.Salary, employee.IsActive, "
                + "COALESCE(currentHistory.StartDate, employee.HireDate) AS PositionStartDate "
                + "FROM Employee employee WITH (UPDLOCK, ROWLOCK) "
                + "OUTER APPLY ("
                + "    SELECT TOP (1) history.StartDate "
                + "    FROM EmployeePositionHistory history "
                + "    WHERE history.EmployeeID = employee.EmployeeID AND history.EndDate IS NULL "
                + "    ORDER BY history.StartDate DESC, history.HistoryID DESC"
                + ") currentHistory "
                + "WHERE employee.EmployeeID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                EmployeeSnapshot employee = new EmployeeSnapshot();
                int positionId = rs.getInt("PositionID");
                employee.currentPositionId = rs.wasNull() ? null : positionId;
                employee.currentSalary = rs.getBigDecimal("Salary");
                employee.active = rs.getBoolean("IsActive");
                employee.positionStartDate = rs.getDate("PositionStartDate");
                return employee;
            }
        }
    }

    private CareerPathSnapshot getActiveCareerPath(int currentPositionId, int proposedPositionId)
            throws SQLException {
        String sql = "SELECT path.MinMonthsInPosition, currentLevel.RankOrder AS CurrentRankOrder, "
                + "proposedLevel.RankOrder AS ProposedRankOrder, "
                + "proposedPosition.Department AS ProposedDepartment "
                + "FROM CareerPath path "
                + "JOIN [Position] currentPosition ON path.FromPositionID = currentPosition.PositionID "
                + "JOIN JobLevel currentLevel ON currentPosition.JobLevelID = currentLevel.JobLevelID "
                + "JOIN [Position] proposedPosition ON path.ToPositionID = proposedPosition.PositionID "
                + "JOIN JobLevel proposedLevel ON proposedPosition.JobLevelID = proposedLevel.JobLevelID "
                + "WHERE path.FromPositionID = ? AND path.ToPositionID = ? "
                + "AND path.IsActive = 1 AND currentPosition.IsActive = 1 "
                + "AND proposedPosition.IsActive = 1 AND currentLevel.IsActive = 1 "
                + "AND proposedLevel.IsActive = 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, currentPositionId);
            ps.setInt(2, proposedPositionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                CareerPathSnapshot path = new CareerPathSnapshot();
                path.minMonthsInPosition = rs.getInt("MinMonthsInPosition");
                path.currentRankOrder = rs.getInt("CurrentRankOrder");
                path.proposedRankOrder = rs.getInt("ProposedRankOrder");
                path.proposedDepartment = rs.getString("ProposedDepartment");
                return path;
            }
        }
    }

    private boolean lockPendingRequest(int employeeId) throws SQLException {
        String sql = "SELECT TOP (1) PromotionRequestID FROM PromotionRequest WITH (UPDLOCK, HOLDLOCK) "
                + "WHERE EmployeeID = ? AND Status = 'Pending'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int insertRequest(int employeeId, int currentPositionId, int proposedPositionId,
            BigDecimal currentSalary, BigDecimal proposedSalary, String proposedDepartment,
            Date effectiveDate, String reason, int proposedBy) throws SQLException {
        String sql = "INSERT INTO PromotionRequest "
                + "(EmployeeID, CurrentPositionID, ProposedPositionID, CurrentSalary, "
                + "ProposedSalary, ProposedDepartment, EffectiveDate, Reason, ProposedBy) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, currentPositionId);
            ps.setInt(3, proposedPositionId);
            if (currentSalary == null) ps.setNull(4, Types.DECIMAL);
            else ps.setBigDecimal(4, currentSalary);
            if (proposedSalary == null) ps.setNull(5, Types.DECIMAL);
            else ps.setBigDecimal(5, proposedSalary);
            ps.setString(6, proposedDepartment);
            ps.setDate(7, effectiveDate);
            ps.setString(8, reason);
            ps.setInt(9, proposedBy);
            if (ps.executeUpdate() != 1) throw new SQLException("Promotion request was not inserted");
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Cannot read generated PromotionRequestID");
                return keys.getInt(1);
            }
        }
    }

    private void insertHistory(int requestId, String action, String fromStatus,
            String toStatus, String comment, int actionBy) throws SQLException {
        String sql = "INSERT INTO PromotionRequestHistory "
                + "(PromotionRequestID, Action, FromStatus, ToStatus, Comment, ActionBy) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.setString(2, action);
            if (fromStatus == null) ps.setNull(3, Types.VARCHAR);
            else ps.setString(3, fromStatus);
            ps.setString(4, toStatus);
            ps.setString(5, comment);
            ps.setInt(6, actionBy);
            if (ps.executeUpdate() != 1) throw new SQLException("Promotion history was not inserted");
        }
    }

    private EmployeeCareerModel mapPromotionCandidate(ResultSet rs) throws SQLException {
        EmployeeCareerModel employee = new EmployeeCareerModel();
        employee.setEmployeeId(rs.getInt("EmployeeID"));
        employee.setEmployeeCode(rs.getString("EmployeeCode"));
        employee.setFullName(rs.getString("CandidateName"));
        employee.setEmail(rs.getString("Email"));
        employee.setRoleName(rs.getString("RoleName"));
        employee.setHireDate(rs.getDate("HireDate"));
        employee.setDepartment(rs.getString("EmployeeDepartment"));
        employee.setSalary(rs.getBigDecimal("Salary"));
        employee.setActive(rs.getBoolean("IsActive"));

        PositionModel position = new PositionModel();
        position.setPositionId(rs.getInt("PositionID"));
        position.setPositionName(rs.getString("PositionName"));
        position.setDepartment(rs.getString("PositionDepartment"));
        position.setDescription(rs.getString("PositionDescription"));
        position.setActive(rs.getBoolean("PositionIsActive"));
        position.setCreatedAt(rs.getTimestamp("PositionCreatedAt"));

        JobLevelModel level = new JobLevelModel();
        level.setJobLevelId(rs.getInt("JobLevelID"));
        level.setLevelName(rs.getString("LevelName"));
        level.setRankOrder(rs.getInt("RankOrder"));
        level.setDescription(rs.getString("LevelDescription"));
        level.setActive(rs.getBoolean("LevelIsActive"));
        level.setCreatedAt(rs.getTimestamp("LevelCreatedAt"));
        position.setJobLevel(level);
        employee.setCurrentPosition(position);

        Date positionStartDate = rs.getDate("PositionStartDate");
        employee.setPositionStartDate(positionStartDate);
        employee.setMonthsInPosition(calculateCompleteMonths(positionStartDate));
        return employee;
    }

    private PromotionRequestModel mapPromotionRequest(ResultSet rs) throws SQLException {
        PromotionRequestModel request = new PromotionRequestModel();
        request.setPromotionRequestId(rs.getInt("PromotionRequestID"));
        request.setEmployeeId(rs.getInt("EmployeeID"));
        request.setEmployeeCode(rs.getString("EmployeeCode"));
        request.setEmployeeName(rs.getString("EmployeeName"));
        request.setCurrentPosition(mapRequestPosition(rs, "Current"));
        request.setProposedPosition(mapRequestPosition(rs, "Proposed"));
        request.setCurrentSalary(rs.getBigDecimal("CurrentSalary"));
        request.setProposedSalary(rs.getBigDecimal("ProposedSalary"));
        request.setProposedDepartment(rs.getString("ProposedDepartment"));
        request.setEffectiveDate(rs.getDate("EffectiveDate"));
        request.setReason(rs.getString("Reason"));
        request.setStatus(rs.getString("Status"));
        request.setProposedBy(rs.getInt("ProposedBy"));
        request.setProposedByName(rs.getString("ProposedByName"));
        request.setProposedAt(rs.getTimestamp("ProposedAt"));
        int reviewedBy = rs.getInt("ReviewedBy");
        request.setReviewedBy(rs.wasNull() ? null : reviewedBy);
        request.setReviewedByName(rs.getString("ReviewedByName"));
        request.setReviewedAt(rs.getTimestamp("ReviewedAt"));
        request.setReviewNote(rs.getString("ReviewNote"));
        request.setCreatedAt(rs.getTimestamp("CreatedAt"));
        request.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
        return request;
    }

    private PositionModel mapRequestPosition(ResultSet rs, String prefix) throws SQLException {
        PositionModel position = new PositionModel();
        position.setPositionId(rs.getInt(prefix + "PositionID"));
        position.setPositionName(rs.getString(prefix + "PositionName"));
        position.setDepartment(rs.getString(prefix + "Department"));

        JobLevelModel level = new JobLevelModel();
        level.setLevelName(rs.getString(prefix + "LevelName"));
        level.setRankOrder(rs.getInt(prefix + "RankOrder"));
        position.setJobLevel(level);
        return position;
    }

    private int calculateCompleteMonths(Date startDate) {
        if (startDate == null) return 0;
        LocalDate start = startDate.toLocalDate();
        LocalDate today = LocalDate.now();
        if (start.isAfter(today)) return 0;
        long months = ChronoUnit.MONTHS.between(start, today);
        return months > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) months;
    }

    private boolean isDuplicateKey(SQLException e) {
        return e.getErrorCode() == 2601 || e.getErrorCode() == 2627;
    }

    private void rollbackQuietly() {
        try {
            c.rollback();
        } catch (Exception ignored) {
        }
    }

    private void restoreAutoCommit() {
        try {
            c.setAutoCommit(true);
        } catch (Exception ignored) {
        }
    }

    private static class EmployeeSnapshot {
        private int roleId;
        private Integer currentPositionId;
        private String positionName;
        private String department;
        private BigDecimal currentSalary;
        private boolean active;
        private Date hireDate;
        private Date positionStartDate;
    }

    private static class CareerPathSnapshot {
        private int minMonthsInPosition;
        private int currentRankOrder;
        private int proposedRankOrder;
        private String proposedDepartment;
    }

    private static class PromotionRequestSnapshot {
        private int requestId;
        private int employeeId;
        private int currentPositionId;
        private int proposedPositionId;
        private BigDecimal proposedSalary;
        private String proposedDepartment;
        private Date effectiveDate;
        private String status;
        private int proposedBy;
    }

    private static class CurrentHistorySnapshot {
        private int historyId;
        private Date startDate;
    }

    private static class PositionSnapshot {
        private int positionId;
        private String positionName;
        private String department;
    }
}
