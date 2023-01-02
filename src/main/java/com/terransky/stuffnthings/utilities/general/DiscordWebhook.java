package com.terransky.stuffnthings.utilities.general;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

public class DiscordWebhook {

    private final WebhookClient client;

    public DiscordWebhook(WebhookClient client) {
        this.client = client;
    }

    public DiscordWebhook(String threadName) {
        this(threadName, Config.getRequestWebhookURL());
    }

    public DiscordWebhook(String threadName, String requestURL) {
        this(new WebhookClientBuilder(requestURL)
            .setThreadFactory(job -> {
                Thread thread = new Thread(job);
                thread.setName(threadName);
                thread.setDaemon(true);
                return thread;
            })
            .setWait(true)
            .build()
        );
    }

    public DiscordWebhook sendMessage(@NotNull MessageEmbed webhookEmbed) {
        try (WebhookClient client = this.client) {
            client.send(WebhookEmbedBuilder
                .fromJDA(webhookEmbed)
                .build()
            );
        }
        return this;
    }
}
