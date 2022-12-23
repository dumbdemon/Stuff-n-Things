package com.terransky.stuffnthings.utilities.cannedAgenda;

import com.terransky.stuffnthings.utilities.command.EmbedColors;
import com.terransky.stuffnthings.utilities.general.Interactions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.jetbrains.annotations.NotNull;

public class GuildOnly {

    private static final EmbedBuilder BOT_IS_GUILD_ONLY = new EmbedBuilder()
        .setTitle("This bot is a server only bot.")
        .setColor(EmbedColors.getError());

    private GuildOnly() {
    }

    public static void interactionResponse(@NotNull GenericCommandInteractionEvent event, Interactions interactions) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                .setDescription(Responses.GUILD_ONLY.getMessage(interactions))
                .build()
        ).queue();
    }

    public static void interactionResponse(@NotNull ModalInteractionEvent event, Interactions interactions) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                .setDescription(Responses.GUILD_ONLY.getMessage(interactions))
                .build()
        ).queue();
    }

    public static <T extends GenericSelectMenuInteractionEvent<?, ? extends SelectMenu>> void interactionResponse(@NotNull T event, Interactions interactions) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                .setDescription(Responses.GUILD_ONLY.getMessage(interactions))
                .build()
        ).queue();
    }

    public static void interactionResponse(@NotNull ButtonInteractionEvent event, Interactions interactions) {
        event.replyEmbeds(
            BOT_IS_GUILD_ONLY.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                .setDescription(Responses.GUILD_ONLY.getMessage(interactions))
                .build()
        ).queue();
    }
}
