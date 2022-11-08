package com.terransky.StuffnThings.sources.freshMemes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    "preview"
})
@Generated("jsonschema2pojo")
public class FreshMemeData {

    @JsonProperty("postLink")
    private String postLink;
    @JsonProperty("subreddit")
    private String subreddit;
    @JsonProperty("title")
    private String title;
    @JsonProperty("url")
    private String url;
    @JsonProperty("nsfw")
    private boolean nsfw;
    @JsonProperty("spoiler")
    private boolean spoiler;
    @JsonProperty("author")
    private String author;
    @JsonProperty("ups")
    private long ups;
    @JsonProperty("preview")
    private List<String> preview = new LinkedList<>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
    public boolean isExplicit() {
        return nsfw;
    }

    @JsonProperty("nsfw")
    public void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }

    @JsonProperty("spoiler")
    public boolean isSpoiler() {
        return spoiler;
    }

    @JsonProperty("spoiler")
    public void setSpoiler(boolean spoiler) {
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
    public long getUps() {
        return ups;
    }

    @JsonProperty("ups")
    public void setUps(long ups) {
        this.ups = ups;
    }

    @JsonProperty("preview")
    public List<String> getPreview() {
        return preview;
    }

    @JsonProperty("preview")
    public void setPreview(List<String> preview) {
        this.preview = preview;
    }
}
