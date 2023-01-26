package com.terransky.stuffnthings.interactions.buttons;

import com.terransky.stuffnthings.database.DatabaseManager;
import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.interactions.modals.killSuggest;
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

public class acceptKill implements IButton {

    @NotNull
    public static MessageEmbed youAreNotAllowed(@NotNull GenericInteractionCreateEvent event, @NotNull EventBlob blob) {
        return new EmbedBuilder()
            .setTitle(WordUtils.capitalize(killSuggest.ACCEPT_BUTTON.toLowerCase().replaceAll("-", " ")))
            .setDescription(String.format("You're not allowed to do that, %s", event.getUser().getAsMention()))
            .setImage("https://media.tenor.com/KkRrym9X09EAAAAd/i-dont-think-thats-allowed-ryan-bailey.gif")
            .setColor(EmbedColors.getError())
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build();
    }

    @Override
    public String getName() {
        return killSuggest.ACCEPT_BUTTON;
    }

    @Override
    public void execute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        if (!event.getUser().getId().equals(Config.getDeveloperId())) {
            event.replyEmbeds(youAreNotAllowed(event, blob)).setEphemeral(true).queue();
            return;
        }

        DatabaseManager.INSTANCE.addKillString(Property.KILL_RANDOM, event.getMessage().getContentRaw(), "0");

        MessageEditData messageEditData = new MessageEditBuilder()
            .setContent("Kill Suggestion was accepted.")
            .setComponents(new ArrayList<>())
            .build();

        event.getMessage().editMessage(messageEditData).queue();
    }
}
