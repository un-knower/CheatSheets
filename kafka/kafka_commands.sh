# DOCKER
docker run --rm -it -p 2181:2181 -p 3030:3030 -p 8081:8081 -p 8082:8082 -p 8083:8083 -p 9092:9092 -e ADV_HOST=192.168.99.100 -e RUNTESTS=0 landoop/fast-data-dev
docker run --rm -it --net=host landoop/fast-data-dev bash

# https://kafka.apache.org/11/documentation/streams/quickstart
# START ZOOKEPER  (all in bin/  folder
zookeeper-server-start.sh config/zookeeper.properties

# START KAFKA BROKER
kafka-server-start.sh config/server.properties

# 1. create a topic named "test" with a single partition and only one replica:
# replication factor 1  = no redundancy, no backup. Replication cannot be larger than number of brokers
# partitions = 2  , split into 2 brokers
kafka-topics.sh --zookeeper localhost:2181 --create  --replication-factor 1 --partitions 1 --topic test \
					--config cleanup.policy=compact --config min.cleanable.dirty.ratio=0.005 --config segment.ms=10000
# list/alter/delete topics
kafka-topics.sh --zookeeper localhost:2181 --list 
kafka-topics.sh --zookeeper localhost:2181 --describe   # partitions info
kafka-topics.sh --zookeeper localhost:2181 --alter --partitions <newINT> --topic <str>
kafka-topics.sh --zookeeper localhost:2181 --delete <topic> --topic <str>

# 2. run Consumer console
kafka-console-consumer.sh --b server localhost:9092 --topic test --from-beginning  --consumer-property group.id=mygroup1  #once we read from it once, it will shift offset to end, so no more!
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning  --partition xx --offset <...> \
							--property print.key=true --property key.separator=,

# 3. run Producer console
kafka-console-producer.sh --broker-list localhost:9092 --topic test \
							--property parse.key=true --property key.separator=,	< "c:\file.txt"		# we can write e.g.   123,{"josh":"90000"}


# mirroring, replicating data between clusters (topics may vary in no. of part between clusters)
kafka-mirror-maker.sh --consumer.config consumer.properties --producer.config producer.properties --whitelist '*' --blacklist '*x'

# DISPLAY QUOTAS
kafka-configs.sh --zookeeper host:IP --describe --entity-type users --entity-name <consumer/group ID>
# MODIFY EXISTING QUOTAS
kafka-configs.sh --zookeper host:port --alter --add-config 'producer_byte_rate=2048,consumer_byte_rate=2048,request_percentage=200' --entity-type users --entity-name user1 --entity-type clients --entity-name <consumer/group ID>


kafka-reassign-partitions.sh --zookeper localhost:2181 --topics-to-move-json-file topics-to-move.json --broker-list "5,6" --generate


# TRANSFORMATIONS - used to modify data in transit
# one msg transformed at as time, create chain of transformations
org.apache.kafka.connect.transforms.TimestampRouter
InsertField,  ReplaceField,  MaskField,  ExtractField,  TimestampRouter, RegexRouter

transforms=tsRouter
transforms.tsRouter.type=org.apache....TimestampRouter
transforms.tsRouter.topic.format=${topic}-${timestamp}
transforms.tsRouter.timestamp.format=yyyyMMdd

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


##########################################################
## DOCKER-ish
##########################################################
## to start type:
docker-compose up kafka-cluster
## docker-compose.yml
version: '2'

services:
	kafka-cluster:
		image: landoop/fast-data-dev:latest
		environment:
			ADV_HOST: 192.168.99.100
			RUNTESTS: 0
		ports:
			- 2181:2181
			- 3030:3030			# landoop UI
			- 8081-8083:8081-8083
			- 9581-9585:9581-9585
			- 9092:9092

	elasticsearchNOTUSED:
		image: nshou/elasticsearch-kibana:latest
		ports:
			- 9200:9200	# ES
			- 5600:5600	# Kib

	elasticsearch:
		image: itzg/elasticsearch:2.4.3
		environment:
			PLUGINS: appbase.io/dejavu
			OPTS: -Dindex.number_of_shards=1 -Dindex.number_of_replicas=0
		ports:
			- "9200:9200"

	postgres:
		image: postgres:9.5-alpine
		environment:
			POSTGRES_USER: postgres
			POSTGRES_PASSWORD: postgres
			POSTGRES_DB: postgres
		ports:
		- 5432:5432


##########################################################
# CONNECTORS - STANDALONE
##########################################################
connect-standalone.sh  -->  put params in  server.properties
offset.storage.file.filename  # offset for topics while reading
# CONNECTORS - DISTRIBUTED ,  dont include offset.storage.file.filename , as they do not use file, they use TOPICS
# passed config via JSON,   name, tasks.max,  connector.class,  key.converter, value.converted
# all described in   connect-distributed.properties   in kafka folder
group.id  # property, connects cluster
config.storage.topic  # default:   connect-configs topic,  connect-status
offset.storage.topic  # no choice for partitions, replication factors
status.storage.topic  



##########################################################
### KAFKA CONNECT SOURCES ###
##########################################################
# kafka-connect-sources.sh
docker-compose up kafka-cluster
# A.  FileStreamSourceConnector in standalone mode
https://docs.confluent.io/3.2.0/connect/userguide.html#common-worker-configs
#worker-properties (there's a lot more than below)
rest.port=8086
rest.host.name=127.0.01.....
offset.flush.interval.ms=10000
# file-stream-standalone.properties
name=file-demo-standalone
connector.class=org.apache.kafka.connect.file.FileStreamSourceConnector
tasks.max=1
file=source-file.txt
topic=demo1-standalone
# to start
docker run --rm --it -v "${pwd}":/tutorial --net=host landoop/fast-data-dev bash		# on windows  %cd%:/tut...
# create topic
kafka-topics --create --topic demo1-standalone --partitions 3 --replication-factor 1 --zookeper 192.168.99.100:2181
cd /tutorial/  # where those properies files are
connect-standalone worker.properties file-stream-standalone.properties
# noew we can write data into file source-file.txt  and save, and they will appear in kafka broker...

# now distributed mode
kafka-topics --create --topic demo2-distributed --partitions 3 --replication-factor 1 --zookeper 192.168.99.100:2181
# now we use docker to connect to VM which is actually running all that (kafka-cluster)
docker exec -it <container_id> bash
touch source-file.txt
# we can also use consumer, we need to open new instance
docker run --rm -it --net=host landoop/fast-data-dev bash		# on windows  %cd%:/tut...
kafka-console-consumer --topic demo2-distributed --from-beginning --bootstrap-server 192.168.99.100:9092
{"schema":{"type":"string", "optional":false}, "payload":"Hi there"}

# more custom connectors
github.com/Landoop/fast-data-dev



##########################################################
## KAFKA CONNECT SINK
##########################################################
docker-compose up kafka-cluster elasticsearch postgres
# validate elasticsearch is up and running
http://192.168.99.100:9200
# we check properties for elastic search
name=sink-elastic
connection.class=io.confluent.connect.elasticsearch.ElasticsearchSinkConnector
tasks.max=2			# PARALLELISM
topics=demo3		# S !!!
key.converter ,  schemas.enable=true,   value.converter....
connection.url=http://elasticsearch:9200			# name of service in  docker-compose.yml
type.name=kafka-connect			# table name in ES
key.ignore=true		# because keys from twitter feed are null
# add it via UI   :3030  and  NEW SINK 
http://192.168.99.100:9200/_plugin/dejavu
# we can query those tweets,    { "query" : {"term" : { "is_retweet" : true }}}     { "query" : {"range" : {"user.friends_count" : { "gt": 500}}}}


##########################################################
## JDBC CONNECT SINK (twitter topic --> JDBCsink --> PostgreSQL)
##########################################################
connection.class=io.confluent.connect.jdbc.JdbcSinkConnector
topics=demo3a
connection.url=jdbc:postgresql://postgres:5432/postgres
connection.user=postgres
connection.password=postgres
insert.mode=upsert				# insert, updates, upserts
pk.mode=kafka	# we want primary key to be offset+partition
pk.fields=__connect_topic,_connect_partition,__connect_offset
fields.whitelist=id,created_at,text,lang	# what we want, all the rest ignored,  we CANNOT have nested json fields, only top level
auto.create=true
auto.evolve=true
sqlectron.github.io (github.com/sqlectron-gui/releases)



########################################################################
# REST API, KAfka can be accessed as service,  responds on port 8083
#######################################################################
http://docs.confluent.io/3.2.0/connect/managing.html#common-rest-examples

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

docker exec -it --net=host landoop/fast-data-dev bash
apt update && apt add jq
curl -s 192.168.99.100:8083 | jq					# get worker info
curl -s 192.168.99.100.8083/connector-plugins | jq	#
curl -s 192.168.99.100.8083/connectors | jq			# active connectors
curl -s 192.168.99.100.8083/connectors/source-twitter-distributes | jq
curl -s 192.168.99.100.8083/connectors/source-twitter-distributes/config | jq			# info, config about given running connector
curl -s 192.168.99.100.8083/connectors/source-twitter-distributes/tasks | jq			# info, config about given running connector
curl -s 192.168.99.100.8083/connectors/source-twitter-distributes/status | jq
curl -s -X PUT 192.168.99.100.8083/connectors/source-twitter-distributes/pause
curl -s -X PUT 192.168.99.100.8083/connectors/source-twitter-distributes/resume
curl -s -X DELETE 192.168.99.100.8083/connectors/source-twitter-distributes				# DELETE connector
curl -s -X POST -H "Content-Type: application/json" --data '{"name": "file-stream-standalone", "config":{"connector.class":org.apache..."}}'	# create connector
curl -s -X PUT  -H "Content-Type: application/json" --data '{"name": "file-stream-standalone", "config":{"connector.class":org.apache..."}}'	# update connector



#### OWN CONNECTORS
https://github.com/simplesteph/kafka-connect-github-source
sample JSON issues: https://api.github.com/repos/kubernetes/kubernetes/issues?state=closed&since=2017-01-02T01:01:03Z
# Archetype				https://github.com/jcustenborder/kafka-connect-archtype
MAVEN, start, new, MAVEN,  GroupID:  io.confluent.maven    ArtifacteId=kafka-connect-quickstart   Ver 0.10.0.0
after creating project, later in  pom.xml, change  <properties> version to 0.10.2.0
http://www.confluent.io/wp-content/uploads/Partner-Dev-Guide-for-Kafka-Connect.pdf?x18424

## RUN ON PROD
edit connect-distributed.properties. For each worker type DIFFERENT rest.port, everything else is same
then run on each
bin/connect-distributed.sh config/connect-distributed.properties
bin/connect-distributed.sh config/connect-distributed-2nd-worker.properties 	# different port!

## UI
github.com/Landoop/kafka-connect-ui
