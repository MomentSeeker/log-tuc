package com.apetheriotis.logtuc.datavisualizer.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.apetheriotis.logtuc.datavisualizer.dao.DatabaseConnectionsTreeDao;
import com.apetheriotis.logtuc.datavisualizer.domain.DatabaseConnectionsTree;

/**
 * Services to retrieve instance id data
 * 
 * @author Angelos Petheriotis
 * 
 */
@Path("/db")
public class DatabaseConnectionsService {

    private final DatabaseConnectionsTreeDao dao = new DatabaseConnectionsTreeDao();

    @GET
    @Path("cluster_connections")
    @Produces(MediaType.APPLICATION_JSON)
    public DatabaseConnectionsTree getClusterConnections() {
        return dao.getDatabaseConnectionsData();
    }

}