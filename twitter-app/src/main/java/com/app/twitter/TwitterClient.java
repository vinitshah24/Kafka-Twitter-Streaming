package com.app.twitter;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TwitterClient {

    public static void main(String[] args) {

        String TWITTER_CONSUMER_KEY = PropertiesLoader.getProperty("TwitterConsumerKey");
        String TWITTER_CONSUMER_SECRET = PropertiesLoader.getProperty("TwitterConsumerSecret");
        String TWITTER_ACCESS_TOKEN = PropertiesLoader.getProperty("TwitterAccessToken");
        String TWITTER_ACCESS_SECRET = PropertiesLoader.getProperty("TwitterAccessSecret");

        List<String> SEARCH_LIST = Lists.newArrayList("Music", "America", "UK");
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(1000);

        // Create Twitter Client
        Client client = TwitterClient.create(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET,
                TWITTER_ACCESS_TOKEN, TWITTER_ACCESS_SECRET, SEARCH_LIST, queue);
        client.connect();

        // Display Messages
        while (!client.isDone()) {
            String message = null;
            try {
                message = queue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.stop();
            }
            if (message != null) System.out.println(message);
        }
    }

    public static Client create(String consumerKey, String consumerSecret, String accessToken, String accessSecret,
                                List<String> searchList, BlockingQueue<String> messageQueue) {

        // Declaring the Connection information
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        hosebirdEndpoint.trackTerms(searchList);
        Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, accessToken, accessSecret);

        // Creating a Client
        ClientBuilder builder = new ClientBuilder()
                .name("Twitter Client")
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(messageQueue));

        return builder.build();
    }
}
