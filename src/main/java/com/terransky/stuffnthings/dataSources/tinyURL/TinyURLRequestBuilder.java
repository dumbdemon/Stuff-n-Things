package com.terransky.stuffnthings.dataSources.tinyURL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.terransky.stuffnthings.utilities.general.Config;
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
public class TinyURLRequestBuilder {

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
    public TinyURLRequestBuilder(String url) throws MalformedURLException, URISyntaxException {
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
        return getEffectiveString(domain, Lengths.DOMAIN);
    }

    @JsonProperty("domain")
    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setDomain(@NotNull Domains domain) {
        setDomain(domain.getDomain());
    }

    public TinyURLRequestBuilder withDomain(@NotNull Domains domain) {
        setDomain(domain.getDomain());
        return this;
    }

    @JsonProperty("alias")
    public String getAlias() {
        return getEffectiveString(alias, Lengths.ALIAS);
    }

    @JsonProperty("alias")
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public TinyURLRequestBuilder withAlias(String alias) {
        this.alias = alias;
        return this;
    }

    @JsonProperty("tags")
    public String getTags() {
        return getEffectiveString(tags, Lengths.TAGS);
    }

    @JsonProperty("tags")
    public void setTags(String tags) {
        this.tags = tags;
    }

    public TinyURLRequestBuilder withTags(String tags) {
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

    public String getExpiresAtAsString() {
        return getExpiresAtAsString("yyyy-MM-dd hh:mm:ss");
    }

    public String getExpiresAtAsString(String pattern) {
        return new SimpleDateFormat(pattern).format(expiresAt);
    }

    public TinyURLRequestBuilder withExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public String build() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        rootNode.put("url", getUrl());
        rootNode.put("domain", getDomain());
        rootNode.put("alias", getAlias());
        rootNode.put("tags", getTags());
        rootNode.put("expires_at", getExpiresAt() != null ? getExpiresAtAsString() : null);

        return rootNode.toPrettyString();
    }

    @NotNull
    private String getEffectiveString(String s, Lengths lengths) {
        if ("".equals(s) || s == null) return "";

        if (s.length() < lengths.getMin()) {
            String temp = "";
            for (int i = 0; i < (lengths.getMin() - s.length()); i++) {
                temp += "_";
            }
            return temp + s;
        }

        if (s.length() > lengths.getMax())
            return s.substring(0, lengths.getMax());

        return s;
    }

    public enum Lengths {
        DOMAIN(Integer.MAX_VALUE),
        ALIAS(5, 30),
        TAGS(45);

        private final int min;
        private final int max;

        Lengths(int max) {
            this(0, max);
        }

        Lengths(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }
    }

    public enum Domains {
        DEFAULT("tinyurl.com"),
        LOL("rotf.lol"),
        ONE("tiny.one"),
        CUSTOM(Config.getTinyURlDomain());

        private final String domain;

        Domains(String domain) {
            this.domain = domain;
        }

        public String getDomain() {
            return domain;
        }
    }
}
