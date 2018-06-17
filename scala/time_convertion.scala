// UDF functions for SQL-like operations on columns
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

import java.sql.Timestamp
import org.apache.spark.sql.functions.udf

val  dayofweek = udf( (ts: Timestamp, tz: String) => {
  val dt = new DateTime(ts,DateTimeZone.forID(tz))
  // Monday starts at 1, but we would like to count from zero
  dt.getDayOfWeek() -1
})

val  localhour = udf( (ts: Timestamp, tz: String) => {
  val dt = new DateTime(ts,DateTimeZone.forID(tz))
  dt.getHourOfDay()
})

val newyork_tz = "America/New_York"

val df = df_ny.
  withColumn("dow",  dayofweek($"ts", lit(newyork_tz))).
  withColumn("hour", localhour($"ts", lit(newyork_tz))).
  as("events")

df.printSchema()