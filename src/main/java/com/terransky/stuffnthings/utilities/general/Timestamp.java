package com.terransky.stuffnthings.utilities.general;

import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.Date;

@SuppressWarnings("unused")
public enum Timestamp {
    LONG_DATE_W_DoW_SHORT_TIME(0, 'F'),
    LONG_DATE_W_SHORT_TIME(1, 'f'),
    LONG_DATE(2, 'D'),
    SHORT_DATE(3, 'd'),
    LONG_TIME(4, 'T'),
    SHORT_TIME(5, 't'),
    RELATIVE(6, 'R');

    private final int id;
    private final Character code;

    Timestamp(int id, Character code) {
        this.id = id;
        this.code = code;
    }

    /**
     * Get a timestamp Markdown code using an {@link OffsetDateTime}.
     *
     * @param offsetDateTime An {@link OffsetDateTime}.
     * @param timestamp      A {@link Timestamp} for visual.
     * @return A formatted Markdown for a timestamp.
     */
    public static String getDateAsTimestamp(@NotNull OffsetDateTime offsetDateTime, @NotNull Timestamp timestamp) {
        return getDateAsTimestamp(offsetDateTime.toEpochSecond(), timestamp);
    }

    /**
     * Get a timestamp Markdown code for a specific date.
     *
     * @param date      A {@link Date} to parse.
     * @param timestamp A {@link Timestamp} for visual.
     * @return A formatted Markdown for a timestamp.
     */
    public static String getDateAsTimestamp(@NotNull Date date, @NotNull Timestamp timestamp) {
        return getDateAsTimestamp(date.toInstant().getEpochSecond(), timestamp);
    }

    /**
     * Get a timestamp Markdown code using epoch seconds.
     *
     * @param epochSeconds Epoch seconds. NOTE: this <b>must</b> be in seconds.
     * @param timestamp    A {@link Timestamp} for visual.
     * @return A formatted Markdown for a timestamp.
     */
    public static String getDateAsTimestamp(long epochSeconds, @NotNull Timestamp timestamp) {
        return "<t:%d:%s>".formatted(epochSeconds, timestamp.getCode());
    }

    public int getId() {
        return id;
    }

    public Character getCode() {
        return code;
    }
}
