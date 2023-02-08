package com.terransky.stuffnthings.dataSources.kitsu.entries.manga;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.dataSources.kitsu.KitsuData;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://kitsu.docs.apiary.io/#">API Documentaion</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data",
    "meta",
    "links"
})
@Generated("jsonschema2pojo")
public class MangaKitsuData extends KitsuData {

    @JsonProperty("data")
    private List<MangaDatum> data = new ArrayList<>();

    @JsonProperty("data")
    public List<MangaDatum> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<MangaDatum> data) {
        this.data = List.copyOf(data);
    }
}
