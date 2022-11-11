
package com.terransky.StuffnThings.dataSources.NumbersAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "text",
    "found",
    "number",
    "type",
    "date",
    "year"
})
@Generated("jsonschema2pojo")
public class NumbersAPIData {

    @JsonProperty("text")
    private String text;
    @JsonProperty("found")
    private boolean found;
    @JsonProperty("number")
    private double number;
    @JsonProperty("type")
    private String type;
    @JsonProperty("date")
    private String date;
    @JsonProperty("year")
    private double year;

    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("found")
    public boolean isFound() {
        return found;
    }

    @JsonProperty("found")
    public void setFound(boolean found) {
        this.found = found;
    }

    @JsonProperty("number")
    public double getNumber() {
        return number;
    }

    @JsonProperty("number")
    public void setNumber(double number) {
        this.number = number;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("year")
    public double getYear() {
        return year;
    }

    @JsonProperty("year")
    public void setYear(int year) {
        this.year = year;
    }
}
