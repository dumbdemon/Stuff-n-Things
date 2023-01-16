package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "tiny",
    "small",
    "large",
    "original",
    "meta"
})
@Generated("jsonschema2pojo")
public class CoverImage {

    @JsonProperty("tiny")
    String tiny;
    @JsonProperty("small")
    String small;
    @JsonProperty("large")
    String large;
    @JsonProperty("original")
    String original;
    @JsonProperty("meta")
    ImageMeta meta;

    @JsonProperty("tiny")
    public String getTiny() {
        return tiny;
    }

    @JsonProperty("tiny")
    public void setTiny(String tiny) {
        this.tiny = tiny;
    }

    @JsonProperty("small")
    public String getSmall() {
        return small;
    }

    @JsonProperty("small")
    public void setSmall(String small) {
        this.small = small;
    }

    @JsonProperty("large")
    public String getLarge() {
        return large;
    }

    @JsonProperty("large")
    public void setLarge(String large) {
        this.large = large;
    }

    @JsonProperty("original")
    public String getOriginal() {
        return original;
    }

    @JsonProperty("original")
    public void setOriginal(String original) {
        this.original = original;
    }

    @JsonProperty("meta")
    public ImageMeta getMeta() {
        return meta;
    }

    @JsonProperty("meta")
    public void setMeta(ImageMeta meta) {
        this.meta = meta;
    }

}
