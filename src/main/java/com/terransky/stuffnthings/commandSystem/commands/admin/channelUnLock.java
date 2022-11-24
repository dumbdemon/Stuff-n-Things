package com.terransky.stuffnthings.commandSystem.commands.admin;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.commandSystem.utilities.Mastermind;
import com.terransky.stuffnthings.commandSystem.utilities.Metadata;
import com.terransky.stuffnthings.commandSystem.utilities.SlashModule;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class channelUnLock implements ISlashCommand {
    @Nullable
    private static <T extends IPermissionHolder> PermissionOverride getPermissionOverride(T iPermissionHolder,
                                                                                          @NotNull List<PermissionOverride> overrides) {
        for (PermissionOverride override : overrides) {
            if (iPermissionHolder instanceof Role && iPermissionHolder.equals(override.getRole()))
                return override;
            else if (iPermissionHolder.equals(override.getMember()))
                return override;
        }
        return null;
    }

    @Override
    public String getName() {
        return "channel-lock";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Commons.getFastDateFormat();
        String description = "Lock or unlock a channel for everyone or from a specific role to see.";
        var metadata = new Metadata(this.getName(), description, description, Mastermind.DEVELOPER, SlashModule.ADMIN,
            format.parse("23-11-2022_18:34"),
            format.parse("24-11-2022_13:37")
        );

        metadata.addMinPerms(Permission.MANAGE_CHANNEL);
        metadata.addSubcommands(
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

        return metadata;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        Role targetRole = event.getOption("role", blob.getGuild().getPublicRole(), OptionMapping::getAsRole);
        GuildChannel targetChannel = event.getOption("target-channel", event.getGuildChannel(), OptionMapping::getAsChannel);
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");
        EmbedBuilder response = new EmbedBuilder()
            .setTitle("Channel Control - " + WordUtils.capitalize(subcommand))
            .setColor(Commons.getDefaultEmbedColor())
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl());

        try {
            IPermissionContainer container = targetChannel.getPermissionContainer();
            PermissionOverride permOverride = getPermissionOverride(targetRole, container.getRolePermissionOverrides());

            switch (subcommand) {
                case "lock" ->
                    lockChannel(event, response, permOverride, targetRole, targetChannel, Permission.VIEW_CHANNEL);
                case "unlock" ->
                    unlockChannel(event, response, permOverride, targetRole, targetChannel, Permission.VIEW_CHANNEL);
                case "reset" ->
                    resetChannelForRole(event, response, permOverride, targetRole, targetChannel, Permission.VIEW_CHANNEL);
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(channelUnLock.class).debug("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
            Commons.loggerPrinterOfError(Arrays.asList(e.getStackTrace()), channelUnLock.class);
            event.replyEmbeds(
                response
                    .setDescription(("Either I do not have access to modify the permissions for %s, or something else has happened and it should be reported." +
                        " Head [here](%s) to report.")
                        .formatted(targetChannel.getAsMention(), Commons.getConfig().get("BOT_ERROR_REPORT")))
                    .build()
            ).queue();
            return;
        }

        if (!event.isAcknowledged())
            event.replyEmbeds(
                response
                    .setDescription("Permissions for %s on %s was set successfully.".formatted(targetRole.getAsMention(),
                        targetChannel.getAsMention()))
                    .build()
            ).queue();
    }

    private <T extends IPermissionHolder> void resetChannelForRole(SlashCommandInteractionEvent event, EmbedBuilder response,
                                                                   PermissionOverride permOverride, T iPermissionHolder,
                                                                   GuildChannel targetChannel, Permission... permissions) {
        if (permOverride == null) {
            String mention = iPermissionHolder instanceof Role ? ((Role) iPermissionHolder).getAsMention() :
                ((Member) iPermissionHolder).getAsMention();
            event.replyEmbeds(
                response
                    .setDescription("%s is not in the permissions list for %s".formatted(mention, targetChannel.getAsMention()))
                    .build()
            ).queue();
            return;
        }

        PermissionOverrideAction permAction = permOverride.getManager();
        permAction.clear(permissions).queue();
    }

    private <T extends IPermissionHolder> void unlockChannel(SlashCommandInteractionEvent event, EmbedBuilder response,
                                                             PermissionOverride permOverride, @NotNull T iPermissionHolder,
                                                             GuildChannel channel, Permission... permissions) {
        if (iPermissionHolder.hasPermission(channel, permissions)) {
            String mention = iPermissionHolder instanceof Role ? ((Role) iPermissionHolder).getAsMention() :
                ((Member) iPermissionHolder).getAsMention();
            event.replyEmbeds(
                response
                    .setDescription("%s is already set to be viewable by %s.".formatted(channel.getAsMention(), mention))
                    .build()
            ).queue();
            return;
        }
        if (permOverride == null) {
            permOverride = channel.getPermissionContainer().upsertPermissionOverride(iPermissionHolder).complete();
        }

        PermissionOverrideAction permAction = permOverride.getManager();
        permAction.grant(permissions).queue();
    }

    private <T extends IPermissionHolder> void lockChannel(SlashCommandInteractionEvent event, EmbedBuilder response,
                                                           PermissionOverride permOverride, @NotNull T iPermissionHolder,
                                                           GuildChannel channel, Permission... permissions) {
        if (!iPermissionHolder.hasPermission(channel, permissions)) {
            String mention = iPermissionHolder instanceof Role ? (((Role) iPermissionHolder)).getAsMention() :
                ((Member) iPermissionHolder).getAsMention();
            event.replyEmbeds(
                response
                    .setDescription("%s is already set to not be viewed by %s.".formatted(channel.getAsMention(), mention))
                    .build()
            ).queue();
            return;
        }
        if (permOverride == null) {
            permOverride = channel.getPermissionContainer().upsertPermissionOverride(iPermissionHolder).complete();
        }
        PermissionOverrideAction permAction = permOverride.getManager();
        permAction.deny(permissions).queue();
    }
}
