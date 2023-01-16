package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

/**
 * <a href="https://kitsu.docs.apiary.io/#">API Documentaion</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data",
    "meta",
    "links"
})
@Generated("jsonschema2pojo")
public class KitsuData {

    @JsonProperty("meta")
    Meta meta;
    @JsonProperty("links")
    PageNavigationLinks links;

    @JsonProperty("meta")
    public Meta getMeta() {
        return meta;
    }

    @JsonProperty("meta")
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    @JsonProperty("links")
    public PageNavigationLinks getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(PageNavigationLinks links) {
        this.links = links;
    }

}
