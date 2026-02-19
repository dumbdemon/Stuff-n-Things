
package com.terransky.stuffnthings.dataSources.freeDictionary;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "definition",
    "example",
    "synonyms",
    "antonyms"
})
@Generated("jsonschema2pojo")
public class Definition {

    @JsonProperty("definition")
    private String definition;
    @JsonProperty("example")
    private String example;
    @JsonProperty("synonyms")
    @Valid
    private List<String> synonyms;
    @JsonProperty("antonyms")
    @Valid
    private List<String> antonyms;
    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("definition")
    public String getDefinition() {
        return definition;
    }

    @JsonProperty("definition")
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @JsonProperty("example")
    public String getExample() {
        return example;
    }

    @JsonProperty("example")
    public void setExample(String example) {
        this.example = example;
    }

    @JsonProperty("synonyms")
    public List<String> getSynonyms() {
        return synonyms;
    }

    @JsonProperty("synonyms")
    public void setSynonyms(List<String> synonyms) {
        this.synonyms = List.copyOf(synonyms);
    }

    @JsonProperty("antonyms")
    public List<String> getAntonyms() {
        return antonyms;
    }

    @JsonProperty("antonyms")
    public void setAntonyms(List<String> antonyms) {
        this.antonyms = List.copyOf(antonyms);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
