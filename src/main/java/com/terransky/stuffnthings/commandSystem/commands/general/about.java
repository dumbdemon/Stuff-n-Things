package com.terransky.stuffnthings.commandSystem.commands.general;

import com.terransky.stuffnthings.commandSystem.CommandManager;
import com.terransky.stuffnthings.commandSystem.commands.mtg.calculateRats;
import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.commandSystem.utilities.Mastermind;
import com.terransky.stuffnthings.commandSystem.utilities.Metadata;
import com.terransky.stuffnthings.commandSystem.utilities.SlashModule;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import com.terransky.stuffnthings.utilities.Config;
import com.terransky.stuffnthings.utilities.EmbedColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
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

    @NotNull
    private static StringBuilder getStringBuilder(@NotNull RuntimeMXBean rb) {
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
        return uptime;
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        var metadata = new Metadata(this.getName(), "What am I? Who am I?", """
            The about command. What else did you expect?
            """, Mastermind.DEVELOPER,
            SlashModule.GENERAL,
            format.parse("24-08-2022_11:10"),
            format.parse("7-12-2022_10:25")
        );

        metadata.addOptions(
            new OptionData(OptionType.STRING, "command", "Get more info on a Command.")
                .addChoices(new CommandManager().getCommandsAsChoices())
        );

        return metadata;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        event.deferReply().queue();
        Optional<String> ifCommand = Optional.ofNullable(event.getOption("command", OptionMapping::getAsString));

        if (ifCommand.isPresent()) {
            getCommandInfo(event, blob, ifCommand.get());
            return;
        }

        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        StringBuilder uptime = getStringBuilder(rb);

        int commandCnt = new CommandManager().getCommandData().size();
        int guildCommandCnt = new CommandManager().getCommandData(blob.getGuildIdLong()).size();
        commandCnt += guildCommandCnt;

        //todo: Replace with database calls when fully implemented.
        long guildCount = event.getJDA().getGuildCache().stream().distinct().count();
        long userCount = event.getJDA().getUserCache().stream().distinct().filter(user -> !user.isBot()).count();

        event.getHook().sendMessageEmbeds(
            new EmbedBuilder()
                .setColor(EmbedColors.getDefault())
                .setDescription("""
                    > *Who am I?*
                    I am %s
                    > *What am I?*
                    An entertainment bot.
                    > [*I think I need help...*](%s)
                    """.formatted(event.getJDA().getSelfUser().getAsMention(), Config.getSupportGuildInvite()))
                .setTitle(event.getJDA().getSelfUser().getName(), Config.getRepositoryURL())
                .setThumbnail(Config.getBotLogoURL())
                .addField("Servers", "%d servers".formatted(guildCount), true)
                .addField("Users", "%s users".formatted(calculateRats.largeNumberFormat(userCount)).replace(".0 ", " "), true)
                .addField("Your Shard", "[%s/%s]".formatted(event.getJDA().getShardInfo().getShardId(), event.getJDA().getShardInfo().getShardTotal()), true)
                .addField("Start Time", "<t:%s:F>".formatted((int) (rb.getStartTime() / 1_000f)), false)
                .addField("Uptime", uptime.toString(), false)
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
        Optional<Metadata> ifMetadata = new CommandManager().getMetadata(command);
        Metadata metadata = ifMetadata.orElse(this.getMetadata());

        if (!metadata.getDefaultPerms().isEmpty() && event.getMember() != null && !event.getMember().hasPermission(metadata.getDefaultPerms())) {
            event.replyEmbeds(
                new EmbedBuilder()
                    .setTitle("About Command")
                    .setDescription("You don't have access to this command to see its details.")
                    .setColor(EmbedColors.getDefault())
                    .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
                    .build()
            ).queue();
            return;
        }

        long implementedDate = metadata.getImplementedAsEpochSecond(),
            lastEditedDate = metadata.getLastEditedAsEpochSecond();

        event.getHook().sendMessageEmbeds(
            new EmbedBuilder()
                .setTitle("About Command - %s".formatted(WordUtils.capitalize(metadata.getCommandName().replace("-", " "))))
                .setDescription(metadata.getLongDescription())
                .setColor(EmbedColors.getDefault())
                .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
                .addField("Mastermind", metadata.getMastermind().getWho(), true)
                .addField("Module", metadata.getModule().getName(), true)
                .addField("Implementation Date", "<t:%s:f> (<t:%s:R>)".formatted(implementedDate, implementedDate), false)
                .addField("Last Edited", "<t:%s:f> (<t:%s:R>)".formatted(lastEditedDate, lastEditedDate), false)
                .build()
        ).queue();
    }
}
