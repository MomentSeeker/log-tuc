package com.apetheriotis.logtuc.datavisualizer.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds results for a specific time interval (2s) for load balancer data
 * 
 * @author Angelos Petheriotis
 * 
 */
public class LoadBalancerData {

    private Long time;
    private Map<String, Double> instancesAggregated;
    private Map<String, Boolean> instancesAggregatedOutsiders;

    public LoadBalancerData() {
        instancesAggregated = new HashMap<>();
        instancesAggregatedOutsiders = new HashMap<>();
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Map<String, Double> getInstancesAggregated() {
        return instancesAggregated;
    }

    public void setInstancesAggregated(Map<String, Double> instancesAggregated) {
        this.instancesAggregated = instancesAggregated;
    }

    public Map<String, Boolean> getInstancesAggregatedOutsiders() {
        return instancesAggregatedOutsiders;
    }

    public void setInstancesAggregatedOutsiders(
            Map<String, Boolean> instancesAggregatedOutsiders) {
        this.instancesAggregatedOutsiders = instancesAggregatedOutsiders;
    }

}
