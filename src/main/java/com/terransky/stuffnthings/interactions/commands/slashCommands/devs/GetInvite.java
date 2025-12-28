package com.terransky.stuffnthings.interactions.commands.slashCommands.devs;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interactions.commands.slashCommands.admin.CheckPerms;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class GetInvite extends SlashCommandInteraction {

    public GetInvite() {
        super("get-invite", "Get an invite for the bot.", Mastermind.DEFAULT, CommandCategory.DEVS,
            parseDate(2022, 8, 24, 11, 10),
            parseDate(2025, 12, 27, 0, 50)
        );
        addRestrictedServer(StuffNThings.getConfig().getCore().getSupportGuild().getId());
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        event.reply(event.getJDA().getInviteUrl(CheckPerms.getRequiredPerms())).useComponentsV2(false).queue();
    }
}
