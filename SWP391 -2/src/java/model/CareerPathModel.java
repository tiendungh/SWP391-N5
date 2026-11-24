package model;

import java.sql.Timestamp;

public class CareerPathModel {

    private int careerPathId;
    private PositionModel fromPosition;
    private PositionModel toPosition;
    private int minMonthsInPosition;
    private String requirementNote;
    private boolean active;
    private int createdBy;
    private String createdByName;
    private Timestamp createdAt;
    private int employeeMonthsInPosition;

    public int getCareerPathId() {
        return careerPathId;
    }

    public void setCareerPathId(int careerPathId) {
        this.careerPathId = careerPathId;
    }

    public PositionModel getFromPosition() {
        return fromPosition;
    }

    public void setFromPosition(PositionModel fromPosition) {
        this.fromPosition = fromPosition;
    }

    public PositionModel getToPosition() {
        return toPosition;
    }

    public void setToPosition(PositionModel toPosition) {
        this.toPosition = toPosition;
    }

    public int getMinMonthsInPosition() {
        return minMonthsInPosition;
    }

    public void setMinMonthsInPosition(int minMonthsInPosition) {
        this.minMonthsInPosition = minMonthsInPosition;
    }

    public String getRequirementNote() {
        return requirementNote;
    }

    public void setRequirementNote(String requirementNote) {
        this.requirementNote = requirementNote;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public int getEmployeeMonthsInPosition() {
        return employeeMonthsInPosition;
    }

    public void setEmployeeMonthsInPosition(int employeeMonthsInPosition) {
        this.employeeMonthsInPosition = employeeMonthsInPosition;
    }

    public int getRemainingMonths() {
        return Math.max(0, minMonthsInPosition - employeeMonthsInPosition);
    }

    public boolean isEligibleByTenure() {
        return employeeMonthsInPosition >= minMonthsInPosition;
    }
}
