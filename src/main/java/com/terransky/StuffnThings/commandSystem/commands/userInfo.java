package com.terransky.StuffnThings.commandSystem.commands;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.ExtraDetails.ExtraDetails;
import com.terransky.StuffnThings.commandSystem.ExtraDetails.Mastermind;
import com.terransky.StuffnThings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class userInfo implements ISlashCommand {
    @Override
    public String getName() {
        return "user-info";
    }

    @Override
    public ExtraDetails getExtraDetails() {
        return new ExtraDetails(this.getName(), """
            Get info on a user or bot.
            The following info with be returned:
            \u2022 User ID
            \u2022 User Status
            \u2022 Server Permissions
            \u2022 Server Joined Date
            \u2022 Discord Joined Date
            \u2022 Boosting Status (if user)
            """, Mastermind.DEFAULT);
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(this.getName(), "Get info on a specific user on the server! Defaults to you.")
            .addOption(OptionType.USER, "user", "Who you want to know about.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) return;
        User uVictim = event.getOption("user", event.getUser(), OptionMapping::getAsUser);
        Member mVictim = event.getOption("user", event.getMember(), OptionMapping::getAsMember);
        StringBuilder permText = new StringBuilder();
        EmbedBuilder eb = new EmbedBuilder().setColor(Commons.DEFAULT_EMBED_COLOR);
        List<Permission> modPerms = new ArrayList<>();
        modPerms.add(Permission.KICK_MEMBERS);
        modPerms.add(Permission.BAN_MEMBERS);
        modPerms.add(Permission.MESSAGE_SEND);
        modPerms.add(Permission.MESSAGE_MANAGE);
        modPerms.add(Permission.MESSAGE_HISTORY);

        if (mVictim.hasPermission(modPerms)) {
            permText.append("Server Moderator");
            if (mVictim.hasPermission(Permission.ADMINISTRATOR)) {
                permText.append(", Server Administrator");
                if (mVictim.isOwner()) {
                    permText.append(", Server Owner");
                }
            }
        } else permText.append("Member");

        if (uVictim.getId().equals(Commons.CONFIG.get("OWNER_ID"))) {
            permText.append(", Developer");
        }

        StringBuilder userPerms = new StringBuilder();
        String finalUserPerms;

        if (mVictim.getPermissions().size() != 0) {
            for (Permission perm : mVictim.getPermissions()) {
                userPerms.append(perm.getName()).append(", ");
            }
            finalUserPerms = userPerms.substring(0, userPerms.toString().length() - 2);
        } else finalUserPerms = "None";

        if (!mVictim.hasTimeJoined()) {
            mVictim = event.getGuild().retrieveMemberById(mVictim.getId()).complete();
        }

        eb.setAuthor(WordUtils.capitalize(mVictim.getEffectiveName()) + "'s Info")
            .setThumbnail(mVictim.getEffectiveAvatarUrl())
            .addField("User ID", uVictim.getId(), true)
            .addField("User Status", permText.toString(), true)
            .addField("Server Permissions", "```%s```".formatted(finalUserPerms), false)
            .addField("Joined Server on", "<t:%s:F>".formatted(mVictim.getTimeJoined().toEpochSecond()), true)
            .addField("Joined Discord on", "<t:%s:F>".formatted(uVictim.getTimeCreated().toEpochSecond()), true)
            .setFooter("Requested by " + event.getUser().getAsTag() + " | " + event.getUser().getId(), event.getUser().getEffectiveAvatarUrl());

        if (!uVictim.isBot()) {
            String boostedText =
                (mVictim.getTimeBoosted() != null) ? ":gem: <t:%d:F> (<t:%d:R>)".formatted(mVictim.getTimeBoosted().toEpochSecond(), mVictim.getTimeBoosted().toEpochSecond()) :
                    ":x: Not Boosting.";
            eb.addField("Boosting Since", boostedText, false);
        }

        event.replyEmbeds(eb.build()).queue();
    }
}
