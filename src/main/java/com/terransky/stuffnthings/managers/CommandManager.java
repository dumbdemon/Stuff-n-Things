package com.terransky.stuffnthings.managers;

import com.terransky.stuffnthings.interfaces.ICommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CommandManager<T extends ICommand> extends Manager<T> {

    private final Logger log = LoggerFactory.getLogger(CommandManager.class);
    private final List<T> commands = new ArrayList<>();

    @SafeVarargs
    public CommandManager(@NotNull T... commands) {
        for (T command : commands) {
            addInteraction(command);
        }
    }

    @Override
    public void addInteraction(T command) {
        boolean interactionFound = commands.stream().anyMatch(it -> it.getName().equalsIgnoreCase(command.getName()));

        if (interactionFound) throw new IllegalArgumentException("A command with that name already exists");

        commands.add(command);
    }

    public List<CommandData> getCommandData() {
        final List<CommandData> commandData = new ArrayList<>();

        for (T command : commands.stream().filter(ICommand::isWorking).toList()) {
            try {
                commandData.add(command.getCommandData());
            } catch (ParseException e) {
                log.warn("The date formatting in %s is invalid and will not be pushed.".formatted(command.getName().toUpperCase()));
            }
        }

        return commandData;
    }

    public List<CommandData> getCommandData(long serverId) {
        final List<CommandData> commandData = new ArrayList<>();
        List<T> effectiveCommand = commands.stream().filter(ICommand::isWorking).sorted().toList();

        for (T command : effectiveCommand) {
            try {
                commandData.add(command.getCommandData());
            } catch (ParseException e) {
                log.warn("The date formatting in %s is invalid and will not be pushed.".formatted(command.getName().toUpperCase()));
            }
        }

        return commandData;
    }

    public List<CommandData> getCommandData(@NotNull Guild guild) {
        return getCommandData(guild.getIdLong());
    }
}
