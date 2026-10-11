package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class JobPostModel {

    private int jobPostId;
    private String title;
    private String description;
    private String category;
    private String position;
    private String location;
    private BigDecimal offerMin;
    private BigDecimal offerMax;
    private Integer numberExp;
    private boolean visible;
    private String typeJob;
    private Date deadline;
    private Timestamp dayCreate;
    private Integer createdBy;
    private String createdByName;
    private int applyCount;

    public int getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(int jobPostId) {
        this.jobPostId = jobPostId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getOfferMin() {
        return offerMin;
    }

    public void setOfferMin(BigDecimal offerMin) {
        this.offerMin = offerMin;
    }

    public BigDecimal getOfferMax() {
        return offerMax;
    }

    public void setOfferMax(BigDecimal offerMax) {
        this.offerMax = offerMax;
    }

    public Integer getNumberExp() {
        return numberExp;
    }

    public void setNumberExp(Integer numberExp) {
        this.numberExp = numberExp;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getTypeJob() {
        return typeJob;
    }

    public void setTypeJob(String typeJob) {
        this.typeJob = typeJob;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Timestamp getDayCreate() {
        return dayCreate;
    }

    public void setDayCreate(Timestamp dayCreate) {
        this.dayCreate = dayCreate;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public int getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(int applyCount) {
        this.applyCount = applyCount;
    }
}
