// this starts/stops local spark context, so we dont have to do input
// and it also keeps it running so it doesnt start, stop each time
trait SharedSparkContext extends BeforeAndAfterAll { self: Suite =>
    @transient private var _sc: SparkContext = _
    def sc: SparkContext = _sc

    var conf = new SparkConf(false)

    override def beforeAll() {
        _sc = new SparkContext("local[*]", "test", conf)
        super.beforeAll()
    }

    override def afterAll() {
        LocalSparkContext.stop(_sc)
        _sc = null
        super.afterAll()
    }
}


class filterWordCountSuite extends FunSuite with SharedSparkContext {
	test("testFilterWC") {
		val filterWordSetBc = sc.broadcast(Set[String]("the", "a", "is"))
		val inputRDD = sc.parallelize(Array("the cat and the hat", "the car is blue", "the cat is in a car", "the cat lost his hat"))
		val filteredWordCount: RDD[(String, Int)] = FilterWordCount.filterAndCount(filterWordSetBc, inputRDD)

		assert(filteredWordCount.count() == 8)
		val map = filteredWordCount.collect().toMap
		assert(map.get("cat").getOrElse(0) == 3)
		assert(map.get("the").getOrElse(0) == 0)
	}
}

class sampleRDDtest extends FunSuite with SharedSparkContext {
	test("reallysimple") {
		val input = List("hi holden", "bye")
		val expected = List(List("hi", "holden"), List("bye"))
		assert(SampleRDD.tokenize(sc.parallelize(input)).collect().toList === expected)
	}
}


// Stream
class StreamFilterWordCount extends TestSuiteBase {
	test("mapPartitions") {
		assert(numInputPartitions === 2, "must be 2")
		val input = Seq( Seq("the cat is in the hat", "the car is blue"),
						 Seq("the cat is in the car", "the cat lost his hat"))
		val output = Seq( Seq(9), Seq(11))
		val operation = (r: DStream[String]) => StreamingWordCount.wordCount(r)
		testOperation(input, operation, output)
	}
	test("simple") {
		val input = List(1,2)
		val expected = List(List("1"), List("2"))
		testOperation[String,String](input, tokenize _, expected, useSet = true)
	}
}



// DF
test("DF should be qeual") {
	val sqlCtx = sqlContext
	import sqlCtx.implicits._
	val input = sc.parallelize(inputList).toDF
	equalDataFrames(input, input)
}

// if we know schema, we can use Scala Generator to create it for us
test("assert rows types like schema type") {
	val schema = StructType(List(StructField("name, StringType")))
	val rowGen: Gen[Row] = DataFrameGenerator.getRowGenerator(schema)
	val property = forAll(rowGen) {
		row => row.get(0).isInstanceOf[String]
	}
	check(property)
}

// Random RDD
val zipRDD = RandomRDDs.exponentialRDD(sc, mean=1000, size=rows).map(_.toInt.toString)
val valuesRDD = RandomRDDs.normalVectorRDD(sc, numRows=rows, numCols=numCols).repartition(zipRDD.partitions.size)
val keyRDD = sc.parallelize(1L.to(rows), zipRDD.getNumPartitions)
keyRDD.zipPartitions(zipRDD, valuesRDD) {
	(i1, i2, i3) => new Iterator[(Long, String, Vector)] { ....}
}



// Counters, Validations
val vc = new ValidationConf(tempPath, "1", true,
	List[ValidationRule](new AbsoluteSparkCounterValidationRule("recordsRead", Some(30), Some(1000))))
val sqlCtx = new SQLContext(sc)
val v = Validation(sc, sqlCtx, vc)
//...do work here
asser(v.validate(5) === true)




def compareWithOrderSamePartitioner[T: ClassTag](expected: RDD[T], result: RDD[T]): Option[(T, T)] = {
	expected.zip(result).filter{ case (x,y) => x != y }.take(1).heapOption
}







test("should parse csv with numbers") {
    val input = sc.parallelize(List("1,2"))
    val result = input.map(parseLine)
    result.collect() should equal (Array[Double](1.0, 2.0))
}
// testing with DStreams
class SampleStreamingTest extends StreamingSuiteBase {
    test("really simple transform") {
        val input = List(List("hi"), List("hi holden"), List("bye"))
        val expect= List(List("hi"), List("hi", "holden"), List("bye"))
        testOperation[String, String](input, tokenize _, expect, useSet = true)
    }
}


test("RunningCountringSQL") {
	val rdd = sqlContext.sparkContext.parallelize(Array(Row("bob", "1", "1"), Row("bob", "1", "1")))
	val results = RunCountingSQL.runCountingSQL(sqlContext)
	assert(results.length == 2)
}

test("map should not change number of elements") {
	implicit val generatorDrivenConfig = 
		PropertyCheckConfig(minSize=0, maxSize=100000)
	forAll(RDDGenerator.genRDD[String](sc)) {
		rdd => rdd.map(_.length).count() == rdd.count()
	}
}



BAD:
val splitLines = inFile.map(line => {
    val reader = new CSVReader(new StringReader(line))
    reader.readNext()
})


GOOD:
def parseLine(line: String): Array[Double] = {
    val reader = new CSVReader(new StringReader(line))
    reader.readNext().map(_.toDouble)
}

then we can:
test("Should parse a csv with numbers") {
    MoreTestableLoadCsvExample.parseLine("1,2") should equal (Array[Double](1.0, 2.0))
}



BAD:
def difficultRDD(input: RDD[String]) = { input.flatMap(line => line.split(" "))}

GOOD:
def tokenizeRDD(input: RDD[String]) = { input.flatMap(tokenize) }
protected[tokenize] def tokenize(line: String) = { line.split(" ") }





// UNIT TO TEST
class WordCount {
	def get(url: String, sc: SparkContext): RDD[(String, Int)] = {
		val lines = sc.textFile(url)
		lines.flatMap(_.split(" ").map((_, 1)).reduceByKey(_+_)
	}
}
// METHOD1 of testing  - before/after must be repeated in all test suites - problem!
import org.scalatest. { BeforeAndAfterAll, FunSuite }
class WordCountTest extends FunSuite with BeforeAndAfterAll {
	private var sparkConf: SparkConf = _
	private var sc: SparkContext = _
	override def beforeAll() {
		sparkConf = new SparkConf().setAppName().setMaster("local")
		sc = new sparkContext(sparkConf)
	}
	private val wordCount = new WordCount
	test("test1") {
		val result = wordCount.get("file.txt", sc)
		assert(result.take(10).length === 10)
	}
	override def afterAll() {
		sc.stop()
	}
}





class xxx extends... {
	private val master = "local[2]"
	private val appName = "example"
	val sc: SparkContext = _
	val ssc = StreamingContext = _
	private val checkpointDirectory = Files.createTempDirectory(appName).toString

	override def beforeAll() {
		val conf = new SparkConf().setMaster(master).setAppName(appName)
		sc = SparkSession.builder().config(conf).getOrCreate().sparkContext
		ssc = new StreamingContext(sc, Seconds(10))
	}
	override def afterAll() {
		if (sc != null) {
			sc.stop()
			new File(checkpointDirectory).delete()
		}
	} // https://github.com/ganeshayadiyala/Scalatest-library-to-unit-test-spark
}




// MAVEN DEPENDENCY
<dependency>
	<groupId>org.apache.spark</groupId>
	<artifactId>spark-core_${scala.binary.version}</artifactId>
	<version>${spark.version}</version>
	<type>test-jar</type>
	<scope>test</scope>
</dependency>

<dependency>
	<groupId>org.scalatest</groupId>
	<artifactId>scalatest_${scala.binary.version}</artifactId>
	<version>2.2.1</version>
	<scope>test</scope>
</dependency>


