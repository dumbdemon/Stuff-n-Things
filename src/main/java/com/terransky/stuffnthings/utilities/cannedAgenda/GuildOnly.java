package com.terransky.stuffnthings.utilities.cannedAgenda;

import com.terransky.stuffnthings.utilities.command.EmbedColors;
import com.terransky.stuffnthings.utilities.general.InteractionType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;

public class GuildOnly {

    private static final EmbedBuilder BOT_IS_GUILD_ONLY = new EmbedBuilder()
        .setTitle("This bot is a server only bot.")
        .setColor(EmbedColors.getError());

    private GuildOnly() {
    }

    public static <T extends GenericCommandInteractionEvent> void interactionResponse(@NotNull T event, InteractionType interactionType) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                .setDescription(Responses.GUILD_ONLY.getMessage(interactionType))
                .build()
        ).queue();
    }

    public static <T extends GenericComponentInteractionCreateEvent> void interactionResponse(@NotNull T event, InteractionType interactionType) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                .setDescription(Responses.GUILD_ONLY.getMessage(interactionType))
                .build()
        ).queue();
    }

    public static void interactionResponse(@NotNull ModalInteractionEvent event, InteractionType interactionType) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                .setDescription(Responses.GUILD_ONLY.getMessage(interactionType))
                .build()
        ).queue();
    }
}
