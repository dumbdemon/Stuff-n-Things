package com.terransky.stuffnthings.commandSystem.commands;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.commandSystem.CommandManager;
import com.terransky.stuffnthings.commandSystem.commands.mtg.calculateRats;
import com.terransky.stuffnthings.commandSystem.metadata.Mastermind;
import com.terransky.stuffnthings.commandSystem.metadata.Metadata;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.ParseException;
import java.time.Duration;
import java.util.Optional;

public class about implements ISlashCommand {
    @Override
    public String getName() {
        return "about";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat formatter = Commons.getFastDateFormat();
        return new Metadata(this.getName(), """
            The about command. What else did you expect?
            """, Mastermind.DEVELOPER,
            formatter.parse("24-08-2022_11:10"),
            formatter.parse("13-11-2022_10:05")
        );
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(this.getName(), "What am I? Who am I?")
            .addOptions(
                new OptionData(OptionType.STRING, "command", "Get more info on a Command.")
                    .addChoices(new CommandManager().getCommandsAsChoices())
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) throws ParseException {
        event.deferReply().queue();
        String command = event.getOption("command", "none", OptionMapping::getAsString);

        if (!command.equals("none")) {
            Optional<Metadata> ifMetadata = new CommandManager().getMetadata(command);
            Metadata metadata = ifMetadata.orElse(this.getMetadata());

            if (metadata.minPerms().length != 0) {
                if (event.getMember() != null && !event.getMember().hasPermission(metadata.minPerms())) {
                    event.replyEmbeds(
                        new EmbedBuilder()
                            .setTitle("About Command")
                            .setDescription("You don't have access to this command to see its details.")
                            .setColor(Commons.getDefaultEmbedColor())
                            .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                            .build()
                    ).queue();
                    return;
                }
            }

            long implementedDate = metadata.getImplementedAsEpochSecond(),
                lastEditedDate = metadata.getLastEditedAsEpochSecond();

            event.getHook().sendMessageEmbeds(
                new EmbedBuilder()
                    .setTitle("About Command - %s".formatted(WordUtils.capitalize(metadata.commandName().replace("-", "\s"))))
                    .setDescription(metadata.longDescription())
                    .setColor(Commons.getDefaultEmbedColor())
                    .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                    .addField("Mastermind", metadata.mastermind().getWho(), true)
                    .addField("Implementation Date", "<t:%s:f> (<t:%s:R>)".formatted(implementedDate, implementedDate), false)
                    .addField("Last Edited", "<t:%s:f> (<t:%s:R>)".formatted(lastEditedDate, lastEditedDate), false)
                    .build()
            ).queue();
            return;
        }

        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();

        int commandCnt = new CommandManager().getCommandData().size();
        if (event.getGuild() != null)
            commandCnt += new CommandManager().getCommandData(event.getGuild().getIdLong()).size();

        Duration duration = Duration.ofMillis(rb.getUptime());
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

        //TODO: Replace with database calls when fully implemented.
        long guildCount = event.getJDA().getGuildCache().stream().distinct().count();
        long userCount = event.getJDA().getUserCache().stream().distinct().filter(user -> !user.isBot()).count();

        event.getHook().sendMessageEmbeds(
            new EmbedBuilder()
                .setColor(Commons.getDefaultEmbedColor())
                .setDescription("""
                    > *Who am I?*
                    I am %s
                    > *What am I?*
                    An entertainment bot.
                    > [*I think I need help...*](%s)
                    """.formatted(event.getJDA().getSelfUser().getAsMention(), Commons.getConfig().get("SUPPORT_GUILD_INVITE")))
                .setTitle(event.getJDA().getSelfUser().getName(), Commons.getConfig().get("REPO_LINK"))
                .setThumbnail(Commons.getConfig().get("BOT_LOGO"))
                .addField("Servers", "%d servers".formatted(guildCount), true)
                .addField("Users", "%s users".formatted(calculateRats.largeNumberFormat(userCount)).replace(".0\s", "\s"), true)
                .addField("Your Shard", "[%s/%s]".formatted(event.getJDA().getShardInfo().getShardId(), event.getJDA().getShardInfo().getShardTotal()), true)
                .addField("Start Time", "<t:%s:F>".formatted((int) Math.floor(rb.getStartTime() / 1_000f)), false)
                .addField("Uptime", uptime.toString(), false)
                .addField("Total Commands", "%s on %s".formatted(commandCnt, event.getGuild().getName()), false)
                .build()
        ).queue();
    }
}
