package com.terransky.stuffnthings.dataSources.openWeather;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.utils.TimeFormat;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "dt",
    "sunrise",
    "sunset",
    "temp",
    "feels_like",
    "pressure",
    "humidity",
    "dew_point",
    "uvi",
    "clouds",
    "visibility",
    "wind_speed",
    "wind_deg",
    "wind_gust",
    "rain",
    "snow",
    "weather"
})
@Generated("jsonschema2pojo")
public class Current {

    @JsonIgnore
    public final DecimalFormat FORMAT = new DecimalFormat("##.##");
    @JsonProperty("dt")
    private Long dt;
    @JsonProperty("sunrise")
    private Long sunrise;
    @JsonProperty("sunset")
    private Long sunset;
    @JsonProperty("temp")
    private Float temp;
    @JsonProperty("feels_like")
    private Float feelsLike;
    @JsonProperty("pressure")
    private Long pressure;
    @JsonProperty("humidity")
    private Long humidity;
    @JsonProperty("dew_point")
    private Float dewPoint;
    @JsonProperty("uvi")
    private Float uvi;
    @JsonProperty("clouds")
    private Long clouds;
    @JsonProperty("visibility")
    private Long visibility;
    @JsonProperty("wind_speed")
    private Float windSpeed;
    @JsonProperty("wind_deg")
    private Double windDeg;
    @JsonProperty("wind_gust")
    private Float windGust;
    @JsonProperty("rain")
    private Precipitation rain;
    @JsonProperty("snow")
    private Precipitation snow;
    @JsonProperty("weather")
    @Valid
    private List<Weather> weather = new ArrayList<>();

    @JsonInclude
    private Float kelvinToCelsius(float kelkin) {
        return (float) (kelkin - 273.15);
    }

    @JsonInclude
    private Float kelvinToFerenheit(float kelvin) {
        return (float) (kelvinToCelsius(kelvin) * 1.8 + 32);
    }

    @JsonInclude
    public String getPrettyWindString(float windValue) {
        double asMPH = windValue * 2.2369;
        return String.format("**%s**m/s | **%s**mph", windValue, FORMAT.format(asMPH));
    }

    @JsonIgnore
    public String getPrettyTempretures(Float tempreture) {
        int celsiusInt = Math.round(kelvinToCelsius(tempreture));
        int ferenheitInt = Math.round(kelvinToFerenheit(tempreture));

        return String.format("**%s**K | **%s**°C | **%s**°F", FORMAT.format(tempreture), celsiusInt, ferenheitInt);
    }

    @JsonProperty("dt")
    public Long getDt() {
        return dt;
    }

    @JsonProperty("dt")
    public void setDt(Long dt) {
        this.dt = dt;
    }

    @JsonIgnore
    public String getDtAsTimeStamp() {
        return Timestamp.format(dt);
    }

    @JsonIgnore
    public String getDtAsTimeStamp(TimeFormat timestamp) {
        return Timestamp.format(dt, timestamp);
    }

    @JsonProperty("sunrise")
    public Long getSunrise() {
        return sunrise;
    }

    @JsonProperty("sunrise")
    public void setSunrise(Long sunrise) {
        this.sunrise = sunrise;
    }

    @JsonIgnore
    public String getSunriseAsTimeStamp() {
        if (sunrise == null)
            return "N/A";
        return Timestamp.format(sunrise);
    }

    @JsonIgnore
    public String getSunriseAsTimeStamp(TimeFormat timestamp) {
        if (sunrise == null)
            return "N/A";
        return Timestamp.format(sunrise, timestamp);
    }

    @JsonProperty("sunset")
    public Long getSunset() {
        return sunset;
    }

    @JsonProperty("sunset")
    public void setSunset(Long sunset) {
        this.sunset = sunset;
    }

    @JsonIgnore
    public String getSunsetAsTimestamp() {
        if (sunset == null)
            return "N/A";
        return Timestamp.format(sunset);
    }

    @JsonIgnore
    public String getSunsetAsTimestamp(TimeFormat timestamp) {
        if (sunrise == null)
            return "N/A";
        return Timestamp.format(sunset, timestamp);
    }

    @JsonProperty("temp")
    public Float getTemp() {
        return temp;
    }

    @JsonProperty("temp")
    public void setTemp(Float temp) {
        this.temp = temp;
    }

    @JsonIgnore
    public String getTempsAsString() {
        return getPrettyTempretures(temp);
    }

    @JsonProperty("feels_like")
    public Float getFeelsLike() {
        return feelsLike;
    }

    @JsonProperty("feels_like")
    public void setFeelsLike(Float feelsLike) {
        this.feelsLike = feelsLike;
    }

    @JsonIgnore
    public String getFeelsLikesAsString() {
        return getPrettyTempretures(feelsLike);
    }

    @JsonProperty("pressure")
    public Long getPressure() {
        return pressure;
    }

    @JsonProperty("pressure")
    public void setPressure(Long pressure) {
        this.pressure = pressure;
    }

    @JsonProperty("humidity")
    public Long getHumidity() {
        return humidity;
    }

    @JsonProperty("humidity")
    public void setHumidity(Long humidity) {
        this.humidity = humidity;
    }

    @JsonProperty("dew_point")
    public Float getDewPoint() {
        return dewPoint;
    }

    @JsonProperty("dew_point")
    public void setDewPoint(Float dewPoint) {
        this.dewPoint = dewPoint;
    }

    @JsonIgnore
    public String getDewPiontsAsString() {
        return getPrettyTempretures(dewPoint);
    }

    @JsonProperty("uvi")
    public Float getUvi() {
        return uvi;
    }

    @JsonProperty("uvi")
    public void setUvi(Float uvi) {
        this.uvi = uvi;
    }

    @JsonProperty("clouds")
    public Long getClouds() {
        return clouds;
    }

    @JsonProperty("clouds")
    public void setClouds(Long clouds) {
        this.clouds = clouds;
    }

    @JsonProperty("visibility")
    public Long getVisibility() {
        return visibility;
    }

    @JsonProperty("visibility")
    public void setVisibility(Long visibility) {
        this.visibility = visibility;
    }

    @JsonIgnore
    public String getVisibilityAsString() {
        float asMiles = (float) (visibility / 1_609.344);
        if (visibility > 1_000) {
            float asKM = visibility / 1_000;
            return String.format("**%s**km | ~**%s**mi", FORMAT.format(asKM), FORMAT.format(asMiles));
        }
        return String.format("**%s**m | ~**%s**mi", visibility, FORMAT.format(asMiles));
    }

    @JsonProperty("wind_speed")
    public Float getWindSpeed() {
        return windSpeed;
    }

    @JsonProperty("wind_speed")
    public void setWindSpeed(Float windSpeed) {
        this.windSpeed = windSpeed;
    }

    @JsonIgnore
    public String getWindSpeedAsString() {
        return getPrettyWindString(windSpeed);
    }

    @JsonProperty("wind_deg")
    public Double getWindDeg() {
        return windDeg;
    }

    @JsonProperty("wind_deg")
    public void setWindDeg(Double windDeg) {
        this.windDeg = windDeg;
    }

    @JsonProperty("wind_gust")
    public Float getWindGust() {
        return windGust;
    }

    @JsonProperty("wind_gust")
    public void setWindGust(Float windGust) {
        this.windGust = windGust;
    }

    @JsonIgnore
    public String getWindGustAsString() {
        return getPrettyWindString(windGust);
    }

    @JsonProperty("rain")
    public Precipitation getRain() {
        return rain;
    }

    @JsonProperty("rain")
    public void setRain(Precipitation rain) {
        this.rain = rain;
    }

    @JsonProperty("snow")
    public Precipitation getSnow() {
        return snow;
    }

    @JsonProperty("snow")
    public void setSnow(Precipitation snow) {
        this.snow = snow;
    }

    @JsonProperty("weather")
    public List<Weather> getWeather() {
        return weather;
    }

    @JsonProperty("weather")
    public void setWeather(List<Weather> weather) {
        this.weather = List.copyOf(weather);
    }
}
