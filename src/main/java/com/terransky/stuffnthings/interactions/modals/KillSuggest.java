package com.terransky.stuffnthings.interactions.modals;

import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interactions.commands.slashCommands.fun.Kill;
import com.terransky.stuffnthings.interfaces.interactions.IModal;
import com.terransky.stuffnthings.utilities.command.EmbedColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class KillSuggest {

    public static final String ACCEPT_RANDOM_BUTTON = "accept-random-kill";
    public static final String ACCEPT_TARGET_BUTTON = "accept-target-kill";
    public static final String DENY_BUTTON = "deny-kill";

    private static void doExecute(@NotNull ModalInteractionEvent event, @NotNull EventBlob blob, boolean isRandom) throws RuntimeException, IOException {
        event.deferReply().queue();
        Optional<ModalMapping> ifSuggestion = Optional.ofNullable(event.getValue(Kill.MODAL_TEXT_INPUT_NAME));
        String suggestion = ifSuggestion.orElseThrow(DiscordAPIException::new).getAsString();

        AtomicLong seed = new AtomicLong(new Date().getTime());
        List<String> victims = new ArrayList<>() {{
            blob.getNonBotMembersAndSelf().forEach(member -> {
                seed.updateAndGet(seed -> seed | member.getIdLong());
                add(member.getAsMention());
            });
        }};

        java.util.Random rando = new java.util.Random(seed.get());
        TextChannel requestChannel = event.getJDA().getTextChannelById(Config.getRequestChannelID());

        if (requestChannel == null)
            throw new DiscordAPIException("Either the channel does not exist or I could not obtain the channel.");

        requestChannel.sendMessage(suggestion)
            .addActionRow(
                Button.success(isRandom ? ACCEPT_RANDOM_BUTTON : ACCEPT_TARGET_BUTTON, "Accept"),
                Button.danger(DENY_BUTTON, "Deny")
            ).queue();

        String testKillString = suggestion.formatted(
            victims.get(rando.nextInt(victims.size())),
            victims.get(rando.nextInt(victims.size())),
            victims.get(rando.nextInt(victims.size())),
            victims.get(rando.nextInt(victims.size()))
        );

        event.getHook().sendMessageEmbeds(
            new EmbedBuilder(blob.getStandardEmbed("Suggestion received!", EmbedColors.getSecondary()))
                .setDescription("The next embed will show what your suggestion will look like!\n" +
                    "***Note: Will not show up automatically!***")
                .build(),
            new EmbedBuilder(blob.getStandardEmbed(blob.getMember().getEffectiveName()))
                .setDescription("… " + testKillString)
                .setFooter("Suggestion by " + event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
                .build()
        ).queue();
    }

    public static class Random implements IModal {
        @Override
        public String getName() {
            return Kill.RANDOM_MODAL_NAME;
        }

        @Override
        public void execute(@NotNull ModalInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
            doExecute(event, blob, true);
        }
    }

    public static class Target implements IModal {
        @Override
        public String getName() {
            return Kill.TARGET_MODAL_NAME;
        }

        @Override
        public void execute(@NotNull ModalInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
            doExecute(event, blob, false);
        }
    }
}
