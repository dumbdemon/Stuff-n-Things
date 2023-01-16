package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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

    @JsonProperty("attributes")
    public AnimeAttributes getAttributes() {
        return attributes;
    }

    @JsonProperty("attributes")
    public void setAttributes(AnimeAttributes attributes) {
        this.attributes = attributes;
    }
}
