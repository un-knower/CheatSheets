import org.apache.spark.sql.functions.{explode, split}
import org.apache.spark.sql.functions._


// Setup connection to Kafka
// key, value, topic, partition, offset, timestamp
val streamingInputDF = spark.readStream
  .format("kafka")
  .option("kafka.bootstrap.servers", "YOUR.HOST:PORT1,YOUR.HOST:PORT2")   // comma separated list of broker:host
  .option("subscribe", "YOUR_TOPIC1,YOUR_TOPIC2")    // comma separated list of topics
  .option("subscribePattern", "topic*")       // dynamic list
  .option("assign", {"topicA" : [0,1] } )     //specific partitions
  .option("startingOffsets", "latest") // read data from the end of the stream
  .load()
  // .groupBy('value.cast("string") as 'key)
  // .agg(count("*") as 'value)
  .selectExpr("cast (value as string) as json")
  .select(from_json("json", schema).as("data")) // https://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.sql.functions$@from_json(e:org.apache.spark.sql.Column,schema:String,options:Map[String,String]):org.apache.spark.sql.Column
  // json   -->  data (nested)
  // { "timestamp": 14545454545, "device": "devA", ...}       timestamp    device
  // { "timestamp": 14545454546, "device": "devB", ...}       14545454545  devA
  .select("data.*")   // not nested any more
  .writeStream    //SINK
  .format("parquet")
  .option("path", "/parquetTable/")
  .trigger("1 minute")  // checkpoint
  .partitionBy("date")
  .option("checkpointLocation", "...")
  .outputMode("update")
  .start()
  
  
// split lines by whitespace and explode the array as rows of `word`
val df = streamingInputDF
  .select(explode(split($"value".cast("string"), "\\s+")).as("word"))
  .groupBy($"word")
  .count

var streamingSelectDF = 
  streamingInputDF
   .select(get_json_object(($"value").cast("string"), "$.zip").alias("zip"))
    .groupBy($"zip") 
    .count()
    
// Window
var streamingSelectDF = 
  streamingInputDF
   .select(get_json_object(($"value").cast("string"), "$.zip"    ).alias("zip"),
           get_json_object(($"value").cast("string"), "$.hittime").alias("hittime"))
   .groupBy($"zip", window($"hittime".cast("timestamp"), "10 minute", "5 minute", "2 minute"))
   .count()

// File output with partition
var streamingSelectDF = 
  streamingInputDF
   .select(get_json_object(($"value").cast("string"), "$.zip").alias("zip"),    get_json_object(($"value").cast("string"), "$.hittime").alias("hittime"), date_format(get_json_object(($"value").cast("string"), "$.hittime"), "dd.MM.yyyy").alias("day"))
    .groupBy($"zip") 
    .count()
    .as[(String, String)]

val query =
  streamingSelectDF
    .writeStream
    .format("parquet")
    .option("path", "/mnt/sample/test-data")
    .option("checkpointLocation", "/mnt/sample/check")
    .partitionBy("zip", "day")
    .trigger(ProcessingTime("25 seconds"))   // .trigger(processingTime="10 seconds")
    .start()



ALTER TABLE test_par ADD IF NOT EXISTS 
PARTITION (zip='38907', day='08.02.2017')
LOCATION '/mnt/sample/test-data/zip=38907/day=08.02.2017'
    
   
// Memory output
import org.apache.spark.sql.streaming.ProcessingTime

val query =
  streamingSelectDF
    .writeStream
    .format("memory")           // can be "console" for console output , but then without queryName
    .queryName("isphits")     
    .outputMode("complete") 
    .trigger(ProcessingTime("25 seconds"))
    .start()
    

// JDBC SINK
import java.sql._

class  JDBCSink(url:String, user:String, pwd:String) extends ForeachWriter[(String, String)] {
      val driver = "com.mysql.jdbc.Driver"
      var connection:Connection = _
      var statement:Statement = _
      
    def open(partitionId: Long,version: Long): Boolean = {
        Class.forName(driver)
        connection = DriverManager.getConnection(url, user, pwd)
        statement = connection.createStatement
        true
      }

      def process(value: (String, String)): Unit = {
        statement.executeUpdate("INSERT INTO zip_test " + 
                "VALUES (" + value._1 + "," + value._2 + ")")
      }

      def close(errorOrNull: Throwable): Unit = {
        connection.close
      }
   }
// noe we can use
val url="jdbc:mysql://<mysqlserver>:3306/test"
val user ="user"
val pwd = "pwd"

val writer = new JDBCSink(url,user, pwd)
val query =
  streamingSelectDF
    .writeStream
    .foreach(writer)
    .outputMode("update")  // update, complete, append,    https://databricks.com/blog/2016/07/28/structured-streaming-in-apache-spark.html
    .trigger(ProcessingTime("25 seconds"))
    .start()
    
    
    
// KAFKA SINK
import java.util.Properties
import kafkashaded.org.apache.kafka.clients.producer._
import org.apache.spark.sql.ForeachWriter

class  KafkaSink(topic:String, servers:String) extends ForeachWriter[(String, String)] {
      val kafkaProperties = new Properties()
      kafkaProperties.put("bootstrap.servers", servers)
      kafkaProperties.put("key.serializer", "kafkashaded.org.apache.kafka.common.serialization.StringSerializer")
      kafkaProperties.put("value.serializer", "kafkashaded.org.apache.kafka.common.serialization.StringSerializer")
      val results = new scala.collection.mutable.HashMap[String, String]
      var producer: KafkaProducer[String, String] = _

      def open(partitionId: Long,version: Long): Boolean = {
        producer = new KafkaProducer(kafkaProperties)
        true
      }

      def process(value: (String, String)): Unit = {
          producer.send(new ProducerRecord(topic, value._1 + ":" + value._2))
      }

      def close(errorOrNull: Throwable): Unit = {
        producer.close()
      }
   }

val topic = "<topic2>"
val brokers = "<server:ip>"

val writer = new KafkaSink(topic, brokers)

val query =
  streamingSelectDF
    .writeStream
    .foreach(writer)
    .outputMode("update")
    .trigger(ProcessingTime("25 seconds"))
    .start()


    
    
// SPARK SCHEDULER POOLS   https://spark.apache.org/docs/latest/job-scheduling.html#fair-scheduler-pools
// Run streaming query1 in scheduler pool1
spark.sparkContext.setLocalProperty("spark.scheduler.pool", "pool1")
df.writeStream.queryName("query1").format("parquet").start(path1)

// Run streaming query2 in scheduler pool2
spark.sparkContext.setLocalProperty("spark.scheduler.pool", "pool2")
df.writeStream.queryName("query2").format("orc").start(path2)


// read NOT a stream from Kafka
val df = spark.read.format("kafka").option("subscribe", "topic").load()
df.registerTempTable("topicData")
spark.sql("select value from topicData")








// http://cdn2.hubspot.net/hubfs/438089/notebooks/spark2.0/Structured%20Streaming%20using%20Scala%20DataFrames%20API.html
// python http://cdn2.hubspot.net/hubfs/438089/notebooks/spark2.0/Structured%20Streaming%20using%20Python%20DataFrames%20API.html

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


// CHECKPOINTING  METADATA

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
  .option("latestFirst", "true")
  .option("maxFilesPerTrigger", 1)  // Treat a sequence of files as a stream by picking one file at a time !!!
  .option("maxFilesPerTrigger", "20")  // limit how many files to process every time
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




//we will start a StreamingQuery that runs continuously to transform new data as it arrives.
val streamingETLQuery = df //streamed DF
  .withColumn("date", $"timestamp".cast("date") // derive the date from timestamp column
  .writeStream
  .trigger(ProcessingTime("10 seconds")) // check for new files every 10s
  .format("parquet") // write as Parquet partitioned by date
  .partitionBy("date")
  .option("path", "/cloudtrail")
  .option("checkpointLocation", "/cloudtrail.checkpoint/")
  .start()


// Save our previous xxx query to an in-memory table
countsDF.writeStream.format("memory")
  .queryName("xxx")
  .outputMode("complete")     // complete for aggregation queries, append for non-aggregate
  .start()
// Then any thread can query the table using SQL
sql("select sum(count) from xxx where action=’login’")





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


//FRANK
import org.apache.spark.streaming.kafka._
import kafka.serializer.StringDecoder
object KafkaExample {
  def main(args: Array[String]) {
    val ssc = new StreamingContext("local[*]", "example", Seconds(1))
    val kafkaParams = Map("metadata.broker.list" -> "localhost:9092")
    val topics = List("testLogs").toSet
    val lines = KafkaUtils.createDirectStream[String, String, StringDecder, StringDecder] (ssc, kafkaParams, topics).map(_._2)  // to only get messages
    val requests = lines.map(x => { val matcher:Matcher = pattern.matcher(x); if (matcher.matches()) matcher.group}) // extracts the request field from each log line
    val urls = requests.map(x => { val arr = x.toString().split(" ") ; if (arr.size == 3) arr(1) else "[error]"}) // extracts URL from request
    val urlCounts = urls.map(x => (x,1)).reduceByKeyAndWindow(_+_, _-_, Seconds(300, Seconds(1)))   // reduce by URL over 5-min window sliding every second
    val sortedResults = urlCounts.transform(rdd => rdd.sortBy(x => x._2, false)).print()

  }
}



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
  .avg("col1")
  .orderBy("window")
// Start running the query that prints the windowed word counts to the console
val query = windowedCounts.writeStream
  .outputMode("complete")
  .format("console")
  .option("truncate", "false")
  .start()


  


val inputDF = spark.readStream.json("s3://logs")
inputDF.groupBy($"action", window($"time", "1 hour")).count()   // tumbling, i.e. non-overlapping
  .writeStream.format("jdbc").start("jdbc:mysql//...")


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





//http://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.sql.streaming.GroupState
//https://github.com/apache/spark/blob/v2.3.1/examples/src/main/scala/org/apache/spark/examples/sql/streaming/StructuredSessionization.scala
mapGroupsWithState - how to use
1. define data structures
case class UserAction( userId: String, action: String)    // input
case class UserStatus( userId: String, active: Boolean)   // state data + output event

2. define function to update state each of grouping key using new data
def updateState {
  userId: String,                                   // grouping key
  actions: Iterator[UserAction],                    // new data
  state: GroupState[UserStatus]): UserStatus = {    // previous state , previous status of this user
    val prevStatus = state.getOption.getOrElse {
      new UserStatus()                              // get previous status
    }
    actions.foreach { action => prevStatus.updateWith(action) }   // update user status with actions
    state.update(prevStatus)                        // update state with latest user status
    return prevStatus                             
  }
}

3. use the user-defined function on a grouped Dataset
userActions.groupByKey(_.userId).mapGroupsWithState(updateState)




// TIMEOUTS, e.g. mark user as inactive when there is no actions for 1 hour
// https://youtu.be/hyZU_bw1-ow?t=1842
// when a group does not get any event for a while , then function is called with empty Iterator and  hasTimedOut=true
userActions.groupByKey(_.userId).mapGroupsWithState(timeoutConf)(updateState)
1. you need to set withWatermark
userActions.withWatermark("timestamp").....mapGroupsWithState(EventTimeTimeout)(updateState)

2. update function to add explicitly timeout
def updateState(...): UserStatus = {
  if (!state.hasTimedOut) {
    state.setTimeoutTimestamp(maxActionTimestamp, "1 hour")  
  } else {
    userStatus.handleTimeout()    // need to set up each time, as function is executed each time
    state.remove()
  }
}



val progress = query.lastProgress
print (progress.json)
query.awaitTermination()
query.exception()
query.sourceStatuses()
query.sinkStatus()




windows()
reduceByWindow()
reduceByKeyAndWindow()
  val hashTagCounts = hashtagKeyValues.reduceByKeyAndWindow( (x,y) => x+y, (x,y) => x-y, Seconds(300), Seconds(1))  // windowSize, slidingInterval
  // batched going IN  +  ,  and those going OUT of the window -
  wordStream.checkpoint(checkpointInterval)


  val sortedResults = hashTagCounts.transform( rdd => rdd.sortBy(x => x._2, false))
updateStateyKey() - mainstane state accross many batches, e.g. running count

https://www.coursera.org/learn/big-data-analysis/home/week/6


// STATEFUL: GLOBAL AGGREGATIONS
val stateSpec = StateSpec.function(updateState _)
  .initialState(initialRDD)
  .numPartitions(100)
  .partitioner(MyPartitioner()) //or hash
  .timeout(Minutes(120))
val wordCountState = wordStream.mapWithState(stateSpec)

def updateState(batchtime: Time,  // current batch time
                key: String,      // a word in the input stream
                value: Option[Int],  // current value (= 1)
                state: State[Long])  // counts so far for the word
      : Option[(String, Long)]        // the word and its new count



// Achieving good throughput (2016), this below is better than repartition
val numStreams = 5
val inputStreams = (1 to numStreams).map(i => context.socketStream(...))
val fullStream = context.union(inputStreams)
fullStream.map(...).filter(...).saveAsHadoopFile(...)


// combine SQL and Stream processing
inputStream.foreachRDD { rdd => 
  val df = SQLContext.createDataFrame(rdd)
  df.select(...).where(...).groupBy(...)
}

dstream.foreachRDD { rdd =>
  rdd.cache()
  val alternatives = restServer.get("/v1/alternatives").toSet // executed on Driver
  alternatives.foreach { a =>
    val asRecords = rdd.map(e => asRecord(e))
    asRecords.foreachPartition { p =>
      val conn = DB.connect(server)
      p.foreach(e => conn.insert(e))                  // on workers
    }
  }
  rdd.unpersist(true)
}

// how to restart driver  (in spark-defaults.conf),  e.g. accept 2 failuter per 1h
spark.yarn.maxAppAttempts=2
spark.yarn.am.attemptFailuresValidityInterval=1h

// spark has configurable metrics system,  based on DropWizard Metrics Library,   we can drop to Grafana/Graphite

// enable WAL for RECEIVER based sources
// data on receiver is stored in executor memory which can crash
// with WAL data is written to durable storage (HDFS, S3), before being ack back to source
spark.streaming.receiver.writeAheadLog.enable=true
spark.streaming.receiver.writeAheadLog.closeFileAfterWrite=true // for S3
// but WAL creates additional copy, so 
StorageLevel.MEMORY_AND_DISK_SER   -> use for your input DStreams, disable in memory replication (already replicated in hdfs)
// for Kafka no need for WAL, use DIRECT CONNECTOR

// shutdown gracefully
run in CLUSTER mode, (and --supervise when standalone)
//spark-submit -master <masterURL> -kill <driverID>
spark.streaming.stopGracefullyOnShutdown=true

StreamingContext.stop(stopSparkContext = true, stopGracefully = true)    // delete file with marker file on hdfs, you delete

// restructuring code for Checkpointing
def creatingFunc(): StreamingContext = {
  val context = new StreamingContext(...)
  val lines = KafkaUtils.createStream(...)
  val words = lines.flatMap(...)
  ...
  context.checkpoint(hdfsDir)
}
//pull all setup code into a function that returns a new StreamingContext
val context = StreamingContext.getOrCreate(hdfsDir, creatingFunc)
context.start()


unionedStream.repartition(40) // more receivers@





// ConstantInputDStream, always returns same RDD on each step, useful for testing
// https://youtu.be/iIo1hN-2JEI
class ConstantInputDStream[T: Classtag](_ssc: StreamingContext, rdd: RDD[T])

val constantDStream = new ConstantInputDStream(ssc, rdd)

// but we can generate data, by e.g. creating function that generates data, and applying it each time to generate dataset
import scala.util.Random
val sensorId: () => Int = () => Random.nextInt(sensorCount)
val data: () => Double = () => Random.nextDouble
val timestamp: () => Long = () => System.currentTimeMillis
val recordFunction: () => String = { () => 
  if (Random.nextDouble < 0.9) { Seq(sensorId().toString, timestamp(), data()).mkString(",") }
  else { "||-corrupt-^&##$" }
}
val sensorDataGenerator = sparkContext.parallelize(1 to n).map(_ => recordFunction)   // to wszystko   RDD[() => Record]
val sensorData = sensorDataGenerator.map(recordFun => recordFun())
val rawDStream = new ConstantInputDStream(streamingContext, sensorData)

// ConstantInputDStream + foreachRDD = Reload External Data periodically 
var sensorRef = sparkSession.read.parquet(s"$refFile") 
sensorRef.cache()

val refreshDStream = new ConstantInputDStream(streamingContext, sparkContext.emptyRDD[Int])
val refreshIntervalDStream = refreshDStream.window(Seconds(300), Seconds(300))   // every 5min
refreshIntervalDStream.foreachRDD { _ => 
  sensorRef.unpersist(false)
  sensorRef = sparkSession.read.parquet(s"$refFile")
  sensorRef.cache()
  }


// DStream + foreachRDD = REload External Data with a Trigger
// we are transforming one DStream in another
var sensorRef = sparkSession.read.parquet(s"$refFile") 
sensorRef.cache()

val triggerRefreshDStream: DStream = // create, e.g. kafka

val referenceStream = triggerRefreshDStream.transform { rdd =>
  if (rdd.take(1) == "refreshNow") {
    sensorRef.unpersist(false)
    sensorRef = sparkSession.read.parquet(s"$refFile")
    sensorRef.cache()
  }
  sensorRef.rdd
}
incomingStream.join(referenceStream) ....


// Keeping Arbitrary state (BAD!!!!!, rosnie wykladniczo!!!)   https://gist.github.com/maasg/9d51a2a42fc831e385cf744b84e80479
val baseline: Dataset[Features] = sparkSession.read.parquet(file).as[Features]
...
stream.foreachRDD { rdd =>
  val incomingData = sparkSession.createDataset(rdd)
  val incomingFeatures = rawToFeatures(incomingData)
  val analyzed = compare(incomingFeatures, baseline)
  baseline = (baseline union incomingFeatures).filter(isExpired)
}


// rolling roll own checkpoints
cycle = (cycle + 1) % checkpointInterval
if (cycle == 0) {
  checkpointFile = (checkpointFile + 1) % 2
  baseline.write.mode("overwrite").parquet(s"$targetFile_$checkpointFile")
  baseline = baseline.read(s"$targetFile_$checkpointFile")
}

// Probabilistic Accululator   https://github.com/StreamProcessingWithSpark/HLLAccumulator