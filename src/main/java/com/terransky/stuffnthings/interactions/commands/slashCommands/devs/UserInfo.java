package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class UserInfo implements ICommandSlash {
    private static String setUserPerms(@NotNull Member member) {
        StringBuilder userPerms = new StringBuilder();
        String finalUserPerms;
        if (!member.getPermissions().isEmpty()) {
            for (Permission perm : member.getPermissions()) {
                userPerms.append(perm.getName()).append(", ");
            }
            finalUserPerms = userPerms.substring(0, userPerms.toString().length() - 2);
        } else finalUserPerms = "None";
        return finalUserPerms;
    }

    @NotNull
    private static String setPermissionStatus(@NotNull Member member) {
        final List<Permission> modPerms = Arrays.asList(
            Permission.BAN_MEMBERS,
            Permission.KICK_MEMBERS,
            Permission.MESSAGE_HISTORY,
            Permission.MESSAGE_MANAGE,
            Permission.MESSAGE_SEND
        );

        StringBuilder permText = new StringBuilder();
        if (member.hasPermission(modPerms)) {
            permText.append("Server Moderator");
            if (member.hasPermission(Permission.ADMINISTRATOR)) {
                permText.append(", Server Administrator");
                if (member.isOwner()) {
                    permText.append(", Server Owner");
                }
            }
        } else permText.append("Member");

        if (member.getId().equals(Config.getDeveloperId())) {
            permText.append(", Developer");
        }
        return permText.toString();
    }

    @Override
    public String getName() {
        return "user-info";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(this.getName(), "Get info on a specific user on the server! Defaults to you.", """
            Get info on a user or bot.
            The following info with be returned:
            • User ID
            • User Status
            • Total Roles
            • Top Tole
            • Server Permissions
            • Server Joined Date
            • Discord Joined Date
            • Boosting Status (if user)
            """, Mastermind.DEFAULT,
            CommandCategory.DEVS,
            Metadata.parseDate("2023-08-24T11:10Z"),
            Metadata.parseDate("2023-01-27T21:58Z")
        )
            .addOptions(
                new OptionData(OptionType.USER, "user", "Who you want to know about.")
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        String memberId = event.getOption("user", blob.getMember(), OptionMapping::getAsMember).getId();
        Member member = blob.getGuild().retrieveMemberById(memberId).complete();
        List<Role> roles = member.getRoles().stream().sorted().toList();
        int roleCount = roles.size() + 1;
        Role topRole = roles.isEmpty() ? blob.getGuild().getPublicRole() : roles.get(roles.size() - 1);

        String permissionStatus = setPermissionStatus(member);
        String userPerms = setUserPerms(member);

        EmbedBuilder eb = new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setTitle(WordUtils.capitalize(member.getEffectiveName()) + "'s Info")
            .setThumbnail(member.getEffectiveAvatarUrl())
            .addField("User ID", memberId, false)
            .addField("User Status", permissionStatus, false)
            .addField("Total Roles", "%d role%s".formatted(roleCount, roleCount > 1 ? "s" : ""), true)
            .addField("Top Role", topRole.getAsMention(), true)
            .addField("Server Permissions", "```%s```".formatted(userPerms), false)
            .addField("Joined Discord on", Timestamp.getDateAsTimestamp(member.getUser().getTimeCreated()), true)
            .addField("Joined Server on", Timestamp.getDateAsTimestamp(member.getTimeJoined()), true)
            .setFooter("Requested by " + blob.getMemberAsTag() + " | " + blob.getMemberId(), blob.getMemberEffectiveAvatarUrl());

        if (!member.getUser().isBot()) {
            String boostedText = (member.getTimeBoosted() != null) ?
                String.format(":gem: %s (%s)", Timestamp.getDateAsTimestamp(member.getTimeBoosted()),
                    Timestamp.getDateAsTimestamp(member.getTimeBoosted(), Timestamp.RELATIVE)) : ":x: Not Boosting.";
            eb.addField("Boosting Since", boostedText, false);
        }

        event.replyEmbeds(eb.build()).queue();
    }
}
