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
    "episodeCount",
    "episodeLength",
    "youtubeVideoId",
    "showType",
    "nsfw"
})
@Generated("jsonschema2pojo")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnimeAttributes extends EntryAttributes {

    @JsonProperty("episodeCount")
    private Long episodeCount;
    @JsonProperty("episodeLength")
    private Long episodeLength;
    @JsonProperty("youtubeVideoId")
    private String youtubeVideoId;
    @JsonProperty("nsfw")
    private Boolean nsfw;

    @JsonProperty("episodeCount")
    public Long getEpisodeCount() {
        return episodeCount;
    }

    @JsonProperty("episodeCount")
    public void setEpisodeCount(Long episodeCount) {
        this.episodeCount = episodeCount;
    }

    @JsonProperty("episodeLength")
    public Long getEpisodeLength() {
        return episodeLength;
    }

    @JsonProperty("episodeLength")
    public void setEpisodeLength(Long episodeLength) {
        this.episodeLength = episodeLength;
    }

    @JsonProperty("youtubeVideoId")
    public String getYoutubeVideoId() {
        return youtubeVideoId;
    }

    @JsonProperty("youtubeVideoId")
    public void setYoutubeVideoId(String youtubeVideoId) {
        this.youtubeVideoId = youtubeVideoId;
    }

    @JsonProperty("nsfw")
    public Boolean getNsfw() {
        return nsfw;
    }

    @JsonProperty("nsfw")
    public void setNsfw(Boolean nsfw) {
        this.nsfw = nsfw;
    }
}
