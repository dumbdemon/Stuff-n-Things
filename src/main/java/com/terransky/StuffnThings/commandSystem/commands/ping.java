package com.terransky.StuffnThings.commandSystem.commands;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
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
    public CommandData commandData() {
        return Commands.slash(this.getName(), "Get the ping of the bot.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        JDA jda = event.getJDA();
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(Commons.defaultEmbedColor)
            .setTitle("Ping Info")
            .setFooter("Requested by " + event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl());

        jda.getRestPing().queue(ping -> event.replyEmbeds(
            eb.addField("Rest Ping", ping + "ms", true)
                .addField("Web Socket Ping", jda.getGatewayPing() + "ms", true)
                .build()
        ).queue());
    }
}
