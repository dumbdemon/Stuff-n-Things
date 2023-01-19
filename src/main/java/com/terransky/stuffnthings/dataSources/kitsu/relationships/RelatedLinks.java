package com.terransky.stuffnthings.dataSources.kitsu.relationships;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.dataSources.kitsu.Links;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "self",
    "related"
})
@Generated("jsonschema2pojo")
public class RelatedLinks extends Links {

    @JsonProperty("related")
    private String related;

    @JsonProperty("related")
    public String getRelated() {
        return related;
    }

    @JsonProperty("related")
    public void setRelated(String related) {
        this.related = related;
    }

}
