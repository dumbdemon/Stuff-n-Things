package com.terransky.stuffnthings.interactions.commands.slashCommands.general;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;

public class Ping implements ICommandSlash {
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        return new Metadata(this.getName(), "Get the ping of the bot.", """
            Pong! Get the ping of the bot.
            """, Mastermind.DEFAULT,
            CommandCategory.GENERAL,
            format.parse("24-08-2022_11:10"),
            format.parse("30-12-2022_16:44")
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        JDA jda = event.getJDA();
        jda.getRestPing().queue(ping -> event.replyEmbeds(
            new EmbedBuilder()
                .setColor(EmbedColors.getDefault())
                .setTitle(String.format("Ping Info [Shard %s]", jda.getShardInfo().getShardId()))
                .setFooter("Requested by " + blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
                .addField("Rest Ping", ping + "ms", true)
                .addField("Web Socket Ping", jda.getGatewayPing() + "ms", true)
                .build()
        ).queue());
    }
}
