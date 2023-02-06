package com.terransky.stuffnthings.dataSources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.terransky.stuffnthings.interfaces.Pojo;

import java.util.List;

/**
 * A base {@link Pojo} that's used when the JSON is just an array.
 *
 * @param datum A list of type T
 * @param <T>   Any object type accepted by {@link com.fasterxml.jackson.databind.ObjectMapper ObjectMapper}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DatumPojo<T>(List<T> datum) implements Pojo {

    @Override
    public String getAsJsonString() throws JsonProcessingException {
        return Pojo.getMapperObject()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(datum);
    }
}
