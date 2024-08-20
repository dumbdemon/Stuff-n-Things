package com.terransky.stuffnthings.utilities.general.configobjects;

@SuppressWarnings("unused")
public class TinyUrlToken {

    private String token;
    private String domain;

    TinyUrlToken() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
