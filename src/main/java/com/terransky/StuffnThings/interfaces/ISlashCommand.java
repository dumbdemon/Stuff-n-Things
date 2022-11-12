package com.terransky.StuffnThings.interfaces;

import com.terransky.StuffnThings.commandSystem.Metadata.Metadata;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface ISlashCommand extends ICommand {

    Metadata getMetadata();

    //Whether the command is a global command or guild command
    default boolean isGlobal() {
        return true;
    }

    //If guild command, which guild can it be used in.
    default List<Long> getServerRestrictions() {
        return new ArrayList<>();
    }

    //The commands handler
    void execute(@NotNull SlashCommandInteractionEvent event) throws Exception;
}
