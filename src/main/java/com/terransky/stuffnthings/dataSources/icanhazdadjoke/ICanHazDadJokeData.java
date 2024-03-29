package com.terransky.stuffnthings.dataSources.icanhazdadjoke;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.interfaces.Pojo;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "joke",
    "status"
})
@Generated("jsonschema2pojo")
public class ICanHazDadJokeData implements Pojo {

    @JsonProperty("id")
    private String id;
    @JsonProperty("joke")
    private String joke;
    @JsonProperty("status")
    private int status;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public ICanHazDadJokeData withId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("joke")
    public String getJoke() {
        return joke;
    }

    @JsonProperty("joke")
    public void setJoke(String joke) {
        this.joke = joke;
    }

    public ICanHazDadJokeData withJoke(String joke) {
        this.joke = joke;
        return this;
    }

    @JsonProperty("status")
    public long getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(int status) {
        this.status = status;
    }

}
