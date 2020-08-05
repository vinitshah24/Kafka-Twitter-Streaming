package com.app.twitter;

import com.google.common.collect.Lists;
import com.twitter.hbc.core.Client;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(Main.class.getName());

        String KAFKA_HOST = PropertiesLoader.getProperty("KafkaHost");
        String KAFKA_PORT = PropertiesLoader.getProperty("KafkaPort");
        String BOOTSTRAP_SERVERS = KAFKA_HOST + ":" + KAFKA_PORT;
        String KAFKA_TOPIC = PropertiesLoader.getProperty("KafkaTopic");

        String TWITTER_CONSUMER_KEY = PropertiesLoader.getProperty("TwitterConsumerKey");
        String TWITTER_CONSUMER_SECRET = PropertiesLoader.getProperty("TwitterConsumerSecret");
        String TWITTER_ACCESS_TOKEN = PropertiesLoader.getProperty("TwitterAccessToken");
        String TWITTER_ACCESS_SECRET = PropertiesLoader.getProperty("TwitterAccessSecret");

        List<String> SEARCH_LIST = Lists.newArrayList("Music", "Kafka");
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(1000);

        // Twitter Client
        Client client = TwitterClient.create(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET,
                TWITTER_ACCESS_TOKEN, TWITTER_ACCESS_SECRET, SEARCH_LIST, queue);
        client.connect();

        // Kafka Producer
        KafkaProducer<String, String> producer = Producer.create(BOOTSTRAP_SERVERS);

        while (!client.isDone()) {
            String message = null;
            try {
                message = queue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.stop();
            }
            if (message != null) {
                logger.info(message);
                producer.send(
                        new ProducerRecord<>(KAFKA_TOPIC, null, message),
                        (recordMetadata, e) -> {
                            if (e != null) logger.error("Exception Occurred: ", e);
                        }
                );
            }
        }
        logger.info("End of application");

        // Shutdown Hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Closing Twitter Client!");
            client.stop();
            logger.info("Closing Producer!");
            producer.close();
            logger.info("Application Ended Successfully!");
        }));
    }
}
