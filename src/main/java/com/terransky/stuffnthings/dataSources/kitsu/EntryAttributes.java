package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.terransky.stuffnthings.dataSources.kitsu.enums.AgeRating;
import com.terransky.stuffnthings.dataSources.kitsu.enums.Subtype;
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
    String slug;
    @JsonProperty("synopsis")
    String synopsis;
    @JsonProperty("titles")
    Titles titles;
    @JsonProperty("canonicalTitle")
    String canonicalTitle;
    @JsonProperty("abbreviatedTitles")
    List<String> abbreviatedTitles = new ArrayList<>();
    @JsonProperty("averageRating")
    String averageRating;
    @JsonProperty("ratingFrequencies")
    RatingFrequencies ratingFrequencies;
    @JsonProperty("userCount")
    long userCount;
    @JsonProperty("favoritesCount")
    long favoritesCount;
    @JsonProperty("startDate")
    Date startDate;
    @JsonProperty("endDate")
    Date endDate;
    @JsonProperty("popularityRank")
    long popularityRank;
    @JsonProperty("ratingRank")
    long ratingRank;
    @JsonProperty("ageRating")
    AgeRating ageRating;
    @JsonProperty("ageRatingGuide")
    String ageRatingGuide;
    @JsonProperty("subtype")
    Subtype subtype;
    @JsonProperty("status")
    String status;
    @JsonProperty("posterImage")
    PosterImage posterImage;
    @JsonProperty("coverImage")
    CoverImage coverImage;

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

    @JsonProperty("titles")
    public Titles getTitles() {
        return titles;
    }

    @JsonProperty("titles")
    public void setTitles(Titles titles) {
        this.titles = titles;
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

    @JsonProperty("ageRating")
    public AgeRating getAgeRating() {
        return ageRating;
    }

    @JsonProperty("ageRating")
    public void setAgeRating(String ageRating) {
        this.ageRating = AgeRating.getAgeRatingByCode(ageRating);
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

    public Subtype getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
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
}
