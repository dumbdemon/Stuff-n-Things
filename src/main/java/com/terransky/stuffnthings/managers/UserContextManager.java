package com.terransky.stuffnthings.managers;

import com.terransky.stuffnthings.interfaces.ICommandUser;
import com.terransky.stuffnthings.utilities.cannedAgenda.GuildOnly;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.EmbedColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.Interactions;
import com.terransky.stuffnthings.utilities.general.LogList;
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
    private static final List<ICommandUser> iMessageContexts = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(UserContextManager.class);

    public UserContextManager(@NotNull ICommandUser... iCommandUsers) {
        for (ICommandUser iCommandUser : iCommandUsers) {
            addContextMenu(iCommandUser);
        }
    }

    /**
     * Add a {@link ICommandUser} object to be indexed and used.
     *
     * @param iCommandUser An {@link ICommandUser} object.
     * @throws IllegalArgumentException  If an {@link ICommandUser} with that name is already indexed.
     * @throws IndexOutOfBoundsException If {@link UserContextManager#iMessageContexts} has more than the {@link Commands#MAX_USER_COMMANDS}.
     */
    @SuppressWarnings("unused")
    private void addContextMenu(ICommandUser iCommandUser) {
        boolean nameFound = iMessageContexts.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iCommandUser.getName()));

        if (nameFound) throw new IllegalArgumentException("A context menu with this name already exists");

        if (iMessageContexts.size() > Commands.MAX_USER_COMMANDS)
            throw new IndexOutOfBoundsException("You can only have at most %d user contexts".formatted(Commands.MAX_USER_COMMANDS));
        else iMessageContexts.add(iCommandUser);
    }

    /**
     * Get the {@link ICommandUser} object for execution at {@code onUserContextInteraction()}.
     *
     * @param search An {@link Optional} of {@link ICommandUser}.
     */
    private Optional<ICommandUser> getUserMenu(@NotNull String search) {
        for (ICommandUser menu : iMessageContexts) {
            if (menu.getName().equals(search)) {
                return Optional.of(menu);
            }
        }

        return Optional.empty();
    }

    /**
     * Get the command data of User Context Menus.
     * <p>
     * If a {@link ParseException} occurs, it will not be pushed.
     *
     * @return Returns a list of {@link CommandData}.
     */
    public List<CommandData> getCommandData() {
        final List<CommandData> commandData = new ArrayList<>();

        for (ICommandUser iCommandUser : iMessageContexts.stream().filter(ICommandUser::isWorking).toList()) {
            try {
                commandData.add(iCommandUser.getCommandData());
            } catch (ParseException e) {
                log.warn("The date formatting in %s is invalid and will not be pushed.".formatted(iCommandUser.getName().toUpperCase()));
            }
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
            GuildOnly.interactionResponse(event, Interactions.CONTEXT_USER);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        Optional<ICommandUser> ifMenu = getUserMenu(event.getName());
        MessageEmbed menuFailed = new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription(Responses.INTERACTION_FAILED.getMessage(Interactions.CONTEXT_USER))
            .setColor(EmbedColors.getError())
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build();

        if (ifMenu.isPresent()) {
            ICommandUser context = ifMenu.get();
            log.debug("Command \"" + context.getName().toUpperCase() + "\" called on %s [%d]".formatted(blob.getGuildName(), blob.getGuildIdLong()));
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
