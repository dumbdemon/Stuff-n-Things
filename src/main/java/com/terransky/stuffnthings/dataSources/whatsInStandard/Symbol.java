package com.terransky.stuffnthings.dataSources.whatsInStandard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "common",
    "uncommon",
    "rare",
    "mythicRare"
})
@Generated("jsonschema2pojo")
public class Symbol {

    @JsonProperty("common")
    private String common;
    @JsonProperty("uncommon")
    private String uncommon;
    @JsonProperty("rare")
    private String rare;
    @JsonProperty("mythicRare")
    private String mythicRare;

    @JsonProperty("common")
    public String getCommon() {
        return common;
    }

    @JsonProperty("common")
    public void setCommon(String common) {
        this.common = common;
    }

    @JsonProperty("uncommon")
    public String getUncommon() {
        return uncommon;
    }

    @JsonProperty("uncommon")
    public void setUncommon(String uncommon) {
        this.uncommon = uncommon;
    }

    @JsonProperty("rare")
    public String getRare() {
        return rare;
    }

    @JsonProperty("rare")
    public void setRare(String rare) {
        this.rare = rare;
    }

    @JsonProperty("mythicRare")
    public String getMythicRare() {
        return mythicRare;
    }

    @JsonProperty("mythicRare")
    public void setMythicRare(String mythicRare) {
        this.mythicRare = mythicRare;
    }

}
