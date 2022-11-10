package com.terransky.StuffnThings.buttonSystem;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.buttonSystem.buttons.expiredButton;
import com.terransky.StuffnThings.buttonSystem.buttons.getMoreDadJokes;
import com.terransky.StuffnThings.interfaces.IButton;
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
import java.util.List;

public class ButtonManager extends ListenerAdapter {
    private final Dotenv config = Dotenv.configure().load();
    private final Logger log = LoggerFactory.getLogger(ButtonManager.class);
    private final List<IButton> iButtonList = new ArrayList<>();

    public ButtonManager() {
        addButton(new expiredButton());
        addButton(new getMoreDadJokes());
    }

    @SuppressWarnings("unused")
    private void addButton(IButton iButton) {
        boolean buttonFound = iButtonList.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iButton.getName()));

        if (buttonFound) throw new IllegalArgumentException("A button with that name already exists");

        iButtonList.add(iButton);
    }

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

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getGuild() == null || event.getButton().getId() == null) return;

        IButton butt = getButton(event.getButton().getId());
        MessageEmbed buttonFailed = new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription("An error occurred while executing the button!\nPlease contact <@" + config.get("OWNER_ID") + "> with the button you clicked/tapped on and when.")
            .setColor(Commons.DEFAULT_EMBED_COLOR)
            .setFooter(event.getUser().getAsTag())
            .build();

        if (butt != null) {
            log.debug("Button " + butt.getName().toUpperCase() + " called on %s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()));
            try {
                butt.execute(event);
            } catch (Exception e) {
                log.debug(event.getButton().getId() + " interaction on %s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()));
                log.error(e.getClass().getName() + ": " + e.getMessage());
                event.replyEmbeds(buttonFailed).setEphemeral(true).queue();
            }
        }
    }
}
