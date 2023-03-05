package com.terransky.stuffnthings.interactions.buttons;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.IButton;
import com.terransky.stuffnthings.utilities.command.EmbedColor;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ExpiredButton implements IButton {
    @Override
    public String getName() {
        return "expired-button";
    }

    @Override
    public void execute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        event.replyEmbeds(
            blob.getStandardEmbed("Button Expired", EmbedColor.ERROR)
                .setDescription("This button has expired. Please issue the command again.")
                .build()
        ).setEphemeral(true).queue();
    }
}
