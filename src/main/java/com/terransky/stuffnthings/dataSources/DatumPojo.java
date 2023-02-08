package com.terransky.stuffnthings.dataSources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.terransky.stuffnthings.interfaces.Pojo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A base {@link Pojo} that's used when the JSON is just an array.
 *
 * @param datum A list of type T
 * @param <T>   Any object type accepted by {@link com.fasterxml.jackson.databind.ObjectMapper ObjectMapper}.
 */
@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DatumPojo<T>(List<T> datum) implements Pojo {

    /**
     * Get the first element of a filtered stream.
     *
     * @param predicate a non-interfering, stateless predicate to apply to each element to determine if it should be included
     * @return An {@link Optional} containing an object of type {@link T} from a filtered stream.
     */
    @NotNull
    public Optional<T> first(Predicate<? super T> predicate) {
        return datum.stream().filter(predicate).findFirst();
    }

    /**
     * Get the first result of the list.
     *
     * @return An {@link Optional} containing an object of type {@link T}.
     */
    public Optional<T> first() {
        // Both List<> and Optional<> are of type T skipcq: JAVA-W1036
        return Optional.ofNullable(datum.get(0));
    }

    @Override
    public String getAsJsonString() throws JsonProcessingException {
        return Pojo.getMapperObject()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(datum);
    }
}
