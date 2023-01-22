package com.terransky.stuffnthings.dataSources.kitsu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.time.FastDateFormat;

import javax.annotation.Generated;
import java.text.ParseException;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("jsonschema2pojo")
public class Attributes {

    public static final Date NULL_DATE = new Date(0);
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
    @JsonProperty("createdAt")
    private String createdAt;
    @JsonProperty("updatedAt")
    private String updatedAt;

    public static Date formatDate(String date) {
        if (date == null)
            return NULL_DATE;
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException ignored) {
            return NULL_DATE;
        }
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    @JsonProperty("createdAt")
    public Date getCreatedAt() {
        return formatDate(createdAt);
    }

    @JsonProperty("createdAt")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updatedAt")
    public Date getUpdatedAt() {
        return formatDate(updatedAt);
    }

    @JsonProperty("updatedAt")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
