
package com.terransky.stuffnthings.dataSources.whatsInStandard;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Set
 * <p>
 * A Magic: The Gathering set.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "code",
    "codename",
    "enterDate",
    "exitDate",
    "symbol"
})
@Generated("jsonschema2pojo")
public class MtGSet {

    /**
     * Name
     * <p>
     * The human-friendly name of the set, if known.
     * (Required)
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("The human-friendly name of the set, if known.")
    @NotNull
    private Object name;

    /**
     * Code
     * <p>
     * Wizards of the Coasts's official code for the set.
     * (Required)
     * 
     */
    @JsonProperty("code")
    @JsonPropertyDescription("Wizards of the Coasts's official code for the set.")
    @NotNull
    private String code;

    /**
     * Codename
     * <p>
     * Pre-launch development codename given to the set.
     * (Required)
     * 
     */
    @JsonProperty("codename")
    @JsonPropertyDescription("Pre-launch development codename given to the set.")
    @NotNull
    private Object codename;

    /**
     * Date
     * <p>
     * Date wraps both a rough date and an optional exact date.
     * (Required)
     * 
     */
    @JsonProperty("enterDate")
    @JsonPropertyDescription("Date wraps both a rough date and an optional exact date.")
    @Valid
    @NotNull
    private SetDate enterSetDate;

    /**
     * Date
     * <p>
     * Date wraps both a rough date and an optional exact date.
     * (Required)
     * 
     */
    @JsonProperty("exitDate")
    @JsonPropertyDescription("Date wraps both a rough date and an optional exact date.")
    @Valid
    @NotNull
    private SetDate exitSetDate;

    /**
     * Symbol
     * <p>
     * A set of URLs to access this set's symbol.
     * (Required)
     * 
     */
    @JsonProperty("symbol")
    @JsonPropertyDescription("A set of URLs to access this set's symbol.")
    @Valid
    @NotNull
    private Symbol symbol;
    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * Name
     * <p>
     * The human-friendly name of the set, if known.
     * (Required)
     * 
     */
    @JsonProperty("name")
    public Object getName() {
        return name;
    }

    /**
     * Name
     * <p>
     * The human-friendly name of the set, if known.
     * (Required)
     * 
     */
    @JsonProperty("name")
    public void setName(Object name) {
        this.name = name;
    }

    /**
     * Code
     * <p>
     * Wizards of the Coasts's official code for the set.
     * (Required)
     * 
     */
    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    /**
     * Code
     * <p>
     * Wizards of the Coasts's official code for the set.
     * (Required)
     * 
     */
    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Codename
     * <p>
     * Pre-launch development codename given to the set.
     * (Required)
     * 
     */
    @JsonProperty("codename")
    public Object getCodename() {
        return codename;
    }

    /**
     * Codename
     * <p>
     * Pre-launch development codename given to the set.
     * (Required)
     * 
     */
    @JsonProperty("codename")
    public void setCodename(Object codename) {
        this.codename = codename;
    }

    /**
     * Date
     * <p>
     * Date wraps both a rough date and an optional exact date.
     * (Required)
     * 
     */
    @JsonProperty("enterDate")
    public SetDate getEnterDate() {
        return enterSetDate;
    }

    /**
     * Date
     * <p>
     * Date wraps both a rough date and an optional exact date.
     * (Required)
     * 
     */
    @JsonProperty("enterDate")
    public void setEnterDate(SetDate enterSetDate) {
        this.enterSetDate = enterSetDate;
    }

    /**
     * Date
     * <p>
     * Date wraps both a rough date and an optional exact date.
     * (Required)
     * 
     */
    @JsonProperty("exitDate")
    public SetDate getExitDate() {
        return exitSetDate;
    }

    /**
     * Date
     * <p>
     * Date wraps both a rough date and an optional exact date.
     * (Required)
     * 
     */
    @JsonProperty("exitDate")
    public void setExitDate(SetDate exitSetDate) {
        this.exitSetDate = exitSetDate;
    }

    /**
     * Symbol
     * <p>
     * A set of URLs to access this set's symbol.
     * (Required)
     * 
     */
    @JsonProperty("symbol")
    public Symbol getSymbol() {
        return symbol;
    }

    /**
     * Symbol
     * <p>
     * A set of URLs to access this set's symbol.
     * (Required)
     * 
     */
    @JsonProperty("symbol")
    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
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
