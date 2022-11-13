package com.terransky.stuffnthings.modalSystem.modals;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.interfaces.IModal;
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

    @Override
    public String getName() {
        return "kill-suggest";
    }

    @Override
    public void execute(@NotNull ModalInteractionEvent event) throws Exception {
        Random rando = new Random();
        event.deferReply().queue();
        List<String> victims = new ArrayList<>();
        String suggestion = Objects.requireNonNull(event.getValue("kill-suggestion")).getAsString();

        List<Member> members = new ArrayList<>();
        if (event.getGuild() != null) members = event.getGuild().getMembers();
        for (Member member : members.stream().filter(it -> !it.getUser().isBot() || it.getUser().equals(event.getJDA().getSelfUser())).toList()) {
            victims.add(member.getAsMention());
        }

        WebhookClientBuilder builder = new WebhookClientBuilder(Commons.getConfig().get("REQUEST_WEBHOOK"));
        builder.setThreadFactory(job -> {
            Thread thread = new Thread(job);
            thread.setName("Kill_Suggestion");
            thread.setDaemon(true);
            return thread;
        });
        builder.setWait(true);

        String testKillString = suggestion.formatted(
            victims.get(rando.nextInt(victims.size())),
            victims.get(rando.nextInt(victims.size())),
            victims.get(rando.nextInt(victims.size())),
            victims.get(rando.nextInt(victims.size()))
        );

        try (WebhookClient client = builder.build()) {
            WebhookEmbed request = new WebhookEmbedBuilder()
                .setColor(Commons.getSecondaryEmbedColor().getRGB())
                .setTitle(new WebhookEmbed.EmbedTitle("Kill-string Suggestion", null))
                .setDescription(suggestion)
                .addField(new WebhookEmbed.EmbedField(false, "From", "@%s".formatted(event.getUser().getAsTag())))
                .build();

            client.send(request);
        }

        MessageCreateData message = new MessageCreateBuilder()
            .setEmbeds(
                new EmbedBuilder()
                    .setColor(Commons.getSecondaryEmbedColor())
                    .setTitle("Suggestion received!")
                    .setDescription("The next embed will show what your suggestion will look like!\n" +
                        "***Note: Will not show up automatically!***")
                    .build(),
                new EmbedBuilder()
                    .setColor(Commons.getDefaultEmbedColor())
                    .setTitle(Objects.requireNonNull(event.getMember()).getEffectiveName())
                    .setDescription("\u2026 " + testKillString)
                    .setFooter("Suggestion by " + event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                    .build()
            )
            .build();

        event.getHook().sendMessage(message).queue();
    }
}
