package com.terransky.stuffnthings.buttonSystem;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.buttonSystem.buttons.expiredButton;
import com.terransky.stuffnthings.buttonSystem.buttons.getMoreDadJokes;
import com.terransky.stuffnthings.interfaces.IButton;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ButtonManager extends ListenerAdapter {
    private final Dotenv config = Dotenv.configure().load();
    private final Logger log = LoggerFactory.getLogger(ButtonManager.class);
    private final List<IButton> iButtonList = new ArrayList<>();

    public ButtonManager() {
        addButton(new expiredButton());
        addButton(new getMoreDadJokes());
    }

    /**
     * Add a {@link IButton} to be indexed and used.
     *
     * @param iButton An {@link IButton} object.
     */
    @SuppressWarnings("unused")
    private void addButton(IButton iButton) {
        boolean buttonFound = iButtonList.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iButton.getName()));

        if (buttonFound) throw new IllegalArgumentException("A button with that name already exists");

        iButtonList.add(iButton);
    }

    /**
     * Get an {@link IButton} object to be used at {@code onButtonInteraction()}.
     *
     * @param search The {@link IButton} ID.
     * @return An {@link IButton} object.
     */
    @Nullable
    private IButton getButton(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (IButton butt : iButtonList) {
            if (butt.getName().equals(toSearch)) {
                return butt;
            }
        }

        return null;
    }

    /**
     * The main event handler for all buttons.
     *
     * @param event A {@link ButtonInteractionEvent}.
     */
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId() == null) return;
        else if (event.getGuild() == null) {
            Commons.botIsGuildOnly(event);
            return;
        }

        IButton butt = getButton(event.getButton().getId());
        MessageEmbed buttonFailed = new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription("An error occurred while executing the button!\nPlease contact <@" + config.get("OWNER_ID") + "> with the button you clicked/tapped on and when.")
            .setColor(Commons.getDefaultEmbedColor())
            .setFooter(event.getUser().getAsTag())
            .build();

        if (butt != null) {
            log.debug("Button " + butt.getName().toUpperCase() + " called on %s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()));
            try {
                butt.execute(event);
            } catch (Exception e) {
                log.debug(event.getButton().getId() + " interaction on %s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()));
                log.error(e.getClass().getName() + ": " + e.getMessage());
                Commons.listPrinter(Arrays.asList(e.getStackTrace()), ButtonManager.class);
                event.replyEmbeds(buttonFailed).setEphemeral(true).queue();
            }
        }
    }
}
