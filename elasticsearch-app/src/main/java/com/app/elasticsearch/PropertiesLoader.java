package com.app.elasticsearch;

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
}
