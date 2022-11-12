package com.terransky.stuffnthings.dataSources.oxfordDictionary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "homographNumber",
    "senses"
})
@Generated("jsonschema2pojo")
public class Entry {

    @JsonProperty("homographNumber")
    private String homographNumber;
    @JsonProperty("senses")
    private List<Sense> senses = new ArrayList<>();

    @JsonProperty("homographNumber")
    public String getHomographNumber() {
        return homographNumber;
    }

    @JsonProperty("homographNumber")
    public void setHomographNumber(String homographNumber) {
        this.homographNumber = homographNumber;
    }

    @JsonProperty("senses")
    public List<Sense> getSenses() {
        return senses;
    }

    @JsonProperty("senses")
    public void setSenses(List<Sense> senses) {
        this.senses = senses;
    }

}
