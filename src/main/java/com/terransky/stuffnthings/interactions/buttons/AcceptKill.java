package com.terransky.stuffnthings.interactions.buttons;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.database.helpers.KillStorage;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interactions.modals.KillSuggest;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ButtonInteraction;
import com.terransky.stuffnthings.utilities.command.BotColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.StandardResponse;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AcceptKill {

    private static final String RANDOM_NAME_READABLE =
        WordUtils.capitalize(KillSuggest.ACCEPT_RANDOM_BUTTON.toLowerCase().replaceAll("-", " "));
    private static final String TARGET_NAME_READABLE =
        WordUtils.capitalize(KillSuggest.ACCEPT_TARGET_BUTTON.toLowerCase().replaceAll("-", " "));

    @NotNull
    public static Container youAreNotAllowed(@NotNull GenericInteractionCreateEvent event) {
        return StandardResponse.getResponseContainer("Action Denied", List.of(
            TextDisplay.ofFormat("You're not allowed to do that, %s", event.getUser().getAsMention()),
            MediaGallery.of(MediaGalleryItem.fromUrl("https://media.tenor.com/KkRrym9X09EAAAAd/i-dont-think-thats-allowed-ryan-bailey.gif"))
        ), BotColors.ERROR);
    }

    private static void doExecute(@NotNull ButtonInteractionEvent event, boolean isRandom) {
        event.deferEdit().queue();
        if (!event.getUser().getId().equals(StuffNThings.getConfig().getCore().getOwnerId())) {
            event.replyComponents(youAreNotAllowed(event)).setEphemeral(true).queue();
            return;
        }

        if (!DatabaseManager.INSTANCE.addKillString(isRandom ? KillStorage.RANDOM : KillStorage.TARGET, "0", event.getMessage().getContentRaw())) {
            event.replyComponents(
                StandardResponse.getResponseContainer(isRandom ? RANDOM_NAME_READABLE : TARGET_NAME_READABLE,
                    "Unable to upload suggestion to Database. Please check logs.",
                    BotColors.ERROR
                )
            ).setEphemeral(true).queue();
            return;
        }

        MessageEditData messageEditData = new MessageEditBuilder()
            .setContent("Kill Suggestion was accepted.")
            .setComponents(new ArrayList<>())
            .useComponentsV2(false)
            .build();

        event.getMessage().editMessage(messageEditData).useComponentsV2(false).queue();
    }

    public static class Random extends ButtonInteraction {
        public Random() {
            super(KillSuggest.ACCEPT_RANDOM_BUTTON);
        }

        @Override
        public void execute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
            doExecute(event, true);
        }
    }

    public static class Target extends ButtonInteraction {
        public Target() {
            super(KillSuggest.ACCEPT_TARGET_BUTTON);
        }

        @Override
        public void execute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
            doExecute(event, false);
        }
    }
}
