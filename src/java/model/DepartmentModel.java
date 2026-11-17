/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

public class DepartmentModel {
    private int departmentId;
    private String departmentName;
    private String description;
    private Integer managerId;
    private String managerName;
    private Timestamp createdAt;

    // THÊM MỚI: Số lượng nhân sự trực thuộc phòng ban để thống kê
    private int employeeCount;

    public DepartmentModel() {}

    public DepartmentModel(int departmentId, String departmentName, String description, Integer managerId) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.description = description;
        this.managerId = managerId;
    }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getManagerId() { return managerId; }
    public void setManagerId(Integer managerId) { this.managerId = managerId; }
    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // THÊM MỚI: Getter và Setter cho employeeCount
    public int getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(int employeeCount) { this.employeeCount = employeeCount; }
}