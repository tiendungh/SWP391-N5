package model;

public class EmployeeProfileSummaryModel {

    private int employeeId;
    private String fullName;
    private String email;
    private String roleName;
    private int totalRequired;
    private int approvedRequired;
    private int pendingReview;
    private int overdue;

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getTotalRequired() {
        return totalRequired;
    }

    public void setTotalRequired(int totalRequired) {
        this.totalRequired = totalRequired;
    }

    public int getApprovedRequired() {
        return approvedRequired;
    }

    public void setApprovedRequired(int approvedRequired) {
        this.approvedRequired = approvedRequired;
    }

    public int getPendingReview() {
        return pendingReview;
    }

    public void setPendingReview(int pendingReview) {
        this.pendingReview = pendingReview;
    }

    public int getOverdue() {
        return overdue;
    }

    public void setOverdue(int overdue) {
        this.overdue = overdue;
    }

    public int getCompletionPercentage() {
        if (totalRequired == 0) return 0;
        return (int) Math.round(approvedRequired * 100.0 / totalRequired);
    }

    public boolean isComplete() {
        return totalRequired > 0 && approvedRequired == totalRequired;
    }
}
