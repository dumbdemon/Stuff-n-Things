package com.terransky.stuffnthings.interactions.buttons;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ButtonInteraction;
import com.terransky.stuffnthings.utilities.command.BotColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.StandardResponse;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ExpiredButton extends ButtonInteraction {

    public ExpiredButton() {
        super("expired-button");
    }

    @Override
    public void execute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        event.replyComponents(
            StandardResponse.getResponseContainer("Button Expired", "This button has expired. Please issue the command again.", BotColors.ERROR)
        ).setEphemeral(true).queue();
    }
}
