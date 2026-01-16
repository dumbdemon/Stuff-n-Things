package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.general.InteractionType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

public abstract class SlashCommandInteraction extends CommandInteraction<SlashCommandInteractionEvent> {

    private final String description;
    private final List<SubcommandGroupData> subcommandGroups = new ArrayList<>();
    private final List<SubcommandData> subcommands = new ArrayList<>();
    private final List<OptionData> options = new ArrayList<>();
    private final Mastermind mastermind;
    private final CommandCategory category;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
    private boolean isNSFW = false;

    protected SlashCommandInteraction(String name, String description, Mastermind mastermind, CommandCategory category,
                                      OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        super(name);
        this.description = description;
        this.mastermind = mastermind;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Get an {@link OffsetDateTime} from a string
     *
     * @param date     Date string
     * @param timeZone The current time zone
     * @return An {@link OffsetDateTime}
     */
    @NotNull
    public static OffsetDateTime parseDate(@NotNull String date, TimeZone timeZone) {
        if (timeZone == null) return parseDate(date, TimeZone.getDefault());
        ZoneId here = timeZone.toZoneId();
        ZonedDateTime hereAndNow = Instant.now().atZone(here);
        return OffsetDateTime.parse(date.replace("Z", String.format("%tz", hereAndNow)),
            new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendLiteral('T')
                .append(DateTimeFormatter.ISO_LOCAL_TIME)
                .optionalStart()
                .parseLenient()
                .appendOffset("+HHMMss", "Z")
                .parseStrict()
                .toFormatter()
        );
    }

    /**
     * Create an {@link OffsetDateTime} based on provided dighit of tha day.
     * This will assume the time zone the server is currently in.
     *
     * @param year  The year
     * @param month The month
     * @param day   The day
     * @param hour  Hour of the day.
     * @param min   Minute  of the hour.
     * @param time  A {@link TimeZone} to add the appropriate offset.
     * @return An {@link OffsetDateTime}
     */
    @NotNull
    public static OffsetDateTime parseDate(int year, int month, int day, int hour, int min, Time time) {
        return parseDate(year, month, day, hour, min, time, TimeZone.getDefault());
    }

    /**
     * Create an {@link OffsetDateTime} based on provided digit of tha day.
     * This will assume the time zone the server is currently in.
     *
     * @param year     The year
     * @param month    The month
     * @param day      The day
     * @param hour     Hour of the day.
     * @param min      Minute of the hour.
     * @param time     Whether the hour is {@link Time#AM AM} or {@link Time#PM PM}
     * @param timeZone A {@link TimeZone} to add the appropriate offset.
     * @return An {@link OffsetDateTime}
     */
    @NotNull
    public static OffsetDateTime parseDate(int year, int month, int day, int hour, int min, Time time, TimeZone timeZone) {
        if (time == Time.PM) {
            hour += 12;
        }
        return parseDate(year, month, day, hour, min, timeZone);
    }

    /**
     * Create an {@link OffsetDateTime} based on provided dighit of tha day.
     * This will assume the time zone the server is currently in.
     *
     * @param year  The year
     * @param month The month
     * @param day   The day
     * @param hour  Hour of a 24 day.
     * @param min   Minute  of the hour.
     * @return An {@link OffsetDateTime}
     */
    @NotNull
    public static OffsetDateTime parseDate(int year, int month, int day, int hour, int min) {
        return parseDate(year, month, day, hour, min, TimeZone.getDefault());
    }

    /**
     * Create an {@link OffsetDateTime} based on provided dighit of tha day.
     *
     * @param year     The year
     * @param month    The month
     * @param day      The day
     * @param hour     Hour of a 24 day.
     * @param min      Minute  of the hour.
     * @param timeZone A {@link TimeZone} to add the appropriate offset.
     * @return An {@link OffsetDateTime}
     */
    @NotNull
    public static OffsetDateTime parseDate(int year, int month, int day, int hour, int min, TimeZone timeZone) {
        return parseDate(String.format("%d-%02d-%02dT%02d:%02dZ", year,
            Math.min(month, Month.values().length),
            Math.min(day, Month.of(month).maxLength()),
            Math.min(hour, 24),
            Math.min(min, 59)
        ), timeZone);
    }

    /**
     * Get an {@link OffsetDateTime} from a string
     *
     * @param date Date string
     * @return An {@link OffsetDateTime}
     */
    @NotNull
    public static OffsetDateTime parseDate(@NotNull String date) {
        return parseDate(date, TimeZone.getDefault());
    }

    @NotNull
    @Contract(" -> new")
    public static OffsetDateTime now() {
        return OffsetDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    protected void addSubcommandGroups(SubcommandGroupData... subcommandGroup) {
        subcommandGroups.addAll(Arrays.asList(subcommandGroup));
    }

    protected void addSubcommands(SubcommandData... subcommand) {
        subcommands.addAll(Arrays.asList(subcommand));
    }

    protected void addOptions(OptionData... option) {
        options.addAll(Arrays.asList(option));
    }

    public boolean isNSFW() {
        return isNSFW;
    }

    protected void setNSFW() {
        isNSFW = true;
    }

    public Mastermind getMastermind() {
        return mastermind;
    }

    public CommandCategory getCategory() {
        return category;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public SlashCommandData getCommandData() {
        SlashCommandData commandData = Commands.slash(getName(), getDescription())
            .setIntegrationTypes(IntegrationType.GUILD_INSTALL)
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(getDefaultMemberPermissions()))
            .setNSFW(isNSFW());

        if (!options.isEmpty()) {
            return commandData.addOptions(options);
        }

        if (!subcommands.isEmpty()) {
            return commandData.addSubcommands(subcommands);
        }

        if (!subcommandGroups.isEmpty()) {
            return commandData.addSubcommandGroups(subcommandGroups);
        }

        return commandData;
    }

    @Override
    public InteractionType getInteractionType() {
        return InteractionType.COMMAND_SLASH;
    }

    public enum Time {
        AM,
        PM
    }
}
