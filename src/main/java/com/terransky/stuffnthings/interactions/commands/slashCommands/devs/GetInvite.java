package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interactions.commands.slashCommands.admin.CheckPerms;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public class GetInvite implements ICommandSlash {
    @Override
    public String getName() {
        return "get-invite";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(this.getName(), "Get an invite for the bot.",
            Mastermind.DEFAULT, CommandCategory.DEVS,
            Metadata.parseDate(2022, 8, 24, 11, 10),
            Metadata.parseDate(2024, 8, 20, 12, 3)
        );
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    @Override
    public @Nullable List<Long> getServerRestrictions() {
        return List.of(StuffNThings.getConfig().getCore().getSupportGuild().getId());
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        event.replyEmbeds(
            new EmbedBuilder()
                .setColor(EmbedColor.DEFAULT.getColor())
                .setTitle("You can't even use this.", event.getJDA().getInviteUrl(CheckPerms.getRequiredPerms()))
                .build()
        ).queue();
    }
}
