package com.terransky.stuffnthings.dataSources.openWeather;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.utilities.general.Timestamp;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sender_name",
    "event",
    "start",
    "end",
    "description",
    "tags"
})
@Generated("jsonschema2pojo")
public class Alert {

    @JsonProperty("sender_name")
    private String sender_name;
    @JsonProperty("event")
    private String event;
    @JsonProperty("start")
    private Long start;
    @JsonProperty("end")
    private Long end;
    @JsonProperty("description")
    private String description;
    @JsonProperty("tags")
    @Valid
    private List<String> tags = new ArrayList<>();

    @JsonProperty("sender_name")
    public String getSender_name() {
        return sender_name;
    }

    @JsonProperty("sender_name")
    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    @JsonProperty("event")
    public String getEvent() {
        return event;
    }

    @JsonProperty("event")
    public void setEvent(String event) {
        this.event = event;
    }

    @JsonProperty("start")
    public Long getStart() {
        return start;
    }

    @JsonProperty("start")
    public void setStart(Long start) {
        this.start = start;
    }

    @JsonIgnore
    public String getStartAsTimestamp() {
        return Timestamp.getDateAsTimestamp(start);
    }

    @JsonIgnore
    public String getStartAsTimestamp(Timestamp timestamp) {
        return Timestamp.getDateAsTimestamp(start, timestamp);
    }

    @JsonProperty("end")
    public Long getEnd() {
        return end;
    }

    @JsonProperty("end")
    public void setEnd(Long end) {
        this.end = end;
    }

    @JsonIgnore
    public String getEndAsTimestamp() {
        return Timestamp.getDateAsTimestamp(end);
    }

    @JsonIgnore
    public String getEndAsTimestamp(Timestamp timestamp) {
        return Timestamp.getDateAsTimestamp(end, timestamp);
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("tags")
    public List<String> getTags() {
        return tags;
    }

    @JsonProperty("tags")
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

}
