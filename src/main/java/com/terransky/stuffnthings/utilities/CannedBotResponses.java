package com.terransky.stuffnthings.utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class CannedBotResponses {

    private static final EmbedBuilder BOT_IS_GUILD_ONLY = new EmbedBuilder()
        .setTitle("This bot is a server only bot.")
        .setDescription("Please execute this interaction in a server.")
        .setColor(EmbedColors.getError());

    private CannedBotResponses() {
    }

    public static void botIsGuildOnly(@NotNull GenericCommandInteractionEvent event) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                .build()
        ).queue();
    }

    public static void botIsGuildOnly(@NotNull ModalInteractionEvent event) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                .build()
        ).queue();
    }

    public static void botIsGuildOnly(@NotNull EntitySelectInteractionEvent event) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                .build()
        ).queue();
    }

    public static void botIsGuildOnly(@NotNull ButtonInteractionEvent event) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl()
            ).build()
        ).queue();
    }
}
