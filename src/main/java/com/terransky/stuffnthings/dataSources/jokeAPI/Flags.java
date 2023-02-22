package com.terransky.stuffnthings.dataSources.jokeAPI;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "nsfw",
    "religious",
    "political",
    "racist",
    "sexist",
    "explicit"
})
@Generated("jsonschema2pojo")
public class Flags {

    @JsonProperty("nsfw")
    @BsonProperty("nsfw")
    private Boolean nsfw;
    @JsonProperty("religious")
    @BsonProperty("religious")
    private Boolean religious;
    @JsonProperty("political")
    @BsonProperty("political")
    private Boolean political;
    @JsonProperty("racist")
    @BsonProperty("racist")
    private Boolean racist;
    @JsonProperty("sexist")
    @BsonProperty("sexist")
    private Boolean sexist;
    @JsonProperty("explicit")
    @BsonProperty("explicit")
    private Boolean explicit;
    @JsonIgnore
    @BsonProperty("safe_mode")
    private Boolean safeMode;

    public Flags() {
        this.religious = false;
        this.political = false;
        this.racist = false;
        this.sexist = false;
        this.safeMode = false;
    }

    @JsonProperty("nsfw")
    @BsonIgnore
    public Boolean getNsfw() {
        return nsfw;
    }

    @JsonProperty("nsfw")
    @BsonIgnore
    public void setNsfw(Boolean nsfw) {
        this.nsfw = nsfw;
    }

    public Flags withNsfw(Boolean nsfw) {
        this.nsfw = nsfw;
        return this;
    }

    @JsonProperty("religious")
    @BsonProperty("religious")
    public Boolean getReligious() {
        return religious;
    }

    @JsonProperty("religious")
    @BsonProperty("religious")
    public void setReligious(Boolean religious) {
        this.religious = religious;
    }

    public Flags withReligoius(Boolean religious) {
        this.religious = religious;
        return this;
    }

    @JsonProperty("political")
    @BsonProperty("political")
    public Boolean getPolitical() {
        return political;
    }

    @JsonProperty("political")
    @BsonProperty("political")
    public void setPolitical(Boolean political) {
        this.political = political;
    }

    public Flags withPolitical(Boolean political) {
        this.political = political;
        return this;
    }

    @JsonProperty("racist")
    @BsonProperty("racist")
    public Boolean getRacist() {
        return racist;
    }

    @JsonProperty("racist")
    @BsonProperty("racist")
    public void setRacist(Boolean racist) {
        this.racist = racist;
    }

    public Flags withRacist(Boolean racist) {
        this.racist = racist;
        return this;
    }

    @JsonProperty("sexist")
    @BsonProperty("sexist")
    public Boolean getSexist() {
        return sexist;
    }

    @JsonProperty("sexist")
    @BsonProperty("sexist")
    public void setSexist(Boolean sexist) {
        this.sexist = sexist;
    }

    public Flags withSexist(Boolean sexist) {
        this.sexist = sexist;
        return this;
    }

    @JsonProperty("explicit")
    @BsonIgnore
    public Boolean getExplicit() {
        return explicit;
    }

    @JsonProperty("explicit")
    @BsonIgnore
    public void setExplicit(Boolean explicit) {
        this.explicit = explicit;
    }

    public Flags withExplicit(Boolean explicit) {
        this.explicit = explicit;
        return this;
    }

    @JsonIgnore
    @BsonProperty("safe_mode")
    public Boolean getSafeMode() {
        return safeMode;
    }

    @JsonIgnore
    @BsonProperty("safe_mode")
    public void setSafeMode(Boolean safeMode) {
        this.safeMode = safeMode;
    }
}
