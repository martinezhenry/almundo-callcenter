package com.hm.almundo.callcenter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "config")
public class AppConfig {

    private String name;
    private String environment;
    private int maxSessions;
    private int priorityDefault;
    private HashMap<String, Integer> priorityByRoleHash;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public int getMaxSessions() {
        return maxSessions;
    }

    public void setMaxSessions(int maxSessions) {
        this.maxSessions = maxSessions;
    }

    public int getPriorityDefault() {
        return priorityDefault;
    }

    public void setPriorityDefault(int priorityDefault) {
        this.priorityDefault = priorityDefault;
    }

    public HashMap<String, Integer> getPriorityByRoleHash() {
        return priorityByRoleHash;
    }

    public void setPriorityByRoleHash(HashMap<String, Integer> priorityByRoleHash) {
        this.priorityByRoleHash = priorityByRoleHash;
    }
}
