package com.terransky.stuffnthings.interactions.commands.slashCommands.admin;

import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class checkPerms implements ICommandSlash {

    @NotNull
    @Contract(" -> new")
    public static List<Permission> getRequiredPerms() {
        return new ArrayList<>() {{
            //For funsies
            add(Permission.MESSAGE_SEND);
            add(Permission.MESSAGE_ADD_REACTION);
            add(Permission.MESSAGE_EMBED_LINKS);
            add(Permission.MESSAGE_EXT_EMOJI);
            add(Permission.MESSAGE_EXT_STICKER);
            add(Permission.VIEW_CHANNEL);

            //Moderation
            add(Permission.MANAGE_CHANNEL);
            add(Permission.ADMINISTRATOR);
        }};
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
            Checks if the bot has **all necessary** permissions for this server or channel.
            Currently the bot requires:
            ```
            %s```
            """.formatted(permString.substring(0, permString.length() - 2)),
            Mastermind.DEVELOPER,
            CommandCategory.ADMIN,
            format.parse("30-08-2022_16:14"),
            format.parse("29-12-2022_10:14")
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

        List<Permission> doNotHaveThis = new ArrayList<>(getRequiredPerms().stream().filter(permission -> !myPerms.contains(permission)).toList());
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
            .setTitle("Permission Checker");

        boolean ifToCheck = toCheck != null,
            adminCheck = doNotHaveThis.size() == 1 && doNotHaveThis.stream().anyMatch(permission -> permission.equals(Permission.ADMINISTRATOR));

        if (doNotHaveThis.isEmpty() || adminCheck) {
            if (adminCheck)
                eb.addField("WARNING: Administrator", "One or more commands needs `Administrator` to work without extra effort from the user.", false);

            event.replyEmbeds(eb.setDescription("I have all necessary permissions for %s. Thank you! :heart:"
                .formatted(ifToCheck ? toCheck.getAsMention() : "this server")).build()).queue();
            return;
        }

        StringBuilder sb = new StringBuilder();
        eb.setDescription("I'm missing the following permissions for %s:\n```json\n[\n".formatted(ifToCheck ? toCheck.getAsMention() : "this server"));
        for (Permission permission : doNotHaveThis.stream().sorted().toList()) {
            String oneWord = permission.getName().replace("(s)", "s").replaceAll(" ", "_");
            sb.append("\s\s\s\s").append(oneWord).append(" : false,\n");
        }
        eb.appendDescription(sb.substring(0, sb.length() - 2))
            .appendDescription("\n]```")
            .setColor(EmbedColors.getError());
        event.replyEmbeds(eb.build()).queue();
    }
}
