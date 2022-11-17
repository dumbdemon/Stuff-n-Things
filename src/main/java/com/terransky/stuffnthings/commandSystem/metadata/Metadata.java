package com.terransky.stuffnthings.commandSystem.metadata;

import com.terransky.stuffnthings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public class Metadata implements Comparable<Metadata> {
    private String commandName;
    private String shortDescription;
    private String longDescription;
    private Mastermind mastermind;
    private Date implementationDate;
    private Date lastUpdated;
    private List<Permission> minPerms = new ArrayList<>();
    private List<SubcommandGroupData> subcommandGroups = new ArrayList<>();
    private List<SubcommandData> subcommands = new ArrayList<>();
    private List<OptionData> options = new ArrayList<>();

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
     */
    public Metadata(String commandName, String shortDescription, String longDescription, Mastermind mastermind, Date implementationDate, Date lastUpdated) {
        this.commandName = commandName;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.mastermind = mastermind;
        this.implementationDate = implementationDate;
        this.lastUpdated = lastUpdated;
    }

    /**
     * Extended details for an {@link ISlashCommand}.
     * <p>
     * It is recommended that when constructing a Metadata Object for {@link CommandData}, that you use the top level type used in the {@code ISlashCommand.getCommandData()}.
     * Hierarchy (from highest to lowest) goes as follows: none, {@link SubcommandGroupData}, {@link SubcommandData}, {@link OptionData}.
     */
    public Metadata(String commandName, String shortDescription, String longDescription, Mastermind mastermind, Date implementationDate, Date lastUpdated, List<Permission> minPerms) {
        this(commandName, shortDescription, longDescription, mastermind, implementationDate, lastUpdated);
        this.minPerms = minPerms;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public List<SubcommandGroupData> getSubcommandGroups() {
        return subcommandGroups;
    }

    public void setSubcommandGroups(List<SubcommandGroupData> subcommandGroups) {
        this.subcommandGroups = subcommandGroups;
    }

    public void addSubcommandGroups(List<SubcommandGroupData> subcommandGroups) {
        this.subcommandGroups.addAll(subcommandGroups);
    }

    public void addSubcommandGroups(SubcommandGroupData subcommandGroup, SubcommandGroupData... subcommandGroups) {
        this.subcommandGroups.add(subcommandGroup);
        if (subcommandGroups != null) {
            this.subcommandGroups.addAll(List.of(subcommandGroups));
        }
    }

    public List<SubcommandData> getSubcommands() {
        return subcommands;
    }

    public void setSubcommands(List<SubcommandData> subcommands) {
        this.subcommands = subcommands;
    }

    public void addSubcommands(List<SubcommandData> subcommands) {
        this.subcommands.addAll(subcommands);
    }

    public void addSubcommands(SubcommandData subcommand, SubcommandData... subcommands) {
        this.subcommands.add(subcommand);
        if (subcommands != null)
            this.subcommands.addAll(List.of(subcommands));
    }

    public List<OptionData> getOptions() {
        return options;
    }

    public void setOptions(List<OptionData> options) {
        this.options = options;
    }

    public void addOptions(List<OptionData> options) {
        this.options.addAll(options);
    }

    public void addOptions(OptionData option, OptionData... options) {
        this.options.add(option);
        if (options != null)
            this.options.addAll(List.of(options));
    }

    public void setMinPerms(List<Permission> minPerms) {
        this.minPerms = minPerms;
    }

    public void addMinPerms(List<Permission> minPerms) {
        this.minPerms.addAll(minPerms);
    }

    public void addMinPerms(Permission permission, Permission... permissions) {
        minPerms.add(permission);
        if (permissions != null)
            minPerms.addAll(Arrays.asList(permissions));
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
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

    public void setImplementationDate(Date implementationDate) {
        this.implementationDate = implementationDate;
    }

    public long getImplementedAsEpochSecond() {
        return this.getImplementationDate().toInstant().getEpochSecond();
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getLastEditedAsEpochSecond() {
        return this.getLastUpdated().toInstant().getEpochSecond();
    }

    public List<Permission> getMinPerms() {
        return minPerms;
    }

    public String getLongDescription() {
        int descriptionMaxLength = MessageEmbed.DESCRIPTION_MAX_LENGTH;

        if (longDescription.length() > descriptionMaxLength) {
            return longDescription.substring(0, descriptionMaxLength);
        }

        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
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
            getMinPerms().equals(metadata.getMinPerms()) &&
            getSubcommandGroups().equals(metadata.getSubcommandGroups()) &&
            getSubcommands().equals(metadata.getSubcommands()) &&
            getOptions().equals(metadata.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandName(), getShortDescription(), getLongDescription(), getMastermind(), getImplementationDate(), getLastUpdated(), getMinPerms(), getSubcommandGroups(), getSubcommands(), getOptions());
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
            ", minPerms=" + minPerms +
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
