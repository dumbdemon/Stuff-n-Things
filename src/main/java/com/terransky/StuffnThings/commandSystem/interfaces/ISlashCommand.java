package com.terransky.StuffnThings.commandSystem.interfaces;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface ISlashCommand extends IBaseCommand {

    //Whether the command is a global command or guild command
    default boolean isGlobalCommand() {
        return true;
    }

    //If guild command, which guild can it be used in.
    default List<Long> getServerRestrictions() {
        return new ArrayList<>();
    }

    //The commands handler
    void execute(@NotNull SlashCommandInteractionEvent event) throws Exception;
}
