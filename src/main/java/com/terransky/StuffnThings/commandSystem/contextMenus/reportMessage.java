package com.terransky.StuffnThings.commandSystem.contextMenus;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.IContextMenu;
import com.terransky.StuffnThings.database.SQLiteDataSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class reportMessage implements IContextMenu {
    private final Logger log = LoggerFactory.getLogger(reportMessage.class);

    @Override
    public String getMenuName() {
        return "Report Message";
    }

    @Override
    public CommandData contextData() {
        return Commands.message(this.getMenuName());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void messageContextExecute(@NotNull MessageContextInteractionEvent event) {
        String webhookURL = "",
                autoResponse = "Got it. Message has been reported.";
        try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                .prepareStatement("SELECT reporting_wh, reporting_ar FROM guilds WHERE guild_id = ?")) {
            stmt.setString(1, event.getGuild().getId());
            try (final ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    webhookURL = rs.getString("reporting_wh") != null ? rs.getString("reporting_wh") : "";
                    if (rs.getString("reporting_ar") != null) {
                        autoResponse = rs.getString("reporting_ar");
                    }
                }
            }
        } catch (SQLException e) {
            log.error("%s : %s".formatted(e.getClass().getName(), e.getMessage()));
            e.printStackTrace();
        }

        if (!webhookURL.equals("")) {
            Message msg = event.getTarget();
            List<Message.Attachment> attachments = msg.getAttachments();
            String contentRaw = msg.getContentRaw();

            WebhookClientBuilder builder = new WebhookClientBuilder(webhookURL);
            builder.setThreadFactory(job -> {
                Thread thread = new Thread(job);
                thread.setName("Report-Message");
                thread.setDaemon(true);
                return thread;
            });
            builder.setWait(true);

            try (WebhookClient client = builder.build()) {
                WebhookEmbedBuilder request = new WebhookEmbedBuilder()
                        .setColor(new Commons().getIntFromColor(102, 52, 102))
                        .setTitle(new WebhookEmbed.EmbedTitle("Message reported", msg.getJumpUrl()))
                        .setDescription("The following message has been reported:\n```\n%s\n```".formatted(contentRaw));
                if (attachments.size() != 0) {
                    int i = 1;
                    for (Message.Attachment attachment : attachments) {
                        request.addField(new WebhookEmbed.EmbedField(false, "Attachment #%d".formatted(i), attachment.getUrl()));
                        i++;
                    }
                }

                client.send(request.build());
            }

            event.replyEmbeds(new EmbedBuilder()
                    .setColor(new Commons().defaultEmbedColor)
                    .setTitle(this.getMenuName())
                    .setDescription(autoResponse)
                    .setFooter(event.getUser().getAsTag(), event.getMember().getEffectiveAvatarUrl())
                    .build()
            ).setEphemeral(true).queue();
        } else {
            event.replyEmbeds(new EmbedBuilder()
                    .setColor(new Commons().defaultEmbedColor)
                    .setTitle(this.getMenuName())
                    .setDescription("Message reporting has not been set up yet!\nTell your server admins to set up with `/config report`!")
                    .setFooter(event.getUser().getAsTag(), event.getMember().getEffectiveAvatarUrl())
                    .build()
            ).setEphemeral(true).queue();
        }
    }
}
