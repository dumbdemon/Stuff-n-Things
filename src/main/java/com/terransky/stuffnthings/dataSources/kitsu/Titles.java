package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "en",
    "en_jp",
    "ja_jp"
})
@Generated("jsonschema2pojo")
public class Titles {

    @JsonProperty("en")
    private String englishTitle;
    @JsonProperty("en_jp")
    private String romanizedTitle;
    @JsonProperty("ja_jp")
    private String japaneseTitle;

    @JsonProperty("en")
    @Nullable
    public String getEnglishTitle() {
        return englishTitle;
    }

    @JsonProperty("en")
    public void setEnglishTitle(String englishTitle) {
        this.englishTitle = englishTitle;
    }

    @JsonProperty("en_jp")
    public String getRomanizedTitle() {
        return romanizedTitle;
    }

    @JsonProperty("en_jp")
    public void setRomanizedTitle(String romanizedTitle) {
        this.romanizedTitle = romanizedTitle;
    }

    @JsonProperty("ja_jp")
    public String getJapaneseTitle() {
        return japaneseTitle;
    }

    @JsonProperty("ja_jp")
    public void setJapaneseTitle(String japaneseTitle) {
        this.japaneseTitle = japaneseTitle;
    }

}
