package com.terransky.stuffnthings.managers;

import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.interfaces.interactions.ICommand;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CommandIManager<T extends ICommand> extends IManager<T> {

    private final Logger log = LoggerFactory.getLogger(CommandIManager.class);

    @SafeVarargs
    public CommandIManager(@NotNull T... commands) {
        for (T command : commands) {
            addInteraction(command);
        }
    }

    @Override
    void addInteraction(@NotNull T command) {
        if (command.getInteractionType() == IInteraction.Type.COMMAND_SLASH)
            throw new IllegalArgumentException(String.format("Please use %s for slash commands", SlashIManager.class.getName()));
        noTypeCheckAddInteraction(command);
    }

    /**
     * Get the command data of all commands.
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
     * Get the command data of all commands.
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
     * Get the command data of all commands.
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
        if (effectiveCommands.isEmpty()) return new ArrayList<>();

        return new ArrayList<>() {{
            for (T command : getEffectiveCounts(effectiveCommands, effectiveCommands.get(0).getInteractionType())) {
                try {
                    add(command.getCommandData());
                } catch (ParseException e) {
                    String commandName = command instanceof ICommandSlash slash ?
                        slash.getNameReadable() : command.getName();
                    log.warn("The date formatting in %s is invalid and will not be pushed.".formatted(commandName.toUpperCase()));
                }
            }
        }};
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
