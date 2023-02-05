package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.time.FastDateFormat;

import javax.annotation.Generated;
import java.text.ParseException;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("jsonschema2pojo")
public class Attributes {

    @JsonIgnore
    public static final Date NULL_DATE = new Date(0);
    @JsonIgnore
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
    @JsonProperty("createdAt")
    private String createdAt;
    @JsonProperty("updatedAt")
    private String updatedAt;

    @JsonIgnore
    public static Date formatDate(String date) {
        if (date == null)
            return NULL_DATE;
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException ignored) {
            return NULL_DATE;
        }
    }

    @JsonIgnore
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    @JsonProperty("createdAt")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("createdAt")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonIgnore
    public Date getCreatedAtAsDate() {
        return formatDate(createdAt);
    }

    @JsonProperty("updatedAt")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updatedAt")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonIgnore
    public Date getUpdatedAtAsDate() {
        return formatDate(updatedAt);
    }
}
