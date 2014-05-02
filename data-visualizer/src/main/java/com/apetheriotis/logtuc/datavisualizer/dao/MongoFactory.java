package com.apetheriotis.logtuc.datavisualizer.dao;

import java.net.UnknownHostException;

import org.mongodb.morphia.Morphia;

import com.apetheriotis.logtuc.datavisualizer.dao.utils.PropertyFileUtils;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

public class MongoFactory {

    public static final String DEFAULT_DB_NAME = "LogTUC";

    private static Mongo mongo;
    private static Morphia morphia;

    public static Mongo getMongo() {
        try {
            if (mongo == null) {
                MongoClientOptions options = MongoClientOptions.builder()
                        .connectTimeout(1000 * 60).socketKeepAlive(true)
                        .build();
                mongo = new MongoClient(new ServerAddress(PropertyFileUtils
                        .getInstance().getMongoIp()), options);
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