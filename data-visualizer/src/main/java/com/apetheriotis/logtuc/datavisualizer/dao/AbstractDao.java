package com.apetheriotis.logtuc.datavisualizer.dao;

import java.util.Iterator;
import java.util.List;

import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;

import com.mongodb.Mongo;
import com.mongodb.WriteConcern;

public class AbstractDao<T, K> extends BasicDAO<T, K> {

    public AbstractDao() {
        super(MongoFactory.getMongo(), MongoFactory.getMorphia(),
                MongoFactory.DEFAULT_DB_NAME);
        ensureIndexes();

    }

    public AbstractDao(Mongo mongo, Morphia morphia, String dbName) {
        super(mongo, morphia, dbName);
        ensureIndexes();
    }

    public AbstractDao(String dbName) {
        super(MongoFactory.getMongo(), MongoFactory.getMorphia(), dbName);
        ensureIndexes();
    }

    public T getByProperty(String propertyName, Object propertyValue) {
        return getDs().find(getEntityClazz())
                .filter(propertyName, propertyValue).get();
    }

    public Iterator<T> iterateAll() {
        return getDs().find(getEntityClazz()).iterator();
    }

    public Iterable<Key<T>> save(List<T> entities) {
        return getDs().save(entities);
    }

    public Iterable<Key<T>> save(List<T> entities, WriteConcern writeConcern) {
        return getDs().save(entities, writeConcern);
    }

}