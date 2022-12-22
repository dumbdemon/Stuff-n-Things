package com.terransky.stuffnthings.managers;

import com.terransky.stuffnthings.interfaces.ICommandMessage;
import com.terransky.stuffnthings.utilities.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MessageContextManager extends ListenerAdapter {
    private final List<ICommandMessage> iCommandMessages = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(MessageContextManager.class);

    public MessageContextManager(@NotNull ICommandMessage... iCommandMessages) {
        for (ICommandMessage iCommandMessage : iCommandMessages) {
            addContextMenu(iCommandMessage);
        }
    }

    /**
     * Add a {@link ICommandMessage} object to be indexed and used.
     *
     * @param iCommandMessage An {@link ICommandMessage} object.
     * @throws IllegalArgumentException  If an {@link ICommandMessage} with that name is already indexed.
     * @throws IndexOutOfBoundsException If {@link MessageContextManager#iCommandMessages} has more than the {@link Commands#MAX_MESSAGE_COMMANDS}.
     */
    @SuppressWarnings("unused")
    private void addContextMenu(ICommandMessage iCommandMessage) {
        boolean nameFound = iCommandMessages.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iCommandMessage.getName()));

        if (nameFound) throw new IllegalArgumentException("A context menu with this name already exists");

        if (iCommandMessages.size() > Commands.MAX_MESSAGE_COMMANDS)
            throw new IndexOutOfBoundsException("You can only have at most %d message contexts".formatted(Commands.MAX_MESSAGE_COMMANDS));
        else iCommandMessages.add(iCommandMessage);
    }

    /**
     * /**
     * Get the {@link ICommandMessage} object for execution at {@code onMessageContextInteraction()}.
     *
     * @param search The name of the Context Menu
     * @return An {@link Optional} {@link ICommandMessage}.
     */
    private Optional<ICommandMessage> getMessageMenu(@NotNull String search) {
        for (ICommandMessage menu : iCommandMessages) {
            if (menu.getName().equals(search)) {
                return Optional.of(menu);
            }
        }

        return Optional.empty();
    }

    /**
     * Get the command data of message contexts menus.
     * <p>
     * If a {@link ParseException} occurs, it will not be pushed.
     *
     * @return Returns a list of {@link CommandData}.
     */
    public List<CommandData> getCommandData() {
        final List<CommandData> commandData = new ArrayList<>();

        for (ICommandMessage iCommandMessage : iCommandMessages.stream().filter(ICommandMessage::isWorking).toList()) {
            try {
                commandData.add(iCommandMessage.getCommandData());
            } catch (ParseException e) {
                log.warn("The date formatting in %s is invalid and will not be pushed.".formatted(iCommandMessage.getName().toUpperCase()));
            }
        }

        return commandData;
    }

    /**
     * The main event handler for all Message Context Menus.
     *
     * @param event A {@link MessageContextInteractionEvent}.
     */
    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, Interactions.CONTEXT_MESSAGE);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        Optional<ICommandMessage> ifMenu = getMessageMenu(event.getName());
        MessageEmbed menuFailed = new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription(CannedResponses.INTERACTION_FAILED.getMessage(Interactions.CONTEXT_MESSAGE))
            .setColor(EmbedColors.getError())
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build();

        if (ifMenu.isPresent()) {
            ICommandMessage context = ifMenu.get();
            log.debug("Command \"" + context.getName().toUpperCase() + "\" called on %s [%d]".formatted(blob.getGuild().getName(), blob.getGuildIdLong()));
            try {
                context.execute(event, blob);
            } catch (Exception e) {
                log.debug("%s failed to execute on guild id %s".formatted(context.getName(), blob.getGuildId()));
                log.error("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
                LogList.error(Arrays.asList(e.getStackTrace()), log);
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(menuFailed).queue();
                } else event.replyEmbeds(menuFailed).setEphemeral(true).queue();
            }
        }
    }
}
