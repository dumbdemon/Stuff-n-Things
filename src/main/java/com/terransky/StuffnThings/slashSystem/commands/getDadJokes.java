package com.terransky.StuffnThings.slashSystem.commands;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.slashSystem.ISlash;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

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
        Random random = new Random();
        String[] dadJokesList = new com.terransky.StuffnThings.slashSystem.cmdResources.dadJokes().strings;

        MessageCreateData message = new MessageCreateBuilder()
                .setEmbeds(new EmbedBuilder()
                        .setDescription(dadJokesList[random.nextInt(dadJokesList.length)])
                        .setColor(new Commons().defaultEmbedColor)
                        .setFooter("Requested by %s".formatted(event.getUser().getAsTag()))
                        .build()
                )
                .addComponents(
                        ActionRow.of(Button.primary("get-dad-joke", "Get new Dad Joke!"))
                )
                .build();

        event.reply(message).queue();
    }
}
