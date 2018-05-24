import org.apache.spark.sql.{Dataset, DataFrame, Encoders, SaveMode, SparkSession)
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.expressions.scalalang.typed
import org.apache.spark.sql.types.DataTypes.*
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.sql.SparkSession 
import org.apache.spark.{SparkConf , SparkContext , Accumulator , rdd._, SparkContext._ }
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

// -------------------------------------------------------------------------
// ---------------------------------- PARAMETERS ---------------------------
// -------------------------------------------------------------------------
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
  val sc = new SparkContext(master, "GeoIpExample". System.getenv("SPARK_HOME"), Seq(System.getenv("JARS")))    // zamiast master  -->   local[*]
val hiveCtx = new HiveContext(sc) 
val sqlCtx  = new SQLContext(sc) //depreciated 
import hiveCtx._ 
 
object myApp { 
  def main(args: Array[String]) { 
	val sparkSession = SparkSession
        .builder() 
        .master("local[*]") 
        .appName("example") 
        .config("spark.some.option", "true") 
        .config("spark.sql.warehouse.dir", "file:///C:/temp")   //hive 
        .enableHiveSupport()                                	//hive 
        .getOrCreate() 

    import sparkSession.implicits._
    sparkSession.setConf("xxxx", "yyyy") 

    val sc = sparkSession.sparkContext  // 2.0+
    

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
        .select($"_corrupt_record")
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

// PYTHON
access_log.select("ip", "unixtime", f.count("*").over(Window.partitionBy("ip")).alias("cnt")).limit(5) //counts number of clicks for each IP address, same rows will have same duplicated count
access_log.select("ip", "unixtime", f.count("*").over(Window.partitionBy("ip").orderBy("unixtime")).alias("cnt")).limit(5)
// this allows new set of functions, e.g.  lag() -> returns one before,   or  lead() -> returns one after,       row_number() -> returns row number
user_window = Window.orderBy("unixtime").partitionBy("ip")
access_log.select("ip", "unixtime", f.row_number().over(user_window).alias("count"),
                                f.lag("unixtime").over(user_window).alias("lag"),
                                f.lead("unixtime").over(user_window).alias("lead")) \
            .select("ip","unixtime", (f.col("lead")-f.col("unixtime")).alias("diff")) \
            .where("diff >= 1800 or diff is NULL").limit(5)





scala> :pa    PASTE MODE
val jsonCompanies = List(
"""{"company":"NewCo", "employees":[...]}"""

// PIVOT function
// 1. we need list of column headers in result table, so we take them, e.g. 5 most popular URL
top_url_pd = access_log.groupBy("url").count().orderBy(f.col("count").desc()).limit(1000).toPandas()
// 2. convert to Pandas List
top_url_list = top_url_pd["url"].tolist()           [u'/favicon.ico', u'/header.jpg', ...]
// 3. get most popular IPs 
access_log.groupBy("ip").count().toPandas())  // just for demo of results
// 4. apply pivot function
access_log.groupBy("ip").pivot("url", top_url_list).fillna(0).count().limit(5).toPandas()




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

// FRANK
val colNames = Seq("label", "features")
val df = dataRDD.toDF(colNames: _*)



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






// ----------------------------------------------------------------------------------------------------
// ------------------------------  UDF  ---------------------------------- org.apache.spark.sql.java.api.UDF1 - UDF22 (# of params!)
// ----------------------------------------------------------------------------------------------------
// http://blog.cloudera.com/blog/2017/02/working-with-udfs-in-apache-spark/    UDAF

// OPTION 1 - z nowej funkcji
spark.udf.register("capitalizeFirstLetter", (fullString: String) => fullString.split(" ").map(_.capitalize).mkString(" ")) 
spark.udf.register("myUpper", (x:String) => x.toUpperCase)
sqlContext.udf.register("CTOF", (degreesCelcius: Double) => ((degreesCelcius * 9.0 / 5.0) + 32.0)) 
sqlContext.udf.register("CTOF", (degreesCelcius: Double) => if (costam ==0) 0 else costam/costam ) 
sqlContext.sql("SELECT city, CTOF(avgLow) AS avgLowF, CTOF(avgHigh) AS avgHighF FROM citytemps").show() 

spark.catalog.listFunctions.filter('name like "%upper%").show(false)
sampleDF.select($"id", callUDF("capitalizeFirstLetter", $"text").as("text")).show

// OPTION 2     org.spache.spark.sql.functions.udf
val capitalizerUDF = udf((fullString: String, splitter: String) => fullString.split(splitter).map(_.capitalize).mkString(splitter))
sampleDF.select($"id", capitalizerUDF($"text",      " ").as("text")).show   // WRONG !! require column type!
sampleDF.select($"id", capitalizerUDF($"text", lit(" ")).as("text")).show   // OK

val add_n = udf((x: Integer, y: Integer) => x + y)
df = df.withColumn("id_offset", add_n(lit(1000), col("id").cast("int")))    // ds.withColumn("volume", volumeUDF($"width", $"height", $"depth"))
------------
val last_n_days = udf((x: Integer, y: Integer) => {
  if (x < y) true else false
})
val df_filtered = df.filter(last_n_days(col("date_diff"), lit(90)))
-----------
val toHour   = udf((t: String) => "%04d".format(t.toInt).take(2).toInt )
-----------

 // OPTION 3 - z istniejacej funkcji  register
val squared = (s: Int) => {  s * s  } 
sqlContext.udf.register("square", squared)  // albo   squared _) 
  
sqlContext.range(1, 20).registerTempTable("test") 
select id, square(id) as id_squared from test 

// OPTION 4 - z istniejacej funkcji  sql.functions.udf
val upper: String => String = _.toUpperCase
val upperUDF = udf(upper) 
val square = (x => x*x)
squaredDF = df.withColumn("squarecol", square("val"))

// SMART JOINING
// UDF is a black box for Spark, so it needs to scan whole set
val  eqUDF = udf( (x:Int, y:Int) => x == y)
df1.join(df2, eqUDF($"x", $"y"))   // this is CARTESIAN + FILTER    n^2   , super bad
df1.join(df2, $"x" === $"y")       // SortMergeJoin                 n log n   optimized, very good!  use build in functions

registerFunction("strLenScala", (_: String).length) 
val tweetLength = hiveCtx.sql("SELECT strLenScala('tweet') FROM tweets LIMIT 10") 
 
Using a Hive UDF requires that we use the HiveContext instead of a regular SQLContext.
To make a Hive UDF available, simply call hiveCtx.sql("CREATE TEMPORARY FUNCTION name AS class.function")  


// regex
re.sub("/?[\d_.]+", "")              re.sub("[;\(\):,]*", "")
parser_agent_udf = f.udf(parser_agent, t.ArrayType(t.StringType()))

in SQL:
spark_session.udf.register("parser_agent", parser_agent, t.ArrayType(t.StringType()))
 







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



val financeDFwindowed = financeDF.select($"*", window($"Date", "30 days", "30 days", "15 minutes")
// splits Date into window buckets based on 3 params:            length  slide length    offset
financeDFwindowed.groupBy($"window", $"AccountNumber").count.show(truncate=false)
// window                                       ACcount       count
// [2015-03-05 19:15:00, 2015-03-05 20:15:00]   222-333-444     5





// FRANK

def parseLine(line: String) = {
    val fields = line.split(",")
    val age = field(2).toInt
    val num = field(3).toInt
    (age, num)  // return tuple
}

val rdd = sc.textFile(....).map(parseLine)
val totalByAge = rdd.mapValues(x => (x,1)).reduceByKey( (x,y) => (x._1 + y._1 , x._2 + y._2))
//              (33, 385) => (33, (385, 1))                     (33, (387, 2))
//              (33, 2  ) => (33, (2,   1))
val averageByAge = totalByAge.mapValues(x => x._1 / x._2)
//              (33, (387, 2)) =>  (33, 193.5)
val results = averageByAge.collect()
results.sorted.foreach(println)


val wordCounts = lowercaseWords.map( x => (x,1) ).reduceByKey( (x,y) => x+y )
val wordCountsSorted = worsCounts.map( x => (x._2, x._1)).sortByKey()           // numeric first


import scala.io.Codec
import scala.io.Source
import java.nio.charset.CodingErrorAction
// read file with movie id and title and transmit as broadcast (just once)
def loadMovieNames() : Map[Int, String] = {
    implicit val codec = Codes("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

    // create Map of Ints to Strings
    val movieNames:Map[Int, String] = Map()
    val lines = Source.fromFile("../ml-100k/u.item").getLines()
    for (line <- lines) {
        var fields = line.split('|')
        if (fields.length > 1) {
            movieNames += (fields(0).toInt -> fields(1))
        }
    }
    return movieNames   //dict!
}

// pozniej w main....
val nameDict = sc.broadcast(loadMovieNames)
val sortedMoviesWithNames = sortedMovies.map( x => (nameDict.value(x._2), x._1) )   // szukamy tytulu w broadcast dla ID filmu


def parseNames(line: String) : Option[(Int, String)] = {
    var fields = line.split('\"')
    if (fields.length > 1) {
        return Some(fields(0).trim().toInt, fields(1))      //  (ID, name)
    } else {
        return None 
    }
}

def countCoOccurences(line: String) = {
    // linia ma same ID, przy czym pierwszy to glowny, a pozostale to "Friends"   435 546546 456453 34645 34654 , wiec je zliczamy
    var elements = line.split("\\s+")  // whitespaces
    ( elements(0).toInt,  elements.length -1)
}

val namesRDD = names.flatMap(parseNames)
val mostPopular = flipped.max()
// pozniej szukamy max, a pozniej co to bylo za imie
val mostPopularName = namesRDD.lookup(mostPopular._2)(0) // lookup returns array, so we need 1st item (0)


import scala.collection.mutable.ArrayBuffer
val connections: ArrayBuffer[Int] = ArrayBuffer()
for (connection <- 1 to (fields.length -1)) {
    connections += fields(connection).toInt
}
return (hero, (connections.toArray, distance, color))


val hitCounter : Option[AccumulatorLong] = None
if (hitCounter.isDefined) {
    hitCounter.get.value  // for fun
    hitCounter.get.add(1)
}
// custom data types
type BFSData = (Array[Int], Int, String)        // array of hero ID connections, distance and color
type BFSNode = (Int, BFSData)

def createStartingRdd(sc:SparkContext): RDD[BFSNode] = {
    val inputFile = sc.textFile(...)
    return inputFile.map(convertToBFS)
}
// pozniej w main inicjujemy
hitCounter = Some(sc.accumulator(0))


val ratings = data.map(l => l.split("\t")).map(l => (l(0).toInt,  (l(1).toInt, l(2).toDouble)  ))       // userID => (movieID, rating)
val joinedRatings = ratings.join(rating)    // self-join, will get every possible PAIR permutation, i.e.  ABC  =>  AA, AB, AC, BA, BB, BC....
//  RDD consists of   userID => ((movieID, rating), (movieID, rating))

type MovieRating = (Int, Double)
type UserRatingPair = (Int, (MovieRating, MovieRating))
def makePairs(userRatings:UserRatingPair) = {
    val movieRating1 = userRatings._2._1
    val movieRating2 = userRatings._2._2

    val movie1 = movieRating1._1
    val rating1 = movieRating1._2
    val movie2 = movieRating2._1
    val rating2 = movieRating2._2

    ((movie1, movie2), (rating1, rating2))
}
// groupByKey:     (movie1, movie2) => (rating1, rating2), (rating1, rating2)

val moviePairs = uniqueJoinedRatings.map(makePairs).partitionBy(new HashPartitioner(100))


case class Person(ID: Int, name:String, age:Int, numFriends:Int)
def mapper(line: String) : Person = {
    val fields = line.split(',')
    val person:Person = Person(fields(0).toInt, fields(1), fields(2).toInt, fields(3).toInt)
    return person
}
val lines = sparkSession.sparkContext.textFile(....)       .map(x => Movie(x.split("\t")(1).toInt))        // najpierw RDD, zeby pozniej convert to DS
val people = lines.map(mapper)
val peopleDS = people.toDS()
val userRatings = ratings.filter(x => x.user == userID)  // userID = args(0).toInt




// ****************************** TIME FUNCTIONS *******************************
access_log.withColumn("unixtime", f.unix_timestamp("timecolumn","dd/MMM/yyyy:HH:mm:ss Z"))  // returns epoch
access_log.withColumn("unixtime", f.unix_timestamp("timecolumn","dd/MMM/yyyy:HH:mm:ss Z")).astype("timestamp")  // returns 2015-11-11 11:11:11









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