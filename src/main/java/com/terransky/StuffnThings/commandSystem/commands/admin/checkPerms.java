package com.terransky.StuffnThings.commandSystem.commands.admin;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
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

public class checkPerms implements ISlashCommand {
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
    public void execute(@NotNull SlashCommandInteractionEvent event) throws Exception {
        EnumSet<Permission> myPerms;
        GuildChannel toCheck = null;
        if (event.getSubcommandName() == null) throw new Exception("Discord API Error: No subcommand was given.");

        if (event.getSubcommandName().equals("server")) {
            myPerms = event.getGuild().getSelfMember().getPermissions();
        } else {
            toCheck = event.getOption("check-channel", OptionMapping::getAsChannel);
            assert toCheck != null;
            myPerms = event.getGuild().getSelfMember().getPermissions(toCheck);
        }

        List<Permission> requiredPerms = Commons.requiredPerms(),
            dontHaveThis = new ArrayList<>();
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(Commons.defaultEmbedColor)
            .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
            .setTitle("Permission Checker");

        for (Permission requiredPerm : requiredPerms) {
            boolean havePerm = myPerms.stream().anyMatch(it -> it.equals(requiredPerm));

            if (!havePerm) dontHaveThis.add(requiredPerm);
        }

        if (dontHaveThis.size() == 0) {
            event.replyEmbeds(eb.setDescription("I have all necessary permissions. Thank you! :heart:").build()).queue();
        } else {
            eb.setDescription("I'm missing the following permissions%s:".formatted(toCheck != null ? " for %s".formatted(toCheck.getAsMention()) : ""));
            for (Permission permission : dontHaveThis) {
                eb.addField(permission.getName(), "false", false);
            }
            event.replyEmbeds(eb.build()).queue();
        }
    }
}
