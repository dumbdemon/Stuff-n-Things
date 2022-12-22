package com.terransky.stuffnthings.utilities;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class Metadata implements Comparable<Metadata> {
    private final List<Permission> defaultPerms = new ArrayList<>();
    private final List<SubcommandGroupData> subcommandGroups = new ArrayList<>();
    private final List<SubcommandData> subcommands = new ArrayList<>();
    private final List<OptionData> options = new ArrayList<>();
    private String commandName;
    private String shortDescription;
    private String longDescription;
    private Mastermind mastermind;
    private SlashModule module;
    private Date implementationDate;
    private Date lastUpdated;
    private boolean isNsfw = false;

    /**
     * Extended details for an {@link com.terransky.stuffnthings.interfaces.ICommandSlash ICommandSlash}.
     * <p>
     * It is recommended that when constructing a Metadata Object for {@link CommandData}, that you use the top level type used in the {@code ISlashCommand.getCommandData()}.
     * Hierarchy (from highest to lowest) goes as follows: none, {@link SubcommandGroupData}, {@link SubcommandData}, {@link OptionData}.
     */
    public Metadata() {
    }

    /**
     * Extended details for an {@link com.terransky.stuffnthings.interfaces.ICommandSlash ICommandSlash}.
     * <p>
     * It is recommended that when constructing a Metadata Object for {@link CommandData}, that you use the top level type used in the {@code ISlashCommand.getCommandData()}.
     * Hierarchy (from highest to lowest) goes as follows: none, {@link SubcommandGroupData}, {@link SubcommandData}, {@link OptionData}.
     *
     * @param commandName        The name of the command. Cannot be no than {@link CommandData#MAX_NAME_LENGTH}.
     * @param shortDescription   The description of the command. Cannot be no longer than {@link MessageEmbed#DESCRIPTION_MAX_LENGTH}.
     * @param longDescription    The description of the command used in {@link com.terransky.stuffnthings.interactions.commands.slashCommands.general.about /about [command]}.
     *                           It will be truncated if it has more characters than {@link MessageEmbed#DESCRIPTION_MAX_LENGTH}.
     * @param mastermind         The {@link Mastermind}.
     * @param module             The {@link SlashModule}.
     * @param implementationDate The {@link Date} when the command was first created.
     * @param lastUpdated        The {@link Date} when the last time the command was edited.
     */
    public Metadata(String commandName, String shortDescription, String longDescription, Mastermind mastermind,
                    SlashModule module, Date implementationDate, Date lastUpdated) {
        this.commandName = commandName;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.mastermind = mastermind;
        this.module = module;
        this.implementationDate = implementationDate;
        this.lastUpdated = lastUpdated;
    }

    /**
     * The default formatting for all Metadata dates
     *
     * @return A {@link FastDateFormat}
     */
    public static FastDateFormat getFastDateFormat() {
        return FastDateFormat.getInstance("dd-MM-yyyy_HH:mm");
    }

    public SlashModule getModule() {
        return module;
    }

    public Metadata setModule(SlashModule module) {
        this.module = module;
        return this;
    }

    public String getShortDescription() {
        return shortDescription;
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

    public Mastermind getMastermind() {
        return mastermind;
    }

    public Metadata setMastermind(Mastermind mastermind) {
        this.mastermind = mastermind;
        return this;
    }

    public Date getImplementationDate() {
        return implementationDate;
    }

    public String getImplementedAsTimestamp(@NotNull Timestamp timestamp) {
        return Timestamp.getDateAsTimestamp(getImplementationDate(), timestamp);
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public String getLastEditedAsTimestamp(@NotNull Timestamp timestamp) {
        return Timestamp.getDateAsTimestamp(getLastUpdated(), timestamp);
    }

    /**
     * Gets the long descriptions of a {@link Metadata} object.
     *
     * @return The long description or it's truncated variant if it has more characters than {@link MessageEmbed#DESCRIPTION_MAX_LENGTH}.
     */
    public String getLongDescription() {
        int descriptionMaxLength = MessageEmbed.DESCRIPTION_MAX_LENGTH;

        if (longDescription.length() > descriptionMaxLength) {
            return longDescription.substring(0, descriptionMaxLength);
        }

        return longDescription;
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
            getImplementationDate().equals(metadata.getImplementationDate()) &&
            getLastUpdated().equals(metadata.getLastUpdated()) &&
            getDefaultPerms().equals(metadata.getDefaultPerms()) &&
            getSubcommandGroups().equals(metadata.getSubcommandGroups()) &&
            getSubcommands().equals(metadata.getSubcommands()) &&
            getOptions().equals(metadata.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandName(),
            getShortDescription(),
            getLongDescription(),
            getMastermind(),
            getImplementationDate(),
            getLastUpdated(),
            getDefaultPerms(),
            getSubcommandGroups(),
            getSubcommands(),
            getOptions());
    }

    @Override
    public String toString() {
        return "Metadata{" +
            "commandName='" + commandName + '\'' +
            ", shortDescription='" + shortDescription + '\'' +
            ", longDescription='" + longDescription + '\'' +
            ", mastermind=" + mastermind +
            ", implementationDate=" + implementationDate +
            ", lastUpdated=" + lastUpdated +
            ", minPerms=" + defaultPerms +
            ", subcommandGroups=" + subcommandGroups +
            ", subcommands=" + subcommands +
            ", options=" + options +
            '}';
    }

    @Override
    public int compareTo(@NotNull Metadata metadata) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.commandName, metadata.commandName);
    }
}
