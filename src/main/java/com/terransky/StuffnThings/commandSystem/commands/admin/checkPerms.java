package com.terransky.StuffnThings.commandSystem.commands.admin;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.ExtraDetails.ExtraDetails;
import com.terransky.StuffnThings.commandSystem.ExtraDetails.Mastermind;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
import com.terransky.StuffnThings.exceptions.DiscordAPIException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class checkPerms implements ISlashCommand {

    public static @NotNull List<Permission> requiredPerms() {
        List<Permission> permissionList = new ArrayList<>();
        //For funsies
        permissionList.add(Permission.MESSAGE_SEND);
        permissionList.add(Permission.MESSAGE_ADD_REACTION);
        permissionList.add(Permission.MESSAGE_EMBED_LINKS);
        permissionList.add(Permission.MESSAGE_EXT_EMOJI);
        permissionList.add(Permission.MESSAGE_EXT_STICKER);
        permissionList.add(Permission.VIEW_CHANNEL);

        //Moderation

        return permissionList;
    }

    private @NotNull String requiredPermsAsString() {
        var permString = new StringBuilder();

        for (Permission requiredPerm : requiredPerms()) {
            permString.append(requiredPerm.getName()).append(", ");
        }
        return permString.substring(0, permString.length() - 2);
    }

    @Override
    public String getName() {
        return "check-perms";
    }

    @Override
    public ExtraDetails getExtraDetails() {
        return new ExtraDetails(this.getName(), """
            Checks if the bot has all necessary permissions for this server or channel.
            Currently the bot requires:
            ```
            %s```
            """.formatted(requiredPermsAsString()), Mastermind.DEVELOPER, Permission.MANAGE_ROLES);
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(this.getName(), "Check if I have all of my perms needed for all of my commands.")
            .addSubcommands(
                new SubcommandData("server", "Check if I have all of my perms needed for all of my commands for the server."),
                new SubcommandData("channel", "Check if I have all of my perms needed for all of my commands in a specific channel.")
                    .addOptions(
                        new OptionData(OptionType.CHANNEL, "check-channel", "The channel to check.", true)
                            .setChannelTypes(ChannelType.TEXT, ChannelType.VOICE)
                    )
            )
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) throws Exception {
        EnumSet<Permission> myPerms;
        GuildChannel toCheck = null;
        if (event.getSubcommandName() == null) throw new DiscordAPIException("No subcommand was given.");
        Guild guild = null;
        if (event.getGuild() != null) guild = event.getGuild();

        if (event.getSubcommandName().equals("server")) {
            myPerms = guild.getSelfMember().getPermissions();
        } else {
            toCheck = event.getOption("check-channel", OptionMapping::getAsChannel);
            assert toCheck != null;
            myPerms = guild.getSelfMember().getPermissions(toCheck);
        }

        List<Permission> requiredPerms = requiredPerms(),
            dontHaveThis = new ArrayList<>();
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(Commons.DEFAULT_EMBED_COLOR)
            .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
            .setTitle("Permission Checker");

        for (Permission requiredPerm : requiredPerms) {
            boolean havePerm = myPerms.stream().anyMatch(it -> it.equals(requiredPerm));

            if (!havePerm) dontHaveThis.add(requiredPerm);
        }

        boolean ifToCheck = toCheck != null;
        if (dontHaveThis.size() == 0) {
            event.replyEmbeds(eb.setDescription("I have all necessary permissions for %s. Thank you! :heart:".formatted(ifToCheck ? toCheck.getAsMention() : "this server")).build()).queue();
        } else {
            eb.setDescription("I'm missing the following permissions for %s:".formatted(ifToCheck ? toCheck.getAsMention() : "this server"));
            for (Permission permission : dontHaveThis) {
                eb.addField(permission.getName(), "false", false);
            }
            event.replyEmbeds(eb.build()).queue();
        }
    }
}
