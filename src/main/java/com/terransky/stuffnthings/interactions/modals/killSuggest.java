package com.terransky.stuffnthings.interactions.modals;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interactions.commands.slashCommands.fun.kill;
import com.terransky.stuffnthings.interfaces.discordInteractions.IModal;
import com.terransky.stuffnthings.utilities.command.EmbedColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class killSuggest implements IModal {

    @Override
    public String getName() {
        return "kill-suggest";
    }

    @Override
    public void execute(@NotNull ModalInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        Random rando = new Random(new Date().getTime());
        event.deferReply().queue();
        List<String> victims = new ArrayList<>();
        Optional<ModalMapping> ifSuggestion = Optional.ofNullable(event.getValue(kill.MODAL_NAME));
        String suggestion = ifSuggestion.orElseThrow(DiscordAPIException::new).getAsString();

        List<Member> members =
            blob.getGuild().getMembers().stream().filter(it -> !it.getUser().isBot() || it.getUser().equals(event.getJDA().getSelfUser())).toList();

        for (Member member : members) {
            victims.add(member.getAsMention());
        }

        WebhookClientBuilder builder = new WebhookClientBuilder(Config.getRequestWebhookURL());
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
                .setColor(EmbedColors.getSecondary().getRGB())
                .setTitle(new WebhookEmbed.EmbedTitle("Kill-string Suggestion", null))
                .setDescription(suggestion)
                .addField(new WebhookEmbed.EmbedField(false, "From", "@%s".formatted(event.getUser().getAsTag())))
                .build();

            client.send(request);
        }

        MessageCreateData message = new MessageCreateBuilder()
            .setEmbeds(
                new EmbedBuilder()
                    .setColor(EmbedColors.getSecondary())
                    .setTitle("Suggestion received!")
                    .setDescription("The next embed will show what your suggestion will look like!\n" +
                        "***Note: Will not show up automatically!***")
                    .build(),
                new EmbedBuilder()
                    .setColor(EmbedColors.getDefault())
                    .setTitle(Objects.requireNonNull(event.getMember()).getEffectiveName())
                    .setDescription("… " + testKillString)
                    .setFooter("Suggestion by " + event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
                    .build()
            )
            .build();

        event.getHook().sendMessage(message).queue();
    }
}
