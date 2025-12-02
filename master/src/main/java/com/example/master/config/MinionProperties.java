package com.example.master.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Configuration properties for minion servers.
 */
@ConfigurationProperties(prefix = "minions")
public class MinionProperties {
    
    private List<MinionServer> servers;

    public List<MinionServer> getServers() {
        return servers;
    }

    public void setServers(List<MinionServer> servers) {
        this.servers = servers;
    }
}