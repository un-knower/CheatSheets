// ------------------------ STREAMING (old)
// https://github.com/apache/spark/tree/v2.3.0/examples/src/main/scala/org/apache/spark/examples/streaming
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._ // not necessary since Spark 1.3

val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
val ssc = new StreamingContext(conf, Seconds(1))		// batch interval of 1 second.
val lines = ssc.socketTextStream("localhost", 9999)
val lines = ssc.socketTextStream(args(0), args(1).toInt, StorageLevel.MEMORY_AND_DISK_SER)
val errors = lines.filter(_.contains("error"))
val words = lines.flatMap(_.split(" "))		//one-to-many
val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)
wordCounts.print()
ssc.start()             // Start the computation
ssc.awaitTermination()  // Wait for the computation to terminate


// CHECKPOINTING

def functionToCreateContext(): StreamingContext = {
  val ssc = new StreamingContext(...)   // new context
  val lines = ssc.socketTextStream(...) // create DStreams
  ...
  ssc.checkpoint(checkpointDirectory)   // set checkpoint directory
  ssc
}

// Get StreamingContext from checkpoint data or create a new one
val context = StreamingContext.getOrCreate(checkpointDirectory, functionToCreateContext _)

// Do additional setup on context that needs to be done,
// irrespective of whether it is being started or restarted
context. ...

// Start the context
context.start()
context.awaitTermination()



// ----------------------------------------------------------------------
// ------------------------------------------ STRUCTURED (SQL based)
// ----------------------------------------------------------------------
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import java.sql.Timestamp
val spark: SparkSession = ...
spark.conf.set("spark.sql.shuffle.partitions", "1")

// Read text from socket
val socketDF = spark
  .readStream
  .format("socket")
  .option("host", "localhost")
  .option("port", 9999)
  .load()

socketDF.isStreaming    // Returns True for DataFrames that have streaming sources
socketDF.printSchema

val words = lines.as[String].flatMap(_.split(" "))
val wordCounts = words.groupBy("value").count()




// Read all the csv files written atomically in a directory
val userSchema = new StructType().add("name", "string").add("age", "integer")
val csvDF = spark
  .readStream
  .option("sep", ";")
  .schema(userSchema)      // Specify schema of the csv files
  .csv("/path/to/directory")    // Equivalent to format("csv").load("/path/to/directory")




// auto-generate data
val clicks = spark
  .readStream
  .format("rate")
  .option("rowsPerSecond", "5")
  .option("numPartitions", "1")
  .load()
  .where((rand() * 100).cast("integer") < 10)       // 10 out of every 100 impressions result in a click
  .select(($"value" - 50).as("adId"), $"timestamp".as("clickTime"))   // -100 so that a click with same id as impression is generated much later.
  .where("adId > 0")  




val inputDF = spark.readStream.json("s3://logs")
inputDF.groupBy($"action", window($"time", "1 hour")).count()
	.writeStream.format("jdbc").start("jdbc:mysql//...")




//////////////////////
case class DeviceData(device: String, deviceType: String, signal: Double, time: DateTime)
val df: DataFrame = spark.readStream... // streaming DataFrame with IOT device data with schema { device: string, deviceType: string, signal: double, time: string }
val ds: Dataset[DeviceData] = df.as[DeviceData]    // streaming Dataset with IOT device data
// Select the devices which have signal more than 10
df.select("device").where("signal > 10")      // using untyped APIs   
ds.filter(_.signal > 10).map(_.device)        // using typed APIs
// Running count of the number of updates for each device type
df.groupBy("deviceType").count()                          // using untyped API
// Running average signal for each device type
import org.apache.spark.sql.expressions.scalalang.typed
ds.groupByKey(_.deviceType).agg(typed.avg(_.signal))      // using typed API
///////////////////////




// ----------------------------------- WINDOWING
val lines = spark.readStream
      .format("socket")
      .option("host", host)
      .option("port", port)
      .option("includeTimestamp", true)
      .load()

// Split the lines into words, retaining timestamps
val words = lines.as[(String, Timestamp)].flatMap(line => line._1.split(" ").map(word => (word, line._2))).toDF("word", "timestamp")
// Group the data by window and word and compute the count of each group
val windowedCounts = words
  .withWatermark("timestamp", "10 minutes")		// defining “10 minutes” as the threshold of how late is the data allowed to be
  .groupBy(window($"timestamp", windowDuration, slideDuration), $"word")
  .count()
  .orderBy("window")
// Start running the query that prints the windowed word counts to the console
val query = windowedCounts.writeStream
  .outputMode("complete")
  .format("console")
  .option("truncate", "false")
  .start()

query.awaitTermination()
  




// KAFKA

// Subscribe to multiple topics
val df = spark
  .readStream
  .format("kafka")
  .option("kafka.bootstrap.servers", "host1:port1,host2:port2")
  .option("subscribe", "topic1,topic2")
  // Subscribe to a pattern
  .option("subscribePattern", "topic.*")
  .load()
df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
  .as[(String, String)]




// Subscribe to multiple topics, specifying explicit Kafka offsets
val df = spark
  .read
  .format("kafka")
  .option("kafka.bootstrap.servers", "host1:port1,host2:port2")
  .option("subscribe", "topic1,topic2")
  .option("startingOffsets", """{"topic1":{"0":23,"1":-2},"topic2":{"0":-2}}""")
  .option("endingOffsets", """{"topic1":{"0":50,"1":-1},"topic2":{"0":-1}}""")
  .load()
df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
  .as[(String, String)]

// Subscribe to a pattern, at the earliest and latest offsets
val df = spark
  .read
  .format("kafka")
  .option("kafka.bootstrap.servers", "host1:port1,host2:port2")
  .option("subscribePattern", "topic.*")
  .option("startingOffsets", "earliest")
  .option("endingOffsets", "latest")
  .load()
df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
  .as[(String, String)]
















windows()
reduceByWindow()
reduceByKeyAndWindow()
  val hashTagCounts = hashtagKeyValues.reduceByKeyAndWindow( (x,y) => x+y, (x,y) => x-y, Seconds(300), Seconds(1))
  val sortedResults = hashTagCounts.transform( rdd => rdd.sortBy(x => x._2, false))
updateStateyKey() - mainstane state accross many batches, e.g. running count

https://www.coursera.org/learn/big-data-analysis/home/week/6