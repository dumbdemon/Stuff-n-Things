package com.terransky.stuffnthings.dataSources.openWeather;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "zip",
    "name",
    "lat",
    "lon",
    "country",
    "state"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("jsonschema2pojo")
public class OpenWeatherGeoData {

    @JsonProperty("zip")
    private String zip;
    @JsonProperty("name")
    private String name;
    @JsonProperty("lat")
    private Float lat;
    @JsonProperty("lon")
    private Float lon;
    @JsonProperty("country")
    private String country;
    @JsonProperty("state")
    private String state;

    @JsonProperty("zip")
    public String getZip() {
        return zip;
    }

    @JsonProperty("zip")
    public void setZip(String zip) {
        this.zip = zip;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("lat")
    public Float getLat() {
        return lat;
    }

    @JsonProperty("lat")
    public void setLat(Float lat) {
        this.lat = lat;
    }

    @JsonProperty("lon")
    public Float getLon() {
        return lon;
    }

    @JsonProperty("lon")
    public void setLon(Float lon) {
        this.lon = lon;
    }

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonIgnore
    public String getNameReadable() {
        return name + ", " + (state != null ? state + ", " : "") + country;
    }
}
