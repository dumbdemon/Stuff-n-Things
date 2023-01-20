package com.terransky.stuffnthings.dataSources.kitsu.relationships.categories;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.dataSources.kitsu.relationships.RelationshipLinks;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "parent",
    "anime",
    "drama",
    "manga"
})
@Generated("jsonschema2pojo")
public class CategoriesRelationships {

    @JsonProperty("parent")
    private RelationshipLinks parent;
    @JsonProperty("anime")
    private RelationshipLinks anime;
    @JsonProperty("drama")
    private RelationshipLinks drama;
    @JsonProperty("manga")
    private RelationshipLinks manga;

    @JsonProperty("parent")
    public RelationshipLinks getParent() {
        return parent;
    }

    @JsonProperty("parent")
    public void setParent(RelationshipLinks parent) {
        this.parent = parent;
    }

    @JsonProperty("anime")
    public RelationshipLinks getAnime() {
        return anime;
    }

    @JsonProperty("anime")
    public void setAnime(RelationshipLinks anime) {
        this.anime = anime;
    }

    @JsonProperty("drama")
    public RelationshipLinks getDrama() {
        return drama;
    }

    @JsonProperty("drama")
    public void setDrama(RelationshipLinks drama) {
        this.drama = drama;
    }

    @JsonProperty("manga")
    public RelationshipLinks getManga() {
        return manga;
    }

    @JsonProperty("manga")
    public void setManga(RelationshipLinks manga) {
        this.manga = manga;
    }
}
