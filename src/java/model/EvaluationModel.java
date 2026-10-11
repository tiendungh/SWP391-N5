package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EvaluationModel {

    private int evaluationId;
    private int interviewId;
    private int interviewerId;
    private String interviewerName;
    private BigDecimal totalScore;
    private String recommendation;
    private String comment;
    private Timestamp evaluatedAt;
    private List<EvaluationDetailModel> details = new ArrayList<>();

    public int getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(int evaluationId) {
        this.evaluationId = evaluationId;
    }

    public int getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(int interviewId) {
        this.interviewId = interviewId;
    }

    public int getInterviewerId() {
        return interviewerId;
    }

    public void setInterviewerId(int interviewerId) {
        this.interviewerId = interviewerId;
    }

    public String getInterviewerName() {
        return interviewerName;
    }

    public void setInterviewerName(String interviewerName) {
        this.interviewerName = interviewerName;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(Timestamp evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public List<EvaluationDetailModel> getDetails() {
        return details;
    }

    public void setDetails(List<EvaluationDetailModel> details) {
        this.details = details;
    }
}
