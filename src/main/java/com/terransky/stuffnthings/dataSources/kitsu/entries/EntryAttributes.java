package com.terransky.stuffnthings.dataSources.kitsu.entries;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.terransky.stuffnthings.dataSources.kitsu.Attributes;
import com.terransky.stuffnthings.dataSources.kitsu.entries.enums.Status;
import com.terransky.stuffnthings.dataSources.kitsu.entries.enums.Subtype;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.Date;
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
    private String averageRating;
    @JsonProperty("ratingFrequencies")
    private RatingFrequencies ratingFrequencies;
    @JsonProperty("userCount")
    private long userCount;
    @JsonProperty("favoritesCount")
    private long favoritesCount;
    @JsonProperty("startDate")
    private Date startDate;
    @JsonProperty("endDate")
    private Date endDate;
    @JsonProperty("nextRelease")
    private Date nextRelease;
    @JsonProperty("popularityRank")
    private long popularityRank;
    @JsonProperty("ratingRank")
    private long ratingRank;
    @JsonProperty("ageRatingGuide")
    private String ageRatingGuide;
    @JsonProperty("subtype")
    private Subtype subtype;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("posterImage")
    private PosterImage posterImage;
    @JsonProperty("coverImage")
    private CoverImage coverImage;
    private String baseUrl;

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
        return synopsis;
    }

    @JsonProperty("synopsis")
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("canonicalTitle")
    public String getCanonicalTitle() {
        return canonicalTitle;
    }

    @JsonProperty("canonicalTitle")
    public void setCanonicalTitle(String canonicalTitle) {
        this.canonicalTitle = canonicalTitle;
    }

    @JsonProperty("abbreviatedTitles")
    public List<String> getAbbreviatedTitles() {
        return abbreviatedTitles;
    }

    @JsonProperty("abbreviatedTitles")
    public void setAbbreviatedTitles(List<String> abbreviatedTitles) {
        this.abbreviatedTitles = abbreviatedTitles;
    }

    @JsonProperty("averageRating")
    public String getAverageRating() {
        return averageRating;
    }

    @JsonProperty("averageRating")
    public void setAverageRating(String averageRating) {
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
    public Date getStartDate() {
        return startDate;
    }

    @JsonProperty("startDate")
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @JsonProperty("endDate")
    @Nullable
    public Date getEndDate() {
        return endDate;
    }

    @JsonProperty("endDate")
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @JsonProperty("nextRelease")
    public Date getNextRelease() {
        return nextRelease;
    }

    @JsonProperty("nextRelease")
    public void setNextRelease(Date nextRelease) {
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
    public Status getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        if (status == null) {
            this.status = Status.UNKNOWN;
            return;
        }
        this.status = Status.getStatusByState(status);
    }

    public Subtype getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        if (subtype == null) {
            this.subtype = Subtype.UNKNOWN;
            return;
        }
        this.subtype = Subtype.getSubtypeByCode(subtype);
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

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
