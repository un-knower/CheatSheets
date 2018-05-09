import org.apache.spark.sql.functions.{explode, split}
import org.apache.spark.sql.functions._


// Setup connection to Kafka
val streamingInputDF = spark.readStream
  .format("kafka")
  .option("kafka.bootstrap.servers", "YOUR.HOST:PORT1,YOUR.HOST:PORT2")   // comma separated list of broker:host
  .option("subscribe", "YOUR_TOPIC1,YOUR_TOPIC2")    // comma separated list of topics
  .option("startingOffsets", "latest") // read data from the end of the stream
  .load()
  
  
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
    .format("memory")           // can be "console" for console output 
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

val url="jdbc:mysql://<mysqlserver>:3306/test"
val user ="user"
val pwd = "pwd"

val writer = new JDBCSink(url,user, pwd)
val query =
  streamingSelectDF
    .writeStream
    .foreach(writer)
    .outputMode("update")
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

