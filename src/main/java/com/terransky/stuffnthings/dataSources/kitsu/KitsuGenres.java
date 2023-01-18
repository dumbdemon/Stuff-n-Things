package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data",
    "meta",
    "links"
})
@Generated("jsonschema2pojo")
public class KitsuGenres {

    @JsonProperty("data")
    private List<GenreDatum> data = new ArrayList<>();
    @JsonProperty("meta")
    private Meta meta;
    @JsonProperty("links")
    private PageNavigationLinks links;

    @JsonProperty("meta")
    public Meta getMeta() {
        return meta;
    }

    @JsonProperty("meta")
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    @JsonProperty("data")
    public List<GenreDatum> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<GenreDatum> data) {
        this.data = data;
    }

    @JsonProperty("links")
    public PageNavigationLinks getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(PageNavigationLinks links) {
        this.links = links;
    }

    public List<String> getGenreList() {
        return new ArrayList<>() {{
            for (GenreDatum datum : data.stream().sorted().toList()) {
                add(datum.getAttributes().getName());
            }
        }};
    }

    public String getGenreString() {
        StringBuilder builder = new StringBuilder();
        for (String genre : getGenreList()) {
            builder.append(genre).append(", ");
        }
        return builder.substring(0, builder.length() - 2);
    }
}
