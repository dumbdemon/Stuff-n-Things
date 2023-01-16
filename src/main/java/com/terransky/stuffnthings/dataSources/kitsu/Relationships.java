package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "genres",
    "categories",
    "castings",
    "installments",
    "mappings",
    "reviews",
    "mediaRelationships",
    "chapters",
    "mangaCharacters",
    "mangaStaff"
})
@Generated("jsonschema2pojo")
public class Relationships {

    @JsonProperty("genres")
    private MangaLinks genres;
    @JsonProperty("categories")
    private MangaLinks categories;
    @JsonProperty("castings")
    private MangaLinks castings;
    @JsonProperty("installments")
    private MangaLinks installments;
    @JsonProperty("mappings")
    private MangaLinks mappings;
    @JsonProperty("reviews")
    private MangaLinks reviews;
    @JsonProperty("mediaRelationships")
    private MangaLinks mediaRelationships;
    @JsonProperty("chapters")
    private MangaLinks chapters;
    @JsonProperty("mangaCharacters")
    private MangaLinks mangaCharacters;
    @JsonProperty("mangaStaff")
    private MangaLinks mangaStaff;

    @JsonProperty("genres")
    public MangaLinks getGenres() {
        return genres;
    }

    @JsonProperty("genres")
    public void setGenres(MangaLinks genres) {
        this.genres = genres;
    }

    @JsonProperty("categories")
    public MangaLinks getCategories() {
        return categories;
    }

    @JsonProperty("categories")
    public void setCategories(MangaLinks categories) {
        this.categories = categories;
    }

    @JsonProperty("castings")
    public MangaLinks getCastings() {
        return castings;
    }

    @JsonProperty("castings")
    public void setCastings(MangaLinks castings) {
        this.castings = castings;
    }

    @JsonProperty("installments")
    public MangaLinks getInstallments() {
        return installments;
    }

    @JsonProperty("installments")
    public void setInstallments(MangaLinks installments) {
        this.installments = installments;
    }

    @JsonProperty("mappings")
    public MangaLinks getMappings() {
        return mappings;
    }

    @JsonProperty("mappings")
    public void setMappings(MangaLinks mappings) {
        this.mappings = mappings;
    }

    @JsonProperty("reviews")
    public MangaLinks getReviews() {
        return reviews;
    }

    @JsonProperty("reviews")
    public void setReviews(MangaLinks reviews) {
        this.reviews = reviews;
    }

    @JsonProperty("mediaRelationships")
    public MangaLinks getMediaRelationships() {
        return mediaRelationships;
    }

    @JsonProperty("mediaRelationships")
    public void setMediaRelationships(MangaLinks mediaRelationships) {
        this.mediaRelationships = mediaRelationships;
    }

    @JsonProperty("chapters")
    public MangaLinks getChapters() {
        return chapters;
    }

    @JsonProperty("chapters")
    public void setChapters(MangaLinks chapters) {
        this.chapters = chapters;
    }

    @JsonProperty("mangaCharacters")
    public MangaLinks getMangaCharacters() {
        return mangaCharacters;
    }

    @JsonProperty("mangaCharacters")
    public void setMangaCharacters(MangaLinks mangaCharacters) {
        this.mangaCharacters = mangaCharacters;
    }

    @JsonProperty("mangaStaff")
    public MangaLinks getMangaStaff() {
        return mangaStaff;
    }

    @JsonProperty("mangaStaff")
    public void setMangaStaff(MangaLinks mangaStaff) {
        this.mangaStaff = mangaStaff;
    }

}
