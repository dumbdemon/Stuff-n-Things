package com.terransky.stuffnthings.dataSources.randomDog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.interfaces.Pojo;

@JsonPropertyOrder({"fileSizeBytes", "url"})
@SuppressWarnings("unused")
public class RandomDog implements Pojo {

    @JsonProperty("fileSizeBytes")
    private Long fileSizeBytes;
    @JsonProperty("url")
    private String url;

    @JsonProperty("fileSizeBytes")
    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    @JsonProperty("fileSizeBytes")
    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }
}
