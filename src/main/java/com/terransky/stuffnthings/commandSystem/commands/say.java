package com.terransky.stuffnthings.commandSystem.commands;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.commandSystem.metadata.Mastermind;
import com.terransky.stuffnthings.commandSystem.metadata.Metadata;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class say implements ISlashCommand {
    @Override
    public String getName() {
        return "say";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH:mm");
        return new Metadata(this.getName(), """
            Make the bot say anything!
                        
            ~~Subject to your server's rules and Discord Community Guidelines.~~
            """, Mastermind.DEVELOPER,
            formatter.parse("24-08-2022_11:10"),
            formatter.parse("12-11-2022_12:01"));
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(this.getName(), "Make the bot say anything!")
            .addOptions(
                new OptionData(OptionType.STRING, "message", "The message you want sent.", true),
                new OptionData(OptionType.CHANNEL, "channel", "the channel where you want the message to be sent.")
                    .setChannelTypes(ChannelType.TEXT, ChannelType.VOICE, ChannelType.GUILD_PUBLIC_THREAD, ChannelType.GUILD_NEWS_THREAD)
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(Commons.DEFAULT_EMBED_COLOR);

        String message = event.getOption("message", OptionMapping::getAsString);
        MessageChannel channel = (MessageChannel) event.getOption("channel", event.getChannel().asGuildMessageChannel(), OptionMapping::getAsChannel);

        eb.setDescription(message)
            .setFooter("Sent by " + event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl());

        channel.sendMessageEmbeds(eb.build()).queue();
        event.reply("Your message has been sent.").setEphemeral(true).queue();
    }
}