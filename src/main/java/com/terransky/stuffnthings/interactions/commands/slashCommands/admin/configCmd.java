package com.terransky.stuffnthings.interactions.commands.slashCommands.admin;

import com.terransky.stuffnthings.database.DatabaseManager;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.DBProperty;
import com.terransky.stuffnthings.utilities.general.LogList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class configCmd implements ICommandSlash {
    private final Logger log = LoggerFactory.getLogger(configCmd.class);

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        return new Metadata(this.getName(), "The config manager.", """
            Sets certain constant values of specific commands.
            """,
            Mastermind.DEVELOPER,
            CommandCategory.ADMIN,
            format.parse("28-08-2022_21:46"),
            format.parse("21-1-2023_11:09")
        )
            .addDefaultPerms(Permission.MANAGE_SERVER)
            .addSubcommandGroups(
                new SubcommandGroupData("kill", "Change the config settings for the kill command.")
                    .addSubcommands(
                        new SubcommandData("max-kills", "Get the max kills for the `/kill target` command.")
                            .addOptions(
                                new OptionData(OptionType.INTEGER, "set-max", "Set the max kills for the server.")
                                    .setRequiredRange(1, 99)
                            ),
                        new SubcommandData("timeout", "\"X\" amount of kills within \"Y\" amount of time… what's \"Y\"?")
                            .addOptions(
                                new OptionData(OptionType.INTEGER, "set-timeout", "Set the timeout of the kill command in whole minutes up to an hour.")
                                    .setRequiredRange(1, 60)
                            )
                    )
            );
    }

    @Override
    public boolean isWorking() {
        return Config.isDatabaseEnabled();
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        String subCommandGroup = event.getSubcommandGroup();
        if (subCommandGroup == null) throw new DiscordAPIException("No subcommand group was given.");

        if ("kill".equals(subCommandGroup)) {
            killConfig(event, blob);
        }
    }

    private void killConfig(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        String subcommand = event.getSubcommandName();
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl());

        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");

        switch (subcommand) {
            case "max-kills" -> updateKillMaxKills(event, blob, eb);

            case "timeout" -> updateKillTimeout(event, blob, eb);
        }
    }

    private void updateKillTimeout(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, EmbedBuilder eb) {
        event.deferReply(true).queue();
        Optional<Integer> ifNewTimeout = Optional.ofNullable(event.getOption("set-timeout", OptionMapping::getAsInt));
        int oldTimeout = DatabaseManager.INSTANCE.getFromDBInt(blob, DBProperty.KILLS_TIMEOUT).orElse(300000);
        long oldTimeoutMinutes = TimeUnit.MILLISECONDS.toMinutes(oldTimeout);


        if (ifNewTimeout.isEmpty()) {
            event.getHook().sendMessageEmbeds(eb.setTitle("Kill Config")
                .addField("Timeout", String.format("%d minutes", oldTimeoutMinutes), false)
                .build()
            ).queue();
            return;
        }
        int newTimeoutMinutes = ifNewTimeout.get(),
            newTimeoutMillis = (int) TimeUnit.MINUTES.toMillis(newTimeoutMinutes);

        if (newTimeoutMillis == oldTimeout) {
            log.debug("No change for {}", blob.getGuildId());
            event.getHook().sendMessageEmbeds(eb.setTitle("Config not Updated")
                .setDescription(String.format("Currently set Timeout is requested amount: `%d minutes`.", oldTimeoutMinutes))
                .build()
            ).queue();
            return;
        }

        try {
            DatabaseManager.INSTANCE.updateProperty(blob, DBProperty.KILLS_TIMEOUT, newTimeoutMillis);
        } catch (Exception e) {
            log.error("{}: {}", e.getClass().getName(), e.getMessage());
            LogList.error(Arrays.asList(e.getStackTrace()), log);
        }

        eb.setTitle("Config Updated")
            .addField("Old Timeout", String.valueOf(oldTimeout), true)
            .addField("New Timeout", String.valueOf(newTimeoutMinutes), true);

        event.getHook().sendMessageEmbeds(eb.build()).queue();
    }

    private void updateKillMaxKills(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, EmbedBuilder eb) {
        event.deferReply(true).queue();
        Optional<Integer> ifNewMax = Optional.ofNullable(event.getOption("set-max", OptionMapping::getAsInt));
        int oldMax = DatabaseManager.INSTANCE.getFromDBInt(blob, DBProperty.KILLS_MAX).orElse(5);

        if (ifNewMax.isEmpty()) {
            event.getHook().sendMessageEmbeds(eb.setTitle("Kill Config")
                .addField("Max Kills", String.valueOf(oldMax), false)
                .build()
            ).queue();
            return;
        }
        int newMax = ifNewMax.get();

        if (newMax == oldMax) {
            log.debug("No change for {}", blob.getGuildId());
            event.getHook().sendMessageEmbeds(eb.setTitle("Config not Updated")
                .setDescription("Currently set Max Kills is requested amount: `%d Max Kills`.".formatted(newMax))
                .build()
            ).queue();
            return;
        }

        try {
            DatabaseManager.INSTANCE.updateProperty(blob, DBProperty.KILLS_MAX, newMax);
        } catch (Exception e) {
            log.error("{}: {}", e.getClass().getName(), e.getMessage());
            LogList.error(Arrays.asList(e.getStackTrace()), log);
        }

        eb.setTitle("Config Updated")
            .addField("Old Max", String.valueOf(oldMax), true)
            .addField("New Max", String.valueOf(newMax), true);

        event.getHook().sendMessageEmbeds(eb.build()).queue();
    }
}
