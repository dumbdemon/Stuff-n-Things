package com.terransky.stuffnthings.managers;

import com.terransky.stuffnthings.interfaces.interactions.ICommand;
import com.terransky.stuffnthings.utilities.general.InteractionType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CommandManager<T extends ICommand> extends Manager<T> {

    private final Logger log = LoggerFactory.getLogger(CommandManager.class);

    @SafeVarargs
    public CommandManager(@NotNull T... commands) {
        for (T command : commands) {
            addInteraction(command);
        }
    }

    @Override
    public void addInteraction(@NotNull T command) {
        boolean interactionFound = interactions.stream().anyMatch(it -> it.getName().equalsIgnoreCase(command.getName()));

        if (command.getInteractionType() == InteractionType.COMMAND_SLASH)
            throw new IllegalArgumentException(String.format("Please use %s for slash commands", SlashManager.class.getName()));
        if (interactionFound) throw new IllegalArgumentException("A command with that name already exists");

        interactions.add(command);
    }

    /**
     * Get the command data of all slash commands, message contexts, and user contexts.
     * <p>
     * <b>Things to note:</b><br>
     * • If a {@link ParseException} occurs, it will not be pushed.<br>
     *
     * @return A {@link List} of {@link CommandData}
     */
    public List<CommandData> getCommandData() {
        List<T> effectiveCommands = interactions.stream().filter(this::checkIfGlobal).sorted().toList();

        return getCommands(effectiveCommands);
    }

    /**
     * Get the command data of all slash commands, message contexts, and user contexts.
     * <p>
     * <b>Things to note:</b><br>
     * • If a {@link ParseException} occurs, it will not be pushed.<br>
     *
     * @param serverId The ID of the server to check for.
     * @return A {@link List} of {@link CommandData}
     */
    public List<CommandData> getCommandData(long serverId) {
        List<T> effectiveCommands = interactions.stream().filter(it -> checkIfGuild(it, serverId)).sorted().toList();

        return getCommands(effectiveCommands);
    }

    /**
     * Get the command data of all slash commands, message contexts, and user contexts.
     * <p>
     * <b>Things to note:</b><br>
     * • If a {@link ParseException} occurs, it will not be pushed.<br>
     *
     * @param guild A {@link Guild} to check for.
     * @return A {@link List} of {@link CommandData}
     */
    public List<CommandData> getCommandData(@NotNull Guild guild) {
        return getCommandData(guild.getIdLong());
    }

    @NotNull
    private List<CommandData> getCommands(@NotNull List<T> effectiveCommands) {
        if (effectiveCommands.size() > 0) {
            int previousAmount = effectiveCommands.size();
            InteractionType type = effectiveCommands.get(0).getInteractionType();

            switch (type) {
                case COMMAND_CONTEXT_MESSAGE -> effectiveCommands = getEffectiveCommands(effectiveCommands,
                    Commands.MAX_MESSAGE_COMMANDS, type, previousAmount);

                case COMMAND_CONTEXT_USER -> effectiveCommands = getEffectiveCommands(effectiveCommands,
                    Commands.MAX_USER_COMMANDS, type, previousAmount);

                case COMMAND_SLASH -> effectiveCommands = getEffectiveCommands(effectiveCommands,
                    Commands.MAX_SLASH_COMMANDS, type, previousAmount);
            }
        }

        final List<CommandData> commandData = new ArrayList<>();

        for (T command : effectiveCommands) {
            try {
                commandData.add(command.getCommandData());
            } catch (ParseException e) {
                log.warn("The date formatting in %s is invalid and will not be pushed.".formatted(command.getName().toUpperCase()));
            }
        }

        return commandData;
    }

    @NotNull
    private List<T> getEffectiveCommands(@NotNull List<T> effectiveCommands, int max, @NotNull InteractionType type, int previousAmount) {
        log.info("Checking quantity of %ss against maximum of %d...".formatted(type.getName(), max));
        if (effectiveCommands.size() > max) {
            log.warn("There are too many %ss (there's %d)! Truncating to %d..."
                .formatted(type.getName(), previousAmount, max));
            effectiveCommands = effectiveCommands.subList(0, max);
        }
        return effectiveCommands;
    }

    /**
     * Internal check if the command is a working global command.
     *
     * @param interaction An {@link ICommand}.
     * @return True if the {@link ICommand} meets the requirements.
     */
    boolean checkIfGlobal(@NotNull T interaction) {
        return interaction.isGlobal() &&
            interaction.isWorking();
    }

    /**
     * Internal check if the command is a working guild command.
     *
     * @param interaction An {@link ICommand}.
     * @param serverId    The ID of a guild.
     * @return True if the {@link ICommand} meets the requirements.
     */
    boolean checkIfGuild(@NotNull T interaction, long serverId) {
        return !interaction.isGlobal() &&
            interaction.isWorking() &&
            (interaction.getServerRestrictions().contains(serverId) || interaction.getServerRestrictions().isEmpty());
    }
}
