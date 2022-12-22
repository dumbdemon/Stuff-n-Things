package com.terransky.stuffnthings.commandSystem.commands.admin;

import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.commandSystem.utilities.Mastermind;
import com.terransky.stuffnthings.commandSystem.utilities.Metadata;
import com.terransky.stuffnthings.commandSystem.utilities.SlashModule;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interfaces.ICommandSlash;
import com.terransky.stuffnthings.utilities.EmbedColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class checkPerms implements ICommandSlash {

    public static @NotNull List<Permission> getRequiredPerms() {
        List<Permission> permissionList = new ArrayList<>();
        //For funsies
        permissionList.add(Permission.MESSAGE_SEND);
        permissionList.add(Permission.MESSAGE_ADD_REACTION);
        permissionList.add(Permission.MESSAGE_EMBED_LINKS);
        permissionList.add(Permission.MESSAGE_EXT_EMOJI);
        permissionList.add(Permission.MESSAGE_EXT_STICKER);
        permissionList.add(Permission.VIEW_CHANNEL);

        //Moderation
        permissionList.add(Permission.MANAGE_CHANNEL);
        permissionList.add(Permission.ADMINISTRATOR);

        return permissionList;
    }

    @Override
    public String getName() {
        return "check-perms";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        var permString = new StringBuilder();

        for (Permission requiredPerm : getRequiredPerms()) {
            permString.append(requiredPerm.getName()).append(", ");
        }

        return new Metadata(this.getName(), "Check if I have all of my perms needed for all of my commands.", """
            Checks if the bot has all necessary permissions for this server or channel.
            Currently the bot requires:
            ```
            %s```
            """.formatted(permString.substring(0, permString.length() - 2)),
            Mastermind.DEVELOPER,
            SlashModule.ADMIN,
            format.parse("30-08-2022_16:14"),
            format.parse("21-12-2022_19:57")
        )
            .addDefaultPerms(Permission.MANAGE_ROLES)
            .addSubcommands(
                new SubcommandData("server", "Check if I have all of my perms needed for all of my commands for the server."),
                new SubcommandData("channel", "Check if I have all of my perms needed for all of my commands in a specific channel.")
                    .addOptions(
                        new OptionData(OptionType.CHANNEL, "check-channel", "The channel to check.", true)
                            .setChannelTypes(ChannelType.TEXT, ChannelType.VOICE)
                    )
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        EnumSet<Permission> myPerms;
        GuildChannel toCheck = null;
        if (event.getSubcommandName() == null) throw new DiscordAPIException("No subcommand was given.");

        if (event.getSubcommandName().equals("server")) {
            myPerms = blob.getGuild().getSelfMember().getPermissions();
        } else {
            Optional<GuildChannel> ifToCheck = Optional.ofNullable(event.getOption("check-channel", OptionMapping::getAsChannel));
            toCheck = ifToCheck.orElseThrow(DiscordAPIException::new);
            myPerms = blob.getGuild().getSelfMember().getPermissions(toCheck);
        }

        List<Permission> doNotHaveThis = new ArrayList<>();
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
            .setTitle("Permission Checker");

        for (Permission requiredPerm : getRequiredPerms()) {
            if (!myPerms.contains(requiredPerm)) doNotHaveThis.add(requiredPerm);
        }

        boolean ifToCheck = toCheck != null;
        if (doNotHaveThis.isEmpty()) {
            event.replyEmbeds(eb.setDescription("I have all necessary permissions for %s. Thank you! :heart:".formatted(ifToCheck ? toCheck.getAsMention() : "this server")).build()).queue();
        } else {
            StringBuilder sb = new StringBuilder();
            eb.setDescription("I'm missing the following permissions for %s:\n```json\n[\n".formatted(ifToCheck ? toCheck.getAsMention() : "this server"));
            for (Permission permission : doNotHaveThis) {
                String oneWord = permission.getName().replace("(s)", "s").replace(" ", "_");
                sb.append("\s\s").append(oneWord).append(" : false,\n");
            }
            eb.appendDescription(sb.substring(0, sb.length() - 2) + "\n]```")
                .setColor(EmbedColors.getError());
            event.replyEmbeds(eb.build()).queue();
        }
    }
}
