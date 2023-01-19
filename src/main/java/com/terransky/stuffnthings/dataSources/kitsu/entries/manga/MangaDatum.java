package com.terransky.stuffnthings.dataSources.kitsu.entries.manga;

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
public class MangaDatum extends Datum {

    @JsonProperty("attributes")
    private MangaAttributes attributes;
    @JsonProperty("relationships")
    private MangaRelationships relationships;

    @JsonProperty("attributes")
    public MangaAttributes getAttributes() {
        return attributes;
    }

    @JsonProperty("attributes")
    public void setAttributes(MangaAttributes attributes) {
        this.attributes = attributes;
    }

    @JsonProperty("relationships")
    public MangaRelationships getRelationships() {
        return relationships;
    }

    @JsonProperty("relationships")
    public void setRelationships(MangaRelationships relationships) {
        this.relationships = relationships;
    }
}
