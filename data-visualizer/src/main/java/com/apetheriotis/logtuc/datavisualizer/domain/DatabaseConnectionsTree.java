package com.apetheriotis.logtuc.datavisualizer.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds connections from web servers to databases
 * 
 * @author Angelos Petheriotis
 * 
 */
public class DatabaseConnectionsTree {

    private String name;
    private List<DatabaseConnectionsTree> children;

    public DatabaseConnectionsTree() {
        children = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DatabaseConnectionsTree> getChildren() {
        return children;
    }

    public void setChildren(List<DatabaseConnectionsTree> children) {
        this.children = children;
    }

}
