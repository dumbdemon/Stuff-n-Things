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
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.List;

public class channelControl implements ISlashCommand {
    @Override
    public String getName() {
        return "channel-control";
    }

    @Override
    public boolean isWorking() {
        return Commons.isTestingMode();
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Commons.getFastDateFormat();
        String description = "Lock or unlock a channel for everyone or from a specific role to see.";
        var metadata = new Metadata(this.getName(), description, description, Mastermind.DEVELOPER, SlashModule.ADMIN,
            format.parse("23-11-2022_18:34"),
            format.parse("23-11-2022_19:30")
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
                )
        );

        return metadata;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        Role role = event.getOption("role", blob.getGuild().getPublicRole(), OptionMapping::getAsRole);
        GuildChannel targetChannel = event.getOption("target-channel", event.getGuildChannel(), OptionMapping::getAsChannel);
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");
        EmbedBuilder response = new EmbedBuilder()
            .setTitle("Channel Control")
            .setColor(Commons.getDefaultEmbedColor())
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl());

        PermissionOverride permOverride = null;

        List<PermissionOverride> roleOverrides = targetChannel.getPermissionContainer().getRolePermissionOverrides();

        for (PermissionOverride override : roleOverrides) {
            if (role.equals(override.getRole())) {
                permOverride = override;
                break;
            }
        }

        //TODO: If null, add role to container and set permOverride to role
        if (permOverride == null) {
            event.replyEmbeds(
                response.setDescription("Due to my own ignorance, I do not know how to add a new role into channel permissions." +
                        " Please add the role to the list before executing this command. You do not need to modify it.")
                    .build()
            ).queue();
            return;
        }

        try {
            List<Permission> perms = permOverride.getAllowed().stream().toList();
            PermissionOverrideAction permAction = permOverride.getManager();

            switch (subcommand) {
                case "lock" -> lockChannel(event, response, perms, permAction);
                case "unlock" -> unlockChannel(event, response, perms, permAction);
            }
        } catch (Exception e) {
            event.replyEmbeds(
                response
                    .setDescription("Either I do not have access to modify the permissions for %s, or something else has happened and it should be reported [here](%s)."
                        .formatted(targetChannel.getAsMention(), Commons.getConfig().get("BOT_ERROR_REPORT")))
                    .build()
            ).queue();
            return;
        }

        event.replyEmbeds(
            response
                .setDescription("Permissions for %s on %s was set successfully.".formatted(role.getAsMention(), targetChannel.getAsMention()))
                .build()
        ).queue();
    }

    private void unlockChannel(SlashCommandInteractionEvent event, EmbedBuilder response, @NotNull List<Permission> perms,
                               PermissionOverrideAction permAction) {
        if (perms.contains(Permission.VIEW_CHANNEL)) {
            event.replyEmbeds(
                response
                    .setDescription("%s is already set to be viewed by %s.".formatted(permAction.getChannel(), permAction.getRole()))
                    .build()
            ).queue();
            return;
        }

        perms.add(Permission.VIEW_CHANNEL);
        permAction.setAllowed(perms).queue();
    }

    private void lockChannel(SlashCommandInteractionEvent event, EmbedBuilder response, @NotNull List<Permission> perms,
                             PermissionOverrideAction permAction) {
        if (!perms.contains(Permission.VIEW_CHANNEL)) {
            event.replyEmbeds(
                response
                    .setDescription("%s is already set to not be viewed by %s.".formatted(permAction.getChannel(), permAction.getRole()))
                    .build()
            ).queue();
            return;
        }

        List<Permission> newPerms = perms.stream().filter(perm -> !Permission.VIEW_CHANNEL.equals(perm)).toList();
        permAction.setAllowed(newPerms).queue();
    }
}
