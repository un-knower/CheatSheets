import java.util

import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.collection.JavaConverters._

object ConsumerExample extends App {

  import java.util.Properties

  val TOPIC="test"

  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "something")
  props.put("enable.auto.commit", "true")
  props.put("auto.commit.interval.ms", "1000")  // every 1 second, offset will be commited
  props.put("auto.offset.reset", "earliest")  // read doc!


  val consumer = new KafkaConsumer[String, String](props)

  consumer.subscribe(util.Collections.singletonList(TOPIC))   // you may specify list of topics
  consumer.subscribe(Array.asList ("topic1", "topic2"));
  consumer.subscribe(Arrays.asList("name.*", "address.*"));

  while(true){
    val records=consumer.poll(100)      // org.apache.kafka.clients.consumer.ConsumerRecord   ConsumerRecords
    for ( record <- records.asScala ) {
     println(record)
     
     //alternatively!
     // val consumerRecords = new org.apache.kafka.clients.consumer.ConsumerRecords[String, String]
     // consumerRecords = consumer.poll(100)
     // val consumerRecord = new org.apache.kafka.clients.consumer.ConsumerRecord[String, String] 

     // for (consumerRecord <- consumerRecords) {
     //  consumerRecord.value()
     //  consumerRecord.key()
     //  consumerRecord.offset()
     //  consumerRecord.partition()
     //  consumerRecord.topic()
     //  consumerRecord.timestamp()
     // }
    }

    // if we dont set up auto.commit.interval.ms, then we need to force commit
    consumer.commitSync()  // or  Asyns
  }
}