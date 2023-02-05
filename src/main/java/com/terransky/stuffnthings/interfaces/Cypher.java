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
     * Decode a message.
     *
     * @param str A message to decode
     * @return A decoded message.
     */
    String decode(@NotNull String str);
}
