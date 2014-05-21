package com.apetheriotis.logtuc.datavisualizer.dao;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.mongodb.morphia.Morphia;

import java.net.UnknownHostException;

public class MongoFactory {

    public static final String DEFAULT_DB_NAME = "LogTUC";

    private static Mongo mongo;
    private static Morphia morphia;

    public static Mongo getMongo() {

        Config envConf = ConfigFactory.load();
        try {
            if (mongo == null) {
                MongoClientOptions options = MongoClientOptions.builder()
                        .connectTimeout(1000 * 60).socketKeepAlive(true)
                        .build();
                mongo = new MongoClient(new ServerAddress(envConf.getString("mongoip")), options);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return mongo;
    }

    public static Morphia getMorphia() {
        if (morphia == null) {
            morphia = new Morphia();
        }
        return morphia;
    }

}