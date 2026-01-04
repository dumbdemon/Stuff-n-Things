
package com.terransky.stuffnthings.dataSources.freeDictionary;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "partOfSpeech",
    "definitions"
})
@Generated("jsonschema2pojo")
public class Meaning {

    @JsonProperty("partOfSpeech")
    private String partOfSpeech;
    @JsonProperty("definitions")
    @Valid
    private List<Definition> definitions;
    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("partOfSpeech")
    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    @JsonProperty("partOfSpeech")
    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    @JsonProperty("definitions")
    public List<Definition> getDefinitions() {
        return definitions;
    }

    @JsonProperty("definitions")
    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions;
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
