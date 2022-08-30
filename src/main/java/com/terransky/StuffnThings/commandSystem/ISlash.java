package com.terransky.StuffnThings.commandSystem;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface ISlash {
    String getName();

    CommandData commandData();

    //Whether the command works and whether it should be pushed to the public version.
    default boolean workingCommand() {
        return true;
    }

    //Whether the command is a global command or guild command
    default boolean isGlobalCommand() {
        return true;
    }

    //If guild command, which guild can it be used in.
    default List<Long> getServerRestrictions() {
        return new ArrayList<>();
    }

    //The commands handler
    void slashExecute(@NotNull SlashCommandInteractionEvent event);
}
