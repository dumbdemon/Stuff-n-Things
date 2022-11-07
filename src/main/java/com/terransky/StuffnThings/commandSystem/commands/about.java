package com.terransky.StuffnThings.commandSystem.commands;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.CommandManager;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;

public class about implements ISlashCommand {
    @Override
    public String getName() {
        return "about";
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(this.getName(), "What am I? Who am I?");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();

        int commandCnt = new CommandManager().getCommandData(null).size();
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

        //Replace with database calls when fully implemented.
        long guildCount = event.getJDA().getGuildCache().stream().distinct().count();
        long userCount = event.getJDA().getUserCache().stream().distinct().filter(user -> !user.isBot()).count();

        event.getHook().sendMessageEmbeds(
            new EmbedBuilder()
                .setColor(Commons.defaultEmbedColor)
                .setDescription("""
                    > *Who am I?*
                    I am %s
                    > *What am I?*
                    An entertainment bot.
                    > [*I think I need help...*](%s)
                    """.formatted(event.getJDA().getSelfUser().getAsMention(), Commons.config.get("SUPPORT_GUILD_INVITE")))
                .setTitle(event.getJDA().getSelfUser().getName(), Commons.config.get("REPO_LINK"))
                .setThumbnail(Commons.config.get("BOT_LOGO"))
                .addField("Servers", "%d servers".formatted(guildCount), true)
                .addField("Users", "%s users".formatted(userCount), true)
                .addField("Your Shard", "[%s/%s]".formatted(event.getJDA().getShardInfo().getShardId(), event.getJDA().getShardInfo().getShardTotal()), true)
                .addField("Start Time", "<t:%s:F>".formatted((int) Math.floor(rb.getStartTime() / 1_000f)), false)
                .addField("Uptime", uptime.toString(), false)
                .addField("Total Commands", "%s on %s".formatted(commandCnt, event.getGuild().getName()), false)
                .build()
        ).queue();
    }
}
