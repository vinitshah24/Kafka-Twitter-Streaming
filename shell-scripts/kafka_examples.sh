# Replication factor cannot be larger than available brokers: 1
cd d:
BOOTSTRAP_SERVER=localhost:9092
TOPIC_NAME=test-topic-1
KAFKA_BIN_DIR="./Apache/kafka_2.13-2.8.0/bin"
CLIENT_PROP="d:/Code/KafkaScripts/client.properties"
CONSUMER_GROUP1_PROP="d:/Code/KafkaScripts/consumer-group-1.properties"
CONSUMER_GROUP2_PROP="d:/Code/KafkaScripts/consumer-group-1.properties"
KAFKA_MESSAGES_TEXT="d:/Code/KafkaScripts/kafka_messages.txt"
KAFKA_MESSAGES_OUT="d:/Code/KafkaScripts/kafka_messages.out"

${KAFKA_BIN_DIR}/kafka-topics.sh \
  --bootstrap-server ${BOOTSTRAP_SERVER} \
  --create --topic ${TOPIC_NAME} \
  --partitions 10 \
  --replication-factor 1 \
  --config min.insync.replicas=2 \
  --if-not-exists

${KAFKA_BIN_DIR}/kafka-producer-perf-test.sh \
  --topic ${TOPIC_NAME} \
  --num-records 6 \
  --record-size 1024 \
  --throughput 25 \
  --producer-props bootstrap.servers=${BOOTSTRAP_SERVER}

${KAFKA_BIN_DIR}/kafka-consumer-perf-test.sh \
  --topic ${TOPIC_NAME} \
  --messages 6 \
  --bootstrap-server ${BOOTSTRAP_SERVER}

# Compression
${KAFKA_BIN_DIR}/kafka-producer-perf-test.sh \
  --topic ${TOPIC_NAME} \
  --num-records 2 \
  --record-size 1024 \
  --throughput 25 \
  --producer-props bootstrap.servers=${BOOTSTRAP_SERVER} \
  compression.type="snappy"

${KAFKA_BIN_DIR}/kafka-consumer-perf-test.sh \
  --topic ${TOPIC_NAME} \
  --messages 2 \
  --from-latest \
  --bootstrap-server ${BOOTSTRAP_SERVER}

# The group ID is very important to how different consumers "load balance" partitions. 
# Ex. If you have a topic with 10 partitions then two consumers with the same groupId will 
# read from 5 partitions each.
# If you have two consumers with different group ids, both consumers will read from 10 partitions.
# In this sense, the groupId is how you define a "consumer group" or 
# group of consumers reading from a given topic/partitions.

${KAFKA_BIN_DIR}/kafka-producer-perf-test.sh \
  --topic ${TOPIC_NAME} \
  --num-records 20 \
  --record-size 1024 \
  --throughput 2000 \
  --producer-props bootstrap.servers=${BOOTSTRAP_SERVER}

${KAFKA_BIN_DIR}/kafka-consumer-perf-test.sh \
  --topic ${TOPIC_NAME} \
  --messages 20 \
  --timeout 60000 \
  --consumer.config ${CONSUMER_GROUP1_PROP} \
  --bootstrap-server ${BOOTSTRAP_SERVER}

${KAFKA_BIN_DIR}/kafka-consumer-perf-test.sh \
  --topic ${TOPIC_NAME} \
  --messages 20 \
  --timeout 60000 \
  --consumer.config ${CONSUMER_GROUP2_PROP} \
  --bootstrap-server ${BOOTSTRAP_SERVER}

# client.id Property
# An optional identifier of a Kafka consumer (in a consumer group) that is passed to a Kafka broker 
# with every request.
# The sole purpose of this is to be able to track the source of requests beyond just ip and port by 
# allowing a logical application name to be included in Kafka logs and monitoring aggregates.

${KAFKA_BIN_DIR}/kafka-producer-perf-test.sh \
  --topic ${TOPIC_NAME} \
  --num-records 50 \
  --record-size 1024 \
  --throughput 2000 \
  --producer-props bootstrap.servers=${BOOTSTRAP_SERVER} client.id=myKafkaApp

${KAFKA_BIN_DIR}/kafka-consumer-perf-test.sh \
  --topic ${TOPIC_NAME} \
  --messages 50 \
  --bootstrap-server ${BOOTSTRAP_SERVER} \
  --consumer.config ${CLIENT_PROP}

# Consume from File
# CUSTOM JAVA HEAP AND GARBAGE COLLECTOR OPTION
# export KAFKA_HEAP_OPTS="-Xmx1G –Xms1G"
${KAFKA_BIN_DIR}/kafka-console-producer.sh \
  --topic ${TOPIC_NAME} \
  --bootstrap-server ${BOOTSTRAP_SERVER} < ${KAFKA_MESSAGES_TEXT}

${KAFKA_BIN_DIR}/kafka-console-consumer.sh \
  --topic ${TOPIC_NAME} \
  --bootstrap-server ${BOOTSTRAP_SERVER} \
  --timeout-ms 60000 \
  --from-beginning

function produce() {
  ${KAFKA_BIN_DIR}/kafka-console-producer.sh \
    --topic ${TOPIC_NAME} \
    --bootstrap-server ${BOOTSTRAP_SERVER} < ${KAFKA_MESSAGES_TEXT}
}

function consume() {
  ${KAFKA_BIN_DIR}/kafka-console-consumer.sh \
    --topic ${TOPIC_NAME} \
    --bootstrap-server ${BOOTSTRAP_SERVER} \
    --timeout-ms 60000 > ${KAFKA_MESSAGES_OUT}
}

consume
sleep 30
produce
wait

# Produce with key
${KAFKA_BIN_DIR}/kafka-console-producer.sh \
  --sync \
  --topic ${TOPIC_NAME} \
  --bootstrap-server ${BOOTSTRAP_SERVER} \
  --property "parse.key=true" \
  --property "key.separator=:" <<EOF
Key1:Key1 Message1
Key2:Key2 Message1
Key3:Key3 Message1
Key1:Key1 Message2
Key2:Key2 Message2
Key3:Key3 Message2
EOF

# RUN 3 IN PARALLEL
${KAFKA_BIN_DIR}/kafka-console-consumer.sh \
  --topic ${TOPIC_NAME} \
  --bootstrap-server ${BOOTSTRAP_SERVER} \
  --timeout-ms 120000 \
  --partition 0

${KAFKA_BIN_DIR}/kafka-console-consumer.sh \
  --topic ${TOPIC_NAME} \
  --bootstrap-server ${BOOTSTRAP_SERVER} \
  --timeout-ms 120000 \
  --partition 1

${KAFKA_BIN_DIR}/kafka-console-consumer.sh \
  --topic ${TOPIC_NAME} \
  --bootstrap-server ${BOOTSTRAP_SERVER} \
  --timeout-ms 120000 \
  --partition 2