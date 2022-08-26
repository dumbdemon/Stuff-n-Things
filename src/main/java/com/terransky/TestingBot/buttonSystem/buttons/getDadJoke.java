package com.terransky.TestingBot.buttonSystem.buttons;

import com.terransky.TestingBot.Commons;
import com.terransky.TestingBot.buttonSystem.IButton;
import com.terransky.TestingBot.slashSystem.cmdResources.dadJokes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

public class getDadJoke implements IButton {
    @Override
    public String getButtonID() {
        return "get-dad-joke";
    }

    @Override
    public void buttonExecute(@NotNull ButtonInteractionEvent event) {
        String[] dadJokesList = new dadJokes().strings;

        Message message = new MessageBuilder()
                .setEmbeds(new EmbedBuilder()
                        .setDescription(dadJokesList[(int) (Math.random() * dadJokesList.length)])
                        .setColor(new Commons().defaultEmbedColor)
                        .setFooter("Requested by %s".formatted(event.getUser().getAsTag()))
                        .build()
                )
                .setActionRows(
                        ActionRow.of(Button.primary("get-dad-joke", "Get new Dad Joke!"))
                )
                .build();

        event.editMessage(message).queue();
    }
}
