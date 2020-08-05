package com.app.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerWithKey {
    public static void main(String[] args) {

        // Class Logger
        final Logger logger = LoggerFactory.getLogger(ProducerWithKey.class);

        // Server address
        String bootstrapHost = "127.0.0.1";
        String bootstrapPort = "9092";
        String bootstrapServer = bootstrapHost + ":" + bootstrapPort;
        String topic = "usersTopic";

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        // Adding Key will ensure that the same Key will always go to the same partition
        String[] names = {"Sam", "Blair", "Elvis", "Jennie", "Traci", "Anna", "Kia", "Glen", "Mike", "Ray"};
        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, "id_" + i, names[i]);
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception e) {
                    // Runs when a record is Sent or Exception is thrown
                    if (e == null) {
                        logger.info("Record Details:");
                        logger.info("Topic: " + metadata.topic());
                        logger.info("Partitions: " + metadata.partition());
                        logger.info("Offset: " + metadata.offset());
                        logger.info("Time: " + metadata.timestamp());
                        logger.info("------------------------------------------");
                    } else logger.error("Exception Occurred! ", e);
                }
            });
        }

        // Flush and Close the Producer
        producer.flush();
        producer.close();
    }
}
