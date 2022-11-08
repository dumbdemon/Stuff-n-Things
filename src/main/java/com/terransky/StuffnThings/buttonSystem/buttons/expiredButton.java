package com.terransky.StuffnThings.buttonSystem.buttons;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.buttonSystem.IButton;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class expiredButton implements IButton {
    @Override
    public String getID() {
        return "expired-button";
    }

    @Override
    public void execute(@NotNull ButtonInteractionEvent event) throws Exception {
        event.replyEmbeds(
            new EmbedBuilder()
                .setTitle("Button Expired")
                .setDescription("This button has expired. Please issue the command again.")
                .setColor(Commons.DEFAULT_EMBED_COLOR)
                .build()
        ).setEphemeral(true).queue();
    }
}
