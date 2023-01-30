package com.terransky.stuffnthings.interactions.commands.messageContextMenus;

import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ICommandMessage;
import com.terransky.stuffnthings.utilities.command.EmbedColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.DiscordWebhook;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ReportMessage implements ICommandMessage {
    @Override
    public String getName() {
        return "Report Message";
    }

    @Override
    public boolean isWorking() {
        return Config.isDatabaseEnabled();
    }

    @Override
    public void execute(@NotNull MessageContextInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        Optional<String> ifWebhookId = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.REPORT_WEBHOOK)
            .map(hook -> (String) hook);
        String reportResponse = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.REPORT_RESPONSE)
            .map(response -> (String) response)
            .orElse("Got it. Message has been reported.");

        if (ifWebhookId.isEmpty()) {
            event.replyEmbeds(new EmbedBuilder()
                .setColor(EmbedColors.getError())
                .setTitle(getName())
                .setDescription("Message reporting has not been set up yet!\nTell your server admins to set up with `/config report`!")
                .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
                .build()
            ).setEphemeral(true).queue();
            return;
        }

        Message message = event.getTarget();
        List<Message.Attachment> attachments = message.getAttachments();
        Webhook webhook = event.getJDA().retrieveWebhookById(ifWebhookId.get()).complete();

        if (message.getAuthor().isBot()) {
            event.replyEmbeds(new EmbedBuilder()
                .setTitle(getName())
                .setDescription("Bot messages should be reported to either the developers or to" +
                    " [Discord Trust & Safety](https://support.discord.com/hc/en-us/requests/new).")
                .setColor(EmbedColors.getError())
                .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
                .build()
            ).setEphemeral(true).queue();
            return;
        }

        EmbedBuilder report = new EmbedBuilder()
            .setColor(EmbedColors.getSecondary())
            .setTitle("Message Reported", message.getJumpUrl())
            .setDescription(String.format("The following message has been reported:%n```%s```", message.getContentRaw()))
            .setFooter(String.format("Reported by %s | %s", blob.getMemberAsTag(), blob.getMemberId()), blob.getMemberEffectiveAvatarUrl());

        for (Message.Attachment attachment : attachments) {
            int position = attachments.indexOf(attachment) + 1;
            report.addField("Attachment #" + position, attachment.getUrl(), false);
        }

        new DiscordWebhook("Report-Message", webhook)
            .sendMessage(report.build());

        event.replyEmbeds(new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setTitle(getName())
            .setDescription(reportResponse)
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build()
        ).setEphemeral(true).queue();
    }
}
