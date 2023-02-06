package com.terransky.stuffnthings.dataSources.whatsInStandard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.interfaces.Pojo;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "$schema",
    "deprecated",
    "sets",
    "bans"
})
@Generated("jsonschema2pojo")
public class WhatsInStandardData implements Pojo {

    @JsonProperty("$schema")
    private String $schema;
    @JsonProperty("deprecated")
    private boolean deprecated;
    @JsonProperty("sets")
    private List<MtGSet> mtgSets = new ArrayList<>();
    @JsonProperty("bans")
    private List<Ban> bans = new ArrayList<>();

    @JsonProperty("$schema")
    public String get$schema() {
        return $schema;
    }

    @JsonProperty("$schema")
    public void set$schema(String $schema) {
        this.$schema = $schema;
    }

    @JsonProperty("deprecated")
    public boolean isDeprecated() {
        return deprecated;
    }

    @JsonProperty("deprecated")
    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    @JsonProperty("sets")
    public List<MtGSet> getSets() {
        return mtgSets;
    }

    @JsonProperty("sets")
    public void setSets(List<MtGSet> mtgSets) {
        this.mtgSets = mtgSets;
    }

    @JsonProperty("bans")
    public List<Ban> getBans() {
        return bans;
    }

    @JsonProperty("bans")
    public void setBans(List<Ban> bans) {
        this.bans = bans;
    }

}
