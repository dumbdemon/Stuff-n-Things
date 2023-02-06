package com.terransky.stuffnthings.listeners;

import com.terransky.stuffnthings.Managers;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.interfaces.interactions.*;
import com.terransky.stuffnthings.utilities.cannedAgenda.GuildOnly;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.EmbedColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.Config;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InteractionListener extends ListenerAdapter {

    private final Logger log = LoggerFactory.getLogger(InteractionListener.class);
    private final Managers manager = Managers.getInstance();

    @NotNull
    private MessageEmbed getFailedInteractionMessage(IInteraction.Type type, @NotNull EventBlob blob) {
        return new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription(Responses.INTERACTION_FAILED.getMessage(type))
            .setColor(EmbedColors.getError())
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build();
    }

    private void logInteractionFailure(String interactionName, @NotNull String guildId, @NotNull Exception e) {
        log.error(String.format("%S failed to execute on guild id %s", interactionName, guildId), e);
    }

    private void errorHandler(@NotNull GenericCommandInteractionEvent event, @NotNull IInteraction interaction,
                              IInteraction.Type type, EventBlob blob, Exception e) {
        MessageEmbed commandFailed = getFailedInteractionMessage(type, blob);
        logInteractionFailure(interaction.getName(), blob.getGuildId(), e);
        if (event.isAcknowledged()) {
            event.getHook().sendMessageEmbeds(commandFailed).queue();
        } else event.replyEmbeds(commandFailed).setEphemeral(true).queue();
    }

    private void errorHandler(@NotNull GenericComponentInteractionCreateEvent event, @NotNull IInteraction interaction,
                              IInteraction.Type type, EventBlob blob, Exception e) {
        MessageEmbed componentFailed = getFailedInteractionMessage(type, blob);
        logInteractionFailure(interaction.getName(), blob.getGuildId(), e);
        if (event.isAcknowledged()) {
            event.getHook().sendMessageEmbeds(componentFailed).queue();
        } else event.replyEmbeds(componentFailed).setEphemeral(true).queue();
    }

    private void commandIsOwnerOnly(@NotNull GenericCommandInteractionEvent event, @NotNull EventBlob blob,
                                    @NotNull IInteraction.Type type) {
        String typeName = type.getName();
        event.replyEmbeds(new EmbedBuilder()
            .setTitle(String.format("%s is Owner Only", typeName))
            .setDescription(String.format("This %s can only be ran by the Owner.", typeName))
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build()
        ).setEphemeral(true).queue();
    }

    private void commandIsDevsOnly(@NotNull GenericCommandInteractionEvent event, @NotNull EventBlob blob,
                                   @NotNull IInteraction.Type type) {
        String typeName = type.getName();
        event.replyEmbeds(new EmbedBuilder()
            .setTitle(String.format("%s is for Devs Only", typeName))
            .setDescription(String.format("This %s can only be ran by Developers.", typeName))
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build()
        ).setEphemeral(true).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getUser().isBot()) return;
        else if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, IInteraction.Type.COMMAND_SLASH);
            return;
        }

        var slashManager = manager.getSlashManager();
        Optional<ICommandSlash> ifSlash = slashManager.getInteraction(event.getName());
        if (ifSlash.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());
        if (Config.isDatabaseEnabled()) DatabaseManager.INSTANCE.addUser(blob);

        ICommandSlash slash = ifSlash.get();
        log.debug("Command {} called on {} [{}]", slash.getName().toUpperCase(), blob.getGuildName(), blob.getGuildIdLong());

        if (slash.isOwnerOnly() && !blob.getMember().isOwner()) {
            commandIsOwnerOnly(event, blob, IInteraction.Type.COMMAND_SLASH);
            return;
        }

        if (slash.isDeveloperCommand() && !blob.getMemberId().equals(Config.getDeveloperId())) {
            commandIsDevsOnly(event, blob, IInteraction.Type.COMMAND_SLASH);
            return;
        }

        try {
            slash.execute(event, blob);
        } catch (RuntimeException | IOException e) {
            log.debug("Full command path that triggered error :: [{}]", event.getFullCommandName());
            errorHandler(event, slash, IInteraction.Type.COMMAND_SLASH, blob, e);
        }
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, IInteraction.Type.COMMAND_MESSAGE);
            return;
        }

        var contextManager = manager.getMessageContextManager();
        Optional<ICommandMessage> ifMenu = contextManager.getInteraction(event.getName());
        if (ifMenu.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());
        if (Config.isDatabaseEnabled()) DatabaseManager.INSTANCE.addUser(blob);

        ICommandMessage commandMessage = ifMenu.get();
        log.debug("Command \"{}\" called on {} [{}]", commandMessage.getName().toUpperCase(), blob.getGuild().getName(), blob.getGuildIdLong());

        if (commandMessage.isOwnerOnly() && !blob.getMember().isOwner()) {
            commandIsOwnerOnly(event, blob, IInteraction.Type.COMMAND_MESSAGE);
            return;
        }

        if (commandMessage.isDeveloperCommand() && !blob.getMemberId().equals(Config.getDeveloperId())) {
            commandIsDevsOnly(event, blob, IInteraction.Type.COMMAND_MESSAGE);
            return;
        }

        try {
            commandMessage.execute(event, blob);
        } catch (RuntimeException | IOException e) {
            errorHandler(event, commandMessage, IInteraction.Type.COMMAND_MESSAGE, blob, e);
        }
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, IInteraction.Type.COMMAND_USER);
            return;
        }

        var contextManager = manager.getUserContextManager();
        Optional<ICommandUser> ifMenu = contextManager.getInteraction(event.getName());
        if (ifMenu.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());
        if (Config.isDatabaseEnabled()) DatabaseManager.INSTANCE.addUser(blob);

        ICommandUser commandUser = ifMenu.get();
        log.debug("Command \"{}\" called on {} [{}]", commandUser.getName().toUpperCase(), blob.getGuildName(), blob.getGuildIdLong());

        if (commandUser.isOwnerOnly() && !blob.getMember().isOwner()) {
            commandIsOwnerOnly(event, blob, IInteraction.Type.COMMAND_USER);
            return;
        }

        if (commandUser.isDeveloperCommand() && !blob.getMemberId().equals(Config.getDeveloperId())) {
            commandIsDevsOnly(event, blob, IInteraction.Type.COMMAND_USER);
            return;
        }

        try {
            commandUser.execute(event, blob);
        } catch (RuntimeException | IOException e) {
            errorHandler(event, commandUser, IInteraction.Type.COMMAND_USER, blob, e);
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId() == null) return;
        else if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, IInteraction.Type.BUTTON);
            return;
        }

        var buttonManager = manager.getButtonManager();
        Optional<IButton> ifButton = buttonManager.getInteraction(event.getButton().getId());
        if (ifButton.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        IButton iButton = ifButton.get();
        log.debug("Button {} called on {} [{}]", iButton.getName().toUpperCase(), blob.getGuildName(), blob.getGuildIdLong());
        try {
            iButton.execute(event, blob);
        } catch (RuntimeException | IOException e) {
            errorHandler(event, iButton, IInteraction.Type.BUTTON, blob, e);
        }
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, IInteraction.Type.SELECTION_ENTITY);
            return;
        }

        var selectMenuManager = manager.getEntitySelectMenuManager();
        Optional<ISelectMenuEntity> ifMenu = selectMenuManager.getInteraction(event.getInteraction().getComponentId());
        if (ifMenu.isEmpty()) return;


        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());
        ISelectMenuEntity menu = ifMenu.get();
        log.debug("Select Menu {} called on {} [{}]", menu.getName().toUpperCase(), blob.getGuildName(), blob.getGuildIdLong());
        try {
            menu.execute(event, blob);
        } catch (RuntimeException | IOException e) {
            errorHandler(event, menu, IInteraction.Type.SELECTION_ENTITY, blob, e);
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event);
            return;
        }

        var modalManager = manager.getModalManager();
        Optional<IModal> ifModal = modalManager.getInteraction(event.getModalId());
        if (ifModal.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());
        IModal modal = ifModal.get();
        log.debug("Modal {} called on {} [{}]", modal.getName().toUpperCase(), blob.getGuild().getName(), blob.getGuildIdLong());
        try {
            modal.execute(event, blob);
        } catch (RuntimeException | IOException e) {
            MessageEmbed modalFailed = getFailedInteractionMessage(IInteraction.Type.MODAL, blob);
            logInteractionFailure(modal.getName(), blob.getGuildId(), e);
            if (event.isAcknowledged()) {
                event.getHook().sendMessageEmbeds(modalFailed).queue();
            } else event.replyEmbeds(modalFailed).setEphemeral(true).queue();
        }
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, IInteraction.Type.SELECTION_STRING);
            return;
        }

        var selectMenuManager = manager.getStringSelectMenuManager();
        List<Optional<ISelectMenuString>> ifMenus = new ArrayList<>() {{
            for (String id : event.getInteraction().getValues()) {
                add(selectMenuManager.getInteraction(id));
            }
        }};

        String componentId = event.getComponentId();
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        for (Optional<ISelectMenuString> ifMenu : ifMenus) {
            if (ifMenu.isPresent()) {
                ISelectMenuString menu = ifMenu.get();
                String interactionName = "%s[%s]".formatted(componentId.toUpperCase(), menu.getName().toUpperCase());
                log.debug("Select Menu {} called on {} [{}]", interactionName, blob.getGuildName(), blob.getGuildIdLong());
                try {
                    menu.execute(event, blob);
                } catch (RuntimeException | IOException e) {
                    errorHandler(event, menu, IInteraction.Type.BUTTON, blob, e);
                }
            }
        }
    }
}
