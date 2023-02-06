package com.terransky.stuffnthings.dataSources.openWeather;

import com.fasterxml.jackson.annotation.*;
import com.terransky.stuffnthings.interfaces.Pojo;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import org.apache.commons.lang3.time.FastDateFormat;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "lat",
    "lon",
    "timezone",
    "timezone_offset",
    "current",
    "alerts"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("jsonschema2pojo")
public class OpenWeatherData implements Pojo {

    @JsonProperty("lat")
    private Float latitude;
    @JsonProperty("lon")
    private Float longitude;
    @JsonProperty("timezone")
    private String timezone;
    @JsonProperty("timezone_offset")
    private Long timezoneOffset;
    @JsonProperty("current")
    @Valid
    private Current current;
    @JsonProperty("alerts")
    @Valid
    private List<Alert> alerts = new ArrayList<>();
    @JsonIgnore
    private OpenWeatherGeoData geoData;

    @JsonProperty("lat")
    public Float getLatitude() {
        return latitude;
    }

    @JsonProperty("lat")
    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("lon")
    public Float getLongitude() {
        return longitude;
    }

    @JsonProperty("lon")
    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("timezone")
    public String getTimezone() {
        return timezone;
    }

    @JsonProperty("timezone")
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @JsonProperty("timezone_offset")
    public Long getTimezoneOffset() {
        return timezoneOffset;
    }

    @JsonProperty("timezone_offset")
    public void setTimezoneOffset(Long timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    @JsonIgnore
    public String getDateWithOffset(long time) {
        FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd hh:mm:ss");
        return format.format(new Date(TimeUnit.SECONDS.toMillis(time + timezoneOffset)));
    }

    @JsonIgnore
    public String getDateWithOffset(long time, Timestamp timestamp) {
        return Timestamp.getDateAsTimestamp((time + timezoneOffset), timestamp);
    }

    @JsonProperty("current")
    public Current getCurrent() {
        return current;
    }

    @JsonProperty("current")
    public void setCurrent(Current current) {
        this.current = current;
    }

    @JsonProperty("alerts")
    public List<Alert> getAlerts() {
        return alerts;
    }

    @JsonProperty("alerts")
    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    @JsonIgnore
    public OpenWeatherGeoData getGeoData() {
        return geoData;
    }

    @JsonIgnore
    public OpenWeatherData setGeoData(OpenWeatherGeoData geoData) {
        this.geoData = geoData;
        return this;
    }
}
