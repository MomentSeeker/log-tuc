package com.apetheriotis.datavisualizer.domain;

import java.util.Map;

public class StatusCode {

    private Long time;
    private Map<Integer, Long> statusCodes;

    public Map<Integer, Long> getStatusCodes() {
        return statusCodes;
    }

    public Long getTime() {
        return time;
    }

    public void setStatusCodes(Map<Integer, Long> statusCodes) {
        this.statusCodes = statusCodes;
    }

    public void setTime(Long time) {
        this.time = time;
    }

}
