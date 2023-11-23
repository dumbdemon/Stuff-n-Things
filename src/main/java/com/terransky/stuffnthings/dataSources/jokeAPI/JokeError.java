package com.terransky.stuffnthings.dataSources.jokeAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.terransky.stuffnthings.dataSources.CodesAndMessages;

import javax.validation.Valid;
import java.util.List;

public class JokeError extends CodesAndMessages {

    @JsonProperty("causedBy")
    @Valid
    private List<String> causedBy;

    @JsonProperty("causedBy")
    public List<String> getCausedBy() {
        return causedBy;
    }

    @JsonProperty("causedBy")
    @SuppressWarnings("unused")
    public void setCausedBy(List<String> causedBy) {
        this.causedBy = List.copyOf(causedBy);
    }
}
