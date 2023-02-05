package com.terransky.stuffnthings.dataSources.whatsInStandard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Generated;
import java.text.ParseException;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "exact",
    "rough"
})
@Generated("jsonschema2pojo")
public class SetDate {

    @JsonProperty("exact")
    private String exact;
    @JsonProperty("rough")
    private String rough;

    @JsonProperty("exact")
    public String getExact() {
        return exact;
    }

    @JsonProperty("exact")
    public void setExact(String exact) {
        this.exact = exact;
    }

    @JsonIgnore
    @Nullable
    public Date getExactAsDate() {
        if (exact == null)
            return null;
        FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            return format.parse(exact);
        } catch (ParseException e) {
            return null;
        }
    }

    @JsonProperty("rough")
    public String getRough() {
        return rough;
    }

    @JsonProperty("rough")
    public void setRough(String rough) {
        this.rough = rough;
    }

}
