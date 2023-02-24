package com.terransky.stuffnthings.dataSources.jokeAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

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
public class JokeSubmitForm extends JokeAPI {

    @JsonProperty("formatVersion")
    private Integer formatVersion;

    public JokeSubmitForm() {
    }

    public JokeSubmitForm(Integer formatVersion, String category, String type, String joke, String setup, String delivery, Flags flags, String lang) {
        this.formatVersion = formatVersion;
        setCategory(category);
        setType(type);
        setJoke(joke);
        setSetup(setup);
        setDelivery(delivery);
        setFlags(flags);
        setLang(lang);
    }

    @JsonProperty("formatVersion")
    public Integer getFormatVersion() {
        return formatVersion;
    }

    @JsonProperty("formatVersion")
    public void setFormatVersion(Integer formatVersion) {
        this.formatVersion = formatVersion;
    }
}
