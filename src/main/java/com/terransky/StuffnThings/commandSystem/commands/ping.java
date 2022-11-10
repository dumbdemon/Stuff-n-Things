package com.terransky.StuffnThings.commandSystem.commands;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.ExtraDetails.ExtraDetails;
import com.terransky.StuffnThings.commandSystem.ExtraDetails.Mastermind;
import com.terransky.StuffnThings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class ping implements ISlashCommand {
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public ExtraDetails getExtraDetails() {
        return new ExtraDetails(this.getName(), """
            Pong! Get the ping of the bot.
            """, Mastermind.DEFAULT);
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
