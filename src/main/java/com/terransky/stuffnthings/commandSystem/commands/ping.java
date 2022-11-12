package com.terransky.stuffnthings.commandSystem.commands;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.commandSystem.metadata.Mastermind;
import com.terransky.stuffnthings.commandSystem.metadata.Metadata;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

public class ping implements ISlashCommand {
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat formatter = FastDateFormat.getInstance("dd-MM-yyyy_HH:mm");
        return new Metadata(this.getName(), """
            Pong! Get the ping of the bot.
            """, Mastermind.DEFAULT,
            formatter.parse("24-08-2022_11:10"),
            formatter.parse("12-11-2022_12:01"));
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(this.getName(), "Get the ping of the bot.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        JDA jda = event.getJDA();
        jda.getRestPing().queue(ping -> event.replyEmbeds(
            new EmbedBuilder()
                .setColor(Commons.DEFAULT_EMBED_COLOR)
                .setTitle("Ping Info")
                .setFooter("Requested by " + event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
                .addField("Rest Ping", ping + "ms", true)
                .addField("Web Socket Ping", jda.getGatewayPing() + "ms", true)
                .build()
        ).queue());
    }
}
