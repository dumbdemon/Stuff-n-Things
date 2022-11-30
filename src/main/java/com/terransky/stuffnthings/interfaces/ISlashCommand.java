package com.terransky.stuffnthings.interfaces;

import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.commandSystem.utilities.Metadata;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public interface ISlashCommand extends ICommand {

    /**
     * Builds a {@link CommandData} object based on {@link Metadata}.
     *
     * @return A built {@link CommandData} object.
     * @throws ParseException If the pattern used in {@code Metadata.getImplementationDate()} or {@code Metadata.getLastUpdated()} in a slash command class
     *                        is given an invalid date string.
     */
    @Override
    default CommandData getCommandData() throws ParseException {
        Metadata metadata = this.getMetadata();
        SlashCommandData commandData = Commands.slash(this.getName(), metadata.getShortDescription())
            .setNSFW(metadata.isNsfw())
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(metadata.getDefaultPerms()));

        if (!metadata.getOptions().isEmpty())
            return commandData.addOptions(metadata.getOptions());

        if (!metadata.getSubcommands().isEmpty())
            return commandData.addSubcommands(metadata.getSubcommands());

        if (!metadata.getSubcommandGroups().isEmpty())
            return commandData.addSubcommandGroups(metadata.getSubcommandGroups());

        return commandData;
    }

    /**
     * Get the {@link Metadata} object of an ISlashCommand.
     *
     * @return A {@link Metadata} object.
     * @throws ParseException If the pattern used in {@code Metadata.getImplementationDate()} or {@code Metadata.getLastUpdated()} in a slash command class
     *                        is given an invalid date string.
     */
    Metadata getMetadata() throws ParseException;

    /**
     * If this command can be used in all servers.
     *
     * @return True unless otherwise.
     */
    default boolean isGlobal() {
        return true;
    }

    /**
     * If guild command, which guild(s) can it be used in.
     *
     * @return {@link List} of Guild IDs.
     */
    default List<Long> getServerRestrictions() {
        return new ArrayList<>();
    }

    /**
     * The main slash command handler.
     *
     * @param event A {@link SlashCommandInteractionEvent}.
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws Exception Any exception could get thrown across all ISlashCommands.
     */
    void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception;
}
