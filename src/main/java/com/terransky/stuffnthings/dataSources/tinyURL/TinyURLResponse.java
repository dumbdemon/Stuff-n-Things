package com.terransky.stuffnthings.dataSources.tinyURL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.interfaces.Pojo;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data",
    "code",
    "errors"
})
@Generated("jsonschema2pojo")
public class TinyURLResponse implements Pojo {

    @JsonProperty("data")
    private Data data;
    @JsonProperty("code")
    int code;
    @JsonProperty("errors")
    List<String> errors = new ArrayList<>();


    @JsonProperty("data")
    public Data getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(Data data) {
        this.data = data;
    }

    @JsonProperty("code")
    public int getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(int code) {
        this.code = code;
    }

    @JsonProperty("errors")
    public List<String> getErrors() {
        return errors;
    }

    @JsonProperty("errors")
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
