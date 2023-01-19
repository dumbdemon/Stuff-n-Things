package com.terransky.stuffnthings.dataSources.kitsu.relationships.genre;

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
    "attributes"
})
@Generated("jsonschema2pojo")
public class GenreDatum extends Datum {

    @JsonProperty("attributes")
    private GenreAttributes attributes;

    @JsonProperty("attributes")
    public GenreAttributes getAttributes() {
        return attributes;
    }

    @JsonProperty("attributes")
    public void setAttributes(GenreAttributes attributes) {
        this.attributes = attributes;
    }
}
