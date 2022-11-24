package com.terransky.stuffnthings;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;

public class Commons {

    private static final EmbedBuilder BOT_IS_GUILD_ONLY_MESSAGE = new EmbedBuilder()
        .setTitle("This bot is a server only bot.")
        .setDescription("Please execute this interaction in a server.")
        .setColor(getDefaultEmbedColor());

    private Commons() {
    }

    public static FastDateFormat getFastDateFormat() {
        return FastDateFormat.getInstance("dd-MM-yyyy_HH:mm");
    }

    public static boolean isEnableDatabase() {
        return getConfig().get("ENABLE_DATABASE").equals("true");
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

    public static void botIsGuildOnly(@NotNull EntitySelectInteractionEvent event) {
        event.replyEmbeds(BOT_IS_GUILD_ONLY_MESSAGE.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl()).build()).queue();
    }

    public static void botIsGuildOnly(@NotNull ButtonInteractionEvent event) {
        event.replyEmbeds(BOT_IS_GUILD_ONLY_MESSAGE.setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl()).build()).queue();
    }

    public static void loggerPrinterOfError(@NotNull List<?> aList, Logger log) {
        for (Object listItem : aList) {
            log.error(listItem.toString());
        }
    }

    public static void loggerPrinterOfError(@NotNull List<?> aList, Class<?> clazz) {
        final Logger log = LoggerFactory.getLogger(clazz);

        loggerPrinterOfError(aList, log);
    }

    public static void loggerPrinterOfDebug(@NotNull List<?> aList, Logger log) {
        for (Object listItem : aList) {
            log.debug(listItem.toString());
        }
    }

    public static void loggerPrinterOfDebug(@NotNull List<?> aList, Class<?> clazz) {
        final Logger log = LoggerFactory.getLogger(clazz);

        loggerPrinterOfDebug(aList, log);
    }
}
