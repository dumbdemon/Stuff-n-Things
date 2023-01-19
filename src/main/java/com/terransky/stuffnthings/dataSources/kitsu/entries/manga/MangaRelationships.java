package com.terransky.stuffnthings.dataSources.kitsu.entries.manga;

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
    "characters",
    "staff",
    "productions",
    "quotes",
    "chapters",
    "mangaCharacters",
    "mangaStaff"
})
@Generated("jsonschema2pojo")
public class MangaRelationships extends Relationships {

    @JsonProperty("chapters")
    private RelationshipLinks chapters;
    @JsonProperty("mangaCharacters")
    private RelationshipLinks mangaCharacters;
    @JsonProperty("mangaStaff")
    private RelationshipLinks mangaStaff;

    @JsonProperty("chapters")
    public RelationshipLinks getChapters() {
        return chapters;
    }

    @JsonProperty("chapters")
    public void setChapters(RelationshipLinks chapters) {
        this.chapters = chapters;
    }

    @JsonProperty("mangaCharacters")
    public RelationshipLinks getMangaCharacters() {
        return mangaCharacters;
    }

    @JsonProperty("mangaCharacters")
    public void setMangaCharacters(RelationshipLinks mangaCharacters) {
        this.mangaCharacters = mangaCharacters;
    }

    @JsonProperty("mangaStaff")
    public RelationshipLinks getMangaStaff() {
        return mangaStaff;
    }

    @JsonProperty("mangaStaff")
    public void setMangaStaff(RelationshipLinks mangaStaff) {
        this.mangaStaff = mangaStaff;
    }
}
