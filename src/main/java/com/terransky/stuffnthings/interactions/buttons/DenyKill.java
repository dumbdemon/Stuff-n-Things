package com.terransky.stuffnthings.interactions.buttons;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interactions.modals.KillSuggest;
import com.terransky.stuffnthings.interfaces.interactions.IButton;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public class DenyKill implements IButton {
    @Override
    public String getName() {
        return KillSuggest.DENY_BUTTON;
    }

    @Override
    public void execute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        if (!event.getUser().getId().equals(Config.getDeveloperId())) {
            event.replyEmbeds(AcceptKill.youAreNotAllowed(event, blob)).setEphemeral(true).queue();
            return;
        }

        MessageEditData messageEditData = new MessageEditBuilder()
            .setContent("Kill Suggestion was denied.")
            .setComponents(new ArrayList<>())
            .build();

        event.getMessage().editMessage(messageEditData).queue();
    }
}
