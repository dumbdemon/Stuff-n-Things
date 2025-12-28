package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.BotColors;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserInfo extends SlashCommandInteraction {

    public UserInfo() {
        super("user-info", "Get info on a specific user on the server! Defaults to you.", Mastermind.DEFAULT, CommandCategory.DEVS,
            parseDate(2023, 8, 24, 11, 10),
            parseDate(2025, 12, 27, 5, 56)
        );
        addOptions(
            new OptionData(OptionType.USER, "user", "Who you want to know about.")
        );
    }

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
    private static String setPermissionStatus(@NotNull Member member, Member selfMember) {
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

        if (member.getId().equals(StuffNThings.getConfig().getCore().getOwnerId())) {
            permText.append(", Developer");
        }

        if (member.equals(selfMember)) {
            permText.append(", Me");
        }

        return permText.toString();
    }

    @NotNull
    public static Container getUserInfo(@NotNull Member member, @NotNull EventBlob blob) throws ExecutionException, InterruptedException {
        List<Role> roles = member.getRoles().stream().sorted().toList();
        int roleCount = roles.size() + 1;
        Role topRole = roles.isEmpty() ? blob.getGuild().getPublicRole() : roles.get(roles.size() - 1);

        String permissionStatus = setPermissionStatus(member, blob.getSelfMember());
        String userPerms = setUserPerms(member);
        User.Profile profile = member.getUser().retrieveProfile().submit().get();
        Color embedColor = profile.getAccentColor() == null ? BotColors.SUB_DEFAULT.getColor() : profile.getAccentColor();

        List<ContainerChildComponent> children = new ArrayList<>();

        children.add(
            Section.of(
                Thumbnail.fromUrl(
                    member.getEffectiveAvatarUrl()
                ),
                TextDisplay.of("## " + WordUtils.capitalize(member.getEffectiveName()) + "'s Info"),
                TextDisplay.of(String.format("### Username\n%s", member.getUser().getName())),
                TextDisplay.of(String.format("### User ID\n%s", member.getId()))
            )
        );

        if (member.getUser().isBot())
            children.add(TextDisplay.of(String.format("Is System?\n%s", member.getUser().isSystem() ? "Yes" : "No")));

        children.add(Separator.createDivider(Separator.Spacing.SMALL));
        children.add(TextDisplay.of(String.format("### User Status\n%s", permissionStatus)));
        children.add(TextDisplay.of(String.format("### Top Role\n%s", topRole.getAsMention())));
        children.add(TextDisplay.of(String.format("### Total Roles\n%d role%s", roleCount, roleCount > 1 ? "s" : "")));
        children.add(TextDisplay.of(String.format("### Server Permissions\n```%s```".formatted(userPerms))));
        children.add(Separator.createDivider(Separator.Spacing.SMALL));
        if (!member.getUser().isBot()) {
            String boostedText = (member.getTimeBoosted() != null) ?
                String.format("Boosting Since :gem: %s (%s)", Timestamp.getDateAsTimestamp(member.getTimeBoosted()),
                    Timestamp.getDateAsTimestamp(member.getTimeBoosted(), Timestamp.RELATIVE)) : ":x: Not Boosting";
            children.add(TextDisplay.of(boostedText));
        }
        children.add(TextDisplay.of(String.format("Joined Discord on %s", Timestamp.getDateAsTimestamp(member.getUser().getTimeCreated()))));
        children.add(TextDisplay.of(String.format("Joined Server on %s", Timestamp.getDateAsTimestamp(member.getTimeJoined()))));


        return Container.of(children).withAccentColor(embedColor);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        String memberId = event.getOption("user", blob.getMember(), OptionMapping::getAsMember).getId();
        Member member = blob.getGuild().retrieveMemberById(memberId).submit().get();

        event.replyComponents(getUserInfo(member, blob)).queue();
    }
}
