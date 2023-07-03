package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.database.helpers.PropertyMapping;
import com.terransky.stuffnthings.database.helpers.entry.UserGuildEntry;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interactions.modals.KillSuggest;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.Metadata;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class Kill implements ICommandSlash {

    private void killRandom(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, EmbedBuilder eb) {
        List<String> randomStrings = DatabaseManager.INSTANCE
            .getFromDatabase(blob, Property.KILL_RANDOM, List.of("just dies by %s's hands."), PropertyMapping::getAsListOfString);
        AtomicLong seed = new AtomicLong(new Date().getTime());
        List<String> victims = new ArrayList<>() {{
            blob.getNonBotMembersAndSelf().forEach(member -> {
                seed.updateAndGet(seed -> seed | member.getIdLong());
                add(member.getAsMention());
            });
        }};
        Random random = new Random(seed.get());

        String message = randomStrings.get(random.nextInt(randomStrings.size())).formatted(
            victims.get(random.nextInt(victims.size())),
            victims.get(random.nextInt(victims.size())),
            victims.get(random.nextInt(victims.size())),
            victims.get(random.nextInt(victims.size()))
        );

        if (message.contains(blob.getSelfMember().getAsMention()))
            message += " :O";

        eb.setDescription("… " + message);

        event.replyEmbeds(eb.build()).queue();
    }

    private void killTarget(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, Random random, EmbedBuilder eb) {
        String target = event.getOption("target", event.getJDA().getSelfUser(), OptionMapping::getAsUser).getAsMention();
        if (target.equals(blob.getSelfMember().getAsMention())) {
            target += " (hey wait a second…)";
        }
        UserGuildEntry entry = DatabaseManager.INSTANCE.getUserGuildEntry(blob.getMemberId(), blob.getGuildId());
        OffsetDateTime endTime = entry.getKillEndTimeAsDate();

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
            if (entry.getKillUnderTo() == null || !entry.getKillUnderTo())
                performLockOut(blob, entry.getServerTimeout());
        }
        List<String> targetStrings = DatabaseManager.INSTANCE
            .getFromDatabase(blob, Property.KILL_TARGET, List.of("tried to kill %s but they couldn't because that's bad manners!"), PropertyMapping::getAsListOfString);

        eb.setDescription(String.format("… %s", targetStrings.get(random.nextInt(targetStrings.size()))).formatted(target));
        event.replyEmbeds(eb.build()).queue();
    }

    private void performLockOut(EventBlob blob, long timeout) {
        DatabaseManager.INSTANCE.updateProperty(blob, Property.KILL_TIMEOUT, true);
        DatabaseManager.INSTANCE.updateProperty(blob, Property.KILL_END_DATE, OffsetDateTime.now().plus(timeout, ChronoUnit.MILLIS));
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
            """, Mastermind.USER, CommandCategory.FUN,
            Metadata.parseDate("2022-08-24T11:10Z"),
            Metadata.parseDate("2023-07-02T21:02Z")
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
        EmbedBuilder eb = blob.getStandardEmbed(blob.getMember().getEffectiveName())
            .setFooter("Requested by " + blob.getMemberName(), blob.getMemberEffectiveAvatarUrl());

        switch (subcommand) {
            case "random" -> killRandom(event, blob, eb);

            case "suggest" -> {
                boolean isRandom = event.getOption("is-random", true, OptionMapping::getAsBoolean);

                Modal modal = isRandom ? new KillSuggest.Random().getConstructedModal() : new KillSuggest.Target().getConstructedModal();

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