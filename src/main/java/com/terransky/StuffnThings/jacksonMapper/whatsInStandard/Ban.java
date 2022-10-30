package com.terransky.StuffnThings.jacksonMapper.whatsInStandard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

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

    @JsonProperty("cardName")
    private String cardName;
    @JsonProperty("cardImageUrl")
    private String cardImageUrl;
    @JsonProperty("setCode")
    private String setCode;
    @JsonProperty("reason")
    private String reason;
    @JsonProperty("announcementUrl")
    private String announcementUrl;

    @JsonProperty("cardName")
    public String getCardName() {
        return cardName;
    }

    @JsonProperty("cardName")
    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    @JsonProperty("cardImageUrl")
    public String getCardImageUrl() {
        return cardImageUrl;
    }

    @JsonProperty("cardImageUrl")
    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    @JsonProperty("setCode")
    public String getSetCode() {
        return setCode;
    }

    @JsonProperty("setCode")
    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }

    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }

    @JsonProperty("reason")
    public void setReason(String reason) {
        this.reason = reason;
    }

    @JsonProperty("announcementUrl")
    public String getAnnouncementUrl() {
        return announcementUrl;
    }

    @JsonProperty("announcementUrl")
    public void setAnnouncementUrl(String announcementUrl) {
        this.announcementUrl = announcementUrl;
    }

}
