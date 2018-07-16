-- ------------------------------------------------------------------------------------------------
-- SQL
-- ------------------------------------------------------------------------------------------------
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
SET NOCOUNT ON

ALTER PROC [dbo].[ATTRIBUTES] AS
BEGIN
SET NOCOUNT ON
INSERT INTO P_COUNTS SELECT COALESCE(x,y,x) AS P_ATTRIBUTE,  count( *) PCount FROM nose GROUP BY x


-- DWU
SELECT [type], COUNT([pdw_node_id]) FROM sys.dm_pdw_nodes GROUP BY [type];
// node_type  node_count
// COMPUTE    2
// CONTROL    1

IF OBJECT_ID('tablename', 'U') IS NOT NULL DROP TABLE tablename ;
GO


SELECT *, distribution_policy_desc FROM pdw_table_distribution_properties ptdp JOIN sys.objects o ON ptdp.object_id = o.object_id



DECLARE @i INT    SET @i = 1
DECLARE @date DATETIME   SET @date = dateadd(mi, @i, '2017-02-04')
WHILE (@i <= 60)
BEGIN 
INSERT INTO [Orders] (OrderDate) SEELCT @date
INSERT INTO [Orders] (OrderDate) SELECT dateadd(week,1,@date)
INSERT INTO [Orders] (OrderDate) SELECT dateadd(week,2,@date) 
SET @i = @i + 1
END
https://youtu.be/rH0HPoQplCQ
https://youtu.be/bGJnfJGnU30
exec uspGetRowCountByPartition @tableName1='Orders', @tableName2=NULL

ALTER TABLE dbo.Orders SWITCH PARTITION 3 to dbo.Orders_Staging
ALTER TABLE dbo.Orders SWITCH TO dbo.Orders_Staging PARTITION 3



//  REPLICATED TABLES  (avoid data movement, shuffling, by having data locally)

CREATE TABLE Orders (col2, col2, col3 int etc...)
WITH
(
DISTRIBUTION = REPLICATE,
CLUSTERED COLUMNSTORE INDEX                       -- 1. CLUSTERED COLUMNSTORE doesnt require colulmn 
CLUSTERED INDEX (id)                              -- 2. clustered rowstore index, we must specify column
CREATE INDEX zipCodeIndex ON table1 (zipCode)     -- 3. non-clustered rowstore index, must be updated each time rows are added
)

CREATE TABLE (...) WITH ( HEAP ) ;                -- 4. no index (will not appear as index in MSSQL), unordered data, may be best choice to load data 




-- CREATE SPECIFIC PARTITIONS
CREATE TABLE Orders (col2 int, col2 int, col3 int etc...)
WITH
(
  CLUSTERED COLUMNSTORE INDEX,
  DISTRIBUTION = ROUND_ROBIN,
  -- DISTRIBUTION = HASH(col1)
  PARTITION
  (
      OrderDate RANGE RIGHT FOR VALUES
      (
               pierwszy pusty dla < 2017         -- przed 5 lutego   parti.(1)
             ,  '2017-02-05T00:00:00.000' ,      -- od 5 do 12             (2)
             ,  '2017-02-12T00'                  -- od 12 wlacznie do 19   (3)
            ,   '2017-02-19T00:00'               -- od 19 wlaczne i dalej  (4)
       )
      -- OrderDate RANGE RIGHT FOR VALUES ( 20130101, 20140101, 20150101, 20160101 )
	)
)
-- CTAS version (bez definicji w Orders powyzej)
-- AS SELECT * FROM some_external_table ;



-- STATISTICS
CREATE STATISTICS col1_stat ON dbo.table1 (col1) ;
CREATE STATISTICS col1_stat ON dbo.table1 (col1) WITH FULLSCAN;
CREATE STATISTICS col1_stat ON dbo.table1 (col1) WHERE col1 > '20000101' AND cols < '20001231' ;
UPDATE STATISTICS dbo.table1 (col1_stat) ;
UPDATE STATISTICS dbo.table1 ;


-- LABELS
SELECT ...FROM...WHERE...
OPTION ( LABEL = 'procuct : step 01 : CTAS dim_xxx : My Label for this query' )
-- pozniej mozna szukac
SELECT * FROM  sys.dm_pdw_exec_requests WHERE [label] LIKE 'Exercise%'
SELECT * FROM  sys.dm_pdw_exec_requests WHERE [label] = 'STATEMENT:DemoQuery'



-- ELASTIC QUERY ( to execute 
1. create login in master DB !!!
CREATE LOGIN salesLogin WITH PASSWORD = 'password123' ; 
GO
-- now in destination DB
CREATE USER salesUser FOR LOGIN salesLogin
GO
-- CREATE ROLE dw_admin
-- GRANT ALTER, CONTROL, DELETE, EXECUTE, INSERT, SELECT, UPDATE, TAKE OWNERSHIP, VIEW DEFINITION ON SCHEMA::dbo TO dw_admin ; 
-- EXECUTE sp_addrolemember N'dw_admin' N'salesUser'
-- EXECUTE sp_addrolemember N'DBMANAGER', N'salesUSer'
-- EXECUTE sp_addrolemember N'LOGINMANAGER', N'salesUser'
-- SELECT * FROM sys.database_principals 
GRANT SELECT ON SCHEMA :: [dbo] TO salesUser
GO



-- creating external (elastic) data source POLYBASE
-------------------------------------
-- create db master key (used to encrypt other keys and certs)
CREATE MASTER KEY ENCRYPTION BY PASSWORD = 'password123' ;
-- create db scroped cred to auth against Azure storage account
CRATE DATABASE SCOPED CREDENTIAL AzureStorageCredential
WITH IDENTITY = 'name_of_azure_storage_account' ,
SECRET = '<storageAccountKey1' ;

SELECT * FROM sys.database_credentials ;
-- 1
CREATE EXTERNAL DATA SOURCE AzureStorage1
WITH (
	TYPE = Hadoop,
	LOCATION = 'wasbs://name@name.blob.core.windows.net',
	CREDENTIAL = AzureStorageCredential);
-- 2
CREATE EXTERNAL FILE FORMAT TextFile1
WITH (
  FORMAT_TYPE = DelimitedText,
  FORMAT_OPTIONS (FIELD_TERMINATOR = '|')
);
-- 3
CREATE EXTERNAL TABLE dbo.FactTableExternal (
	prodkey [int] NOT NULL,
	line [tinyint] NULL, revision [smallint] NULL,  rice [money] NULL, sales [money] NULL, sth [nvarchar](25) NULL, order [datetime] NULL
)
WITH (
	LOCATION = '/myFile.txt',
	DATA_SOURCE = AzureStorage1,    -- can be Hadoop, Azure Blob Storage, ShardMapManager
	FILE_FORMAT = TextFile1
)
-- 4 -- CTAS dbo.newtable HERE....then need to rebuild table
CREATE TABLE dbo.final
WITH (
	DISTRIBUTION = ROUND_ROBIN,
	CLUSTERED COLUMNSTORE INDEX)
AS SELECT
	col1, CAST(col2Ts AS DATETIME) AS col2DateTime
FROM dbo.FactTableExternal

ALTER INDEX ALL ON dbo.newtable REBUILD;

https://blogs.msdn.microsoft.com/sqlcat/2017/05/17/azure-sql-data-warehouse-loading-patterns-and-strategies/



/////////////////////////////////////////////
/////////////////////////////////////////////
-- while connecting to dbo.master
CREATE LOGIN userx WITH PASSWORD = 'pass'
-- you need to connect manually to another db
CREATE USER userx FOR LOGIN userx
GO
EXEC sp_addrolemember 'db_datareader' , 'userx'
EXEC sp_addrolemember 'db_datawriter' , 'userx'
EXEC sp_addrolemember 'db_ddladminr' , 'userx'



DIMENSION = ROUND_ROBIN
FACT = HASH