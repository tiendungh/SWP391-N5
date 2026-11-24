package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * A Manager's request asking Admin to create an Employee account for a given
 * email. Admin reviews it, then either creates the account from it (which
 * marks the request "Approved" and links the resulting EmployeeID) or
 * rejects it with a note.
 */
public class AccountRequestModel {

    private int requestId;
    private String requestedEmail;
    private String fullName;
    private String phone;
    private String address;
    private String nationality;
    private String department;
    private String position;
    private Integer roleId;
    private String roleName;
    private BigDecimal salary;
    private String note;
    private int requestedBy;
    private String requestedByName;
    private String status; // Pending | Approved | Rejected
    private Timestamp createdAt;
    private Integer reviewedByAdminId;
    private String reviewedByAdminName;
    private Timestamp reviewedAt;
    private String rejectionNote;
    private Integer fulfilledEmployeeId;

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getRequestedEmail() {
        return requestedEmail;
    }

    public void setRequestedEmail(String requestedEmail) {
        this.requestedEmail = requestedEmail;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(int requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getRequestedByName() {
        return requestedByName;
    }

    public void setRequestedByName(String requestedByName) {
        this.requestedByName = requestedByName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getReviewedByAdminId() {
        return reviewedByAdminId;
    }

    public void setReviewedByAdminId(Integer reviewedByAdminId) {
        this.reviewedByAdminId = reviewedByAdminId;
    }

    public String getReviewedByAdminName() {
        return reviewedByAdminName;
    }

    public void setReviewedByAdminName(String reviewedByAdminName) {
        this.reviewedByAdminName = reviewedByAdminName;
    }

    public Timestamp getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Timestamp reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getRejectionNote() {
        return rejectionNote;
    }

    public void setRejectionNote(String rejectionNote) {
        this.rejectionNote = rejectionNote;
    }

    public Integer getFulfilledEmployeeId() {
        return fulfilledEmployeeId;
    }

    public void setFulfilledEmployeeId(Integer fulfilledEmployeeId) {
        this.fulfilledEmployeeId = fulfilledEmployeeId;
    }
}
