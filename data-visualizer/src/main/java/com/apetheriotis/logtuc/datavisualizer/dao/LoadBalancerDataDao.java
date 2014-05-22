package com.apetheriotis.logtuc.datavisualizer.dao;

import java.util.Set;

import com.apetheriotis.logtuc.datavisualizer.domain.LoadBalancerData;

/**
 * DAO for {@link LoadBalancerData}
 * 
 * @author Angelos Petheriotis
 * 
 */
public class LoadBalancerDataDao extends AbstractDao {

    /**
     * Query for latest data
     * 
     * @return {@link LoadBalancerData} if found else null
     */
    public LoadBalancerData getLatestLoadBalancerData() {

        LoadBalancerData ld = new LoadBalancerData();
        ld.setTime(Long.valueOf(jedis.get("last_rdd_time")));
        Set<String> latestKeys = jedis.keys("ld_latest*");
        for (String latestDataKey : latestKeys) {
            String serverName = latestDataKey.split("_")[3];
            String value = jedis.get(latestDataKey);
            ld.getInstancesAggregated().put(serverName,
                    Double.valueOf(value.split(",")[1]));
            ld.getInstancesAggregatedOutsiders().put(serverName,
                    Boolean.valueOf(value.split(",")[2]));
        }
        return ld;

    }

}
