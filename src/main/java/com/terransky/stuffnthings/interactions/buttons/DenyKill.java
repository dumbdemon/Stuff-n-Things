package com.terransky.stuffnthings.interactions.buttons;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interactions.modals.KillSuggest;
import com.terransky.stuffnthings.interfaces.interactions.ButtonInteraction;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public class DenyKill extends ButtonInteraction {

    public DenyKill() {
        super(KillSuggest.DENY_BUTTON);
    }

    @Override
    public void execute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        event.deferEdit().queue();
        if (!event.getUser().getId().equals(StuffNThings.getConfig().getCore().getOwnerId())) {
            event.replyComponents(AcceptKill.youAreNotAllowed(event)).setEphemeral(true).queue();
            return;
        }

        MessageEditData messageEditData = new MessageEditBuilder()
            .setContent("Kill Suggestion was denied.")
            .setComponents(new ArrayList<>())
            .useComponentsV2(false)
            .build();

        event.getMessage().editMessage(messageEditData).queue();
    }
}
