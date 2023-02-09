package com.terransky.stuffnthings.dataSources.kitsu.entries;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.terransky.stuffnthings.dataSources.kitsu.Attributes;
import com.terransky.stuffnthings.dataSources.kitsu.entries.enums.AgeRating;
import com.terransky.stuffnthings.dataSources.kitsu.entries.enums.Status;
import com.terransky.stuffnthings.dataSources.kitsu.entries.enums.Subtype;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("jsonschema2pojo")
public class EntryAttributes extends Attributes {

    @JsonProperty("slug")
    private String slug;
    @JsonProperty("synopsis")
    private String synopsis;
    @JsonProperty("description")
    private String description;
    @JsonProperty("canonicalTitle")
    private String canonicalTitle;
    @JsonProperty("abbreviatedTitles")
    private List<String> abbreviatedTitles = new ArrayList<>();
    @JsonProperty("averageRating")
    private Float averageRating;
    @JsonProperty("ratingFrequencies")
    private RatingFrequencies ratingFrequencies;
    @JsonProperty("userCount")
    private long userCount;
    @JsonProperty("favoritesCount")
    private long favoritesCount;
    @JsonProperty("startDate")
    private String startDate;
    @JsonProperty("endDate")
    private String endDate;
    @JsonProperty("nextRelease")
    private String nextRelease;
    @JsonProperty("popularityRank")
    private long popularityRank;
    @JsonProperty("ratingRank")
    private long ratingRank;
    @JsonProperty("ageRating")
    private String ageRating;
    @JsonProperty("ageRatingGuide")
    private String ageRatingGuide;
    @JsonProperty("subtype")
    private String subtype;
    @JsonProperty("status")
    private String status;
    @JsonProperty("posterImage")
    private PosterImage posterImage;
    @JsonProperty("coverImage")
    private CoverImage coverImage;
    private String baseUrl;

    @JsonIgnore
    @NotNull
    private String getEffectiveString(@NotNull String s, int limit) {
        if (s.length() > limit)
            return s.substring(0, limit - 1) + "…";
        return s;
    }

    @JsonProperty("slug")
    public String getSlug() {
        return slug;
    }

    @JsonProperty("slug")
    public void setSlug(String slug) {
        this.slug = slug;
    }

    @JsonProperty("synopsis")
    public String getSynopsis() {
        return getEffectiveString(synopsis, MessageEmbed.DESCRIPTION_MAX_LENGTH);
    }

    @JsonProperty("synopsis")
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    @JsonProperty("description")
    public String getDescription() {
        return getEffectiveString(description, MessageEmbed.DESCRIPTION_MAX_LENGTH);
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("canonicalTitle")
    public String getCanonicalTitle() {
        return getEffectiveString(canonicalTitle, MessageEmbed.TITLE_MAX_LENGTH);
    }

    @JsonProperty("canonicalTitle")
    public void setCanonicalTitle(String canonicalTitle) {
        this.canonicalTitle = canonicalTitle;
    }

    @JsonProperty("abbreviatedTitles")
    public List<String> getAbbreviatedTitles() {
        return new ArrayList<>() {{
            for (String abbreviatedTitle : abbreviatedTitles) {
                add(getEffectiveString(abbreviatedTitle, MessageEmbed.TITLE_MAX_LENGTH));
            }
        }};
    }

    @JsonProperty("abbreviatedTitles")
    public void setAbbreviatedTitles(List<String> abbreviatedTitles) {
        this.abbreviatedTitles = List.copyOf(abbreviatedTitles);
    }

    @JsonProperty("ageRating")
    public String getAgeRating() {
        return ageRating;
    }

    @JsonProperty("ageRating")
    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    @JsonIgnore
    public AgeRating getAgeRatingEnum() {
        return AgeRating.getAgeRatingByCode(ageRating);
    }

    @JsonProperty("averageRating")
    public Float getAverageRating() {
        return averageRating;
    }

    @JsonProperty("averageRating")
    public void setAverageRating(Float averageRating) {
        this.averageRating = averageRating;
    }

    @JsonProperty("ratingFrequencies")
    public RatingFrequencies getRatingFrequencies() {
        return ratingFrequencies;
    }

    @JsonProperty("ratingFrequencies")
    public void setRatingFrequencies(RatingFrequencies ratingFrequencies) {
        this.ratingFrequencies = ratingFrequencies;
    }

    @JsonProperty("userCount")
    public long getUserCount() {
        return userCount;
    }

    @JsonProperty("userCount")
    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    @JsonProperty("favoritesCount")
    public long getFavoritesCount() {
        return favoritesCount;
    }

    @JsonProperty("favoritesCount")
    public void setFavoritesCount(long favoritesCount) {
        this.favoritesCount = favoritesCount;
    }

    @JsonProperty("startDate")
    public String getStartDate() {
        return startDate;
    }

    @JsonProperty("startDate")
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @JsonProperty("endDate")
    public String getEndDate() {
        return endDate;
    }

    @JsonProperty("endDate")
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @JsonProperty("nextRelease")
    public void setNextRelease(String nextRelease) {
        this.nextRelease = nextRelease;
    }

    @JsonProperty("popularityRank")
    public long getPopularityRank() {
        return popularityRank;
    }

    @JsonProperty("popularityRank")
    public void setPopularityRank(long popularityRank) {
        this.popularityRank = popularityRank;
    }

    @JsonProperty("ratingRank")
    public long getRatingRank() {
        return ratingRank;
    }

    @JsonProperty("ratingRank")
    public void setRatingRank(long ratingRank) {
        this.ratingRank = ratingRank;
    }

    @JsonProperty("ageRatingGuide")
    public String getAgeRatingGuide() {
        return ageRatingGuide;
    }

    @JsonProperty("ageRatingGuide")
    public void setAgeRatingGuide(String ageRatingGuide) {
        this.ageRatingGuide = ageRatingGuide;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonIgnore
    public Status getStatusEnum() {
        return Status.getStatusByState(status);
    }

    @JsonProperty("subtype")
    public String getSubtype() {
        return subtype;
    }

    @JsonProperty("subtype")
    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    @JsonIgnore
    public Subtype getSubtypeEnum() {
        return Subtype.getSubtypeByCode(subtype);
    }

    @JsonProperty("posterImage")
    public PosterImage getPosterImage() {
        return posterImage;
    }

    @JsonProperty("posterImage")
    public void setPosterImage(PosterImage posterImage) {
        this.posterImage = posterImage;
    }

    @JsonProperty("coverImage")
    public CoverImage getCoverImage() {
        return coverImage;
    }

    @JsonProperty("coverImage")
    public void setCoverImage(CoverImage coverImage) {
        this.coverImage = coverImage;
    }

    @JsonIgnore
    public String getBaseUrl() {
        return baseUrl;
    }

    @JsonIgnore
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
