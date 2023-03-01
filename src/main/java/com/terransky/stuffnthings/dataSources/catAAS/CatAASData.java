package com.terransky.stuffnthings.dataSources.catAAS;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.dataSources.CodesAndMessages;
import com.terransky.stuffnthings.utilities.general.Timestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://cataas.com/doc.html">CatAAS Documentation</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "message",
    "code",
    "tags",
    "createdAt",
    "updatedAt",
    "validated",
    "owner",
    "file",
    "mimetype",
    "size",
    "_id",
    "url"
})
@SuppressWarnings("unused")
public class CatAASData extends CodesAndMessages {

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<>();
    @JsonProperty("createdAt")
    private String createdAt;
    @JsonProperty("updatedAt")
    private String updatedAt;
    @JsonProperty("validated")
    private Boolean validated;
    @JsonProperty("owner")
    private String owner;
    @JsonProperty("file")
    private String file;
    @JsonProperty("mimetype")
    private String mimetype;
    @JsonProperty("size")
    private Double size;
    @JsonProperty("_id")
    private String id;
    @JsonProperty("url")
    private String url;

    @JsonIgnore
    public boolean hasError() {
        return getMessage() != null || getCode() != null;
    }

    @JsonProperty("tags")
    public List<String> getTags() {
        return tags;
    }

    @JsonProperty("tags")
    public void setTags(List<String> tags) {
        this.tags = List.copyOf(tags);
    }

    @JsonIgnore
    public String getTagsAsString() {
        StringBuilder tagsString = new StringBuilder();
        if (tags.isEmpty()) return "No Tags";
        for (String tag : tags) {
            tagsString.append(tag).append(", ");
        }
        return tagsString.substring(0, tagsString.length() - 2);
    }

    @JsonProperty("createdAt")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("createdAt")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonIgnore
    public OffsetDateTime getCreatedAtAsDate() {
        return OffsetDateTime.parse(createdAt);
    }

    @JsonIgnore
    public String getCreatedAtAsTimestamp() {
        return Timestamp.getDateAsTimestamp(getCreatedAtAsDate(), Timestamp.LONG_DATE_W_SHORT_TIME);
    }

    @JsonProperty("updatedAt")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updatedAt")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonIgnore
    public OffsetDateTime getUpdatedAtAsDate() {
        return OffsetDateTime.parse(updatedAt);
    }

    @JsonIgnore
    public String getUpdatedAtAsTimestamp() {
        return Timestamp.getDateAsTimestamp(getUpdatedAtAsDate(), Timestamp.LONG_DATE_W_SHORT_TIME);
    }

    @JsonProperty("validated")
    public Boolean getValidated() {
        return validated;
    }

    @JsonProperty("validated")

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    @JsonProperty("owner")
    public String getOwner() {
        return owner;
    }

    @JsonProperty("owner")
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @JsonIgnore
    public String getEffectiveOwner() {
        if (owner == null || "null".equals(owner)) {
            return "No Owner";
        }
        return owner;
    }

    @JsonProperty("file")
    public String getFile() {
        return file;
    }

    @JsonProperty("file")
    public void setFile(String file) {
        this.file = file;
    }

    @JsonProperty("mimetype")
    public String getMimetype() {
        return mimetype;
    }

    @JsonProperty("mimetype")
    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    @JsonProperty("size")
    public Double getSize() {
        return size;
    }

    @JsonProperty("size")
    public void setSize(Double size) {
        this.size = size;
    }

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }
}
