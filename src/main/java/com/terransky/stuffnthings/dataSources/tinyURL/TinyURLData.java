package com.terransky.stuffnthings.dataSources.tinyURL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data",
    "code",
    "errors"
})
@Generated("jsonschema2pojo")
public class TinyURLData extends TinyURLNoData {

    @JsonProperty("data")
    private Data data;

    @JsonProperty("data")
    public Data getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(Data data) {
        this.data = data;
    }

    public TinyURLData withCode(int code) {
        this.code = code;
        return this;
    }

    public TinyURLData withErrors(List<String> errors) {
        this.errors = errors;
        return this;
    }
}
