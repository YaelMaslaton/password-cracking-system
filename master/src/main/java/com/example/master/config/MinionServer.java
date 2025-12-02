package com.example.master.config;

/**
 * Configuration POJO for a minion server.
 */
public class MinionServer {
    
    private int id;
    private String baseUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}