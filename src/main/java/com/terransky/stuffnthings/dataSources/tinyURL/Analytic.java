package com.terransky.stuffnthings.dataSources.tinyURL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "enabled",
    "public"
})
@Generated("jsonschema2pojo")
public class Analytic {

    @JsonProperty("enabled")
    private boolean enabled;
    @JsonProperty("public")
    private boolean _public;

    @JsonProperty("enabled")
    public boolean isEnabled() {
        return enabled;
    }

    @JsonProperty("enabled")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @JsonProperty("public")
    public boolean isPublic() {
        return _public;
    }

    @JsonProperty("public")
    public void setPublic(boolean _public) {
        this._public = _public;
    }
}
