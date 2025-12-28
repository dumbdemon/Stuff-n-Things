package com.terransky.stuffnthings.utilities.cannedAgenda;

import com.terransky.stuffnthings.utilities.command.StandardResponse;
import com.terransky.stuffnthings.utilities.general.InteractionType;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

public class GuildOnly {

    private static final String GUILD_ONLY_TITLE = "This bot is a server only bot.";

    private GuildOnly() {
    }

    public static void interactionResponse(@NotNull GenericCommandInteractionEvent event, InteractionType type) {
        event.replyComponents(
            StandardResponse.getResponseContainer(GUILD_ONLY_TITLE, Responses.GUILD_ONLY, type)
        ).queue();
    }

    public static void interactionResponse(@NotNull GenericComponentInteractionCreateEvent event, InteractionType type) {
        event.replyComponents(
            StandardResponse.getResponseContainer(GUILD_ONLY_TITLE, Responses.GUILD_ONLY, type)
        ).queue();
    }

    public static void interactionResponse(@NotNull ModalInteractionEvent event) {
        event.replyComponents(
            StandardResponse.getResponseContainer(GUILD_ONLY_TITLE, Responses.GUILD_ONLY, InteractionType.MODAL)
        ).queue();
    }
}
