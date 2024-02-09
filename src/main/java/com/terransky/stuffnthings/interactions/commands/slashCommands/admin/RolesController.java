package com.terransky.stuffnthings.interactions.commands.slashCommands.admin;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class RolesController implements ICommandSlash {
    private static void addRole(@NotNull SlashCommandInteractionEvent event, EventBlob blob, @NotNull Member targetMember,
                                Role roleToGive, String errorEmbedTitle) {
        if (targetMember.getRoles().contains(roleToGive)) {
            event.replyEmbeds(blob.getStandardEmbed(errorEmbedTitle, EmbedColor.ERROR)
                    .setDescription(String.format("User [%s] already has the %s role.",
                        targetMember.getAsMention(),
                        roleToGive.getAsMention()
                    )).build())
                .setEphemeral(true)
                .queue();
            return;
        }

        blob.getGuild().addRoleToMember(targetMember, roleToGive).queue(ignored ->
                event.replyEmbeds(blob.getStandardEmbed("Role Added to member")
                    .setDescription(String.format("Role [%s] was successfully added to member [%s]!",
                        roleToGive.getAsMention(),
                        targetMember.getAsMention()
                    ))
                    .build()).queue(),
            error -> {
                event.replyEmbeds(blob.getStandardEmbed("Failed to Give Role to Member", EmbedColor.ERROR)
                        .setDescription(Responses.INTERACTION_FAILED.getMessage())
                        .build())
                    .setEphemeral(true)
                    .queue();
                LoggerFactory.getLogger(RolesController.class).error("Error in assigning role.", error);
            }
        );
    }

    private static void removeRole(@NotNull SlashCommandInteractionEvent event, EventBlob blob, @NotNull Member targetMember,
                                   Role roleToGive, String errorEmbedTitle) {
        if (!targetMember.getRoles().contains(roleToGive)) {
            event.replyEmbeds(blob.getStandardEmbed(errorEmbedTitle, EmbedColor.ERROR)
                    .setDescription(String.format("User [%s] does not have the %s role.",
                        targetMember.getAsMention(),
                        roleToGive.getAsMention()
                    )).build())
                .setEphemeral(true)
                .queue();
            return;
        }

        blob.getGuild().removeRoleFromMember(targetMember, roleToGive).queue(ignored ->
                event.replyEmbeds(blob.getStandardEmbed("Role removed from member")
                    .setDescription(String.format("Role [%s] was successfully removed from member [%s]!",
                        roleToGive.getAsMention(),
                        targetMember.getAsMention()
                    ))
                    .build()).queue(),
            error -> {
                event.replyEmbeds(blob.getStandardEmbed("Failed to Remove Role to Member", EmbedColor.ERROR)
                        .setDescription(Responses.INTERACTION_FAILED.getMessage())
                        .build())
                    .setEphemeral(true)
                    .queue();
                LoggerFactory.getLogger(RolesController.class).error("Error in removing role.", error);
            }
        );
    }

    @Override
    public String getName() {
        return "roles";
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, EventBlob blob) throws FailedInteractionException,
        IOException, ExecutionException, InterruptedException {
        boolean isRemove = "remove".equals(event.getSubcommandName());
        Optional<Role> optionalRole = Optional.ofNullable(event.getOption("role", OptionMapping::getAsRole));
        Optional<Member> optionalMember = Optional.ofNullable(event.getOption("user", OptionMapping::getAsMember));

        if (optionalRole.isEmpty()) {
            event.replyEmbeds(blob.getStandardEmbed("Invalid Role", EmbedColor.ERROR)
                    .setDescription("Role provided either does not exist or an error occurred in retrieval.").build())
                .setEphemeral(true)
                .queue();
            return;
        } else if (optionalMember.isEmpty()) {
            event.replyEmbeds(blob.getStandardEmbed("Invalid User", EmbedColor.ERROR)
                    .setDescription("Member either does not exist or an error occurred in retrieval.").build())
                .setEphemeral(true)
                .queue();
            return;
        }

        Role roleToGive = optionalRole.get();
        Member targetMember = optionalMember.get();

        String errorEmbedTitle = String.format("Cannot %s Role %s User", isRemove ? "Remove" : "Give",
            isRemove ? "from" : "to");
        if (!blob.getSelfMember().canInteract(targetMember)) {
            event.replyEmbeds(blob.getStandardEmbed(errorEmbedTitle, EmbedColor.ERROR)
                    .setDescription(String.format("Unable to give role [%s] to %s.%nPlease make sure my role [%s] is above all regular members.",
                        roleToGive.getAsMention(),
                        targetMember.getAsMention(),
                        Objects.requireNonNull(blob.getGuild().getBotRole()).getAsMention()
                    )).build())
                .setEphemeral(true)
                .queue();
            return;
        } else if (!blob.getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.replyEmbeds(blob.getStandardEmbed("Cannot Give Role to User", EmbedColor.ERROR)
                    .setDescription(String.format("Cannot perform action due to a lack of Permission. Need `%s` to proceed.",
                        Permission.MANAGE_ROLES.getName())).build())
                .setEphemeral(true)
                .queue();
            return;
        }

        if (isRemove) {
            removeRole(event, blob, targetMember, roleToGive, errorEmbedTitle);
            return;
        }

        addRole(event, blob, targetMember, roleToGive, errorEmbedTitle);
    }

    @Override
    public Metadata getMetadata() {
        List<OptionData> options = List.of(
            new OptionData(OptionType.USER, "user", "User to manage roles of", true),
            new OptionData(OptionType.ROLE, "role", "Role to give/remove a user", true)
        );
        return new Metadata(getName(), "Adds or Removes a role from a user.", Mastermind.USER, CommandCategory.ADMIN,
            Metadata.parseDate(2024, 1, 28, 12, 34),
            Metadata.parseDate(2024, 2, 9, 16, 11))
            .addDefaultPerms(Permission.MANAGE_ROLES)
            .addSubcommands(
                new SubcommandData("add", "Adds a role to a user.")
                    .addOptions(options),
                new SubcommandData("remove", "Removes a rol from a user")
                    .addOptions(options)
            );
    }
}
