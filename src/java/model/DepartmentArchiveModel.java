package model;

import java.util.Date;
import java.util.List;

public class DepartmentArchiveModel {
    private int archiveId;
    private int departmentId;
    private String projectName;
    private Date archiveDate;
    private int totalTasks;
    private int completedTasks;
    private int avgPercent;
    private Integer createdBy;
    private List<DepartmentTaskModel> taskList;

    public DepartmentArchiveModel() {}

    public int getArchiveId() { return archiveId; }
    public void setArchiveId(int archiveId) { this.archiveId = archiveId; }
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public Date getArchiveDate() { return archiveDate; }
    public void setArchiveDate(Date archiveDate) { this.archiveDate = archiveDate; }
    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }
    public int getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }
    public int getAvgPercent() { return avgPercent; }
    public void setAvgPercent(int avgPercent) { this.avgPercent = avgPercent; }
    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    public List<DepartmentTaskModel> getTaskList() { return taskList; }
    public void setTaskList(List<DepartmentTaskModel> taskList) { this.taskList = taskList; }
}