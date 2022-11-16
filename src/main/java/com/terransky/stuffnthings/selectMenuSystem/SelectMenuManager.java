package com.terransky.stuffnthings.selectMenuSystem;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.interfaces.ISelectMenu;
import net.dv8tion.jda.api.EmbedBuilder;
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
            Commons.botIsGuildOnly(event);
            return;
        }

        Optional<ISelectMenu> ifMenu = getMenu(event.getId());
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle("Oops")
            .setDescription("An error occurred while loading the menu!\nPlease report this event [here](%s).".formatted(Commons.getConfig().get("BOT_ERROR_REPORT")))
            .setColor(Commons.getDefaultEmbedColor())
            .setFooter(event.getUser().getAsTag());

        if (ifMenu.isPresent()) {
            ISelectMenu menu = ifMenu.get();
            String origins = event.isFromGuild() ? "%s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()) : event.getUser().getAsTag() + "'s private channel";
            log.debug("Command " + menu.getName().toUpperCase() + " called on " + origins);
            try {
                menu.execute(event);
            } catch (Exception e) {
                log.error("%s: %s".formatted(e.getClass().getName(), e.getCause()));
                Commons.listPrinter(Arrays.asList(e.getStackTrace()), SelectMenuManager.class);
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(eb.build()).queue();
                } else event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            }
        }
    }
}
