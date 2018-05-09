import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

private Properties myProps = new Properties();
myProps.put("bootstrap.servers", "localhost:9091,localhost:9092");
myProps.put("key.serializer",   "org.apache.kafka.common.serialization.StringSerializer");
myProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//myProps.put("key.serializer",   "io.confluent.kafka.serializers.KafkaAvroSerializer");
//myProps.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
// default:   ByteArraySerializer, StringSerializer, IntegerSerializer
myProps.put("acks", "all");   // producer -> broker  are success on partition, how many parts should reply with success
// 0  - high throughput, low latency, we dont care about response, just send info to Broker, no guarantee
// 1  - medium throughput/latency,  primary got it , but replica may not
// -1  =  all  - low throughput, high latency,  no loss, all replicas must respond
myProps.put("retries", 0);
//myProps.put("buffer.memory", 0);   // how much memory procuder should user
myProps.put("batch.size", 16384);
//myProps.put("linger.ms", 0);   //how long to wait till reach batch send anyway

// using SchemaRegistry for serializers
myProps.put("schema.registry.url", schemaUrl);
Producer<String,User> producer = new KafkaProducer<String,User>(myProps);
// end SchemaRegistry


producer = new KafkaProducer<String, String>(myProps);

ProducerRecord<String, String> record = 
    new ProducerRecord<>("CustomerPhone", "Sarah", "1-555-555-5555");
    // 2nd field is used to determine partition (by hash), round-robin for balance

// SIMPLE SEND send to Broker, doesnt care, no ACK, assumes it gets there    
try {
    producer.send(record);
} catch (Exception e) {
    e.printStackTrace();
}

//SYNCHRONOUS send  - check if result gets back
try {
    producer.send(record).get();  //SYNC !!!
} catch (Exception e) {
    e.printStackTrace();
}

//ASYNCHRONOUS send - callback function if response is received
private class MyCallback implements Callback {
    @Override
    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        if (e != null) {
            e.printStackTrace();
        }
    }
}
producer.send(record, new MyCallback());

// PYTHON
// from kafka import KafkaProducer
// producer = KafkaProducer(bootstrap_servers=["localhost:9091,localhost"])
// producer = KafkaProducer(retries=5)
// producer.send('CustomerPhone', key=b'Sarah', value=b'1-555-555')