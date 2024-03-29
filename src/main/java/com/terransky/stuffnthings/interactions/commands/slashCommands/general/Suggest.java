package com.terransky.stuffnthings.interactions.commands.slashCommands.general;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.jda.DiscordWebhook;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Suggest implements ICommandSlash {

    @Override
    public String getName() {
        return "suggest-command";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(this.getName(), "Have something you want the bot to do? Suggest here!", """
            Have a command that you would like the bot to have? Suggest it with this command!
            """, Mastermind.DEVELOPER,
            CommandCategory.GENERAL,
            Metadata.parseDate(2022, 8, 24, 11, 10),
            Metadata.parseDate(2024, 2, 9, 16, 11)
        )
            .addOptions(
                new OptionData(OptionType.STRING, "suggestion", "What do you want the bot to do?", true),
                new OptionData(OptionType.INTEGER, "importance", "How important on a scale. Where 1 is low, 50 is semi, 100 is high, and everything in-between.", true)
                    .setRequiredRange(1, 100)
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        String suggestion = event.getOption("suggestion", OptionMapping::getAsString);
        int importance = event.getOption("importance", 50, OptionMapping::getAsInt);
        EmbedBuilder callReply = new EmbedBuilder().setColor(EmbedColor.DEFAULT.getColor());
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

        event.replyEmbeds(callReply.build()).setEphemeral(true).queue();
    }
}
