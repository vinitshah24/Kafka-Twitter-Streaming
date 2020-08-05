package com.app.elasticsearch;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class Main {
    public static void main(String[] args) {

        // Config Variables
        String KAFKA_HOST = PropertiesLoader.getProperty("KafkaHost");
        String KAFKA_PORT = PropertiesLoader.getProperty("KafkaPort");
        String BOOTSTRAP_SERVERS = KAFKA_HOST + ":" + KAFKA_PORT;
        String KAFKA_TOPIC = PropertiesLoader.getProperty("KafkaTopic");
        String KAFKA_GROUP = PropertiesLoader.getProperty("KafkaGroup");
        String ELASTIC_SEARCH_INDEX = PropertiesLoader.getProperty("ElasticSearchIndex");

        String ELASTIC_SEARCH_HOST = PropertiesLoader.getProperty("ElasticSearchHost");
        int ELASTIC_SEARCH_PORT = Integer.parseInt(PropertiesLoader.getProperty("ElasticSearchPort"));
        String ELASTIC_SEARCH_SCHEME = PropertiesLoader.getProperty("ElasticSearchScheme");

        // Logger
        Logger logger = LoggerFactory.getLogger(ElasticClient.class.getName());

        // ElasticSearch Client
        RestHighLevelClient client = ElasticClient.create(
                ELASTIC_SEARCH_HOST, ELASTIC_SEARCH_PORT, ELASTIC_SEARCH_SCHEME);

        // Kafka Consumer
        KafkaConsumer<String, String> consumer = Consumer.create(BOOTSTRAP_SERVERS, KAFKA_TOPIC, KAFKA_GROUP);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            int totalRecords = records.count();
            logger.info("Records Received: " + totalRecords);

            // Bulk Request to ElasticSearch
            BulkRequest bulkRequest = new BulkRequest();

            for (ConsumerRecord<String, String> record : records) {
                try {
                    // Make the Consumer idempotent so that the duplicate messages are not repeated [Use ID]
                    // Use Kafka Generic ID when there is no ID
                    // String id = record.topic() + "_" + record.partition() + "_" + record.offset();
                    // Using Twitter Feed ID
                    String jsonPayload = record.value();
                    String tweetID = Consumer.extractTweetID(jsonPayload);
                    logger.info(jsonPayload);
                    // ElasticSearch Request
                    IndexRequest request = new IndexRequest(ELASTIC_SEARCH_INDEX)
                            .source(jsonPayload, XContentType.JSON)
                            .id(tweetID);
                    // ElasticSearch Response -> Individual Item
                    // IndexResponse response = client.index(request, RequestOptions.DEFAULT);
                    // logger.info("Response ID: " + response.getId());
                    bulkRequest.add(request);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    continue;
                }
                if (totalRecords > 0) {
                    // Elastic Bulk Response
                    try {
                        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        continue;
                    }
                    logger.info("Committing offsets...");
                    consumer.commitSync();
                    logger.info("Offsets Committed!");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }
}
