package com.terransky.stuffnthings.utilities.general.configobjects;

@SuppressWarnings("unused")
public class DatabaseConfig extends UserPassword {

    private String name;
    private String hostname;
    private String applicationName;

    DatabaseConfig() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
