package com.terransky.stuffnthings.interactions.commands.messageContextMenus;

import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.database.helpers.PropertyMapping;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ICommandMessage;
import com.terransky.stuffnthings.utilities.command.EmbedColor;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import com.terransky.stuffnthings.utilities.jda.DiscordWebhook;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ReportMessage implements ICommandMessage {

    @NotNull
    @Contract(pure = true)
    private String getPlural(int value) {
        return Math.abs(value) != 1 ? "s" : "";
    }

    private String getEditedMessage(@NotNull Message message) {
        if (!message.isEdited())
            return "No";

        return String.format("Yes, on %s.%nOriginal message was sent on%n%s (%s).",
            Timestamp.getDateAsTimestamp(Objects.requireNonNull(message.getTimeEdited())),
            Timestamp.getDateAsTimestamp(message.getTimeCreated()),
            Timestamp.getDateAsTimestamp(message.getTimeCreated(), Timestamp.RELATIVE)
        );
    }

    @Override
    public String getName() {
        return "Report Message";
    }

    @Override
    public boolean isWorking() {
        return Config.isDatabaseEnabled();
    }

    @Override
    public void execute(@NotNull MessageContextInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        Optional<String> ifWebhookId = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.REPORT_WEBHOOK, PropertyMapping::getAsString);
        String reportResponse = DatabaseManager.INSTANCE
            .getFromDatabase(blob, Property.REPORT_RESPONSE, "Got it. Message has been reported.", PropertyMapping::getAsString);

        if (ifWebhookId.isEmpty()) {
            event.replyEmbeds(
                blob.getStandardEmbed(getName(), EmbedColor.ERROR)
                    .setDescription("Message reporting has not been set up yet!\nTell your server admins to set up with `/config report`!")
                    .setFooter(blob.getMemberName(), blob.getMemberEffectiveAvatarUrl())
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        Message message = event.getTarget();

        if (message.getAuthor().isBot()) {
            event.replyEmbeds(
                blob.getStandardEmbed(getName(), EmbedColor.ERROR)
                    .setDescription("Bot messages should be reported to either the developers or to" +
                        " [Discord Trust & Safety](https://support.discord.com/hc/en-us/requests/new).")
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        Mentions mentions = message.getMentions();
        Set<GuildChannel> channels = mentions.getChannelsBag().uniqueSet();
        Set<CustomEmoji> emojis = mentions.getCustomEmojisBag().uniqueSet();
        Set<Role> roles = mentions.getRolesBag().uniqueSet();
        Set<User> users = mentions.getUsersBag().uniqueSet();

        EmbedBuilder report = blob.getStandardEmbed("Message Reported", message.getJumpUrl(), EmbedColor.SUB_DEFAULT)
            .setDescription(String.format("The following message has been reported:%n```%s```", message.getContentRaw()))
            .setFooter(String.format("Reported by %s | %s", blob.getMemberName(), blob.getMemberId()), blob.getMemberEffectiveAvatarUrl())
            .addField("Mentions",
                String.format("%s channel%s, %s custom emoji%s, %s role%s, %s user%s",
                    channels.size(), getPlural(channels.size()),
                    emojis.size(), getPlural(emojis.size()),
                    roles.size(), getPlural(roles.size()),
                    users.size(), getPlural(users.size())
                ), false)
            .addField("Edited?", getEditedMessage(message), false);

        ListIterator<Message.Attachment> attachments = message.getAttachments().listIterator();
        while (attachments.hasNext()) {
            try (Message.Attachment attachment = attachments.next()) {
                String fieldName = "Attachment #" + attachments.nextIndex();
                String fieldValue = String.format("[`%s`](%s)", attachment.getFileName(), attachment.getUrl());
                if (report.length() + fieldValue.length() + fieldValue.length() >= MessageEmbed.EMBED_MAX_LENGTH_BOT)
                    break;
                report.addField(fieldName, fieldValue, false);
            }
        }

        Webhook webhook = event.getJDA().retrieveWebhookById(ifWebhookId.get()).submit().get();
        new DiscordWebhook("Report-Message", webhook)
            .sendMessage(report.build());

        event.replyEmbeds(blob.getStandardEmbed(getName())
            .setDescription(reportResponse)
            .build()
        ).setEphemeral(true).queue();
    }
}
