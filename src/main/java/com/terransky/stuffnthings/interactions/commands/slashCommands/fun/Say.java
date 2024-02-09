package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.Metadata;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Say implements ICommandSlash {
    @Override
    public String getName() {
        return "say";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(this.getName(), "Make the bot say anything!", """
            Make the bot say anything!
                        
            ~~Subject to your server's rules and [Discord Community Guidelines](https://discord.com/guidelines).~~
            """, Mastermind.DEVELOPER,
            CommandCategory.FUN,
            Metadata.parseDate(2022, 8, 24, 11, 10),
            Metadata.parseDate(2024, 2, 9, 16, 11)
        )
            .addOptions(
                new OptionData(OptionType.STRING, "message", "The message you want sent.", true),
                new OptionData(OptionType.CHANNEL, "channel", "the channel where you want the message to be sent.")
                    .setChannelTypes(ChannelType.TEXT, ChannelType.VOICE, ChannelType.GUILD_PUBLIC_THREAD, ChannelType.GUILD_NEWS_THREAD)
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        String message = event.getOption("message", OptionMapping::getAsString);
        MessageChannel channel = (MessageChannel) event.getOption("channel", event.getChannel().asGuildMessageChannel(), OptionMapping::getAsChannel);

        if (!blob.getSelfMember().hasPermission((GuildChannel) channel, Permission.MESSAGE_SEND)) {
            event.reply("I do not have access to that channel.").setEphemeral(true).queue();
            return;
        }

        channel.sendMessageEmbeds(
            blob.getStandardEmbed()
                .setDescription(message)
                .setFooter("Sent by " + blob.getMemberName(), blob.getMemberEffectiveAvatarUrl())
                .build()
        ).queue();
        event.reply("Your message has been sent.").setEphemeral(true).queue();
    }
}
