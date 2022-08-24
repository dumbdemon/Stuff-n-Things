package com.terransky.TestingBot.buttonSystem.buttons;

import com.terransky.TestingBot.Commons;
import com.terransky.TestingBot.secretsAndLies;
import com.terransky.TestingBot.buttonSystem.IButton;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class testButton implements IButton {
    private final Color embedColor = new Commons().defaultEmbedColor;

    @Override
    public String getButtonID() {
        return "test-button";
    }

    @Override
    public void buttonExecute(@NotNull ButtonInteractionEvent event) {
        String[] messages = new secretsAndLies().messages;

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("A casual test.")
                .setDescription(messages[(int) (Math.random() * messages.length)])
                .setColor(embedColor);
        Message msg = new MessageBuilder()
                .setEmbeds(eb.build())
                .setActionRows(
                        ActionRow.of(Button.primary("test-button", "Change Text"))
                )
                .build();

        event.getMessage().editMessage(msg).queue();
        event.replyEmbeds(new EmbedBuilder().setTitle("Message Changed!").build()).setEphemeral(true).queue();
    }
}
