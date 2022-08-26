package com.terransky.TestingBot.buttonSystem;

import com.terransky.TestingBot.Commons;
import com.terransky.TestingBot.buttonSystem.buttons.getDadJoke;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ButtonManager extends ListenerAdapter {
    private final Color embedColor = new Commons().defaultEmbedColor;
    private final Dotenv config = Dotenv.configure().load();
    private final Logger log = LoggerFactory.getLogger(ButtonManager.class);
    private final List<IButton> iButtonList = new ArrayList<>();

    public ButtonManager() {
        addButton(new getDadJoke());
    }

    private void addButton(IButton iButton) {
        boolean buttonFound = iButtonList.stream().anyMatch(it -> it.getButtonID().equalsIgnoreCase(iButton.getButtonID()));

        if (buttonFound) throw new IllegalArgumentException("A button with that name already exists");

        iButtonList.add(iButton);
    }

    @Nullable
    private IButton getButton(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (IButton butt : iButtonList) {
            if (butt.getButtonID().equals(toSearch)) {
                return butt;
            }
        }

        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        IButton butt = getButton(Objects.requireNonNull(event.getButton().getId()));
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Oops!")
                .setDescription("An error occurred while executing the button!\nPlease contact <@" + config.get("OWNER_ID") + "> with the button you click/tapped on and when.")
                .setColor(embedColor)
                .setFooter(event.getUser().getAsTag());

        if (butt != null) {
            String origins = event.isFromGuild() ? "%s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()) : event.getUser().getAsTag() + "'s private channel";
            log.debug("Button " + butt.getButtonID().toUpperCase() + " called on " + origins);
            try {
                butt.buttonExecute(event);
            } catch (Exception e) {
                log.debug(event.getButton().getId() + " interaction on " + origins);
                log.error(e.getClass().getName() + ": " + e.getMessage());
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            }
        }
    }
}
