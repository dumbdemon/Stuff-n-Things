
package com.terransky.StuffnThings.jacksonMapper.owlBotData;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "word",
    "pronunciation",
    "definitions"
})
@Generated("jsonschema2pojo")
public class OwlBotData {

    @JsonProperty("word")
    private String word;
    @JsonProperty("pronunciation")
    private String pronunciation;
    @JsonProperty("definitions")
    private List<Definition> definitions = null;

    @JsonProperty("word")
    public String getWord() {
        return word;
    }

    @JsonProperty("word")
    public void setWord(String word) {
        this.word = word;
    }

    @JsonProperty("pronunciation")
    public String getPronunciation() {
        return pronunciation;
    }

    @JsonProperty("pronunciation")
    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    @JsonProperty("definitions")
    public List<Definition> getDefinitions() {
        return definitions;
    }

    @JsonProperty("definitions")
    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions;
    }

}
