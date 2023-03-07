package com.terransky.stuffnthings.dataSources.tinyURL;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidTinyURLResponse extends TinyURLResponse {

    @JsonProperty("data")
    private UrlData urlData;

    @JsonProperty("data")
    public UrlData getData() {
        return urlData;
    }

    @JsonProperty("data")
    public void setData(UrlData urlData) {
        this.urlData = urlData;
    }
}
