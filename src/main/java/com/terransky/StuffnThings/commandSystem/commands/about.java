package com.terransky.StuffnThings.commandSystem.commands;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.CommandManager;
import com.terransky.StuffnThings.commandSystem.Metadata.Mastermind;
import com.terransky.StuffnThings.commandSystem.Metadata.Metadata;
import com.terransky.StuffnThings.commandSystem.commands.mtg.calculateRats;
import com.terransky.StuffnThings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.util.Optional;

public class about implements ISlashCommand {
    @Override
    public String getName() {
        return "about";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(this.getName(), """
            The about command. What else did you expect?
            """, Mastermind.DEVELOPER);
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
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        String command = event.getOption("command", "none", OptionMapping::getAsString);

        if (!command.equals("none")) {
            Optional<Metadata> extraDetails = new CommandManager().getMetadata(command);
            Metadata details = extraDetails.orElse(this.getMetadata());

            if (details.minPerms().length != 0) {
                if (event.getMember() != null && !event.getMember().hasPermission(details.minPerms())) {
                    event.replyEmbeds(
                        new EmbedBuilder()
                            .setTitle("About Command")
                            .setDescription("You don't have access to this command to see its details.")
                            .setColor(Commons.DEFAULT_EMBED_COLOR)
                            .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                            .build()
                    ).queue();
                    return;
                }
            }

            event.getHook().sendMessageEmbeds(
                new EmbedBuilder()
                    .setTitle("About Command - %s".formatted(WordUtils.capitalize(details.commandName().replace("-", "\s"))))
                    .setDescription(details.longDescription())
                    .setColor(Commons.DEFAULT_EMBED_COLOR)
                    .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                    .addField("Mastermind", details.mastermind().getWho(), true)
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
                .setColor(Commons.DEFAULT_EMBED_COLOR)
                .setDescription("""
                    > *Who am I?*
                    I am %s
                    > *What am I?*
                    An entertainment bot.
                    > [*I think I need help...*](%s)
                    """.formatted(event.getJDA().getSelfUser().getAsMention(), Commons.CONFIG.get("SUPPORT_GUILD_INVITE")))
                .setTitle(event.getJDA().getSelfUser().getName(), Commons.CONFIG.get("REPO_LINK"))
                .setThumbnail(Commons.CONFIG.get("BOT_LOGO"))
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
