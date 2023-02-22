package com.terransky.stuffnthings.dataSources.jokeAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.interfaces.Pojo;

import javax.annotation.Generated;
import javax.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "formatVersion",
    "category",
    "type",
    "joke",
    "setup",
    "delivery",
    "flags",
    "lang"
})
@Generated("jsonschema2pojo")
public class JokeSubmitForm implements Pojo {

    @JsonProperty("formatVersion")
    private Integer formatVersion;
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
    @JsonProperty("lang")
    private String lang;

    public JokeSubmitForm() {
    }

    public JokeSubmitForm(Integer formatVersion, String category, String type, String joke, String setup, String delivery, Flags flags, String lang) {
        this.formatVersion = formatVersion;
        this.category = category;
        this.type = type;
        this.joke = joke;
        this.setup = setup;
        this.delivery = delivery;
        this.flags = flags;
        this.lang = lang;
    }

    @JsonProperty("formatVersion")
    public Integer getFormatVersion() {
        return formatVersion;
    }

    @JsonProperty("formatVersion")
    public void setFormatVersion(Integer formatVersion) {
        this.formatVersion = formatVersion;
    }

    public JokeSubmitForm withFormatVersion(Integer formatVersion) {
        this.formatVersion = formatVersion;
        return this;
    }

    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
    }

    public JokeSubmitForm withCategory(String category) {
        this.category = category;
        return this;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    public JokeSubmitForm withType(String type) {
        this.type = type;
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

    public JokeSubmitForm withJoke(String joke) {
        this.joke = joke;
        return this;
    }

    @JsonProperty("setup")
    public String getSetup() {
        return setup;
    }

    @JsonProperty("setup")
    public void setSetup(String setup) {
        this.setup = setup;
    }

    public JokeSubmitForm withSetup(String setup) {
        this.setup = setup;
        return this;
    }

    @JsonProperty("delivery")
    public String getDelivery() {
        return delivery;
    }

    @JsonProperty("delivery")
    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public JokeSubmitForm withDelivery(String delivery) {
        this.delivery = delivery;
        return this;
    }

    @JsonProperty("flags")
    public Flags getFlags() {
        return flags;
    }

    @JsonProperty("flags")
    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public JokeSubmitForm withFlags(Flags flags) {
        this.flags = flags;
        return this;
    }

    @JsonProperty("lang")
    public String getLang() {
        return lang;
    }

    @JsonProperty("lang")
    public void setLang(String lang) {
        this.lang = lang;
    }

    public JokeSubmitForm withLang(String lang) {
        this.lang = lang;
        return this;
    }

}
