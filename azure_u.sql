DECLARE @postcodesPath string = "/postcodes/codes.csv";
DECLARE @tsvURL string = "https://raw.githubusercontent.com/Azure/usql/master/Examples/Samples/Data/SearchLog.tsv"
DECLARE @outputPath = "/output/path.csv";
DECLARE @x = "/output/path_some_{variable}.csv"; // can be later used in EXTRACT variable string;
DECLARE @a string = "BEGIN" + @text1 + "END";
DECLARE @b string = string.Format("BEGIN{0}END", @text1);
DECLARE @c string = MyHelper.GetMyName();
DECLARE @e Guid = System.Guid.Parse("XXX-XXX-XXX-XXX");
DECLARE @f byte [] = new byte[] {0,1,2,3,4};
 
// EXTRACT
@postcoderows =
    EXTRACT [1 1 1 1 1 1] string // WITH HEADER !!!
    EXTRACT Postcode string,
            PersonTotal int,
            DistinctName string,
                                                __filenum string
    FROM @postcodesPath // or table without @       /input/{*}.csv      /input/prefix{__filenum}
    USING Extractors.Csv(skipFirstNRows : 1, quoting : false. encoding: Encoding.Unicode, nullEscape:"#null#");                     // https://msdn.microsoft.com/en-us/azure/data-lake-analytics/u-sql/extractor-parameters-u-sql
                // https://saveenr.gitbooks.io/usql-tutorial/content/reading-and-writing-files/text-encoding.html
 
// TRANSFORM
@sumresult = SELECT Postcode.Substring(0,4) AS PostcodeArea,
        SUM(PersonTotal) AS NoPersons,
        (Postcode.IndexOf(" ") == -1) ? Postcode : Postcode.Substring(0, Postcode.IndexOf(" ")) AS PostcodeArea2,            // if there is a space
        Math.Round((decimal)AVG((decimal)column1, 2) AS MaleAverage),
        new SQL.ARRAY<string>(tweet.Split(' ').Where(x => x.StartsWith("@"))) AS refsx,
        new SQL.ARRAY<string>(ARRAY_AGG(col1).SelectMany(x => x).Distinct()) AS file_origin,
        SqlArray.Create(Urls.Split(';')) AS UrlTokens
        ARRAY_AGG(DISTINCT col2) AS col2,
        ARRAY_AGG<string>(Url).ToArray() AS UrlArray   // aggregate array type to .net
    FROM @postcoderows AS p
        a. WHERE Postcode NOT IN ("ONLY", "STATIC", "VALUES") // as equivalent LEFT JOIN is needed with  null                                                                                                          
        b. LEFT JOIN tabx AS pd
            ON p.Postcode == pd.Postcode
            WHERE p.Postcode IS NULL;
        c. LEFT ANTISEMIJOIN tabx AS pd //  or without ANTI
            ON p.Postcode == pd.Postcode
    GROUP BY Postcode.Substring(0,4);
 
@t = SELECT r.Substring(1) AS r
        FROM @sumresults CROSS APPLY EXPLODE(refsx) AS Refs(r)  // https://saveenr.gitbooks.io/usql-tutorial/content/grouping-and-aggregation/cross-apply-and-array_agg.html
@c = SELECT Token as Url
        FROM @sumresult CROSS APPLY EXPLODE(UrlTokens) AS r(Token)
@arr_to_string = SELECT string.Join(";", UrlArray) AS Urls   // .net array to string   A;B;C
 
// SAVE
OUTPUT @sumresult
TO @outputPath
ORDER BY PostcodeArea ASC
FETCH 10 ROWS
USING Outputters.Text(outputHeader:true, quoting:true, , delimiter : '|') ;  //char param so in ' ',   string in ""


/////////////////////////////////////////// DATETIME //////////////
DECLARE @date1 DateTime = new DateTime(2014, 9, 14, 00, 00, 00, 00, DateTimeKind.Utc);
DECLARE @default_dt DateTime = Convert.ToDateTime("06/01/2016");
DECLARE @d = DateTime.Parse("2016/01/01");
DECLARE @dt = "2016-07-06 10:23:15";

@x = EXTRACT fileid int, date DateTime
FROM "/Cool_file{fileid}_{date:yyyy}{date:MM}{date:dd}.txt"   // date is virtual file set column
USING Extractors.Csv();
 
@rs1 =
  SELECT
    DateTime.Parse(date) AS date,
    DateTime.Now.ToString("M/d/yyyy") AS Nowdate,
    MIN(Convert.ToDateTime(Convert.ToDateTime(dt<@default_dt?@default_dt:dt).ToString("yyyy-MM-dd"))) AS start_zero_time,
    Convert.ToDateTime(Convert.ToDateTime(@dt).ToString("yyyy-MM-dd")) AS dt,
  FROM @rs0
  WHERE date >= DateTime.Parse("2014-08-31") AND date < DateTime.Parse("2014-10-31")  // use &&
  AND date BETWEEN DateTime.Parse("2018-01-01" AND DateTime.Parse("2019-01-01")
 

// UDF FUNCTION    https://docs.microsoft.com/en-us/azure/data-lake-analytics/data-lake-analytics-u-sql-programmability-guide#use-user-defined-functions-udf
@x = SELECT
    MIN(USQL_Programmability.CustomFunctions.GetFiscalPeriod(dt)) AS start_fiscalperiod
    FROM @y
 
//UDAGGs, are User Defined AGGregators    AGG<...>

// if null, pick a default value, otherwise use the value, The ?? is the C# null coalescing operator.
stringcol1 ?? "defval"
// if an expression is true, pick one value, otherwise pick a different one
<expr> ? "was_true" : "was_false"
// Test if null
<expr> == null : "was_false"
// case INsensitivity
string.Equals( "Abc",  "ABC",  System.Text.StringComparison.CurrentCultureIgnoreCase )
 
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ADLAU - Azure Data Lake Analytics Unit = Vertex = Server 1 core, 6 GB RAM.   1 vertex approx 700 MB csv data
// serialized = 1 parallel job
 
// OPTION 1. create table with index in one go
USE DATABASE master;
USE SCHEMA dbo;
DROP TABLE IF EXISTS t;
TRUNCATE TABLE xxxx;
CREATE TABLE IF NOT EXISTS t

(
    ID int IDENTITY(1,1) NOT NULL,
    col1 string,
    col2 float,
    col3 SQL.MAP<string,string> ,
    date DateTime
    INDEX idx_col1
    CLUSTERED (date DESC, col1 ASC)  // within each distribution keep rows together that have same value
    PARTITIONED BY (name, YearReported)         // physically partition into files by columns
    DISTRIBUTED BY HASH (col1,  ifrequiredCOL2, ifrequiredCOL3) //distr rows into at most 5 buckets inside
                                                                //each clustered file based on hash
    INTO 5  // number of buckets, keep 1 GB per bucket, so 4.3 GB = 5 buckets
);

// OPTION 2. by creating index separately
CREATE TABLE IF NOT EXISTS t2
(
    col1 string,
    col2 string
);
CREATE CLUSTERED INDEX idx_col2
ON table1(col1 ASC)
DISTRIBUTED BY
HASH(col1);
 
// OPTION 3. CTAS
CREATE TABLE Table1
    (
        INDEX idx CLUSTERED(Year) DISTRIBUTED BY HASH(Year)
    )
    AS SELECT * FROM master.dbo.SearchTableExample ; // FROM @weblog
    AS EXTRACT ...;
    AS myTVF(DEFAULT);
 
 
//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
// HORIZONTAL partitioning - fine grained, works on smaller data, similar values kept close together
// partitioning  - HOW data are stored   vertical
// distribution - WHERE is it stored     horizontal
// 1. hash (keys)
INSERT INTO tab1 (col1, col2) SELECT (col1, col2) FROM @tab2;
// 2. direct hash (ID)
// CLUSTERED(col1, CurrentFileDate DESC)   -- supported sorting, nastepne tez
// in definition we need e.g. HashValue long  , defined by developer
// then in select mozna uzyc jako tej kolumny np:
SELECT ( Convert.ToInt64(DENSE_RANK() OVER( ORDER BY name, date, crimeType)) AS HashValue
// 3. range, good for data with ranges, e.g. Dates or numeric
// here we used instead of distributed by hash, this:
DISTRIBUTED BY RANGE(date, name, CrimeType)
// 4. round robin (equal data size, evenly , e.g. 20000 rows), slower, but helps avoiding data skew
DISTRIBUTED BY ROUND ROBIN INTO 20
 
// VERTICAL partitioning - cause-grained, manual, , in hands of developer, define buckets, ostatniaKolumna int,
INDEX idx_somename
CLUSTERED (date DESC, type)                                     // cannot use name here if used already in partitioned by
PARTITIONED BY HASH (name)                                                  // individual table within table
PARTITIONED BY BUCKETS(event_date) HASH (name) INTO 4;
//DISTRIBUTED BY HASH (type)                                  // may use Round Robin if data skewed, otherwise Hash is good
//INTO 5

// loading data into partitions statically    (partitions must exist)
IF (!PARTITION.EXISTS(table1, "parttionName"))
THEN ALTER TABLE table1 ADD PARTITION("partitionName"), PARTITION (@date1); END;  // pozniej WHERE name == "partitionName"   //alter table drop part("n")
INSERT INTO tab1 (col1, col2)
ON INTEGRITY VIOLATION MOVE TO PARTITION("unknown")
SELECT (col1, col2) FROM @tab2 WHERE name == "unknown";

// loading data into partitions dynamically
INSERT INTO tab1 (col1, col2)
ON INTEGRITY VIOLATION IGNORE /*MOVE TO PARTITION("unknown")*/
SELECT (col1, col2) FROM @tab2 WHERE name == "unknown" AND event_date <= @date1;
 

//INSERT
INSERT INTO TAB VALUES (1, "text", new SQL.MAP<string,string>("key","value"));
INSERT INTO TAB SELECT col1,col2,col3 FROM @rowset;
ALTER TABLE TAB REBUILD;    //multiple inserts

TRUNCATE TABLE table1 PARTITION("unknown");
 
ALTER TABLE table1
ADD PARTITION("avon-and", 2015),
    PARTITION("avon-and", 2016),
    PARTITION("unknown", 1);
 
// after frequent table insertions rebuild with
ALTER TABLE REBUILD


//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
// supported types string, int, bool
// https://saveenr.gitbooks.io/usql-tutorial/content/declare-parameters/supported-datatypes.html
 
// ASSEMBLIES
CREATE ASSEMBLY CustDB.SampleCode FROM @"/DLLs/Helpers.dll";   // or FROM byte[]
REFERENCE ASSEMBLY CustDB.Helpers;   //  REFERENCE SYSTEM ASSEMBLY [System.xml]

@rows = SELECT UKpostCode.Helpers.Normalize(Customer) AS Customer FROM @rows   // CustDB.Helpers.Udfs.get_nxxx

@d1 = PROCESS @d PRODUCE col1 string, col2 string USING new USQLApplication_codebehind.MyProcessor()


// PYTHON CODE
https://github.com/Azure-Samples/usql-python-hello-world

// VIEWS
CREATE VIEW V AS EXTRACT ...
CREATE VIEW V AS SELECT ...

// TABLE-VALUED FUNCTIONS (TVF)
DROP FUNCTION IF EXISTS Regio;
CREATE FUNCTION SampleDBTutorials.dbo.WeblogsView()
CREATE FUNCTION RegioTVF(@region string = "en-gb")
RETURNS @result
/* TABLE
    ( UserId int, Start DateTime)   */    // this is optional!
AS
BEGIN
@result = EXTRACT UserId int, Start DateTime
            FROM @"/Samples/Data/WebLog.log"
            USING Extractors.Text(delimiter: ' ');
            RETURN;
@result2= SELECT * FROM table1 WHERE Region == @region;
END;
 
//uzycie funkcji
@weblog = SampleDBTutorials.dbo.WeblogsView();  //https://docs.microsoft.com/en-us/azure/data-lake-analytics/data-lake-analytics-analyze-weblogs
@weblog2= SELECT * FROM SampleDBTutorials.dbo.WeblogsView();
@rs1 = SELECT UserId, Start FROM RegioTVF(DEFAULT) AS S;
OUTPUT @rs1 TO "..." USING Outputters.Csv();
 
 
 
// PROCEDURES
CREATE PROCEDURE P (@arg string = "default")
AS BEGIN ... ;
  OUTPUT @res TO ...;
  INSERT INTO T ...;
END;
 
// TABLE TYPES
CREATE TYPE T AS TABLE(c1 string, c2 int);
 
CREATE FUNCTION F (@table_arg T)
RETURNS @res T
AS BEGIN ... @res = ... END;
 
 
 
///////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////
 
CREATE CREDENTIAL nameX WITH USER_NAME = "user@server", IDENTITY="pass"
// create external data source on
CREATE DATA SOURCE SQL_PATIENTS FROM SQLSERVER WITH
( PROVIDER_STRING = "Database=DB;Trusted_Connection=False;Encrypt=False", CREDENTIAL=nameX, REMOTABLE_TYPES=(bool,byte,short,string,DateTime));
 
CREATE EXTERNAL TABLE sql_patients ( cust int, name string ) FROM SQL_PATIENTS LOCATION "dbo.patients";
 
// pass-through to execute remote language
@alive_patients = SELECT * FROM EXTERNAL SQL_PATIENTS EXECUTE @"
                 SELECT name, CASE WHEN is_alive=1 THEN 'Alive' ELSE 'Deceased' END AS status FROM dbo.patients";
//schema-less
@patients = SELECT * FROM EXTERNAL master.SQL_PATIENTS LOCATION "dbo.patients";

//schematized
@patients = SELECT * FROM EXTERNAL master.dbo.sql_patients;



//////////////////////////////////////////////////////////////////////////////////////////////////
@bands = 
  SELECT * 
  FROM (VALUES ("Beatles", "George Harrison, John Lennon, Paul McCartney, Ringo Starr"), 
               ("Creedence Clearwater Revival", "Doug Clifford, John Fogerty, Stu Cook, Tom Fogerty"), 
               ("Eagles", "Don Henley, Glenn Frey, Joe Walsh, Timothy Schmit"), 
               ("Pink Floyd", "David Gilmour, Nick Mason, Richard Wright, Roger Watters, Syd Barrett"), 
               ("Rolling Stones", "Charlie Watts, Keith Richards, Mick Jagger, Ronnie Wood")) AS Bands(name, members);

@ca_val1  = SELECT * FROM @bands CROSS APPLY VALUES (100,200,300,400,500) AS T(x);  // Col0 - key, Col1 - value, Col2 - 100, Col3 - NULL (w sumie 5 rows)
@ca_val12 = SELECT * FROM @bands CROSS APPLY VALUES (100,200) AS T(x,y);            // Col0 - key, Col1 - value, Col2 - 100, Col3 - 200, Col4 - NULL (w sumie 5 rows)
// tyle ile nawiazow, tyle bedzie nowych kolumn
@ca_val2  = SELECT * FROM @bands CROSS APPLY VALUES (100),(name.Length),(200) AS T(x); // mamy 3 nawiasy, ale jeden AS (x) wiec 3 extra rows per key (Beatles), w sumie 15 rows
@ca_val3  = SELECT * FROM @bands CROSS APPLY VALUES (100,name.Length,200) AS T(x,y,z); // mamy 1 nawias , ale trzy AS(x,y,z) wiec po jednym secie na key (w sumie 5 rows)





# DEBUG
https://youtu.be/3enkNvprfm4
https://docs.microsoft.com/en-us/azure/data-lake-analytics/data-lake-analytics-debug-u-sql-jobs

# SAMPLE FILES ON HTTP
// https://github.com/Azure/usql/tree/master/Examples/Samples/Data
// https://github.com/Azure/usql/tree/master/Examples/Samples/Blogs/MRys/JSON