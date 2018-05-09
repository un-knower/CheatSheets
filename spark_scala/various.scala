import org.apache.spark.sql.{Dataset, DataFrame, Encoders, SaveMode, SparkSession)
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.expressions.scalalang.typed
import org.apache.spark.sql.types.DataTypes.*
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.sql.SparkSession 
import org.apache.spark.SparkConf 
import org.apache.spark.SparkContext 
import org.apache.spark.SparkContext._ 
import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType} 
import org.apache.spark.sql.types._ 
import org.apache.spark.sql.{Row, SparkSession}	// SchemaRDD? 
import org.apache.spark.sql.hive.HiveContext 
import org.apache.spark.sql.SQLContext 
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder 
import org.apache.spark.sql.Encoder 
import org.apache.spark.sql.SaveMode 
import org.apache.spark.sql._ 
import org.apache.spark.sql.functions._ 
import org.apache.spark.sql.types._ 
import org.apache.spark.sql.functions.udf
import java.io.File 
import scala.io.Source._ 
import scala.collection.JavaConverters._ 
import org.apache.spark.util.Utils 
import java.util.Random 
import org.apache.hadoop.io.LongWritable 
import org.apache.hadoop.io.Text 
import org.apache.hadoop.conf.Configuration 
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat 


// ***************************** PARAMETERS ********************************
val conf = new SparkConf()  //  .setMaster("local").setAppName("My App") 
conf.set("spark.app.name", "My Spark App") 
conf.set("spark.master", "local[4]") 
conf.set("spark.ui.port", "36000") // Override the default port 
conf.set("spark.sql.codegen", "true")  // PERFORMANCE options 
conf.set(“spark.io.compression.codes”, “lzf”)
conf.set(“spark.speculation”,”true”)
spark.conf.set("spark.sql.shuffle.partitions", 10) 
spark.conf.set("spark.executor.memory", "2g")

//get all settings
val configMap:Map[String, String] = spark.conf.getAll()
sc.getConf.toDebugString


val sc = new SparkContext(conf) 
val hiveCtx = new HiveContext(sc) 
val sqlCtx  = new SQLContext(sc) //depreciated 
import hiveCtx._ 
 
object myApp { 
  def main(args: Array[String]) { 
	val sparkSession = SparkSession
        .builder() 
        .master("local") 
        .appName("example") 
        .config("spark.some.option", "true") 
        .config("spark.sql.warehouse.dir", warehouseLocation)   //hive 
        .enableHiveSupport()                                	//hive 
        .getOrCreate() 

val sc = new SparkContext(master, "GeoIpExample". System.getenv("SPARK_HOME"), Seq(System.getenv("JARS")))
sparkSession.setConf("xxxx", "yyyy") 

val sparkContext = sparkSession.sparkContext 
import sparkSession.implicits._




// configuration parameters
spark.sql.retainGroupColumns = false
reader.option("wholeFile", true) //json
reader.option("samplingRatio", (0...1.0))
--driver-class-path PATH/TO/JAR
//bit.ly/2u3frhN





--master        Indicates the cluster manager to connect to. e.g.  spark://host:port,  yarn,   local,   local[*] 
--deploy-mode   Whether to launch the driver program locally (“client”) or on one of the worker machines inside the cluster (“cluster”). In client mode spark-submit will run your driver on the same machine where spark-submit is itself being invoked. In cluster mode, the driver will be shipped to execute on a worker node in the cluster. The default is client mode. 
--class         The “main” class of your application if you’re running a Java or Scala program. 
--name          A human-readable name for your application. This will be displayed in Spark’s web UI. 
--conf          --conf spark.ui.port=36000 
--hiveconf      for hive options 
--properties-file  default:  conf/spark-defaults.conf 
                    // ## Contents of my-config.conf ## w przypadku zmiany 
                    spark.master local[4] 
                    spark.app.name "My Spark App" 
                    spark.ui.port 36000 
                    spark.speculation false (default false)  - true will allow re running slow tasks 
                    spark.executor.extraJavaOptions="-XX:+PrintGCDetails-XX:+PrintGCTimeStamps" 
                    spark.eventLog.enabled false (req. history server) 
                    spark.eventLog.dir hdfs:///path.. 
--jars          A list of JAR files to upload and place on the classpath of your application. If your application depends on a small number of third-party JARs, you can add them here. e.g. --jars dep1.jar,dep2.jar,dep3.jar 
--files         A list of files to be placed in the working directory of your application. This can be used for data files that you want to distribute to each node. 
--py-files      A list of files to be added to the PYTHONPATH of your application. This can contain .py, .egg, or .zip files. 
--executor-memory   The amount of memory to use for executors, in bytes. Suffixes can be used to specify larger quantities such as “512m” (512 megabytes) or “15g” (15 gigabytes).  [spark.executor.memory] 
--driver-memory 
The amount of memory to use for the driver process, in bytes. Suffixes can be used to specify larger quantities such as “512m” (512 megabytes) or “15g” (15 gigabytes). 
--total-executor-cores      Standalone mode ,   --total-executor-cores 300, consolidate executors by property:  spark.deploy.spreadOut to false ( 
 

ASSEMBLY_JAR=./target/scala-2.10/examples_2.10.jar 
CLASS="com.highperformancespark.dataframe.mysqlload" 
spark-submit --jars ./resources/mysql-connector-java-5.1.38.jar $ASSEMBLY_JAR $CLASS  
spark-submit(shell) --master spark://masternode:7077  --class com.example.MyApp --name "My App" myApp.jar "options" "to" "app" 

 
SPARK_LOCAL_DIRS (conf/spark-env.sh) - comma separated storage locations for shuffle data 


in YARN: 
--num-executors         def 2 
--executor-memory 
--executor-cores  
--queue 
 
export HADOOP_CONF_DIR=HADOOP_HOME/conf 
spark-submit --master yarn yourapp   # in client mode only 



// ***************************** LOGGING ***********************************

import org.apache.log4j.Logger 
import org.apache.log4j.Level 
import org.apache.log4j.LogManager   // zeby drukowac logi
 
Logger.getLogger("org").setLevel(Level.OFF) 
 
	def setupLogging() = { 
	import org.apache.log4j.{Level, Logger} 
	val rootLogger = Logger.getRootLogger() 
	rootLogger.setLevel(Level.ERROR) 
  }
spark = new SparkContext(sc)
c

val LOGGER = LogManager.getLogger(this.getClass.getName)
LOGGER.error(“jakis tam error”)












case class Account(number: String, firstName: String, lastName: String)
case class Transaction(id: Long, account: Account, date: java.sql.Date, amount: Double, description: String)
case class TransactionForAverage(accountNumber: String, amount: Double, description: String, date: java.sql.Date)

object Detector {
    def main(args: Array[String]) {
        val spark = SparkSession
        .builder
        .master("local")
        .appname("Fraud Detector")
        .config("spark.driver.memory", "2g")
        .enableHiveSupport
        .getOrCreate()
        
    import spark.implicits._
    val financesDS = spark.read.json("Data.json")
                    .withColumn("date", to_date(unix_timestamp($"Date","MM/dd/yyyy")
                    .cast("timestamp"))).as[Transaction]
    
    
    financesDS
        .na.drop("all"), Seq("ID","Account","Amount","Description","Date")
        .na.fill("Unknown", Seq("Description")).as[Transaction]  //na  method are untyped
        //.filter(tx=>(tx.amount != 0 || tx.description == "Unknown"))  //more type-safe
        .where($"Amount" =!= 0 || $"Description" === "Unknown")     //but same as this
        //.selectExpr("Account.Number as AccountNumber", "Amount", 
        //            "to_date(CAST(unix_timestamp(Date, 'MM/dd/yyyy') AS TIMESTAMP)) AS Date",
        //             to_date(unix_timestamp($"Date","MM/dd/yyyy").cast("timestamp")).as("Date")) //DSL
        .select($"Account.number".as("AccountNumber").as[String],
                $"Amount".as[Double],
                $"Date".as[java.sql.Date](Encoders.DATE),
                $"Description".as[String])
        .withColumn("RollingAverage", rollingAvgForPrevious4PerAccount)
        .write.mode("SaveMode.Overwrite").parquet("Output/small")
        
    if(financesDS.hasColumn("_corrupt_record")) {
        financesDS.where($"_corrupt_record".isNotNull)
        .select($"_corrupt_record)
        .write.mode(SaveMode.Overwrite).text("Output/corrupt_finance")
    }
    
    financesDS
        //.select(concat($"Account.FirstName", lit(" "), $"Account.LastName").as("Fullname"), $"Account.Number".as("AccountNumber"))
        .map(tx=> (s"${tx.Account.firstName} ${tx.Account.lastName}", tx.Account.number))  // become  _1,  _2
        .distinct  // .dropDuplicates(Seq("col1", "col2", ...)
        .toDF("FullName", "AccountNumber")
        .coalesce(5)  //reduce to 5 partitions
        .write.mode(SaveMode.Overwrite).json("Output/small")
    
    financeDS.
        //.select($"Account.Number".as("AccountNumber"), ...)
        .select($"Account.number".as("accountNumber").as[String],
                $"Amount".as[Double],
                $"description".as[String],
                $"date".as[java.sql.Date](Encoders.DATE)).as[TransactionForAverage]
        //.groupBy($"AccountNumber")  // safer than RDD groupBy
        .groupByKey(_.accountNumber)   //value is entire object (line above)
        //.flatMapGroups
        //.reduceGroups((val1, val2) => reductionFunctionFor(val1, val2)) // Dataset[(KeyType, ValueType)]
        //.mapValues(value => mappingFunction(value))   //Dataset[(KeyType, ValueType)]
        
        // agg(Map("Amount"->"avg", "Amount"->"sum", ...))
        //.agg(avg($"Amount").as(...), sumDistinct(...), countDistinct(...),
        //    collect_set($"Description").as("UniqueTransactionDescriptions"),
        //    collect_list($"Description").as("DuplicateInside"))
        
        /*.agg(
            typed.avg[TransactionForAverage](_.amount).as("AverageTransaction").as[Double],
            typed.sum[TransactionForAverage](_.amount),
            typed.count[TransactionForAverage](_.amount),
            max($"Amount").as("MaxTransaction").as[Double])   //untyped, but must specify type
            // limit 4 in total (!!!!)
            //returns  Dataset[(KeyType, Agg1type, ....Agg4type)]
        */
        // not usable due to limit of 4
        .mapGroups((key, transactions) => (key, transactions.map(_.amount).sum))
        .coalesce(5)
        .write.mode(...)

    // **************** WINDOW WINDOWING BASED FUNCTIONS ************
    import org.apache.spark.sql.expressions.Window
    val accountNumberPrevious4WindowSpec = Window.partitionBy($"AccountNumber")
                                                .orderBy($"Date").rowsBetween(-4,0)  // .rangeBetween( based on value )
    // Long.MinValue = Window.unboundedPreceding
    // Long.MaxValue = Window.unboundedFollowing
    // 0 = Window.currentRow
    val rollingAvgForPrevious4PerAccount = avg($"Amount").over(accountNumberPrevious4WindowSpec)

        
    financeDetails = spark.read.json("...")
    financeDetails.select(array_contains($"someList/Set", "value").as("WentToMovies")).where(!$"WentToMovies").show()
                                                                                       not(col("WentToMovies"))
                         , size($"UniqueSET") // display set size
                         , sort_array($"Unique...", asc=false).as(...)
    }
        
    //implicit class DataFrameHelper(df: DataFrame) {
    implicit class DataFrameHelper[T](ds: Dataset[T]) {    // typed
        import scala.util.Try
        def hasColumn(columnName: String) = Try(df(colulmName)).isSuccess
    }
}

scala> :pa    PASTE MODE
val jsonCompanies = List(
"""{"company":"NewCo", "employees":[...]}"""






// SESSIONS
spark.newSession  // creates copy of session, separate from previous
SparkSession.setActiveSession(copiedSession)




// ********************  CREATE DATA FRAME *******************************
tempDF = sqlcontext.createDataFrame(List(("Justin", 100), ("SampleUser", 70)))
// [_1: string, _2: int]
namedDF = tempDF.toDF("UserName", "Score")
// [Username: string, Score: int]
tempDF = sqlContext.createDataFrame([("Joe", 1), ("Anna", 15), ("Anna", 12), ('name', 'score'))
>>> l = [('Alice', 1)]
>>> sqlContext.createDataFrame(l).collect()
[Row(_1=u'Alice', _2=1)]
>>> sqlContext.createDataFrame(l, ['name', 'age']).collect()
[Row(name=u'Alice', age=1)]

>>> d = [{'name': 'Alice', 'age': 1}]
>>> sqlContext.createDataFrame(d).collect()
[Row(age=1, name=u'Alice')]

>>> rdd = sc.parallelize(l)
>>> sqlContext.createDataFrame(rdd).collect()
[Row(_1=u'Alice', _2=1)]
>>> df = sqlContext.createDataFrame(rdd, ['name', 'age'])
>>> df.collect()
[Row(name=u'Alice', age=1)]

>>> from pyspark.sql import Row
>>> Person = Row('name', 'age')
>>> person = rdd.map(lambda r: Person(*r))
>>> df2 = sqlContext.createDataFrame(person)
>>> df2.collect()
[Row(name=u'Alice', age=1)]

from pyspark.sql.types import *CREATE
schema = StructType([
    StructField("name", StringType(), True),
    StructField("age", IntegerType(), True)])
df3 = sqlContext.createDataFrame(rdd, schema)
df3.collect()
// [Row(name=u'Alice', age=1)]

sqlContext.createDataFrame(df.toPandas()).collect()  # doctest: +SKIP
// [Row(name=u'Alice', age=1)]
sqlContext.createDataFrame(pandas.DataFrame([[1, 2]])).collect()  # doctest: +SKIP
// [Row(0=1, 1=2)]

df = sc.parallelize([(1, "foo"), (2, "x"), (3, "bar")]).toDF(("k", "v"))

//
val rdd = sc.parallelize(  Row(52.23, 21.01, "Warsaw") :: Row(42.30, 9.15, "Corte") :: Nil)
val schema = StructType(
    StructField("lat", DoubleType, false) ::
    StructField("long", DoubleType, false) ::
    StructField("key", StringType, false) ::Nil)
val df = sqlContext.createDataFrame(rdd, schema)

//
from pyspark.sql import Row
>>> df = sc.parallelize([ Row(name='Alice', age=5, height=80),Row(name='Alice', age=5, height=80),Row(name='Alice', age=10, height=80)]).toDF()

val companiesRDD = spark.sparkContext.makeRDD(jsonCompaniesLISTA)
val companiesDF  = spark.read.json(companiesRDD)

import org.apache.spark.sql.types._
val mySchema = 
    StructType(
        StructField("keyValueMap", ArrayType(MapType(StringType, IntegerType))) :: Nil)
df.read.schema(mySchema).json("PATH")


val employeesDF = empTemp.select($"company", expr("employee.firstName as firstName"))
// org.aapache.spark.sql.DataFrame = [company: string, firstName: string]

// CASE WHEN (company=FamilyCo) THEN Premium WHEN (company = OldCo) THEN Legacy ELSE Standard END
employeesDF.select($"*", when($"company"==="FamilyCo", "Premium").when($"company"==="OldCo", "Legacy").otherwise("S"))

posexplode($"employees").as(Seq("employeePosition", "employee"))).show

// **********************  DATA SET ***************
val schema = StructType(List(
    StructField("test", BooleanType, true)))
val rdd = spark.sparkContext.parallelize(List(Row(0), Row(true), Row("stuff")))
val df = spark.createDataFrame(rdd, schema)
df.collect  // it will fail only during ACTION !
val ds = spark.createDataset(rdd, schema)  // will fail instantly, that's DF vs DS
toLocalIterator , same as collect but takes less space (just as much as biggest partition)






// ***************************************


TIPS
Compatibility with older systems: 
sqlContext.setConf("spark.sql.parquet.binaryAsString", "true")          [set spark.sql.parquet.binaryAsString=true]

Finding out IDE or other input parameters, so we can run config
   //check if running from IDE
	println("Input args: " +ManagementFactory.getRuntimeMXBean.getInputArguments.toString())
	if (ManagementFactory.getRuntimeMXBean.getInputArguments.toString().contains("eclipse")) {
  	conf.setMaster("local[*]")
	}


Val DF = sqlContext.read.format("parquet").load("hdfs://lambda-host/user/cloudera/batch1/")
Val DF = sqlContext.read.parquet("hdfs://lambda-host/user/cloudera/batch1/")
Val DF = sqlContext.sql("SELECT * FROM parquet.`hdfs://lambda-host/user/cloudera/batch1/` WHERE page_view_count > 2")

Specify a JSON schema: val jsonSchema = sqlContext.read.json(sc.parallelize(Array("""{"string":"string1","int":1,"dict": {"key": "value1"}}""")))
val testJsonDataWithoutExtraKey = sqlContext.read.format("json").schema(jsonSchema.schema).load("/tmp/test.json")
display(testJsonDataWithBadRecord.where("_corrupt_record is not null"))






// ******************************  UDF  ********************************** // org.apache.spark.sql.java.api.UDF1 - UDF22 (# of params!)
// OPTION 1
spark.udf.register("capitalizeFirstLetter", (fullString: String) => fullString.split(" ").map(_.capitalize).mkString(" ")) 
sampleDF.select($"id", callUDF("capitalizeFirstLetter", $"text").as("text")).show
// OPTION 2
val capitalizerUDF = udf((fullString: String, splitter: String) => fullString.split(splitter).map(_.capitalize).mkString(splitter))
sampleDF.select($"id", capitalizerUDF($"text",      " ").as("text")).show   // WRONG !! require column type!
sampleDF.select($"id", capitalizerUDF($"text", lit(" ")).as("text")).show   // OK

// SMART JOINING
// UDF is a black box for Spark, so it needs to scan whole set
val  eqUDF = udf( (x:Int, y:Int) => x == y)
df1.join(df2, eqUDF($"x", $"y"))   // this is CARTESIAN + FILTER    n^2   , super bad
df1.join(df2, $"x" === $"y")       // SortMergeJoin                 n log n   optimized, very good!  use build in functions


registerFunction("strLenScala", (_: String).length) 
val tweetLength = hiveCtx.sql("SELECT strLenScala('tweet') FROM tweets LIMIT 10") 
 
Using a Hive UDF requires that we use the HiveContext instead of a regular SQLContext.
To make a Hive UDF available, simply call hiveCtx.sql("CREATE TEMPORARY FUNCTION name AS class.function")  

val upper: String => String = _.toUpperCase
val upperUDF = org.spache.spark.sql.functionsjjjj.udf(upper)

// inna metoda
spark.udf.register("myUpper", (x:String) => x.toUpperCase)
spark.catalog.listFunctions.filter('name like "%upper%").show(false)


 // z istniejacej funkcji
val squared = (s: Int) => { 
  s * s 
} 
sqlContext.udf.register("square", squared)	// albo   squared _) 
  
sqlContext.range(1, 20).registerTempTable("test") 
select id, square(id) as id_squared from test 
 
val volumeUDF = udf {
 (width: Double, height: Double, depth: Double) => width * height * depth
}
ds.withColumn("volume", volumeUDF($"width", $"height", $"depth"))

 // z nowej funkcji
sqlContext.udf.register("CTOF", (degreesCelcius: Double) => ((degreesCelcius * 9.0 / 5.0) + 32.0)) 
sqlContext.udf.register("CTOF", (degreesCelcius: Double) => if (costam ==0) 0 else costam/costam ) 
sqlContext.sql("SELECT city, CTOF(avgLow) AS avgLowF, CTOF(avgHigh) AS avgHighF FROM citytemps").show() 
 
val add_n = udf((x: Integer, y: Integer) => x + y)
df = df.withColumn("id_offset", add_n(lit(1000), col("id").cast("int")))
------------
val last_n_days = udf((x: Integer, y: Integer) => {
  if (x < y) true else false
})
val df_filtered = df.filter(last_n_days(col("date_diff"), lit(90)))
-----------
val toHour   = udf((t: String) => "%04d".format(t.toInt).take(2).toInt )
-----------






// ****************************** FUNCTIONS **************************
// monotonically_increasing_id, input_file_name, spark_partition_id, $"col".explain
financeDF.groupBy(lower($"firstName")).agg(first($"weightKg", true)).show    // true means ignore nulls
financeDF.sort($"weightKg".desc).groupBy(lower($"firstName")).agg(first($"weightKg")).show    // sorting helps
financeDF.sort($"weightKg".desc_nulls_last).groupBy(lower($"firstName")).agg(first($"weightKg")).show    // nulls!
financeDF.sort(desc("weightKg")).groupBy(lower($"firstName")).agg(first($"weightKg", true)).show    // desc(string) not column obj $"..."
financeDF.withColumn("fullName", format_string("%s %s", $"firstName", $"lastName"))  // will overwrite column if already exists
financeDF.withColumn("weightKg", coalesce($"weightKg", lit(0))).show   // replace nulls with 0
financeDF.filter(locate("engineer", lower($"jobType"), startIndex) > 0).show
financeDF.filter(locate("engineer", lower($"jobType")) > 0).show  //returns index of occurrence  0 - nothing found
financeDF.filter(instr(lower($"jobType"), "engineer") > 0).show  //returns index of occurrence  0 - nothing found SAME as above, arguments swapped
financeDF.filter(lower($"jobType").contains("engineer")).show
financeDF.filter(lower($"jobType").isin(List("chemical engineer", "teacher"):_*)).show  //cast to fit variable argument slot of "isin" function

// map
val r = sc.textFile("/Users/akuntamukkala/temp/customers.txt")
val records = r.map(_.split('|'))
val c = records.map(r=>Customer(r(0),r(1).trim.toInt,r(2),r(3)))
c.registerAsTable("customers")



//  ***************************** JOIN ***********************************
personDF.join(roleDF, $"col1" === $"col2", JOIN_TYPE)                       // when column names different
personDF.join(roleDF, personDF("col1") === roleDF("col2"), JOIN_TYPE)       // when column names the same
personDF.join(roleDF, Seq("Id"), JOIN_TYPE)                                 // when column names the same + will avoid duplicated col names in result
personDF.join(roleDF, Seq("Id"), "left_semi") // EXISTS, zostawi tylko DISTINCT rows from left tab, ktore maja odpowiednik z prawej, nie pokaze prawej! 
personDF.join(roleDF, Seq("Id"), "left_anti") // OPPOSITE EXISTS,, zostawi te ktore nie maja odpowiednika w prawej ! 
personDF.join(roleDF) == personDF.join(roleDF, Seq("Id"), "cross") == personDF.crossJoin(roleDF)   // wszystkie mozliwe combinations
// spark.sql.crossJoin.enabled = True

// DATASET JOIN,   uzywamy  joinWith method
case class Person(id:Int, first:String, last:String)
case class Role(id:Int, role:String)
val personDS = List(Person(1,"Justin","X"), Person(2,"joe","X"),Person(3,"aa","Bb")).toDS
val roleDS = List(Role(1,"Manager"), Role(3,"CEO"), Role(4,"Huh")).toDS
val innerJoinDS = personDS.joinWith(roleDS, personDS("id") === roleDS("id"), "inner")  //return Dataset[(Type1,Type2)]
// org.apache.spark.sql.Dataset[(Person,Role)] = [_1: struct<id...>, _2: struct<id:Int, role:string>]
//  _1              _2
// [1,Justing,X]    [1,Manager]
// [3,aa,bb]        [3,CEO]

// Inner join with time range conditions
impressionsWithWatermark.join(
    clicksWithWatermark,
    expr(""" 
        clickAdId = impressionAdId AND 
        clickTime >= impressionTime AND 
        clickTime <= impressionTime + interval 1 minutes    
         """
    ), "inner"
)







// ****************************** WINDOWING *******************************
case class Status(id:Int, customer:String, status:String)
val statuses = spark.createDataFrame(List(Status(1, "Justin", "New"), Status(2, "A", "Open"), Status(3, "B", "Resolved")))
statuses.select($"id", $"customer", $"status", lag($"status", 1, "N/A").over(Window.orderBy($"id").partitionBy($"customer")).as("prevStatus"), 
|                                                          row_number().over(Window.orderBy($"id").partitionBy($"customer"))).show
// lag 1 row back/behing our actual row,   sorted by "id",     jak damy 2 to bedziemy miec wartosc dopiero w trzecim wierszu
//  id  customer    status  prevStatus   rownum
//  1   Justin      New     null        1
//  4   Unknown     New     null        1
//  9   Unknown     Invalid New         2 

// to samo z RANK
    rank()     dense_rank()      percent_rank()
[tie, rank] =1      1          rank/no. of rows
[tie, rank] =1      1
[tie, rank] =1      1
[next,rank] =4      2



// ******************************** STREAMING *****************************
val financeDFwindowed = financeDF.select($"*", window($"Date", "30 days", "30 days", "15 minutes")
// splits Date into window buckets based on 3 params:            length  slide length    offset
financeDFwindowed.groupBy($"window", $"AccountNumber").count.show(truncate=false)
// window                                       ACcount       count
// [2015-03-05 19:15:00, 2015-03-05 20:15:00]   222-333-444     5


import org.apache.spark.sql.functions._
spark.conf.set("spark.sql.shuffle.partitions", "1")
//databricks  generate fake data streams using our built-in "rate stream", that gen. data at a given fixed rate.
val impressions = spark
  .readStream.format("rate").option("rowsPerSecond", "5").option("numPartitions", "1").load()
  .select($"value".as("adId"), $"timestamp".as("impressionTime"))

val clicks = spark
  .readStream.format("rate").option("rowsPerSecond", "5").option("numPartitions", "1").load()
  .where((rand() * 100).cast("integer") < 10)       // 10 out of every 100 impressions result in a click
  .select(($"value" - 50).as("adId"), $"timestamp".as("clickTime"))   // -100 so that a click with same id as impression is generated much later.
  .where("adId > 0")  




// monitoring console  localhost:4040/SQL

app.pluralsight.com/courses/tsql-window-functions
community.modeanalytics.com/sql/tutorial/sql-window-functions
databricks.com/blog/2015/07/15/introducing-window-functions-in-spark-sql
databricks.com/blog/2016/02/09/reshapingdata-with-pivot-in-apache-spark
databricks.com/blog/2015/09/16/apache-spark-1-5-dataframe-api-highlights // UDAggregate Functions
stackoverflow.com/a/32101530/779513
http://data-flair.training/blogs/create-run-first-spark-project-scala-eclipse-without-maven/
http://data-flair.training/blogs/install-deploy-run-spark-2-x-multi-node-cluster-step-by-step-guide/
https://github.com/sryza/simplesparkapp
http://www.nodalpoint.com/development-and-deployment-of-spark-applications-with-scala-eclipse-and-sbt-part-1-installation-configuration/
Spark vs mapred https://www.linkedin.com/pulse/comprehensive-analysis-apache-spark-vs-mapreduce-rassul-fazelat
https://0x0fff.com/spark-architecture-shuffle/
https://0x0fff.com/spark-dataframes-are-faster-arent-they/#more-268
https://0x0fff.com/spark-memory-management/
https://0x0fff.com/spark-architecture/
https://jaceklaskowski.gitbooks.io/mastering-apache-spark/content/spark-sql-aggregation.html
http://www.devinline.com/2016/01/apache-spark-setup-in-eclipse-scala-ide.html


http://ampcamp.berkeley.edu/exercises-strata-conf-2013/index.html
https://databricks-training.s3.amazonaws.com/data-exploration-using-spark-sql.html
https://community.cloud.databricks.com/?o=6367690482937859#notebook/2637480095869071 (kristofer@migm.pl)