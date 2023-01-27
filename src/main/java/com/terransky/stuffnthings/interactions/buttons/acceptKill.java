package com.terransky.stuffnthings.interactions.buttons;

import com.terransky.stuffnthings.database.helpers.KillStorage;
import com.terransky.stuffnthings.interactions.modals.killSuggest;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.IButton;
import com.terransky.stuffnthings.utilities.command.EmbedColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class acceptKill {

    private static final String RANDOM_NAME_READABLE =
        WordUtils.capitalize(killSuggest.ACCEPT_RANDOM_BUTTON.toLowerCase().replaceAll("-", " "));
    private static final String TARGET_NAME_READABLE =
        WordUtils.capitalize(killSuggest.ACCEPT_TARGET_BUTTON.toLowerCase().replaceAll("-", " "));

    @NotNull
    public static MessageEmbed youAreNotAllowed(@NotNull GenericInteractionCreateEvent event, @NotNull EventBlob blob) {
        return new EmbedBuilder()
            .setTitle("Action Denied")
            .setDescription(String.format("You're not allowed to do that, %s", event.getUser().getAsMention()))
            .setImage("https://media.tenor.com/KkRrym9X09EAAAAd/i-dont-think-thats-allowed-ryan-bailey.gif")
            .setColor(EmbedColors.getError())
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build();
    }

    private static void doExecute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob, boolean isRandom) {
        if (!event.getUser().getId().equals(Config.getDeveloperId())) {
            event.replyEmbeds(youAreNotAllowed(event, blob)).setEphemeral(true).queue();
            return;
        }

        if (!DatabaseManager.INSTANCE.addKillString(isRandom ? KillStorage.RANDOM : KillStorage.TARGET, event.getMessage().getContentRaw(), "0")) {
            event.replyEmbeds(
                new EmbedBuilder()
                    .setTitle(isRandom ? RANDOM_NAME_READABLE : TARGET_NAME_READABLE)
                    .setDescription("Unable to upload suggestion to Database. Please check logs.")
                    .setColor(EmbedColors.getError())
                    .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
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

    public static class random implements IButton {

        @Override
        public String getName() {
            return killSuggest.ACCEPT_RANDOM_BUTTON;
        }

        @Override
        public void execute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob) throws Exception {
            doExecute(event, blob, true);
        }
    }

    public static class target implements IButton {
        @Override
        public String getName() {
            return killSuggest.ACCEPT_TARGET_BUTTON;
        }

        @Override
        public void execute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob) throws Exception {
            doExecute(event, blob, false);
        }
    }
}
