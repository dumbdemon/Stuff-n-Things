package com.terransky.stuffnthings.selectMenuSystem;

import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.interfaces.IStringSelectMenu;
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
    private final List<IStringSelectMenu> iStringSelectMenus = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(StringSelectMenuManager.class);

    public StringSelectMenuManager() {
    }

    /**
     * Add an {@link IStringSelectMenu} to be indexed and used.
     *
     * @param iStringSelectMenu An {@link IStringSelectMenu} object.
     */
    @SuppressWarnings("unused")
    private void addMenu(IStringSelectMenu iStringSelectMenu) {
        boolean menuFound = iStringSelectMenus.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iStringSelectMenu.getName()));

        if (menuFound) throw new IllegalArgumentException("A menu with this id already exists");

        iStringSelectMenus.add(iStringSelectMenu);
    }

    /**
     * Get an {@link IStringSelectMenu} object to be used at {@code onSelectMenuInteraction()}
     *
     * @param search The {@link IStringSelectMenu}'s ID.
     * @return An {@link Optional} of {@link IStringSelectMenu}.
     */
    private Optional<IStringSelectMenu> getMenu(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (IStringSelectMenu iStringSelectMenu : iStringSelectMenus) {
            if (iStringSelectMenu.getName().equals(toSearch)) {
                return Optional.of(iStringSelectMenu);
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

        List<Optional<IStringSelectMenu>> ifMenus = new ArrayList<>();
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

        for (Optional<IStringSelectMenu> ifMenu : ifMenus) {
            if (ifMenu.isPresent()) {
                IStringSelectMenu menu = ifMenu.get();
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
