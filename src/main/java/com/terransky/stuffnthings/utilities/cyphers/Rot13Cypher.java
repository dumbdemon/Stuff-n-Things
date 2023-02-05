package com.terransky.stuffnthings.utilities.cyphers;

import com.terransky.stuffnthings.interfaces.Cypher;
import org.jetbrains.annotations.NotNull;

/**
 * <b><a href="https://introcs.cs.princeton.edu/java/31datatype/Rot13.java.html">Source</a></b><br/>
 * Encodes the string using Rot13. Rot13 works by replacing each upper
 * and lower case letters with the letter 13 positions ahead or behind
 * it in the alphabet. The encryption algorithm is symmetric - applying
 * the same algorithm a second time recovers the original message.
 * <p>
 * % java Rot13 Encryption <br/>
 * Rapelcgvba
 * <p>
 * % java Rot13 Rapelcgvba<br/>
 * Encryption
 * <p>
 * % java Rot13 abcABCzyxZYX<br/>
 * nopNOPmlkMLK
 */
public class Rot13Cypher implements Cypher {
    @Override
    public String encode(@NotNull String str) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ((c >= 'a' && c <= 'm') || (c >= 'A' && c <= 'M')) {
                c += 13;
            } else if ((c >= 'n' && c <= 'z') || (c >= 'N' && c <= 'Z')) {
                c -= 13;
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }
}
