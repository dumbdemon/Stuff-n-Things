package com.terransky.stuffnthings.interactions.commands.slashCommands.admin;

import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.LogList;
import com.terransky.stuffnthings.utilities.jda.ChannelPermsController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Arrays;

public class channelUnLock implements ICommandSlash {

    @Override
    public String getName() {
        return "channel-lock";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        String description = "Lock or unlock a channel for everyone or from a specific role to see.";
        return new Metadata(this.getName(), description, description, Mastermind.DEVELOPER, SlashModule.ADMIN,
            format.parse("23-11-2022_18:34"),
            format.parse("29-12-2022_10:14")
        )
            .addDefaultPerms(Permission.MANAGE_CHANNEL)
            .addSubcommands(
                new SubcommandData("lock", "Lock a channel.")
                    .addOptions(
                        new OptionData(OptionType.ROLE, "role", "Prevent a role from seeing the channel"),
                        new OptionData(OptionType.CHANNEL, "target-channel", "Lock a different channel.")
                    ),
                new SubcommandData("unlock", "Unlock a channel")
                    .addOptions(
                        new OptionData(OptionType.ROLE, "role", "Allow a role to see the channel"),
                        new OptionData(OptionType.CHANNEL, "target-channel", "Unlock a different channel.")
                    ),
                new SubcommandData("reset", "Reset role's View Channel to inherent in a channel.")
                    .addOptions(
                        new OptionData(OptionType.ROLE, "role", "The role to reset", true),
                        new OptionData(OptionType.CHANNEL, "target-channel", "Remove from a different channel.")
                    )
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        Role targetRole = event.getOption("role", blob.getGuild().getPublicRole(), OptionMapping::getAsRole);
        GuildChannel targetChannel = event.getOption("target-channel", event.getGuildChannel(), OptionMapping::getAsChannel);
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");
        EmbedBuilder response = new EmbedBuilder()
            .setTitle("Channel Control - " + WordUtils.capitalize(subcommand))
            .setColor(EmbedColors.getDefault())
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl());

        try {
            ChannelPermsController permsController = new ChannelPermsController(targetChannel);

            switch (subcommand) {
                case "lock" -> {
                    if (!permsController.denyChannelPerms(targetRole, Permission.VIEW_CHANNEL)) {
                        event.replyEmbeds(
                            response
                                .setDescription("%s is already set to not be viewed by %s.".formatted(targetChannel.getAsMention(), targetRole.getAsMention()))
                                .setColor(EmbedColors.getError())
                                .build()
                        ).queue();
                        return;
                    }
                }
                case "unlock" -> {
                    if (!permsController.grantChannelPerms(targetRole, Permission.VIEW_CHANNEL)) {
                        event.replyEmbeds(
                            response
                                .setDescription("%s is already set to be viewable by %s.".formatted(targetChannel.getAsMention(), targetRole.getAsMention()))
                                .setColor(EmbedColors.getError())
                                .build()
                        ).queue();
                        return;
                    }
                }
                case "reset" -> {
                    if (!permsController.resetChannelPerms(targetRole, Permission.VIEW_CHANNEL)) {
                        event.replyEmbeds(
                            response
                                .setDescription("%s is not in the permissions list for %s.".formatted(targetRole.getAsMention(), targetChannel.getAsMention()))
                                .setColor(EmbedColors.getError())
                                .build()
                        ).queue();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(channelUnLock.class).debug("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
            LogList.error(Arrays.asList(e.getStackTrace()), channelUnLock.class);
            event.replyEmbeds(
                response
                    .setDescription(("Either I do not have access to modify the permissions for %s, or something else has happened and it should be reported." +
                        " Head [here](%s) to report.")
                        .formatted(targetChannel.getAsMention(), Config.getErrorReportingURL()))
                    .setColor(EmbedColors.getError())
                    .build()
            ).queue();
            return;
        }

        event.replyEmbeds(
            response
                .setDescription("Permissions for %s on %s was set successfully.".formatted(targetRole.getAsMention(),
                    targetChannel.getAsMention()))
                .build()
        ).queue();
    }
}
