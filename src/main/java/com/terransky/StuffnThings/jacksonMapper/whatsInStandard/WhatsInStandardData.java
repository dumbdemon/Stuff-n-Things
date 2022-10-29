
package com.terransky.StuffnThings.jacksonMapper.whatsInStandard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
public class WhatsInStandardData {

    @JsonProperty("$schema")
    private String $schema;
    @JsonProperty("deprecated")
    private boolean deprecated;
    @JsonProperty("sets")
    private List<Set> sets = new ArrayList<>();
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
    public List<Set> getSets() {
        return sets;
    }

    @JsonProperty("sets")
    public void setSets(List<Set> sets) {
        this.sets = sets;
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
