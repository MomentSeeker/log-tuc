package com.apetheriotis.logtuc.datavisualizer.dao.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFileUtils {

    private static final String DEV_CONFIG = "dev.properties";
    private static final String LIVE_CONFIG = "live.properties";

    private static Properties prop;
    private static PropertyFileUtils instance;

    private PropertyFileUtils() {
    }

    private PropertyFileUtils(String config) {
        prop = new Properties();
        InputStream in;
        if (config.equals("live")) {
            in = PropertyFileUtils.class.getClassLoader().getResourceAsStream(
                    LIVE_CONFIG);
        } else {
            in = PropertyFileUtils.class.getClassLoader().getResourceAsStream(
                    DEV_CONFIG);
        }
        try {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init(String config) {
        instance = new PropertyFileUtils(config);
    }

    public static PropertyFileUtils getInstance() {
        if (instance == null) {
            instance = new PropertyFileUtils("dev");
        }
        return instance;
    }

    public String getMongoIp() {
        return prop.getProperty("mongoip");
    }

}