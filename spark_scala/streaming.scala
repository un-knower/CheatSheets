// Dstream object breaks up stream into distincts RDD's
val stream = new StreamingContext(conf, Seconds(1))
val lines  = stream.socketTextStream("localhost", 8888)
val errors = lines.filter(_.contains("error"))
stream.start()
stream.awaitTermination()



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


val inputDF = spark.readStream.json("s3://logs")
inputDF.groupBy($"action", window($"time", "1 hour")).count()
	.writeStream.format("jdbc").start("jdbc:mysql//...")

	

windows()
reduceByWindow()
reduceByKeyAndWindow()
  val hashTagCounts = hashtagKeyValues.reduceByKeyAndWindow( (x,y) => x+y, (x,y) => x-y, Seconds(300), Seconds(1))
  val sortedResults = hashTagCounts.transform( rdd => rdd.sortBy(x => x._2, false))
updateStateyKey() - mainstane state accross many batches, e.g. running count