package com.terransky.StuffnThings.commandSystem;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.interfaces.IUserContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class UserContextManager extends ListenerAdapter {
    private static final List<IUserContext> iMessageContexts = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(UserContextManager.class);

    public UserContextManager() {
    }

    @SuppressWarnings("unused")
    private void addContextMenu(IUserContext iUserContext) {
        boolean nameFound = iMessageContexts.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iUserContext.getName()));

        if (nameFound) throw new IllegalArgumentException("A context menu with this name already exists");

        if (iMessageContexts.size() > Commands.MAX_USER_COMMANDS)
            throw new IndexOutOfBoundsException("You can only have at most %d user contexts".formatted(Commands.MAX_USER_COMMANDS));
        else iMessageContexts.add(iUserContext);
    }

    @Nullable
    private IUserContext getUserMenu(@NotNull String search) {
        for (IUserContext menu : iMessageContexts) {
            if (menu.getName().equals(search)) {
                return menu;
            }
        }

        return null;
    }

    public List<CommandData> getCommandData() {
        final List<CommandData> commandData = new ArrayList<>();

        for (IUserContext iUserContext : iMessageContexts) {
            if (iUserContext.isWorking())
                commandData.add(iUserContext.getCommandData());
        }

        return commandData;
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        if (event.getGuild() == null) return;

        IUserContext menu = getUserMenu(event.getName());
        MessageEmbed menuFailed = new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription("An error occurred while executing that context menu!\nPlease contact <@" + Commons.CONFIG.get("OWNER_ID") + "> with the context menu that you used and when.")
            .setColor(Commons.DEFAULT_EMBED_COLOR)
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
