
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
    "word",
    "phonetic",
    "phonetics",
    "origin",
    "meanings"
})
@Generated("jsonschema2pojo")
public class FreeDictionaryDatum {

    @JsonProperty("word")
    private String word;
    @JsonProperty("phonetic")
    private String phonetic;
    @JsonProperty("phonetics")
    @Valid
    private List<Phonetic> phonetics;
    @JsonProperty("origin")
    private String origin;
    @JsonProperty("meanings")
    @Valid
    private List<Meaning> meanings;
    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("word")
    public String getWord() {
        return word;
    }

    @JsonProperty("word")
    public void setWord(String word) {
        this.word = word;
    }

    @JsonProperty("phonetic")
    public String getPhonetic() {
        return phonetic;
    }

    @JsonProperty("phonetic")
    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    @JsonProperty("phonetics")
    public List<Phonetic> getPhonetics() {
        return phonetics;
    }

    @JsonProperty("phonetics")
    public void setPhonetics(List<Phonetic> phonetics) {
        this.phonetics = List.copyOf(phonetics);
    }

    @JsonProperty("origin")
    public String getOrigin() {
        return origin;
    }

    @JsonProperty("origin")
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    @JsonProperty("meanings")
    public List<Meaning> getMeanings() {
        return meanings;
    }

    @JsonProperty("meanings")
    public void setMeanings(List<Meaning> meanings) {
        this.meanings = List.copyOf(meanings);
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
