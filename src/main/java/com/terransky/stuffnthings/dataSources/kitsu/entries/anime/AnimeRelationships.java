package com.terransky.stuffnthings.dataSources.kitsu.entries.anime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.dataSources.kitsu.relationships.RelationshipLinks;
import com.terransky.stuffnthings.dataSources.kitsu.relationships.Relationships;

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
    "episodes",
    "streamingLinks",
    "staff",
    "productions",
    "quotes",
    "animeProductions",
    "animeCharacters",
    "animeStaff"
})
@Generated("jsonschema2pojo")
public class AnimeRelationships extends Relationships {

    @JsonProperty("episodes")
    private RelationshipLinks episodes;
    @JsonProperty("v")
    private RelationshipLinks streamingLinks;
    @JsonProperty("animeProductions")
    private RelationshipLinks animeProductions;
    @JsonProperty("animeCharacters")
    private RelationshipLinks animeCharacters;
    @JsonProperty("animeStaff")
    private RelationshipLinks animeStaff;

    @JsonProperty("episodes")
    public RelationshipLinks getEpisodes() {
        return episodes;
    }

    @JsonProperty("episodes")
    public void setEpisodes(RelationshipLinks episodes) {
        this.episodes = episodes;
    }

    @JsonProperty("streamingLinks")
    public RelationshipLinks getStreamingLinks() {
        return streamingLinks;
    }

    @JsonProperty("streamingLinks")
    public void setStreamingLinks(RelationshipLinks streamingLinks) {
        this.streamingLinks = streamingLinks;
    }

    @JsonProperty("animeProductions")
    public RelationshipLinks getAnimeProductions() {
        return animeProductions;
    }

    @JsonProperty("animeProductions")
    public void setAnimeProductions(RelationshipLinks animeProductions) {
        this.animeProductions = animeProductions;
    }

    @JsonProperty("animeCharacters")
    public RelationshipLinks getAnimeCharacters() {
        return animeCharacters;
    }

    @JsonProperty("animeCharacters")
    public void setAnimeCharacters(RelationshipLinks animeCharacters) {
        this.animeCharacters = animeCharacters;
    }

    @JsonProperty("animeStaff")
    public RelationshipLinks getAnimeStaff() {
        return animeStaff;
    }

    @JsonProperty("animeStaff")
    public void setAnimeStaff(RelationshipLinks animeStaff) {
        this.animeStaff = animeStaff;
    }
}
