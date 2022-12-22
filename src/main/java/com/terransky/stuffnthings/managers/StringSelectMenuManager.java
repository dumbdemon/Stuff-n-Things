package com.terransky.stuffnthings.managers;

import com.terransky.stuffnthings.interfaces.ISelectMenuString;
import com.terransky.stuffnthings.utilities.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StringSelectMenuManager extends ListenerAdapter {
    private final List<ISelectMenuString> iSelectMenuStrings = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(StringSelectMenuManager.class);

    public StringSelectMenuManager(@NotNull ISelectMenuString... iSelectMenuStrings) {
        for (ISelectMenuString iSelectMenuString : iSelectMenuStrings) {
            addMenu(iSelectMenuString);
        }
    }

    /**
     * Add an {@link ISelectMenuString} to be indexed and used.
     *
     * @param iSelectMenuString An {@link ISelectMenuString} object.
     */
    @SuppressWarnings("unused")
    private void addMenu(ISelectMenuString iSelectMenuString) {
        boolean menuFound = iSelectMenuStrings.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iSelectMenuString.getName()));

        if (menuFound) throw new IllegalArgumentException("A menu with this id already exists");

        iSelectMenuStrings.add(iSelectMenuString);
    }

    /**
     * Get an {@link ISelectMenuString} object to be used at {@code onSelectMenuInteraction()}
     *
     * @param search The {@link ISelectMenuString}'s ID.
     * @return An {@link Optional} of {@link ISelectMenuString}.
     */
    private Optional<ISelectMenuString> getMenu(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (ISelectMenuString iSelectMenuString : iSelectMenuStrings) {
            if (iSelectMenuString.getName().equals(toSearch)) {
                return Optional.of(iSelectMenuString);
            }
        }

        return Optional.empty();
    }

    /**
     * The main event handler for String type Select Menus.
     *
     * @param event A {@link StringSelectInteractionEvent}.
     */
    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, Interactions.SELECT_MENU);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        List<Optional<ISelectMenuString>> ifMenus = new ArrayList<>();
        for (String id : event.getInteraction().getValues()) {
            ifMenus.add(getMenu(id));
        }
        String componentId = event.getComponentId();

        MessageEmbed menuFailed = new EmbedBuilder()
            .setTitle("Oops")
            .setDescription(CannedResponses.INTERACTION_FAILED.getMessage(Interactions.SELECT_MENU))
            .setColor(EmbedColors.getError())
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build();

        for (Optional<ISelectMenuString> ifMenu : ifMenus) {
            if (ifMenu.isPresent()) {
                ISelectMenuString menu = ifMenu.get();
                log.debug("Select Menu %s[%s] called on %s [%d]".formatted(componentId.toUpperCase(), menu.getName().toUpperCase(),
                    blob.getGuildName(), blob.getGuildIdLong()));
                try {
                    menu.execute(event, blob);
                } catch (Exception e) {
                    log.debug("%s[%s] interaction failed on %s [%d]".formatted(componentId.toUpperCase(), menu.getName().toUpperCase(),
                        blob.getGuildName(), blob.getGuildIdLong()));
                    log.error("%s: %s".formatted(e.getClass().getName(), e.getCause()));
                    LogList.error(List.of(e.getStackTrace()), log);
                    if (event.isAcknowledged()) {
                        event.getHook().sendMessageEmbeds(menuFailed).queue();
                    } else event.replyEmbeds(menuFailed).setEphemeral(true).queue();
                }
            }
        }
    }
}
