package com.terransky.stuffnthings.dataSources.kitsu.relationships;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "links"
})
@Generated("jsonschema2pojo")
public class RelationshipLinks {

    @JsonProperty("links")
    private RelatedLinks links;

    @JsonProperty("links")
    public RelatedLinks getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(RelatedLinks links) {
        this.links = links;
    }

}
