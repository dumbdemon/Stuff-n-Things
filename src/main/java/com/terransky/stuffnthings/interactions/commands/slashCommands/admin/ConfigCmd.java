package com.terransky.stuffnthings.interactions.commands.slashCommands.admin;

import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.LogList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Icon;
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
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ConfigCmd implements ICommandSlash {
    private final Logger log = LoggerFactory.getLogger(ConfigCmd.class);

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
            format.parse("25-1-2023_12:55")
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
                            )
                    )
            );
    }

    @Override
    public boolean isWorking() {
        return Config.isDatabaseEnabled();
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl());

        switch (subcommand) {
            case "max-kills" -> updateKillMaxKills(event, blob, eb);
            case "timeout" -> updateKillTimeout(event, blob, eb);
            case "reporting-channel" -> updateReportingWebhook(event, blob, eb);
            case "reporting-response" -> updateReportingResponse(event, blob, eb);
        }
    }

    private void updateReportingWebhook(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, @NotNull EmbedBuilder eb) throws IOException {
        event.deferReply().queue();
        Optional<GuildChannelUnion> channel = Optional.ofNullable(event.getOption("channel", OptionMapping::getAsChannel));
        eb.setTitle(getNameReadable() + " - Reporting Channel");

        if (channel.isEmpty()) {
            Optional<Webhook> ifWebhook = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.REPORT_WEBHOOK)
                .map(hook -> (Webhook) hook);

            if (ifWebhook.isEmpty()) {
                event.getHook().sendMessageEmbeds(
                    eb.setDescription("Reporting has not been set up yet.")
                        .setColor(EmbedColors.getError())
                        .build()
                ).queue();
                return;
            }

            event.getHook().sendMessageEmbeds(
                eb.setDescription(String.format("Reporting to %s.", ifWebhook.get().getChannel().getAsMention()))
                    .build()
            ).queue();
            return;
        }
        GuildChannelUnion channelUnion = channel.get();

        if (!blob.getSelfMember().hasPermission(channelUnion.asStandardGuildChannel(), Permission.MANAGE_WEBHOOKS)) {
            event.getHook().sendMessageEmbeds(
                eb.setColor(EmbedColors.getError())
                    .setDescription(String.format("Unable to proceed. Missing permission to manage webhooks in %s.", channelUnion.getAsMention()))
                    .build()
            ).queue();
            return;
        }

        TextChannel textChannel = channelUnion.asTextChannel();
        List<Webhook> webhooks = textChannel.retrieveWebhooks().complete();
        webhooks.stream().filter(hook -> blob.getSelfMember().equals(hook.getOwner()))
            .findFirst()
            .ifPresent(hook -> hook.delete().queue());

        Webhook hook = textChannel.createWebhook("Message Reporting")
            .setAvatar(Icon.from(new URL(Config.getBotLogoURL()).openStream()))
            .complete();
        DatabaseManager.INSTANCE.updateProperty(blob, Property.REPORT_WEBHOOK, hook.getId());

        event.getHook().sendMessageEmbeds(
            eb.setDescription(String.format("The reporting channel was set to %s.", textChannel.getAsMention()))
                .build()
        ).queue();
    }

    private void updateReportingResponse(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, @NotNull EmbedBuilder eb) {
        event.deferReply(true).queue();
        Optional<String> ifResponse = Optional.ofNullable(event.getOption("report-message", OptionMapping::getAsString));
        eb.setTitle(getNameReadable() + " - Reporting Message");

        if (ifResponse.isEmpty()) {
            String response = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.REPORT_RESPONSE)
                .map(message -> (String) message)
                .orElse("Got it. Message has been reported.");

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
        event.deferReply(true).queue();
        Optional<Integer> ifNewTimeout = Optional.ofNullable(event.getOption("set-timeout", OptionMapping::getAsInt));
        long oldTimeout = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.KILLS_TIMEOUT)
            .map(o -> (long) o)
            .orElseGet(() -> TimeUnit.MINUTES.toMillis(10));
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
            log.error("{}: {}", e.getClass().getName(), e.getMessage());
            LogList.error(Arrays.asList(e.getStackTrace()), log);
        }

        eb.setTitle("Config Updated")
            .addField("Old Timeout", String.valueOf(oldTimeout), true)
            .addField("New Timeout", String.valueOf(newTimeoutMinutes), true);

        event.getHook().sendMessageEmbeds(eb.build()).queue();
    }

    private void updateKillMaxKills(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, @NotNull EmbedBuilder eb) {
        event.deferReply(true).queue();
        Optional<Long> ifNewMax = Optional.ofNullable(event.getOption("set-max", OptionMapping::getAsLong));
        long oldMax = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.KILLS_MAX)
            .map(o -> (Long) o)
            .orElse(5L);
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
                eb.setDescription("Currently set Max Kills is requested amount: `%d Max Kills`." .formatted(newMax))
                    .build()
            ).queue();
            return;
        }

        try {
            DatabaseManager.INSTANCE.updateProperty(blob, Property.KILLS_MAX, newMax);
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
