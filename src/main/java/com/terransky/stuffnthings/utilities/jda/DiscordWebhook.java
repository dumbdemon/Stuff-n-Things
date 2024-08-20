package com.terransky.stuffnthings.utilities.jda;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.terransky.stuffnthings.StuffNThings;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Webhook;
import org.jetbrains.annotations.NotNull;

public class DiscordWebhook {

    private final WebhookClientBuilder clientBuilder;

    public DiscordWebhook(WebhookClientBuilder clientBuilder) {
        this.clientBuilder = clientBuilder;
    }

    public DiscordWebhook(String threadName) {
        this(threadName, StuffNThings.getConfig().getCore().getRequest().getWebhookUrl());
    }

    public DiscordWebhook(String threadName, @NotNull Webhook webhook) {
        this(WebhookClientBuilder.fromJDA(webhook)
            .setThreadFactory(job -> {
                Thread thread = new Thread(job);
                thread.setName(threadName);
                thread.setDaemon(true);
                return thread;
            })
            .setWait(true)
        );
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
        );
    }

    public void sendMessage(@NotNull MessageEmbed webhookEmbed) {
        try (WebhookClient client = clientBuilder.build()) {
            client.send(WebhookEmbedBuilder
                .fromJDA(webhookEmbed)
                .build()
            );
        }
    }
}
