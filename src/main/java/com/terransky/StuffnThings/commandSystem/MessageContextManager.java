package com.terransky.StuffnThings.commandSystem;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.interfaces.IMessageContext;
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

import java.util.ArrayList;
import java.util.List;

public class MessageContextManager extends ListenerAdapter {
    private final List<IMessageContext> iMessageContexts = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(MessageContextManager.class);

    public MessageContextManager() {
    }

    @SuppressWarnings("unused")
    private void addContextMenu(IMessageContext iMessageContext) {
        boolean nameFound = iMessageContexts.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iMessageContext.getName()));

        if (nameFound) throw new IllegalArgumentException("A context menu with this name already exists");
        if (iMessageContexts.size() > Commands.MAX_MESSAGE_COMMANDS)
            throw new IndexOutOfBoundsException("You can only have at most %d message contexts".formatted(Commands.MAX_MESSAGE_COMMANDS));

        iMessageContexts.add(iMessageContext);
    }

    @Nullable
    private IMessageContext getMessageMenu(@NotNull String search) {
        for (IMessageContext menu : iMessageContexts) {
            if (menu.getName().equals(search)) {
                return menu;
            }
        }

        return null;
    }

    public List<CommandData> getCommandData() {
        final List<CommandData> commandData = new ArrayList<>();

        for (IMessageContext iMessageContext : iMessageContexts) {
            commandData.add(iMessageContext.commandData());
        }

        return commandData;
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        if (event.getGuild() == null) return;

        IMessageContext menu = getMessageMenu(event.getName());
        MessageEmbed menuFailed = new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription("An error occurred while executing that context menu!\nPlease contact <@" + Commons.config.get("OWNER_ID") + "> with the context menu that you used and when.")
            .setColor(Commons.defaultEmbedColor)
            .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
            .build();

        if (menu != null) {
            log.debug("Command \"" + menu.getName().toUpperCase() + "\" called on %s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()));
            try {
                menu.execute(event);
            } catch (Exception e) {
                log.debug("%s failed to execute on guild id %s".formatted(menu.getName(), event.getGuild().getId()));
                log.error("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(menuFailed).queue();
                } else event.replyEmbeds(menuFailed).setEphemeral(true).queue();
            }
        }
    }
}
