package com.terransky.stuffnthings.utilities.command;

import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

@SuppressWarnings("SpellCheckingInspection")
public class Metadata implements Comparable<Metadata> {
    private final List<Permission> defaultPerms = new ArrayList<>();
    private final List<SubcommandGroupData> subcommandGroups = new ArrayList<>();
    private final List<SubcommandData> subcommands = new ArrayList<>();
    private final List<OptionData> options = new ArrayList<>();
    private String commandName;
    private String shortDescription;
    private String longDescription;
    private Mastermind mastermind;
    private CommandCategory category;
    private OffsetDateTime createdDate;
    private OffsetDateTime lastUpdated;
    private boolean isNsfw = false;

    /**
     * Extended details for an {@link com.terransky.stuffnthings.interfaces.interactions.ICommandSlash ICommandSlash}.
     * <p>
     * It is recommended that when constructing a Metadata Object for {@link net.dv8tion.jda.api.interactions.commands.build.CommandData}, that you use the top level type used in the {@code ISlashCommand.getCommandData()}.
     * Hierarchy (from highest to lowest) goes as follows: none, {@link SubcommandGroupData}, {@link SubcommandData}, {@link OptionData}.
     */
    public Metadata() {
    }

    /**
     * Extended details for an {@link com.terransky.stuffnthings.interfaces.interactions.ICommandSlash ICommandSlash}.
     * <p>
     * It is recommended that when constructing a Metadata Object for {@link net.dv8tion.jda.api.interactions.commands.build.CommandData}, that you use the top level
     * type used in the {@link com.terransky.stuffnthings.interfaces.interactions.ICommandSlash#getCommandData()}. Hierarchy (from highest to lowest) goes as follows: none,
     * {@link SubcommandGroupData}, {@link SubcommandData}, {@link OptionData}.
     *
     * @param commandName The name of the command. Cannot be no than {@value net.dv8tion.jda.api.interactions.commands.build.CommandData#MAX_NAME_LENGTH} characters.
     * @param description The description of the command. Cannot be no longer than
     *                    {@value MessageEmbed#DESCRIPTION_MAX_LENGTH} characters.
     * @param mastermind  The {@link Mastermind}.
     * @param category    The {@link CommandCategory}.
     * @param createdDate The {@link OffsetDateTime} of when the command was first created.
     * @param lastUpdated The {@link OffsetDateTime} of when the last time the command was edited.
     */
    public Metadata(String commandName, String description, Mastermind mastermind, CommandCategory category,
                    OffsetDateTime createdDate, OffsetDateTime lastUpdated) {
        this(commandName, description, description, mastermind, category, createdDate, lastUpdated);
    }

    /**
     * Extended details for an {@link com.terransky.stuffnthings.interfaces.interactions.ICommandSlash ICommandSlash}.
     * <p>
     * It is recommended that when constructing a Metadata Object for {@link net.dv8tion.jda.api.interactions.commands.build.CommandData}, that you use the top level
     * type used in the {@link com.terransky.stuffnthings.interfaces.interactions.ICommandSlash#getCommandData()}. Hierarchy (from highest to lowest) goes as follows: none,
     * {@link SubcommandGroupData}, {@link SubcommandData}, {@link OptionData}.
     *
     * @param commandName      The name of the command. Cannot be no than {@value net.dv8tion.jda.api.interactions.commands.build.CommandData#MAX_NAME_LENGTH} characters.
     * @param shortDescription The description of the command. Cannot be no longer than
     *                         {@value MessageEmbed#DESCRIPTION_MAX_LENGTH} characters.
     * @param longDescription  The description of the command used in {@link com.terransky.stuffnthings.interactions.commands.slashCommands.general.About
     *                         /about [command]}. It will be truncated if it has more than {@value MessageEmbed#DESCRIPTION_MAX_LENGTH} characters.
     * @param mastermind       The {@link Mastermind}.
     * @param category         The {@link CommandCategory}.
     * @param createdDate      The {@link OffsetDateTime} of when the command was first created.
     * @param lastUpdated      The {@link OffsetDateTime} of when the last time the command was edited.
     */
    public Metadata(String commandName, String shortDescription, String longDescription, Mastermind mastermind, CommandCategory category,
                    OffsetDateTime createdDate, OffsetDateTime lastUpdated) {
        this.commandName = commandName;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.mastermind = mastermind;
        this.category = category;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
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

    /**
     * Obtains the current date-time from the system clock in the default time-zone.
     *
     * @return the current date-time using the system clock, not null
     */
    @NotNull
    public static OffsetDateTime now() {
        return OffsetDateTime.now();
    }

    /**
     * A temporary {@link Metadata} object for new commands.
     *
     * @return A constructed {@link Metadata} object
     */
    @NotNull
    @SuppressWarnings("unused")
    public static Metadata getTemporaryMetadata() {
        return new Metadata()
            .setCommandName("temp-test-name")
            .setDescripstions("A casual Test Name")
            .setCreatedDate(OffsetDateTime.now())
            .setLastUpdated(OffsetDateTime.now())
            .setMastermind(Mastermind.DEVELOPER)
            .setCategory(CommandCategory.TEST)
            .setNsfw(false);
    }

    public CommandCategory getCategory() {
        return category;
    }

    public Metadata setCategory(CommandCategory category) {
        this.category = category;
        return this;
    }

    public Metadata setDescripstions(String description) {
        return setShortDescription(description)
            .setLongDescription(description);
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public Metadata setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
        return this;
    }

    public boolean isNsfw() {
        return isNsfw;
    }

    public Metadata setNsfw(boolean nsfw) {
        isNsfw = nsfw;
        return this;
    }

    public List<SubcommandGroupData> getSubcommandGroups() {
        return subcommandGroups;
    }

    public Metadata addSubcommandGroups(List<SubcommandGroupData> subcommandGroups) {
        this.subcommandGroups.addAll(subcommandGroups);
        return this;
    }

    public Metadata addSubcommandGroups(SubcommandGroupData... subcommandGroups) {
        return addSubcommandGroups(List.of(subcommandGroups));
    }

    public List<SubcommandData> getSubcommands() {
        return subcommands;
    }

    public Metadata addSubcommands(List<SubcommandData> subcommands) {
        this.subcommands.addAll(subcommands);
        return this;
    }

    public Metadata addSubcommands(SubcommandData... subcommands) {
        return addSubcommands(List.of(subcommands));
    }

    public List<OptionData> getOptions() {
        return options;
    }

    public Metadata addOptions(List<OptionData> options) {
        this.options.addAll(options);
        return this;
    }

    public Metadata addOptions(OptionData... options) {
        return addOptions(List.of(options));
    }

    public List<Permission> getDefaultPerms() {
        return defaultPerms;
    }

    public Metadata addDefaultPerms(List<Permission> defaultPerms) {
        this.defaultPerms.addAll(defaultPerms);
        return this;
    }

    public Metadata addDefaultPerms(Permission... permissions) {
        return addDefaultPerms(List.of(permissions));
    }

    public String getCommandName() {
        return commandName;
    }

    public Metadata setCommandName(String commandName) {
        this.commandName = commandName;
        return this;
    }

    public String getCommandNameReadable() {
        return WordUtils.capitalize(getCommandName().replaceAll("-", " "));
    }

    public Mastermind getMastermind() {
        return mastermind;
    }

    public Metadata setMastermind(Mastermind mastermind) {
        this.mastermind = mastermind;
        return this;
    }

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public Metadata setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public String getCreatedAsTimestamp() {
        return Timestamp.getDateAsTimestamp(getCreatedDate());
    }

    public String getCreatedAsTimestamp(@NotNull Timestamp timestamp) {
        return Timestamp.getDateAsTimestamp(getCreatedDate(), timestamp);
    }

    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }

    public Metadata setLastUpdated(OffsetDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public String getLastUpdatedAsTimestamp() {
        return Timestamp.getDateAsTimestamp(getLastUpdated());
    }

    public String getLastUpdatedAsTimestamp(@NotNull Timestamp timestamp) {
        return Timestamp.getDateAsTimestamp(getLastUpdated(), timestamp);
    }

    /**
     * Gets the long descriptions of a {@link Metadata} object.
     *
     * @return The long description or it's truncated variant if it has more characters than {@link MessageEmbed#DESCRIPTION_MAX_LENGTH}.
     */
    public String getLongDescription() {
        return getEffectiveDescription(longDescription);
    }

    public Metadata setLongDescription(@NotNull String longDescription) {
        this.longDescription = getEffectiveDescription(longDescription);
        return this;
    }

    @NotNull
    private String getEffectiveDescription(@NotNull String description) {
        int descriptionMaxLength = MessageEmbed.DESCRIPTION_MAX_LENGTH;

        if (description.length() > descriptionMaxLength) {
            return description.substring(0, descriptionMaxLength);
        }

        return description;
    }

    public SlashCommandData getConstructedCommandData() {
        SlashCommandData commandData = Commands.slash(commandName, shortDescription)
            .setNSFW(isNsfw)
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(defaultPerms));

        if (!options.isEmpty())
            return commandData.addOptions(options);

        if (!subcommands.isEmpty())
            return commandData.addSubcommands(subcommands);

        if (!subcommandGroups.isEmpty())
            return commandData.addSubcommandGroups(subcommandGroups);

        return commandData;
    }

    @NotNull
    @Contract(" -> new")
    private int[] getCounts() {
        int subcommandCount = subcommands.size(),
            optionCount = options.size();
        for (SubcommandGroupData subcommandGroup : subcommandGroups) {
            List<SubcommandData> subData = subcommandGroup.getSubcommands();
            subcommandCount += subData.size();
            for (SubcommandData subcommandData : subData) {
                optionCount += subcommandData.getOptions().size();
            }
        }
        return new int[]{
            subcommandGroups.size(),
            subcommandCount,
            optionCount
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metadata metadata = (Metadata) o;
        return getCommandName().equals(metadata.getCommandName()) &&
            getShortDescription().equals(metadata.getShortDescription()) &&
            getLongDescription().equals(metadata.getLongDescription()) &&
            getMastermind() == metadata.getMastermind() &&
            getCategory() == metadata.getCategory() &&
            getCreatedDate().equals(metadata.getCreatedDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDefaultPerms(),
            getSubcommandGroups(),
            getSubcommands(),
            getOptions(),
            getCommandName(),
            getShortDescription(),
            getLongDescription(),
            getMastermind(),
            getCategory(),
            getCreatedDate(),
            getLastUpdated(),
            isNsfw());
    }

    @Override
    public String toString() {
        int[] countz = getCounts();
        return "Metadata{" +
            "defaultPerms=" + Permission.getRaw(defaultPerms) +
            ", subcommandGroups=" + countz[0] +
            ", subcommands=" + countz[1] +
            ", options=" + countz[2] +
            ", commandName='" + commandName + '\'' +
            ", shortDescription='" + shortDescription + '\'' +
            ", longDescription='" + longDescription + '\'' +
            ", mastermind=" + mastermind.toString() +
            ", module=" + category.toString() +
            ", implementationDate=" + createdDate.format(DateTimeFormatter.ISO_LOCAL_DATE) +
            ", lastUpdated=" + lastUpdated.format(DateTimeFormatter.ISO_LOCAL_DATE) +
            ", isNsfw=" + isNsfw +
            '}';
    }

    @Override
    public int compareTo(@NotNull Metadata metadata) {
        return String.CASE_INSENSITIVE_ORDER.compare(getCommandName(), metadata.getCommandName()) |
            String.CASE_INSENSITIVE_ORDER.compare(getShortDescription(), metadata.getShortDescription()) |
            String.CASE_INSENSITIVE_ORDER.compare(getLongDescription(), metadata.getLongDescription()) |
            createdDate.compareTo(metadata.getCreatedDate());
    }

    public enum Time {
        AM,
        PM
    }
}
