package com.terransky.stuffnthings.utilities.cyphers;

import com.terransky.stuffnthings.interfaces.Cypher;
import org.jetbrains.annotations.NotNull;

/**
 * Cypher that reverses the position of letters. First to last; last to first.<br/>
 * Encoding and decoding are the same.
 */
public class ReverseCypher implements Cypher {
    @Override
    public String encode(@NotNull String str) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = (str.length() - 1); i > -1; i--) {
            stringBuilder.append(str.charAt(i));
        }
        return stringBuilder.toString();
    }
}
