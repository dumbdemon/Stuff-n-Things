package com.terransky.stuffnthings.managers;

import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.Metadata;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class SlashIManager extends CommandIManager<ICommandSlash> {

    public SlashIManager(@NotNull ICommandSlash... interactions) {
        for (ICommandSlash iCommandSlash : interactions) {
            noTypeCheckAddInteraction(iCommandSlash);
        }
    }

    /**
     * Get all command names as {@link Command.Choice Choises} for the
     * {@link com.terransky.stuffnthings.interactions.commands.slashCommands.general.About about} command.
     *
     * @return A {@link List} of {@link Command.Choice Choises}.
     */
    public List<Command.Choice> getCommandsAsChoicesSetOne() {
        List<ICommandSlash> slashes = interactions.stream().filter(super::checkIfGlobal).sorted().toList();
        return new ArrayList<>() {{
            int max = Math.min(25, slashes.size());
            for (int i = 0; i < max; i++) {
                add(new Command.Choice(slashes.get(i).getNameReadable(), slashes.get(i).getName()));
            }
        }};
    }

    public List<Command.Choice> getCommandsAsChoicesSetTwo() {
        List<ICommandSlash> slashes = interactions.stream().filter(super::checkIfGlobal).sorted().toList();
        if (slashes.size() < 25) return List.of(new Command.Choice("Dummy Option", "dum"));
        return new ArrayList<>() {{
            for (int i = 25; i < slashes.size(); i++) {
                add(new Command.Choice(slashes.get(i).getNameReadable(), slashes.get(i).getName()));
            }
        }};
    }

    /**
     * Get the {@link Metadata} of an {@link ICommandSlash}.
     *
     * @param search The name of the command to look for.
     * @return An {@link Optional} of {@link Metadata}.
     * @throws ParseException If the pattern used in {@link Metadata#getCreatedDate()} or {@link Metadata#getLastUpdated()} in a slash command class
     *                        is given an invalid date string.
     */
    public Optional<Metadata> getMetadata(@NotNull String search) throws ParseException {
        Optional<ICommandSlash> commandSlash = getInteraction(search);

        if (commandSlash.isPresent())
            return Optional.of(commandSlash.get().getMetadata());
        return Optional.empty();
    }

    /**
     * Get the effective amount of global slash commands.
     *
     * @return The amount of slash commands.
     */
    public int getSlashCommandCount() {
        return (int) interactions.stream().filter(super::checkIfGlobal).count();
    }

    /**
     * Get the effective amount of guild slash commands for a guild.
     *
     * @param serverId The server id to check for.
     * @return The amount of slash commands.
     */
    public int getSlashCommandCount(long serverId) {
        return (int) interactions.stream().filter(iCommandSlash -> checkIfGuild(iCommandSlash, serverId)).count();
    }

}
