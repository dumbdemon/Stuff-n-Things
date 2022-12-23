package com.terransky.stuffnthings.listeners;

import com.terransky.stuffnthings.InteractionManager;
import com.terransky.stuffnthings.database.SQLiteDataSource;
import com.terransky.stuffnthings.interfaces.*;
import com.terransky.stuffnthings.managers.SlashManager;
import com.terransky.stuffnthings.utilities.cannedAgenda.GuildOnly;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.EmbedColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.Interactions;
import com.terransky.stuffnthings.utilities.general.LogList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class InteractionListener extends ListenerAdapter {

    private final Logger log = LoggerFactory.getLogger(InteractionListener.class);
    private final InteractionManager manager = new InteractionManager();

    @NotNull
    private MessageEmbed getFailedInteractionMessage(Interactions interaction, @NotNull EventBlob blob) {
        return new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription(Responses.INTERACTION_FAILED.getMessage(interaction))
            .setColor(EmbedColors.getError())
            .setFooter(blob.getMember().getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build();
    }

    private void logInteractionFailure(String interactionName, @NotNull EventBlob blob, @NotNull Exception e) {
        log.debug("%s failed to execute on guild id %s".formatted(interactionName, blob.getGuildId()));
        log.error("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
        LogList.error(Arrays.asList(e.getStackTrace()), log);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        var slashManager = manager.getSlashManager();
        if (event.getUser().isBot()) return;
        else if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, Interactions.SLASH_COMMAND);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        //Add user to database or ignore if exists
        if (Config.isDatabaseEnabled()) {
            try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                .prepareStatement("INSERT OR IGNORE INTO users_" + blob.getGuildId() + "(user_id) VALUES(?)")) {
                stmt.setString(1, event.getUser().getId());
                stmt.execute();
            } catch (SQLException e) {
                log.error("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
                LogList.error(Arrays.asList(e.getStackTrace()), SlashManager.class);
            }
        }

        Optional<ICommandSlash> ifSlash = slashManager.getInteraction(event.getName());
        MessageEmbed cmdFailed = getFailedInteractionMessage(Interactions.SLASH_COMMAND, blob);

        if (ifSlash.isPresent()) {
            ICommandSlash slash = ifSlash.get();
            log.debug("Command " + slash.getName().toUpperCase() + " called on %s [%d]".formatted(blob.getGuildName(), blob.getGuildIdLong()));
            try {
                slash.execute(event, blob);
            } catch (Exception e) {
                log.debug("Full command path that triggered error :: [" + event.getFullCommandName() + "]");
                logInteractionFailure(slash.getName(), blob, e);
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(cmdFailed).queue();
                } else event.replyEmbeds(cmdFailed).setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        var contextManager = manager.getMessageContextManager();
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, Interactions.CONTEXT_MESSAGE);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        Optional<ICommandMessage> ifMenu = contextManager.getInteraction(event.getName());
        if (ifMenu.isEmpty()) return;

        MessageEmbed menuFailed = getFailedInteractionMessage(Interactions.CONTEXT_MESSAGE, blob);
        ICommandMessage commandMessage = ifMenu.get();
        log.debug("Command \"" + commandMessage.getName().toUpperCase() + "\" called on %s [%d]".formatted(blob.getGuild().getName(), blob.getGuildIdLong()));
        try {
            commandMessage.execute(event, blob);
        } catch (Exception e) {
            logInteractionFailure(commandMessage.getName(), blob, e);
            if (event.isAcknowledged()) {
                event.getHook().sendMessageEmbeds(menuFailed).queue();
            } else event.replyEmbeds(menuFailed).setEphemeral(true).queue();
        }

    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        var contextManager = manager.getUserContextManager();
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, Interactions.CONTEXT_USER);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        Optional<ICommandUser> ifMenu = contextManager.getInteraction(event.getName());
        if (ifMenu.isEmpty()) return;

        MessageEmbed menuFailed = getFailedInteractionMessage(Interactions.CONTEXT_USER, blob);
        ICommandUser commandUser = ifMenu.get();
        log.debug("Command \"" + commandUser.getName().toUpperCase() + "\" called on %s [%d]".formatted(blob.getGuildName(), blob.getGuildIdLong()));
        try {
            commandUser.execute(event, blob);
        } catch (Exception e) {
            logInteractionFailure(commandUser.getName(), blob, e);
            if (event.isAcknowledged()) {
                event.getHook().sendMessageEmbeds(menuFailed).queue();
            } else event.replyEmbeds(menuFailed).setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        var buttonManager = manager.getButtonManager();
        if (event.getButton().getId() == null) return;
        else if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, Interactions.BUTTON);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        Optional<IButton> ifButton = buttonManager.getInteraction(event.getButton().getId());
        if (ifButton.isEmpty()) return;

        MessageEmbed buttonFailed = getFailedInteractionMessage(Interactions.BUTTON, blob);
        IButton iButton = ifButton.get();
        log.debug("Button %s called on %s [%d]".formatted(iButton.getName().toUpperCase(), blob.getGuildName(), blob.getGuildIdLong()));
        try {
            iButton.execute(event, blob);
        } catch (Exception e) {
            logInteractionFailure(iButton.getName(), blob, e);
            event.replyEmbeds(buttonFailed).setEphemeral(true).queue();
        }
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        var selectMenuManager = manager.getEntitySelectMenuManager();
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, Interactions.SELECT_MENU);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());
        Optional<ISelectMenuEntity> ifMenu = selectMenuManager.getInteraction(event.getInteraction().getComponentId());

        if (ifMenu.isEmpty()) return;

        MessageEmbed menuFailed = getFailedInteractionMessage(Interactions.SELECT_MENU, blob);
        ISelectMenuEntity menu = ifMenu.get();
        log.debug("Select Menu %s called on %s [%d]".formatted(menu.getName().toUpperCase(), blob.getGuildName(), blob.getGuildIdLong()));
        try {
            menu.execute(event, blob);
        } catch (Exception e) {
            logInteractionFailure(menu.getName(), blob, e);
            if (event.isAcknowledged()) {
                event.getHook().sendMessageEmbeds(menuFailed).queue();
            } else event.replyEmbeds(menuFailed).setEphemeral(true).queue();
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        var modalManager = manager.getModalManager();
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, Interactions.MODAL);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());
        Optional<IModal> ifModal = modalManager.getInteraction(event.getModalId());

        if (ifModal.isEmpty()) return;

        MessageEmbed modalFailed = getFailedInteractionMessage(Interactions.MODAL, blob);
        IModal modal = ifModal.get();
        log.debug("Modal %s called on %s [%d]".formatted(modal.getName().toUpperCase(), blob.getGuild().getName(), blob.getGuildIdLong()));
        try {
            modal.execute(event, blob);
        } catch (Exception e) {
            logInteractionFailure(modal.getName(), blob, e);
            if (event.isAcknowledged()) {
                event.getHook().sendMessageEmbeds(modalFailed).queue();
            } else event.replyEmbeds(modalFailed).setEphemeral(true).queue();
        }
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        var selectMenuManager = manager.getStringSelectMenuManager();
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, Interactions.SELECT_MENU);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        List<Optional<ISelectMenuString>> ifMenus = new ArrayList<>();
        for (String id : event.getInteraction().getValues()) {
            ifMenus.add(selectMenuManager.getInteraction(id));
        }
        String componentId = event.getComponentId();
        MessageEmbed menuFailed = getFailedInteractionMessage(Interactions.SELECT_MENU, blob);

        for (Optional<ISelectMenuString> ifMenu : ifMenus) {
            if (ifMenu.isPresent()) {
                ISelectMenuString menu = ifMenu.get();
                log.debug("Select Menu %s[%s] called on %s [%d]".formatted(componentId.toUpperCase(), menu.getName().toUpperCase(),
                    blob.getGuildName(), blob.getGuildIdLong()));
                try {
                    menu.execute(event, blob);
                } catch (Exception e) {
                    String interactionName = "%s[%s]".formatted(componentId.toUpperCase(), menu.getName().toUpperCase());
                    logInteractionFailure(interactionName, blob, e);
                    if (event.isAcknowledged()) {
                        event.getHook().sendMessageEmbeds(menuFailed).queue();
                    } else event.replyEmbeds(menuFailed).setEphemeral(true).queue();
                }
            }
        }
    }
}
