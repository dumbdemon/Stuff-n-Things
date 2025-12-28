package com.terransky.stuffnthings.interactions.commands.slashCommands.admin;

import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.StandardResponse;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class CheckPerms extends SlashCommandInteraction {

    public CheckPerms() {
        super("check-perms", "Check if I have all of my perms needed for all of my commands.", Mastermind.DEVELOPER,
            CommandCategory.ADMIN, parseDate(2022, 6, 30, 16, 14),
            parseDate(2025, 12, 27, 1, 0)
        );
        setDefaultMemberPermissions(Permission.MANAGE_ROLES);
        addSubcommands(
            new SubcommandData("server", "Check if I have all of my perms needed for all of my commands for the server."),
            new SubcommandData("channel", "Check if I have all of my perms needed for all of my commands in a specific channel.")
                .addOptions(
                    new OptionData(OptionType.CHANNEL, "check-channel", "The channel to check.", true)
                        .setChannelTypes(ChannelType.TEXT, ChannelType.VOICE)
                )
        );
    }

    @NotNull
    @Contract(" -> new")
    public static List<Permission> getRequiredPerms() {
        return new ArrayList<>() {{
            //For funsies
            add(Permission.MESSAGE_SEND);
            add(Permission.MESSAGE_ADD_REACTION);
            add(Permission.MESSAGE_EMBED_LINKS);
            add(Permission.MESSAGE_EXT_STICKER);
            add(Permission.VIEW_CHANNEL);

            //Moderation
            add(Permission.MANAGE_CHANNEL);
            add(Permission.ADMINISTRATOR);
            add(Permission.MANAGE_WEBHOOKS);
        }};
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        EnumSet<Permission> myPerms;
        GuildChannel toCheck = null;
        if (event.getSubcommandName() == null) throw new DiscordAPIException("No subcommand was given.");

        if (event.getSubcommandName().equals("server")) {
            myPerms = blob.getSelfMember().getPermissions();
        } else {
            Optional<GuildChannel> ifToCheck = Optional.ofNullable(event.getOption("check-channel", OptionMapping::getAsChannel));
            toCheck = ifToCheck.orElseThrow(DiscordAPIException::new);
            myPerms = blob.getSelfMember().getPermissions(toCheck);
        }

        List<Permission> doNotHaveThis = new ArrayList<>(getRequiredPerms().stream().filter(permission -> !myPerms.contains(permission)).toList());
        List<ContainerChildComponent> children = new ArrayList<>();

        boolean ifToCheck = toCheck != null,
            adminCheck = doNotHaveThis.size() == 1 && doNotHaveThis.stream().anyMatch(permission -> permission.equals(Permission.ADMINISTRATOR));

        if (doNotHaveThis.isEmpty() || adminCheck) {
            if (adminCheck)
                children.addAll(List.of(
                    TextDisplay.of("WARNING: One or more commands needs `Administrator` to work without extra effort from the user. This is **not** required."),
                    Separator.createDivider(Separator.Spacing.SMALL)
                ));
            children.add(TextDisplay.of(String.format("I have all necessary permissions for %s. Thank you! :heart:", ifToCheck ? toCheck.getAsMention() : "this server")));

            event.replyComponents(StandardResponse.getResponseContainer(this, children)).queue();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("I'm missing the following permissions for %s:\n```json\n[\n", ifToCheck ? toCheck.getAsMention() : "this server"));
        for (Permission permission : doNotHaveThis.stream().sorted().toList()) {
            String oneWord = permission.getName().replace("(s)", "s").replaceAll(" ", "_");
            sb.append("\s\s\s\s").append(oneWord).append(" : false,\n");
        }
        sb.replace(sb.length() - 1, sb.length() - 1, "");
        sb.append("]```");
        event.replyComponents(StandardResponse.getResponseContainer(this, sb.toString())).queue();
    }
}
