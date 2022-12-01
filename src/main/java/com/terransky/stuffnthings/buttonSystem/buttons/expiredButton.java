package com.terransky.stuffnthings.buttonSystem.buttons;

import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.interfaces.IButton;
import com.terransky.stuffnthings.utilities.EmbedColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class expiredButton implements IButton {
    @Override
    public String getName() {
        return "expired-button";
    }

    @Override
    public void execute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        event.replyEmbeds(
            new EmbedBuilder()
                .setTitle("Button Expired")
                .setDescription("This button has expired. Please issue the command again.")
                .setColor(EmbedColors.getError())
                .build()
        ).setEphemeral(true).queue();
    }
}
