package com.terransky.stuffnthings.interactions.commands.slashCommands.general;

import com.terransky.stuffnthings.Managers;
import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.managers.SlashIManager;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.Metadata;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import com.terransky.stuffnthings.utilities.general.configobjects.CoreConfig;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;

public class About implements ICommandSlash {

    private final CoreConfig CORE_CONFIG = StuffNThings.getConfig().getCore();

    @NotNull
    private static String getUptime(@NotNull RuntimeMXBean mxBean) {
        Duration duration = Duration.ofMillis(mxBean.getUptime());
        long days = duration.toDaysPart();
        int hours = duration.toHoursPart();
        int minutes = duration.toMinutesPart();
        int seconds = duration.toSecondsPart();
        int millis = duration.toMillisPart();

        StringBuilder uptime = new StringBuilder();
        if (days != 0)
            uptime.append("%s day%s ".formatted(days, days > 1 ? "s" : ""));
        if (hours != 0)
            uptime.append("%s hour%s ".formatted(hours, hours > 1 ? "s" : ""));
        if (minutes != 0)
            uptime.append("%s minute%s ".formatted(minutes, minutes > 1 ? "s" : ""));
        uptime.append("%s.%s secs".formatted(seconds, millis));
        return uptime.toString();
    }

    @Override
    public String getName() {
        return "about";
    }

    @Override
    public Metadata getMetadata() {
        SlashIManager slashManager = Managers.INSTANCE.getSlashManager();
        return new Metadata(this.getName(), "What am I? Who am I?", """
            The about command. What else did you expect?

            **NOTE:** If you choose from more than one, the command will prioritize from highest to lowest: `command-one`, `command-two`, `command-three`, and then `command-four`.
            """, Mastermind.DEVELOPER,
            CommandCategory.GENERAL,
            Metadata.parseDate(2022, 8, 24, 11, 10),
            Metadata.parseDate(2024, 8, 20, 12, 3)
        )
            .addOptions(
                new OptionData(OptionType.STRING, "command-one", "Get more info on a Command.")
                    .addChoices(slashManager.getCommandsAsChoices(SlashIManager.SlashSet.ONE)),
                new OptionData(OptionType.STRING, "command-two", "Get more info on a Command.")
                    .addChoices(slashManager.getCommandsAsChoices(SlashIManager.SlashSet.TWO)),
                new OptionData(OptionType.STRING, "command-three", "Get more info on a Command.")
                    .addChoices(slashManager.getCommandsAsChoices(SlashIManager.SlashSet.THREE)),
                new OptionData(OptionType.STRING, "command-four", "Get more info on a Command.")
                    .addChoices(slashManager.getCommandsAsChoices(SlashIManager.SlashSet.FOUR))
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        event.deferReply().queue();
        Optional<String> ifCommandOne = Optional.ofNullable(event.getOption("command-one", OptionMapping::getAsString));
        Optional<String> ifCommandTwo = Optional.ofNullable(event.getOption("command-two", OptionMapping::getAsString));
        Optional<String> ifCommandThree = Optional.ofNullable(event.getOption("command-three", OptionMapping::getAsString));
        Optional<String> ifCommandFour = Optional.ofNullable(event.getOption("command-four", OptionMapping::getAsString));

        if (ifCommandOne.isPresent() || ifCommandTwo.isPresent() || ifCommandThree.isPresent() || ifCommandFour.isPresent()) {
            getCommandInfo(event, blob, ifCommandOne.orElse(
                ifCommandTwo.orElse(
                    ifCommandThree.orElse(
                        ifCommandFour.orElse(getName())
                    )
                )
            ));
            return;
        }

        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
        String uptime = getUptime(mxBean);

        SlashIManager manager = Managers.INSTANCE.getSlashManager();
        int commandCnt = manager.getSlashCommandCount();
        int guildCommandCnt = manager.getSlashCommandCount(blob.getGuildIdLong());
        commandCnt += guildCommandCnt;
        long guildCount;
        long userCount;
        String serviced = CORE_CONFIG.getEnableDatabase() ? " Serviced" : "";

        guildCount = DatabaseManager.INSTANCE.getGuildsCount(event.getJDA());
        userCount = DatabaseManager.INSTANCE.getUserCount(event.getJDA());

        event.getHook().sendMessageEmbeds(
            blob.getStandardEmbed(event.getJDA().getSelfUser().getName(), CORE_CONFIG.getRepoLink())
                .setDescription("""
                    > *Who am I?*
                    I am %s
                    > *What am I?*
                    An entertainment bot.
                    > *I think I need help...*
                    [Then get some](%s)
                    """.formatted(event.getJDA().getSelfUser().getAsMention(), CORE_CONFIG.getSupportGuild().getInvite()))
                .setThumbnail(CORE_CONFIG.getLogoUrl())
                .addField("Servers", String.format("%d servers", guildCount), true)
                .addField("Users" + serviced, String.format("%s user%s", userCount, userCount > 1 ? "s" : ""), true)
                .addField("Your Shard", event.getJDA().getShardInfo().getShardString(), true)
                .addField("Start Time", Timestamp.getDateAsTimestamp(new Date(mxBean.getStartTime())), false)
                .addField("Uptime", uptime, false)
                .addField("Total Commands", "%s on %s\n%s of which are guild commands"
                    .formatted(
                        commandCnt,
                        blob.getGuild().getName(),
                        guildCommandCnt == 0 ? "None" : guildCommandCnt
                    ), false)
                .build()
        ).queue();
    }

    private void getCommandInfo(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, String command) {
        Optional<Metadata> ifMetadata = Managers.INSTANCE.getSlashManager().getMetadata(command);
        Metadata metadata = ifMetadata.orElse(getMetadata());
        String commandName = metadata.getCommandNameReadable();

        if (!metadata.getDefaultPerms().isEmpty() && !blob.getMember().hasPermission(metadata.getDefaultPerms())) {
            event.replyEmbeds(
                blob.getStandardEmbed("About Command - %s".formatted(commandName))
                    .setDescription("You don't have access to this command to see its details.")
                    .setFooter(blob.getMemberName(), blob.getMemberEffectiveAvatarUrl())
                    .build()
            ).queue();
            return;
        }

        String[] timestamps = {
            metadata.getCreatedAsTimestamp(),
            metadata.getCreatedAsTimestamp(Timestamp.RELATIVE),
            metadata.getLastUpdatedAsTimestamp(),
            metadata.getLastUpdatedAsTimestamp(Timestamp.RELATIVE)
        };

        event.getHook().sendMessageEmbeds(
            blob.getStandardEmbed("About Command - %s".formatted(commandName))
                .setDescription(metadata.getLongDescription())
                .addField("Mastermind", metadata.getMastermind().getWho(), true)
                .addField("Module", metadata.getCategory().getName(), true)
                .addField("Implementation Date", "%s (%s)".formatted(timestamps[0], timestamps[1]), false)
                .addField("Last Edited", "%s (%s)".formatted(timestamps[2], timestamps[3]), false)
                .build()
        ).queue();
    }
}
