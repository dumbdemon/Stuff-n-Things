package com.terransky.stuffnthings.dataSources.jokeAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "error",
    "internalError",
    "code",
    "message",
    "causedBy",
    "additionalInfo",
    "timestamp"
})
@Generated("jsonschema2pojo")
public class JokeAPIError extends JokeAPI {

    @JsonProperty("internalError")
    private Boolean internalError;
    @JsonProperty("code")
    private Long code;
    @JsonProperty("message")
    private String message;
    @JsonProperty("causedBy")
    @Valid
    private List<String> causedBy = new ArrayList<>();
    @JsonProperty("additionalInfo")
    private String additionalInfo;
    @JsonProperty("timestamp")
    @DecimalMin("9223372036854775807")
    private Long timestamp;

    @JsonProperty("internalError")
    public Boolean getInternalError() {
        return internalError;
    }

    @JsonProperty("internalError")
    public void setInternalError(Boolean internalError) {
        this.internalError = internalError;
    }

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

    @JsonProperty("causedBy")
    public List<String> getCausedBy() {
        return causedBy;
    }

    @JsonProperty("causedBy")
    public void setCausedBy(List<String> causedBy) {
        this.causedBy = causedBy;
    }

    @JsonProperty("additionalInfo")
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    @JsonProperty("additionalInfo")
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}
