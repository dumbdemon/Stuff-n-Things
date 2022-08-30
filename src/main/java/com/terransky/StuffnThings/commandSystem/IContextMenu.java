package com.terransky.StuffnThings.commandSystem;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public interface IContextMenu {
    //Menu name can have spaces
    String getMenuName();

    CommandData contextData();

    //Execute the appropriate handler
    default void messageContextExecute(@NotNull MessageContextInteractionEvent event) {
    }

    default void userContextExecute(@NotNull UserContextInteractionEvent event) {
    }
}
