package com.terransky.stuffnthings.commandSystem;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.interfaces.IMessageContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageContextManager extends ListenerAdapter {
    private final List<IMessageContext> iMessageContexts = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(MessageContextManager.class);

    public MessageContextManager() {
    }

    /**
     * Add a {@link IMessageContext} object to be indexed and used.
     *
     * @param iMessageContext An {@link  IMessageContext} object.
     */
    @SuppressWarnings("unused")
    private void addContextMenu(IMessageContext iMessageContext) {
        boolean nameFound = iMessageContexts.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iMessageContext.getName()));

        if (nameFound) throw new IllegalArgumentException("A context menu with this name already exists");

        if (iMessageContexts.size() > Commands.MAX_MESSAGE_COMMANDS)
            throw new IndexOutOfBoundsException("You can only have at most %d message contexts".formatted(Commands.MAX_MESSAGE_COMMANDS));
        else iMessageContexts.add(iMessageContext);
    }

    /**
     * /**
     * Get the {@link IMessageContext} object for execution at {@code onMessageContextInteraction()}.
     *
     * @param search The name of the Context Menu
     * @return An {@link IMessageContext} object or null.
     */
    @Nullable
    private IMessageContext getMessageMenu(@NotNull String search) {
        for (IMessageContext menu : iMessageContexts) {
            if (menu.getName().equals(search)) {
                return menu;
            }
        }

        return null;
    }

    /**
     * Get the command data of message contexts menus.
     *
     * @return Returns a list of {@link CommandData}.
     * @throws ParseException If the pattern used in {@code Metadata.implementationDate()} or {@code Metadata.lastUpdated()} in an {@link IMessageContext}
     *                        is given an invalid date string.
     */
    public List<CommandData> getCommandData() throws ParseException {
        final List<CommandData> commandData = new ArrayList<>();

        for (IMessageContext iMessageContext : iMessageContexts) {
            if (iMessageContext.isWorking())
                commandData.add(iMessageContext.getCommandData());
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
            Commons.botIsGuildOnly(event);
            return;
        }

        IMessageContext menu = getMessageMenu(event.getName());
        MessageEmbed menuFailed = new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription("An error occurred while executing that context menu!\nPlease contact <@" + Commons.getConfig().get("OWNER_ID") + "> with the context menu that you used and when.")
            .setColor(Commons.getDefaultEmbedColor())
            .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
            .build();

        if (menu != null) {
            log.debug("Command \"" + menu.getName().toUpperCase() + "\" called on %s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()));
            try {
                menu.execute(event);
            } catch (Exception e) {
                log.debug("%s failed to execute on guild id %s".formatted(menu.getName(), event.getGuild().getId()));
                log.error("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
                log.error(Arrays.toString(e.getStackTrace()));
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(menuFailed).queue();
                } else event.replyEmbeds(menuFailed).setEphemeral(true).queue();
            }
        }
    }
}
