package com.terransky.StuffnThings.slashSystem.commands;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.slashSystem.ISlash;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

public class suggest implements ISlash {
    private final Commons cmn = new Commons();
    private final Dotenv config = Dotenv.configure().load();

    @Override
    public String getName() {
        return "suggest-command";
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(this.getName(), "Have something you want the bot to do? Suggest here!")
                .addOptions(
                        new OptionData(OptionType.STRING, "suggestion", "What do you want the bot to do?", true),
                        new OptionData(OptionType.INTEGER, "importance", "How important on a scale. Where 1 is low, 50 is semi, 100 is high, and everything in-between.", true)
                                .setMinValue(1).setMaxValue(100)
                );
    }

    @Override
    public void slashExecute(@NotNull SlashCommandInteractionEvent event) {
        String suggestion = event.getOption("suggestion", OptionMapping::getAsString);
        int importance = event.getOption("importance", 50, OptionMapping::getAsInt);
        EmbedBuilder callReply = new EmbedBuilder().setColor(cmn.defaultEmbedColor);
        String description = "```\n" + suggestion + "\n```";

        WebhookClientBuilder builder = new WebhookClientBuilder(config.get("REQUEST_WEBHOOK"));
        builder.setThreadFactory(job -> {
            Thread thread = new Thread(job);
            thread.setName("Suggestion");
            thread.setDaemon(true);
            return thread;
        });
        builder.setWait(true);

        try (WebhookClient client = builder.build()) {
            WebhookEmbed request = new WebhookEmbedBuilder()
                    .setColor(cmn.getIntFromColor(102, 52, 102))
                    .setTitle(new WebhookEmbed.EmbedTitle("Command Suggestion", null))
                    .setDescription(description)
                    .addField(new WebhookEmbed.EmbedField(false, "Importance Value", "[" + importance + "/100]"))
                    .addField(new WebhookEmbed.EmbedField(false, "From", "@" + event.getUser().getAsTag()))
                    .build();

            client.send(request);
        }

        callReply.setTitle("Your suggestion was sent successfully!")
                .setDescription(description)
                .addField("Importance Value", "[" + importance + "/100]", false);

        event.replyEmbeds(callReply.build()).setEphemeral(true).queue();
    }
}
