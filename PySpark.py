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


### UDF
sqlContext.registerFunction("stringLengthString", lambda x: len(x))
sqlContext.registerFunction("stringLengthInt"   , lambda x: len(x), IntegerType())
sqlContext.sql("SELECT stringLengthString('test')").collect()
[Row(stringLengthString(test)=u'4')]  # for string
[Row(stringLengthInt(test)=4)]        # for int
F1 = udf(lambda x: -1 if x not in not_found_cat else x, DoubleType())   # typ Double tutaj musi = temu w DF !!
df.withColumn("nowa", F1(df['nr'])).show()                              # tutaj "nr" jest typu Double

### DATA FRAME

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
df.where(col('col1').like("%string%")).show()  # where = filter
       
### FUNCTIONS
# ADD MONTHS
df = spark.createDataFrame([('2015-04-08',)], ['d'])
df = spark.createDataFrame([('1997-02-28 10:30:00',)], ['t'])
df.select(add_months(df.d, 1).alias('d')).collect()
# DAYS
df.select(date_add(df.d, 10).alias('d')).collect()  # add 10 days
df.select(date_sub(df.d, 10).alias('d')).collect()  # subtract 10 days
# CURRENT
df.withColumn('timestamp',current_timestamp()).withColumn('data', current_date()).show(5,False)
# FORMATTING
df.select(date_format('d', 'MM/dd/yyy').alias('date')).collect()   # formt existing date to new format
df.select(from_unixtime(timestamp, format='yyyy-MM-dd HH:mm:ss')....
df.select(from_utc_timestamp(df.t, "PST").alias('t')).collect()

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
          
