package com.terransky.stuffnthings.utilities.general;

import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

public class MathicTools {

    public static long getStringAsSeed(@NotNull String str) {
        long number = OffsetDateTime.now().toEpochSecond();

        for (char c : str.toCharArray()) {
            number = number | c;
        }

        return number;
    }
}
