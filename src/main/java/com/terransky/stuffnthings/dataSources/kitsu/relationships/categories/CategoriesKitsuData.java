package com.terransky.stuffnthings.dataSources.kitsu.relationships.categories;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.dataSources.kitsu.KitsuData;

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
public class CategoriesKitsuData extends KitsuData {

    @JsonProperty("data")
    private List<CategoriesDatum> data = new ArrayList<>();

    @JsonProperty("data")
    public List<CategoriesDatum> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<CategoriesDatum> data) {
        this.data = data;
    }

    public List<String> getCategoriesList() {
        return new ArrayList<>() {{
            for (CategoriesDatum datum : data.stream().sorted().toList()) {
                add(datum.getAttributes().getTitle());
            }
        }};
    }

    public String getCategoriesString() {
        StringBuilder builder = new StringBuilder();
        for (String genre : getCategoriesList()) {
            builder.append(genre).append(", ");
        }
        return builder.substring(0, builder.length() - 2);
    }
}
