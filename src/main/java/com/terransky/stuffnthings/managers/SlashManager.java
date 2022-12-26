package com.terransky.stuffnthings.managers;

import com.terransky.stuffnthings.ManagersManager;
import com.terransky.stuffnthings.interfaces.discordInteractions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.Metadata;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class SlashManager extends CommandManager<ICommandSlash> {
    private final List<ICommandSlash> iCommandSlashes = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(SlashManager.class);

    public SlashManager(@NotNull ICommandSlash... iCommandSlashes) {
        for (ICommandSlash iCommandSlash : iCommandSlashes) {
            addInteraction(iCommandSlash);
        }
    }

    /**
     * Add a {@link ICommandSlash} object to be indexed and used.
     *
     * @param iCommandSlash An {@link ICommandSlash} object.
     * @throws IndexOutOfBoundsException If {@link SlashManager#iCommandSlashes} has more than the {@link Commands#MAX_SLASH_COMMANDS}.
     */
    @Override
    public void addInteraction(ICommandSlash iCommandSlash) {
        boolean nameFound = iCommandSlashes.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iCommandSlash.getName()));

        if (nameFound) throw new IllegalArgumentException("A command with this name already exists");

        if (iCommandSlashes.size() + 1 > Commands.MAX_SLASH_COMMANDS)
            throw new IllegalArgumentException("You can only have at most %d slash commands.".formatted(Commands.MAX_SLASH_COMMANDS));
        else iCommandSlashes.add(iCommandSlash);
    }

    /**
     * Get the {@link ICommandSlash} object for execution at
     * {@link com.terransky.stuffnthings.listeners.InteractionListener#onSlashCommandInteraction(SlashCommandInteractionEvent) InteractionListener.onSlashCommandInteraction()}.
     *
     * @param search The name of the command.
     * @return An {@link Optional} of {@link ICommandSlash}.
     */
    @Override
    public Optional<ICommandSlash> getInteraction(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (ICommandSlash slash : iCommandSlashes) {
            if (slash.getName().equals(toSearch)) {
                return Optional.of(slash);
            }
        }

        return Optional.empty();
    }

    /**
     * Get all command names as {@link Command.Choice Choises} for the {@link com.terransky.stuffnthings.interactions.commands.slashCommands.general.about about} command.
     *
     * @return A {@link List} of {@link Command.Choice Choises}.
     */
    public List<Command.Choice> getCommandsAsChoices() {
        List<Command.Choice> choices = new ArrayList<>();
        for (ICommandSlash command : iCommandSlashes.stream().filter(it -> it.isGlobal() && it.isWorking()).sorted().toList()) {
            choices.add(new Command.Choice(command.getNameReadable(), command.getName()));
        }
        return choices;
    }

    /**
     * Get the {@link Metadata} of an {@link ICommandSlash}.
     *
     * @param search The name of the command to look for.
     * @return An {@link Optional} of {@link Metadata}.
     * @throws ParseException If the pattern used in {@link Metadata#getImplementationDate()} or {@link Metadata#getLastUpdated()} in a slash command class
     *                        is given an invalid date string.
     */
    public Optional<Metadata> getMetadata(@NotNull String search) throws ParseException {
        String toSearch = search.toLowerCase();

        for (ICommandSlash slash : iCommandSlashes) {
            if (slash.getName().equals(toSearch)) {
                return Optional.of(slash.getMetadata());
            }
        }
        return Optional.empty();
    }

    /**
     * Get the command data of all slash commands, message contexts, and user contexts.
     * <p>
     * <b>Things to note:</b><br>
     * • If a {@link ParseException} occurs, it will not be pushed.<br>
     * • If there is more than {@link Commands#MAX_SLASH_COMMANDS}, {@link Commands#MAX_MESSAGE_COMMANDS}, and
     * {@link Commands#MAX_USER_COMMANDS}, than it will return a truncated list of each.
     *
     * @return Returns a list of {@link CommandData}.
     */
    @Override
    public List<CommandData> getCommandData() {
        ManagersManager manager = new ManagersManager();
        List<CommandData> commandData = new ArrayList<>();
        final List<CommandData> messageContext = manager.getMessageContextManager().getCommandData();
        final List<CommandData> userContext = manager.getUserContextManager().getCommandData();

        for (ICommandSlash command : iCommandSlashes.stream().filter(it -> it.isGlobal() && it.isWorking()).sorted().toList()) {
            try {
                commandData.add(command.getCommandData());
            } catch (ParseException e) {
                log.warn("The date formatting in %s is invalid and will not be pushed.".formatted(command.getName().toUpperCase()));
            }
        }

        if (commandData.size() > Commands.MAX_SLASH_COMMANDS) {
            int previousAmount = commandData.size();
            commandData = commandData.subList(0, Commands.MAX_SLASH_COMMANDS);
            log.warn("There are too many slash commands (%d commands)! Truncating to %d...".formatted(previousAmount, Commands.MAX_SLASH_COMMANDS));
        }

        if (!messageContext.isEmpty()) {
            if (messageContext.size() > Commands.MAX_MESSAGE_COMMANDS)
                commandData.addAll(messageContext.subList(0, Commands.MAX_MESSAGE_COMMANDS));
            else commandData.addAll(messageContext);
            log.debug("%d message contexts added".formatted(messageContext.size()));
        } else log.debug("No message contexts were added.");

        if (!userContext.isEmpty()) {
            if (userContext.size() > Commands.MAX_USER_COMMANDS)
                commandData.addAll(userContext.subList(0, Commands.MAX_USER_COMMANDS));
            else commandData.addAll(userContext);
            log.debug("%d user contexts added".formatted(userContext.size()));
        } else log.debug("No user contexts were added.");

        return commandData;
    }

    /**
     * Get the command data of all slash commands specifically for a server.
     * <p>
     * <b>Things to note:</b><br>
     * • If a {@link ParseException} occurs, it will not be pushed.<br>
     * • If there is more than {@link Commands#MAX_SLASH_COMMANDS}, than it will return a truncated list.
     *
     * @param serverId The ID of the server to check for.
     * @return Returns a list of {@link CommandData}. Could potentially return an empty list.
     */
    @Override
    public List<CommandData> getCommandData(long serverId) {
        final List<CommandData> commandData = new ArrayList<>();
        List<ICommandSlash> effectiveSlashes = iCommandSlashes.stream().filter(it ->
            !it.isGlobal() &&
                it.isWorking() &&
                (it.getServerRestrictions().contains(serverId) || it.getServerRestrictions().isEmpty())
        ).sorted().toList();

        for (ICommandSlash command : effectiveSlashes) {
            try {
                commandData.add(command.getCommandData());
            } catch (ParseException e) {
                log.warn("The date formatting in %s is invalid and will not be pushed.".formatted(command.getName().toUpperCase()));
            }
        }

        if (commandData.size() > Commands.MAX_SLASH_COMMANDS)
            return commandData.subList(0, Commands.MAX_SLASH_COMMANDS);

        return commandData;
    }

    /**
     * Get the effective amount of global slash commands.
     *
     * @return The amount of slash commands.
     */
    public int getSlashCommandCount() {
        return (int) iCommandSlashes.stream().filter(it -> it.isGlobal() && it.isWorking()).count();
    }

    /**
     * Get the effective amount of guild slash commands for a guild.
     *
     * @param serverId The server id to check for.
     * @return The amount of slash commands.
     */
    public int getSlashCommandCount(long serverId) {
        return (int) iCommandSlashes.stream().filter(it ->
            !it.isGlobal() &&
                it.isWorking() &&
                (it.getServerRestrictions().contains(serverId) || it.getServerRestrictions().isEmpty())
        ).count();
    }

    /**
     * Get the effective amount of guild slash commands for a guild.
     *
     * @param guild The guild to check for.
     * @return The amount of slash commands.
     */
    public int getSlashCommandCount(@NotNull Guild guild) {
        return getSlashCommandCount(guild.getIdLong());
    }
}
