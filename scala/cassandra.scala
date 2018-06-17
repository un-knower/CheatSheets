sudo jupyter-toree install --spark_home=${SPARK_HOME} \
--spark_opts='--packages com.datastax.spark:spark-cassandra-connector_2.10:1.6.0 --conf spark.cassandra.connection.host=localhost'

// spark-cassandra connector
import com.datastax.spark.connector._
import com.datastax.spark.connector.cql._

import org.apache.spark.sql.cassandra.CassandraSQLContext
val cc = new CassandraSQLContext(sc)

val venues   = cc.sql("select vid, name from lbsn.venues").as("venues")

// in general
sqlContext.read.format("org.apache.spark.sql.cassandra").options(table="kv", keyspace="ks").load().show()


https://docs.datastax.com/en/dse/6.0/dse-admin/datastax_enterprise/spark/usingSparkModules.html

val events   = cc.sql("""select ts, uid, lat, lon, vid from lbsn.events where
                            lon>-74.2589 and lon<-73.7004 and 
                            lat> 40.4774 and lat< 40.9176
                      """).as("events").orderBy("uid", "ts").repartition(8, $"uid").cache()



val  nth = udf( (i:Int, arr: Vector) => {
  val v = arr.toArray.lift(i).getOrElse(0.0) 
  // more or less than average?
  v * arr.toArray.length
})

df_probs.select($"ts", $"uid", $"vid", $"dow", round(nth($"dow", $"dow_hist"),2).as("dow_trend")).show(3,false)