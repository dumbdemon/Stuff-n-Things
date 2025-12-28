package com.terransky.stuffnthings.interactions.commands.slashCommands.general;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.jda.DiscordWebhook;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Suggest extends SlashCommandInteraction {

    public Suggest() {
        super("suggest-command", "Have something you want the bot to do? Suggest here!", Mastermind.DEVELOPER,
            CommandCategory.GENERAL, parseDate(2022, 8, 24, 11, 10),
            parseDate(2025, 12, 27, 0, 25)
        );
        addOptions(
            new OptionData(OptionType.STRING, "suggestion", "What do you want the bot to do?", true),
            new OptionData(OptionType.INTEGER, "importance", "How important on a scale. Where 1 is low, 50 is semi, 100 is high, and everything in-between.", true)
                .setRequiredRange(1, 100)
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        String suggestion = event.getOption("suggestion", OptionMapping::getAsString);
        int importance = event.getOption("importance", 50, OptionMapping::getAsInt);
        EmbedBuilder callReply = new EmbedBuilder().setColor(BotColors.DEFAULT.getColor());
        String description = "```\n" + suggestion + "\n```";

        new DiscordWebhook("Suggestion")
            .sendMessage(new EmbedBuilder(callReply)
                .setTitle("Command Suggestion")
                .setDescription(description)
                .addField("Importance Value", "[" + importance + "/100]", false)
                .addField("From", "@" + blob.getMemberName(), false)
                .build()
            );

        callReply.setTitle("Your suggestion was sent successfully!")
            .setDescription(description)
            .addField("Importance Value", "[" + importance + "/100]", false);

        event.replyComponents(
            StandardResponse.getResponseContainer("Your suggestion was sent successfully!",
                List.of(
                    TextDisplay.of(description),
                    TextDisplay.of(String.format("Importance Value - %s/100", importance))
                )
            )
        ).setEphemeral(true).queue();
    }
}
