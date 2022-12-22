package com.terransky.stuffnthings.managers;

import com.terransky.stuffnthings.interfaces.ISelectMenuEntity;
import com.terransky.stuffnthings.utilities.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EntitySelectMenuManager extends ListenerAdapter {
    private final List<ISelectMenuEntity> iSelectMenuEntities = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(EntitySelectMenuManager.class);

    public EntitySelectMenuManager(@NotNull ISelectMenuEntity... iSelectMenuEntities) {
        for (ISelectMenuEntity iSelectMenuEntity : iSelectMenuEntities) {
            addMenu(iSelectMenuEntity);
        }
    }

    /**
     * Add an {@link ISelectMenuEntity} to be indexed and used.
     *
     * @param iSelectMenuEntity An {@link ISelectMenuEntity} object.
     */
    @SuppressWarnings("unused")
    private void addMenu(ISelectMenuEntity iSelectMenuEntity) {
        boolean menuFound = iSelectMenuEntities.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iSelectMenuEntity.getName()));

        if (menuFound) throw new IllegalArgumentException("A menu with this id already exists");

        iSelectMenuEntities.add(iSelectMenuEntity);
    }

    /**
     * Get an {@link ISelectMenuEntity} object to be used at {@code onSelectMenuInteraction()}
     *
     * @param search The {@link ISelectMenuEntity}'s ID.
     * @return An {@link Optional} of {@link ISelectMenuEntity}.
     */
    private Optional<ISelectMenuEntity> getMenu(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (ISelectMenuEntity iSelectMenuEntity : iSelectMenuEntities) {
            if (iSelectMenuEntity.getName().equals(toSearch)) {
                return Optional.of(iSelectMenuEntity);
            }
        }

        return Optional.empty();
    }

    /**
     * The main event handler for String type Select Menus.
     *
     * @param event A {@link EntitySelectInteractionEvent}.
     */
    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, Interactions.SELECT_MENU);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        Optional<ISelectMenuEntity> ifMenu = getMenu(event.getInteraction().getComponentId());
        MessageEmbed menuFailed = new EmbedBuilder()
            .setTitle("Oops")
            .setDescription(CannedResponses.INTERACTION_FAILED.getMessage(Interactions.SELECT_MENU))
            .setColor(EmbedColors.getError())
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build();

        if (ifMenu.isEmpty()) return;

        ISelectMenuEntity menu = ifMenu.get();
        log.debug("Select Menu %s called on %s [%d]".formatted(menu.getName().toUpperCase(), blob.getGuildName(), blob.getGuildIdLong()));
        try {
            menu.execute(event, blob);
        } catch (Exception e) {
            log.debug(event.getId() + " interaction failed on %s [%d]".formatted(blob.getGuildName(), blob.getGuildIdLong()));
            log.error("%s: %s".formatted(e.getClass().getName(), e.getCause()));
            LogList.error(Arrays.asList(e.getStackTrace()), log);
            if (event.isAcknowledged()) {
                event.getHook().sendMessageEmbeds(menuFailed).queue();
            } else event.replyEmbeds(menuFailed).setEphemeral(true).queue();
        }
    }
}
