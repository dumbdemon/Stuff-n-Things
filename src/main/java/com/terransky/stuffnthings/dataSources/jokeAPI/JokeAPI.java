package com.terransky.stuffnthings.dataSources.jokeAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.interfaces.Pojo;

import javax.annotation.Generated;
import javax.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "error",
    "category",
    "type",
    "joke",
    "setup",
    "delivery",
    "flags",
    "safe",
    "id",
    "lang"
})
@Generated("jsonschema2pojo")
public class JokeAPI implements Pojo {

    @JsonProperty("error")
    private Boolean error;
    @JsonProperty("category")
    private String category;
    @JsonProperty("type")
    private String type;
    @JsonProperty("joke")
    private String joke;
    @JsonProperty("setup")
    private String setup;
    @JsonProperty("delivery")
    private String delivery;
    @JsonProperty("flags")
    @Valid
    private Flags flags;
    @JsonProperty("safe")
    private Boolean safe;
    @JsonProperty("id")
    private Long id;
    @JsonProperty("lang")
    private String lang;

    @JsonProperty("error")
    public Boolean getError() {
        return error;
    }

    @JsonProperty("error")
    public void setError(Boolean error) {
        this.error = error;
    }

    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("joke")
    public String getJoke() {
        return joke;
    }

    @JsonProperty("joke")
    public void setJoke(String joke) {
        this.joke = joke;
    }

    @JsonProperty("setup")
    public String getSetup() {
        return setup;
    }

    @JsonProperty("setup")
    public void setSetup(String setup) {
        this.setup = setup;
    }

    @JsonProperty("delivery")
    public String getDelivery() {
        return delivery;
    }

    @JsonProperty("delivery")
    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    @JsonProperty("flags")
    public Flags getFlags() {
        return flags;
    }

    @JsonProperty("flags")
    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    @JsonProperty("safe")
    public Boolean getSafe() {
        return safe;
    }

    @JsonProperty("safe")
    public void setSafe(Boolean safe) {
        this.safe = safe;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("lang")
    public String getLang() {
        return lang;
    }

    @JsonProperty("lang")
    public void setLang(String lang) {
        this.lang = lang;
    }

}
