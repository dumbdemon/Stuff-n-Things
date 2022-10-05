package com.terransky.StuffnThings.commandSystem.commands;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
    public CommandData commandData() {
        return Commands.slash(this.getName(), "Get info on a specific user on the server! Defaults to you.")
            .addOption(OptionType.USER, "user", "Who you want to know about.");
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        User uVictim = event.getOption("user", event.getUser(), OptionMapping::getAsUser);
        Member mVictim = event.getOption("user", event.getMember(), OptionMapping::getAsMember);
        StringBuilder permText = new StringBuilder();
        EmbedBuilder eb = new EmbedBuilder().setColor(Commons.defaultEmbedColor);
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

        StringBuilder userPerms = new StringBuilder();
        String finalUserPerms;

        if (mVictim.getPermissions().size() != 0) {
            for (Permission perm : mVictim.getPermissions()) {
                userPerms.append(perm.getName()).append(", ");
            }
            finalUserPerms = userPerms.substring(0, userPerms.toString().length() - 2);
        } else finalUserPerms = "None";

        eb.setAuthor(WordUtils.capitalize(mVictim.getEffectiveName()) + "'s Info")
            .setThumbnail(mVictim.getEffectiveAvatarUrl())
            .addField(new MessageEmbed.Field("User ID", uVictim.getId(), true))
            .addField(new MessageEmbed.Field("User Status", permText.toString(), true))
            .addField(new MessageEmbed.Field("Server Permissions", finalUserPerms, false))
            .addField(new MessageEmbed.Field("Joined Server on", "<t:" + mVictim.getTimeJoined().toEpochSecond() + ":F>", true))
            .addField(new MessageEmbed.Field("Joined Discord on", "<t:" + uVictim.getTimeCreated().toEpochSecond() + ":F>", true))
            .setFooter("Requested by " + event.getUser().getAsTag() + " | " + event.getUser().getId(), event.getUser().getEffectiveAvatarUrl());

        if (!uVictim.isBot()) {
            String boostedText;
            if (mVictim.isBoosting())
                boostedText = ":gem: <t:%d:F> (<t:%d:R>)".formatted(mVictim.getTimeBoosted().toEpochSecond(), mVictim.getTimeBoosted().toEpochSecond());
            else boostedText = ":x: Not Boosting.";
            eb.addField(new MessageEmbed.Field("Boosting Since", boostedText, false));
        }

        event.replyEmbeds(eb.build()).queue();
    }
}