package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.database.helpers.entry.UserGuildEntry;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Kill implements ICommandSlash {

    public static final String RANDOM_MODAL_NAME = "random-kill-suggest";
    public static final String TARGET_MODAL_NAME = "target-kill-suggest";
    public static final String MODAL_TEXT_INPUT_NAME = "kill-suggestion";

    private void killRandom(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, @NotNull Random random, EmbedBuilder eb) {
        List<String> randomStrings = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.KILL_RANDOM)
            .map(o -> (List<String>) new ArrayList<String>() {{
                    for (Object o1 : ((List<?>) o)) {
                        add((String) o1);
                    }
                }}
            )
            .orElse(List.of("just dies by %s's hands."));
        List<String> victims = new ArrayList<>() {{
            blob.getGuild().getMembers().stream()
                .filter(member -> !member.getUser().isBot() || member.getUser().equals(event.getJDA().getSelfUser()))
                .forEach(member -> add(member.getAsMention()));
        }};

        String message = randomStrings.get(random.nextInt(randomStrings.size())).formatted(
            victims.get(random.nextInt(victims.size())),
            victims.get(random.nextInt(victims.size())),
            victims.get(random.nextInt(victims.size())),
            victims.get(random.nextInt(victims.size()))
        );

        if (message.contains(blob.getSelfMember().getAsMention()))
            message += " :O";

        eb.setColor(EmbedColors.getDefault())
            .setDescription("… " + message);

        event.replyEmbeds(eb.build()).queue();
    }

    private void killTarget(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, Random random, EmbedBuilder eb) {
        String target = event.getOption("target", event.getJDA().getSelfUser(), OptionMapping::getAsUser).getAsMention();
        if (target.equals(blob.getSelfMember().getAsMention())) {
            target += " (hey wait a second…)";
        }
        UserGuildEntry entry = DatabaseManager.INSTANCE.getUserGuildEntry(blob.getMemberId(), blob.getGuildId());
        OffsetDateTime endTime = entry.getEndTime();

        if (endTime.isBefore(OffsetDateTime.now())) {
            new ResetKillProperties(blob, getClass()).run();
            entry.resetAttempts();
        }

        if (!event.getUser().getId().equals(Config.getDeveloperId()) &&
            !blob.getGuildId().equals(Config.getSupportGuildId()) &&
            !Config.isTestingMode()) {
            long maxKills = entry.getMaxKills();
            long attempts = entry.getKillAttempts();

            if (attempts >= maxKills) {
                event.replyEmbeds(
                    eb.setDescription(String.format("… tried to kill %s but they couldn't because that's bad manners!%n" +
                            "Next available kill %s.", target, Timestamp.getDateAsTimestamp(endTime, Timestamp.RELATIVE)))
                        .build()
                ).setEphemeral(true).queue();
                return;
            }

            DatabaseManager.INSTANCE.updateProperty(blob, Property.KILL_ATTEMPTS, attempts + 1);
            if (!entry.isUnderTimeout())
                performLockOut(blob, entry.getTimeout());
        }
        List<String> targetStrings = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.KILL_TARGET)
            .map(o -> (List<String>) new ArrayList<String>() {{
                    for (Object o1 : ((List<?>) o)) {
                        add((String) o1);
                    }
                }}
            )
            .orElse(List.of("tried to kill %s but they couldn't because that's bad manners!"));

        eb.setDescription(String.format("… %s", targetStrings.get(random.nextInt(targetStrings.size()))).formatted(target));
        event.replyEmbeds(eb.build()).queue();
    }

    private void performLockOut(EventBlob blob, long timeout) {
        DatabaseManager.INSTANCE.updateProperty(blob, Property.KILL_TIMEOUT, true);
        DatabaseManager.INSTANCE.updateProperty(blob, Property.KILL_END_DATE, OffsetDateTime.now().plusSeconds(TimeUnit.MILLISECONDS.toSeconds(timeout)));
        new Timer().schedule(new ResetKillProperties(blob, getClass()), timeout);
    }

    @Override
    public String getName() {
        return "kill";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(this.getName(), "Time to un-alive random members!", """
            Take a chance and try to kill a random member in your server! Or just *that guy* cause they've been annoying you recently.
            """, Mastermind.USER,
            CommandCategory.FUN,
            Metadata.parseDate("2022-08-24T11:10Z"),
            Metadata.parseDate("2023-02-11T14:34Z")
        )
            .addSubcommands(
                new SubcommandData("random", "Try your hand at un-aliving someone!"),
                new SubcommandData("target", "Target someone for a kill.")
                    .addOption(OptionType.USER, "target", "Your target", true),
                new SubcommandData("suggest", "Suggest a kill-string. Use \"%s\" to represent targets. Up to four can be in a kill-string.")
                    .addOptions(
                        new OptionData(OptionType.BOOLEAN, "is-random", "Random or target?", true)
                    )
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");

        Random random = new Random(new Date().getTime());
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setTitle(blob.getMember().getEffectiveName())
            .setFooter("Requested by " + blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl());

        switch (subcommand) {
            case "random" -> killRandom(event, blob, random, eb);

            case "suggest" -> {
                boolean isRandom = event.getOption("is-random", true, OptionMapping::getAsBoolean);
                String placeholder = isRandom ? "up to four targets! There could be more, but I don't wanna!" : "your target.";

                TextInput suggestion = TextInput.create(MODAL_TEXT_INPUT_NAME, "Suggestion", TextInputStyle.PARAGRAPH)
                    .setRequired(true)
                    .setRequiredRange(10, MessageEmbed.DESCRIPTION_MAX_LENGTH / 4)
                    .setPlaceholder("Use \"%s\" to represent " + placeholder)
                    .build();


                Modal modal = Modal.create(isRandom ? RANDOM_MODAL_NAME : TARGET_MODAL_NAME, "Suggest Kill-String")
                    .addActionRow(suggestion)
                    .build();

                event.replyModal(modal).queue();
            }

            case "target" -> killTarget(event, blob, random, eb);
        }
    }

    static class ResetKillProperties extends TimerTask {

        private final String userId;
        private final String guildId;
        private final Logger log;

        protected ResetKillProperties(@NotNull EventBlob blob, Class<? extends ICommandSlash> aClass) {
            this.userId = blob.getMemberId();
            this.guildId = blob.getGuildId();
            this.log = LoggerFactory.getLogger(aClass);
        }

        @Override
        public void run() {
            if (!DatabaseManager.INSTANCE.resetUserKillProperties(userId, guildId)) {
                log.error("Failed to reset kill settings for {}", userId);
            }
        }
    }
}