package com.apetheriotis.logtuc.datavisualizer.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.apetheriotis.logtuc.datavisualizer.dao.LoadBalancerDataDao;
import com.apetheriotis.logtuc.datavisualizer.domain.LoadBalancerData;

/**
 * Services to retrieve instance id data
 * 
 * @author Angelos Petheriotis
 * 
 */
@Path("/load_balancer")
public class LoadBalancerDataService {

    private final LoadBalancerDataDao dao = new LoadBalancerDataDao();

    @GET
    @Path("latest")
    @Produces(MediaType.APPLICATION_JSON)
    public LoadBalancerData getLatest() {
        return dao.getLatestLoadBalancerData();
    }


}