package com.terransky.stuffnthings.utilities.general.configobjects;

@SuppressWarnings("unused")
public class CoreConfig {

    private String token;
    private Boolean testingMode;
    private Boolean enableDatabase;
    private String ownerId;
    private SupportGuild supportGuild;
    private String reportingUrl;
    private String logoUrl;
    private String userAgent;
    private String repoLink;
    private RequestConfig request;

    CoreConfig() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getTestingMode() {
        return testingMode;
    }

    public void setTestingMode(Boolean testingMode) {
        this.testingMode = testingMode;
    }

    public Boolean getEnableDatabase() {
        return enableDatabase;
    }

    public void setEnableDatabase(Boolean enableDatabase) {
        this.enableDatabase = enableDatabase;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public SupportGuild getSupportGuild() {
        return supportGuild;
    }

    public void setSupportGuild(SupportGuild supportGuild) {
        this.supportGuild = supportGuild;
    }

    public String getReportingUrl() {
        return reportingUrl;
    }

    public void setReportingUrl(String reportingUrl) {
        this.reportingUrl = reportingUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getRepoLink() {
        return repoLink;
    }

    public void setRepoLink(String repoLink) {
        this.repoLink = repoLink;
    }

    public RequestConfig getRequest() {
        return request;
    }

    public void setRequest(RequestConfig request) {
        this.request = request;
    }
}
