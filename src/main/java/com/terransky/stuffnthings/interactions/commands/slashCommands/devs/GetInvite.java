package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interactions.commands.slashCommands.admin.CheckPerms;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
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
            Metadata.parseDate("2022-08-24T11:10Z"),
            Metadata.parseDate("2023-03-05T16:18Z")
        );
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    @Override
    public @Nullable List<Long> getServerRestrictions() {
        return List.of(Config.getSupportGuildIdLong());
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
