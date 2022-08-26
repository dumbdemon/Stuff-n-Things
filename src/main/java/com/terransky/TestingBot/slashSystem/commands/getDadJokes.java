package com.terransky.TestingBot.slashSystem.commands;

import com.terransky.TestingBot.Commons;
import com.terransky.TestingBot.slashSystem.ISlash;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

public class getDadJokes implements ISlash {
    @Override
    public String getName() {
        return "dad-jokes";
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(this.getName(), "Why was 6 afraid of 7? Because 7 was a registered 6 offender.");
    }

    @Override
    public void slashExecute(@NotNull SlashCommandInteractionEvent event) {
        String[] dadJokesList = new com.terransky.TestingBot.slashSystem.cmdResources.dadJokes().strings;

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

        event.reply(message).queue();
    }
}
