package com.terransky.stuffnthings.commandSystem;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.interfaces.IUserContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
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

public class UserContextManager extends ListenerAdapter {
    private static final List<IUserContext> iMessageContexts = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(UserContextManager.class);

    public UserContextManager() {
    }

    /**
     * Add a {@link IUserContext} object to be indexed and used.
     *
     * @param iUserContext An {@link IUserContext} object.
     * @throws IllegalArgumentException  If an {@link IUserContext} with that name is already indexed.
     * @throws IndexOutOfBoundsException If {@code iMessageContexts} is more than the max user contexts.
     */
    @SuppressWarnings("unused")
    private void addContextMenu(IUserContext iUserContext) {
        boolean nameFound = iMessageContexts.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iUserContext.getName()));

        if (nameFound) throw new IllegalArgumentException("A context menu with this name already exists");

        if (iMessageContexts.size() > Commands.MAX_USER_COMMANDS)
            throw new IndexOutOfBoundsException("You can only have at most %d user contexts".formatted(Commands.MAX_USER_COMMANDS));
        else iMessageContexts.add(iUserContext);
    }

    /**
     * Get the {@link IUserContext} object for execution at {@code onUserContextInteraction()}.
     *
     * @param search An {@link Optional} of {@link IUserContext}.
     */
    private Optional<IUserContext> getUserMenu(@NotNull String search) {
        for (IUserContext menu : iMessageContexts) {
            if (menu.getName().equals(search)) {
                return Optional.of(menu);
            }
        }

        return Optional.empty();
    }

    /**
     * Get the command data of User Context Menus.
     *
     * @return Returns a list of {@link CommandData}.
     * @throws ParseException If the pattern used in {@code Metadata.getImplementationDate()} or {@code Metadata.getLastUpdated()} in an {@link IUserContext}
     *                        is given an invalid date string.
     */
    public List<CommandData> getCommandData() throws ParseException {
        final List<CommandData> commandData = new ArrayList<>();

        for (IUserContext iUserContext : iMessageContexts.stream().filter(IUserContext::isWorking).toList()) {
            commandData.add(iUserContext.getCommandData());
        }

        return commandData;
    }

    /**
     * The main event handler for all User Context Menus.
     *
     * @param event A {@link UserContextInteractionEvent}
     */
    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        if (event.getGuild() == null) {
            Commons.botIsGuildOnly(event);
            return;
        }

        Optional<IUserContext> ifMenu = getUserMenu(event.getName());
        MessageEmbed menuFailed = new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription("An error occurred while executing that context menu!\nPlease contact <@" + Commons.getConfig().get("OWNER_ID") + "> with the context menu that you used and when.")
            .setColor(Commons.getDefaultEmbedColor())
            .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
            .build();

        if (ifMenu.isPresent()) {
            IUserContext context = ifMenu.get();
            log.debug("Command \"" + context.getName().toUpperCase() + "\" called on %s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()));
            try {
                context.execute(event);
            } catch (Exception e) {
                log.debug("%s failed to execute on guild id %s".formatted(context.getName(), event.getGuild().getId()));
                log.error("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
                Commons.listPrinter(Arrays.asList(e.getStackTrace()), UserContextManager.class);
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(menuFailed).queue();
                } else event.replyEmbeds(menuFailed).setEphemeral(true).queue();
            }
        }
    }
}
