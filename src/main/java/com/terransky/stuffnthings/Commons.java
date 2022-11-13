package com.terransky.stuffnthings;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Commons {

    private static final EmbedBuilder BOT_IS_GUILD_ONLY_MESSAGE = new EmbedBuilder()
        .setTitle("This bot is a server only bot.")
        .setDescription("Please execute this interaction in a server.")
        .setColor(getDefaultEmbedColor());

    @Contract(pure = true)
    private Commons() {
    }

    public static FastDateFormat getFastDateFormat() {
        return FastDateFormat.getInstance("dd-MM-yyyy_HH:mm");
    }

    public static boolean isEnableDatabase() {
        return getConfig().get("TESTING_MODE").equals("true");
    }

    public static boolean isTestingMode() {
        return getConfig().get("TESTING_MODE").equals("true");
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull Color getDefaultEmbedColor() {
        return new Color(102, 51, 102);
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull Color getSecondaryEmbedColor() {
        return new Color(153, 102, 153);
    }

    public static Dotenv getConfig() {
        return Dotenv.configure().load();
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
