package com.terransky.stuffnthings.commandSystem;

import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.interfaces.IMessageContext;
import com.terransky.stuffnthings.utilities.CannedBotResponses;
import com.terransky.stuffnthings.utilities.Config;
import com.terransky.stuffnthings.utilities.EmbedColors;
import com.terransky.stuffnthings.utilities.LogList;
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
    private final List<IMessageContext> iMessageContexts = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(MessageContextManager.class);

    public MessageContextManager() {
    }

    /**
     * Add a {@link IMessageContext} object to be indexed and used.
     *
     * @param iMessageContext An {@link IMessageContext} object.
     * @throws IllegalArgumentException  If an {@link IMessageContext} with that name is already indexed.
     * @throws IndexOutOfBoundsException If {@code iMessageContexts} is more than the max message commands.
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
     * @return An {@link Optional} {@link IMessageContext}.
     */
    private Optional<IMessageContext> getMessageMenu(@NotNull String search) {
        for (IMessageContext menu : iMessageContexts) {
            if (menu.getName().equals(search)) {
                return Optional.of(menu);
            }
        }

        return Optional.empty();
    }

    /**
     * Get the command data of message contexts menus.
     *
     * @return Returns a list of {@link CommandData}.
     * @throws ParseException If the pattern used in {@code Metadata.getImplementationDate()} or {@code Metadata.getLastUpdated()} in an {@link IMessageContext}
     *                        is given an invalid date string.
     */
    public List<CommandData> getCommandData() throws ParseException {
        final List<CommandData> commandData = new ArrayList<>();

        for (IMessageContext iMessageContext : iMessageContexts.stream().filter(IMessageContext::isWorking).toList()) {
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
            CannedBotResponses.botIsGuildOnly(event);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        Optional<IMessageContext> ifMenu = getMessageMenu(event.getName());
        MessageEmbed menuFailed = new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription("An error occurred while executing that context menu!\nPlease report this event [here](%s).".formatted(Config.getConfig().get("BOT_ERROR_REPORT")))
            .setColor(EmbedColors.getError())
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build();

        if (ifMenu.isPresent()) {
            IMessageContext context = ifMenu.get();
            log.debug("Command \"" + context.getName().toUpperCase() + "\" called on %s [%d]".formatted(blob.getGuild().getName(), blob.getGuildIdLong()));
            try {
                context.execute(event, blob);
            } catch (Exception e) {
                log.debug("%s failed to execute on guild id %s".formatted(context.getName(), blob.getGuildId()));
                log.error("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
                LogList.error(Arrays.asList(e.getStackTrace()), MessageContextManager.class);
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(menuFailed).queue();
                } else event.replyEmbeds(menuFailed).setEphemeral(true).queue();
            }
        }
    }
}
