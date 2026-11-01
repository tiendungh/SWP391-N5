package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class EmployeeCareerModel {

    private int employeeId;
    private String employeeCode;
    private String fullName;
    private String email;
    private String roleName;
    private Date hireDate;
    private String department;
    private BigDecimal salary;
    private boolean active;
    private PositionModel currentPosition;
    private Date positionStartDate;
    private int monthsInPosition;
    private List<CareerPathModel> nextPaths = new ArrayList<>();
    private List<EmployeePositionHistoryModel> positionHistory = new ArrayList<>();

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

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public PositionModel getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(PositionModel currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Date getPositionStartDate() {
        return positionStartDate;
    }

    public void setPositionStartDate(Date positionStartDate) {
        this.positionStartDate = positionStartDate;
    }

    public int getMonthsInPosition() {
        return monthsInPosition;
    }

    public void setMonthsInPosition(int monthsInPosition) {
        this.monthsInPosition = monthsInPosition;
    }

    public List<CareerPathModel> getNextPaths() {
        return nextPaths;
    }

    public void setNextPaths(List<CareerPathModel> nextPaths) {
        this.nextPaths = nextPaths;
    }

    public List<EmployeePositionHistoryModel> getPositionHistory() {
        return positionHistory;
    }

    public void setPositionHistory(List<EmployeePositionHistoryModel> positionHistory) {
        this.positionHistory = positionHistory;
    }
}
