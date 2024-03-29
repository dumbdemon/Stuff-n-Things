package com.terransky.stuffnthings.dataSources.freshMemes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.dataSources.CodesAndMessages;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "postLink",
    "subreddit",
    "title",
    "url",
    "nsfw",
    "spoiler",
    "author",
    "ups",
    "preview",
    "code",
    "message"
})
@Generated("jsonschema2pojo")
public class FreshMemeData extends CodesAndMessages {

    @JsonProperty("postLink")
    private String postLink;
    @JsonProperty("subreddit")
    private String subreddit;
    @JsonProperty("title")
    private String title;
    @JsonProperty("url")
    private String url;
    @JsonProperty("nsfw")
    private Boolean nsfw;
    @JsonProperty("spoiler")
    private Boolean spoiler;
    @JsonProperty("author")
    private String author;
    @JsonProperty("ups")
    private Long ups;
    @JsonProperty("preview")
    private List<String> preview = new ArrayList<>();

    @JsonProperty("postLink")
    public String getPostLink() {
        return postLink;
    }

    @JsonProperty("postLink")
    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    @JsonProperty("subreddit")
    public String getSubreddit() {
        return subreddit;
    }

    @JsonProperty("subreddit")
    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("nsfw")
    public Boolean isExplicit() {
        return nsfw;
    }

    @JsonProperty("nsfw")
    public void setNsfw(Boolean nsfw) {
        this.nsfw = nsfw;
    }

    @JsonProperty("spoiler")
    public Boolean isSpoiler() {
        return spoiler;
    }

    @JsonProperty("spoiler")
    public void setSpoiler(Boolean spoiler) {
        this.spoiler = spoiler;
    }

    @JsonProperty("author")
    public String getAuthor() {
        return author;
    }

    @JsonProperty("author")
    public void setAuthor(String author) {
        this.author = author;
    }

    @JsonProperty("ups")
    public Long getUps() {
        return ups;
    }

    @JsonProperty("ups")
    public void setUps(Long ups) {
        this.ups = ups;
    }

    @JsonProperty("preview")
    public List<String> getPreview() {
        return preview;
    }

    @JsonProperty("preview")
    public void setPreview(List<String> preview) {
        this.preview = List.copyOf(preview);
    }
}
