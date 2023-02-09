package com.terransky.stuffnthings.dataSources.whatsInStandard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Generated;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

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
    public OffsetDateTime getExactAsDate() {
        if (exact == null)
            return null;
        return OffsetDateTime.parse(exact, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
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
