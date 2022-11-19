package com.terransky.stuffnthings.commandSystem.commands;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.commandSystem.metadata.Mastermind;
import com.terransky.stuffnthings.commandSystem.metadata.Metadata;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class userInfo implements ISlashCommand {
    @Override
    public String getName() {
        return "user-info";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat formatter = Commons.getFastDateFormat();
        var metadata = new Metadata(this.getName(), "Get info on a specific user on the server! Defaults to you.", """
            Get info on a user or bot.
            The following info with be returned:
            \u2022 User ID
            \u2022 User Status
            \u2022 Server Permissions
            \u2022 Server Joined Date
            \u2022 Discord Joined Date
            \u2022 Boosting Status (if user)
            """, Mastermind.DEFAULT,
            formatter.parse("24-08-2022_11:10"),
            formatter.parse("19-11-2022_11:40")
        );

        metadata.addOptions(
            new OptionData(OptionType.USER, "user", "Who you want to know about.")
        );

        return metadata;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild) {
        User uVictim = event.getOption("user", event.getUser(), OptionMapping::getAsUser);
        Member mVictim = event.getOption("user", event.getMember(), OptionMapping::getAsMember);
        StringBuilder permText = new StringBuilder();
        EmbedBuilder eb = new EmbedBuilder().setColor(Commons.getDefaultEmbedColor());
        final List<Permission> modPerms = Arrays.asList(
            Permission.BAN_MEMBERS,
            Permission.KICK_MEMBERS,
            Permission.MESSAGE_HISTORY,
            Permission.MESSAGE_MANAGE,
            Permission.MESSAGE_SEND
        );

        if (mVictim.hasPermission(modPerms)) {
            permText.append("Server Moderator");
            if (mVictim.hasPermission(Permission.ADMINISTRATOR)) {
                permText.append(", Server Administrator");
                if (mVictim.isOwner()) {
                    permText.append(", Server Owner");
                }
            }
        } else permText.append("Member");

        if (uVictim.getId().equals(Commons.getConfig().get("OWNER_ID"))) {
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
            mVictim = guild.retrieveMemberById(mVictim.getId()).complete();
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
