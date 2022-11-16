package com.terransky.stuffnthings.interfaces;

import com.terransky.stuffnthings.commandSystem.metadata.Metadata;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public interface ISlashCommand extends ICommand {

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
     * @throws Exception Any exception could get thrown across all ISlashCommands.
     */
    void execute(@NotNull SlashCommandInteractionEvent event) throws Exception;
}
