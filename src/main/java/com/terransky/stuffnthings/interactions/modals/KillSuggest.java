package com.terransky.stuffnthings.interactions.modals;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ModalInteraction;
import com.terransky.stuffnthings.utilities.command.BotColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.StandardResponse;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class KillSuggest {

    public static final String ACCEPT_RANDOM_BUTTON = "accept-random-kill";
    public static final String ACCEPT_TARGET_BUTTON = "accept-target-kill";
    public static final String DENY_BUTTON = "deny-kill";
    public static final String MODAL_TEXT_INPUT_NAME = "kill-suggestion";

    private static void doExecute(@NotNull ModalInteractionEvent event, @NotNull EventBlob blob, boolean isRandom) throws RuntimeException, IOException {
        event.deferReply().queue();
        Optional<ModalMapping> ifSuggestion = Optional.ofNullable(event.getValue(MODAL_TEXT_INPUT_NAME));
        String suggestion = ifSuggestion.orElseThrow(DiscordAPIException::new).getAsString();

        AtomicLong seed = new AtomicLong(OffsetDateTime.now().getLong(ChronoField.MILLI_OF_DAY));
        List<String> victims = new ArrayList<>() {{
            blob.getNonBotMembersAndSelf().forEach(member -> {
                seed.updateAndGet(seed -> seed | member.getIdLong());
                add(member.getAsMention());
            });
        }};

        java.util.Random rando = new java.util.Random(seed.get());
        TextChannel requestChannel = event.getJDA().getTextChannelById(StuffNThings.getConfig().getCore().getRequest().getChannelId());

        if (requestChannel == null)
            throw new DiscordAPIException("Either the channel does not exist or I could not obtain the channel.");

        requestChannel.sendMessageComponents(
            Container.of(List.of(
                TextDisplay.ofFormat("… %s", suggestion),
                ActionRow.of(
                    Button.success(isRandom ? ACCEPT_RANDOM_BUTTON : ACCEPT_TARGET_BUTTON, "Accept"),
                    Button.danger(DENY_BUTTON, "Deny")
                )
            )).withAccentColor(BotColors.DEFAULT.getColor())
        ).queue();

        String testKillString = suggestion.formatted(
            victims.get(rando.nextInt(victims.size())),
            victims.get(rando.nextInt(victims.size())),
            victims.get(rando.nextInt(victims.size())),
            victims.get(rando.nextInt(victims.size()))
        );

        event.getHook().sendMessageComponents(
            StandardResponse.getResponseContainer("Suggestion received!", TextDisplay.of("The next embed will show what your suggestion will look like!\n" +
                "***Note: Will not show up automatically!***"), BotColors.SUB_DEFAULT),
            StandardResponse.getResponseContainer(blob.getMember().getEffectiveName(), TextDisplay.ofFormat("… %s", testKillString))
        ).queue();
    }

    public static class Random extends ModalInteraction {

        public Random() {
            super("random-kill-suggest", MODAL_TEXT_INPUT_NAME);
        }

        @Override
        public String getName() {
            return "random-kill-suggest";
        }

        @Override
        public void execute(@NotNull ModalInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
            doExecute(event, blob, true);
        }

        //Research new Modal system
        @Override
        public Modal getContructedModal() {
            return getBuilder()
                .addComponents(
                    TextDisplay.of("")
                ).build();
        }
    }

    public static class Target extends ModalInteraction {

        public Target() {
            super("target-kill-suggest", "Suggest Kill-String");
        }

        //Research new Modal system
        @Override
        public Modal getContructedModal() {
            return getBuilder()
                .addComponents(
                    TextDisplay.of("")
                ).build();
        }

        @Override
        public String getName() {
            return "target-kill-suggest";
        }

        @Override
        public void execute(@NotNull ModalInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
            doExecute(event, blob, false);
        }

//        @Override
//        public Modal getConstructedModal() {
//            TextInput suggestion = TextInput.create(MODAL_TEXT_INPUT_NAME, "Suggestion", TextInputStyle.PARAGRAPH)
//                .setRequired(true)
//                .setRequiredRange(10, MessageEmbed.DESCRIPTION_MAX_LENGTH / 4)
//                .setPlaceholder("Use \"%s\" to represent your target.")
//                .build();
//
//
//            return Modal.create(getName(), "Suggest Kill-String")
//                .addActionRow(suggestion)
//                .build();
//        }
    }
}
