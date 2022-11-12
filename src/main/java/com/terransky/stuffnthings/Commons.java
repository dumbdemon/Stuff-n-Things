package com.terransky.stuffnthings;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Commons {
    public static final Color DEFAULT_EMBED_COLOR = new Color(102, 51, 102);
    public static final Color SECONDARY_EMBED_COLOR = new Color(153, 102, 153);
    public static final Dotenv CONFIG = Dotenv.configure().load();
    public static final boolean IS_TESTING_MODE = CONFIG.get("TESTING_MODE").equals("true");
    public static final boolean ENABLE_DATABASE = CONFIG.get("ENABLE_DATABASE").equals("true");
    private static final EmbedBuilder BOT_IS_GUILD_ONLY_MESSAGE = new EmbedBuilder()
        .setTitle("This bot is a server only bot.")
        .setDescription("Please execute this interaction in a server.")
        .setColor(DEFAULT_EMBED_COLOR);

    @Contract(pure = true)
    private Commons() {
    }

    public static void botIsGuildOnly(@NotNull GenericCommandInteractionEvent event) {
        event.replyEmbeds(BOT_IS_GUILD_ONLY_MESSAGE.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl()).build()).queue();
    }

    public static void botIsGuildOnly(@NotNull ModalInteractionEvent event) {
        event.replyEmbeds(BOT_IS_GUILD_ONLY_MESSAGE.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl()).build()).queue();
    }

    public static void botIsGuildOnly(@NotNull SelectMenuInteractionEvent event) {
        event.replyEmbeds(BOT_IS_GUILD_ONLY_MESSAGE.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl()).build()).queue();
    }

    public static void botIsGuildOnly(@NotNull ButtonInteractionEvent event) {
        event.replyEmbeds(BOT_IS_GUILD_ONLY_MESSAGE.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl()).build()).queue();
    }
}
