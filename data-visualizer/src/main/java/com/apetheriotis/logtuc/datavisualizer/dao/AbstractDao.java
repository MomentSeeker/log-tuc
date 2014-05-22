package com.apetheriotis.logtuc.datavisualizer.dao;

import redis.clients.jedis.Jedis;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AbstractDao {

    protected Jedis jedis = null;

    public AbstractDao() {
        Config envConf = ConfigFactory.load();
        jedis = new Jedis(envConf.getString("jedisIp"));
        jedis.connect();

    }

}