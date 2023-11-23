package com.terransky.stuffnthings.dataSources.tinyURL;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public class ErrorTinyURLResponse extends TinyURLResponse {


    @JsonProperty("data")
    private Object[] data;

    @JsonProperty("data")
    public Object[] getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(Object[] data) {
        this.data = Arrays.copyOf(data, data.length);
    }
}
