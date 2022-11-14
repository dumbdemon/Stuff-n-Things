package com.terransky.stuffnthings.selectMenuSystem;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.interfaces.ISelectMenu;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
     * @return An {@link ISelectMenu} object or null.
     */
    @Nullable
    private ISelectMenu getMenu(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (ISelectMenu iSelectMenu : iSelectMenus) {
            if (iSelectMenu.getName().equals(toSearch)) {
                return iSelectMenu;
            }
        }

        return null;
    }

    /**
     * @param event A {@link SelectMenuInteractionEvent}
     */
    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        if (event.getGuild() == null) {
            Commons.botIsGuildOnly(event);
            return;
        }

        ISelectMenu menu = getMenu(event.getId());
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle("Oops")
            .setDescription("An error occurred while loading the menu!\nPlease <@" + Commons.getConfig().get("OWNER_ID") + "> know what command you used and when.")
            .setColor(Commons.getDefaultEmbedColor())
            .setFooter(event.getUser().getAsTag());

        if (menu != null) {
            String origins = event.isFromGuild() ? "%s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()) : event.getUser().getAsTag() + "'s private channel";
            log.debug("Command " + menu.getName().toUpperCase() + " called on " + origins);
            try {
                menu.execute(event);
            } catch (Exception e) {
                log.error("%s: %s".formatted(e.getClass().getName(), e.getCause()));
                e.printStackTrace();
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(eb.build()).queue();
                } else event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            }
        }
    }
}
