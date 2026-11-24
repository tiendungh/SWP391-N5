package model;

import java.math.BigDecimal;

public class CriteriaModel {

    private int criteriaId;
    private int baremId;
    private String criteriaName;
    private String description;
    private BigDecimal maxScore;
    private BigDecimal weight;
    private int displayOrder;

    // Only used when rendering the scoring form together with an entered score
    private BigDecimal enteredScore;
    private String enteredComment;

    public int getCriteriaId() {
        return criteriaId;
    }

    public void setCriteriaId(int criteriaId) {
        this.criteriaId = criteriaId;
    }

    public int getBaremId() {
        return baremId;
    }

    public void setBaremId(int baremId) {
        this.baremId = baremId;
    }

    public String getCriteriaName() {
        return criteriaName;
    }

    public void setCriteriaName(String criteriaName) {
        this.criteriaName = criteriaName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public BigDecimal getEnteredScore() {
        return enteredScore;
    }

    public void setEnteredScore(BigDecimal enteredScore) {
        this.enteredScore = enteredScore;
    }

    public String getEnteredComment() {
        return enteredComment;
    }

    public void setEnteredComment(String enteredComment) {
        this.enteredComment = enteredComment;
    }
}
