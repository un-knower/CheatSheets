import org.apache.spark.sql.{Row, Dataset, DataFrame, Encoders, SaveMode, SparkSession)
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.expressions.scalalang.typed
import org.apache.spark.sql.types.DataTypes.*
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.sql.SparkSession 
import org.apache.spark.{SparkConf , SparkContext , Accumulator , rdd._, SparkContext._ }
import org.apache.spark.sql.types._ 
import org.apache.spark.sql.hive.HiveContext 
import org.apache.spark.sql.SQLContext 
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder 
import org.apache.spark.sql.Encoder 
import org.apache.spark.sql.SaveMode 
import org.apache.spark.sql.types._ 
import java.io.File 
import scala.io.Source._ 
import scala.collection.JavaConverters._ 
import org.apache.spark.util.Utils 
import java.util.Random 
import org.apache.hadoop.io.LongWritable 
import org.apache.hadoop.io.Text 
import org.apache.hadoop.conf.Configuration 
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat 

// ************************************************************************************************
// **************************************** PARAMETERS ********************************************
// ************************************************************************************************
// RDD
val conf = new SparkConf()  //  .setMaster("local").setAppName("My App").set("xxxxx", "yy")
conf.set("spark.app.name", "My Spark App") 
conf.set("spark.master", "local[4]") 
conf.set("spark.ui.port", "36000") // Override the default port      spark.ui.enabled=False???
conf.set("spark.sql.codegen", "true")  // PERFORMANCE options 
conf.set(“spark.io.compression.codes”, “lzf”)
conf.set(“spark.speculation”,”true”)
spark.conf.set("spark.sql.shuffle.partitions", 10)    (def 200)
spark.conf.set("spark.sql.shuffle.partitions", "1")  // keep the size of shuffles small
spark.conf.set("spark.executor.memory", "2g")

//get all settings
val configMap:Map[String, String] = spark.conf.getAll()
sc.getConf.toDebugString
sc.getConf.getAll   // Array[(String,String)]
sc.getConf.get("spark.driver.host")

val sc = new SparkContext(conf) 
val sc = new SparkContext(master, "GeoIpExample". System.getenv("SPARK_HOME"), Seq(System.getenv("JARS")))    // zamiast master  -->   local[*]
val hiveCtx = new HiveContext(sc) 
val sqlCtx  = new SQLContext(sc) //depreciated 
import hiveCtx._ 

 
// Ted Malaska
val runLocal = args(0).equals("L")
val inputFolder = args(1)
val filterWordFile = args(2)
val sc:SparkContext = if (runLocal) {
    val sparkConfig = new SparkConf()
    sparkConfig.set("spark.broadcast.compress", "false")
    sparkConfig.set("spark.shuffle.compress", "false")
    sparkConfig.set("spark.shuffle.spill.compress", "false")
    new SparkContext("local[2]", "DataGenerator", sparkConfig)
} else {
    val sparkConf = new SparkConf().setAppName("DataGenerator")
    new SparkContext(sparkConf)
}
val filterWordSetBc = sc.broadcast(scala.io.Source.fromFile(filterWordFile).getLines.toSet)
val inputRDD = sc.textFile(inputFolder)
val filteredWordCount: RDD[(String, Int)] = filterAndCount(filterWordSetBc, inputRDD)
filteredWordCount.collect().foreach{case ( name: String, count: Int) => println(" = " + name + ":" +  count)}
sc.stop()

def filteredWordCount(filterWordSetBc: Broadcast[Set[String]], inputRDD: RDD[String]): RDD[(String, Int)] = {
    val filteredWordCount: RDD[(String, Int)] = inputRDD.map(r => r.split(" ")).flatMap(a => a).filter(!filterWordSetBc.value.contains(_)).map((_, 1)).reduceByKey(_+_)
    return filteredWordCount
}


 // SparkSession (2.x)
object myApp { 
  def main(args: Array[String]) { 
	val spark = SparkSession
        .builder() 
        .master("local[*]") 
        .appName("example") 
        .config("spark.driver.memory", "2g")
        .config("spark.submit.deployMode", "client")            // runs where launched, if laptop closed, driver is killed/disconnected
        .config("spark.submit.deployMode", "cluster")           // runs in node within clister, even if launched from outside, not killed if laptop closed. No shell
        .config("spark.some.option", "true") 
        .config("spark.sql.warehouse.dir", "file:///C:/temp")   //hive 
        .enableHiveSupport()                                	//hive 
        .getOrCreate() 

    import spark.implicits._
    spark.setConf("xxxx", "yyyy") 

    val sc = sparkSession.sparkContext  // 2.0+

spark.newSession()  // creates copy of session, separate UDF, SQLCont, temp views from previous, but shared SparkContext and table cache
SparkSession.setActiveSession(copiedSession)
    
// configuration parameters
spark.sql.retainGroupColumns = false
reader.option("wholeFile", true) //json
reader.option("samplingRatio", (0...1.0))
--driver-class-path PATH/TO/JAR
//bit.ly/2u3frhN


// COMMAND LINE PARAMETERS
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
spark-submit --jars ./resources/mysql-connector-java-5.1.38.jar $ASSEMBLY_JAR $CLASS   --packages com.databricks:spark-xml_2.11:0.4.1
spark-submit --master spark://masternode:7077 --name "My App" --class com.example.MyApp  myApp.jar "options" "to" "app" 
spark-submit --executor-memory 4g --conf spark.eventLog.enabled=false --class "PrepareCSVApp" target/scala-2.11/project_2.11-1.0.jar

spark-shell --packages org.apache.hadoop:hadoop-aws:2.7.3......
 
SPARK_LOCAL_DIRS (conf/spark-env.sh) - comma separated storage locations for shuffle data 

in YARN: 
--num-executors         def 2 
--executor-memory 
--executor-cores  
--queue 
 
export HADOOP_CONF_DIR=HADOOP_HOME/conf 
spark-submit --master yarn yourapp   # in client mode only 


// ************************************************************************************************
// ***************************************** LOGGING **********************************************
// ************************************************************************************************
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

val LOGGER = LogManager.getLogger(this.getClass.getName)
LOGGER.error(“jakis tam error”)

spark.eventLog.enabled=true
spark.eventLog.dir=/dir

// spark listener
val LOGGER = LogManager.getLogger("CustomListener")
class CustomListener extends SparkListener {
    override def onStageCompleted(stageCompleted: SparkListenerStageCompleted): Unit = {
        LOGGER.warn(s"Stage Completed, runTime: ${stageCompleted.stageInfo.taskMetrics.executorRunTime}", s"cpuTime: ${stageCompleted.stageInfo.taskMetrics.executorCpuTime}")
    }
}
val myListener = new CustomListener
sc.addSparkListener(myListener)
spark.time(sql("select count(*) from range(x) cross join range(x)").show)

// ************************************************************************************************
// **************************************** RDD  **************************************************
// ************************************************************************************************
// can contain any object
val dif = sc.parallelize(Seq(false, 1, "Two", Map("threee"->3), ("kris", 4)))
// org.apache.spark.rdd.RDD[Any]
// one can set name to RDD
// EMPTY RDD
val empty_rdd = sc.parallelize( Array[String]() )
val empty_rdd = sc.emptyRDD
// INITIATE
val nr = sc.parallelize(1 to 1000)
// ORDERING
nr.takeOrdered(10)(Ordering[Int].reverse)       // NOT GOOD, this is like collect!!!
// COLLECT AS Map
tuole_map = tuple_rdd.collectAsMap()
// scala.collection.Map[String, Int] = Map(kris -> 1, a -> 2)

// DRIVER
println(test.collect().mkString(" "))

// LOAD whOLE txt FILE INTO memory
val whole = sc.wholeTextFiles("path")
whole.take(1)(0)._1    // String = hdfs://xxx:8020/test/part-00000

//SAVING
rdd.map(x => x.mkString(",")).saveAsTextFile("/user/final/folder")
rdd.saveAsObjectFile("/user/path/folder")                                   // save
val objecty = sc.objectFile[Array[String]]("/user/path/folder")             // read
// SEQUENCEFILE, only k/v
import org.apache.hadoop.io.Text
import org.apache.hadoop.io.LongWritable
sc.sequenceFile("/user/path", classOf[Text], classOf[LongWritable]).collect()
// HADOOP FORMATS
import org.apache.hadoop.mapreduce.lib.output._
import org.apache.hadoop.mapreduce.lib.input._
import org.apache.hadoop.io._
val prepareForNAH: Array[String] => (Text, Text) = x => (new Text(x(0)), new Text(x(2)))
rdd.map(prepareForNAH).saveAsNewAPIHadoopFile("/user/path", classOf[Text], classOf[Text], classOf[SequenceFileOutputFormat[Text, Text]])    // save
val objecty = sc.newAPIHadoopFile("/user/path", classof[SequenceFileInputFormat[Text, Text]], classOf[Text], classOf[Text])                 // read

// CHECK FOR VARIABLES IN MEMORY
scala> $intp.definedTerms.map(
    defTerms => s"${defTerms.toTermName}: ${$intp.typeOfTerm(defTerms.toTermName.toString)}")
    .filter(x => x.contains("()org.apache.spark.rdd.RDD"))
    .foreach(println)
badges_count_rdd:   ()org.apache.spark.rdd.RDD
rdd_reuse:  ()org.apache.spark.rdd.RDD
res11:  ()org.apache.spark.rdd.RDD
 


// PARQUET TALK , read row
val DF = spark.read....rdd.map(r => transformRow(r)).toDF
case class LedZeppelinFlat( Title: Option[String], Released: Option[String]....)
def transformRow(r: Row): LedZeppelinFlat = {
    def getStr(r: Row, i: Int) = if(!r.isNullAt(i)) Some(r.getString(i)) else None
    def getInt(r: Row, i: Int) = if(!r.isNullAt(i)) Some(r.getInt(i)) else None

    LedZeppelinFlat(
        getStr(r, 0), getStr(r, 1), getInt(r, 2))....
}

hadoop jar /opt/parquet-tools-1.8.2.jar cat  some_file.parquet
hadoop jar /opt/parquet-tools-1.8.2.jar meta some_file.parquet



// ************************************************************************************************
// ****************************** WINDOW WINDOWING BASED FUNCTIONS ********************************
// ************************************************************************************************
// will give you exactly same number of records as on input  (groupby - at most)
// rank, dense_rank, percent_rank, ntile, row_number, cume_dist, lag, lead
// WindowSpecification defines which rows are included in a window given in a row
import org.apache.spark.sql.expressions.Window
val byHtokens                        = Window.partitionBy($"token" startsWith "h") .....
val accountNumberPrevious4WindowSpec = Window.partitionBy($"AccountNumber")
                                            .orderBy($"Date").rowsBetween(-4,0)  // .rangeBetween( based on value )
// Long.MinValue = Window.unboundedPreceding
// Long.MaxValue = Window.unboundedFollowing
// 0 = Window.currentRow
val rollingAvgForPrevious4PerAccount = avg($"Amount").over(accountNumberPrevious4WindowSpec)

// over column operator defines a windowing column (aka analytic clause)
// applies aggregate function over window
val overUnspecifiedFrame = $"someColumn".over()
val overRange = $"someColumn" over someWindow
// use withColumn or select  operators
numbers.withColumn("max", max("num") over dividedBy2)


val joinsed = ids.join(sums).where($"group" === $"id" % 2)
//  id   |   collect_list(id)
//  0       [0,2,4,6]
//  1       [1,3,5,7]
//  2       [0,2,4,6]
val byWindow = Window.paritionBy($"id" % 2)
ids.withColumn("list", collect_list("id") over byWindow).show

    
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




case class Status(id:Int, customer:String, status:String) {
    def weCanPutSomeFunctionAsWell: String = s"""{"id":"$id", "customer":"$customer"}"""
    // and then refer to it as  e.g.   statusJSON = statusDF.map(x => x.weCanPutSomeFunctionAsWell)
}
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
"""{"company":"NewCo", "employees":[...]}""")

// PIVOT function
// 1. we need list of column headers in result table, so we take them, e.g. 5 most popular URL
top_url_pd = access_log.groupBy("url").count().orderBy(f.col("count").desc()).limit(1000).toPandas()
// 2. convert to Pandas List
top_url_list = top_url_pd["url"].tolist()           [u'/favicon.ico', u'/header.jpg', ...]
// 3. get most popular IPs 
access_log.groupBy("ip").count().toPandas())  // just for demo of results
// 4. apply pivot function
access_log.groupBy("ip").pivot("url", top_url_list).fillna(0).count().limit(5).toPandas()





// ************************************************************************************************
// ***************************  READ CSV AND EXPLICIT SCHEMAS *************************************
// ************************************************************************************************
import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType} 

// better schema inference
val schemaDF = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load(sampleFile)
val df = spark.read.format("csv").option("header", "true").schema(schemaDF.schema).load("/tmp/path")    // reuse schema from only one file!!
// even better, save schema
lazy val useSchema = DataType.fromJson(schemaJSON).asInstanceOf[StructType]
val df = spark.read.format("csv").option("header", "true").schema(useSchema).load("/tmp/path")

// with dataframes files are read on load !!!
val df = spark.read.option("inferSchema", "true").option("sep", "|").option("header", "true").csv("/user/...")       // option
val df = spark.read.options(Map("inferSchema"->"true", "sep"->"|", "header"->"true")).csv("/user/...")        // option s
val df = spark.read.schema(schema).csv("/user/....")
// https://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.sql.DataFrameReader@csv(paths:String*):org.apache.spark.sql.DataFrame

val rdd = sc.parallelize( Row(52.23, 21.01, "Warsaw") :: Row(42.30, 9.15, "Corte") :: Nil)  //to
val rdd = sc.parallelize( Array(Row(52.23, 21.01, "Warsaw") , Row(42.30, 9.15, "Corte")))   //samo
val schema =
StructType().              StructType(                                      StructType(Array(
 .add("lat", DoubleType)    StructField("lat", DoubleType, false) ::         StructField("lat", DoubleType),
 .add("long", DoubleType)   StructField("long", DoubleType, false) ::        StructField("long", DoubleType),
 .add("key", StringType)    StructField("key", StringType, false) ::Nil)     StructField("key", StringType)))
 .add("kVmap",ArrayType     StructField("keyValueMap", ArrayType(MapType(StringType, IntegerType))) :: Nil)
 (new StructType().add(.....))

val df = sqlContext.createDataFrame(rdd, schema)

// start!
hc.sql("create external table XXX (user string, time string)" + "location '" + outputTableLocation + "'")
val rowRDD = sc.textFile(..).map(r => Row(r.split(",")))

// 1.create from scratch manually
val userField = new StructField("user", StringType, nullable = true)
val timeField = new StructField("time", StringType, nullable = true)
val schema = new StructType(Array(userField, timeField))
hc.createDataFrame(rowRDD, schema).createOrReplaceTempView("temp")
hc.sql("insert into XXX select * from temp")

// 2.taking a schema from a Table  (instead of 1)
val emptyDF = hc.sql("select * from XXX limit 0")
hc.createDataFrame(rowRDD, emptyDF.schema).createOrReplaceTempView("temp")
hc.sql("insert into XXX select * from temp")


// NESTED DataFrame
hc.sql("create external table XXX (" + "user string," + "items array<struct< amount: string >>)" + "location...")
val rowRDD = sc.parallelize(Array(Row("bob", Seq(Row(1,2,3,4)))))



// for JSON
>= 2.2
spark.read
  .option("multiline", true).option("mode", "PERMISSIVE")
  .json("/path/to/user.json")
< 2.2
spark.read.json(sc.wholeTextFiles("/path/to/user.json").values())





// ************************************************************************************************
// **********************************  CREATE DATA FRAME ******************************************
// ************************************************************************************************
tempDF = sqlcontext.createDataFrame(List(("Justin", 100), ("SampleUser", 70)))
// [_1: string, _2: int]
namedDF = tempDF.toDF("UserName", "Score")
// [Username: string, Score: int]
tempDF = sqlContext.createDataFrame([ ("Joe", 1), ("Anna", 15), ("Anna", 12), ('name', 'score') ])
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


from pyspark.sql import Row
>>> df = sc.parallelize([ Row(name='Alice', age=5, height=80),Row(name='Alice', age=5, height=80),Row(name='Alice', age=10, height=80)]).toDF()

val companiesRDD = spark.sparkContext.makeRDD(jsonCompaniesLISTA)
val companiesDF  = spark.read.json(companiesRDD)






// ************************************************************************************************
// ******************************** DATAFRAME COLUMNS *********************************************
// ************************************************************************************************
df.select(df("Title"))  // canonical notation
df.select($"Title")     // shortcut
df.select(col("Title")) //

// printSchema, schema, dtypes, columns
// change column type
val viewDF = df.withColumn("ViewCount", df("ViewCount").cast("Integer"))    // same names, old == new
val viewDF = df.withColumnRenamed("ViewCount", "ViewCount.Str")     // this is not good to use dots
viewDF.select("`ViewCount.Str`").show(3)                            // use backquotes !!!

// cloning column (will appear at the end!!!)
val df2 = df.withColumn("TitleClone1", $"Title")

// changing expression in column + replace
df.withColumn("Title",  concat(lit("Tiiiitle: "), $"Title")    ).select($"Title")       // (nazwa, jak_utworzyc)
// Title
// ---------------------|
// Tiiiiitle: How can I |

// dropping columns  (just use   df.drop("colname"))
df.columns.contains("TitleClone1")      // true

// CASE WHEN (company=FamilyCo) THEN Premium WHEN (company = OldCo) THEN Legacy ELSE Standard END
df.select($"*", when($"company"==="FamilyCo", "Premium").when($"company"==="OldCo", "Legacy").otherwise("Standard").alias("X") , $"Title").show()
df.where( ($"company"==="FamilyCo") || ($"company"==="X") ).show()
df.orderBy($"company".desc).show()
df.sort($"company".desc, $"another".asc).show() // sort == orderBy

val employeesDF = df.select($"company", expr("employee.firstName as firstName"))
// org.aapache.spark.sql.DataFrame = [company: string, firstName: string]

posexplode($"employees").as(Seq("employeePosition", "employee"))).show
.select(explode($"records") as 'record)




// ************************************************************************************************
// **************************** NULL / CORRUPT RECORDS VALUES NA **********************************
// ************************************************************************************************
df.select("Id", "Title").na.drop("all").show()              // removes WHOLE ROW if there's a null in ALL columns
df.select("Id", "Title").na.drop("any").show()               // removes WHOLE ROW if there's a null in ANY column
df.select("Id", "Title").na.drop("any", Seq("Title")).show()  // removes WHOLE ROW if there's a null in TITLE column

df.select("Id", "Title").na.replace(Seq("Title"), Map("Phrase to replace" -> "[Redacted]")).show()  // replace values in column with Redacted
df.select("Id", "Title").na.fill("[N/A]").show()            // replace ALL null values with N/A

* Separate "bad data"
* Drop records
* Raise exception

spark.read.option("mode", "PERMISSIVE").json("/path")                                           --> we see _corrupt_record column
spark.read.option("mode", "PERMISSIVE").option("columnNameOfCorruptRecord", "Invalid").json()   --> you can specify own colName
spark.read.option("mode", "DROPMALFORMED").json()                                               --> bad rows deleted
spark.read.option("mode", "FAILFAST").json()                                                    --> fails on error
val jsonSchema = sqlContext.read.json(sc.parallelize(Array("""{"string":"string1","int":1,"dict": {"key": "value1"}}""")))  // Specify a JSON schema
val testJsonDataWithoutExtraKey = sqlContext.read.format("json").schema(jsonSchema.schema).load("/tmp/test.json")
display(testJsonDataWithBadRecord.where("_corrupt_record is not null"))





// ************************************************************************************************
// ************************************** SAVING DATAFRAME ****************************************
// ************************************************************************************************
df.write.save("path")                      // just 1 parquet file = 1 partition
df.repartition(2).write.save("path")     // 2 parquet files = 2 partition
df.write.format("text").save("path")      // must be 1 column,  it will also contain NULL values
df.select("Title").write.format("text").save("path")  //ok
df.write.csv("path")                      // ok, but no header
df.write.option("header","true").csv("path")   // also change sep etc.
df.write.mode("overwrite").csv("path")          // also APPEND
write.option()"compression", "snappy").parquet(nestedOutput)


// ************************************************************************************************
// *****************************************  DATA SET ********************************************
// ************************************************************************************************
case class : like regular class, create immutable object (Product),  word "new" is not required (apply method is used)

create from memory   .toDS()
val primi_DS = Seq(10,20,30).toDS() // must be same type

create from DF       .as[CaseClass]
val primi_DS.filter('ColumnName < 533)  // we can use it to reference column

val miniDS = ds.map(p => (p.Id, p.Score))       // org.apache.spark.sql.Dataset[(Int, Integer)] = [_1: int, _2: int]    // typed transf.
val miniDF = ds.select($"Id", $"Score")        // org.apache.spark.sql.DataFrame = [Id: int, Score: int]                // untyped transf.

val rdd = spark.sparkContext.parallelize(List(Row(0), Row(true), Row("stuff")))
val df = spark.createDataFrame(rdd, schema)
df.collect  // it will fail only during ACTION !
val ds = spark.createDataset(rdd, schema)  // will fail instantly, that's DF vs DS
toLocalIterator , same as collect but takes less space (just as much as biggest partition)



//compute histogram of age by name
val hist = ds.groupBy(_.name).mapGroups {
    case (name, people: Iter[Person]) =>
        val buckets = new Array[Int](10)
        people.map(_.age).foreach { a =>
            buckets(a / 10) += 1
        }
        (name, buckets)
}






// ************************************************************************************************
// ****************************************** SPARKSQL ********************************************
// ************************************************************************************************
TEMP VIEW - just per session, automatically dropped at the end, or can be done manually --> dropTempView()
GLOBAL VIEW - not dropped, tied to global_temp variable

Val DF = spark.read.format("parquet").load("hdfs://lambda-host/user/cloudera/batch1/")
Val DF = spark.read.parquet("hdfs://lambda-host/user/cloudera/batch1/")
Val DF = spark.sql("SELECT * FROM parquet.`/user/cloudera/batch1/` WHERE page_view_count > 2")

Loading view as DataFrame //equivalent to loading using SELECT *
df.createOrReplaceTempView("comments")
val df2 = spark.table("comments")
df2.orderBy($"Score".desc).show()

// Saving to persistent Tables (into Hive metastore)
df.write..format("json or parquet or csv").partitionBy("...").saveAsTable("some_table_no_option")
df.write.options(Map("path"->"/path/to/hdfs")).saveAsTable("db_name.table_name")    // before saving
spark.sql("alter table db_name.table_name set SERDEPROPERTIES ('path'=hdfs://host.com:8020/warehouse..../)")
spark.catalog.refreshTable("db_name.table_name")

// JDBC,  remember to load jar driver via  --jars
df = spark.read.format("jdbc")
    .option("url", "jdbc:mysql://cloudera/scm")
    .option("driver", "com.mysql.jdbc.Driver")
    .option("dbtable", "COMMANDS")
    .option("user", "scm_user").option("password", "pss")
    .load

// check specification of table
sparkContext.sql("DESC FORMATTED table1").collect.foreach(println)

// ************************************************************************************************
// ****************************************** CATALOG *********************************************
// ************************************************************************************************
* Structured data
* Registered views, tables, UDFs
* Managing metadata
* Catalog API
spark.catalog.listDatabases()
spark.catalog.listTables("default")
spark.catalog.dropTempView("comments")


// ************************************************************************************************
// ***********************************  UDF  ******** org.apache.spark.sql.java.api.UDF1 - UDF22 (# of params!)
// ************************************************************************************************
// http://blog.cloudera.com/blog/2017/02/working-with-udfs-in-apache-spark/    UDAF

// OPTION 1 - SparkSession.udf.register
val squared = (s: Int) => {  s * s  } 
sqlContext.udf.register("square", squared)  // albo   squared _) 

sqlContext.range(1, 20).registerTempTable("test") 
select id, square(id) as id_squared from test 

SparkSession.udf.register("myUpper", (input: String) => input.toUpperCase)

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
}
val df_filtered = df.filter(last_n_days(col("date_diff"), lit(90)))
-----------
val toHour   = udf((t: String) => "%04d".format(t.toInt).take(2).toInt )
-----------
val upper: String => String = _.toUpperCase
val upperUDF = udf(upper)
val upperUDF = udf(upper _)     // ???
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

// in SQL
spark_session.udf.register("parser_agent", parser_agent, t.ArrayType(t.StringType()))
 




// ************************************************************************************************
// ****************************************** FUNCTIONS *******************************************
// ************************************************************************************************
// named
def split_the_line(x: String): Array[String] = x.split(",")
def starts_h(word: (String, Int)) = word._1.toLowerCase.startsWith("h")
rdd.map(split_the_line) //usage


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

val r = sc.textFile("/Users/akuntamukkala/temp/customers.txt")
// flatMap
val records = r.flatMap( line => line.split(" "))
Array[String] = Array(How, can, I , see...)

// map
val records = r.map( line => line.split('|'))
val records = r.map( line => line.split('|')(6))
Array[Array[String]] = Array(Array(How, can, I, see.....))               // list of lists
val c = records.map(r => Customer(r(0), r(1).trim.toInt, r(2), r(3)) )
c.registerAsTable("customers")

// keyBy   rdd is  (word, count), e.g.   (something, 10)
val x = rdd.keyBy { case (word, count) => count }.sortByKey(ascending=false)      // and we get  (count, (word, count))   czyli wyciagamy key naprzod, reszta zostaje


// sortByKey
word_count.map({ case (x, y) => (y, x) }).sortByKey(false).map( x => x.swap).collect()  // Array[(String, Int)]
// sortBy
word_count.sortBy({ case (x, y) => -y }).collect()

// partitioner
import org.apache.spark.HashPartitioner
val badges_for_part = badges_rdd.map(x => (x(2) , x.mkString(","))).repartition(50)
badges_for_part.partitioner     // Option[org.apache.spark.Partitioner] = None      //no partitioner defined
// w output beda wszystkie keys, niesortownse, nieporozdzielane, e.g.    (AAA, [444,555,20171010]....), czyli podzielone, ale chaotycznie
val badges_by_badge = badges_for_part.partitionBy(new HashPartitioner(50))          //pass number of part
badges_by_badge.partitioner     // Option[org.apache.spark.Partitioner] = Some(org.apache.spark.HashPartitioner)
// w output beda tylko keys na litere X, np.  (Badge, [4443,223,323]...), NO grouping, NO aggregations, but..data SKEW highly possible!

val moviePairs = uniqueJoinedRatings.map(makePairs).partitionBy(new HashPartitioner(100))

//glom , coalesce all rows in a partition into an array
badges_by_badge.map( { case (x,y) => x } ).glom().take(2)
Array[Array[String]]

// how many elements per partition
// MapPartitions, apply a function to each partition, done at single pass, returns after entire partition is processed
badges_by_badge.mapPartitions(x => Array(x.size).iterator, true).collect()
Array[Int] = Array(0, 166, 0,0, 3156,3,4,5,32, 6000)     // we see how data in partitions are gathered, in some 0 rows, in some 6000 rows, SKEW !!!
// we can compare with original RDD where data were with NO paritioner specified, very evenly
badges_for_part.mapPartitions(x => Array(x.size).iterator, true).collect()
Array[Int] = Array(411,412,411,411,411,411...)


// sampling
val sample = rdd.sample(false, 0.1, 50)     // withReplacement , fraction % , seed)
val sample2= rdd.takeSample(false, 15, 50)  // will take exactly 15 elements
rdd.countApprox(100, 0.95)                  // timeout 100 seconds,  95% confidence
val sample = df.sample(true, 0.1)   // 10% of whole DF
  // Create a fraction map where we are only interested:
  // - 50% of the rows that have answer_count = 5
  // - 10% of the rows that have answer_count = 10
  // - 100% of the rows that have answer_count = 20
  // Note also that fractions should be in the range [0, 1]
  val fractionKeyMap = Map(5 -> 0.5, 10 -> 0.1, 20 -> 1.0)
  df.stat.sampleBy("answer_count", fractionKeyMap, 7L).groupBy("answer_count").count().show()

// set
val questions = sc.parallelize(Array( ("aaa", 1), ("bbb", 2), ("aaa", 5) ))
val answers   = sc.parallelize(Array( ("aaa", 3), ("ccc", 4) ))

// union
questions.union(answers).collect()
Array[(String, Int)] = Array((aaa,1), (bbb,2), (aaa,5), (aaa,3), (ccc,4))
questions.union(questions).collect()
Array[(String, Int)] = Array((aaa,1), (bbb,2), (aaa,5), (aaa,1), (bbb,2), (aaa,5))    // duplicates preserved!

// join ,  hash join over cluster - expensive
questions.join(answers).collect()
Array[(String, (Int, Int))] = Array((aaa,(1,3)), (aaa,(5,3)))
questions.join(questions).collect()
Array[(String, (Int, Int))] = Array((aaa,(1,1)), (aaa,(1,5)), (aaa,(5,1)), (aaa,(5,5)), (bbb,(2,2)))

questions.leftOuterJoin(answers).collect
Array[(String, (Int, Option[Int]))] = Array((aaa,(1,Some(3))), (aaa,(5,Some(3))), (bbb,(2,None)))

questions.rightOuterJoin(answers).collect
Array[(String, (Option[Int], Int))] = Array((ccc,(None,4)), (aaa,(Some(1),3)), (aaa,(Some(5),3)))

questions.fullOuterJoin(answers).collect
Array[(String, (Option[Int], Option[Int]))] = Array((ccc,(None,Some(4))), (aaa,(Some(1),Some(3))), (aaa,(Some(5),Some(3))), (bbb,(Some(2),None)))

questions.cartesian(answers).collect
Array[((String, Int), (String, Int))] = Array(((aaa,1),(aaa,3)), ((aaa,1),(ddd,4)), ((bbb,2),(aaa,3)), ((bbb,2),(ddd,4)), ((aaa,5),(aaa,3)), ((aaa,5),(ddd,4)))

// groupByKey - a lot of shuffle
questions.groupByKey.collect
Array[(String, Iterable[Int])] = Array((aaa,CompactBuffer(1, 5)), (bbb,CompactBuffer(2)))

questions.groupByKey.map( { case (k,v) => (k,v.toList) }).collect()
questions.groupByKey.map( x => (x._1,x._2.toList)).collect()
Array[(String, List[Int])] = Array((aaa,List(1, 5)), (bbb,List(2)))

questions.groupByKey.map(x => (x._1,x._2.size)).sortBy(x => -x._2).collect()
Array[(String, Int)] = Array((aaa,2), (bbb,1))

// reduceByKey - done within partition so require LESS shuffle
questions.reduceByKey(_+_).collect()
Array[(String, Int)] = Array((aaa,6), (bbb,2))
questions.reduceByKey(_+_).lookup("aaa")
Seq[Int] = ArrayBuffer(6)
questions.reduceByKey(_+_).lookup("aaa")(0)
Int = 6

// aggregateByKey, like reduceByKey but takes initial value and allows specifying functions for merging and combining
// reduceByKey - when types are the same                aggregateByKey - doesn't require types to be the same        avoid groupByKey (unless you have unique keys)
val for_keeping_count = (0, 0)      // total point, number of questions
def combining (tuple_sum_count: (Int, Int), next_score: Int) =
    (tuple_sum_count._1 + next_score,  tuple_sum_count._2 + 1)      // for same partition
def merging() (tuple_sum_count: (Int, Int), tuple_next_partition_sum_count: (Int, Int)) =
    (tuple_sum_count._1 + tuple_next_partition_sum_count._1  ,    tuple_sum_count._2 + tuple_next_partition_sum_count._2)
val aggregated_user_question = rdd.aggregateByKey(for_keeping_count)(combining, merging)
aggregated_user_question.take(1)
Array[(String, (Int, Int))] = Array((2800,(0,1)))

// combineByKey, like aggregateByKey but more flexible,  specify initial value and returns new value
def to_list(postid: Int): List(int) = List(postid)
def merge_posts(postA: List[Int], postB: Int) = postB :: postA
def combine_posts(postA: List[Int], postB: List[Int]): List[Int] = postA ++ postB
val combined = rdd.combineByKey(to_list, merge_posts, combine_posts)
Array[(String, List[Int])] = Array((51, List(711,222,333,44)))

// countByKey, returns dict with keys and counts of occurrence, like reduceByKey, where we count based on key
questions.reduceByKey(_+_).countByKey()
scala.collection.Map[String,Long] = Map(aaa -> 1, bbb -> 1)
questions.reduceByKey(_+_).countByKey()("aaa")
Long = 1

// histogram.  We can spot SKEW data
questions.map( { case (k,v) => v }).take(10)
Array[Int] = Array(1, 2, 5)
questions.map( { case (k,v) => v }).histogram(5)
(Array[Double], Array[Long]) = (Array(1.0, 1.8, 2.6, 3.4, 4.2, 5.0),Array(1, 1, 0, 0, 1))   // returns INTERVAL (buckets)    ,   how many values fall in that buckets
// we can provide our intervals
val intervals: Array[Double] = Array(0, 1, 2, 3, 4, 5)
questions.map( { case (k,v) => v }).histogram(intervals)
Array[Long] = Array(0, 1, 1, 0, 1)                      // now only array with counts of each interval


//using RDD to compute average avg    data.groupBy("dept").avg("age")
//dataRDD.map(lambda (x,y): (x,(y,1))).reduceByKey(lambda x,y: (x[0]+y[0], x[1]+y[1])).map(lambda (x,(y,z)): (x,y/z))
data.map { case(dept, age) => dept -> (age,1)}
    .reduceByKey { case ((a1,c1), (a2,c2)) => (a1+a2, c1+c2)}
    .map { case (dept, (age,c)) => dept -> age/c}

//  ***************************** JOIN ***********************************
personDF.join(roleDF, $"col1" === $"col2", JOIN_TYPE)                       // when column names different
personDF.join(roleDF, personDF("col1") === roleDF("col2"), JOIN_TYPE)       // when column names the same
personDF.join(roleDF, Seq("Id"), JOIN_TYPE)                                 // when column names the same + will avoid duplicated col names in result
personDF.join(roleDF, Seq("Id"), "left_semi") // EXISTS, zostawi tylko DISTINCT rows from left tab, ktore maja odpowiednik z prawej, nie pokaze prawej! 
personDF.join(roleDF, Seq("Id"), "left_anti") // OPPOSITE EXISTS,, zostawi te ktore nie maja odpowiednika w prawej ! 
personDF.join(roleDF) == personDF.join(roleDF, Seq("Id"), "cross") == personDF.crossJoin(roleDF)   // wszystkie mozliwe combinations
// spark.sql.crossJoin.enabled = True
xx.join(clicks, expr("clickAdID = impressionAdID"))

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



val parsedRDD = rdd.flatMap {
    line => line.split("""\s+""") match {
        case Array(project, page, num, _) => Some((project, page, num))
        case _ => None
    }
}
parsedRDD.filter { case (project, page, num) => project == "en" }
        .map { case (_, page, num) => (page, num)}
        .reduceByKey(_ + _)
        .take(100).foreach { case (page, num) => println(s"$page: $num")}






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

// inna opcja zaladowania pliku
Source.fromFile("...").getLines.map(line => {
    val splits = line.split(Utils.COMMA_DELIMITER, -1)
    splits(0) -> splits(7)
}).toMap



// pozniej w main....
val nameDict = sc.broadcast(loadMovieNames)
val sortedMoviesWithNames = sortedMovies.map( x => (nameDict.value(x._2), x._1) )   // szukamy tytulu w broadcast dla ID filmu

// inna funkcja , z innych lekcji
val tp = rdd.collectAsMap()
val broadcast_tp = sc.broadcast(tp)     // broadcasting DICT
def get_name(user_column: Array[String]) = {
    val user_id = user_column(3)
    val user_name = user_column(0)
    val user_posts_count = 0
    if (broadcast_tp.value.keySet.exists(_ == user_id))
        user_posts_count = broadcast_tp.value(user_id).toString
    (user_id, user_name, user_posts_count)                      // return!
}
val user_info = users_rdd.map(get_name)



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


// custom data types
type BFSData = (Array[Int], Int, String)        // array of hero ID connections, distance and color
type BFSNode = (Int, BFSData)

def createStartingRdd(sc:SparkContext): RDD[BFSNode] = {
    val inputFile = sc.textFile(...)
    return inputFile.map(convertToBFS)
}



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




// to jest plik w folderze  commons o nazwie  Utils.scala
// mozna go zaimportowac pozniej poprzez  import com.sparkTutorial.commons.Utils
// https://github.com/jleetutorial/scala-spark-tutorial/blob/master/src/main/scala/com/sparkTutorial/rdd/airports/AirportsByLatitudeSolution.scala
package com.sparkTutorial.commons
object Utils {
  // a regular expression which matches commas but not commas within double quotations
  val COMMA_DELIMITER = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"
}


// how to avoid lineage explosions (if DF has too long query plan)
def cutLineage(df: DataFrame): DataFrame = {
    val sqlCtx = df.sqlContext
    val rdd = df.rdd
    rdd.cache()
    sqlCtx.createDataFrame(rdd, df.schema)
}



// ************************************************************************************************
// ************************************* TIME TIMING FUNCTIONS ************************************
// ************************************************************************************************
access_log.withColumn("unixtime", f.unix_timestamp("timecolumn","dd/MMM/yyyy:HH:mm:ss Z"))  // returns epoch
access_log.withColumn("unixtime", f.unix_timestamp("timecolumn","dd/MMM/yyyy:HH:mm:ss Z")).astype("timestamp")  // returns 2015-11-11 11:11:11
                                    unix_timestamp($"record.eventTime", "yyyy-MM-dd'T'hh:mm:ss").cast("timestamp") as 'timestamp, $"record.*")
date_format(window.end, "MMM-dd HH:mm") as time,

val yesterday = date_sub(current_date(), 1)

// ************************************************************************************************
// ************************************* CACHING ****** *******************************************
// ************************************************************************************************
import org.apache.spark.storage.StorageLevel
rdd.persist(StorageLevel.DISK_ONLY) //unpersist


// ************************************************************************************************
// ************************************* ACCUMULATOR **********************************************
// ************************************************************************************************
ACCUMULATOR IS NOT GUARANTEED AS TASKS MAY FAIL 

val hitCounter : Option[AccumulatorLong] = None
if (hitCounter.isDefined) {
    hitCounter.get.value  // for fun
    hitCounter.get.add(1)
}
// pozniej w main inicjujemy
hitCounter = Some(sc.accumulator(0))

// mozna tez sumowac rozmiar odpowiedzi, albo ile przerabiamy danych
// wystarczy zainicjowac akumulator i pozniej wywolac w RDD.filter lub gdzie indziej
val processedBytes = sparkContext.longAccumulator
val responseFromCanada = responseRDD.filter( response => {
    processedBytes.add(response.getBytes().length)
})

val accumulator_badge = sc.longAccumulator("BadgeAccumulator")
def add_badge(item: (String, String)) = accumulator_badge.add(1)
rdd.foreach(add_badge)

// using accumulator for validation
val (ok, bad) = (sc.accumulator(0), sc.accumulator(0))
val records = input.map { x => if (isValid(x)) ok +=1  else  bad +=1}
// an action , count, save here..
if (bad.value > 0.1 * ok.value) {
    throw Exception("bad data - do not use results")
}



// ************************************************************************************************
// ************************************* TIPS *****************************************************
// ************************************************************************************************
// encaptulation, issues, wrapping, serialization issues with JVM memory  https://youtu.be/J5Gu012T22U
import org.apache.spark.util.SizeEstimator
val N = 1100*1000*1000   // 1.1 billion
val array = Array.fill[Short][N](0)    // Array[Short] = Array(0,0,0,0,0,0...)
SizeEstimator.estimate(array)       // 2.2 GB !
val b = sc.broadcast(array)
SizeEstimator.estimate(b)
sc.parallelize(0 until 100000).map(i => b.value(i))     // CRASH !!!!  because it will closure array

// solution 1, use transient
@transient val array = Array.fill[Short][N](0)    // Array[Short] = Array(0,0,0,0,0,0...) SOLVES THE PROBLEM

// solution 2, split into separate objects
object Data {
    val N= 1100...
    val array = 
        val getB = sc.broadcast(array)
}
object Work {
    def run(): Unit = {
        val b = Data.getB       // local ref!
        val rdd = sc.parallelize(...).map(i => b.value(i))  // only needs b
        rdd.take(10).forach(println)
    }
}




//...................................... large RDD to listDatabases
data.forEachPartition { records => {
    val connection = new DB(...)
    records.foreach { record => 
        connection. .....}
}}


Compatibility with older systems: 
sqlContext.setConf("spark.sql.parquet.binaryAsString", "true")          [set spark.sql.parquet.binaryAsString=true]

Finding out IDE or other input parameters, so we can run config
   //check if running from IDE
    println("Input args: " +ManagementFactory.getRuntimeMXBean.getInputArguments.toString())
    if (ManagementFactory.getRuntimeMXBean.getInputArguments.toString().contains("eclipse")) {
    conf.setMaster("local[*]")
    }


// Tokenize the wiki content
val tokenizer = new Tokenizer().setInputCol("content").setOutputCol("words")
val wordsDf = tokenizer.transform(some_func)         // or   DF inside brackets         // transform is used for streaming, also foreachRDD

def some_func( logs: RDD[String] = { logs.map(_.split(" ")(0) ) } )                     // func defined for normal batch




// salting
"Foo" + random.nextInt(saltFactor)
// isolated salting, only to NULLs  for example

//use ReduceByKey over GroupByKey !!!!!!
// use TreeReduce ove Reduce    //reduce brings everything back to driver,    TreeReduce does more work on execurtors

conf.set("spark.io.compression.codes", "lzf")   // snappy is ok, but LZF may give better performance
conf.set("spark.speculation", "true")           // help prevent stragglers

//optimization of loading
val df = spark.read.table("tab.x").filter('ss_sold_date is in (222,333,555))    // speed up loading time,   ss_sold_date is partition
MSCK REPAIT TABLE tab.x;    // for unmanaged tables - partition discovery runs once, use   saveAsTable or insertInto to add new partitions

// same with DF API
// managed
df1.write.partitionBy("ss_sold_date").saveAsTable("tab.x")
// unmanaged
df1.write.partitionBy("ss_sold_date").option("path", "/tmp/some/path/x").saveAsTable("tab.x")






//bucketing
SET spark.sql.sources.bucketing.enabled=true
output.write.bucketBy(100, "ss_item_sk").sortBy("ss_item_sk").saveAsTable("tab.x")  // CREATE TABLE...USING PARQUET CLUSTERED BY (col1,col2) SORTED BY (col1) INTO 10 BUCKETS
//repartition, to get cleaner files
output.repartition("some_column").write.partitionBy("some_column").option("path", "/tmp").saveAsTable("tab.x")
//managing file size
spark.conf("spark.sql.files.maxRecordPerFile", enable!)
// adaptive execution
spark.sql.adaptive.enabled
spark.sql.adaptive.shuffle.targetPostShuffleInputSize (default 64mb)  SPARK-9850



ANALYZE TABLE tab.x COMPUTE STATISTICS
ANALYZE TABLE tab.x COMPUTE STATISTICS noscan           // will let spark know that is small enough for broadcast join
ANALYZE TABLE tab.x COMPUTE STATISTICS FOR COLUMNS col1, col2, col3


// tuning, optimization for large-scale
// dynamic executor allocation
spark.dynamicAllocation.enabled = true
spark.dynamicAllocation.executorIdleTimeout = 2m
spark.dynamicAllocation.minExecutors = 1
spark.dynamicAllocation.maxExecutors = 2000
// configurable max number of fetched failures
spark.max.fetch.failures.per.stage = 10
// Out Of Memory too frequent - tune RPC server threads
spark.rpc.io.serverThreads = 64


// executor memory layout
spark.memory.fraction * ( spark.executor.memory - 300 MB)       // shuffle memory
(1 - spark.memory.fraction) * (spark.executor.memory - 300 MB)  // user memory
300 MB // reserved memory
spark.yarn.executor.memoryOverhead = 0.1 * (spark.executor.memory)  // memory buffer


// enable off-heap memory
spark.memory.offHeap.enabled = true         // shuffle
spark.memory.offHeap.size = 3g              // shuffle
spark.executor.memory = 3g                  // user memory
spark.yarn.executor.memoryOverhead = 0.1 * (spark.executor.memory + spark.memory.offHeap.size)

// GC collection tuning  G1GC suffers from fragmentation due to humongous allocations if object size is > 32 MB
// use parallel GC instead of G1GC
spark.executor.extraJavaOptions = -XX:ParallelGCThreads=4 -XX:+UseParallelGC

// eliminate disk I/O bottleneck, tune shuffle buffer
spark.shuffle.file.buffer = 1 MB
spark.unsafe.sorter.spill.reader.buffer.size = 1 MB
// optimize spill files merging
spark.file.transferTo = false
spark.shuffle.unsafe.file.output.buffer = 5 MB
// tune compression block size
spark.io.compression.lz4.blockSize = 512kB
// cache index files on shuffle server
spark.shuffle.service.index.cache.entries = 2048
// tunr shuffle service worker thread and backlog
spark.shuffle.io.serverThreads = 128
spark.shuffle.io.backLog = 8192
// shuffle registration timeout and retry
spark.shuffle.registration.timeout = 2m
spark.shuffle.registration.maxAttempts = 5
// optimal number of mappers
num_of_mappers = max(256 MB, inputTableSize / 50000)
num_of_reducers = max(200, min(10000, max(inputTableSize / 256 MB * 0.125, 200)))

spark.sql.files.maxPartitionBytes
spark.sql.broadcastTimeout
spark.sql.autoBroadcastJoinThreshold



case class Account(number: String, firstName: String, lastName: String)
case class Transaction(id: Long, account: Account, date: java.sql.Date, amount: Double, description: String)
case class TransactionForAverage(accountNumber: String, amount: Double, description: String, date: java.sql.Date)

object Detector {

        
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

https://github.com/deanwampler/JustEnoughScalaForSpark/blob/master/notebooks/JustEnoughScalaForSpark.ipynb   =  https://youtu.be/LBoSgiLV_NQ


// frequent issues:

Problem: Exception in thread "main" java.lang.NoSuchMethodError: 
Solution:  make Scala versions match, in sbt e.g.
scalaVersion := "2.12"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.2.0"


Problem: Error: Could not find or load main class in intelliJ IDE
Solution:  right click  src  folder  and select  MARK DIRECTORY AS: SOURCE


Problem:  
Solution:  class name must be in format <package>.<object>


Problem:  Exception in thread "main" java.lang.IllegalArgumentException: System memory 259522560 must be at least 471859200. Please increase heap size using the --driver-memory option or spark.driver.memory in Spark configuration
Solution:    .config("spark.testing.memory", "471859201")