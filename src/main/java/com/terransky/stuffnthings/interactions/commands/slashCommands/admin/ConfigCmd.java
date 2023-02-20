package com.terransky.stuffnthings.interactions.commands.slashCommands.admin;

import com.terransky.stuffnthings.dataSources.jokeAPI.Flags;
import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.database.helpers.PropertyMapping;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ConfigCmd implements ICommandSlash {
    private final Logger log = LoggerFactory.getLogger(ConfigCmd.class);

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(this.getName(), "The config manager.", """
            Sets certain constant values of specific commands.
            """,
            Mastermind.DEVELOPER,
            CommandCategory.ADMIN,
            Metadata.parseDate("2022-08-28T21:46Z"),
            Metadata.parseDate("2022-02-19T19:01Z")
        )
            .addDefaultPerms(Permission.MANAGE_SERVER)
            .addSubcommandGroups(
                new SubcommandGroupData("general", "Change settings shared by some commands.")
                    .addSubcommands(
                        new SubcommandData("verbose", "Do not iterate through anything. NO SPAMMING!")
                            .addOption(OptionType.BOOLEAN, "do-verbose", "Do not iterate through anything. NO SPAMMING!")
                    ),
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
                    ),
                new SubcommandGroupData("report-message", "Change the config for reporting a message.")
                    .addSubcommands(
                        new SubcommandData("reporting-channel", "Set, change, or view the current reporting channel.")
                            .addOptions(
                                new OptionData(OptionType.CHANNEL, "channel", "The channel the messages are reporting to.")
                                    .setChannelTypes(ChannelType.TEXT)
                            ),
                        new SubcommandData("reporting-response", "Set, change, or view the response the bot gives when reporting.")
                            .addOptions(
                                new OptionData(OptionType.STRING, "report-message", "The message the bot gives when a user reports.")
                                    .setRequiredLength(10, MessageEmbed.DESCRIPTION_MAX_LENGTH / 4)
                            )
                    ),
                new SubcommandGroupData("joke-flags", "Allow or deny certain flags from being shown.")
                    .addSubcommands(
                        new SubcommandData("view-flags", "View current flags"),
                        new SubcommandData("set-flags", "Allow/Deny flags")
                            .addOptions(
                                new OptionData(OptionType.BOOLEAN, "religious", "Allow/Deny religious jokes.", true),
                                new OptionData(OptionType.BOOLEAN, "political", "Allow/Deny political jokes.", true),
                                new OptionData(OptionType.BOOLEAN, "racist", "Allow/Deny racist jokes.", true),
                                new OptionData(OptionType.BOOLEAN, "sexist", "Allow/Deny sexist jokes.", true),
                                new OptionData(OptionType.BOOLEAN, "safe-mode", "Set the entire server to safe mode.")
                            )
                    )
            );
    }

    @Override
    public boolean isWorking() {
        return Config.isDatabaseEnabled();
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl());

        switch (subcommand) {
            case "verbose" -> updateVerbose(event, blob, eb);
            case "max-kills" -> updateKillMaxKills(event, blob, eb);
            case "timeout" -> updateKillTimeout(event, blob, eb);
            case "reporting-channel" -> updateReportingWebhook(event, blob, eb);
            case "reporting-response" -> updateReportingResponse(event, blob, eb);
            case "view-flags", "set-flags" -> updateFlags(event, blob, eb, subcommand);
        }
    }

    private void updateVerbose(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, @NotNull EmbedBuilder eb) {
        Optional<Boolean> verbose = Optional.ofNullable(event.getOption("do-verbose", OptionMapping::getAsBoolean));
        boolean databaseVerbose = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.VERBOSE, true, PropertyMapping::getAsBoolean);
        eb.setTitle(getNameReadable() + " - Verbose Iteration");

        if (verbose.isEmpty()) {
            event.replyEmbeds(
                eb.setDescription("Verbose is set to: **" + (databaseVerbose ? "True" : "False") + "**!")
                    .build()
            ).queue();
            return;
        }

        if (verbose.get() == databaseVerbose) {
            event.replyEmbeds(
                eb.setDescription("No change was made!\nVerbose is set to: **" + (databaseVerbose ? "True" : "False") + "**!")
                    .build()
            ).queue();
            return;
        }

        DatabaseManager.INSTANCE.updateProperty(blob, Property.VERBOSE, verbose.get());

        event.replyEmbeds(
            eb.setDescription("Verbose was changed to: **" + (verbose.get() ? "True" : "False") + "**!")
                .build()
        ).queue();
    }

    private void updateFlags(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, @NotNull EmbedBuilder eb, @NotNull String subcommand) {
        Flags serverFlags = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.JOKE_FLAGS, new Flags(), PropertyMapping::getAsFlags);

        if (subcommand.equals("view-flags")) {
            eb.setTitle(getNameReadable() + " - View Joke Flags");
        } else {
            eb.setTitle(getNameReadable() + " - Set Joke Flags");

            boolean religious = !event.getOption("religious", true, OptionMapping::getAsBoolean);
            boolean political = !event.getOption("political", true, OptionMapping::getAsBoolean);
            boolean racist = !event.getOption("racist", true, OptionMapping::getAsBoolean);
            boolean sexist = !event.getOption("sexist", true, OptionMapping::getAsBoolean);
            boolean safeMode = event.getOption("safe-mode", false, OptionMapping::getAsBoolean);

            serverFlags.setReligious(religious);
            serverFlags.setPolitical(political);
            serverFlags.setRacist(racist);
            serverFlags.setSexist(sexist);
            serverFlags.setSafeMode(safeMode);
        }

        if (serverFlags.getSafeMode()) {
            eb.setDescription("**NOTICE: Server is in safe mode.**");
        }

        DatabaseManager.INSTANCE.updateProperty(blob, Property.JOKE_FLAGS, serverFlags);
        String allow = ":white_check_mark: **Allowed**",
            deny = ":x: ***Denied***";

        event.replyEmbeds(
            eb.addField("Religious", serverFlags.getReligious() ? deny : allow, false)
                .addField("Political", serverFlags.getPolitical() ? deny : allow, false)
                .addField("Racist", serverFlags.getRacist() ? deny : allow, false)
                .addField("Sexist", serverFlags.getSexist() ? deny : allow, false)
                .build()
        ).queue();
    }

    private void updateReportingWebhook(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, @NotNull EmbedBuilder eb) throws IOException, ExecutionException, InterruptedException {
        event.deferReply().queue();
        Optional<GuildChannelUnion> channel = Optional.ofNullable(event.getOption("channel", OptionMapping::getAsChannel));
        eb.setTitle(getNameReadable() + " - Reporting Channel");

        if (!blob.getSelfMember().hasPermission(Permission.MANAGE_WEBHOOKS)) {
            event.getHook().sendMessageEmbeds(
                eb.setColor(EmbedColors.getError())
                    .setDescription("Unable to proceed. Missing permission to manage webhooks.")
                    .build()
            ).queue();
            return;
        }

        if (channel.isEmpty()) {
            String webhookId = (String) DatabaseManager.INSTANCE.getFromDatabase(blob, Property.REPORT_WEBHOOK).orElse(null);
            List<Webhook> webhooks = blob.getGuild().retrieveWebhooks().submit().get();
            Optional<Webhook> webhook = webhooks.stream().filter(hook -> hook.getId().equals(webhookId)).findFirst();

            if (webhook.isEmpty()) {
                event.getHook().sendMessageEmbeds(
                    eb.setDescription("Reporting has not been set up yet.")
                        .setColor(EmbedColors.getError())
                        .build()
                ).queue();
                return;
            }

            event.getHook().sendMessageEmbeds(
                eb.setDescription(String.format("Reporting to %s.", webhook.get().getChannel().getAsMention()))
                    .build()
            ).queue();
            return;
        }

        TextChannel textChannel = channel.get().asTextChannel();
        textChannel.retrieveWebhooks().submit()
            .whenComplete((webhooks, throwable) -> {
                if (throwable == null)
                    webhooks.stream().filter(hook -> blob.getSelfMember().equals(hook.getOwner()))
                        .findFirst()
                        .ifPresent(hook -> hook.delete().queue());
                else log.error("Couldn't obtain webhooks", throwable);
            });

        Webhook hook = textChannel.createWebhook("Message Reporting")
            .setAvatar(Icon.from(new URL(Config.getBotLogoURL()).openStream()))
            .submit().get();
        DatabaseManager.INSTANCE.updateProperty(blob, Property.REPORT_WEBHOOK, hook.getId());

        event.getHook().sendMessageEmbeds(
            eb.setDescription(String.format("The reporting channel was set to %s.", textChannel.getAsMention()))
                .build()
        ).queue();
    }

    private void updateReportingResponse(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, @NotNull EmbedBuilder eb) {
        event.deferReply().queue();
        Optional<String> ifResponse = Optional.ofNullable(event.getOption("report-message", OptionMapping::getAsString));
        eb.setTitle(getNameReadable() + " - Reporting Message");

        if (ifResponse.isEmpty()) {
            String response = DatabaseManager.INSTANCE
                .getFromDatabase(blob, Property.REPORT_RESPONSE, "Got it. Message has been reported.", PropertyMapping::getAsString);

            event.getHook().sendMessageEmbeds(
                eb.setDescription("Current Set: ```" + response + "```")
                    .build()
            ).queue();
            return;
        }

        DatabaseManager.INSTANCE.updateProperty(blob, Property.REPORT_RESPONSE, ifResponse.get());

        event.getHook().sendMessageEmbeds(
            eb.setDescription("Reporting response was updated successfully to: ```" + ifResponse.get() + "```")
                .build()
        ).queue();
    }

    private void updateKillTimeout(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, @NotNull EmbedBuilder eb) {
        event.deferReply().queue();
        Optional<Integer> ifNewTimeout = Optional.ofNullable(event.getOption("set-timeout", OptionMapping::getAsInt));
        long oldTimeout = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.KILLS_TIMEOUT, TimeUnit.MINUTES.toMillis(10), PropertyMapping::getAsLong);
        long oldTimeoutMinutes = TimeUnit.MILLISECONDS.toMinutes(oldTimeout);
        eb.setTitle(getNameReadable() + " - Kill Timeout");

        if (ifNewTimeout.isEmpty()) {
            event.getHook().sendMessageEmbeds(
                eb.addField("Timeout", String.format("%d minutes", oldTimeoutMinutes), false)
                    .build()
            ).queue();
            return;
        }
        int newTimeoutMinutes = ifNewTimeout.get(),
            newTimeoutMillis = (int) TimeUnit.MINUTES.toMillis(newTimeoutMinutes);

        if (newTimeoutMillis == oldTimeout) {
            log.debug("No change for {}", blob.getGuildId());
            event.getHook().sendMessageEmbeds(
                eb.setDescription(String.format("Currently set Timeout is requested amount: `%d minutes`.", oldTimeoutMinutes))
                    .build()
            ).queue();
            return;
        }

        try {
            DatabaseManager.INSTANCE.updateProperty(blob, Property.KILLS_TIMEOUT, newTimeoutMillis);
        } catch (Exception e) {
            log.error(String.format("Unable to update property %s", Property.KILLS_TIMEOUT), e);
        }

        eb.setTitle("Config Updated")
            .addField("Old Timeout", String.valueOf(oldTimeoutMinutes), true)
            .addField("New Timeout", String.valueOf(newTimeoutMinutes), true);

        event.getHook().sendMessageEmbeds(eb.build()).queue();
    }

    private void updateKillMaxKills(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, @NotNull EmbedBuilder eb) {
        event.deferReply().queue();
        Optional<Long> ifNewMax = Optional.ofNullable(event.getOption("set-max", OptionMapping::getAsLong));
        long oldMax = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.KILLS_MAX, 5L, PropertyMapping::getAsLong);
        eb.setTitle(getNameReadable() + " - Max Kills");

        if (ifNewMax.isEmpty()) {
            event.getHook().sendMessageEmbeds(
                eb.addField("Max Kills", String.valueOf(oldMax), false)
                    .build()
            ).queue();
            return;
        }
        long newMax = ifNewMax.get();

        if (newMax == oldMax) {
            log.debug("No change for {}", blob.getGuildId());
            event.getHook().sendMessageEmbeds(
                eb.setDescription("Currently set Max Kills is requested amount: `%d Max Kills`.".formatted(newMax))
                    .build()
            ).queue();
            return;
        }

        try {
            DatabaseManager.INSTANCE.updateProperty(blob, Property.KILLS_MAX, newMax);
        } catch (Exception e) {
            log.error(String.format("Unable to update property %s", Property.KILLS_MAX), e);
        }

        eb.setTitle("Config Updated")
            .addField("Old Max", String.valueOf(oldMax), true)
            .addField("New Max", String.valueOf(newMax), true);

        event.getHook().sendMessageEmbeds(eb.build()).queue();
    }
}
