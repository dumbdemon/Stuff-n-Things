package com.terransky.stuffnthings.interactions.commands.slashCommands.admin;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
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
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ChannelUnLock implements ICommandSlash {

    @Override
    public String getName() {
        return "channel-lock";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(this.getName(), "Lock or unlock a channel for everyone or from a specific role to see.",
            Mastermind.DEVELOPER, CommandCategory.ADMIN,
            Metadata.parseDate(2022, 11, 23, 18, 34),
            Metadata.parseDate(2024, 8, 20, 12, 3)
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
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        Role targetRole = event.getOption("role", blob.getGuild().getPublicRole(), OptionMapping::getAsRole);
        GuildChannel targetChannel = event.getOption("target-channel", event.getGuildChannel(), OptionMapping::getAsChannel);
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");
        EmbedBuilder response = blob.getStandardEmbed("Channel Control - " + WordUtils.capitalize(subcommand));

        try {
            ChannelPermsController permsController = new ChannelPermsController(targetChannel);

            switch (subcommand) {
                case "lock" -> {
                    if (!permsController.denyChannelPerms(targetRole, Permission.VIEW_CHANNEL)) {
                        event.replyEmbeds(
                            response
                                .setDescription("%s is already set to not be viewed by %s.".formatted(targetChannel.getAsMention(), targetRole.getAsMention()))
                                .setColor(EmbedColor.ERROR.getColor())
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
                                .setColor(EmbedColor.ERROR.getColor())
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
                                .setColor(EmbedColor.ERROR.getColor())
                                .build()
                        ).queue();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(ChannelUnLock.class).debug("Unable to perform channel action", e);
            event.replyEmbeds(
                response
                    .setDescription(("Either I do not have access to modify the permissions for %s, or something else has happened and it should be reported." +
                        " Head [here](%s) to report.")
                        .formatted(targetChannel.getAsMention(), StuffNThings.getConfig().getCore().getReportingUrl()))
                    .setColor(EmbedColor.ERROR.getColor())
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
