package com.terransky.stuffnthings.interactions.modals;

import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interactions.commands.slashCommands.fun.kill;
import com.terransky.stuffnthings.interfaces.interactions.IModal;
import com.terransky.stuffnthings.utilities.command.EmbedColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.DiscordWebhook;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
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
        Optional<ModalMapping> ifSuggestion = Optional.ofNullable(event.getValue(kill.MODAL_NAME));
        String suggestion = ifSuggestion.orElseThrow(DiscordAPIException::new).getAsString();

        List<String> victims = new ArrayList<>() {{
            blob.getGuild().getMembers().stream()
                .filter(member -> !member.getUser().isBot() ||
                    member.getUser().equals(event.getJDA().getSelfUser())
                ).forEach(member -> add(member.getAsMention()));
        }};

        var ignored = new DiscordWebhook("Kill_Suggestion")
            .sendMessage(new EmbedBuilder()
                .setTitle("Kill-string Suggestion")
                .setDescription(suggestion)
                .addField("From", "@%s".formatted(event.getUser().getAsTag()), false)
                .setColor(EmbedColors.getSecondary())
                .build()
            );

        String testKillString = suggestion.formatted(
            victims.get(rando.nextInt(victims.size())),
            victims.get(rando.nextInt(victims.size())),
            victims.get(rando.nextInt(victims.size())),
            victims.get(rando.nextInt(victims.size()))
        );

        event.getHook().sendMessageEmbeds(
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
        ).queue();
    }
}
