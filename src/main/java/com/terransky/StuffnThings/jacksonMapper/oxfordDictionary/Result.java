package com.terransky.StuffnThings.jacksonMapper.oxfordDictionary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "language",
    "lexicalEntries",
    "type",
    "word"
})
@Generated("jsonschema2pojo")
public class Result {

    @JsonProperty("id")
    private String id;
    @JsonProperty("language")
    private String language;
    @JsonProperty("lexicalEntries")
    private List<LexicalEntry> lexicalEntries = new ArrayList<>();
    @JsonProperty("type")
    private String type;
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

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("lexicalEntries")
    public List<LexicalEntry> getLexicalEntries() {
        return lexicalEntries;
    }

    @JsonProperty("lexicalEntries")
    public void setLexicalEntries(List<LexicalEntry> lexicalEntries) {
        this.lexicalEntries = lexicalEntries;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
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
