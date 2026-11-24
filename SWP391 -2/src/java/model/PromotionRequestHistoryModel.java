package model;

import java.sql.Timestamp;

public class PromotionRequestHistoryModel {

    private int promotionHistoryId;
    private int promotionRequestId;
    private String action;
    private String fromStatus;
    private String toStatus;
    private String comment;
    private int actionBy;
    private String actionByName;
    private Timestamp createdAt;

    public int getPromotionHistoryId() {
        return promotionHistoryId;
    }

    public void setPromotionHistoryId(int promotionHistoryId) {
        this.promotionHistoryId = promotionHistoryId;
    }

    public int getPromotionRequestId() {
        return promotionRequestId;
    }

    public void setPromotionRequestId(int promotionRequestId) {
        this.promotionRequestId = promotionRequestId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }

    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getActionBy() {
        return actionBy;
    }

    public void setActionBy(int actionBy) {
        this.actionBy = actionBy;
    }

    public String getActionByName() {
        return actionByName;
    }

    public void setActionByName(String actionByName) {
        this.actionByName = actionByName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
