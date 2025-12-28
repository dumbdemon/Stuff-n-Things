package com.terransky.stuffnthings.interactions.commands.slashCommands.admin;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.jda.ChannelPermsController;
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
import java.util.concurrent.ExecutionException;

public class ChannelUnLock extends SlashCommandInteraction {

    public ChannelUnLock() {
        super("channel-lock", "Lock or unlock a channel for everyone or from a specific role to see.",
            Mastermind.DEVELOPER, CommandCategory.ADMIN,
            parseDate(2022, 11, 23, 18, 34),
            parseDate(2025, 12, 27, 1, 31)
        );
        setDefaultMemberPermissions(Permission.MANAGE_CHANNEL);
        addSubcommands(
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
            new SubcommandData("reset", "Reset role's View Channel to inherent the category defaults.")
                .addOptions(
                    new OptionData(OptionType.ROLE, "role", "The role to reset", true),
                    new OptionData(OptionType.CHANNEL, "target-channel", "Remove from a different channel.")
                )
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        Role targetRole = event.getOption("role", blob.getGuild().getPublicRole(), OptionMapping::getAsRole);
        GuildChannel targetChannel = event.getOption("target-channel", event.getGuildChannel(), OptionMapping::getAsChannel);
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");
        String title = "Channel Control - " + WordUtils.capitalize(subcommand);

        try {
            ChannelPermsController permsController = new ChannelPermsController(targetChannel);

            switch (subcommand) {
                case "lock" -> {
                    if (!permsController.denyChannelPerms(targetRole, Permission.VIEW_CHANNEL)) {
                        event.replyComponents(
                            StandardResponse.getResponseContainer(title, String.format("%s is already set to not be viewed by %s.",
                                targetChannel.getAsMention(), targetRole.getAsMention()), BotColors.SUB_DEFAULT
                            )
                        ).queue();
                        return;
                    }
                }
                case "unlock" -> {
                    if (!permsController.grantChannelPerms(targetRole, Permission.VIEW_CHANNEL)) {
                        event.replyComponents(
                            StandardResponse.getResponseContainer(title,
                                String.format("%s is already set to be viewable by %s.", targetChannel.getAsMention(), targetRole.getAsMention()),
                                BotColors.SUB_DEFAULT
                            )
                        ).queue();
                        return;
                    }
                }
                case "reset" -> {
                    if (!permsController.resetChannelPerms(targetRole, Permission.VIEW_CHANNEL)) {
                        event.replyComponents(
                            StandardResponse.getResponseContainer(title, String.format("%s is not in the permissions list for %s.",
                                    targetRole.getAsMention(), targetChannel.getAsMention()),
                                BotColors.SUB_DEFAULT
                            )
                        ).queue();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(ChannelUnLock.class).debug("Unable to perform channel action", e);
            event.replyComponents(
                StandardResponse.getResponseContainer(title,
                    String.format(
                        "Either I do not have access to modify the permissions for %s, or something else has happened and it should be reported." +
                            " Head [here](%s) to report.",
                        targetChannel.getAsMention(),
                        StuffNThings.getConfig().getCore().getReportingUrl()
                    ),
                    BotColors.ERROR
                )
            ).queue();
            return;
        }

        event.replyComponents(
            StandardResponse.getResponseContainer(title, String.format("Permissions for %s on %s was set successfully.", targetRole.getAsMention(),
                targetChannel.getAsMention())
            )
        ).queue();
    }
}
