package dal;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import model.CareerPathModel;
import model.EmployeeCareerModel;
import model.EmployeePositionHistoryModel;
import model.JobLevelModel;
import model.PositionModel;

public class CareerPathDAO extends DBContext {

    public EmployeeCareerModel getEmployeeCareer(int employeeId) {
        if (c == null) return null;

        String sql = "SELECT e.EmployeeID, e.EmployeeCode, candidate.CandidateName, candidate.Email, "
                + "role.RoleName, e.HireDate, e.Department AS EmployeeDepartment, "
                + "e.Position AS LegacyPositionName, e.Salary, e.IsActive, "
                + "position.PositionID, position.PositionName, position.Department AS Department, "
                + "position.Description AS PositionDescription, position.IsActive AS PositionIsActive, "
                + "position.CreatedAt AS PositionCreatedAt, "
                + "level.JobLevelID, level.LevelName, level.RankOrder, "
                + "level.Description AS LevelDescription, level.IsActive AS LevelIsActive, "
                + "level.CreatedAt AS LevelCreatedAt, "
                + "COALESCE(currentHistory.StartDate, e.HireDate) AS PositionStartDate "
                + "FROM Employee e "
                + "JOIN Candidate candidate ON e.CandidateID = candidate.CandidateID "
                + "JOIN Roles role ON e.RoleID = role.RoleID "
                + "LEFT JOIN [Position] position ON e.PositionID = position.PositionID "
                + "LEFT JOIN JobLevel level ON position.JobLevelID = level.JobLevelID "
                + "OUTER APPLY ("
                + "    SELECT TOP (1) history.StartDate "
                + "    FROM EmployeePositionHistory history "
                + "    WHERE history.EmployeeID = e.EmployeeID AND history.EndDate IS NULL "
                + "    ORDER BY history.StartDate DESC, history.HistoryID DESC"
                + ") currentHistory "
                + "WHERE e.EmployeeID = ?";

        EmployeeCareerModel career = null;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) career = mapEmployeeCareer(rs);
            }
        } catch (Exception e) {
            System.err.println("CareerPathDAO.getEmployeeCareer error: " + e.getMessage());
            return null;
        }

        if (career == null) return null;
        PositionModel currentPosition = career.getCurrentPosition();
        if (currentPosition != null && currentPosition.getPositionId() > 0) {
            career.setNextPaths(getNextCareerPaths(
                    currentPosition.getPositionId(), career.getMonthsInPosition()));
        }
        career.setPositionHistory(getPositionHistory(employeeId));
        return career;
    }

    public List<CareerPathModel> getNextCareerPaths(int fromPositionId, int employeeMonthsInPosition) {
        List<CareerPathModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = "SELECT path.CareerPathID, path.MinMonthsInPosition, path.RequirementNote, "
                + "path.IsActive AS PathIsActive, path.CreatedBy, path.CreatedAt AS PathCreatedAt, "
                + "creator.CandidateName AS CreatedByName, "
                + "fromPosition.PositionID AS FromPositionID, "
                + "fromPosition.PositionName AS FromPositionName, "
                + "fromPosition.Department AS FromDepartment, "
                + "fromPosition.Description AS FromPositionDescription, "
                + "fromPosition.IsActive AS FromPositionIsActive, "
                + "fromPosition.CreatedAt AS FromPositionCreatedAt, "
                + "fromLevel.JobLevelID AS FromJobLevelID, fromLevel.LevelName AS FromLevelName, "
                + "fromLevel.RankOrder AS FromRankOrder, fromLevel.Description AS FromLevelDescription, "
                + "fromLevel.IsActive AS FromLevelIsActive, fromLevel.CreatedAt AS FromLevelCreatedAt, "
                + "toPosition.PositionID AS ToPositionID, toPosition.PositionName AS ToPositionName, "
                + "toPosition.Department AS ToDepartment, "
                + "toPosition.Description AS ToPositionDescription, "
                + "toPosition.IsActive AS ToPositionIsActive, "
                + "toPosition.CreatedAt AS ToPositionCreatedAt, "
                + "toLevel.JobLevelID AS ToJobLevelID, toLevel.LevelName AS ToLevelName, "
                + "toLevel.RankOrder AS ToRankOrder, toLevel.Description AS ToLevelDescription, "
                + "toLevel.IsActive AS ToLevelIsActive, toLevel.CreatedAt AS ToLevelCreatedAt "
                + "FROM CareerPath path "
                + "JOIN [Position] fromPosition ON path.FromPositionID = fromPosition.PositionID "
                + "JOIN JobLevel fromLevel ON fromPosition.JobLevelID = fromLevel.JobLevelID "
                + "JOIN [Position] toPosition ON path.ToPositionID = toPosition.PositionID "
                + "JOIN JobLevel toLevel ON toPosition.JobLevelID = toLevel.JobLevelID "
                + "JOIN Employee creatorEmployee ON path.CreatedBy = creatorEmployee.EmployeeID "
                + "JOIN Candidate creator ON creatorEmployee.CandidateID = creator.CandidateID "
                + "WHERE path.FromPositionID = ? AND path.IsActive = 1 "
                + "AND fromPosition.IsActive = 1 AND toPosition.IsActive = 1 "
                + "ORDER BY toLevel.RankOrder, toPosition.PositionName";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, fromPositionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CareerPathModel path = new CareerPathModel();
                    path.setCareerPathId(rs.getInt("CareerPathID"));
                    path.setFromPosition(mapPosition(rs, "From"));
                    path.setToPosition(mapPosition(rs, "To"));
                    path.setMinMonthsInPosition(rs.getInt("MinMonthsInPosition"));
                    path.setRequirementNote(rs.getString("RequirementNote"));
                    path.setActive(rs.getBoolean("PathIsActive"));
                    path.setCreatedBy(rs.getInt("CreatedBy"));
                    path.setCreatedByName(rs.getString("CreatedByName"));
                    path.setCreatedAt(rs.getTimestamp("PathCreatedAt"));
                    path.setEmployeeMonthsInPosition(employeeMonthsInPosition);
                    list.add(path);
                }
            }
        } catch (Exception e) {
            System.err.println("CareerPathDAO.getNextCareerPaths error: " + e.getMessage());
        }
        return list;
    }

    public List<EmployeePositionHistoryModel> getPositionHistory(int employeeId) {
        List<EmployeePositionHistoryModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = "SELECT history.HistoryID, history.EmployeeID, history.RoleID, role.RoleName, "
                + "history.PositionID, COALESCE(position.PositionName, history.Position) AS PositionName, "
                + "COALESCE(position.Department, history.Department) AS Department, history.Salary, "
                + "history.StartDate, history.EndDate, history.Note, history.PromotionRequestID, "
                + "history.CreatedAt "
                + "FROM EmployeePositionHistory history "
                + "JOIN Roles role ON history.RoleID = role.RoleID "
                + "LEFT JOIN [Position] position ON history.PositionID = position.PositionID "
                + "WHERE history.EmployeeID = ? "
                + "ORDER BY history.StartDate DESC, history.HistoryID DESC";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPositionHistory(rs));
            }
        } catch (Exception e) {
            System.err.println("CareerPathDAO.getPositionHistory error: " + e.getMessage());
        }
        return list;
    }

    public List<PositionModel> getActivePositions() {
        List<PositionModel> list = new ArrayList<>();
        if (c == null) return list;

        String sql = "SELECT position.PositionID, position.PositionName, position.Department, "
                + "position.Description AS PositionDescription, position.IsActive AS PositionIsActive, "
                + "position.CreatedAt AS PositionCreatedAt, "
                + "level.JobLevelID, level.LevelName, level.RankOrder, "
                + "level.Description AS LevelDescription, level.IsActive AS LevelIsActive, "
                + "level.CreatedAt AS LevelCreatedAt "
                + "FROM [Position] position "
                + "JOIN JobLevel level ON position.JobLevelID = level.JobLevelID "
                + "WHERE position.IsActive = 1 AND level.IsActive = 1 "
                + "ORDER BY position.Department, level.RankOrder, position.PositionName";

        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapPosition(rs, ""));
        } catch (Exception e) {
            System.err.println("CareerPathDAO.getActivePositions error: " + e.getMessage());
        }
        return list;
    }

    public PositionModel getPositionById(int positionId) {
        if (c == null) return null;

        String sql = "SELECT position.PositionID, position.PositionName, position.Department, "
                + "position.Description AS PositionDescription, position.IsActive AS PositionIsActive, "
                + "position.CreatedAt AS PositionCreatedAt, "
                + "level.JobLevelID, level.LevelName, level.RankOrder, "
                + "level.Description AS LevelDescription, level.IsActive AS LevelIsActive, "
                + "level.CreatedAt AS LevelCreatedAt "
                + "FROM [Position] position "
                + "JOIN JobLevel level ON position.JobLevelID = level.JobLevelID "
                + "WHERE position.PositionID = ?";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, positionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapPosition(rs, "") : null;
            }
        } catch (Exception e) {
            System.err.println("CareerPathDAO.getPositionById error: " + e.getMessage());
            return null;
        }
    }

    private EmployeeCareerModel mapEmployeeCareer(ResultSet rs) throws SQLException {
        EmployeeCareerModel career = new EmployeeCareerModel();
        career.setEmployeeId(rs.getInt("EmployeeID"));
        career.setEmployeeCode(rs.getString("EmployeeCode"));
        career.setFullName(rs.getString("CandidateName"));
        career.setEmail(rs.getString("Email"));
        career.setRoleName(rs.getString("RoleName"));
        career.setHireDate(rs.getDate("HireDate"));
        career.setDepartment(rs.getString("EmployeeDepartment"));
        career.setSalary(rs.getBigDecimal("Salary"));
        career.setActive(rs.getBoolean("IsActive"));

        int positionId = rs.getInt("PositionID");
        PositionModel position;
        if (rs.wasNull()) {
            position = new PositionModel();
            position.setPositionName(rs.getString("LegacyPositionName"));
            position.setDepartment(rs.getString("EmployeeDepartment"));
            position.setActive(true);
        } else {
            position = mapPosition(rs, "");
        }
        career.setCurrentPosition(position);

        Date startDate = rs.getDate("PositionStartDate");
        career.setPositionStartDate(startDate);
        career.setMonthsInPosition(calculateCompleteMonths(startDate));
        return career;
    }

    private PositionModel mapPosition(ResultSet rs, String prefix) throws SQLException {
        PositionModel position = new PositionModel();
        position.setPositionId(rs.getInt(prefix + "PositionID"));
        position.setPositionName(rs.getString(prefix + "PositionName"));
        position.setDepartment(rs.getString(prefix + "Department"));
        position.setDescription(rs.getString(prefix + "PositionDescription"));
        position.setActive(rs.getBoolean(prefix + "PositionIsActive"));
        position.setCreatedAt(rs.getTimestamp(prefix + "PositionCreatedAt"));

        JobLevelModel level = new JobLevelModel();
        level.setJobLevelId(rs.getInt(prefix + "JobLevelID"));
        level.setLevelName(rs.getString(prefix + "LevelName"));
        level.setRankOrder(rs.getInt(prefix + "RankOrder"));
        level.setDescription(rs.getString(prefix + "LevelDescription"));
        level.setActive(rs.getBoolean(prefix + "LevelIsActive"));
        level.setCreatedAt(rs.getTimestamp(prefix + "LevelCreatedAt"));
        position.setJobLevel(level);
        return position;
    }

    private EmployeePositionHistoryModel mapPositionHistory(ResultSet rs) throws SQLException {
        EmployeePositionHistoryModel history = new EmployeePositionHistoryModel();
        history.setHistoryId(rs.getInt("HistoryID"));
        history.setEmployeeId(rs.getInt("EmployeeID"));
        history.setRoleId(rs.getInt("RoleID"));
        history.setRoleName(rs.getString("RoleName"));
        int positionId = rs.getInt("PositionID");
        history.setPositionId(rs.wasNull() ? null : positionId);
        history.setPositionName(rs.getString("PositionName"));
        history.setDepartment(rs.getString("Department"));
        history.setSalary(rs.getBigDecimal("Salary"));
        history.setStartDate(rs.getDate("StartDate"));
        history.setEndDate(rs.getDate("EndDate"));
        history.setNote(rs.getString("Note"));
        int requestId = rs.getInt("PromotionRequestID");
        history.setPromotionRequestId(rs.wasNull() ? null : requestId);
        history.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return history;
    }

    private int calculateCompleteMonths(Date startDate) {
        if (startDate == null) return 0;
        LocalDate start = startDate.toLocalDate();
        LocalDate today = LocalDate.now();
        if (start.isAfter(today)) return 0;
        long months = ChronoUnit.MONTHS.between(start, today);
        return months > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) months;
    }
}
