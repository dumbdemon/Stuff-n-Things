package com.terransky.stuffnthings.interfaces;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for all Cyphers to ensure consistency
 */
public interface Cypher {

    /**
     * Encode a message.
     *
     * @param str A message to encode
     * @return An encoded message.
     */
    String encode(@NotNull String str);

    /**
     * Decode a message.<br/>
     * Defaults to calling {@link #encode(String)} if the process for decoding is the same.
     *
     * @param str A message to decode
     * @return A decoded message.
     */
    default String decode(@NotNull String str) {
        return encode(str);
    }
}
