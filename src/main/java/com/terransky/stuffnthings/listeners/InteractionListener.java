package com.terransky.stuffnthings.listeners;

import com.terransky.stuffnthings.ManagersManager;
import com.terransky.stuffnthings.database.SQLiteDataSource;
import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.interfaces.interactions.*;
import com.terransky.stuffnthings.utilities.cannedAgenda.GuildOnly;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.EmbedColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.IInteractionType;
import com.terransky.stuffnthings.utilities.general.LogList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
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
    private final ManagersManager manager = new ManagersManager();

    @NotNull
    private MessageEmbed getFailedInteractionMessage(IInteractionType interaction, @NotNull EventBlob blob) {
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

    private void errorHandler(@NotNull GenericCommandInteractionEvent event, @NotNull IInteraction interaction,
                              IInteractionType type, EventBlob blob, Exception e) {
        MessageEmbed commandFailed = getFailedInteractionMessage(type, blob);
        logInteractionFailure(interaction.getName(), blob, e);
        if (event.isAcknowledged()) {
            event.getHook().sendMessageEmbeds(commandFailed).queue();
        } else event.replyEmbeds(commandFailed).setEphemeral(true).queue();
    }

    private void errorHandler(@NotNull GenericComponentInteractionCreateEvent event, @NotNull IInteraction interaction,
                              IInteractionType type, EventBlob blob, Exception e) {
        MessageEmbed componentFailed = getFailedInteractionMessage(type, blob);
        logInteractionFailure(interaction.getName(), blob, e);
        if (event.isAcknowledged()) {
            event.getHook().sendMessageEmbeds(componentFailed).queue();
        } else event.replyEmbeds(componentFailed).setEphemeral(true).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getUser().isBot()) return;
        else if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, IInteractionType.COMMAND_SLASH);
            return;
        }

        var slashManager = manager.getSlashManager();
        Optional<ICommandSlash> ifSlash = slashManager.getInteraction(event.getName());
        if (ifSlash.isEmpty()) return;
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        //Add user to database or ignore if exists
        if (Config.isDatabaseEnabled()) {
            try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                .prepareStatement("INSERT OR IGNORE INTO users_" + blob.getGuildId() + "(user_id) VALUES(?)")) {
                stmt.setString(1, event.getUser().getId());
                stmt.execute();
            } catch (SQLException e) {
                log.error("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
                LogList.error(Arrays.asList(e.getStackTrace()), log);
            }
        }

        ICommandSlash slash = ifSlash.get();
        log.debug("Command " + slash.getName().toUpperCase() + " called on %s [%d]".formatted(blob.getGuildName(), blob.getGuildIdLong()));
        try {
            slash.execute(event, blob);
        } catch (Exception e) {
            log.debug("Full command path that triggered error :: [" + event.getFullCommandName() + "]");
            errorHandler(event, slash, IInteractionType.COMMAND_SLASH, blob, e);
        }
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, IInteractionType.COMMAND_MESSAGE);
            return;
        }

        var contextManager = manager.getMessageContextManager();
        Optional<ICommandMessage> ifMenu = contextManager.getInteraction(event.getName());
        if (ifMenu.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        ICommandMessage commandMessage = ifMenu.get();
        log.debug("Command \"" + commandMessage.getName().toUpperCase() + "\" called on %s [%d]".formatted(blob.getGuild().getName(), blob.getGuildIdLong()));
        try {
            commandMessage.execute(event, blob);
        } catch (Exception e) {
            errorHandler(event, commandMessage, IInteractionType.COMMAND_MESSAGE, blob, e);
        }
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, IInteractionType.COMMAND_USER);
            return;
        }

        var contextManager = manager.getUserContextManager();
        Optional<ICommandUser> ifMenu = contextManager.getInteraction(event.getName());
        if (ifMenu.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        ICommandUser commandUser = ifMenu.get();
        log.debug("Command \"" + commandUser.getName().toUpperCase() + "\" called on %s [%d]".formatted(blob.getGuildName(), blob.getGuildIdLong()));
        try {
            commandUser.execute(event, blob);
        } catch (Exception e) {
            errorHandler(event, commandUser, IInteractionType.COMMAND_USER, blob, e);
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId() == null) return;
        else if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, IInteractionType.BUTTON);
            return;
        }

        var buttonManager = manager.getButtonManager();
        Optional<IButton> ifButton = buttonManager.getInteraction(event.getButton().getId());
        if (ifButton.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        IButton iButton = ifButton.get();
        log.debug("Button %s called on %s [%d]".formatted(iButton.getName().toUpperCase(), blob.getGuildName(), blob.getGuildIdLong()));
        try {
            iButton.execute(event, blob);
        } catch (Exception e) {
            errorHandler(event, iButton, IInteractionType.BUTTON, blob, e);
        }
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, IInteractionType.SELECTION_ENTITY);
            return;
        }

        var selectMenuManager = manager.getEntitySelectMenuManager();
        Optional<ISelectMenuEntity> ifMenu = selectMenuManager.getInteraction(event.getInteraction().getComponentId());
        if (ifMenu.isEmpty()) return;


        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());
        ISelectMenuEntity menu = ifMenu.get();
        log.debug("Select Menu %s called on %s [%d]".formatted(menu.getName().toUpperCase(), blob.getGuildName(), blob.getGuildIdLong()));
        try {
            menu.execute(event, blob);
        } catch (Exception e) {
            errorHandler(event, menu, IInteractionType.SELECTION_ENTITY, blob, e);
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, IInteractionType.MODAL);
            return;
        }

        var modalManager = manager.getModalManager();
        Optional<IModal> ifModal = modalManager.getInteraction(event.getModalId());
        if (ifModal.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());
        IModal modal = ifModal.get();
        log.debug("Modal %s called on %s [%d]".formatted(modal.getName().toUpperCase(), blob.getGuild().getName(), blob.getGuildIdLong()));
        try {
            modal.execute(event, blob);
        } catch (Exception e) {
            MessageEmbed modalFailed = getFailedInteractionMessage(IInteractionType.MODAL, blob);
            logInteractionFailure(modal.getName(), blob, e);
            if (event.isAcknowledged()) {
                event.getHook().sendMessageEmbeds(modalFailed).queue();
            } else event.replyEmbeds(modalFailed).setEphemeral(true).queue();
        }
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, IInteractionType.SELECTION_STRING);
            return;
        }

        var selectMenuManager = manager.getStringSelectMenuManager();
        List<Optional<ISelectMenuString>> ifMenus = new ArrayList<>();
        for (String id : event.getInteraction().getValues()) {
            ifMenus.add(selectMenuManager.getInteraction(id));
        }

        String componentId = event.getComponentId();
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        for (Optional<ISelectMenuString> ifMenu : ifMenus) {
            if (ifMenu.isPresent()) {
                ISelectMenuString menu = ifMenu.get();
                String interactionName = "%s[%s]".formatted(componentId.toUpperCase(), menu.getName().toUpperCase());
                log.debug("Select Menu %s called on %s [%d]".formatted(interactionName, blob.getGuildName(), blob.getGuildIdLong()));
                try {
                    menu.execute(event, blob);
                } catch (Exception e) {
                    errorHandler(event, menu, IInteractionType.BUTTON, blob, e);
                }
            }
        }
    }
}
