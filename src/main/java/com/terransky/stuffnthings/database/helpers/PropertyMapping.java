package com.terransky.stuffnthings.database.helpers;

import com.terransky.stuffnthings.dataSources.jokeAPI.Flags;
import com.terransky.stuffnthings.games.Bingo.BingoGame;
import com.terransky.stuffnthings.utilities.command.Formatter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PropertyMapping {

    private PropertyMapping() {
    }

    /**
     * Get a constructed IllegalArgumentException
     *
     * @param type The type
     * @return An IllegalArgumentException
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private static IllegalArgumentException getException(String type) {
        return new IllegalArgumentException("Object is not a " + type);
    }

    /**
     * Get a constructed IllegalArgumentException
     *
     * @param aClass A possibly changing class
     * @return An IllegalArgumentException
     */
    @NotNull
    @Contract("_ -> new")
    private static IllegalArgumentException getException(Class<?> aClass) {
        return getException(Formatter.getNameOfClass(aClass));
    }

    /**
     * Converts an object to a {@link List} of Strings
     *
     * @param property An Object to be converted
     * @return A {@link List} of Strings
     * @throws IllegalArgumentException If the object is not an instance of a {@link List}
     */
    @NotNull
    @Contract("null -> fail")
    public static List<String> getAsListOfString(Object property) {
        if (property instanceof List<?> aList)
            return new ArrayList<>() {{
                for (Object obj : aList) {
                    if (obj instanceof String str) add(str);
                }
            }};
        throw getException("List");
    }

    /**
     * Converts an object to a long
     *
     * @param property An Object to be converted
     * @return A long
     * @throws IllegalArgumentException If the object is not an instance of a long
     */
    public static Long getAsLong(Object property) {
        if (property instanceof Long number)
            return number;
        throw getException("Long");
    }

    /**
     * Converts an object to a string
     *
     * @param property An Object to be converted
     * @return A string
     * @throws IllegalArgumentException If the object is not an instance of a string
     */
    public static String getAsString(Object property) {
        if (property instanceof String string)
            return string;
        throw getException("String");
    }

    /**
     * Converts an object to a {@link Flags} object
     *
     * @param property An Object to be converted
     * @return A {@link Flags} object
     * @throws IllegalArgumentException If the object is not an instance of a {@link Flags} object
     */
    public static Flags getAsFlags(Object property) {
        if (property instanceof Flags flags)
            return flags;
        throw getException(Flags.class);
    }

    /**
     * Converts an object to a {@link BingoGame}
     *
     * @param property An Object to be converted
     * @return A {@link BingoGame}
     * @throws IllegalArgumentException If the object is not an instance of a {@link BingoGame}
     */
    public static BingoGame getAsBingoGame(Object property) {
        if (property instanceof BingoGame player)
            return player;
        throw getException(BingoGame.class);
    }
}
