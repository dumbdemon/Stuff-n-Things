package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.BotColors;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Say extends SlashCommandInteraction {

    public Say() {
        super("say", "Make the bot say anything!", Mastermind.DEVELOPER, CommandCategory.FUN,
            parseDate(2022, 8, 24, 11, 10),
            parseDate(2025, 12, 27, 12, 41)
        );
        addOptions(
            new OptionData(OptionType.STRING, "message", "The message you want sent.", true),
            new OptionData(OptionType.CHANNEL, "channel", "The channel where you want the message to be sent.")
                .setChannelTypes(ChannelType.TEXT, ChannelType.VOICE, ChannelType.GUILD_PUBLIC_THREAD, ChannelType.GUILD_NEWS_THREAD)
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        String message = event.getOption("message", "", OptionMapping::getAsString);
        MessageChannel channel = (MessageChannel) event.getOption("channel", event.getChannel().asGuildMessageChannel(), OptionMapping::getAsChannel);

        if (!blob.getSelfMember().hasPermission((GuildChannel) channel, Permission.MESSAGE_SEND)) {
            event.reply("I do not have access to that channel.").useComponentsV2(false).setEphemeral(true).queue();
            return;
        }
        List<ContainerChildComponent> children = new ArrayList<>();
        children.add(TextDisplay.of(message));
        children.add(Separator.createDivider(Separator.Spacing.SMALL));
        children.add(TextDisplay.ofFormat("From %s", blob.getMember().getUser().getAsMention()));

        channel.sendMessageComponents(
            Container.of(children).withAccentColor(BotColors.DEFAULT.getColor())
        ).queue();
        event.reply("Your message has been sent.").useComponentsV2(false).setEphemeral(true).queue();
    }
}
