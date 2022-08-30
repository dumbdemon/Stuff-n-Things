package com.terransky.StuffnThings.commandSystem;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.contextMenus.reportMessage;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ContextManager extends ListenerAdapter {
    private final Color embedColor = new Commons().defaultEmbedColor;
    private final Dotenv config = Dotenv.configure().load();
    private final Logger log = LoggerFactory.getLogger(ContextManager.class);
    private final List<IContextMenu> iContextMenus = new ArrayList<>();

    public ContextManager() {
        addContextMenu(new reportMessage());
    }

    private void addContextMenu(IContextMenu iContextMenu) {
        boolean nameFound = iContextMenus.stream().anyMatch(it -> it.getMenuName().equalsIgnoreCase(iContextMenu.getMenuName()));

        if (nameFound) throw new IllegalArgumentException("A context menu with this name already exists");

        iContextMenus.add(iContextMenu);
    }

    @Nullable
    private IContextMenu getContextMenu(@NotNull String search) {
        for (IContextMenu menu : iContextMenus) {
            if (menu.getMenuName().equals(search)) {
                return menu;
            }
        }

        return null;
    }

    public @NotNull List<CommandData> getContextData() {
        final List<CommandData> commandData = new ArrayList<>();

        for (IContextMenu iContextMenu : iContextMenus) {
            commandData.add(iContextMenu.contextData());
        }

        return commandData;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        log.debug(event.getName());
        IContextMenu menu = getContextMenu(event.getName());
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Oops!")
                .setDescription("An error occurred while executing that context menu!\nPlease contact <@" + config.get("OWNER_ID") + "> with the context menu that you used and when.")
                .setColor(embedColor)
                .setFooter(event.getUser().getAsTag(), event.getMember().getEffectiveAvatarUrl());

        if (menu != null) {
            String origins = event.isFromGuild() ? "%s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()) : event.getUser().getAsTag() + "'s private channel";
            log.debug("Command \"" + menu.getMenuName().toUpperCase() + "\" called on " + origins);
            try {
                menu.messageContextExecute(event);
            } catch (Exception e) {
                log.debug("%s failed to execute on guild id %s".formatted(menu.getMenuName(), event.getGuild().getId()));
                log.error("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(eb.build()).queue();
                } else event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        IContextMenu menu = getContextMenu(event.getName());
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Oops!")
                .setDescription("An error occurred while executing that context menu!\nPlease contact <@" + config.get("OWNER_ID") + "> with the context menu that you used and when.")
                .setColor(embedColor)
                .setFooter(event.getUser().getAsTag(), event.getMember().getEffectiveAvatarUrl());

        if (menu != null) {
            String origins = event.isFromGuild() ? "%s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()) : event.getUser().getAsTag() + "'s private channel";
            log.debug("Command \"" + menu.getMenuName().toUpperCase() + "\" called on " + origins);
            try {
                menu.userContextExecute(event);
            } catch (Exception e) {
                log.debug("%s failed to execute on guild id %s".formatted(menu.getMenuName(), event.getGuild().getId()));
                log.error("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(eb.build()).queue();
                } else event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            }
        }
    }
}
