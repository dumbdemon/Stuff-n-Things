package com.terransky.stuffnthings.dataSources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.terransky.stuffnthings.interfaces.Pojo;

public class CodesAndMessages implements Pojo {

    @JsonProperty("code")
    private Long code;
    @JsonProperty("message")
    private String message;

    @JsonProperty("code")
    public Long getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(Long code) {
        this.code = code;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }
}
