object ProducerExample extends App {
 
 import java.util.Properties

 import org.apache.kafka.clients.producer._

 val  props = new Properties()
 props.put("bootstrap.servers", "localhost:9092")
 props.put("key.serializer"   , "org.apache.kafka.common.serialization.StringSerializer")
 props.put("value.serializer" , "org.apache.kafka.common.serialization.StringSerializer")
 props.put("acks", "1")
 props.put("retries", "3")
 props.put("linger.ms", "1")		// will send every 1 ms  (unless flush is used)

 val producer = new KafkaProducer[String, String](props)
   
 val TOPIC="test"
 
 for(i<- 1 to 50){
  val record = new ProducerRecord(TOPIC, "key", s"hello $i")		// same key = same partition
  producer.send(record)
 }
    
 val record = new ProducerRecord(TOPIC, "key", "the end "+new java.util.Date)
 producer.send(record)

// we may need to flush
// producer.flush()
 producer.close()
}