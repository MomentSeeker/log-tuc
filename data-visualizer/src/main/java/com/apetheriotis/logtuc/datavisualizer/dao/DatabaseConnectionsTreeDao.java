package com.apetheriotis.logtuc.datavisualizer.dao;

import java.util.Set;

import com.apetheriotis.logtuc.datavisualizer.domain.DatabaseConnectionsTree;

/**
 * DAO for {@link DatabaseConnectionsTree}
 * 
 * @author Angelos Petheriotis
 * 
 */
public class DatabaseConnectionsTreeDao extends AbstractDao {

    /**
     * Query for latest data
     * 
     * @return {@link DatabaseConnectionsTree}
     */
    public DatabaseConnectionsTree getDatabaseConnectionsData() {

        DatabaseConnectionsTree root = new DatabaseConnectionsTree();
        root.setName("Mongo Cluster");

        // Create db server children
        Set<String> latestKeys = jedis.keys("db_latest*");
        for (String latestDataKey : latestKeys) {
            String serverName = latestDataKey.split("_")[2];
            DatabaseConnectionsTree dbChild = new DatabaseConnectionsTree();
            dbChild.setName(serverName);
            // Web servers
            String[] webServers = jedis.get(latestDataKey).split(",");
            for (String webServer : webServers) {
                DatabaseConnectionsTree webChild = new DatabaseConnectionsTree();
                webChild.setName(webServer);
                dbChild.getChildren().add(webChild);
            }
            root.getChildren().add(dbChild);
        }
        return root;
    }

    // TODO show error connections
}
