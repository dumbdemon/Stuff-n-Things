package com.terransky.StuffnThings.commandSystem.commands;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
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

public class say implements ISlashCommand {
    @Override
    public String getName() {
        return "say";
    }

    @Override
    public CommandData commandData() {
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
            .setColor(Commons.defaultEmbedColor);

        String message = event.getOption("message", OptionMapping::getAsString);
        MessageChannel channel = (MessageChannel) event.getOption("channel", event.getChannel().asGuildMessageChannel(), OptionMapping::getAsChannel);

        eb.setDescription(message)
            .setFooter("Sent by " + event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl());

        channel.sendMessageEmbeds(eb.build()).queue();
        event.reply("Your message has been sent.").setEphemeral(true).queue();
    }
}
