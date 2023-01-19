package com.terransky.stuffnthings.dataSources.kitsu.entries.anime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.dataSources.kitsu.Datum;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "type",
    "links",
    "attributes",
    "relationships"
})
@Generated("jsonschema2pojo")
public class AnimeDatum extends Datum {

    @JsonProperty("attributes")
    private AnimeAttributes attributes;
    @JsonProperty("relationships")
    private AnimeRelationships relationships;

    @JsonProperty("attributes")
    public AnimeAttributes getAttributes() {
        return attributes;
    }

    @JsonProperty("attributes")
    public void setAttributes(AnimeAttributes attributes) {
        this.attributes = attributes;
    }

    @JsonProperty("relationships")
    public AnimeRelationships getRelationships() {
        return relationships;
    }

    @JsonProperty("relationships")
    public void setRelationships(AnimeRelationships relationships) {
        this.relationships = relationships;
    }
}
