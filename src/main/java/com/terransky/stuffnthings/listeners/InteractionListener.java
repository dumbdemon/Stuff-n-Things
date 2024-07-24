package com.terransky.stuffnthings.listeners;

import com.terransky.stuffnthings.Managers;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.interfaces.interactions.*;
import com.terransky.stuffnthings.utilities.cannedAgenda.GuildOnly;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.EmbedColor;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.InteractionType;
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
import java.util.concurrent.ExecutionException;

public class InteractionListener extends ListenerAdapter {

    private final Logger log = LoggerFactory.getLogger(InteractionListener.class);
    private final Managers manager = Managers.getInstance();

    @NotNull
    private MessageEmbed getDisabledMessage(@NotNull EventBlob blob, String message) {
        return new EmbedBuilder(blob.getStandardEmbed("This command has been disabled!", EmbedColor.ERROR))
            .setDescription("There is an issue with this command and will need to be disabled until further notice.")
            .addField("Reason", message, false)
            .build();
    }

    @NotNull
    private MessageEmbed getFailedInteractionMessage(@NotNull EventBlob blob) {
        return blob.getStandardEmbed("Oops!", EmbedColor.ERROR)
            .setDescription(Responses.INTERACTION_FAILED.getMessage(blob.getInteractionType()))
            .build();
    }

    private void logInteractionFailure(String interactionName, @NotNull String guildId, @NotNull Exception e) {
        log.error(String.format("%S failed to execute on guild id %s", interactionName, guildId), e);
    }

    private void errorHandler(@NotNull GenericCommandInteractionEvent event, @NotNull IInteraction<?> interaction,
                              EventBlob blob, Exception e) {
        MessageEmbed commandFailed = getFailedInteractionMessage(blob);
        logInteractionFailure(interaction.getName(), blob.getGuildId(), e);
        if (event.isAcknowledged()) {
            event.getHook().sendMessageEmbeds(commandFailed).queue();
        } else event.replyEmbeds(commandFailed).setEphemeral(true).queue();
    }

    private void errorHandler(@NotNull GenericComponentInteractionCreateEvent event, @NotNull IInteraction<?> interaction,
                              EventBlob blob, Exception e) {
        MessageEmbed componentFailed = getFailedInteractionMessage(blob);
        logInteractionFailure(interaction.getName(), blob.getGuildId(), e);
        if (event.isAcknowledged()) {
            event.getHook().sendMessageEmbeds(componentFailed).queue();
        } else event.replyEmbeds(componentFailed).setEphemeral(true).queue();
    }

    private void commandIsOwnerOnly(@NotNull GenericCommandInteractionEvent event, @NotNull EventBlob blob) {
        String typeName = blob.getInteractionType().getName();
        event.replyEmbeds(new EmbedBuilder()
            .setTitle(String.format("%s is Owner Only", typeName))
            .setDescription(String.format("This %s can only be ran by the Owner.", typeName))
            .setFooter(blob.getMemberName(), blob.getMemberEffectiveAvatarUrl())
            .build()
        ).setEphemeral(true).queue();
    }

    private void commandIsDevsOnly(@NotNull GenericCommandInteractionEvent event, @NotNull EventBlob blob) {
        String typeName = blob.getInteractionType().getName();
        event.replyEmbeds(new EmbedBuilder()
            .setTitle(String.format("%s is for Devs Only", typeName))
            .setDescription(String.format("This %s can only be ran by Developers.", typeName))
            .setFooter(blob.getMemberName(), blob.getMemberEffectiveAvatarUrl())
            .build()
        ).setEphemeral(true).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getUser().isBot()) return;
        else if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, InteractionType.COMMAND_SLASH);
            return;
        }

        var slashManager = manager.getSlashManager();
        Optional<ICommandSlash> ifSlash = slashManager.getInteraction(event.getName());
        if (ifSlash.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.COMMAND_SLASH)
            .setChannelUnion(event.getChannel());
        if (Config.isDatabaseEnabled()) DatabaseManager.INSTANCE.addUser(blob);
        if (DatabaseManager.INSTANCE.isBanned(blob)) return;

        ICommandSlash slash = ifSlash.get();
        log.debug("Command {} called on {} [{}]", slash.getName().toUpperCase(), blob.getGuildName(), blob.getGuildIdLong());

        if (slash.isOwnerOnly() && !blob.getMember().isOwner()) {
            commandIsOwnerOnly(event, blob);
            return;
        }

        if (slash.isDeveloperCommand() && !blob.getMemberId().equals(Config.getDeveloperId())) {
            commandIsDevsOnly(event, blob);
            return;
        }

        if (slash.isDisabled()) {
            event.replyEmbeds(getDisabledMessage(blob, slash.getDisabledReason()))
                .setEphemeral(true)
                .queue();
            return;
        }

        try {
            slash.execute(event, blob);
        } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
            log.debug("Full command path that triggered error :: [{}]", event.getFullCommandName());
            errorHandler(event, slash, blob, e);
        }
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, InteractionType.COMMAND_MESSAGE);
            return;
        }

        var contextManager = manager.getMessageContextManager();
        Optional<ICommandMessage> ifMenu = contextManager.getInteraction(event.getName());
        if (ifMenu.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.COMMAND_MESSAGE)
            .setChannelUnion(event.getChannel());
        if (Config.isDatabaseEnabled()) DatabaseManager.INSTANCE.addUser(blob);
        if (DatabaseManager.INSTANCE.isBanned(blob)) return;

        ICommandMessage commandMessage = ifMenu.get();
        log.debug("Command \"{}\" called on {} [{}]", commandMessage.getName().toUpperCase(), blob.getGuild().getName(), blob.getGuildIdLong());

        if (commandMessage.isOwnerOnly() && !blob.getMember().isOwner()) {
            commandIsOwnerOnly(event, blob);
            return;
        }

        if (commandMessage.isDeveloperCommand() && !blob.getMemberId().equals(Config.getDeveloperId())) {
            commandIsDevsOnly(event, blob);
            return;
        }

        if (commandMessage.isDisabled()) {
            event.replyEmbeds(getDisabledMessage(blob, commandMessage.getDisabledReason()))
                .setEphemeral(true)
                .queue();
            return;
        }

        try {
            commandMessage.execute(event, blob);
        } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
            errorHandler(event, commandMessage, blob, e);
        }
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, InteractionType.COMMAND_USER);
            return;
        }

        var contextManager = manager.getUserContextManager();
        Optional<ICommandUser> ifMenu = contextManager.getInteraction(event.getName());
        if (ifMenu.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.COMMAND_USER);
        if (Config.isDatabaseEnabled()) DatabaseManager.INSTANCE.addUser(blob);
        if (DatabaseManager.INSTANCE.isBanned(blob)) return;

        ICommandUser commandUser = ifMenu.get();
        log.debug("Command \"{}\" called on {} [{}]", commandUser.getName().toUpperCase(), blob.getGuildName(), blob.getGuildIdLong());

        if (commandUser.isOwnerOnly() && !blob.getMember().isOwner()) {
            commandIsOwnerOnly(event, blob);
            return;
        }

        if (commandUser.isDeveloperCommand() && !blob.getMemberId().equals(Config.getDeveloperId())) {
            commandIsDevsOnly(event, blob);
            return;
        }

        if (commandUser.isDisabled()) {
            event.replyEmbeds(getDisabledMessage(blob, commandUser.getDisabledReason()))
                .setEphemeral(true)
                .queue();
            return;
        }


        try {
            commandUser.execute(event, blob);
        } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
            errorHandler(event, commandUser, blob, e);
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId() == null) return;
        else if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, InteractionType.BUTTON);
            return;
        }

        var buttonManager = manager.getButtonManager();
        Optional<IButton> ifButton = buttonManager.getInteraction(event.getButton().getId());
        if (ifButton.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.BUTTON)
            .setChannelUnion(event.getChannel());
        if (DatabaseManager.INSTANCE.isBanned(blob)) return;

        IButton iButton = ifButton.get();
        log.debug("Button {} called on {} [{}]", iButton.getName().toUpperCase(), blob.getGuildName(), blob.getGuildIdLong());
        try {
            iButton.execute(event, blob);
        } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
            errorHandler(event, iButton, blob, e);
        }
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, InteractionType.SELECTION_ENTITY);
            return;
        }

        var selectMenuManager = manager.getEntitySelectMenuManager();
        Optional<ISelectMenuEntity> ifMenu = selectMenuManager.getInteraction(event.getInteraction().getComponentId());
        if (ifMenu.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.SELECTION_ENTITY)
            .setChannelUnion(event.getChannel());
        if (DatabaseManager.INSTANCE.isBanned(blob)) return;
        ISelectMenuEntity menu = ifMenu.get();
        log.debug("Select Menu {} called on {} [{}]", menu.getName().toUpperCase(), blob.getGuildName(), blob.getGuildIdLong());
        try {
            menu.execute(event, blob);
        } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
            errorHandler(event, menu, blob, e);
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

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.MODAL)
            .setChannelUnion(event.getChannel());
        if (DatabaseManager.INSTANCE.isBanned(blob)) return;
        IModal modal = ifModal.get();
        log.debug("Modal {} called on {} [{}]", modal.getName().toUpperCase(), blob.getGuild().getName(), blob.getGuildIdLong());
        try {
            modal.execute(event, blob);
        } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
            MessageEmbed modalFailed = getFailedInteractionMessage(blob);
            logInteractionFailure(modal.getName(), blob.getGuildId(), e);
            if (event.isAcknowledged()) {
                event.getHook().sendMessageEmbeds(modalFailed).queue();
            } else event.replyEmbeds(modalFailed).setEphemeral(true).queue();
        }
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, InteractionType.SELECTION_STRING);
            return;
        }

        List<ISelectMenuString> menus = new ArrayList<>() {{
            var selectMenuManager = manager.getStringSelectMenuManager();
            for (String menuName : event.getInteraction().getValues()) {
                selectMenuManager.getInteraction(menuName).ifPresent(this::add);
            }
        }};

        String componentId = event.getComponentId();
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.SELECTION_STRING)
            .setChannelUnion(event.getChannel());
        if (DatabaseManager.INSTANCE.isBanned(blob)) return;

        for (ISelectMenuString stringMenu : menus) {
            String interactionName = "%s[%s]".formatted(componentId.toUpperCase(), stringMenu.getName().toUpperCase());
            log.debug("Select Menu {} called on {} [{}]", interactionName, blob.getGuildName(), blob.getGuildIdLong());
            try {
                stringMenu.execute(event, blob);
            } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
                errorHandler(event, stringMenu, blob, e);
            }
        }
    }
}
