package com.terransky.stuffnthings.listeners;

import com.terransky.stuffnthings.Managers;
import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.interfaces.interactions.*;
import com.terransky.stuffnthings.utilities.cannedAgenda.GuildOnly;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.StandardResponse;
import com.terransky.stuffnthings.utilities.general.InteractionType;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
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

    @NotNull
    private Container getDisabledMessage(String message) {
        return StandardResponse.getResponseContainer("This command has been disabled!",
            List.of(
                TextDisplay.of("### There is an issue with this command and will need to be disabled until further notice."),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.of(String.format("### Reason%n```%s```", message))
            )
        );
    }

    @NotNull
    private Container getFailedInteractionMessage(@NotNull EventBlob blob) {
        return StandardResponse.getResponseContainer("Oops!", Responses.INTERACTION_FAILED, blob.getInteractionType());
    }

    private void logInteractionFailure(String interactionName, @NotNull String guildId, @NotNull Exception e) {
        log.error(String.format("%S failed to execute on guild id %s", interactionName, guildId), e);
    }

    private void errorHandler(@NotNull GenericCommandInteractionEvent event, @NotNull IInteraction<?> interaction,
                              EventBlob blob, Exception e) {
        Container commandFailed = getFailedInteractionMessage(blob);
        logInteractionFailure(interaction.getName(), blob.getGuildId(), e);
        if (event.isAcknowledged()) {
            event.getHook().sendMessageComponents(commandFailed).queue();
        } else event.replyComponents(commandFailed).setEphemeral(true).queue();
    }

    private void errorHandler(@NotNull GenericComponentInteractionCreateEvent event, @NotNull IInteraction<?> interaction,
                              EventBlob blob, Exception e) {
        Container componentFailed = getFailedInteractionMessage(blob);
        logInteractionFailure(interaction.getName(), blob.getGuildId(), e);
        if (event.isAcknowledged()) {
            event.getHook().sendMessageComponents(componentFailed).queue();
        } else event.replyComponents(componentFailed).setEphemeral(true).queue();
    }

    private void commandIsOwnerOnly(@NotNull GenericCommandInteractionEvent event, @NotNull EventBlob blob) {
        String typeName = blob.getInteractionType().getName();
        event.replyComponents(
            StandardResponse.getResponseContainer(String.format("%s is Owner Only", typeName),
                String.format("This %s can only be ran by the Owner.", typeName))
        ).setEphemeral(true).queue();
    }

    private void commandIsDevsOnly(@NotNull GenericCommandInteractionEvent event, @NotNull EventBlob blob) {
        String typeName = blob.getInteractionType().getName();
        event.replyComponents(
            StandardResponse.getResponseContainer(String.format("%s is for Devs Only", typeName),
                String.format("Command [%s] can only be ran by the Devs.", typeName))
        ).setEphemeral(true).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getUser().isBot()) return;
        else if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, InteractionType.COMMAND_SLASH);
            return;
        }

        Optional<SlashCommandInteraction> ifSlash = (new Managers.SlashCommands()).getInteraction(event.getName());
        if (ifSlash.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.COMMAND_SLASH)
            .setChannelUnion(event.getChannel());
        if (StuffNThings.getConfig().getCore().getEnableDatabase()) DatabaseManager.INSTANCE.addUser(blob);
        if (DatabaseManager.INSTANCE.isBanned(blob)) return;

        SlashCommandInteraction slash = ifSlash.get();
        log.debug("Command {} called on {} [{}]", slash.getName().toUpperCase(), blob.getGuildName(), blob.getGuildIdLong());

        if (slash.isOwnerOnly() && !blob.getMember().isOwner()) {
            commandIsOwnerOnly(event, blob);
            return;
        }

        if (slash.isDeveloperOnly() && !blob.getMemberId().equals(StuffNThings.getConfig().getCore().getOwnerId())) {
            commandIsDevsOnly(event, blob);
            return;
        }

        if (slash.isDisabled()) {
            event.replyComponents(getDisabledMessage(slash.getDisabledReason()))

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

        Optional<MessageCommandInteraction> ifMenu = (new Managers.MessageContextMenu()).getInteraction(event.getName());
        if (ifMenu.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.COMMAND_MESSAGE)
            .setChannelUnion(event.getChannel());
        if (StuffNThings.getConfig().getCore().getEnableDatabase()) DatabaseManager.INSTANCE.addUser(blob);
        if (DatabaseManager.INSTANCE.isBanned(blob)) return;

        MessageCommandInteraction commandMessage = ifMenu.get();
        logInteractionEvent(commandMessage, blob);

        if (commandMessage.isOwnerOnly() && !blob.getMember().isOwner()) {
            commandIsOwnerOnly(event, blob);
            return;
        }

        if (commandMessage.isDeveloperOnly() && !blob.getMemberId().equals(StuffNThings.getConfig().getCore().getOwnerId())) {
            commandIsDevsOnly(event, blob);
            return;
        }

        if (commandMessage.isDisabled()) {
            event.replyComponents(getDisabledMessage(commandMessage.getDisabledReason()))

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

        Optional<UserCommandInteraction> ifMenu = (new Managers.UserContextMenu()).getInteraction(event.getName());
        if (ifMenu.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.COMMAND_USER);
        if (StuffNThings.getConfig().getCore().getEnableDatabase()) DatabaseManager.INSTANCE.addUser(blob);
        if (DatabaseManager.INSTANCE.isBanned(blob)) return;

        UserCommandInteraction commandUser = ifMenu.get();
        logInteractionEvent(commandUser, blob);

        if (commandUser.isOwnerOnly() && !blob.getMember().isOwner()) {
            commandIsOwnerOnly(event, blob);
            return;
        }

        if (commandUser.isDeveloperOnly() && !blob.getMemberId().equals(StuffNThings.getConfig().getCore().getOwnerId())) {
            commandIsDevsOnly(event, blob);
            return;
        }

        if (commandUser.isDisabled()) {
            event.replyComponents(getDisabledMessage(commandUser.getDisabledReason()))
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

    private <T extends IInteraction<?>> void logInteractionEvent(@NotNull T interaction, @NotNull EventBlob blob) {
        log.debug("Command \"{}\" called on {} [{}]", interaction.getName().toUpperCase(), blob.getGuildName(), blob.getGuildIdLong());
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, InteractionType.BUTTON);
            return;
        }

        Optional<ButtonInteraction> ifButton = (new Managers.DiscordButtons()).getInteraction(event.getButton().getLabel());
        if (ifButton.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.BUTTON)
            .setChannelUnion(event.getChannel());
        if (DatabaseManager.INSTANCE.isBanned(blob)) return;

        ButtonInteraction iButton = ifButton.get();
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

        Optional<SelectMenuEntityInteraction> ifMenu = (new Managers.EntitySelectMenu()).getInteraction(event.getInteraction().getComponentId());
        if (ifMenu.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.SELECTION_ENTITY)
            .setChannelUnion(event.getChannel());
        if (DatabaseManager.INSTANCE.isBanned(blob)) return;
        SelectMenuEntityInteraction menu = ifMenu.get();
        logSelectMenu(menu.getName().toUpperCase(), blob);
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

        Optional<ModalInteraction> ifModal = (new Managers.ModalInteractions()).getInteraction(event.getModalId());
        if (ifModal.isEmpty()) return;

        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.MODAL)
            .setChannelUnion(event.getChannel());
        if (DatabaseManager.INSTANCE.isBanned(blob)) return;
        ModalInteraction modal = ifModal.get();
        log.debug("Modal {} called on {} [{}]", modal.getName().toUpperCase(), blob.getGuild().getName(), blob.getGuildIdLong());
        try {
            modal.execute(event, blob);
        } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
            Container modalFailed = getFailedInteractionMessage(blob);
            logInteractionFailure(modal.getName(), blob.getGuildId(), e);
            if (event.isAcknowledged()) {
                event.getHook().sendMessageComponents(modalFailed).queue();
            } else event.replyComponents(modalFailed).setEphemeral(true).queue();
        }
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, InteractionType.SELECTION_STRING);
            return;
        }

        List<SelectMenuStringInteraction> menus = new ArrayList<>() {{
            var selectMenuManager = new Managers.StringSelectMenu();
            for (String menuName : event.getInteraction().getValues()) {
                selectMenuManager.getInteraction(menuName).ifPresent(this::add);
            }
        }};

        String componentId = event.getComponentId();
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember())
            .setInteractionType(InteractionType.SELECTION_STRING)
            .setChannelUnion(event.getChannel());
        if (DatabaseManager.INSTANCE.isBanned(blob)) return;

        for (SelectMenuStringInteraction stringMenu : menus) {
            String interactionName = "%s[%s]".formatted(componentId.toUpperCase(), stringMenu.getName().toUpperCase());
            logSelectMenu(interactionName, blob);
            try {
                stringMenu.execute(event, blob);
            } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
                errorHandler(event, stringMenu, blob, e);
            }
        }
    }

    private void logSelectMenu(String interactionName, @NotNull EventBlob blob) {
        log.debug("Select Menu {} called on {} [{}]", interactionName, blob.getGuildName(), blob.getGuildIdLong());
    }
}
