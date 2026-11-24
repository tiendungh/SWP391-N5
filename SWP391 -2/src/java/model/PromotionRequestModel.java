package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class PromotionRequestModel {

    private int promotionRequestId;
    private int employeeId;
    private String employeeCode;
    private String employeeName;
    private PositionModel currentPosition;
    private PositionModel proposedPosition;
    private BigDecimal currentSalary;
    private BigDecimal proposedSalary;
    private String proposedDepartment;
    private Date effectiveDate;
    private String reason;
    private String status;
    private int proposedBy;
    private String proposedByName;
    private Timestamp proposedAt;
    private Integer reviewedBy;
    private String reviewedByName;
    private Timestamp reviewedAt;
    private String reviewNote;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public int getPromotionRequestId() {
        return promotionRequestId;
    }

    public void setPromotionRequestId(int promotionRequestId) {
        this.promotionRequestId = promotionRequestId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public PositionModel getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(PositionModel currentPosition) {
        this.currentPosition = currentPosition;
    }

    public PositionModel getProposedPosition() {
        return proposedPosition;
    }

    public void setProposedPosition(PositionModel proposedPosition) {
        this.proposedPosition = proposedPosition;
    }

    public BigDecimal getCurrentSalary() {
        return currentSalary;
    }

    public void setCurrentSalary(BigDecimal currentSalary) {
        this.currentSalary = currentSalary;
    }

    public BigDecimal getProposedSalary() {
        return proposedSalary;
    }

    public void setProposedSalary(BigDecimal proposedSalary) {
        this.proposedSalary = proposedSalary;
    }

    public String getProposedDepartment() {
        return proposedDepartment;
    }

    public void setProposedDepartment(String proposedDepartment) {
        this.proposedDepartment = proposedDepartment;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProposedBy() {
        return proposedBy;
    }

    public void setProposedBy(int proposedBy) {
        this.proposedBy = proposedBy;
    }

    public String getProposedByName() {
        return proposedByName;
    }

    public void setProposedByName(String proposedByName) {
        this.proposedByName = proposedByName;
    }

    public Timestamp getProposedAt() {
        return proposedAt;
    }

    public void setProposedAt(Timestamp proposedAt) {
        this.proposedAt = proposedAt;
    }

    public Integer getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Integer reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getReviewedByName() {
        return reviewedByName;
    }

    public void setReviewedByName(String reviewedByName) {
        this.reviewedByName = reviewedByName;
    }

    public Timestamp getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Timestamp reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReviewNote() {
        return reviewNote;
    }

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
