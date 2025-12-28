package com.terransky.stuffnthings.interactions.commands.userContextMenus;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interactions.commands.slashCommands.devs.UserInfo;
import com.terransky.stuffnthings.interfaces.interactions.UserCommandInteraction;
import com.terransky.stuffnthings.utilities.cannedAgenda.GuildOnly;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.InteractionType;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class UserInfoMenu extends UserCommandInteraction {

    public UserInfoMenu() {
        super("More Info");
    }

    @Override
    public void execute(@NotNull UserContextInteractionEvent event, EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        if (event.getTargetMember() == null) {
            GuildOnly.interactionResponse(event, InteractionType.COMMAND_USER);
            return;
        }

        event.replyComponents(UserInfo.getUserInfo(event.getTargetMember(), blob))
            .setEphemeral(true)
            .queue();
    }
}
