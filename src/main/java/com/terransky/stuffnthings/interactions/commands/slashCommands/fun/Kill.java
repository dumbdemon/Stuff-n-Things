package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.database.helpers.PropertyMapping;
import com.terransky.stuffnthings.database.helpers.entry.UserGuildEntry;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interactions.modals.KillSuggest;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.StandardResponse;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import com.terransky.stuffnthings.utilities.general.configobjects.CoreConfig;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class Kill extends SlashCommandInteraction {

    private final CoreConfig CORE_CONFIG = StuffNThings.getConfig().getCore();

    public Kill() {
        super("kill", "Time to un-alive random members!",
            Mastermind.USER,
            CommandCategory.FUN,
            parseDate(2022, 8, 24, 11, 10),
            parseDate(2025, 12, 28, 1, 58)
        );
        addSubcommands(
            new SubcommandData("random", "Try your hand at un-aliving someone!"),
            new SubcommandData("target", "Target someone for a kill.")
                .addOption(OptionType.USER, "target", "Your target", true),
            new SubcommandData("suggest", "Suggest a kill-string. Use \"%s\" to represent targets. Up to four can be in a kill-string.")
                .addOptions(
                    new OptionData(OptionType.BOOLEAN, "is-random", "Random or target?", true)
                )
        );
    }

    private void killRandom(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) {
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

        event.replyComponents(StandardResponse.getResponseContainer(this, "… " + message)).queue();
    }

    private void killTarget(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, Random random) {
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

        if (!event.getUser().getId().equals(CORE_CONFIG.getOwnerId()) &&
            blob.getGuildIdLong() != CORE_CONFIG.getSupportGuild().getId() &&
            !CORE_CONFIG.getTestingMode()) {
            long maxKills = entry.getMaxKills();
            long attempts = entry.getKillAttempts();

            if (attempts >= maxKills) {
                event.replyComponents(
                    StandardResponse.getResponseContainer(this,
                        TextDisplay.ofFormat("… tried to kill %s but they couldn't because that's bad manners!\nNext available kill %s.",
                            target,
                            Timestamp.getDateAsTimestamp(endTime, Timestamp.RELATIVE))
                    )
                ).setEphemeral(true).queue();
                return;
            }

            DatabaseManager.INSTANCE.updateProperty(blob, Property.KILL_ATTEMPTS, attempts + 1);
            if (entry.getKillUnderTo() == null || !entry.getKillUnderTo())
                performLockOut(blob, entry.getServerTimeout());
        }
        List<String> targetStrings = DatabaseManager.INSTANCE
            .getFromDatabase(blob, Property.KILL_TARGET, List.of("tried to kill %s but they couldn't because that's bad manners!"), PropertyMapping::getAsListOfString);

        event.replyComponents(StandardResponse.getResponseContainer(
                this,
                TextDisplay.ofFormat(
                    "… %s",
                    targetStrings.get(random.nextInt(targetStrings.size())).formatted(target))
            )
        ).queue();
    }

    private void performLockOut(EventBlob blob, long timeout) {
        DatabaseManager.INSTANCE.updateProperty(blob, Property.KILL_TIMEOUT, true);
        DatabaseManager.INSTANCE.updateProperty(blob, Property.KILL_END_DATE, OffsetDateTime.now().plus(timeout, ChronoUnit.MILLIS));
        new Timer().schedule(new ResetKillProperties(blob, getClass()), timeout);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");

        Random random = new Random(new Date().getTime());

        switch (subcommand) {
            case "random" -> killRandom(event, blob);

            case "suggest" -> {
                boolean isRandom = event.getOption("is-random", true, OptionMapping::getAsBoolean);

                Modal modal = isRandom ? new KillSuggest.Random().getContructedModal() : new KillSuggest.Target().getContructedModal();

                event.replyModal(modal).queue();
            }

            case "target" -> killTarget(event, blob, random);
        }
    }

    static class ResetKillProperties extends TimerTask {

        private final String userId;
        private final String guildId;
        private final Logger log;

        protected ResetKillProperties(@NotNull EventBlob blob, Class<? extends SlashCommandInteraction> aClass) {
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