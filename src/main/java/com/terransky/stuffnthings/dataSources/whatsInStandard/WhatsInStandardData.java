
package com.terransky.stuffnthings.dataSources.whatsInStandard;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * What's in Standard? API v6
 * <p>
 * 
 * 
 */
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
    private Object $schema;

    /**
     * Deprecated
     * <p>
     * Whether this API version is deprecated or not. Configure your software to notify you if this field is ever true so you aren't caught off-guard if this version is deprecated then eventually killed.
     * (Required)
     * 
     */
    @JsonProperty("deprecated")
    @JsonPropertyDescription("Whether this API version is deprecated or not. Configure your software to notify you if this field is ever true so you aren't caught off-guard if this version is deprecated then eventually killed.")
    @NotNull
    private Boolean deprecated;

    /**
     * Sets
     * <p>
     * A superset of the sets in Standard, ordered from least recent release date (`enterDate.exact`) to newest. To get a list of Standard sets you MUST filter this array by comparing your local time to each set's enter and exit date, as the array by itself will contain sets that have already exited Standard as well as sets that have not yet entered it.
     * (Required)
     * 
     */
    @JsonProperty("sets")
    @JsonPropertyDescription("A superset of the sets in Standard, ordered from least recent release date (`enterDate.exact`) to newest. To get a list of Standard sets you MUST filter this array by comparing your local time to each set's enter and exit date, as the array by itself will contain sets that have already exited Standard as well as sets that have not yet entered it.")
    @Valid
    @NotNull
    private List<MtGSet> mtgSets;

    /**
     * Bans
     * <p>
     * Cards that have been banned from Standard, ordered from oldest ban to newest. This array may contain bans from a previous rotation; you should filter them out based on the provided set `setCode` and that set's exit date from Standard.
     * (Required)
     * 
     */
    @JsonProperty("bans")
    @JsonPropertyDescription("Cards that have been banned from Standard, ordered from oldest ban to newest. This array may contain bans from a previous rotation; you should filter them out based on the provided set `setCode` and that set's exit date from Standard.")
    @Valid
    @NotNull
    private List<Ban> bans;

    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("$schema")
    public Object get$schema() {
        return $schema;
    }

    @JsonProperty("$schema")
    public void set$schema(Object $schema) {
        this.$schema = $schema;
    }

    /**
     * Deprecated
     * <p>
     * Whether this API version is deprecated or not. Configure your software to notify you if this field is ever true so you aren't caught off-guard if this version is deprecated then eventually killed.
     * (Required)
     * 
     */
    @JsonProperty("deprecated")
    public Boolean getDeprecated() {
        return deprecated;
    }

    /**
     * Deprecated
     * <p>
     * Whether this API version is deprecated or not. Configure your software to notify you if this field is ever true so you aren't caught off-guard if this version is deprecated then eventually killed.
     * (Required)
     * 
     */
    @JsonProperty("deprecated")
    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    /**
     * Sets
     * <p>
     * A superset of the sets in Standard, ordered from least recent release date (`enterDate.exact`) to newest. To get a list of Standard sets you MUST filter this array by comparing your local time to each set's enter and exit date, as the array by itself will contain sets that have already exited Standard as well as sets that have not yet entered it.
     * (Required)
     * 
     */
    @JsonProperty("sets")
    public List<MtGSet> getSets() {
        return mtgSets;
    }

    /**
     * Sets
     * <p>
     * A superset of the sets in Standard, ordered from least recent release date (`enterDate.exact`) to newest. To get a list of Standard sets you MUST filter this array by comparing your local time to each set's enter and exit date, as the array by itself will contain sets that have already exited Standard as well as sets that have not yet entered it.
     * (Required)
     * 
     */
    @JsonProperty("sets")
    public void setSets(List<MtGSet> mtgSets) {
        this.mtgSets = List.copyOf(mtgSets);
    }

    /**
     * Bans
     * <p>
     * Cards that have been banned from Standard, ordered from oldest ban to newest. This array may contain bans from a previous rotation; you should filter them out based on the provided set `setCode` and that set's exit date from Standard.
     * (Required)
     * 
     */
    @JsonProperty("bans")
    public List<Ban> getBans() {
        return bans;
    }

    /**
     * Bans
     * <p>
     * Cards that have been banned from Standard, ordered from oldest ban to newest. This array may contain bans from a previous rotation; you should filter them out based on the provided set `setCode` and that set's exit date from Standard.
     * (Required)
     * 
     */
    @JsonProperty("bans")
    public void setBans(List<Ban> bans) {
        this.bans = bans;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
