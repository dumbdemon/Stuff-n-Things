package com.terransky.stuffnthings.interactions.commands.slashCommands.admin;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.dataSources.jokeAPI.Flags;
import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.database.helpers.PropertyMapping;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ConfigCmd extends SlashCommandInteraction {
    private final Logger log = LoggerFactory.getLogger(ConfigCmd.class);

    public ConfigCmd() {
        super("config", "Server config manager.", Mastermind.DEVELOPER, CommandCategory.ADMIN,
            parseDate(2022, 8, 28, 21, 46),
            OffsetDateTime.now()
        );
        setDefaultMemberPermissions(Permission.MANAGE_SERVER);
        setWorking(StuffNThings.getConfig().getCore().getEnableDatabase());
        addSubcommandGroups(
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

    @Contract("_, _ -> new")
    @NotNull
    private static List<ContainerChildComponent> getComponents(long oldValue, long newValue) {
        return List.of(
            TextDisplay.of("## Config Updated"),
            TextDisplay.ofFormat("**Old Timeout** - %s", oldValue),
            TextDisplay.ofFormat("**New Timeout** - %s", newValue)
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");

        switch (subcommand) {
            case "verbose" -> updateVerbose(event, blob);
            case "max-kills" -> updateKillMaxKills(event, blob);
            case "timeout" -> updateKillTimeout(event, blob);
            case "reporting-channel" -> updateReportingWebhook(event, blob);
            case "reporting-response" -> updateReportingResponse(event, blob);
            case "view-flags", "set-flags" -> updateFlags(event, blob, subcommand);
        }
    }

    private void updateVerbose(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) {
        Optional<Boolean> verbose = Optional.ofNullable(event.getOption("do-verbose", OptionMapping::getAsBoolean));
        boolean databaseVerbose = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.VERBOSE, true, PropertyMapping::getAsBoolean);
        String title = getNameReadable() + " - Verbose Iteration";

        if (verbose.isEmpty()) {
            event.replyComponents(
                StandardResponse.getResponseContainer(title, "Verbose is set to: **" + (databaseVerbose ? "True" : "False") + "**!")
            ).queue();
            return;
        }

        if (verbose.get() == databaseVerbose) {
            event.replyComponents(
                StandardResponse.getResponseContainer(title, "No change was made!\nVerbose is set to: **" + (databaseVerbose ? "True" : "False") + "**!")
            ).queue();
            return;
        }

        DatabaseManager.INSTANCE.updateProperty(blob, Property.VERBOSE, verbose.get());

        event.replyComponents(
            StandardResponse.getResponseContainer(title, "Verbose was changed to: **" + (verbose.get() ? "True" : "False") + "**!")
        ).queue();
    }

    private void updateFlags(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, @NotNull String subcommand) {
        Flags serverFlags = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.JOKE_FLAGS, new Flags(), PropertyMapping::getAsFlags);
        String title;

        if (subcommand.equals("view-flags")) {
            title = getNameReadable() + " - View Joke Flags";
        } else {
            title = getNameReadable() + " - Set Joke Flags";

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
        List<ContainerChildComponent> children = new ArrayList<>();

        if (serverFlags.getSafeMode()) {
            children.add(TextDisplay.of("**NOTICE: Server is in safe mode.**"));
            children.add(Separator.createDivider(Separator.Spacing.SMALL));
        }

        DatabaseManager.INSTANCE.updateProperty(blob, Property.JOKE_FLAGS, serverFlags);
        String allow = ":white_check_mark: **Allowed**",
            deny = ":ripx: ***Denied***";

        children.addAll(List.of(
            TextDisplay.of(String.format("Religious - %s", serverFlags.getReligious() ? deny : allow)),
            TextDisplay.of(String.format("Political - %s", serverFlags.getPolitical() ? deny : allow)),
            TextDisplay.of(String.format("Racist - %s", serverFlags.getRacist() ? deny : allow)),
            TextDisplay.of(String.format("Sexist - %s", serverFlags.getSexist() ? deny : allow))
        ));

        event.replyComponents(
            StandardResponse.getResponseContainer(title, children)
        ).queue();
    }

    private void updateReportingWebhook(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws IOException, ExecutionException, InterruptedException {
        event.deferReply().queue();
        Optional<GuildChannelUnion> channel = Optional.ofNullable(event.getOption("channel", OptionMapping::getAsChannel));
        String title = getNameReadable() + " - Report Webhook";

        if (!blob.getSelfMember().hasPermission(Permission.MANAGE_WEBHOOKS)) {
            event.getHook().sendMessageComponents(
                StandardResponse.getResponseContainer(title, "Unable to proceed. Missing permission to manage webhooks.", BotColors.ERROR)
            ).queue();
            return;
        }

        if (channel.isEmpty()) {
            String webhookId = (String) DatabaseManager.INSTANCE.getFromDatabase(blob, Property.REPORT_WEBHOOK).orElse(null);
            List<Webhook> webhooks = blob.getGuild().retrieveWebhooks().submit().get();
            Optional<Webhook> webhook = webhooks.stream().filter(hook -> hook.getId().equals(webhookId)).findFirst();

            if (webhook.isEmpty()) {
                event.getHook().sendMessageComponents(
                    StandardResponse.getResponseContainer(title, "Unable to proceed. Missing webhook.", BotColors.ERROR)
                ).queue();
                return;
            }

            event.getHook().sendMessageComponents(
                StandardResponse.getResponseContainer(title, String.format("Reporting to %s.", webhook.get().getChannel().getAsMention()))
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
            .setAvatar(Icon.from(new URL(StuffNThings.getConfig().getCore().getLogoUrl()).openStream()))
            .submit().get();
        DatabaseManager.INSTANCE.updateProperty(blob, Property.REPORT_WEBHOOK, hook.getId());

        event.getHook().sendMessageComponents(
            StandardResponse.getResponseContainer(title, "Report Webhook has been sent.")
        ).queue();
    }

    private void updateReportingResponse(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) {
        event.deferReply().queue();
        Optional<String> ifResponse = Optional.ofNullable(event.getOption("report-message", OptionMapping::getAsString));
        String title = getNameReadable() + " - Report Response";

        if (ifResponse.isEmpty()) {
            String response = DatabaseManager.INSTANCE
                .getFromDatabase(blob, Property.REPORT_RESPONSE, "Got it. Message has been reported.", PropertyMapping::getAsString);

            event.getHook().sendMessageComponents(
                StandardResponse.getResponseContainer(title, String.format("Current Set: ```%s```", response))
            ).queue();
            return;
        }

        DatabaseManager.INSTANCE.updateProperty(blob, Property.REPORT_RESPONSE, ifResponse.get());

        event.getHook().sendMessageComponents(
            StandardResponse.getResponseContainer(title, String.format("Reporting response was updated successfully to: ```%s```", ifResponse.get()))
        ).queue();
    }

    private void updateKillTimeout(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) {
        event.deferReply().queue();
        Optional<Integer> ifNewTimeout = Optional.ofNullable(event.getOption("set-timeout", OptionMapping::getAsInt));
        long oldTimeout = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.KILLS_TIMEOUT, TimeUnit.MINUTES.toMillis(10), PropertyMapping::getAsLong);
        long oldTimeoutMinutes = TimeUnit.MILLISECONDS.toMinutes(oldTimeout);
        String title = getNameReadable() + " - Kill Timeout";

        if (ifNewTimeout.isEmpty()) {
            event.getHook().sendMessageComponents(
                StandardResponse.getResponseContainer(title, String.format("Current Set: ```%s minutes````", oldTimeout))
            ).queue();
            return;
        }
        int newTimeoutMinutes = ifNewTimeout.get(),
            newTimeoutMillis = (int) TimeUnit.MINUTES.toMillis(newTimeoutMinutes);

        if (newTimeoutMillis == oldTimeout) {
            logNoChange(blob);
            event.getHook().sendMessageComponents(
                StandardResponse.getResponseContainer(title, String.format("Currently set Timeout is requested amount: `%d minutes`.", oldTimeoutMinutes),
                    BotColors.SUB_DEFAULT)
            ).queue();
            return;
        }

        try {
            DatabaseManager.INSTANCE.updateProperty(blob, Property.KILLS_TIMEOUT, newTimeoutMillis);
        } catch (Exception e) {
            logPropertyUpdateFailure(Property.KILLS_TIMEOUT, e);
        }

        event.getHook().sendMessageComponents(StandardResponse.getResponseContainer(title, getComponents(oldTimeoutMinutes, newTimeoutMinutes))).queue();
    }

    private void logNoChange(@NotNull EventBlob blob) {
        log.debug("No change for {}", blob.getGuildId());
    }

    private void updateKillMaxKills(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) {
        event.deferReply().queue();
        Optional<Long> ifNewMax = Optional.ofNullable(event.getOption("set-max", OptionMapping::getAsLong));
        long oldMax = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.KILLS_MAX, 5L, PropertyMapping::getAsLong);
        String title = getNameReadable() + " - Max Kills";

        if (ifNewMax.isEmpty()) {
            event.getHook().sendMessageComponents(
                StandardResponse.getResponseContainer(title, String.format("Current Set: ```%s````", oldMax))
            ).queue();
            return;
        }
        long newMax = ifNewMax.get();

        if (newMax == oldMax) {
            logNoChange(blob);
            event.getHook().sendMessageComponents(
                StandardResponse.getResponseContainer(title, String.format("Currently set Max Kills is requested amount: `%d Max Kills`.", newMax),
                    BotColors.SUB_DEFAULT
                )
            ).queue();
            return;
        }

        try {
            DatabaseManager.INSTANCE.updateProperty(blob, Property.KILLS_MAX, newMax);
        } catch (Exception e) {
            logPropertyUpdateFailure(Property.KILLS_MAX, e);
        }

        event.getHook().sendMessageComponents(StandardResponse.getResponseContainer(title, getComponents(oldMax, newMax))).queue();
    }

    private void logPropertyUpdateFailure(Property property, Exception e) {
        log.error(String.format("Unable to update property %s", property), e);
    }
}
