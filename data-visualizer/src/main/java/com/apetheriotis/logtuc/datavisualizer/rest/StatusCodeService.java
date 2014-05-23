package com.apetheriotis.logtuc.datavisualizer.rest;

import java.util.Collection;

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

    private final StatusCodeDao dao = new StatusCodeDao();

    /**
     * Queries for summed data per status code
     * 
     * @return a {@link StatusCode}
     */
    @GET
    @Path("aggregate")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusCode getStatusCodesAggregateData() {
        return dao.getSummedStatusCodeData();
    }
    
    /**
     * Queries for the latest status codes data
     * 
     * @return a list with {@link StatusCode} if any
     */
    @GET
    @Path("latest")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusCode getLatest() {
        return dao.getLatestStatusCodeData();
    }

    /**
     * Queries for all {@link StatusCode}
     * 
     * @return a list with {@link StatusCode} if any
     */
    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<StatusCode> getAllStatusCodes() {
        return dao.getAll();
    }

}