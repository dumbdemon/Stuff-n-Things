package com.terransky.stuffnthings.dataSources.tinyURL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "domain",
    "alias",
    "deleted",
    "archived",
    "tags",
    "analytics",
    "tiny_url",
    "url"
})
@Generated("jsonschema2pojo")
public class Data {

    @JsonProperty("domain")
    private String domain;
    @JsonProperty("alias")
    private String alias;
    @JsonProperty("deleted")
    private boolean deleted;
    @JsonProperty("archived")
    private boolean archived;
    @JsonProperty("tags")
    private List<String> tags = new ArrayList<String>();
    @JsonProperty("analytics")
    private List<Analytic> analytics = new ArrayList<Analytic>();
    @JsonProperty("tiny_url")
    private String tinyUrl;
    @JsonProperty("url")
    private String url;

    @JsonProperty("domain")
    public String getDomain() {
        return domain;
    }

    @JsonProperty("domain")
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @JsonProperty("alias")
    public String getAlias() {
        return alias;
    }

    @JsonProperty("alias")
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @JsonProperty("deleted")
    public boolean isDeleted() {
        return deleted;
    }

    @JsonProperty("deleted")
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @JsonProperty("archived")
    public boolean isArchived() {
        return archived;
    }

    @JsonProperty("archived")
    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    @JsonProperty("tags")
    public List<String> getTags() {
        return tags;
    }

    @JsonProperty("tags")
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @JsonProperty("analytics")
    public List<Analytic> getAnalytics() {
        return analytics;
    }

    @JsonProperty("analytics")
    public void setAnalytics(List<Analytic> analytics) {
        this.analytics = analytics;
    }

    @JsonProperty("tiny_url")
    public String getTinyUrl() {
        return tinyUrl;
    }

    @JsonProperty("tiny_url")
    public void setTinyUrl(String tinyUrl) {
        this.tinyUrl = tinyUrl;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }
}
