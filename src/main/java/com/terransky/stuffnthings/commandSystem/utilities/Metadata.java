package com.terransky.stuffnthings.commandSystem.utilities;

import com.terransky.stuffnthings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
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
     * Extended details for an {@link ISlashCommand}.
     * <p>
     * It is recommended that when constructing a Metadata Object for {@link CommandData}, that you use the top level type used in the {@code ISlashCommand.getCommandData()}.
     * Hierarchy (from highest to lowest) goes as follows: none, {@link SubcommandGroupData}, {@link SubcommandData}, {@link OptionData}.
     */
    public Metadata() {
    }

    /**
     * Extended details for an {@link ISlashCommand}.
     * <p>
     * It is recommended that when constructing a Metadata Object for {@link CommandData}, that you use the top level type used in the {@code ISlashCommand.getCommandData()}.
     * Hierarchy (from highest to lowest) goes as follows: none, {@link SubcommandGroupData}, {@link SubcommandData}, {@link OptionData}.
     *
     * @param commandName        The name of the command. Cannot be no than MAX_NAME_LENGTH in {@link CommandData}.
     * @param shortDescription   The description of the command. Cannot be no longer than MAX_DESCRIPTION_LENGTH in {@link CommandData}.
     * @param longDescription    The description of the command used in {@link com.terransky.stuffnthings.commandSystem.commands.general.about /about [command]}.
     *                           It will be truncated if it has more characters than DESCRIPTION_MAX_LENGTH stated in {@link MessageEmbed}.
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

    public SlashModule getModule() {
        return module;
    }

    public void setModule(SlashModule module) {
        this.module = module;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public boolean isNsfw() {
        return isNsfw;
    }

    public void setNsfw(boolean nsfw) {
        isNsfw = nsfw;
    }

    public List<SubcommandGroupData> getSubcommandGroups() {
        return subcommandGroups;
    }

    public void addSubcommandGroups(List<SubcommandGroupData> subcommandGroups) {
        this.subcommandGroups.addAll(subcommandGroups);
    }

    public void addSubcommandGroups(SubcommandGroupData... subcommandGroups) {
        addSubcommandGroups(List.of(subcommandGroups));
    }

    public List<SubcommandData> getSubcommands() {
        return subcommands;
    }

    public void addSubcommands(List<SubcommandData> subcommands) {
        this.subcommands.addAll(subcommands);
    }

    public void addSubcommands(SubcommandData... subcommands) {
        addSubcommands(List.of(subcommands));
    }

    public List<OptionData> getOptions() {
        return options;
    }

    public void addOptions(List<OptionData> options) {
        this.options.addAll(options);
    }

    public void addOptions(OptionData... options) {
        addOptions(List.of(options));
    }

    public List<Permission> getDefaultPerms() {
        return defaultPerms;
    }

    public void addDefaultPerms(List<Permission> defaultPerms) {
        this.defaultPerms.addAll(defaultPerms);
    }

    public void addDefaultPerms(Permission... permissions) {
        addDefaultPerms(List.of(permissions));
    }

    public String getCommandName() {
        return commandName;
    }

    public Mastermind getMastermind() {
        return mastermind;
    }

    public void setMastermind(Mastermind mastermind) {
        this.mastermind = mastermind;
    }

    public Date getImplementationDate() {
        return implementationDate;
    }

    public long getImplementedAsEpochSecond() {
        return this.getImplementationDate().toInstant().getEpochSecond();
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public long getLastEditedAsEpochSecond() {
        return this.getLastUpdated().toInstant().getEpochSecond();
    }

    /**
     * Gets the long descriptions of a {@link Metadata} object.
     *
     * @return The long description or it's truncated variant if it has more characters than DESCRIPTION_MAX_LENGTH stated in {@link MessageEmbed}.
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
