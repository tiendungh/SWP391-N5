//// DepartmentTaskModel.java
//package model;
//
//import java.util.Date;
//
//public class DepartmentTaskModel {
//    private int taskId;
//    private int topicId;
//    private int employeeId;
//    private String taskContent;
//    private Date deadline;
//    private int progress;
//    private String leaderNote;
//    private boolean completed;
//    private String employeeName;
//    private String employeeCode;
//
//    public DepartmentTaskModel() {}
//
//    public int getTaskId() { return taskId; }
//    public void setTaskId(int taskId) { this.taskId = taskId; }
//    public int getTopicId() { return topicId; }
//    public void setTopicId(int topicId) { this.topicId = topicId; }
//    public int getEmployeeId() { return employeeId; }
//    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
//    public String getTaskContent() { return taskContent; }
//    public void setTaskContent(String taskContent) { this.taskContent = taskContent; }
//    public Date getDeadline() { return deadline; }
//    public void setDeadline(Date deadline) { this.deadline = deadline; }
//    public int getProgress() { return progress; }
//    public void setProgress(int progress) { this.progress = progress; }
//    public String getLeaderNote() { return leaderNote; }
//    public void setLeaderNote(String leaderNote) { this.leaderNote = leaderNote; }
//    public boolean isCompleted() { return completed; }
//    public void setCompleted(boolean completed) { this.completed = completed; }
//    public String getEmployeeName() { return employeeName; }
//    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
//    public String getEmployeeCode() { return employeeCode; }
//    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
//}


// DepartmentTaskModel.java
package model;

import java.util.Date;

public class DepartmentTaskModel {
    private int taskId;
    private int topicId;
    private int employeeId;
    private String taskContent;
    private Date deadline;
    private int progress;
    private String leaderNote;
    private boolean completed;
    private String employeeName;
    private String employeeCode;

    // THÊM MỚI: Tên chủ đề để hiển thị ở các bảng theo dõi tiến độ / hồ sơ
    private String topicTitle;

    public DepartmentTaskModel() {}

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    public int getTopicId() { return topicId; }
    public void setTopicId(int topicId) { this.topicId = topicId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getTaskContent() { return taskContent; }
    public void setTaskContent(String taskContent) { this.taskContent = taskContent; }
    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }

    // THÊM MỚI: Nạp chồng setDeadline hỗ trợ java.sql.Date để tránh lỗi ép kiểu trong JDBC
    public void setDeadline(java.sql.Date sqlDeadline) {
        if (sqlDeadline != null) {
            this.deadline = new Date(sqlDeadline.getTime());
        } else {
            this.deadline = null;
        }
    }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public String getLeaderNote() { return leaderNote; }
    public void setLeaderNote(String leaderNote) { this.leaderNote = leaderNote; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    // THÊM MỚI: Getter getCompleted() hỗ trợ tương thích EL trong một số container JSP
    public boolean getCompleted() { return completed; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    // THÊM MỚI: Getter & Setter cho topicTitle
    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }
}