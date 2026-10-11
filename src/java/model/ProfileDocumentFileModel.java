package model;

public class ProfileDocumentFileModel {

    private int employeeDocumentId;
    private int employeeId;
    private String originalFileName;
    private String contentType;
    private byte[] fileData;

    public int getEmployeeDocumentId() {
        return employeeDocumentId;
    }

    public void setEmployeeDocumentId(int employeeDocumentId) {
        this.employeeDocumentId = employeeDocumentId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
}
