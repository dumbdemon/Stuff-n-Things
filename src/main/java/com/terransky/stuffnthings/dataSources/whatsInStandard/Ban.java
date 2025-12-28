
package com.terransky.stuffnthings.dataSources.whatsInStandard;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Card
 * <p>
 * A card currently banned from Standard.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "cardName",
    "cardImageUrl",
    "setCode",
    "reason",
    "announcementUrl"
})
@Generated("jsonschema2pojo")
public class Ban {

    /**
     * Card name
     * <p>
     * The official English name of the banned card.
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("The official English name of the banned card.")
    private String name;

    /**
     * Card image URL
     * <p>
     * A URL to an image of the banned card.
     * (Required)
     * 
     */
    @JsonProperty("cardImageUrl")
    @JsonPropertyDescription("A URL to an image of the banned card.")
    @NotNull
    private URI cardImageUrl;

    /**
     * Set code
     * <p>
     * The set the banned card was printed into, specified by its `code` property.
     * (Required)
     * 
     */
    @JsonProperty("setCode")
    @JsonPropertyDescription("The set the banned card was printed into, specified by its `code` property.")
    @NotNull
    private String setCode;

    /**
     * Reason
     * <p>
     * Human-readable description for why the card was banned.
     * (Required)
     * 
     */
    @JsonProperty("reason")
    @JsonPropertyDescription("Human-readable description for why the card was banned.")
    @NotNull
    private String reason;

    /**
     * Announcement URL
     * <p>
     * A URL to the page where Wizards of the Coast officially announced the card would be banned.
     * (Required)
     * 
     */
    @JsonProperty("announcementUrl")
    @JsonPropertyDescription("A URL to the page where Wizards of the Coast officially announced the card would be banned.")
    @NotNull
    private URI announcementUrl;
    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * Card name
     * <p>
     * The official English name of the banned card.
     * 
     */
    @JsonProperty("cardName")
    public String getName() {
        return name;
    }

    /**
     * Card name
     * <p>
     * The official English name of the banned card.
     * 
     */
    @JsonProperty("cardName")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Card image URL
     * <p>
     * A URL to an image of the banned card.
     * (Required)
     * 
     */
    @JsonProperty("cardImageUrl")
    public URI getCardImageUrl() {
        return cardImageUrl;
    }

    /**
     * Card image URL
     * <p>
     * A URL to an image of the banned card.
     * (Required)
     * 
     */
    @JsonProperty("cardImageUrl")
    public void setCardImageUrl(URI cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    /**
     * Set code
     * <p>
     * The set the banned card was printed into, specified by its `code` property.
     * (Required)
     * 
     */
    @JsonProperty("setCode")
    public String getSetCode() {
        return setCode;
    }

    /**
     * Set code
     * <p>
     * The set the banned card was printed into, specified by its `code` property.
     * (Required)
     * 
     */
    @JsonProperty("setCode")
    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }

    /**
     * Reason
     * <p>
     * Human-readable description for why the card was banned.
     * (Required)
     * 
     */
    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }

    /**
     * Reason
     * <p>
     * Human-readable description for why the card was banned.
     * (Required)
     * 
     */
    @JsonProperty("reason")
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Announcement URL
     * <p>
     * A URL to the page where Wizards of the Coast officially announced the card would be banned.
     * (Required)
     * 
     */
    @JsonProperty("announcementUrl")
    public URI getAnnouncementUrl() {
        return announcementUrl;
    }

    /**
     * Announcement URL
     * <p>
     * A URL to the page where Wizards of the Coast officially announced the card would be banned.
     * (Required)
     * 
     */
    @JsonProperty("announcementUrl")
    public void setAnnouncementUrl(URI announcementUrl) {
        this.announcementUrl = announcementUrl;
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
