package com.terransky.stuffnthings.commandSystem.commands.general;

import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.commandSystem.utilities.Mastermind;
import com.terransky.stuffnthings.commandSystem.utilities.Metadata;
import com.terransky.stuffnthings.commandSystem.utilities.SlashModule;
import com.terransky.stuffnthings.interfaces.ICommandSlash;
import com.terransky.stuffnthings.utilities.EmbedColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

public class ping implements ICommandSlash {
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
            SlashModule.GENERAL,
            format.parse("24-08-2022_11:10"),
            format.parse("1-12-2022_12:37")
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) {
        JDA jda = event.getJDA();
        jda.getRestPing().queue(ping -> event.replyEmbeds(
            new EmbedBuilder()
                .setColor(EmbedColors.getDefault())
                .setTitle("Ping Info")
                .setFooter("Requested by " + event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
                .addField("Rest Ping", ping + "ms", true)
                .addField("Web Socket Ping", jda.getGatewayPing() + "ms", true)
                .build()
        ).queue());
    }
}
