-- NotificationMaster table
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'NotificationMaster')
BEGIN
    CREATE TABLE NotificationMaster (
        NotificationId INT IDENTITY(1,1) PRIMARY KEY,
        EmpId INT NULL,
        Title NVARCHAR(200) NULL,
        Message NVARCHAR(1000) NULL,
        Type NVARCHAR(50) NULL,
        IsRead BIT DEFAULT 0,
        IsStarred BIT DEFAULT 0,
        RelatedId INT NULL,
        IsActive BIT DEFAULT 1,
        IsDeleted BIT DEFAULT 0,
        CreatedDate DATETIME DEFAULT GETDATE()
    );
END;