from pyspark.sql import SparkSession

spark = SparkSession.builder \
  .master("local[*]") \
  .appName("name") \
  .config("some-option", "value") \
  .config(conf=SparkConfig()) \
  .getOrCreate()
  
