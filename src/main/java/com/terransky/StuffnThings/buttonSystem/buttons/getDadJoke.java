package com.terransky.StuffnThings.buttonSystem.buttons;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.buttonSystem.IButton;
import com.terransky.StuffnThings.commandSystem.cmdResources.dadJokes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class getDadJoke implements IButton {
    @Override
    public String getButtonID() {
        return "get-dad-joke";
    }

    @Override
    public void buttonExecute(@NotNull ButtonInteractionEvent event) {
        Random random = new Random();
        String[] dadJokesList = new dadJokes().strings;

        MessageEditData message = new MessageEditBuilder()
                .setEmbeds(new EmbedBuilder()
                        .setDescription(dadJokesList[random.nextInt(dadJokesList.length)])
                        .setColor(new Commons().defaultEmbedColor)
                        .setFooter("Requested by %s".formatted(event.getUser().getAsTag()))
                        .build()
                ).build();

        event.editMessage(message).queue();
    }
}
