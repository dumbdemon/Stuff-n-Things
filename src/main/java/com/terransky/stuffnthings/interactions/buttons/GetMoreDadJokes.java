package com.terransky.stuffnthings.interactions.buttons;

import com.terransky.stuffnthings.dataSources.icanhazdadjoke.ICanHazDadJokeData;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interactions.commands.slashCommands.fun.GetDadJokes;
import com.terransky.stuffnthings.interfaces.interactions.IButton;
import com.terransky.stuffnthings.utilities.apiHandlers.ICanHazDadJokeHandler;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.EmbedColor;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GetMoreDadJokes implements IButton {
    @Override
    public String getName() {
        return "get-dad-joke";
    }

    @Override
    public void execute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        ICanHazDadJokeData theJoke;

        try {
            theJoke = new ICanHazDadJokeHandler().getDadJoke();
        } catch (InterruptedException e) {
            event.replyEmbeds(
                blob.getStandardEmbed(new GetDadJokes().getNameReadable(), EmbedColor.ERROR)
                    .setDescription(Responses.NETWORK_OPERATION.getMessage())
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        MessageEditData message = new MessageEditBuilder()
            .setEmbeds(blob.getStandardEmbed()
                .setDescription(theJoke.getJoke())
                .setFooter("%s | ID#%s".formatted(event.getUser().getAsTag(), theJoke.getId()), blob.getMemberEffectiveAvatarUrl())
                .build()
            ).build();

        event.editMessage(message).queue();
    }
}
