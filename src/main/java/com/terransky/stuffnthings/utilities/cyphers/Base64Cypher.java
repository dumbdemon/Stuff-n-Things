package com.terransky.stuffnthings.utilities.cyphers;

import com.terransky.stuffnthings.interfaces.Cypher;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;

/**
 * Cypher that uses Base64
 *
 * @see Base64#getMimeEncoder()
 * @see Base64#getMimeDecoder()
 */
public class Base64Cypher implements Cypher {
    @Override
    public String encode(@NotNull String str) {
        return Base64.getMimeEncoder().encodeToString(str.getBytes());
    }

    @Override
    public String decode(@NotNull String str) {
        return new String(Base64.getMimeDecoder().decode(str));
    }
}
