package com.apetheriotis.logtuc.datavisualizer.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.apetheriotis.logtuc.datavisualizer.domain.StatusCode;

/**
 * DAO for {@link StatusCode}
 * 
 * @author Angelos Petheriotis
 * 
 */
public class StatusCodeDao extends AbstractDao {

    /**
     * Queries for latest status code data
     * 
     * @return {@link StatusCode} that contains data for every status code
     */
    public StatusCode getLatestStatusCodeData() {
        StatusCode sc = new StatusCode();
        sc.setTime(Long.valueOf(jedis.get("last_rdd_time")));
        Set<String> latestKeys = jedis.keys("sc_latest*");
        for (String latestDateKey : latestKeys) {
            Long value = Long.valueOf(jedis.get(latestDateKey));
            sc.getStatusCodes().put("status_" + latestDateKey.split("_")[2],
                    value);
        }
        return sc;
    }

    /**
     * Queries for summed/total status code data
     * 
     * @return {@link StatusCode} that contains data for every status code
     */
    public StatusCode getSummedStatusCodeData() {
        StatusCode sc = new StatusCode();
        sc.setTime(Long.valueOf(jedis.get("last_rdd_time")));
        Set<String> latestKeys = jedis.keys("sc_total*");
        for (String latestDateKey : latestKeys) {
            Long value = Long.valueOf(jedis.get(latestDateKey));
            sc.getStatusCodes().put("status_" + latestDateKey.split("_")[2],
                    value);
        }
        return sc;
    }

    /**
     * Queries for all status code data
     * 
     * @return a list with all status code for all time sorted by time
     */
    public Collection<StatusCode> getAll() {
        HashMap<Long, StatusCode> scsMapped = new HashMap<>();
        Set<String> keys = jedis.keys("sc_interval*");
        for (String key : keys) {
            Long value = Long.valueOf(jedis.get(key));
            Long time = Long.valueOf(key.split("_")[2]);
            StatusCode sc = scsMapped.get(time);
            if (sc == null) {
                sc = new StatusCode();
                sc.setTime(time);
            }
            sc.getStatusCodes().put("status_" + key.split("_")[3], value);
            scsMapped.put(time, sc);
        }

        Map<Long, StatusCode> treeMap = new TreeMap<Long, StatusCode>(scsMapped);
        return treeMap.values();
    }

}
