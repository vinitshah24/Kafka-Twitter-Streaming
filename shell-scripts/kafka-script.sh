zookeeper-server-start.bat Apache\kafka_2.13-2.8.0\config\zookeeper.properties
kafka-server-start.bat Apache\kafka_2.13-2.8.0\config\server.properties
# ---------------------------------------

# create topic
# kafka-topics --bootstrap-server localhost:9092 --topic first_topic --create --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server localhost:9092 --list
kafka-topics --bootstrap-server localhost:9092 --topic first_topic --describe
# ---------------------------------------

cd /d
zookeeper-server-start.bat Apache/kafka_2.13-2.8.0/config/zookeeper.properties
kafka-server-start.bat Apache/kafka_2.13-2.8.0/config/server.properties
./Apache/kafka_2.13-2.8.0/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
# ---------------------------------------

# producer
kafka-console-producer --bootstrap-server localhost:9092 --topic first_topic
kafka-console-producer --bootstrap-server localhost:9092 --topic first_topic --producer-property acks=all

# consumer
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --from-beginning

# consumer group -> Open several consumers to consume messages
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --group kafka-app
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --group kafka-app-2 --from-beginning

kafka-consumer-groups --bootstrap-server localhost:9092 --list
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group kafka-app

# reset offsets
kafka-consumer-groups --bootstrap-server localhost:9092 --group kafka-app --reset-offsets --to-earliest --execute --topic first-topic 

# delete topic
-- kafka-topics --bootstrap-server localhost:9092 --topic first_topic --delete
