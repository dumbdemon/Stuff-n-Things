
package com.terransky.stuffnthings.dataSources.whatsInStandard;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Symbol
 * <p>
 * A set of URLs to access this set's symbol.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "common",
    "uncommon",
    "rare",
    "mythicRare"
})
@Generated("jsonschema2pojo")
public class Symbol {

    /**
     * Set symbol URL (common)
     * <p>
     * A URL to a set symbol in common coloring.
     * (Required)
     * 
     */
    @JsonProperty("common")
    @JsonPropertyDescription("A URL to a set symbol in common coloring.")
    @NotNull
    private URI common;

    /**
     * Set symbol URL (uncommon)
     * <p>
     * A URL to a set symbol in uncommon coloring.
     * (Required)
     * 
     */
    @JsonProperty("uncommon")
    @JsonPropertyDescription("A URL to a set symbol in uncommon coloring.")
    @NotNull
    private URI uncommon;

    /**
     * Set symbol URL (rare)
     * <p>
     * A URL to a set symbol in rare coloring.
     * (Required)
     * 
     */
    @JsonProperty("rare")
    @JsonPropertyDescription("A URL to a set symbol in rare coloring.")
    @NotNull
    private URI rare;

    /**
     * Set symbol URL (mythic rare)
     * <p>
     * A URL to a set symbol in mythic rare coloring.
     * (Required)
     * 
     */
    @JsonProperty("mythicRare")
    @JsonPropertyDescription("A URL to a set symbol in mythic rare coloring.")
    @NotNull
    private URI mythicRare;
    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * Set symbol URL (common)
     * <p>
     * A URL to a set symbol in common coloring.
     * (Required)
     * 
     */
    @JsonProperty("common")
    public URI getCommon() {
        return common;
    }

    /**
     * Set symbol URL (common)
     * <p>
     * A URL to a set symbol in common coloring.
     * (Required)
     * 
     */
    @JsonProperty("common")
    public void setCommon(URI common) {
        this.common = common;
    }

    /**
     * Set symbol URL (uncommon)
     * <p>
     * A URL to a set symbol in uncommon coloring.
     * (Required)
     * 
     */
    @JsonProperty("uncommon")
    public URI getUncommon() {
        return uncommon;
    }

    /**
     * Set symbol URL (uncommon)
     * <p>
     * A URL to a set symbol in uncommon coloring.
     * (Required)
     * 
     */
    @JsonProperty("uncommon")
    public void setUncommon(URI uncommon) {
        this.uncommon = uncommon;
    }

    /**
     * Set symbol URL (rare)
     * <p>
     * A URL to a set symbol in rare coloring.
     * (Required)
     * 
     */
    @JsonProperty("rare")
    public URI getRare() {
        return rare;
    }

    /**
     * Set symbol URL (rare)
     * <p>
     * A URL to a set symbol in rare coloring.
     * (Required)
     * 
     */
    @JsonProperty("rare")
    public void setRare(URI rare) {
        this.rare = rare;
    }

    /**
     * Set symbol URL (mythic rare)
     * <p>
     * A URL to a set symbol in mythic rare coloring.
     * (Required)
     * 
     */
    @JsonProperty("mythicRare")
    public URI getMythicRare() {
        return mythicRare;
    }

    /**
     * Set symbol URL (mythic rare)
     * <p>
     * A URL to a set symbol in mythic rare coloring.
     * (Required)
     * 
     */
    @JsonProperty("mythicRare")
    public void setMythicRare(URI mythicRare) {
        this.mythicRare = mythicRare;
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
