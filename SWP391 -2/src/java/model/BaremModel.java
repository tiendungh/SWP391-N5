package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BaremModel {

    private int baremId;
    private int jobPostId;
    private String jobTitle;
    private String baremName;
    private String description;
    private BigDecimal totalScore;
    private int createdBy;
    private String createdByName;
    private Timestamp createdAt;
    private boolean active;
    private List<CriteriaModel> criteriaList = new ArrayList<>();

    public int getBaremId() {
        return baremId;
    }

    public void setBaremId(int baremId) {
        this.baremId = baremId;
    }

    public int getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(int jobPostId) {
        this.jobPostId = jobPostId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getBaremName() {
        return baremName;
    }

    public void setBaremName(String baremName) {
        this.baremName = baremName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<CriteriaModel> getCriteriaList() {
        return criteriaList;
    }

    public void setCriteriaList(List<CriteriaModel> criteriaList) {
        this.criteriaList = criteriaList;
    }
}
