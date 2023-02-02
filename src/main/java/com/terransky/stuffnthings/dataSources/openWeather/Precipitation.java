package com.terransky.stuffnthings.dataSources.openWeather;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "1h"
})
@Generated("jsonschema2pojo")
public class Precipitation {

    @JsonProperty("1h")
    private Long _1h;

    @JsonProperty("1h")
    public Long get_1h() {
        return _1h;
    }

    @JsonProperty("1h")
    public void set_1h(Long _1h) {
        this._1h = _1h;
    }
}
