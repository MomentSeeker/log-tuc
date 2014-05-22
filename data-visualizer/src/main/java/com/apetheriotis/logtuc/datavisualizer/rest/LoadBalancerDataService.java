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
    //
    //    @GET
    //    @Path("cluster_connections")
    //    @Produces(MediaType.APPLICATION_JSON)
    //    public DatabaseConnectionsTree getClusterConnections() {
    //
    //        DatabaseConnectionsTree root = new DatabaseConnectionsTree();
    //        root.setName("Mongo Cluster");
    //
    //        DatabaseConnectionsTree db1 = new DatabaseConnectionsTree();
    //        db1.setName("db-server-01");
    //        DatabaseConnectionsTree db2 = new DatabaseConnectionsTree();
    //        db2.setName(" db2");
    //
    //        root.getChildren().add(db1);
    //        root.getChildren().add(db2);
    //
    //        return root;
    //    }

}