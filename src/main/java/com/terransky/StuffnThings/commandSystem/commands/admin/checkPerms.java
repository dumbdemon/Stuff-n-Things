package com.terransky.StuffnThings.commandSystem.commands.admin;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.ISlash;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
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

public class checkPerms implements ISlash {
    @Override
    public String getName() {
        return "check-perms";
    }

    @Override
    public CommandData commandData() {
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

    @SuppressWarnings("ConstantConditions")
    @Override
    public void slashExecute(@NotNull SlashCommandInteractionEvent event) {
        EnumSet<Permission> myPerms;
        GuildChannel toCheck = null;
        if (event.getSubcommandName().equals("server")) {
            myPerms = event.getGuild().getSelfMember().getPermissions();
        } else {
            toCheck = event.getOption("check-channel", OptionMapping::getAsChannel);
            myPerms = event.getGuild().getSelfMember().getPermissions(toCheck);
        }

        List<Permission> requiredPerms = new Commons().requiredPerms(),
                dontHaveThis = new ArrayList<>();
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(new Commons().defaultEmbedColor)
                .setFooter(event.getUser().getAsTag(), event.getMember().getEffectiveAvatarUrl())
                .setTitle("Permission Checker");

        assert myPerms != null;
        for (Permission requiredPerm : requiredPerms) {
            boolean havePerm = myPerms.stream().anyMatch(it -> it.equals(requiredPerm));

            if (!havePerm) dontHaveThis.add(requiredPerm);
        }

        if (dontHaveThis.size() == 0) {
            event.replyEmbeds(eb.setDescription("I have all necessary permissions. Thank you! :heart:").build()).queue();
        } else {
            eb.setDescription("I'm missing the following permissions%s:".formatted(toCheck != null ? " for " + toCheck.getAsMention() : ""));
            for (Permission permission : dontHaveThis) {
                eb.addField(permission.getName(), "false", false);
            }
            event.replyEmbeds(eb.build()).queue();
        }
    }
}
