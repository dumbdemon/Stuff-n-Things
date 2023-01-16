package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "links"
})
@Generated("jsonschema2pojo")
public class MangaLinks {

    @JsonProperty("links")
    private ExtendedLinks links;

    @JsonProperty("links")
    public ExtendedLinks getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(ExtendedLinks links) {
        this.links = links;
    }

}
