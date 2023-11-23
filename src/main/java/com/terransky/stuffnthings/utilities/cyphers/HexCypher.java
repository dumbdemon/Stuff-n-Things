package com.terransky.stuffnthings.utilities.cyphers;

import com.terransky.stuffnthings.interfaces.Cypher;
import org.jetbrains.annotations.NotNull;

/**
 * Convert a message into its hexadecimal equivalent and vice versa
 */
public class HexCypher implements Cypher {
    @Override
    public String encode(@NotNull String str) {
        char[] chars = str.toCharArray();
        StringBuilder encodedHex = new StringBuilder();
        for (char aChar : chars) {
            encodedHex.append(Integer.toHexString(aChar)).append(" ");
        }
        return encodedHex.toString();
    }

    @Override
    public String decode(@NotNull String str) {
        String[] splitBySpace = str.split(" ");
        StringBuilder ascii = new StringBuilder();
        String error = "Decoding Failed: non-hexadecimal code [%s] in message";

        if (splitBySpace.length == 1) {

            splitBySpace = new String[str.length() / 2];
            int position = 0;
            for (int i = 0; i < str.length(); i += 2) {
                try {
                    splitBySpace[position] = str.substring(i, i + 2);
                    position++;
                } catch (IndexOutOfBoundsException e) {
                    return error.formatted(str.substring(i));
                }
            }
        }

        for (String hex : splitBySpace) {
            if (hex.length() != 2)
                return error.formatted(hex);

            try {
                ascii.append((char) Integer.parseInt(hex, 16));
            } catch (NumberFormatException e) {
                return error.formatted(hex);
            }
        }
        return ascii.toString();
    }
}
