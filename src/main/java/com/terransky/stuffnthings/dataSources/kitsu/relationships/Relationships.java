package com.terransky.stuffnthings.dataSources.kitsu.relationships;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("jsonschema2pojo")
public class Relationships {

    @JsonProperty("genres")
    private RelationshipLinks genres;
    @JsonProperty("categories")
    private RelationshipLinks categories;
    @JsonProperty("castings")
    private RelationshipLinks castings;
    @JsonProperty("installments")
    private RelationshipLinks installments;
    @JsonProperty("mappings")
    private RelationshipLinks mappings;
    @JsonProperty("reviews")
    private RelationshipLinks reviews;
    @JsonProperty("mediaRelationships")
    private RelationshipLinks mediaRelationships;
    @JsonProperty("characters")
    private RelationshipLinks characters;
    @JsonProperty("staff")
    private RelationshipLinks staff;
    @JsonProperty("productions")
    private RelationshipLinks productions;
    @JsonProperty("quotes")
    private RelationshipLinks quotes;

    @JsonProperty("genres")
    public RelationshipLinks getGenres() {
        return genres;
    }

    @JsonProperty("genres")
    public void setGenres(RelationshipLinks genres) {
        this.genres = genres;
    }

    @JsonProperty("categories")
    public RelationshipLinks getCategories() {
        return categories;
    }

    @JsonProperty("categories")
    public void setCategories(RelationshipLinks categories) {
        this.categories = categories;
    }

    @JsonProperty("castings")
    public RelationshipLinks getCastings() {
        return castings;
    }

    @JsonProperty("castings")
    public void setCastings(RelationshipLinks castings) {
        this.castings = castings;
    }

    @JsonProperty("installments")
    public RelationshipLinks getInstallments() {
        return installments;
    }

    @JsonProperty("installments")
    public void setInstallments(RelationshipLinks installments) {
        this.installments = installments;
    }

    @JsonProperty("mappings")
    public RelationshipLinks getMappings() {
        return mappings;
    }

    @JsonProperty("mappings")
    public void setMappings(RelationshipLinks mappings) {
        this.mappings = mappings;
    }

    @JsonProperty("reviews")
    public RelationshipLinks getReviews() {
        return reviews;
    }

    @JsonProperty("reviews")
    public void setReviews(RelationshipLinks reviews) {
        this.reviews = reviews;
    }

    @JsonProperty("mediaRelationships")
    public RelationshipLinks getMediaRelationships() {
        return mediaRelationships;
    }

    @JsonProperty("mediaRelationships")
    public void setMediaRelationships(RelationshipLinks mediaRelationships) {
        this.mediaRelationships = mediaRelationships;
    }

    @JsonProperty("characters")
    public RelationshipLinks getCharacters() {
        return characters;
    }

    @JsonProperty("characters")
    public void setCharacters(RelationshipLinks characters) {
        this.characters = characters;
    }

    @JsonProperty("staff")
    public RelationshipLinks getStaff() {
        return staff;
    }

    @JsonProperty("staff")
    public void setStaff(RelationshipLinks staff) {
        this.staff = staff;
    }

    @JsonProperty("productions")
    public RelationshipLinks getProductions() {
        return productions;
    }

    @JsonProperty("productions")
    public void setProductions(RelationshipLinks productions) {
        this.productions = productions;
    }

    @JsonProperty("quotes")
    public RelationshipLinks getQuotes() {
        return quotes;
    }

    @JsonProperty("quotes")
    public void setQuotes(RelationshipLinks quotes) {
        this.quotes = quotes;
    }
}