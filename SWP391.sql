/*
    SWP391 - Full database initialization and sample data

    Safe behavior:
    - Creates database SWP391 only when it does not exist.
    - Creates missing tables and indexes only.
    - Inserts sample rows only when their natural key does not exist.
    - Does not drop the database or delete existing business data.

    Test password for all seeded accounts: 123456
*/

USE master;
GO

IF DB_ID(N'SWP391') IS NULL
BEGIN
    CREATE DATABASE SWP391;
END;
GO

USE SWP391;
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;
GO

/* ============================================================
   CORE ACCOUNTS AND EMPLOYEES
   ============================================================ */

IF OBJECT_ID(N'dbo.Roles', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Roles (
        RoleID INT IDENTITY(1,1) PRIMARY KEY,
        RoleName NVARCHAR(50) NOT NULL,
        CONSTRAINT UQ_Roles_RoleName UNIQUE (RoleName)
    );
END;
GO

IF OBJECT_ID(N'dbo.Admin', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Admin (
        AdminID INT IDENTITY(1,1) PRIMARY KEY,
        Username NVARCHAR(100) NOT NULL,
        PasswordHash NVARCHAR(255) NOT NULL,
        CreatedAt DATETIME2(0) NOT NULL
            CONSTRAINT DF_Admin_CreatedAt DEFAULT (SYSDATETIME()),
        CONSTRAINT UQ_Admin_Username UNIQUE (Username)
    );
END;
GO

IF OBJECT_ID(N'dbo.Role_Table', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Role_Table (
        AdminID INT NOT NULL,
        RoleID INT NOT NULL,
        CONSTRAINT PK_Role_Table PRIMARY KEY (AdminID, RoleID),
        CONSTRAINT FK_RoleTable_Admin FOREIGN KEY (AdminID)
            REFERENCES dbo.Admin(AdminID) ON DELETE CASCADE,
        CONSTRAINT FK_RoleTable_Role FOREIGN KEY (RoleID)
            REFERENCES dbo.Roles(RoleID) ON DELETE CASCADE
    );
END;
GO

IF OBJECT_ID(N'dbo.Candidate', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Candidate (
        CandidateID INT IDENTITY(1,1) PRIMARY KEY,
        CandidateName NVARCHAR(100) NOT NULL,
        Address NVARCHAR(255) NULL,
        Email NVARCHAR(100) NOT NULL,
        PhoneNumber VARCHAR(15) NOT NULL,
        Nationality NVARCHAR(50) NULL,
        PasswordHash NVARCHAR(255) NOT NULL,
        Avatar NVARCHAR(255) NULL,
        CreatedAt DATETIME2(0) NOT NULL
            CONSTRAINT DF_Candidate_CreatedAt DEFAULT (SYSDATETIME()),
        IsActive BIT NOT NULL
            CONSTRAINT DF_Candidate_IsActive DEFAULT (1),
        CONSTRAINT UQ_Candidate_Email UNIQUE (Email),
        CONSTRAINT UQ_Candidate_Phone UNIQUE (PhoneNumber)
    );
END;
GO

IF OBJECT_ID(N'dbo.Employee', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Employee (
        EmployeeID INT IDENTITY(1,1) PRIMARY KEY,
        CandidateID INT NOT NULL,
        RoleID INT NOT NULL,
        EmployeeCode VARCHAR(20) NOT NULL,
        HireDate DATE NOT NULL,
        Department NVARCHAR(100) NULL,
        Position NVARCHAR(100) NULL,
        Salary DECIMAL(18,2) NULL,
        IsActive BIT NOT NULL
            CONSTRAINT DF_Employee_IsActive DEFAULT (1),
        CreatedAt DATETIME2(0) NOT NULL
            CONSTRAINT DF_Employee_CreatedAt DEFAULT (SYSDATETIME()),
        CONSTRAINT UQ_Employee_Candidate UNIQUE (CandidateID),
        CONSTRAINT UQ_Employee_Code UNIQUE (EmployeeCode),
        CONSTRAINT FK_Employee_Candidate FOREIGN KEY (CandidateID)
            REFERENCES dbo.Candidate(CandidateID),
        CONSTRAINT FK_Employee_Role FOREIGN KEY (RoleID)
            REFERENCES dbo.Roles(RoleID),
        CONSTRAINT CK_Employee_Salary CHECK (Salary IS NULL OR Salary >= 0)
    );
END;
GO

IF OBJECT_ID(N'dbo.Department', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Department (
        DepartmentID INT IDENTITY(1,1) PRIMARY KEY,
        DepartmentName NVARCHAR(100) NOT NULL,
        Description NVARCHAR(255) NULL,
        ManagerID INT NULL,
        CreatedAt DATETIME2(0) NOT NULL
            CONSTRAINT DF_Department_CreatedAt DEFAULT (SYSDATETIME()),
        CONSTRAINT UQ_Department_DepartmentName UNIQUE (DepartmentName),
        CONSTRAINT FK_Department_Manager FOREIGN KEY (ManagerID)
            REFERENCES dbo.Employee(EmployeeID) ON DELETE SET NULL
    );
END;
GO

IF OBJECT_ID(N'dbo.EmployeePositionHistory', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.EmployeePositionHistory (
        HistoryID INT IDENTITY(1,1) PRIMARY KEY,
        EmployeeID INT NOT NULL,
        RoleID INT NOT NULL,
        Position NVARCHAR(100) NULL,
        Department NVARCHAR(100) NULL,
        StartDate DATE NOT NULL,
        EndDate DATE NULL,
        Note NVARCHAR(500) NULL,
        CreatedAt DATETIME2(0) NOT NULL
            CONSTRAINT DF_EmployeePositionHistory_CreatedAt DEFAULT (SYSDATETIME()),
        CONSTRAINT FK_PositionHistory_Employee FOREIGN KEY (EmployeeID)
            REFERENCES dbo.Employee(EmployeeID) ON DELETE CASCADE,
        CONSTRAINT FK_PositionHistory_Role FOREIGN KEY (RoleID)
            REFERENCES dbo.Roles(RoleID),
        CONSTRAINT CK_PositionHistory_Dates CHECK (EndDate IS NULL OR EndDate >= StartDate)
    );
END;
GO

/* ============================================================
   RECRUITMENT
   ============================================================ */

IF OBJECT_ID(N'dbo.CV', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.CV (
        CVID INT IDENTITY(1,1) PRIMARY KEY,
        CandidateID INT NOT NULL,
        FullName NVARCHAR(100) NULL,
        Address NVARCHAR(255) NULL,
        Email NVARCHAR(100) NULL,
        Position NVARCHAR(100) NULL,
        NumberExp INT NULL,
        Education NVARCHAR(255) NULL,
        Field NVARCHAR(100) NULL,
        CurrentSalary DECIMAL(18,2) NULL,
        Birthday DATE NULL,
        Nationality NVARCHAR(50) NULL,
        Gender NVARCHAR(10) NULL,
        FileData NVARCHAR(255) NULL,
        DayCreate DATETIME2(0) NOT NULL
            CONSTRAINT DF_CV_DayCreate DEFAULT (SYSDATETIME()),
        CONSTRAINT CK_CV_NumberExp CHECK (NumberExp IS NULL OR NumberExp >= 0),
        CONSTRAINT CK_CV_CurrentSalary CHECK (CurrentSalary IS NULL OR CurrentSalary >= 0),
        CONSTRAINT FK_CV_Candidate FOREIGN KEY (CandidateID)
            REFERENCES dbo.Candidate(CandidateID) ON DELETE CASCADE
    );
END;
GO

IF OBJECT_ID(N'dbo.JobPost', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.JobPost (
        JobPostID INT IDENTITY(1,1) PRIMARY KEY,
        Title NVARCHAR(100) NOT NULL,
        Description NVARCHAR(MAX) NOT NULL,
        Category NVARCHAR(100) NOT NULL,
        Position NVARCHAR(100) NULL,
        Location NVARCHAR(255) NOT NULL,
        OfferMin DECIMAL(18,2) NULL,
        OfferMax DECIMAL(18,2) NULL,
        NumberExp INT NULL,
        Visible BIT NOT NULL
            CONSTRAINT DF_JobPost_Visible DEFAULT (1),
        TypeJob NVARCHAR(100) NULL,
        Deadline DATE NULL,
        DayCreate DATETIME2(0) NOT NULL
            CONSTRAINT DF_JobPost_DayCreate DEFAULT (SYSDATETIME()),
        CreatedBy INT NULL,
        CONSTRAINT CK_JobPost_Salary CHECK (
            OfferMin IS NULL OR OfferMax IS NULL OR OfferMin <= OfferMax
        ),
        CONSTRAINT CK_JobPost_NumberExp CHECK (NumberExp IS NULL OR NumberExp >= 0),
        CONSTRAINT FK_JobPost_CreatedBy FOREIGN KEY (CreatedBy)
            REFERENCES dbo.Employee(EmployeeID) ON DELETE SET NULL
    );
END;
GO

IF OBJECT_ID(N'dbo.Apply', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Apply (
        ApplyID INT IDENTITY(1,1) PRIMARY KEY,
        JobPostID INT NOT NULL,
        CandidateID INT NOT NULL,
        CVID INT NOT NULL,
        Status NVARCHAR(50) NOT NULL
            CONSTRAINT DF_Apply_Status DEFAULT (N'Pending'),
        Note NVARCHAR(500) NULL,
        FinalResult NVARCHAR(50) NULL,
        ConclusionNote NVARCHAR(MAX) NULL,
        ConclusionBy INT NULL,
        ConclusionDate DATETIME2(0) NULL,
        DayCreate DATETIME2(0) NOT NULL
            CONSTRAINT DF_Apply_DayCreate DEFAULT (SYSDATETIME()),
        CONSTRAINT UQ_Apply_Candidate_Job UNIQUE (CandidateID, JobPostID),
        CONSTRAINT FK_Apply_JobPost FOREIGN KEY (JobPostID)
            REFERENCES dbo.JobPost(JobPostID) ON DELETE CASCADE,
        CONSTRAINT FK_Apply_Candidate FOREIGN KEY (CandidateID)
            REFERENCES dbo.Candidate(CandidateID),
        CONSTRAINT FK_Apply_CV FOREIGN KEY (CVID)
            REFERENCES dbo.CV(CVID),
        CONSTRAINT FK_Apply_ConclusionBy FOREIGN KEY (ConclusionBy)
            REFERENCES dbo.Employee(EmployeeID) ON DELETE SET NULL
    );
END;
GO

IF OBJECT_ID(N'dbo.SavedJob', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.SavedJob (
        SavedJobID INT IDENTITY(1,1) PRIMARY KEY,
        CandidateID INT NOT NULL,
        JobPostID INT NOT NULL,
        DateCreate DATETIME2(0) NOT NULL
            CONSTRAINT DF_SavedJob_DateCreate DEFAULT (SYSDATETIME()),
        CONSTRAINT UQ_SavedJob_Candidate_Job UNIQUE (CandidateID, JobPostID),
        CONSTRAINT FK_SavedJob_Candidate FOREIGN KEY (CandidateID)
            REFERENCES dbo.Candidate(CandidateID) ON DELETE CASCADE,
        CONSTRAINT FK_SavedJob_JobPost FOREIGN KEY (JobPostID)
            REFERENCES dbo.JobPost(JobPostID) ON DELETE CASCADE
    );
END;
GO

IF OBJECT_ID(N'dbo.Potential', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Potential (
        PotentialID INT IDENTITY(1,1) PRIMARY KEY,
        CVID INT NOT NULL,
        JobPostID INT NOT NULL,
        CreatedBy INT NULL,
        Note NVARCHAR(500) NULL,
        CreatedAt DATETIME2(0) NOT NULL
            CONSTRAINT DF_Potential_CreatedAt DEFAULT (SYSDATETIME()),
        CONSTRAINT UQ_Potential_CV_Job UNIQUE (CVID, JobPostID),
        CONSTRAINT FK_Potential_CV FOREIGN KEY (CVID)
            REFERENCES dbo.CV(CVID) ON DELETE CASCADE,
        CONSTRAINT FK_Potential_JobPost FOREIGN KEY (JobPostID)
            REFERENCES dbo.JobPost(JobPostID) ON DELETE CASCADE,
        CONSTRAINT FK_Potential_CreatedBy FOREIGN KEY (CreatedBy)
            REFERENCES dbo.Employee(EmployeeID) ON DELETE SET NULL
    );
END;
GO

/* ============================================================
   INTERVIEWS AND EVALUATIONS
   ============================================================ */

IF OBJECT_ID(N'dbo.InterviewBarem', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.InterviewBarem (
        BaremID INT IDENTITY(1,1) PRIMARY KEY,
        JobPostID INT NOT NULL,
        BaremName NVARCHAR(150) NOT NULL,
        Description NVARCHAR(500) NULL,
        TotalScore DECIMAL(6,2) NOT NULL
            CONSTRAINT DF_InterviewBarem_TotalScore DEFAULT (100),
        CreatedBy INT NOT NULL,
        CreatedAt DATETIME2(0) NOT NULL
            CONSTRAINT DF_InterviewBarem_CreatedAt DEFAULT (SYSDATETIME()),
        IsActive BIT NOT NULL
            CONSTRAINT DF_InterviewBarem_IsActive DEFAULT (1),
        CONSTRAINT UQ_InterviewBarem_Job_Name UNIQUE (JobPostID, BaremName),
        CONSTRAINT CK_InterviewBarem_TotalScore CHECK (TotalScore > 0),
        CONSTRAINT FK_InterviewBarem_JobPost FOREIGN KEY (JobPostID)
            REFERENCES dbo.JobPost(JobPostID) ON DELETE CASCADE,
        CONSTRAINT FK_InterviewBarem_CreatedBy FOREIGN KEY (CreatedBy)
            REFERENCES dbo.Employee(EmployeeID)
    );
END;
GO

IF OBJECT_ID(N'dbo.BaremCriteria', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.BaremCriteria (
        CriteriaID INT IDENTITY(1,1) PRIMARY KEY,
        BaremID INT NOT NULL,
        CriteriaName NVARCHAR(150) NOT NULL,
        Description NVARCHAR(500) NULL,
        MaxScore DECIMAL(6,2) NOT NULL,
        Weight DECIMAL(5,2) NOT NULL,
        DisplayOrder INT NOT NULL
            CONSTRAINT DF_BaremCriteria_DisplayOrder DEFAULT (1),
        CONSTRAINT UQ_BaremCriteria_Barem_Name UNIQUE (BaremID, CriteriaName),
        CONSTRAINT CK_BaremCriteria_MaxScore CHECK (MaxScore > 0),
        CONSTRAINT CK_BaremCriteria_Weight CHECK (Weight >= 0 AND Weight <= 100),
        CONSTRAINT CK_BaremCriteria_DisplayOrder CHECK (DisplayOrder > 0),
        CONSTRAINT FK_BaremCriteria_Barem FOREIGN KEY (BaremID)
            REFERENCES dbo.InterviewBarem(BaremID) ON DELETE CASCADE
    );
END;
GO

IF OBJECT_ID(N'dbo.Interview', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Interview (
        InterviewID INT IDENTITY(1,1) PRIMARY KEY,
        ApplyID INT NOT NULL,
        BaremID INT NOT NULL,
        InterviewRound INT NOT NULL,
        InterviewDate DATE NOT NULL,
        StartTime TIME(0) NOT NULL,
        EndTime TIME(0) NULL,
        InterviewType NVARCHAR(30) NOT NULL,
        Location NVARCHAR(255) NULL,
        MeetingLink NVARCHAR(500) NULL,
        Status NVARCHAR(50) NOT NULL
            CONSTRAINT DF_Interview_Status DEFAULT (N'Scheduled'),
        Note NVARCHAR(1000) NULL,
        CreatedBy INT NOT NULL,
        CreatedAt DATETIME2(0) NOT NULL
            CONSTRAINT DF_Interview_CreatedAt DEFAULT (SYSDATETIME()),
        CONSTRAINT UQ_Interview_Apply_Round UNIQUE (ApplyID, InterviewRound),
        CONSTRAINT CK_Interview_Round CHECK (InterviewRound > 0),
        CONSTRAINT CK_Interview_Time CHECK (EndTime IS NULL OR EndTime > StartTime),
        CONSTRAINT CK_Interview_Type CHECK (InterviewType IN (N'Online', N'Offline', N'Phone')),
        CONSTRAINT FK_Interview_Apply FOREIGN KEY (ApplyID)
            REFERENCES dbo.Apply(ApplyID) ON DELETE CASCADE,
        CONSTRAINT FK_Interview_Barem FOREIGN KEY (BaremID)
            REFERENCES dbo.InterviewBarem(BaremID),
        CONSTRAINT FK_Interview_CreatedBy FOREIGN KEY (CreatedBy)
            REFERENCES dbo.Employee(EmployeeID)
    );
END;
GO

IF OBJECT_ID(N'dbo.InterviewParticipant', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.InterviewParticipant (
        InterviewID INT NOT NULL,
        EmployeeID INT NOT NULL,
        ParticipantRole NVARCHAR(50) NULL,
        IsLeadInterviewer BIT NOT NULL
            CONSTRAINT DF_InterviewParticipant_IsLead DEFAULT (0),
        CONSTRAINT PK_InterviewParticipant PRIMARY KEY (InterviewID, EmployeeID),
        CONSTRAINT FK_InterviewParticipant_Interview FOREIGN KEY (InterviewID)
            REFERENCES dbo.Interview(InterviewID) ON DELETE CASCADE,
        CONSTRAINT FK_InterviewParticipant_Employee FOREIGN KEY (EmployeeID)
            REFERENCES dbo.Employee(EmployeeID)
    );
END;
GO

IF OBJECT_ID(N'dbo.InterviewEvaluation', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.InterviewEvaluation (
        EvaluationID INT IDENTITY(1,1) PRIMARY KEY,
        InterviewID INT NOT NULL,
        InterviewerID INT NOT NULL,
        TotalScore DECIMAL(7,2) NULL,
        Recommendation NVARCHAR(50) NULL,
        Comment NVARCHAR(MAX) NULL,
        EvaluatedAt DATETIME2(0) NOT NULL
            CONSTRAINT DF_InterviewEvaluation_EvaluatedAt DEFAULT (SYSDATETIME()),
        CONSTRAINT UQ_InterviewEvaluation UNIQUE (InterviewID, InterviewerID),
        CONSTRAINT CK_InterviewEvaluation_Score CHECK (TotalScore IS NULL OR TotalScore >= 0),
        CONSTRAINT CK_InterviewEvaluation_Recommendation CHECK (
            Recommendation IS NULL OR Recommendation IN (N'Pass', N'Consider', N'Fail')
        ),
        CONSTRAINT FK_InterviewEvaluation_Interview FOREIGN KEY (InterviewID)
            REFERENCES dbo.Interview(InterviewID) ON DELETE CASCADE,
        CONSTRAINT FK_InterviewEvaluation_Interviewer FOREIGN KEY (InterviewerID)
            REFERENCES dbo.Employee(EmployeeID)
    );
END;
GO

IF OBJECT_ID(N'dbo.EvaluationDetail', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.EvaluationDetail (
        EvaluationDetailID INT IDENTITY(1,1) PRIMARY KEY,
        EvaluationID INT NOT NULL,
        CriteriaID INT NOT NULL,
        Score DECIMAL(7,2) NOT NULL,
        Comment NVARCHAR(500) NULL,
        CONSTRAINT UQ_EvaluationDetail UNIQUE (EvaluationID, CriteriaID),
        CONSTRAINT CK_EvaluationDetail_Score CHECK (Score >= 0),
        CONSTRAINT FK_EvaluationDetail_Evaluation FOREIGN KEY (EvaluationID)
            REFERENCES dbo.InterviewEvaluation(EvaluationID) ON DELETE CASCADE,
        CONSTRAINT FK_EvaluationDetail_Criteria FOREIGN KEY (CriteriaID)
            REFERENCES dbo.BaremCriteria(CriteriaID)
    );
END;
GO

/* ============================================================
   NOTIFICATIONS AND PASSWORD RESET
   ============================================================ */

IF OBJECT_ID(N'dbo.Notification', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Notification (
        NotificationID INT IDENTITY(1,1) PRIMARY KEY,
        SenderRole NVARCHAR(50) NOT NULL,
        ReceiverRole NVARCHAR(50) NOT NULL,
        ReceiverID INT NOT NULL,
        Message NVARCHAR(MAX) NOT NULL,
        SentDate DATETIME2(0) NOT NULL
            CONSTRAINT DF_Notification_SentDate DEFAULT (SYSDATETIME()),
        IsRead BIT NOT NULL
            CONSTRAINT DF_Notification_IsRead DEFAULT (0)
    );
END;
GO

IF OBJECT_ID(N'dbo.PasswordResetToken', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.PasswordResetToken (
        Id BIGINT IDENTITY(1,1) PRIMARY KEY,
        Email NVARCHAR(255) NOT NULL,
        TokenHash NVARCHAR(255) NOT NULL,
        CreatedAt DATETIME2(0) NOT NULL
            CONSTRAINT DF_PasswordReset_CreatedAt DEFAULT (SYSUTCDATETIME()),
        ExpiresAt DATETIME2(0) NOT NULL,
        Used BIT NOT NULL
            CONSTRAINT DF_PasswordReset_Used DEFAULT (0),
        Attempts INT NOT NULL
            CONSTRAINT DF_PasswordReset_Attempts DEFAULT (0),
        Role NVARCHAR(50) NOT NULL,
        CONSTRAINT CK_PasswordReset_Attempts CHECK (Attempts >= 0),
        CONSTRAINT CK_PasswordReset_Expires CHECK (ExpiresAt > CreatedAt)
    );
END;
GO

/* ============================================================
   EMPLOYEE PROFILE DOCUMENTS
   ============================================================ */

IF OBJECT_ID(N'dbo.ProfileDocumentType', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ProfileDocumentType (
        DocumentTypeID INT IDENTITY(1,1) PRIMARY KEY,
        DocumentName NVARCHAR(150) NOT NULL,
        Description NVARCHAR(500) NULL,
        IsActive BIT NOT NULL
            CONSTRAINT DF_ProfileDocumentType_IsActive DEFAULT (1),
        CreatedBy INT NOT NULL,
        CreatedAt DATETIME2(0) NOT NULL
            CONSTRAINT DF_ProfileDocumentType_CreatedAt DEFAULT (SYSDATETIME()),
        CONSTRAINT UQ_ProfileDocumentType_DocumentName UNIQUE (DocumentName),
        CONSTRAINT FK_ProfileDocumentType_CreatedBy FOREIGN KEY (CreatedBy)
            REFERENCES dbo.Employee(EmployeeID)
    );
END;
GO

IF OBJECT_ID(N'dbo.EmployeeProfileDocument', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.EmployeeProfileDocument (
        EmployeeDocumentID INT IDENTITY(1,1) PRIMARY KEY,
        EmployeeID INT NOT NULL,
        DocumentTypeID INT NOT NULL,
        IsRequired BIT NOT NULL
            CONSTRAINT DF_EmployeeProfileDocument_IsRequired DEFAULT (1),
        DueDate DATE NULL,
        Status VARCHAR(20) NOT NULL
            CONSTRAINT DF_EmployeeProfileDocument_Status DEFAULT ('Pending'),
        OriginalFileName NVARCHAR(255) NULL,
        ContentType VARCHAR(100) NULL,
        FileData VARBINARY(MAX) NULL,
        EmployeeNote NVARCHAR(1000) NULL,
        ReviewNote NVARCHAR(1000) NULL,
        SubmittedAt DATETIME2(0) NULL,
        ReviewedBy INT NULL,
        ReviewedAt DATETIME2(0) NULL,
        CreatedBy INT NOT NULL,
        CreatedAt DATETIME2(0) NOT NULL
            CONSTRAINT DF_EmployeeProfileDocument_CreatedAt DEFAULT (SYSDATETIME()),
        UpdatedAt DATETIME2(0) NOT NULL
            CONSTRAINT DF_EmployeeProfileDocument_UpdatedAt DEFAULT (SYSDATETIME()),
        CONSTRAINT UQ_EmployeeProfileDocument_Employee_Type UNIQUE (EmployeeID, DocumentTypeID),
        CONSTRAINT CK_EmployeeProfileDocument_Status CHECK (
            Status IN ('Pending', 'Submitted', 'Approved', 'Rejected')
        ),
        CONSTRAINT CK_EmployeeProfileDocument_Review CHECK (
            (Status IN ('Pending', 'Submitted') AND ReviewedBy IS NULL AND ReviewedAt IS NULL)
            OR (Status IN ('Approved', 'Rejected') AND ReviewedBy IS NOT NULL AND ReviewedAt IS NOT NULL)
        ),
        CONSTRAINT CK_EmployeeProfileDocument_File CHECK (
            (Status = 'Pending' AND OriginalFileName IS NULL AND ContentType IS NULL
                AND FileData IS NULL AND SubmittedAt IS NULL)
            OR (Status IN ('Submitted', 'Approved', 'Rejected')
                AND OriginalFileName IS NOT NULL AND ContentType IS NOT NULL
                AND FileData IS NOT NULL AND SubmittedAt IS NOT NULL)
        ),
        CONSTRAINT CK_EmployeeProfileDocument_FileSize CHECK (
            FileData IS NULL OR DATALENGTH(FileData) <= 10485760
        ),
        CONSTRAINT CK_EmployeeProfileDocument_ContentType CHECK (
            ContentType IS NULL OR ContentType IN ('application/pdf', 'image/jpeg', 'image/png')
        ),
        CONSTRAINT CK_EmployeeProfileDocument_RejectionNote CHECK (
            Status <> 'Rejected' OR NULLIF(LTRIM(RTRIM(ReviewNote)), '') IS NOT NULL
        ),
        CONSTRAINT FK_EmployeeProfileDocument_Employee FOREIGN KEY (EmployeeID)
            REFERENCES dbo.Employee(EmployeeID),
        CONSTRAINT FK_EmployeeProfileDocument_DocumentType FOREIGN KEY (DocumentTypeID)
            REFERENCES dbo.ProfileDocumentType(DocumentTypeID),
        CONSTRAINT FK_EmployeeProfileDocument_ReviewedBy FOREIGN KEY (ReviewedBy)
            REFERENCES dbo.Employee(EmployeeID),
        CONSTRAINT FK_EmployeeProfileDocument_CreatedBy FOREIGN KEY (CreatedBy)
            REFERENCES dbo.Employee(EmployeeID)
    );
END;
GO

IF OBJECT_ID(N'dbo.ProfileDocumentHistory', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ProfileDocumentHistory (
        HistoryID INT IDENTITY(1,1) PRIMARY KEY,
        EmployeeDocumentID INT NOT NULL,
        Action VARCHAR(30) NOT NULL,
        FromStatus VARCHAR(20) NULL,
        ToStatus VARCHAR(20) NOT NULL,
        Comment NVARCHAR(1000) NULL,
        ActionBy INT NOT NULL,
        CreatedAt DATETIME2(0) NOT NULL
            CONSTRAINT DF_ProfileDocumentHistory_CreatedAt DEFAULT (SYSDATETIME()),
        CONSTRAINT CK_ProfileDocumentHistory_Action CHECK (
            Action IN ('Assigned', 'Submitted', 'Resubmitted', 'Approved', 'Rejected')
        ),
        CONSTRAINT CK_ProfileDocumentHistory_FromStatus CHECK (
            FromStatus IS NULL OR FromStatus IN ('Pending', 'Submitted', 'Approved', 'Rejected')
        ),
        CONSTRAINT CK_ProfileDocumentHistory_ToStatus CHECK (
            ToStatus IN ('Pending', 'Submitted', 'Approved', 'Rejected')
        ),
        CONSTRAINT CK_ProfileDocumentHistory_Transition CHECK (
            (Action = 'Assigned' AND FromStatus IS NULL AND ToStatus = 'Pending')
            OR (Action = 'Submitted' AND FromStatus = 'Pending' AND ToStatus = 'Submitted')
            OR (Action = 'Resubmitted' AND FromStatus = 'Rejected' AND ToStatus = 'Submitted')
            OR (Action = 'Approved' AND FromStatus = 'Submitted' AND ToStatus = 'Approved')
            OR (Action = 'Rejected' AND FromStatus = 'Submitted' AND ToStatus = 'Rejected')
        ),
        CONSTRAINT FK_ProfileDocumentHistory_Document FOREIGN KEY (EmployeeDocumentID)
            REFERENCES dbo.EmployeeProfileDocument(EmployeeDocumentID),
        CONSTRAINT FK_ProfileDocumentHistory_ActionBy FOREIGN KEY (ActionBy)
            REFERENCES dbo.Employee(EmployeeID)
    );
END;
GO

/* ============================================================
   INDEXES
   ============================================================ */

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_Department_ManagerID' AND object_id = OBJECT_ID(N'dbo.Department'))
    CREATE INDEX IX_Department_ManagerID ON dbo.Department(ManagerID);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_Employee_Role' AND object_id = OBJECT_ID(N'dbo.Employee'))
    CREATE INDEX IX_Employee_Role ON dbo.Employee(RoleID);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_Employee_Department' AND object_id = OBJECT_ID(N'dbo.Employee'))
    CREATE INDEX IX_Employee_Department ON dbo.Employee(Department);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_EmployeePositionHistory_Employee' AND object_id = OBJECT_ID(N'dbo.EmployeePositionHistory'))
    CREATE INDEX IX_EmployeePositionHistory_Employee ON dbo.EmployeePositionHistory(EmployeeID);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_JobPost_Visible' AND object_id = OBJECT_ID(N'dbo.JobPost'))
    CREATE INDEX IX_JobPost_Visible ON dbo.JobPost(Visible);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_JobPost_Deadline' AND object_id = OBJECT_ID(N'dbo.JobPost'))
    CREATE INDEX IX_JobPost_Deadline ON dbo.JobPost(Deadline);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_Apply_Candidate' AND object_id = OBJECT_ID(N'dbo.Apply'))
    CREATE INDEX IX_Apply_Candidate ON dbo.Apply(CandidateID);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_Apply_JobPost' AND object_id = OBJECT_ID(N'dbo.Apply'))
    CREATE INDEX IX_Apply_JobPost ON dbo.Apply(JobPostID);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_Apply_Status' AND object_id = OBJECT_ID(N'dbo.Apply'))
    CREATE INDEX IX_Apply_Status ON dbo.Apply(Status);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_Interview_Date' AND object_id = OBJECT_ID(N'dbo.Interview'))
    CREATE INDEX IX_Interview_Date ON dbo.Interview(InterviewDate, StartTime);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_InterviewParticipant_Employee' AND object_id = OBJECT_ID(N'dbo.InterviewParticipant'))
    CREATE INDEX IX_InterviewParticipant_Employee ON dbo.InterviewParticipant(EmployeeID, InterviewID);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_InterviewEvaluation_Interviewer' AND object_id = OBJECT_ID(N'dbo.InterviewEvaluation'))
    CREATE INDEX IX_InterviewEvaluation_Interviewer ON dbo.InterviewEvaluation(InterviewerID);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_PasswordResetToken_Email' AND object_id = OBJECT_ID(N'dbo.PasswordResetToken'))
    CREATE INDEX IX_PasswordResetToken_Email ON dbo.PasswordResetToken(Email);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_EmployeeProfileDocument_Status' AND object_id = OBJECT_ID(N'dbo.EmployeeProfileDocument'))
    CREATE INDEX IX_EmployeeProfileDocument_Status ON dbo.EmployeeProfileDocument(Status, DueDate);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_ProfileDocumentHistory_Document_CreatedAt' AND object_id = OBJECT_ID(N'dbo.ProfileDocumentHistory'))
    CREATE INDEX IX_ProfileDocumentHistory_Document_CreatedAt
        ON dbo.ProfileDocumentHistory(EmployeeDocumentID, CreatedAt DESC);
GO

/* ============================================================
   IDEMPOTENT SAMPLE DATA
   ============================================================ */

BEGIN TRY
    BEGIN TRANSACTION;

    DECLARE @PasswordHash NVARCHAR(255) = N'EcAbknci4zUp0FPIsk83QGU0ZWU=';

    INSERT INTO dbo.Roles (RoleName)
    SELECT source.RoleName
    FROM (VALUES
        (N'Admin'),
        (N'HR Staff'),
        (N'Manager'),
        (N'Candidate'),
        (N'Employee')
    ) AS source(RoleName)
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.Roles existing WHERE existing.RoleName = source.RoleName
    );

    IF NOT EXISTS (SELECT 1 FROM dbo.Admin WHERE Username = N'admin')
        INSERT INTO dbo.Admin (Username, PasswordHash) VALUES (N'admin', @PasswordHash);
    ELSE
        UPDATE dbo.Admin SET PasswordHash = @PasswordHash WHERE Username = N'admin';

    DECLARE @AdminID INT = (SELECT AdminID FROM dbo.Admin WHERE Username = N'admin');
    DECLARE @AdminRoleID INT = (SELECT RoleID FROM dbo.Roles WHERE RoleName = N'Admin');
    IF NOT EXISTS (SELECT 1 FROM dbo.Role_Table WHERE AdminID = @AdminID AND RoleID = @AdminRoleID)
        INSERT INTO dbo.Role_Table (AdminID, RoleID) VALUES (@AdminID, @AdminRoleID);

    INSERT INTO dbo.Candidate
        (CandidateName, Address, Email, PhoneNumber, Nationality, PasswordHash, Avatar)
    SELECT source.CandidateName, source.Address, source.Email, source.PhoneNumber,
           source.Nationality, @PasswordHash, source.Avatar
    FROM (VALUES
	(N'Nguyễn ADMIN', N'Hà Nội', N'admin@gmail.com', '0911111169', N'Việt Nam', N'avatar10.png'),
        (N'Nguyễn Văn Nam', N'Hà Nội', N'nam@gmail.com', '0911111111', N'Việt Nam', N'avatar1.png'),
        (N'Trần Thị Hoa', N'Hồ Chí Minh', N'hoa@gmail.com', '0912222222', N'Việt Nam', N'avatar2.png'),
        (N'Lê Văn Minh', N'Đà Nẵng', N'minh@gmail.com', '0913333333', N'Việt Nam', N'avatar3.png'),
        (N'Đỗ Gia An', N'Hà Nội', N'an@gmail.com', '0916666666', N'Việt Nam', N'avatar6.png'),
        (N'Phạm Thị Lan', N'Hải Phòng', N'lan@gmail.com', '0914444444', N'Việt Nam', N'avatar4.png'),
        (N'Hoàng Văn Tuấn', N'Cần Thơ', N'tuan@gmail.com', '0915555555', N'Việt Nam', N'avatar5.png'),
        (N'Nguyễn Đức Bình', N'Hà Nội', N'binh@gmail.com', '0917777777', N'Việt Nam', N'avatar7.png')
    ) AS source(CandidateName, Address, Email, PhoneNumber, Nationality, Avatar)
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.Candidate existing
        WHERE existing.Email = source.Email OR existing.PhoneNumber = source.PhoneNumber
    );

    UPDATE dbo.Candidate
    SET PasswordHash = @PasswordHash, IsActive = 1
    WHERE Email IN (
        N'nam@gmail.com', N'hoa@gmail.com', N'minh@gmail.com', N'an@gmail.com',
        N'lan@gmail.com', N'tuan@gmail.com', N'binh@gmail.com'
    );

    INSERT INTO dbo.Employee
        (CandidateID, RoleID, EmployeeCode, HireDate, Department, Position, Salary)
    SELECT candidate.CandidateID, role.RoleID, source.EmployeeCode, CAST(GETDATE() AS DATE),
           source.Department, source.Position, source.Salary
    FROM (VALUES
	(N'admin@gmail.com', N'Admin', 'EMP099', N'Admin', N'Admin', CAST(1500 AS DECIMAL(18,2))),
        (N'nam@gmail.com', N'HR Staff', 'EMP001', N'Human Resources', N'HR Staff', CAST(1500 AS DECIMAL(18,2))),
        (N'hoa@gmail.com', N'HR Staff', 'EMP002', N'Human Resources', N'HR Staff', CAST(1400 AS DECIMAL(18,2))),
        (N'minh@gmail.com', N'Manager', 'EMP003', N'IT', N'Engineering Manager', CAST(3000 AS DECIMAL(18,2))),
        (N'an@gmail.com', N'Employee', 'EMP004', N'IT', N'Software Engineer', CAST(1800 AS DECIMAL(18,2)))
    ) AS source(Email, RoleName, EmployeeCode, Department, Position, Salary)
    JOIN dbo.Candidate candidate ON candidate.Email = source.Email
    JOIN dbo.Roles role ON role.RoleName = source.RoleName
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.Employee existing
        WHERE existing.CandidateID = candidate.CandidateID OR existing.EmployeeCode = source.EmployeeCode
    );

    INSERT INTO dbo.EmployeePositionHistory
        (EmployeeID, RoleID, Position, Department, StartDate, EndDate, Note)
    SELECT employee.EmployeeID, employee.RoleID, employee.Position, employee.Department,
           employee.HireDate, NULL, N'Vị trí ban đầu khi trở thành nhân viên'
    FROM dbo.Employee employee
    WHERE employee.EmployeeCode IN ('EMP001', 'EMP002', 'EMP003', 'EMP004')
      AND NOT EXISTS (
          SELECT 1 FROM dbo.EmployeePositionHistory history
          WHERE history.EmployeeID = employee.EmployeeID AND history.EndDate IS NULL
      );

    INSERT INTO dbo.CV
        (CandidateID, FullName, Address, Email, Position, NumberExp, Education,
         Field, CurrentSalary, Birthday, Nationality, Gender, FileData)
    SELECT candidate.CandidateID, source.FullName, source.Address, source.Email,
           source.Position, source.NumberExp, source.Education, source.Field,
           source.CurrentSalary, source.Birthday, N'Việt Nam', source.Gender, source.FileData
    FROM (VALUES
        (N'lan@gmail.com', N'Phạm Thị Lan', N'Hải Phòng', N'Java Developer', 4,
         N'Đại học Bách Khoa', N'Công nghệ thông tin', CAST(1700 AS DECIMAL(18,2)),
         CAST('1994-07-07' AS DATE), N'Nữ', N'cv_lan.pdf'),
        (N'tuan@gmail.com', N'Hoàng Văn Tuấn', N'Cần Thơ', N'UI/UX Designer', 2,
         N'Đại học Kiến trúc', N'Thiết kế', CAST(1100 AS DECIMAL(18,2)),
         CAST('1999-03-15' AS DATE), N'Nam', N'cv_tuan.pdf'),
        (N'binh@gmail.com', N'Nguyễn Đức Bình', N'Hà Nội', N'HR Staff', 1,
         N'Đại học Kinh tế', N'Quản trị nhân sự', CAST(900 AS DECIMAL(18,2)),
         CAST('2000-11-20' AS DATE), N'Nam', N'cv_binh.pdf')
    ) AS source(Email, FullName, Address, Position, NumberExp, Education, Field,
                CurrentSalary, Birthday, Gender, FileData)
    JOIN dbo.Candidate candidate ON candidate.Email = source.Email
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.CV existing
        WHERE existing.CandidateID = candidate.CandidateID AND existing.Position = source.Position
    );

    DECLARE @HRID INT = (
        SELECT employee.EmployeeID
        FROM dbo.Employee employee
        JOIN dbo.Candidate candidate ON employee.CandidateID = candidate.CandidateID
        WHERE candidate.Email = N'nam@gmail.com'
    );
    DECLARE @ManagerID INT = (
        SELECT employee.EmployeeID
        FROM dbo.Employee employee
        JOIN dbo.Candidate candidate ON employee.CandidateID = candidate.CandidateID
        WHERE candidate.Email = N'minh@gmail.com'
    );
    DECLARE @EmployeeID INT = (
        SELECT employee.EmployeeID
        FROM dbo.Employee employee
        JOIN dbo.Candidate candidate ON employee.CandidateID = candidate.CandidateID
        WHERE candidate.Email = N'an@gmail.com'
    );

    INSERT INTO dbo.Department (DepartmentName, Description, ManagerID)
    SELECT source.DepartmentName, source.Description,
           CASE source.DepartmentName
               WHEN N'IT' THEN @ManagerID
               WHEN N'Human Resources' THEN @HRID
               ELSE NULL
           END
    FROM (VALUES
        (N'IT', N'Phòng Công nghệ thông tin'),
        (N'Human Resources', N'Phòng Nhân sự'),
        (N'Marketing', N'Nghiên cứu thị trường, quảng cáo và phát triển người dùng'),
        (N'Bán hàng (Sales)', N'Kinh doanh trực tiếp và quan hệ đối tác khách hàng'),
        (N'Kế toán (Accounting)', N'Quản lý sổ sách kế toán, hóa đơn và chứng từ')
    ) AS source(DepartmentName, Description)
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.Department existing WHERE existing.DepartmentName = source.DepartmentName
    );

    INSERT INTO dbo.JobPost
        (Title, Description, Category, Position, Location, OfferMin, OfferMax,
         NumberExp, Visible, TypeJob, Deadline, CreatedBy)
    SELECT source.Title, source.Description, source.Category, source.Position, source.Location,
           source.OfferMin, source.OfferMax, source.NumberExp, 1, N'Full-time',
           DATEADD(DAY, 30, CAST(GETDATE() AS DATE)), @HRID
    FROM (VALUES
        (N'Tuyển Java Developer', N'Phát triển và bảo trì hệ thống Java.', N'IT',
         N'Java Developer', N'Hà Nội, Việt Nam', CAST(1500 AS DECIMAL(18,2)), CAST(2500 AS DECIMAL(18,2)), 2),
        (N'Tuyển HR Staff', N'Thực hiện tuyển dụng và quản lý hồ sơ nhân sự.', N'Human Resources',
         N'HR Staff', N'Hà Nội, Việt Nam', CAST(1000 AS DECIMAL(18,2)), CAST(1800 AS DECIMAL(18,2)), 1),
        (N'Tuyển UI/UX Designer', N'Thiết kế giao diện và trải nghiệm người dùng.', N'Design',
         N'UI/UX Designer', N'Hà Nội, Việt Nam', CAST(1000 AS DECIMAL(18,2)), CAST(2000 AS DECIMAL(18,2)), 1)
    ) AS source(Title, Description, Category, Position, Location, OfferMin, OfferMax, NumberExp)
    WHERE NOT EXISTS (SELECT 1 FROM dbo.JobPost existing WHERE existing.Title = source.Title);

    DECLARE @JavaJobID INT = (SELECT TOP (1) JobPostID FROM dbo.JobPost WHERE Title = N'Tuyển Java Developer');
    DECLARE @HrJobID INT = (SELECT TOP (1) JobPostID FROM dbo.JobPost WHERE Title = N'Tuyển HR Staff');
    DECLARE @DesignJobID INT = (SELECT TOP (1) JobPostID FROM dbo.JobPost WHERE Title = N'Tuyển UI/UX Designer');
    DECLARE @LanID INT = (SELECT CandidateID FROM dbo.Candidate WHERE Email = N'lan@gmail.com');
    DECLARE @TuanID INT = (SELECT CandidateID FROM dbo.Candidate WHERE Email = N'tuan@gmail.com');
    DECLARE @BinhID INT = (SELECT CandidateID FROM dbo.Candidate WHERE Email = N'binh@gmail.com');
    DECLARE @LanCvID INT = (SELECT TOP (1) CVID FROM dbo.CV WHERE CandidateID = @LanID AND Position = N'Java Developer');
    DECLARE @TuanCvID INT = (SELECT TOP (1) CVID FROM dbo.CV WHERE CandidateID = @TuanID AND Position = N'UI/UX Designer');
    DECLARE @BinhCvID INT = (SELECT TOP (1) CVID FROM dbo.CV WHERE CandidateID = @BinhID AND Position = N'HR Staff');

    IF NOT EXISTS (SELECT 1 FROM dbo.Apply WHERE CandidateID = @LanID AND JobPostID = @JavaJobID)
        INSERT INTO dbo.Apply (JobPostID, CandidateID, CVID, Status, Note)
        VALUES (@JavaJobID, @LanID, @LanCvID, N'Interviewing', N'Đang tham gia quy trình phỏng vấn Java Developer');
    IF NOT EXISTS (SELECT 1 FROM dbo.Apply WHERE CandidateID = @TuanID AND JobPostID = @DesignJobID)
        INSERT INTO dbo.Apply (JobPostID, CandidateID, CVID, Status, Note)
        VALUES (@DesignJobID, @TuanID, @TuanCvID, N'Shortlisted', N'Đã qua vòng lọc hồ sơ, sẵn sàng lên lịch');
    IF NOT EXISTS (SELECT 1 FROM dbo.Apply WHERE CandidateID = @BinhID AND JobPostID = @HrJobID)
        INSERT INTO dbo.Apply (JobPostID, CandidateID, CVID, Status, Note)
        VALUES (@HrJobID, @BinhID, @BinhCvID, N'Pending', N'Đang chờ HR sàng lọc');

    IF NOT EXISTS (SELECT 1 FROM dbo.SavedJob WHERE CandidateID = @LanID AND JobPostID = @DesignJobID)
        INSERT INTO dbo.SavedJob (CandidateID, JobPostID) VALUES (@LanID, @DesignJobID);
    IF NOT EXISTS (SELECT 1 FROM dbo.SavedJob WHERE CandidateID = @TuanID AND JobPostID = @JavaJobID)
        INSERT INTO dbo.SavedJob (CandidateID, JobPostID) VALUES (@TuanID, @JavaJobID);

    IF NOT EXISTS (SELECT 1 FROM dbo.Potential WHERE CVID = @LanCvID AND JobPostID = @JavaJobID)
        INSERT INTO dbo.Potential (CVID, JobPostID, CreatedBy, Note)
        VALUES (@LanCvID, @JavaJobID, @HRID, N'Ứng viên có kinh nghiệm Java phù hợp');

    IF NOT EXISTS (SELECT 1 FROM dbo.InterviewBarem WHERE JobPostID = @JavaJobID AND BaremName = N'Barem Java Developer')
        INSERT INTO dbo.InterviewBarem (JobPostID, BaremName, Description, TotalScore, CreatedBy)
        VALUES (@JavaJobID, N'Barem Java Developer', N'Đánh giá chuyên môn và kỹ năng Java', 100, @HRID);
    IF NOT EXISTS (SELECT 1 FROM dbo.InterviewBarem WHERE JobPostID = @DesignJobID AND BaremName = N'Barem UI/UX Designer')
        INSERT INTO dbo.InterviewBarem (JobPostID, BaremName, Description, TotalScore, CreatedBy)
        VALUES (@DesignJobID, N'Barem UI/UX Designer', N'Đánh giá portfolio và tư duy thiết kế', 100, @HRID);

    DECLARE @JavaBaremID INT = (
        SELECT BaremID FROM dbo.InterviewBarem
        WHERE JobPostID = @JavaJobID AND BaremName = N'Barem Java Developer'
    );
    DECLARE @DesignBaremID INT = (
        SELECT BaremID FROM dbo.InterviewBarem
        WHERE JobPostID = @DesignJobID AND BaremName = N'Barem UI/UX Designer'
    );

    INSERT INTO dbo.BaremCriteria
        (BaremID, CriteriaName, Description, MaxScore, Weight, DisplayOrder)
    SELECT @JavaBaremID, source.CriteriaName, source.Description,
           source.MaxScore, source.Weight, source.DisplayOrder
    FROM (VALUES
        (N'Kiến thức Java', N'Java core, OOP và collection', CAST(30 AS DECIMAL(6,2)), CAST(30 AS DECIMAL(5,2)), 1),
        (N'Kinh nghiệm thực tế', N'Kinh nghiệm tham gia dự án', CAST(20 AS DECIMAL(6,2)), CAST(20 AS DECIMAL(5,2)), 2),
        (N'Giải quyết vấn đề', N'Phân tích và xử lý tình huống', CAST(20 AS DECIMAL(6,2)), CAST(20 AS DECIMAL(5,2)), 3),
        (N'Giao tiếp', N'Trình bày và phối hợp', CAST(15 AS DECIMAL(6,2)), CAST(15 AS DECIMAL(5,2)), 4),
        (N'Thái độ', N'Tinh thần học hỏi và trách nhiệm', CAST(15 AS DECIMAL(6,2)), CAST(15 AS DECIMAL(5,2)), 5)
    ) AS source(CriteriaName, Description, MaxScore, Weight, DisplayOrder)
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.BaremCriteria existing
        WHERE existing.BaremID = @JavaBaremID AND existing.CriteriaName = source.CriteriaName
    );

    INSERT INTO dbo.BaremCriteria
        (BaremID, CriteriaName, Description, MaxScore, Weight, DisplayOrder)
    SELECT @DesignBaremID, source.CriteriaName, source.Description,
           source.MaxScore, source.Weight, source.DisplayOrder
    FROM (VALUES
        (N'Portfolio', N'Chất lượng và mức độ phù hợp của portfolio', CAST(40 AS DECIMAL(6,2)), CAST(40 AS DECIMAL(5,2)), 1),
        (N'Tư duy UX', N'Khả năng nghiên cứu và giải quyết vấn đề người dùng', CAST(35 AS DECIMAL(6,2)), CAST(35 AS DECIMAL(5,2)), 2),
        (N'Giao tiếp', N'Khả năng giải thích quyết định thiết kế', CAST(25 AS DECIMAL(6,2)), CAST(25 AS DECIMAL(5,2)), 3)
    ) AS source(CriteriaName, Description, MaxScore, Weight, DisplayOrder)
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.BaremCriteria existing
        WHERE existing.BaremID = @DesignBaremID AND existing.CriteriaName = source.CriteriaName
    );

    DECLARE @LanApplyID INT = (
        SELECT ApplyID FROM dbo.Apply WHERE CandidateID = @LanID AND JobPostID = @JavaJobID
    );
    IF NOT EXISTS (SELECT 1 FROM dbo.Interview WHERE ApplyID = @LanApplyID AND InterviewRound = 1)
        INSERT INTO dbo.Interview
            (ApplyID, BaremID, InterviewRound, InterviewDate, StartTime, EndTime,
             InterviewType, Location, MeetingLink, Status, Note, CreatedBy)
        VALUES
            (@LanApplyID, @JavaBaremID, 1, DATEADD(DAY, 3, CAST(GETDATE() AS DATE)),
             '09:00', '10:00', N'Offline', N'Hà Nội, Việt Nam', NULL,
             N'Scheduled', N'Vòng phỏng vấn chuyên môn', @HRID);

    DECLARE @InterviewID INT = (
        SELECT InterviewID FROM dbo.Interview WHERE ApplyID = @LanApplyID AND InterviewRound = 1
    );
    IF NOT EXISTS (SELECT 1 FROM dbo.InterviewParticipant WHERE InterviewID = @InterviewID AND EmployeeID = @HRID)
        INSERT INTO dbo.InterviewParticipant
            (InterviewID, EmployeeID, ParticipantRole, IsLeadInterviewer)
        VALUES (@InterviewID, @HRID, N'Interviewer', 1);
    IF NOT EXISTS (SELECT 1 FROM dbo.InterviewParticipant WHERE InterviewID = @InterviewID AND EmployeeID = @ManagerID)
        INSERT INTO dbo.InterviewParticipant
            (InterviewID, EmployeeID, ParticipantRole, IsLeadInterviewer)
        VALUES (@InterviewID, @ManagerID, N'Interviewer', 0);

    IF NOT EXISTS (SELECT 1 FROM dbo.InterviewEvaluation WHERE InterviewID = @InterviewID AND InterviewerID = @HRID)
        INSERT INTO dbo.InterviewEvaluation
            (InterviewID, InterviewerID, TotalScore, Recommendation, Comment)
        VALUES (@InterviewID, @HRID, 85, N'Pass', N'Ứng viên có kiến thức chuyên môn tốt.');

    DECLARE @EvaluationID INT = (
        SELECT EvaluationID FROM dbo.InterviewEvaluation
        WHERE InterviewID = @InterviewID AND InterviewerID = @HRID
    );
    INSERT INTO dbo.EvaluationDetail (EvaluationID, CriteriaID, Score, Comment)
    SELECT @EvaluationID, criteria.CriteriaID, source.Score, source.Comment
    FROM (VALUES
        (N'Kiến thức Java', CAST(27 AS DECIMAL(7,2)), N'Nắm chắc Java core'),
        (N'Kinh nghiệm thực tế', CAST(17 AS DECIMAL(7,2)), N'Có kinh nghiệm dự án phù hợp'),
        (N'Giải quyết vấn đề', CAST(18 AS DECIMAL(7,2)), N'Phân tích tình huống tốt'),
        (N'Giao tiếp', CAST(12 AS DECIMAL(7,2)), N'Giao tiếp khá tốt'),
        (N'Thái độ', CAST(11 AS DECIMAL(7,2)), N'Thái độ tích cực')
    ) AS source(CriteriaName, Score, Comment)
    JOIN dbo.BaremCriteria criteria
      ON criteria.BaremID = @JavaBaremID AND criteria.CriteriaName = source.CriteriaName
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.EvaluationDetail existing
        WHERE existing.EvaluationID = @EvaluationID AND existing.CriteriaID = criteria.CriteriaID
    );

    UPDATE application
    SET Status = N'Interviewing'
    FROM dbo.Apply application
    WHERE application.FinalResult IS NULL
      AND EXISTS (SELECT 1 FROM dbo.Interview interview WHERE interview.ApplyID = application.ApplyID)
      AND application.Status IN (N'Pending', N'Shortlisted');

    UPDATE application
    SET Status = N'Shortlisted'
    FROM dbo.Apply application
    WHERE application.CandidateID = @TuanID
      AND application.JobPostID = @DesignJobID
      AND application.FinalResult IS NULL
      AND application.Status = N'Pending'
      AND NOT EXISTS (SELECT 1 FROM dbo.Interview interview WHERE interview.ApplyID = application.ApplyID);

    IF NOT EXISTS (
        SELECT 1 FROM dbo.Notification
        WHERE ReceiverRole = N'Candidate' AND ReceiverID = @LanID
          AND Message = N'Bạn đã được mời tham gia phỏng vấn vòng 1.'
    )
        INSERT INTO dbo.Notification (SenderRole, ReceiverRole, ReceiverID, Message)
        VALUES (N'HR Staff', N'Candidate', @LanID, N'Bạn đã được mời tham gia phỏng vấn vòng 1.');

    INSERT INTO dbo.ProfileDocumentType (DocumentName, Description, CreatedBy)
    SELECT source.DocumentName, source.Description, @HRID
    FROM (VALUES
        (N'Căn cước công dân', N'Ảnh hoặc bản scan rõ hai mặt của CCCD'),
        (N'Bằng tốt nghiệp', N'Bằng cấp cao nhất liên quan đến vị trí công việc'),
        (N'Giấy khám sức khỏe', N'Giấy khám sức khỏe còn hiệu lực'),
        (N'Thông tin tài khoản ngân hàng', N'Tài liệu xác nhận tài khoản nhận lương')
    ) AS source(DocumentName, Description)
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.ProfileDocumentType existing
        WHERE existing.DocumentName = source.DocumentName
    );

    INSERT INTO dbo.EmployeeProfileDocument
        (EmployeeID, DocumentTypeID, IsRequired, DueDate, CreatedBy)
    SELECT @EmployeeID, documentType.DocumentTypeID, source.IsRequired,
           CASE WHEN source.DueDays IS NULL THEN NULL
                ELSE DATEADD(DAY, source.DueDays, CAST(GETDATE() AS DATE)) END,
           @HRID
    FROM (VALUES
        (N'Căn cước công dân', CAST(1 AS BIT), CAST(7 AS INT)),
        (N'Bằng tốt nghiệp', CAST(1 AS BIT), CAST(14 AS INT)),
        (N'Giấy khám sức khỏe', CAST(1 AS BIT), CAST(14 AS INT)),
        (N'Thông tin tài khoản ngân hàng', CAST(0 AS BIT), CAST(NULL AS INT))
    ) AS source(DocumentName, IsRequired, DueDays)
    JOIN dbo.ProfileDocumentType documentType
      ON documentType.DocumentName = source.DocumentName
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.EmployeeProfileDocument existing
        WHERE existing.EmployeeID = @EmployeeID
          AND existing.DocumentTypeID = documentType.DocumentTypeID
    );

    INSERT INTO dbo.ProfileDocumentHistory
        (EmployeeDocumentID, Action, FromStatus, ToStatus, Comment, ActionBy)
    SELECT document.EmployeeDocumentID, 'Assigned', NULL, 'Pending',
           N'Yêu cầu hồ sơ mẫu được khởi tạo cùng hệ thống', @HRID
    FROM dbo.EmployeeProfileDocument document
    WHERE document.EmployeeID = @EmployeeID
      AND NOT EXISTS (
          SELECT 1 FROM dbo.ProfileDocumentHistory history
          WHERE history.EmployeeDocumentID = document.EmployeeDocumentID
      );

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO

CREATE TABLE AccountRequest (
    RequestID INT IDENTITY(1,1) PRIMARY KEY,
    RequestedEmail NVARCHAR(255) NOT NULL,
    FullName NVARCHAR(150) NULL,
    Phone NVARCHAR(20) NULL,
    Department NVARCHAR(100) NULL,
    Position NVARCHAR(100) NULL,
    RoleID INT NULL,
    Salary DECIMAL(12,2) NULL,
    Note NVARCHAR(1000) NULL,
    Address NVARCHAR(255) NULL,
    Nationality NVARCHAR(50) NULL,
    RequestedBy INT NOT NULL,
    Status NVARCHAR(20) NOT NULL DEFAULT 'Pending',
    CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),
    ReviewedByAdminId INT NULL,
    ReviewedAt DATETIME NULL,
    RejectionNote NVARCHAR(1000) NULL,
    FulfilledEmployeeID INT NULL,

    CONSTRAINT FK_AccountRequest_RequestedBy FOREIGN KEY (RequestedBy) REFERENCES Employee(EmployeeID),
    CONSTRAINT FK_AccountRequest_RoleID FOREIGN KEY (RoleID) REFERENCES Roles(RoleID),
    CONSTRAINT FK_AccountRequest_ReviewedByAdminId FOREIGN KEY (ReviewedByAdminId) REFERENCES Admin(AdminID),
    CONSTRAINT FK_AccountRequest_FulfilledEmployeeID FOREIGN KEY (FulfilledEmployeeID) REFERENCES Employee(EmployeeID),
    CONSTRAINT CK_AccountRequest_Status CHECK (Status IN ('Pending', 'Approved', 'Rejected'))
);

CREATE INDEX IX_AccountRequest_Status ON AccountRequest(Status);
CREATE INDEX IX_AccountRequest_RequestedBy ON AccountRequest(RequestedBy);
GO

/* Migration for databases created before Address/Nationality were added
   to AccountRequest - safe to re-run, only adds the columns if missing. */
IF COL_LENGTH('dbo.AccountRequest', 'Address') IS NULL
BEGIN
    ALTER TABLE dbo.AccountRequest ADD Address NVARCHAR(255) NULL;
END;
GO
IF COL_LENGTH('dbo.AccountRequest', 'Nationality') IS NULL
BEGIN
    ALTER TABLE dbo.AccountRequest ADD Nationality NVARCHAR(50) NULL;
END;
GO

/* ============================================================
   VERIFICATION SUMMARY
   ============================================================ */

SELECT N'Database' AS Item, DB_NAME() AS Value
UNION ALL SELECT N'Roles', CAST(COUNT(*) AS NVARCHAR(30)) FROM dbo.Roles
UNION ALL SELECT N'Departments', CAST(COUNT(*) AS NVARCHAR(30)) FROM dbo.Department
UNION ALL SELECT N'Candidates', CAST(COUNT(*) AS NVARCHAR(30)) FROM dbo.Candidate
UNION ALL SELECT N'Employees', CAST(COUNT(*) AS NVARCHAR(30)) FROM dbo.Employee
UNION ALL SELECT N'Job posts', CAST(COUNT(*) AS NVARCHAR(30)) FROM dbo.JobPost
UNION ALL SELECT N'Applications', CAST(COUNT(*) AS NVARCHAR(30)) FROM dbo.Apply
UNION ALL SELECT N'Interviews', CAST(COUNT(*) AS NVARCHAR(30)) FROM dbo.Interview
UNION ALL SELECT N'Profile document types', CAST(COUNT(*) AS NVARCHAR(30)) FROM dbo.ProfileDocumentType
UNION ALL SELECT N'Employee profile documents', CAST(COUNT(*) AS NVARCHAR(30)) FROM dbo.EmployeeProfileDocument;
GO

SELECT candidate.Email, candidate.CandidateName, role.RoleName, employee.EmployeeCode
FROM dbo.Employee employee
JOIN dbo.Candidate candidate ON employee.CandidateID = candidate.CandidateID
JOIN dbo.Roles role ON employee.RoleID = role.RoleID
WHERE candidate.Email IN (N'nam@gmail.com', N'hoa@gmail.com', N'minh@gmail.com', N'an@gmail.com')
ORDER BY employee.EmployeeCode;
GO

PRINT N'SWP391 database initialization completed successfully.';
PRINT N'Test password for seeded employee accounts: 123456';
GO
