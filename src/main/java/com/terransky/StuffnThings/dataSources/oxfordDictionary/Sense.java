package com.terransky.StuffnThings.dataSources.oxfordDictionary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "definitions",
    "id",
    "subsenses"
})
@Generated("jsonschema2pojo")
public class Sense {

    @JsonProperty("definitions")
    private List<String> definitions = new ArrayList<>();
    @JsonProperty("id")
    private String id;
    @JsonProperty("subsenses")
    private List<Subsense> subsenses = new ArrayList<>();

    @JsonProperty("definitions")
    public List<String> getDefinitions() {
        return definitions;
    }

    @JsonProperty("definitions")
    public void setDefinitions(List<String> definitions) {
        this.definitions = definitions;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("subsenses")
    public List<Subsense> getSubsenses() {
        return subsenses;
    }

    @JsonProperty("subsenses")
    public void setSubsenses(List<Subsense> subsenses) {
        this.subsenses = subsenses;
    }

}
