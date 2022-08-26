package com.terransky.TestingBot.modalSystem.modals;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.terransky.TestingBot.Commons;
import com.terransky.TestingBot.modalSystem.IModal;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class killSuggest implements IModal {
    private final Dotenv config = Dotenv.configure().load();
    private final Commons cmn = new Commons();

    @Override
    public String getModalID() {
        return "kill-suggest";
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void modalExecute(@NotNull ModalInteractionEvent event) {
        event.deferReply().queue();
        List<String> victims = new ArrayList<>();
        String suggestion = Objects.requireNonNull(event.getValue("kill-suggestion")).getAsString();

        for (Member member : event.getGuild().getMembers()) {
            if (!member.getUser().isBot()) {
                victims.add(member.getAsMention());
            }
        }
        String[] targets = victims.toArray(new String[0]);

        WebhookClientBuilder builder = new WebhookClientBuilder(config.get("REQUEST_WEBHOOK"));
        builder.setThreadFactory(job -> {
            Thread thread = new Thread(job);
            thread.setName("Kill_Suggestion");
            thread.setDaemon(true);
            return thread;
        });
        builder.setWait(true);

        String testKillString = String.format(suggestion,
                targets[(int) (Math.random() * targets.length)],
                targets[(int) (Math.random() * targets.length)],
                targets[(int) (Math.random() * targets.length)],
                targets[(int) (Math.random() * targets.length)]
        );

        try (WebhookClient client = builder.build()) {
            WebhookEmbed request = new WebhookEmbedBuilder()
                    .setColor(cmn.getIntFromColor(102, 52, 102))
                    .setTitle(new WebhookEmbed.EmbedTitle("Kill-string Suggestion", null))
                    .setDescription(suggestion)
                    .addField(new WebhookEmbed.EmbedField(false, "From", "@" + event.getUser().getAsTag()))
                    .build();

            client.send(request);
        }

        MessageCreateData message = new MessageCreateBuilder()
                .setEmbeds(
                        new EmbedBuilder()
                                .setColor(cmn.secondaryEmbedColor)
                                .setTitle("Suggestion received!")
                                .setDescription("The next embed will show what your suggestion will look like!\n" +
                                        "***Note: Will not show up automatically!***")
                                .build(),
                        new EmbedBuilder()
                                .setColor(cmn.defaultEmbedColor)
                                .setTitle(Objects.requireNonNull(event.getMember()).getEffectiveName())
                                .setDescription("\u2026 " + testKillString)
                                .setFooter("Suggestion by " + event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                                .build()
                )
                .build();

        event.getHook().sendMessage(message).queue();
    }
}
