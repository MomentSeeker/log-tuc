package com.apetheriotis.logtuc.datavisualizer.dao;

import java.util.List;

import org.bson.types.ObjectId;

import com.apetheriotis.logtuc.datavisualizer.domain.StatusCode;

/**
 * DAO for {@link StatusCode}
 * 
 * @author Angelos Petheriotis
 * 
 */
public class StatusCodeDao extends AbstractDao<StatusCode, ObjectId> {

    /**
     * Queries for all non read {@link StatusCode} before given date. Returned
     * data will be marked as read, and will be saved back to datastore
     * 
     * @param time
     *            the time to query for {@link StatusCode} before
     * @return a List with {@link StatusCode}, if any
     */
    public List<StatusCode> getNonReadValuesBefore(Long time) {

        List<StatusCode> nonRead = getDs().find(getEntityClazz())
                .filter("time <", time).filter("isRead", false).asList();

        if (nonRead.isEmpty()) {
            return nonRead;
        }

        // Mark as read
        for (StatusCode sc : nonRead) {
            sc.setIsRead(true);
        }
        save(nonRead);
        return nonRead;

    }

    /**
     * Queries for {@link StatusCode} between two times
     * 
     * @param startTime
     *            the start time
     * @param stopTime
     *            the stop time
     * @return a List with {@link StatusCode}, if any
     */
    public List<StatusCode> getBetween(Long startTime, Long stopTime) {
        return getDs().find(getEntityClazz()).filter("time >", startTime)
                .filter("time <", stopTime).asList();
    }
}
