package com.terransky.stuffnthings.dataSources.kitsu.relationships.categories;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.dataSources.kitsu.entries.EntryAttributes;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "createdAt",
    "updatedAt",
    "title",
    "description",
    "totalMediaCount",
    "slug",
    "nsfw",
    "childCount"
})
@Generated("jsonschema2pojo")
public class CategoriesAttributes extends EntryAttributes {

    @JsonProperty("title")
    private String title;
    @JsonProperty("totalMediaCount")
    private Long totalMediaCount;
    @JsonProperty("nsfw")
    private Boolean nsfw;
    @JsonProperty("childCount")
    private Long childCount;

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("totalMediaCount")
    public Long getTotalMediaCount() {
        return totalMediaCount;
    }

    @JsonProperty("totalMediaCount")
    public void setTotalMediaCount(Long totalMediaCount) {
        this.totalMediaCount = totalMediaCount;
    }

    @JsonProperty("nsfw")
    public Boolean getNsfw() {
        return nsfw;
    }

    @JsonProperty("nsfw")
    public void setNsfw(Boolean nsfw) {
        this.nsfw = nsfw;
    }

    @JsonProperty("childCount")
    public Long getChildCount() {
        return childCount;
    }

    @JsonProperty("childCount")
    public void setChildCount(Long childCount) {
        this.childCount = childCount;
    }

}
