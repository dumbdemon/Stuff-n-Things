package com.terransky.stuffnthings.utilities.general;

import net.dv8tion.jda.api.utils.TimeFormat;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.Date;

public class Timestamp {

    Timestamp() {
    }

    /**
     * Get a timestamp Markdown code using an {@link OffsetDateTime}.
     *
     * @param offsetDateTime An {@link OffsetDateTime}.
     * @param timestamp      A {@link TimeFormat} for visual.
     * @return A formatted Markdown for a timestamp.
     */
    @NotNull
    public static String format(@NotNull OffsetDateTime offsetDateTime, @NotNull TimeFormat timestamp) {
        return format(offsetDateTime.toEpochSecond(), timestamp);
    }

    /**
     * Get a timestamp Markdown code using an {@link OffsetDateTime}.<br/>
     * This will use the enum {@link TimeFormat#DATE_TIME_LONG} for the Markdown.
     *
     * @param offsetDateTime An {@link OffsetDateTime}.
     * @return A formatted Markdown for a timestamp.
     */
    @NotNull
    public static String format(@NotNull OffsetDateTime offsetDateTime) {
        return format(offsetDateTime.toEpochSecond());
    }

    /**
     * Get a timestamp Markdown code for a specific date.
     *
     * @param date      A {@link Date} to parse.
     * @param timestamp A {@link TimeFormat} for visual.
     * @return A formatted Markdown for a timestamp.
     */
    @NotNull
    public static String format(@NotNull Date date, @NotNull TimeFormat timestamp) {
        return format(date.toInstant().getEpochSecond(), timestamp);
    }

    /**
     * Get a timestamp Markdown code for a specific date.<br/>
     * This will use the enum {@link TimeFormat#DATE_TIME_LONG} for the Markdown.
     *
     * @param date A {@link Date} to parse.
     * @return A formatted Markdown for a timestamp.
     */
    @NotNull
    public static String format(@NotNull Date date) {
        return format(date.toInstant().getEpochSecond());
    }

    /**
     * Get a timestamp Markdown code using epoch seconds.
     *
     * @param epochSeconds Epoch seconds. NOTE: this <b>must</b> be in seconds.
     * @param timestamp    A {@link TimeFormat} for visual.
     * @return A formatted Markdown for a timestamp.
     */
    @NotNull
    public static String format(long epochSeconds, @NotNull TimeFormat timestamp) {
        return String.format("<t:%d:%s>", epochSeconds, timestamp.getStyle());
    }

    /**
     * Get a timestamp Markdown code using epoch seconds.<br/>
     * This will use the enum {@link TimeFormat#DATE_TIME_LONG} for the Markdown.
     *
     * @param epochSeconds Epoch seconds. NOTE: this <b>must</b> be in seconds.
     * @return A formatted Markdown for a timestamp.
     */
    @NotNull
    public static String format(long epochSeconds) {
        return format(epochSeconds, TimeFormat.DATE_TIME_LONG);
    }
}
