package dal;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.EvaluationDetailModel;
import model.EvaluationModel;

public class EvaluationDAO extends DBContext {

    /** The evaluation a given interviewer already submitted for an interview, if any (for editing). */
    public EvaluationModel getEvaluation(int interviewId, int interviewerId) {
        if (c == null) return null;
        String sql = "SELECT EvaluationID, InterviewID, InterviewerID, TotalScore, Recommendation, Comment, EvaluatedAt "
                + "FROM InterviewEvaluation WHERE InterviewID = ? AND InterviewerID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, interviewId);
            ps.setInt(2, interviewerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EvaluationModel ev = mapRow(rs);
                    ev.setDetails(getDetails(ev.getEvaluationId()));
                    return ev;
                }
            }
        } catch (Exception e) {
            System.err.println("EvaluationDAO.getEvaluation error: " + e.getMessage());
        }
        return null;
    }

    /** All evaluations submitted so far for an interview (used on the conclusion page). */
    public List<EvaluationModel> getEvaluationsByInterview(int interviewId) {
        List<EvaluationModel> list = new ArrayList<>();
        if (c == null) return list;
        String sql = "SELECT ie.EvaluationID, ie.InterviewID, ie.InterviewerID, c.CandidateName AS InterviewerName, "
                + "ie.TotalScore, ie.Recommendation, ie.Comment, ie.EvaluatedAt "
                + "FROM InterviewEvaluation ie "
                + "JOIN Employee e ON ie.InterviewerID = e.EmployeeID "
                + "JOIN Candidate c ON e.CandidateID = c.CandidateID "
                + "WHERE ie.InterviewID = ? ORDER BY ie.EvaluatedAt";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, interviewId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EvaluationModel ev = mapRow(rs);
                    ev.setInterviewerName(rs.getString("InterviewerName"));
                    list.add(ev);
                }
            }
        } catch (Exception e) {
            System.err.println("EvaluationDAO.getEvaluationsByInterview error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Inserts or updates (delete + re-insert) an interviewer's evaluation and its
     * per-criteria detail rows in one transaction. TotalScore is the sum of the
     * entered criteria scores. Returns true on success.
     */
    public boolean submitEvaluation(int interviewId, int interviewerId, String recommendation,
            String comment, List<EvaluationDetailModel> details) {
        if (c == null) return false;

        BigDecimal total = BigDecimal.ZERO;
        for (EvaluationDetailModel d : details) {
            if (d.getScore() != null) total = total.add(d.getScore());
        }

        try {
            c.setAutoCommit(false);

            Integer existingId = null;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT EvaluationID FROM InterviewEvaluation WHERE InterviewID = ? AND InterviewerID = ?")) {
                ps.setInt(1, interviewId);
                ps.setInt(2, interviewerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) existingId = rs.getInt(1);
                }
            }

            int evaluationId;
            if (existingId != null) {
                evaluationId = existingId;
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE InterviewEvaluation SET TotalScore = ?, Recommendation = ?, Comment = ?, "
                        + "EvaluatedAt = SYSDATETIME() WHERE EvaluationID = ?")) {
                    ps.setBigDecimal(1, total);
                    ps.setString(2, recommendation);
                    ps.setString(3, comment);
                    ps.setInt(4, evaluationId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "DELETE FROM EvaluationDetail WHERE EvaluationID = ?")) {
                    ps.setInt(1, evaluationId);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO InterviewEvaluation (InterviewID, InterviewerID, TotalScore, Recommendation, Comment) "
                        + "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, interviewId);
                    ps.setInt(2, interviewerId);
                    ps.setBigDecimal(3, total);
                    ps.setString(4, recommendation);
                    ps.setString(5, comment);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new Exception("Không lấy được EvaluationID vừa tạo.");
                        evaluationId = keys.getInt(1);
                    }
                }
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO EvaluationDetail (EvaluationID, CriteriaID, Score, Comment) VALUES (?, ?, ?, ?)")) {
                for (EvaluationDetailModel d : details) {
                    ps.setInt(1, evaluationId);
                    ps.setInt(2, d.getCriteriaId());
                    ps.setBigDecimal(3, d.getScore() == null ? BigDecimal.ZERO : d.getScore());
                    ps.setString(4, d.getComment());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            c.commit();
            return true;
        } catch (Exception e) {
            System.err.println("EvaluationDAO.submitEvaluation error: " + e.getMessage());
            try { c.rollback(); } catch (Exception ignored) {}
            return false;
        } finally {
            try { c.setAutoCommit(true); } catch (Exception ignored) {}
        }
    }

    private List<EvaluationDetailModel> getDetails(int evaluationId) {
        List<EvaluationDetailModel> list = new ArrayList<>();
        String sql = "SELECT ed.EvaluationDetailID, ed.EvaluationID, ed.CriteriaID, bc.CriteriaName, "
                + "bc.MaxScore, bc.Weight, ed.Score, ed.Comment "
                + "FROM EvaluationDetail ed JOIN BaremCriteria bc ON ed.CriteriaID = bc.CriteriaID "
                + "WHERE ed.EvaluationID = ? ORDER BY bc.DisplayOrder";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, evaluationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EvaluationDetailModel d = new EvaluationDetailModel();
                    d.setEvaluationDetailId(rs.getInt("EvaluationDetailID"));
                    d.setEvaluationId(rs.getInt("EvaluationID"));
                    d.setCriteriaId(rs.getInt("CriteriaID"));
                    d.setCriteriaName(rs.getString("CriteriaName"));
                    d.setMaxScore(rs.getBigDecimal("MaxScore"));
                    d.setWeight(rs.getBigDecimal("Weight"));
                    d.setScore(rs.getBigDecimal("Score"));
                    d.setComment(rs.getString("Comment"));
                    list.add(d);
                }
            }
        } catch (Exception e) {
            System.err.println("EvaluationDAO.getDetails error: " + e.getMessage());
        }
        return list;
    }

    private EvaluationModel mapRow(ResultSet rs) throws Exception {
        EvaluationModel ev = new EvaluationModel();
        ev.setEvaluationId(rs.getInt("EvaluationID"));
        ev.setInterviewId(rs.getInt("InterviewID"));
        ev.setInterviewerId(rs.getInt("InterviewerID"));
        ev.setTotalScore(rs.getBigDecimal("TotalScore"));
        ev.setRecommendation(rs.getString("Recommendation"));
        ev.setComment(rs.getString("Comment"));
        ev.setEvaluatedAt(rs.getTimestamp("EvaluatedAt"));
        return ev;
    }
}
