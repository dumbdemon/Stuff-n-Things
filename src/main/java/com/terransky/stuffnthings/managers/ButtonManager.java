package com.terransky.stuffnthings.managers;

import com.terransky.stuffnthings.interfaces.IButton;
import com.terransky.stuffnthings.utilities.*;
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
    private final Logger log = LoggerFactory.getLogger(ButtonManager.class);
    private final List<IButton> iButtonList = new ArrayList<>();

    public ButtonManager(@NotNull IButton... iButtons) {
        for (IButton iButton : iButtons) {
            addButton(iButton);
        }
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
     * @param search The {@link IButton IButton's} ID.
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
            GuildOnly.interactionResponse(event, Interactions.BUTTON);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        IButton butt = getButton(event.getButton().getId());
        MessageEmbed buttonFailed = new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription(CannedResponses.INTERACTION_FAILED.getMessage(Interactions.BUTTON))
            .setColor(EmbedColors.getDefault())
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build();

        if (butt != null) {
            log.debug("Button " + butt.getName().toUpperCase() + " called on %s [%d]".formatted(blob.getGuildName(), blob.getGuildIdLong()));
            try {
                butt.execute(event, blob);
            } catch (Exception e) {
                log.debug(event.getButton().getId() + " interaction failed on %s [%d]".formatted(blob.getGuildName(), blob.getGuildIdLong()));
                log.error(e.getClass().getName() + ": " + e.getMessage());
                LogList.error(Arrays.asList(e.getStackTrace()), log);
                event.replyEmbeds(buttonFailed).setEphemeral(true).queue();
            }
        }
    }
}
