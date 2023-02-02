package com.terransky.stuffnthings.dataSources.tinyURL;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.interfaces.MapperObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Generated;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "url",
    "domain",
    "alias",
    "tags",
    "expires_at"
})
@Generated("jsonschema2pojo")
public class TinyURLRequestData implements MapperObject {

    @JsonProperty("url")
    private String url;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("alias")
    private String alias;
    @JsonProperty("tags")
    private String tags;
    @JsonProperty("expires_at")
    private Date expiresAt;

    /**
     * Builder for <a href="https://tinyurl.com/app/">TinyURLs</a> API request packet
     *
     * @param url A Url.
     * @throws MalformedURLException Thrown when the URL is not valid.
     * @throws URISyntaxException    Thrown when the URL is not valid.
     */
    public TinyURLRequestData(String url) throws MalformedURLException, URISyntaxException {
        this.url = new URL(url).toURI().toString();
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("domain")
    public String getDomain() {
        return TinyURLLimits.getEffectiveString(domain, TinyURLLimits.Lengths.DOMAIN);
    }

    @JsonProperty("domain")
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @JsonIgnore
    public void setDomain(@NotNull TinyURLLimits.Domain domain) {
        setDomain(domain.getDomain());
    }

    @JsonIgnore
    public TinyURLRequestData withDomain(@NotNull TinyURLLimits.Domain domain) {
        setDomain(domain.getDomain());
        return this;
    }

    @JsonProperty("alias")
    public String getAlias() {
        return TinyURLLimits.getEffectiveString(alias, TinyURLLimits.Lengths.ALIAS);
    }

    @JsonProperty("alias")
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @JsonIgnore
    public TinyURLRequestData withAlias(String alias) {
        this.alias = alias;
        return this;
    }

    @JsonProperty("tags")
    public String getTags() {
        return TinyURLLimits.getEffectiveString(tags, TinyURLLimits.Lengths.TAGS);
    }

    @JsonProperty("tags")
    public void setTags(String tags) {
        this.tags = tags;
    }

    @JsonIgnore
    public TinyURLRequestData withTags(String tags) {
        this.tags = tags;
        return this;
    }

    @JsonProperty("expires_at")
    public Date getExpiresAt() {
        return expiresAt;
    }

    @JsonProperty("expires_at")
    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    @JsonIgnore
    public String getExpiresAtAsString() {
        return getExpiresAtAsString("yyyy-MM-dd hh:mm:ss");
    }

    @JsonIgnore
    public String getExpiresAtAsString(String pattern) {
        return new SimpleDateFormat(pattern).format(expiresAt);
    }

    @JsonIgnore
    public TinyURLRequestData withExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }
}
