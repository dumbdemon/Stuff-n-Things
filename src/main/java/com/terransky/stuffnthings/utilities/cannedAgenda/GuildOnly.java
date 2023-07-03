package com.terransky.stuffnthings.utilities.cannedAgenda;

import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.utilities.command.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

public class GuildOnly {

    private static final EmbedBuilder BOT_IS_GUILD_ONLY = new EmbedBuilder()
        .setTitle("This bot is a server only bot.")
        .setColor(EmbedColor.ERROR.getColor());

    private GuildOnly() {
    }

    public static void interactionResponse(@NotNull GenericCommandInteractionEvent event, IInteraction.Type type) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getName(), event.getUser().getEffectiveAvatarUrl())
                .setDescription(Responses.GUILD_ONLY.getMessage(type))
                .build()
        ).queue();
    }

    public static void interactionResponse(@NotNull GenericComponentInteractionCreateEvent event, IInteraction.Type type) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getName(), event.getUser().getEffectiveAvatarUrl())
                .setDescription(Responses.GUILD_ONLY.getMessage(type))
                .build()
        ).queue();
    }

    public static void interactionResponse(@NotNull ModalInteractionEvent event) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getName(), event.getUser().getEffectiveAvatarUrl())
                .setDescription(Responses.GUILD_ONLY.getMessage(IInteraction.Type.MODAL))
                .build()
        ).queue();
    }
}
