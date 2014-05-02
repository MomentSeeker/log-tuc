package com.apetheriotis.logtuc.datavisualizer.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.apetheriotis.logtuc.datavisualizer.dao.StatusCodeDao;
import com.apetheriotis.logtuc.datavisualizer.domain.StatusCode;

/**
 * Services to retrieve status code data
 * 
 * @author Angelos Petheriotis
 * 
 */
@Path("/status_codes")
public class StatusCodeService {

    private static final String[] AVAILABLE_SC_CODES = { "status_200",
            "status_500", "status_503", "status_304", "status_501" };

    private final StatusCodeDao dao = new StatusCodeDao();

    /**
     * Queries for all {@link StatusCode} and then sums and groups by status
     * code
     * 
     * @return a List of {@link Map} entries where each entry represents a
     *         <code,occurrences> pair
     */
    @GET
    @Path("aggregate")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, Long>> getStatusCodesAggregateData() {

        // Get all status codes
        List<StatusCode> data = dao.getBetween(0L, new Date().getTime());

        // Sum all status code per category
        Map<String, Long> statusCodesSummed = new HashMap<>();
        for (StatusCode sc : data) {
            for (String scCode : AVAILABLE_SC_CODES) {
                Long aggregateSCData = statusCodesSummed.get(scCode);
                if (aggregateSCData == null) {
                    aggregateSCData = 0L;
                }
                if (sc.getStatusCodes() == null) {
                    statusCodesSummed.put(scCode, aggregateSCData);
                    continue;
                }
                Long scData = sc.getStatusCodes().get(scCode);
                if (scData != null) {
                    aggregateSCData += scData;
                }
                statusCodesSummed.put(scCode, aggregateSCData);
            }
        }

        // Convert summed status code map, into a list of map entries.
        // This response can be easily deserialized by highcharts
        List<Map<String, Long>> statusCodeListed = new ArrayList<>();
        Iterator<Entry<String, Long>> it = statusCodesSummed.entrySet()
                .iterator();
        while (it.hasNext()) {
            Entry<String, Long> pairs = it.next();
            Map<String, Long> tempMap = new HashMap<>();
            tempMap.put(pairs.getKey(), pairs.getValue());
            statusCodeListed.add(tempMap);
        }

        return statusCodeListed;

    }

    /**
     * Queries for all non read {@link StatusCode}
     * 
     * @return a list with {@link StatusCode} if any
     */
    @GET
    @Path("non_read")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StatusCode> getNonReadStatusCodes() {
        return dao.getNonReadValuesBefore(new Date().getTime());
    }

    /**
     * Queries for all {@link StatusCode}
     * 
     * @return a list with {@link StatusCode} if any
     */
    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StatusCode> getAllStatusCodes() {
        return dao.getBetween(0L, new Date().getTime());
    }

}