import org.apache.kafka.clients.producer.KafkaConsumer;
import org.apache.kafka.clients.producer.Consumer;
import org.apache.kafka.clients.producer.ConsumerRecord;

private Properties myProps = new Properties();
myProps.put("bootstrap.servers", "localhost:9091,localhost:9092");
myProps.put("key.serializer",   "org.apache.kafka.common.serialization.StringDesrializer");
myProps.put("value.serializer", "org.apache.kafka.common.serialization.StringDeserializer");
//myProps.put("key.serializer",   "io.confluent.kafka.serializers.KafkaAvroDeserializer"); ??
//myProps.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer"); ??
myProps.put("group.id", "MyGroup");

consumer = new KafkaConsumer<String, String>(myProps);

// need to specify topics consumer will read
consumer.subscribe(Collections.singletonList("topic1"));
consumer.subscribe(Array.asList ("topic1", "topic2"));
consumer.subscribe(Arrays.asList("name.*", "address.*"));

// partitions are assigned by assign() function

// POLL LOOP
try {
    while (true) {
        ConsumerRecord<String,String> records = consumer.poll(100);  //short timeout 100ms
        ConsumerRecord<String,String> records = consumer.poll(Long.MAX_VALUE);  //wait a lot
        for (ConsumerRecord<String,String> record : records)
            System.out.println(record.offset() + ":" + record.value() );
    }
} catch (WakeupException e) {    // if Long.MAX_VALUE used
} finally {
    consumer.close();
}
