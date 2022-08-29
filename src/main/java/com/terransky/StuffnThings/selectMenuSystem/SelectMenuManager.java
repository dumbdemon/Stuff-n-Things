package com.terransky.StuffnThings.selectMenuSystem;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.selectMenuSystem.selectMenus.testMenu;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SelectMenuManager extends ListenerAdapter {
    private final List<ISelectMenu> iSelectMenus = new ArrayList<>();
    private final Color embedColor = new Commons().defaultEmbedColor;
    private final Dotenv config = Dotenv.configure().load();
    private final Logger log = LoggerFactory.getLogger(SelectMenuManager.class);

    public SelectMenuManager() {
        addMenu(new testMenu());
    }

    private void addMenu(ISelectMenu iSelectMenu) {
        boolean menuFound = iSelectMenus.stream().anyMatch(it -> it.getMenuID().equalsIgnoreCase(iSelectMenu.getMenuID()));

        if (menuFound) throw new IllegalArgumentException("A menu with this id already exists");

        iSelectMenus.add(iSelectMenu);
    }

    @Nullable
    private ISelectMenu getMenu(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (ISelectMenu iSelectMenu : iSelectMenus) {
            if (iSelectMenu.getMenuID().equals(toSearch)) {
                return iSelectMenu;
            }
        }

        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        ISelectMenu menu = getMenu(event.getId());
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Oops")
                .setDescription("An error occurred while loading the menu!\nPlease <@" + config.get("OWNER_ID") + "> know what command you used and when.")
                .setColor(embedColor)
                .setFooter(event.getUser().getAsTag());

        if (menu != null) {
            String origins = event.isFromGuild() ? "%s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()) : event.getUser().getAsTag() + "'s private channel";
            log.debug("Command " + menu.getMenuID().toUpperCase() + " called on " + origins);
            try {
                menu.menuExecute(event);
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
