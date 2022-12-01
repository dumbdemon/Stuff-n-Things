package com.terransky.stuffnthings.commandSystem.commands.fun;

import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.commandSystem.utilities.Mastermind;
import com.terransky.stuffnthings.commandSystem.utilities.Metadata;
import com.terransky.stuffnthings.commandSystem.utilities.SlashModule;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import com.terransky.stuffnthings.utilities.EmbedColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

public class say implements ISlashCommand {
    @Override
    public String getName() {
        return "say";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        var metadata = new Metadata(this.getName(), "Make the bot say anything!", """
            Make the bot say anything!
                        
            ~~Subject to your server's rules and [Discord Community Guidelines](https://discord.com/guidelines).~~
            """, Mastermind.DEVELOPER,
            SlashModule.FUN,
            format.parse("24-08-2022_11:10"),
            format.parse("1-12-2022_12:37")
        );

        metadata.addOptions(
            new OptionData(OptionType.STRING, "message", "The message you want sent.", true),
            new OptionData(OptionType.CHANNEL, "channel", "the channel where you want the message to be sent.")
                .setChannelTypes(ChannelType.TEXT, ChannelType.VOICE, ChannelType.GUILD_PUBLIC_THREAD, ChannelType.GUILD_NEWS_THREAD)
        );

        return metadata;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) {
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(EmbedColors.getDefault());

        String message = event.getOption("message", OptionMapping::getAsString);
        MessageChannel channel = (MessageChannel) event.getOption("channel", event.getChannel().asGuildMessageChannel(), OptionMapping::getAsChannel);

        eb.setDescription(message)
            .setFooter("Sent by " + event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl());

        channel.sendMessageEmbeds(eb.build()).queue();
        event.reply("Your message has been sent.").setEphemeral(true).queue();
    }
}
