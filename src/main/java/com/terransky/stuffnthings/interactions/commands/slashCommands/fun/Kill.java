package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
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
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

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

        if (!event.getUser().getId().equals(Config.getDeveloperId()) &&
            !blob.getGuildId().equals(Config.getSupportGuildId())) {
            long maxKills = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.KILLS_MAX)
                .map(o -> (long) o)
                .orElse(5L);
            long attempts = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.KILL_ATTEMPTS)
                .map(o -> (long) o)
                .orElse(0L);
            long timeout = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.KILLS_TIMEOUT)
                .map(o -> (long) o)
                .orElse(0L);
            boolean isUnderTimeout = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.KILL_TIMEOUT)
                .map(o -> (boolean) o)
                .orElse(false);

            if (attempts >= maxKills) {
                event.replyEmbeds(
                    eb.setDescription(String.format("… tried to kill %s but they couldn't because that's bad manners!", target))
                        .build()
                ).setEphemeral(true).queue();
                return;
            }

            DatabaseManager.INSTANCE.updateProperty(blob, Property.KILL_ATTEMPTS, attempts + 1);
            if (!isUnderTimeout)
                performLockOut(blob, timeout);
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
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                DatabaseManager.INSTANCE.updateProperty(blob, Property.KILL_ATTEMPTS, 0);
                DatabaseManager.INSTANCE.updateProperty(blob, Property.KILL_TIMEOUT, false);
            }
        }, timeout);
    }

    @Override
    public String getName() {
        return "kill";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        return new Metadata(this.getName(), "Time to un-alive random members!", """
            Take a chance and try to kill a random member in your server! Or just *that guy* cause they've been annoying you recently.
            """, Mastermind.USER,
            CommandCategory.FUN,
            format.parse("24-08-2022_11:10"),
            format.parse("27-1-2023_21:38")
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
}