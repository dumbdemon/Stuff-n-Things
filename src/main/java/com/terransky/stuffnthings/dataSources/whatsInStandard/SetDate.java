package com.terransky.stuffnthings.dataSources.whatsInStandard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "exact",
    "rough"
})
@Generated("jsonschema2pojo")
public class SetDate {

    @JsonProperty("exact")
    private Date exact;
    @JsonProperty("rough")
    private String rough;

    @JsonProperty("exact")
    public Date getExact() {
        return exact;
    }

    @JsonProperty("exact")
    public void setExact(Date exact) {
        this.exact = exact;
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
