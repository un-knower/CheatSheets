from pyspark.sql import SparkSession
from pyspark.sql.functions import *

spark = SparkSession.builder \
  .master("local[*]") \
  .appName("name") \
  .config("some-option", "value") \
  .config(conf=SparkConfig()) \
  .getOrCreate()

# to set/get configuration
spark.sparkContext.getConf().getAll()
spark.sparkContext.getConf().get("spark.driver.port")
spark.conf.get("spark.driver.port")

spark.sparkContext.setLocalProperty("key", "value")
spark.sparkContext.getLocalProperty("key")
spark.conf.set("key", "val")
spark.conf.get("key")

# ## UDF
sqlContext.registerFunction("stringLengthString", lambda x: len(x))
sqlContext.registerFunction("stringLengthInt"   , lambda x: len(x), IntegerType())
sqlContext.sql("SELECT stringLengthString('test')").collect()
[Row(stringLengthString(test)=u'4')]  # for string
[Row(stringLengthInt(test)=4)]        # for int
F1 = udf(lambda x: -1 if x not in not_found_cat else x, DoubleType())   # typ Double tutaj musi = temu w DF !!
df.withColumn("nowa", F1(df['nr'])).show()                              # tutaj "nr" jest typu Double

### DATA FRAME
# CREATE 1
data = [(1, Row(name='Alice', age=2))]
df = spark.createDataFrame(data, ("key", "value"))

# CREATE 2
g = sc.textFile("/g").filter(lambda x: x[0] != '#').map(lambda x: x.split('\t')).map(lambda p: Row(taxid=int(p[0]), geneid=int(p[1]), pmid=int(p[2])))
schemaGene2Pubmed = sqlContext.inferSchema(g)
schemaGene2Pubmed.registerTempTable("gene2pubmed")

#AGG
people.join(department, people.deptId == department.id).groupBy(department.name, "gender").agg({"salary": "avg", "age": "max"})
df.agg({"age": "max"}).collect() # albo podajesz dict
df.agg(min(df['age']).collect()  # albo func(col)
       
       
#JOIN   inner, cross, outer, full, full_outer, left, left_outer, right, right_outer, left_semi, and left_anti
cond = [df.name == df3.name, df.age == df3.age]
df.join(df3, cond, 'outer').select(df.name, df3.age).collect()
       
# SELECT -> allows SQL expressions
df.selectExpr("age * 2", "abs(age)").collect()
       
#WHEN CASE
df.select(df.name, F.when(df.age > 4, 1).when(df.age < 3, -1).otherwise(0 - or a column here)).show()
df.select(when(df['age'] == 2, 3).otherwise(4).alias("age")).collect()
df.select(when(df.age == 2, df.age + 1).alias("age")).collect()
df.where(col('col1').like("%string%")).show()  # where = filter
       
### FUNCTIONS
# ADD MONTHS
df = spark.createDataFrame([('2015-04-08',)], ['d'])
df = spark.createDataFrame([('1997-02-28 10:30:00',)], ['t'])
df.select(add_months(df.d, 1).alias('d')).collect()
# DAYS
df.select(date_add(df.d, 10).alias('d')).collect()  # add 10 days
df.select(date_sub(df.d, 10).alias('d')).collect()  # subtract 10 days
df.select(last_day(df.d).alias('date')).collect()   # last day of month
df.select(next_day(df.d, 'Sun').alias('date')).collect()   # select next week day , eg. next Monday 'Mon', 'Tue'....
# CURRENT
df.withColumn('timestamp',current_timestamp()).withColumn('data', current_date()).show(5,False)
# FORMATTING
df.select(date_format('d', 'MM/dd/yyy').alias('date')).collect()   # formt existing date to new format
df.select(from_unixtime(timestamp, format='yyyy-MM-dd HH:mm:ss')....
df.select(unix_timestamp(timestamp=None, format='yyyy-MM-dd HH:mm:ss').....
df.select(from_utc_timestamp(df.t, "PST").alias('t')).collect()
# TIME
df.select(to_utc_timestamp(df.t, "PST").alias('t')).collect()     # [Row(t=datetime.datetime(1997, 2, 28, 18, 30))]
>>> df = spark.createDataFrame([('1997-02-28',)], ['d'])       # format – ‘year’, ‘YYYY’, ‘yy’ or ‘month’, ‘mon’, ‘mm’
>>> df.select(trunc(df.d, 'year').alias('year')).collect()
[Row(year=datetime.date(1997, 1, 1))]
>>> df.select(trunc(df.d, 'mon').alias('month')).collect()
[Row(month=datetime.date(1997, 2, 1))]

# ARRAY CONTAINS
df = spark.createDataFrame([(["a", "b", "c"],), ([],)], ['data'])
df.select(array_contains(df.data, "a")).collect()
# UNIQUE/DISTINCT
df.select(collect_set('name')).show(25, False)      # zwraca set/list
df.select(col('name')).distinct().show(25, False)   # zwraca DF

df.select(create_map('name', 'age').alias("map")).collect() # zwraca MAP(kowalski -> 100)
# EXPLODE
eDF = spark.createDataFrame([Row(a=1, intlist=[1,2,3], mapfield={"a": "b"})])
eDF.select(explode(eDF.intlist).alias("anInt")).collect()

#EXPR
df.select(expr("length(nr)")).show()       # to jakby wpisac (length('name'))
# SEARCH
df.select(instr(df.s, 'tegoszukamy').alias('s')).collect()  # pozycja jest od 1 (nie od 0 !!!). Zwraca 0 gdy nie znaleziono



# APPLY SCHEMA
raw_data = sc.textFile("nasdaq.csv")
from collections import namedtuple
Record = namedtuple("Record", ["date", "open", "high", "low","close","adj_close","volume"])
def parse_record(s):
	fields = s.split(",")
	print (len(fields))
	#return Record(fields[0], float(fields[1]), float(fields[2]), float(fields[3]), float(fields[4]), float(fields[5]), int(fields[6]))
	return Record(fields[0], *map(float, fields[1:6]), int(fields[6]))

parsed_data = raw_data.map(parse_record)
# [Record(date='2017-05-26', open=26.1, high=26.360001, low=25.780001, close=26.219999, adj_close=25.93614, volume=81100),...]

parsed_data.map(lambda x: x.date).min()	# gives min

with_month_data = parsed_data.map(lambda x: (x.date[:7], x))	# bierzemy tylko 2017-01
# [('2017-01', Record(...))]
by_month_data = with_month_data.mapValues(lambda x: x.volume)
# [('2017-01', 81100), ('2017-02', 145330)...]
by_month_data = by_month_data.reduceByKey(lambda x,y : x + y)
# [('2017-01', 1000000)]
result_data = by_month_data.map(lambda t: ",".join(map(str, t)))


##############################################################################
# ACCUMULATOR
##############################################################################
# we can use it to measure execution time of some func
time_spent = sc.accumulator(0.0)

def timed_some_function(x):
	import time
	before = time.time()
	result = some_function(x)
	after = time.time()
	time_spent.add(after - before)
	# sc.parallelize([1, 2, 3, 4]).foreach(lambda x: accum.add(x))
	return result

# we now apply function and check accumulator
xxx = parsed_data.map(lambda x: timed_some_function(x.volume)).collect()
time_spent.value	# time


# check longest time of updating a database, i.e. max
from pyspark import AccumulatorParam
class MaxAccumulatorParam(AccumulatorParam):
	def zero(self, initial_value):
		return initial_value

	def addInPlace(self, accumulator, delta):
		return max(accumulator, delta)

max_time = sc.accumulator(0.0, MaxAccumulatorParam())
def persist_to_external_storage(iterable):
	for record in iterable:
		max_time.add(after - before)

parsed_data.foreachPartition(persist_to_external_storage)

##############################################################################
# BROADCAST
##############################################################################
params = sc.broadcast({"mu": 191919, "sigma": 303030})
params.value(["mu"])	#191919
  


##############################################################################
# SOME EXAMPLES
##############################################################################
def parse_article(line):
    try:
        article_id, text = unicode(line.rstrip()).split('\t', 1)
    except ValueError as e:
        return []
    text = re.sub("^\W+|\W+$", "", text, flags=re.UNICODE)		# strip punctation marks from words
    words = re.split("\W*\s+\W*", text, flags=re.UNICODE)
    return words

wiki = sc.textFile("/data/wiki/en_articles_part1/articles-part", 16).map(parse_article)
result = wiki.take(1)[0]

for word in result[:50]:
    print word


###########