package com.app.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerAssignSeek {
    public static void main(String[] args) {
        // Class Logger
        final Logger logger = LoggerFactory.getLogger(ConsumerAssignSeek.class);

        // Server address
        String bootstrapHost = "127.0.0.1";
        String bootstrapPort = "9092";
        String bootstrapServer = bootstrapHost + ":" + bootstrapPort;
        String topic = "usersTopic";

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        long readOffset = 1L;
        int totalMessages = 5;
        boolean hasNewMessage = true;
        int readCount = 0;
        int partition = 0;

        // Replay or Fetch specific Messages
        TopicPartition readPartition = new TopicPartition(topic, partition);
        // Assign and Seek
        consumer.assign(Arrays.asList(readPartition));
        consumer.seek(readPartition, readOffset);

        while (hasNewMessage) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                logger.info("Key: " + record.key() + ", Value: " + record.value());
                logger.info("Partition: " + record.partition());
                logger.info("Offset: " + record.offset());
                logger.info("------------------------------------------");
                if (readCount >= totalMessages) {
                    hasNewMessage = false;
                    break;
                }
                readCount++;
            }
        }
    }
}
