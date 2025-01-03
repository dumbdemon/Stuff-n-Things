package com.terransky.stuffnthings.interactions.buttons;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.database.helpers.KillStorage;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interactions.modals.KillSuggest;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.IButton;
import com.terransky.stuffnthings.utilities.command.EmbedColor;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public class AcceptKill {

    private static final String RANDOM_NAME_READABLE =
        WordUtils.capitalize(KillSuggest.ACCEPT_RANDOM_BUTTON.toLowerCase().replaceAll("-", " "));
    private static final String TARGET_NAME_READABLE =
        WordUtils.capitalize(KillSuggest.ACCEPT_TARGET_BUTTON.toLowerCase().replaceAll("-", " "));

    @NotNull
    public static MessageEmbed youAreNotAllowed(@NotNull GenericInteractionCreateEvent event, @NotNull EventBlob blob) {
        return blob.getStandardEmbed("Action Denied", EmbedColor.ERROR)
            .setDescription(String.format("You're not allowed to do that, %s", event.getUser().getAsMention()))
            .setImage("https://media.tenor.com/KkRrym9X09EAAAAd/i-dont-think-thats-allowed-ryan-bailey.gif")
            .build();
    }

    private static void doExecute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob, boolean isRandom) {
        if (!event.getUser().getId().equals(StuffNThings.getConfig().getCore().getOwnerId())) {
            event.replyEmbeds(youAreNotAllowed(event, blob)).setEphemeral(true).queue();
            return;
        }

        if (!DatabaseManager.INSTANCE.addKillString(isRandom ? KillStorage.RANDOM : KillStorage.TARGET, "0", event.getMessage().getContentRaw())) {
            event.replyEmbeds(
                blob.getStandardEmbed(isRandom ? RANDOM_NAME_READABLE : TARGET_NAME_READABLE, EmbedColor.ERROR)
                    .setDescription("Unable to upload suggestion to Database. Please check logs.")
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        MessageEditData messageEditData = new MessageEditBuilder()
            .setContent("Kill Suggestion was accepted.")
            .setComponents(new ArrayList<>())
            .build();

        event.getMessage().editMessage(messageEditData).queue();
    }

    public static class Random implements IButton {

        @Override
        public String getName() {
            return KillSuggest.ACCEPT_RANDOM_BUTTON;
        }

        @Override
        public void execute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
            doExecute(event, blob, true);
        }
    }

    public static class Target implements IButton {
        @Override
        public String getName() {
            return KillSuggest.ACCEPT_TARGET_BUTTON;
        }

        @Override
        public void execute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
            doExecute(event, blob, false);
        }
    }
}
