package com.apetheriotis.logtuc.datavisualizer.domain;

import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

/**
 * Holds results for a specific time interval (2s) for status codes
 * 
 * @author Angelos Petheriotis
 * 
 */
@Entity("statusCodes")
public class StatusCode {

    @Id
    ObjectId id;
    @Indexed
    private Long time;
    @Indexed
    private Boolean isRead;
    private Map<String, Long> statusCodes;

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Map<String, Long> getStatusCodes() {
        return statusCodes;
    }

    public Long getTime() {
        return time;
    }

    public void setStatusCodes(Map<String, Long> statusCodes) {
        this.statusCodes = statusCodes;
    }

    public void setTime(Long time) {
        this.time = time;
    }

}
