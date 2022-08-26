package com.terransky.TestingBot.slashSystem.commands;

import com.terransky.TestingBot.Commons;
import com.terransky.TestingBot.slashSystem.ISlash;
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
import java.util.Objects;

public class userInfo implements ISlash {
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
    public void slashExecute(@NotNull SlashCommandInteractionEvent event) {
        User uVictim = event.getOption("user", event.getUser(), OptionMapping::getAsUser);
        Member mVictim = event.getOption("user", event.getMember(), OptionMapping::getAsMember);
        StringBuilder permText = new StringBuilder();
        EmbedBuilder eb = new EmbedBuilder().setColor(new Commons().defaultEmbedColor);
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
        String[] str = mVictim.getPermissions().toString().replace("[", "").replace("]", "").split(",\s");
        if (str.length != 0) {
            for (String perm : str) {
                if (Objects.equals(perm, str[str.length - 1]))
                    userPerms.append(WordUtils.capitalize(perm.toLowerCase().replace("_", "\s")));
                else
                    userPerms.append(WordUtils.capitalize(perm.toLowerCase().replace("_", "\s"))).append(",\s");
            }
        } else userPerms.append("None");

        String finalSTR = userPerms.toString()
                .replace("\sTts", "\sTTS")
                .replace("\sExt", "\sEXT")
                .replace("\sAnd", "\sand")
                .replace("\sTo", "\sto")
                .replace("\sIn", "\sin")
                .replace("\sVad", "\sVAD");

        eb.setAuthor(WordUtils.capitalize(mVictim.getEffectiveName()) + "'s Info")
                .setThumbnail(mVictim.getEffectiveAvatarUrl())
                .addField(new MessageEmbed.Field("User ID", uVictim.getId(), true))
                .addField(new MessageEmbed.Field("User Status", permText.toString(), true))
                .addField(new MessageEmbed.Field("Server Permissions", finalSTR, false))
                .addField(new MessageEmbed.Field("Joined Server on", "<t:" + mVictim.getTimeJoined().toEpochSecond() + ":F>", true))
                .addField(new MessageEmbed.Field("Joined Discord on", "<t:" + uVictim.getTimeCreated().toEpochSecond() + ":F>", true))
                .setFooter("Requested by " + event.getUser().getAsTag() + " | " + event.getUser().getId(), event.getMember().getEffectiveAvatarUrl());

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
