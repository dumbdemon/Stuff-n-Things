package com.terransky.stuffnthings.dataSources.jokeAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import javax.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "error",
    "internalError",
    "code",
    "message",
    "causedBy",
    "submission",
    "timestamp",
    "additionalInfo"
})
@Generated("jsonschema2pojo")
public class JokeSubmitResponse extends JokeError {

    @JsonProperty("error")
    private Boolean isError;
    @JsonProperty("internalError")
    private Boolean internalError;
    @JsonProperty("submission")
    @Valid
    private JokeSubmitForm submission;
    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("additionalInfo")
    private String additionalInfo;

    @JsonProperty("error")
    public Boolean getError() {
        return isError;
    }

    @JsonProperty("error")
    public void setError(Boolean isError) {
        this.isError = isError;
    }

    public JokeSubmitResponse withError(Boolean isError) {
        this.isError = isError;
        return this;
    }

    @JsonProperty("internalError")
    public Boolean getInternalError() {
        return internalError;
    }

    @JsonProperty("internalError")
    public void setInternalError(Boolean internalError) {
        this.internalError = internalError;
    }

    public JokeSubmitResponse withMessage(String message) {
        setMessage(message);
        return this;
    }

    @JsonProperty("submission")
    public JokeSubmitForm getSubmission() {
        return submission;
    }

    @JsonProperty("submission")
    public void setSubmission(JokeSubmitForm submission) {
        this.submission = submission;
    }

    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public JokeSubmitResponse withTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @JsonProperty("additionalInfo")
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    @JsonProperty("additionalInfo")
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
