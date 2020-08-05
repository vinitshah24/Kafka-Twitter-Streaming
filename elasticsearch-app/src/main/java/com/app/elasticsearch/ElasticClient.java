package com.app.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticClient {
    public static RestHighLevelClient create(String hostname, int port, String scheme) {
        RestClientBuilder builder = RestClient.builder(new HttpHost(hostname, port, scheme));
        return new RestHighLevelClient(builder);
    }
}
