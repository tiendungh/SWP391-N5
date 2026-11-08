// DepartmentTopicModel.java
package model;

import java.util.Date;

public class DepartmentTopicModel {
    private int topicId;
    private int departmentId;
    private String topicTitle;
    private String description;
    private Date createdAt;
    private int createdBy;

    public DepartmentTopicModel() {}

    public int getTopicId() { return topicId; }
    public void setTopicId(int topicId) { this.topicId = topicId; }
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
}
