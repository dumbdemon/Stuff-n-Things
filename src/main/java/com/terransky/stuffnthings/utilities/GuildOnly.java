package com.terransky.stuffnthings.utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class GuildOnly {

    private static final EmbedBuilder BOT_IS_GUILD_ONLY = new EmbedBuilder()
        .setTitle("This bot is a server only bot.")
        .setDescription(CannedResponses.GUILD_ONLY.getMessage())
        .setColor(EmbedColors.getError());

    private GuildOnly() {
    }

    public static void interactionResponse(@NotNull GenericCommandInteractionEvent event) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                .build()
        ).queue();
    }

    public static void interactionResponse(@NotNull ModalInteractionEvent event) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                .build()
        ).queue();
    }

    public static void interactionResponse(@NotNull EntitySelectInteractionEvent event) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                .build()
        ).queue();
    }

    public static void interactionResponse(@NotNull ButtonInteractionEvent event) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl()
            ).build()
        ).queue();
    }
}
