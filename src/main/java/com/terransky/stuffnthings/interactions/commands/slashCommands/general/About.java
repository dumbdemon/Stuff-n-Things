package com.terransky.stuffnthings.interactions.commands.slashCommands.general;

import com.terransky.stuffnthings.ManagersManager;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.managers.SlashIManager;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.ParseException;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;

public class About implements ICommandSlash {
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
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        return new Metadata(this.getName(), "What am I? Who am I?", """
            The about command. What else did you expect?
            """, Mastermind.DEVELOPER,
            CommandCategory.GENERAL,
            format.parse("24-08-2022_11:10"),
            format.parse("25-1-2023_12:51")
        )
            .addOptions(
                new OptionData(OptionType.STRING, "command", "Get more info on a Command.")
                    .addChoices(new ManagersManager().getSlashManager().getCommandsAsChoices())
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        event.deferReply().queue();
        Optional<String> ifCommand = Optional.ofNullable(event.getOption("command", OptionMapping::getAsString));

        if (ifCommand.isPresent()) {
            try {
                getCommandInfo(event, blob, ifCommand.get());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
        String uptime = getUptime(mxBean);

        SlashIManager manager = new ManagersManager().getSlashManager();
        int commandCnt = manager.getSlashCommandCount();
        int guildCommandCnt = manager.getSlashCommandCount(blob.getGuildIdLong());
        commandCnt += guildCommandCnt;
        long guildCount;
        long userCount;
        String serviced;

        if (Config.isDatabaseEnabled()) {
            guildCount = DatabaseManager.INSTANCE.getGuildsCount();
            userCount = DatabaseManager.INSTANCE.getUserCount();
            serviced = " Serviced";
        } else {
            guildCount = event.getJDA().getGuildCache().stream().distinct().count();
            userCount = event.getJDA().getUserCache().stream().distinct().filter(user -> !user.isBot()).count();
            serviced = "";
        }

        event.getHook().sendMessageEmbeds(
            new EmbedBuilder()
                .setColor(EmbedColors.getDefault())
                .setDescription("""
                    > *Who am I?*
                    I am %s
                    > *What am I?*
                    An entertainment bot.
                    > *I think I need help...*
                    [Then get some](%s)
                    """.formatted(event.getJDA().getSelfUser().getAsMention(), Config.getSupportGuildInvite()))
                .setTitle(event.getJDA().getSelfUser().getName(), Config.getRepositoryURL())
                .setThumbnail(Config.getBotLogoURL())
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

    private void getCommandInfo(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, String command) throws ParseException {
        Optional<Metadata> ifMetadata = new ManagersManager().getSlashManager().getMetadata(command);
        Metadata metadata = ifMetadata.orElse(getMetadata());
        String commandName = metadata.getCommandNameReadable();

        if (!metadata.getDefaultPerms().isEmpty() && !blob.getMember().hasPermission(metadata.getDefaultPerms())) {
            event.replyEmbeds(
                new EmbedBuilder()
                    .setTitle("About Command - %s".formatted(commandName))
                    .setDescription("You don't have access to this command to see its details.")
                    .setColor(EmbedColors.getDefault())
                    .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
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
            new EmbedBuilder()
                .setTitle("About Command - %s".formatted(commandName))
                .setDescription(metadata.getLongDescription())
                .setColor(EmbedColors.getDefault())
                .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
                .addField("Mastermind", metadata.getMastermind().getWho(), true)
                .addField("Module", metadata.getCategory().getName(), true)
                .addField("Implementation Date", "%s (%s)".formatted(timestamps[0], timestamps[1]), false)
                .addField("Last Edited", "%s (%s)".formatted(timestamps[2], timestamps[3]), false)
                .build()
        ).queue();
    }
}
