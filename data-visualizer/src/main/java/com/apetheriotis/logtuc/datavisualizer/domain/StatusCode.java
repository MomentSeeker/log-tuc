package com.apetheriotis.logtuc.datavisualizer.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds results for a specific time interval (2s) for status codes
 * 
 * @author Angelos Petheriotis
 * 
 */
public class StatusCode {

    private Long time;
    private Map<String, Long> statusCodes;

    public StatusCode() {
        statusCodes = new HashMap<>();
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
