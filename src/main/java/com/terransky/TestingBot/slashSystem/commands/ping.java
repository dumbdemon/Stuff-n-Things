package com.terransky.TestingBot.slashSystem.commands;

import com.terransky.TestingBot.Commons;
import com.terransky.TestingBot.slashSystem.ISlash;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class ping implements ISlash {
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(this.getName(), "Get the ping of the bot.");
    }

    @Override
    public void slashExecute(@NotNull SlashCommandInteractionEvent event) {
        JDA jda = event.getJDA();
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(new Commons().defaultEmbedColor)
                .setTitle("Ping Info")
                .setFooter("Requested by " + event.getUser().getAsTag());

        jda.getRestPing().queue(ping -> event.replyEmbeds(eb
                .addField(new MessageEmbed.Field("Reset Ping", ping + "ms", true))
                .addField(new MessageEmbed.Field("Web Socket Ping", jda.getGatewayPing() + "ms", true))
                .build()
        ).queue());
    }
}
