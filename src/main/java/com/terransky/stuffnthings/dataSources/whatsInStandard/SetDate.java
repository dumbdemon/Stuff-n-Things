
package com.terransky.stuffnthings.dataSources.whatsInStandard;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Date
 * <p>
 * Date wraps both a rough date and an optional exact date.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "exact",
    "rough"
})
@Generated("jsonschema2pojo")
public class SetDate {

    /**
     * Exact date
     * <p>
     * A day-precise date formatted as ISO 8601.
     * (Required)
     * 
     */
    @JsonProperty("exact")
    @JsonPropertyDescription("A day-precise date formatted as ISO 8601.")
    @NotNull
    private Date exact;

    /**
     * Rough date
     * <p>
     * A quarter- or month-precise date.
     * (Required)
     * 
     */
    @JsonProperty("rough")
    @JsonPropertyDescription("A quarter- or month-precise date.")
    @NotNull
    private String rough;
    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @org.jetbrains.annotations.NotNull
    @JsonIgnore
    public static OffsetDateTime toOffsetDateTime(@org.jetbrains.annotations.NotNull Date date) {
        Instant instant = date.toInstant();

        ZoneOffset offset = ZoneOffset.UTC;

        return instant.atOffset(offset);
    }

    /**
     * Exact date
     * <p>
     * A day-precise date formatted as ISO 8601.
     * (Required)
     * 
     */
    @JsonProperty("exact")
    public Date getExact() {
        return exact;
    }

    /**
     * Exact date
     * <p>
     * A day-precise date formatted as ISO 8601.
     * (Required)
     * 
     */
    @JsonProperty("exact")
    public void setExact(Date exact) {
        this.exact = exact;
    }

    /**
     * Rough date
     * <p>
     * A quarter- or month-precise date.
     * (Required)
     * 
     */
    @JsonProperty("rough")
    public String getRough() {
        return rough;
    }

    /**
     * Rough date
     * <p>
     * A quarter- or month-precise date.
     * (Required)
     * 
     */
    @JsonProperty("rough")
    public void setRough(String rough) {
        this.rough = rough;
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
