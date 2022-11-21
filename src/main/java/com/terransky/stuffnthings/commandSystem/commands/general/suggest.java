package com.terransky.stuffnthings.commandSystem.commands.general;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.commandSystem.metadata.Mastermind;
import com.terransky.stuffnthings.commandSystem.metadata.Metadata;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

public class suggest implements ISlashCommand {

    @Override
    public String getName() {
        return "suggest-command";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat formatter = Commons.getFastDateFormat();
        var metadata = new Metadata(this.getName(), "Have something you want the bot to do? Suggest here!", """
            Have a command that you would like the bot to have? Suggest it with this command!
            """, Mastermind.DEVELOPER,
            formatter.parse("24-08-2022_11:10"),
            formatter.parse("19-11-2022_11:40")
        );

        metadata.addOptions(
            new OptionData(OptionType.STRING, "suggestion", "What do you want the bot to do?", true),
            new OptionData(OptionType.INTEGER, "importance", "How important on a scale. Where 1 is low, 50 is semi, 100 is high, and everything in-between.", true)
                .setRequiredRange(1, 100)
        );

        return metadata;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild) {
        String suggestion = event.getOption("suggestion", OptionMapping::getAsString);
        int importance = event.getOption("importance", 50, OptionMapping::getAsInt);
        EmbedBuilder callReply = new EmbedBuilder().setColor(Commons.getDefaultEmbedColor());
        String description = "```\n" + suggestion + "\n```";

        WebhookClientBuilder builder = new WebhookClientBuilder(Commons.getConfig().get("REQUEST_WEBHOOK"));
        builder.setThreadFactory(job -> {
            Thread thread = new Thread(job);
            thread.setName("Suggestion");
            thread.setDaemon(true);
            return thread;
        });
        builder.setWait(true);

        try (WebhookClient client = builder.build()) {
            WebhookEmbed request = new WebhookEmbedBuilder()
                .setColor(Commons.getDefaultEmbedColor().getRGB())
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
