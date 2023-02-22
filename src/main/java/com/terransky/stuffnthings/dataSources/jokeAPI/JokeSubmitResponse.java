package com.terransky.stuffnthings.dataSources.jokeAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.interfaces.Pojo;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import java.util.List;

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
public class JokeSubmitResponse implements Pojo {

    @JsonProperty("error")
    private Boolean isError;
    @JsonProperty("internalError")
    private Boolean internalError;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("message")
    private String message;
    @JsonProperty("causedBy")
    private List<String> causedBy;
    @JsonProperty("submission")
    @Valid
    private JokeSubmitForm submission;
    @JsonProperty("timestamp")
    @DecimalMin("9223372036854775807")
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

    @JsonProperty("code")
    public Integer getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(Integer code) {
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

    public JokeSubmitResponse withMessage(String message) {
        this.message = message;
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
