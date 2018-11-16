USE master;
GO
CREATE DATABASE TEST
GO

USE TEST;
GO
CREATE SCHEMA TEST_sch
GO

USE TEST
GO
CREATE TABLE TextTable
(CardID uniqueidentifier NOT NULL,
FileID uniqueidentifier NOT NULL,
PartFileID uniqueidentifier NOT NULL,
Text nvarchar(MAX) NOT NULL
);

CREATE UNIQUE INDEX ui_partFile ON TextTable(PartFileID);  
CREATE FULLTEXT CATALOG ftCatalog AS DEFAULT; 
CREATE FULLTEXT INDEX ON TextTable(Text)   
   KEY INDEX ui_partFile   
   ON  ftCatalog

GO


