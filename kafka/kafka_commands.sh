# https://kafka.apache.org/11/documentation/streams/quickstart
# START ZOOKEPER  (all in bin/  folder
zookeeper-server-start.sh config/zookeeper.properties

# START KAFKA BROKER
kafka-server-start.sh config/server.properties

# create a topic named "test" with a single partition and only one replica:
# replication factor 1  = no redundancy, no backup
# partitions = 2  , split into 2 brokers
kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test

# list/alter/delete topics
kafka-topics.sh --list --zookeeper localhost:2181
kafka-topics.sh --describe --zookeeper localhost:2181  # partitions info
kafka-topics.sh --alter --zookeeper localhost:2181 --partitions <newINT> --topic <str>
kafka-topics.sh --delete <topic> --zookeeper localhost:2181 --topic <str>

# run Producer console
kafka-console-producer.sh --broker-list localhost:9092 --topic test
# run Consumer console
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning

# mirroring, replicating data between clusters (topics may vary in no. of part between clusters)
kafka-mirror-maker.sh --consumer.config consumer.properties --producer.config producer.properties --whitelist '*' --blacklist '*x'

# DISPLAY QUOTAS
kafka-configs.sh --zookeeper host:IP --describe --entity-type users --entity-name <consumer/group ID>
# MODIFY EXISTING QUOTAS
kafka-configs.sh --zookeper host:port --alter --add-config 'producer_byte_rate=2048,consumer_byte_rate=2048,request_percentage=200' --entity-type users --entity-name user1 --entity-type clients --entity-name <consumer/group ID>


# CONNECTORS - STANDALONE
connect-standalone.sh  -->  put params in  server.properties
offset.storage.file.filename  # offset for topics while reading
# CONNECTORS - DISTRIBUTED ,  dont include offset.storage.file.filename , as they do not use file, they use TOPICS
# passed config via JSON,   name, tasks.max,  connector.class,  key.converter, value.converted
group.id  # property, connects cluster
config.storage.topic  # default:   connect-configs topic,  connect-status
offset.storage.topic  # no choice for partitions, replication factors
status.storage.topic  


# TRANSFORMATIONS - used to modify data in transit
# one msg transformed at as time, create chain of transformations
org.apache.kafka.connect.transforms.TimestampRouter
InsertField,  ReplaceField,  MaskField,  ExtractField,  TimestampRouter, RegexRouter

transforms=tsRouter
transforms.tsRouter.type=org.apache....TimestampRouter
transforms.tsRouter.topic.format=${topic}-${timestamp}
transforms.tsRouter.timestamp.format=yyyyMMdd

# REST API, KAfka can be accessed as service,  responds on port 8083
GET /connectors/{name}/config
GET /connectors/{name}/status # running, paused
GET /connectors/{name}/task
GET /connectors/{name}/tasks/{taskId}/status
PUT /connecotrs/{name}/config
PUT /connecotrs/{name}/pause
PUT /connecotrs/{name}/resume
PUT /connecotrs/{name}/tasks/{taskId}/restart

# STREAMS, transform input topics into databases, external apps/services,
# records (not messages), k,v    small latency, fault tolerant,   stateless, stateful, windowing

# CONNECT vs STREAMs
Connect: separate process,  use REST API,  designed to move large data in/out Kafka
Streams: embedded code (java), no additional communications,  designed to create new data output





# ext4 filesystem tweak
# consider disabling journaling
#   data=writeback  (reduces latencynoatime
#   nobh    (used in conjunction with data=writeback)
#   commit=secs  (lower value to reduce crash)  10-15....
#   delalloc  (better throughpput)
#   noatime

vi /etc/sysctl.conf
vm.swappiness=1
net.core.wmem_default = 131072
net.core.rmem_default = 131072
net.core.wmem_mam = 2097152  # network speed
net.core.rmem_mam = 2097152  # network speed

sysctl -a 2> /dev/null | grep dirty
vm.dirty_background_bytes = 0
vm.dirty_background_ratio = 10  # eg 6.4 GB dirty pages from 64 GB ram
vm.dirty_bytes = 0
vm.dirty_expire_centisecs = 3000
vm_dirty_ratio = 20
vm_dirty_writeback_centisecs = 500
vm.dirtytime_expire_seconds = 43200

cat /proc/vmstat | egrep "dirty|writeback"
nr_dirty 134
nr_writeback 0
nr_writeback_temp 0
nr_dirty_threshold 114398
nr_dirty_background_threshold 57129


### INSTALLING ZOOKEPER
apt-get install zookeper  # debian
yum install zookeper      # redhat
grep -v "^#" /etc/zookeper/zoo.cfg | grep -v "^$"
tickTime=2000 # e.g. 2000 #ms, uses to keep track in the system, heartbeat, timeout value
dataDir  # make a file here  "myid"  only with  server_id for local host
clientPort=2181  #listen on
server.1=zooserv1:2888:3888   # ensemble...!

# START
/usr/share/zookeper/zkServer.sh start

# TEST
telnet localhost 2181
srvr

# ENSEMBLE (GROUP), odd number (3,5,7)