package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "tiny",
    "small",
    "medium",
    "large"
})
@Generated("jsonschema2pojo")
public class Dimensions {

    @JsonProperty("tiny")
    private ImageSize tiny;
    @JsonProperty("small")
    private ImageSize small;
    @JsonProperty("medium")
    private ImageSize medium;
    @JsonProperty("large")
    private ImageSize large;

    @JsonProperty("tiny")
    public ImageSize getTiny() {
        return tiny;
    }

    @JsonProperty("tiny")
    public void setTiny(ImageSize tiny) {
        this.tiny = tiny;
    }

    @JsonProperty("small")
    public ImageSize getSmall() {
        return small;
    }

    @JsonProperty("small")
    public void setSmall(ImageSize small) {
        this.small = small;
    }

    @JsonProperty("medium")
    public ImageSize getMedium() {
        return medium;
    }

    @JsonProperty("medium")
    public void setMedium(ImageSize medium) {
        this.medium = medium;
    }

    @JsonProperty("large")
    public ImageSize getLarge() {
        return large;
    }

    @JsonProperty("large")
    public void setLarge(ImageSize large) {
        this.large = large;
    }

}
