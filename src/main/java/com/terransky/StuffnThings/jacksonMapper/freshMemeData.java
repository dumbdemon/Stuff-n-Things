package com.terransky.StuffnThings.jacksonMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class freshMemeData {
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
    private int ups;
    @JsonIgnore
    @JsonProperty("preview")
    private String[] preview;
    @Nullable
    @JsonProperty("code")
    private Integer code;
    @Nullable
    @JsonProperty("message")
    private String message;


    public boolean isExplicit() {
        return nsfw;
    }

    public boolean isSpoiler() {
        return spoiler;
    }

    public int getUps() {
        return ups;
    }

    public String getAuthor() {
        return author;
    }

    public String getPostLink() {
        return postLink;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public @Nullable Integer getCode() {
        return code;
    }
}
