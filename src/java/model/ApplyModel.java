package model;

import java.sql.Timestamp;

public class ApplyModel {

    private int applyId;
    private int jobPostId;
    private String jobTitle;
    private int candidateId;
    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;
    private int cvId;
    private String cvPosition;
    private Integer cvNumberExp;
    private String cvEducation;
    private String cvField;
    private java.math.BigDecimal cvCurrentSalary;
    private java.sql.Date cvBirthday;
    private String cvNationality;
    private String cvGender;
    private String cvFileData;
    private String status;
    private String note;
    private String finalResult;
    private String conclusionNote;
    private Integer conclusionBy;
    private String conclusionByName;
    private Timestamp conclusionDate;
    private Timestamp dayCreate;
    private int scheduledInterviewCount;

    public int getApplyId() {
        return applyId;
    }

    public void setApplyId(int applyId) {
        this.applyId = applyId;
    }

    public int getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(int jobPostId) {
        this.jobPostId = jobPostId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateEmail() {
        return candidateEmail;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }

    public String getCandidatePhone() {
        return candidatePhone;
    }

    public void setCandidatePhone(String candidatePhone) {
        this.candidatePhone = candidatePhone;
    }

    public int getCvId() {
        return cvId;
    }

    public void setCvId(int cvId) {
        this.cvId = cvId;
    }

    public String getCvPosition() {
        return cvPosition;
    }

    public void setCvPosition(String cvPosition) {
        this.cvPosition = cvPosition;
    }

    public Integer getCvNumberExp() {
        return cvNumberExp;
    }

    public void setCvNumberExp(Integer cvNumberExp) {
        this.cvNumberExp = cvNumberExp;
    }

    public String getCvEducation() {
        return cvEducation;
    }

    public void setCvEducation(String cvEducation) {
        this.cvEducation = cvEducation;
    }

    public String getCvField() {
        return cvField;
    }

    public void setCvField(String cvField) {
        this.cvField = cvField;
    }

    public java.math.BigDecimal getCvCurrentSalary() {
        return cvCurrentSalary;
    }

    public void setCvCurrentSalary(java.math.BigDecimal cvCurrentSalary) {
        this.cvCurrentSalary = cvCurrentSalary;
    }

    public java.sql.Date getCvBirthday() {
        return cvBirthday;
    }

    public void setCvBirthday(java.sql.Date cvBirthday) {
        this.cvBirthday = cvBirthday;
    }

    public String getCvNationality() {
        return cvNationality;
    }

    public void setCvNationality(String cvNationality) {
        this.cvNationality = cvNationality;
    }

    public String getCvGender() {
        return cvGender;
    }

    public void setCvGender(String cvGender) {
        this.cvGender = cvGender;
    }

    public String getCvFileData() {
        return cvFileData;
    }

    public void setCvFileData(String cvFileData) {
        this.cvFileData = cvFileData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getFinalResult() {
        return finalResult;
    }

    public void setFinalResult(String finalResult) {
        this.finalResult = finalResult;
    }

    public String getConclusionNote() {
        return conclusionNote;
    }

    public void setConclusionNote(String conclusionNote) {
        this.conclusionNote = conclusionNote;
    }

    public Integer getConclusionBy() {
        return conclusionBy;
    }

    public void setConclusionBy(Integer conclusionBy) {
        this.conclusionBy = conclusionBy;
    }

    public String getConclusionByName() {
        return conclusionByName;
    }

    public void setConclusionByName(String conclusionByName) {
        this.conclusionByName = conclusionByName;
    }

    public Timestamp getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(Timestamp conclusionDate) {
        this.conclusionDate = conclusionDate;
    }

    public Timestamp getDayCreate() {
        return dayCreate;
    }

    public void setDayCreate(Timestamp dayCreate) {
        this.dayCreate = dayCreate;
    }

    public int getScheduledInterviewCount() {
        return scheduledInterviewCount;
    }

    public void setScheduledInterviewCount(int scheduledInterviewCount) {
        this.scheduledInterviewCount = scheduledInterviewCount;
    }
}
