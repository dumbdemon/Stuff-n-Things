package com.terransky.stuffnthings.selectMenuSystem;

import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.interfaces.ISelectMenu;
import com.terransky.stuffnthings.utilities.CannedBotResponses;
import com.terransky.stuffnthings.utilities.Config;
import com.terransky.stuffnthings.utilities.EmbedColors;
import com.terransky.stuffnthings.utilities.LogList;
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

public class SelectMenuManager extends ListenerAdapter {
    private final List<ISelectMenu> iSelectMenus = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(SelectMenuManager.class);

    public SelectMenuManager() {
    }

    /**
     * Add an {@link ISelectMenu} to be indexed and used.
     *
     * @param iSelectMenu An {@link ISelectMenu} object.
     */
    @SuppressWarnings("unused")
    private void addMenu(ISelectMenu iSelectMenu) {
        boolean menuFound = iSelectMenus.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iSelectMenu.getName()));

        if (menuFound) throw new IllegalArgumentException("A menu with this id already exists");

        iSelectMenus.add(iSelectMenu);
    }

    /**
     * Get an {@link ISelectMenu} object to be used at {@code onSelectMenuInteraction()}
     *
     * @param search The {@link ISelectMenu}'s ID.
     * @return An {@link Optional} of {@link ISelectMenu}.
     */
    private Optional<ISelectMenu> getMenu(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (ISelectMenu iSelectMenu : iSelectMenus) {
            if (iSelectMenu.getName().equals(toSearch)) {
                return Optional.of(iSelectMenu);
            }
        }

        return Optional.empty();
    }

    /**
     * @param event A {@link EntitySelectInteractionEvent}
     */
    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        if (event.getGuild() == null) {
            CannedBotResponses.botIsGuildOnly(event);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        Optional<ISelectMenu> ifMenu = getMenu(event.getId());
        MessageEmbed menuFailed = new EmbedBuilder()
            .setTitle("Oops")
            .setDescription("An error occurred while loading the menu!\nPlease report this event [here](%s).".formatted(Config.getConfig().get("BOT_ERROR_REPORT")))
            .setColor(EmbedColors.getError())
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build();

        if (ifMenu.isPresent()) {
            ISelectMenu menu = ifMenu.get();
            log.debug("Select Menu %s called on %s [%d]".formatted(menu.getName().toUpperCase(), blob.getGuildName(), blob.getGuildIdLong()));
            try {
                menu.execute(event, blob);
            } catch (Exception e) {
                log.debug(event.getId() + " interaction failed on %s [%d]".formatted(blob.getGuildName(), blob.getGuildIdLong()));
                log.error("%s: %s".formatted(e.getClass().getName(), e.getCause()));
                LogList.error(Arrays.asList(e.getStackTrace()), SelectMenuManager.class);
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(menuFailed).queue();
                } else event.replyEmbeds(menuFailed).setEphemeral(true).queue();
            }
        }
    }
}
