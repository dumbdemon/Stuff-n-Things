package com.terransky.stuffnthings.dataSources.oxfordDictionary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.interfaces.Pojo;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "metadata",
    "results",
    "word"
})
@Generated("jsonschema2pojo")
public class OxfordData implements Pojo {

    @JsonProperty("id")
    private String id;
    @JsonProperty("metadata")
    private OxfordMetadata metadata;
    @JsonProperty("results")
    private List<Result> results = new ArrayList<>();
    @JsonProperty("word")
    private String word;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("metadata")
    public OxfordMetadata getMetadata() {
        return metadata;
    }

    @JsonProperty("metadata")
    public void setMetadata(OxfordMetadata metadata) {
        this.metadata = metadata;
    }

    @JsonProperty("results")
    public List<Result> getResults() {
        return results;
    }

    @JsonProperty("results")
    public void setResults(List<Result> results) {
        this.results = results;
    }

    @JsonProperty("word")
    public String getWord() {
        return word;
    }

    @JsonProperty("word")
    public void setWord(String word) {
        this.word = word;
    }

}
