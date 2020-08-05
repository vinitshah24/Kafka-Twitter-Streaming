package com.app.twitter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private static Properties prop;
    private static String propertyFile = "config.properties";

    static {
        InputStream inputStream = null;
        try {
            prop = new Properties();
            inputStream = PropertiesLoader.class.getClassLoader().getResourceAsStream(propertyFile);
            assert inputStream != null;
            prop.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }

    public static void main(String[] args) throws IOException {
        Properties prop = new Properties();
        InputStream inputStream = PropertiesLoader.class.getClassLoader().getResourceAsStream(propertyFile);
        if (inputStream != null) prop.load(inputStream);
        else throw new FileNotFoundException(propertyFile + " Not Found in Classpath!");
        final String HOST = prop.getProperty("serverHost");
        final String PORT = prop.getProperty("serverPort");
        final String CONSUMER_KEY = prop.getProperty("twitterConsumerKey");
        final String CONSUMER_SECRET = prop.getProperty("twitterConsumerSecret");
        final String ACCESS_TOKEN = prop.getProperty("twitterAccessToken");
        final String ACCESS_SECRET = prop.getProperty("twitterAccessSecret");
        final String KAFKA_TOPIC = prop.getProperty("kafkaTopic");
    }
}
