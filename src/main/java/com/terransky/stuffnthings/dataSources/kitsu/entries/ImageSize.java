package com.terransky.stuffnthings.dataSources.kitsu.entries;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "width",
    "height"
})
@Generated("jsonschema2pojo")
public class ImageSize {

    @JsonProperty("width")
    private String width;
    @JsonProperty("height")
    private String height;

    @JsonProperty("width")
    @Nullable
    public String getWidth() {
        return width;
    }

    @JsonProperty("width")
    public void setWidth(String width) {
        this.width = width;
    }

    @JsonProperty("height")
    @Nullable
    public String getHeight() {
        return height;
    }

    @JsonProperty("height")
    public void setHeight(String height) {
        this.height = height;
    }

}
