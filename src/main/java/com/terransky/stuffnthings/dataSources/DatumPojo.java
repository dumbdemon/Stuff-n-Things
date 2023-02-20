package com.terransky.stuffnthings.dataSources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.terransky.stuffnthings.interfaces.Pojo;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A base {@link Pojo} that's used when the JSON is just an array.
 *
 * @param datum A list of type T
 * @param <T>   Any object type accepted by {@link com.fasterxml.jackson.databind.ObjectMapper ObjectMapper}.
 */
@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DatumPojo<T>(Collection<T> datum) implements Pojo {

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
    @NotNull
    public Optional<T> first() {
        return datum.stream().findFirst();
    }

    /**
     * Performs the given action for each element of the {@code Iterable}
     * until all elements have been processed or the action throws an
     * exception.  Actions are performed in the order of iteration, if that
     * order is specified.  Exceptions thrown by the action are relayed to the
     * caller.
     *
     * @param action The action to be performed for each element
     * @return The current instance for chaining purposes
     * @throws NullPointerException if the specified action is null
     */
    public DatumPojo<T> forEach(Consumer<? super T> action) {
        datum.forEach(action);
        return this;
    }

    /**
     * Convert this DatumPojo into a different data type
     *
     * @param mapper a function to apply to each element
     * @param <R>    The element type of the new DatumPojo
     * @return The DatumPojo with the new element list
     */
    @NotNull
    public <R> DatumPojo<R> map(Function<? super T, R> mapper) {
        Objects.requireNonNull(mapper);
        return new DatumPojo<>(datum.stream().map(mapper).toList());
    }

    @Override
    public String getAsJsonString() throws JsonProcessingException {
        return Pojo.getMapperObject()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(datum);
    }
}
