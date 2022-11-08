package com.terransky.StuffnThings.modalSystem.modals;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.modalSystem.IModal;
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
import java.util.Random;

public class killSuggest implements IModal {
    private final Dotenv config = Dotenv.configure().load();

    @Override
    public String getID() {
        return "kill-suggest";
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void execute(@NotNull ModalInteractionEvent event) {
        Random rando = new Random();
        event.deferReply().queue();
        List<String> victims = new ArrayList<>();
        String suggestion = event.getValue("kill-suggestion").getAsString();

        for (Member member : event.getGuild().getMembers()) {
            if (!member.getUser().isBot() || member.getUser().equals(event.getJDA().getSelfUser())) {
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

        String testKillString = suggestion.formatted(
            targets[rando.nextInt(targets.length)],
            targets[rando.nextInt(targets.length)],
            targets[rando.nextInt(targets.length)],
            targets[rando.nextInt(targets.length)]
        );

        try (WebhookClient client = builder.build()) {
            WebhookEmbed request = new WebhookEmbedBuilder()
                .setColor(Commons.DEFAULT_EMBED_COLOR.getRGB())
                .setTitle(new WebhookEmbed.EmbedTitle("Kill-string Suggestion", null))
                .setDescription(suggestion)
                .addField(new WebhookEmbed.EmbedField(false, "From", "@%s".formatted(event.getUser().getAsTag())))
                .build();

            client.send(request);
        }

        MessageCreateData message = new MessageCreateBuilder()
            .setEmbeds(
                new EmbedBuilder()
                    .setColor(Commons.SECONDARY_EMBED_COLOR)
                    .setTitle("Suggestion received!")
                    .setDescription("The next embed will show what your suggestion will look like!\n" +
                        "***Note: Will not show up automatically!***")
                    .build(),
                new EmbedBuilder()
                    .setColor(Commons.DEFAULT_EMBED_COLOR)
                    .setTitle(Objects.requireNonNull(event.getMember()).getEffectiveName())
                    .setDescription("\u2026 " + testKillString)
                    .setFooter("Suggestion by " + event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                    .build()
            )
            .build();

        event.getHook().sendMessage(message).queue();
    }
}
