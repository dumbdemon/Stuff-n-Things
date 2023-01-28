package com.terransky.stuffnthings.utilities.command;

import com.terransky.stuffnthings.interactions.commands.slashCommands.general.About;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    private Date createdDate;
    private Date lastUpdated;
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
     * @param createdDate The {@link Date} when the command was first created.
     * @param lastUpdated The {@link Date} when the last time the command was edited.
     */
    public Metadata(String commandName, String description, Mastermind mastermind, CommandCategory category,
                    Date createdDate, Date lastUpdated) {
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
     * @param longDescription  The description of the command used in {@link About
     *                         /about [command]}. It will be truncated if it has more than {@value MessageEmbed#DESCRIPTION_MAX_LENGTH} characters.
     * @param mastermind       The {@link Mastermind}.
     * @param category         The {@link CommandCategory}.
     * @param createdDate      The {@link Date} when the command was first created.
     * @param lastUpdated      The {@link Date} when the last time the command was edited.
     */
    public Metadata(String commandName, String shortDescription, String longDescription, Mastermind mastermind, CommandCategory category,
                    Date createdDate, Date lastUpdated) {
        this.commandName = commandName;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.mastermind = mastermind;
        this.category = category;
        this.createdDate = createdDate;
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

    @NotNull
    public static Metadata getEmptyMetadata() {
        return new Metadata()
            .setCommandName("")
            .setDescripstions("")
            .setCreatedDate(new Date())
            .setLastUpdated(new Date())
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public Metadata setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public String getCreatedAsTimestamp() {
        return Timestamp.getDateAsTimestamp(getCreatedDate());
    }

    public String getCreatedAsTimestamp(@NotNull Timestamp timestamp) {
        return Timestamp.getDateAsTimestamp(getCreatedDate(), timestamp);
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public Metadata setLastUpdated(Date lastUpdated) {
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
        return isNsfw() == metadata.isNsfw() &&
            getDefaultPerms().equals(metadata.getDefaultPerms()) &&
            getSubcommandGroups().equals(metadata.getSubcommandGroups()) &&
            getSubcommands().equals(metadata.getSubcommands()) &&
            getOptions().equals(metadata.getOptions()) &&
            getCommandName().equals(metadata.getCommandName()) &&
            getShortDescription().equals(metadata.getShortDescription()) &&
            getLongDescription().equals(metadata.getLongDescription()) &&
            getMastermind() == metadata.getMastermind() &&
            getCategory() == metadata.getCategory() &&
            getCreatedDate().equals(metadata.getCreatedDate()) &&
            getLastUpdated().equals(metadata.getLastUpdated());
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
        var dateFormat = getFastDateFormat();
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
            ", implementationDate=" + dateFormat.format(createdDate) +
            ", lastUpdated=" + dateFormat.format(lastUpdated) +
            ", isNsfw=" + isNsfw +
            '}';
    }

    @Override
    public int compareTo(@NotNull Metadata metadata) {
        return String.CASE_INSENSITIVE_ORDER.compare(getCommandName(), metadata.getCommandName()) |
            String.CASE_INSENSITIVE_ORDER.compare(getShortDescription(), metadata.getShortDescription()) |
            String.CASE_INSENSITIVE_ORDER.compare(getLongDescription(), metadata.getLongDescription()) |
            createdDate.compareTo(metadata.createdDate);
    }
}
