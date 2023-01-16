package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "createdAt",
    "updatedAt",
    "slug",
    "synopsis",
    "coverImageTopOffset",
    "titles",
    "canonicalTitle",
    "abbreviatedTitles",
    "averageRating",
    "ratingFrequencies",
    "userCount",
    "favoritesCount",
    "startDate",
    "endDate",
    "popularityRank",
    "ratingRank",
    "ageRating",
    "ageRatingGuide",
    "subtype",
    "status",
    "tba",
    "posterImage",
    "coverImage",
    "chapterCount",
    "volumeCount",
    "serialization",
    "mangaType"
})
@Generated("jsonschema2pojo")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MangaAttributes extends Attributes {

    @JsonProperty("chapterCount")
    private Long chapterCount;
    @JsonProperty("volumeCount")
    private Long volumeCount;
    @JsonProperty("serialization")
    private String serialization;

    @JsonProperty("chapterCount")
    public Long getChapterCount() {
        return chapterCount;
    }

    @JsonProperty("chapterCount")
    public void setChapterCount(long chapterCount) {
        this.chapterCount = chapterCount;
    }

    @JsonProperty("volumeCount")
    public Long getVolumeCount() {
        return volumeCount;
    }

    @JsonProperty("volumeCount")
    public void setVolumeCount(long volumeCount) {
        this.volumeCount = volumeCount;
    }

    @JsonProperty("serialization")
    public String getSerialization() {
        return serialization;
    }

    @JsonProperty("serialization")
    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }
}
