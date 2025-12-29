package com.terransky.stuffnthings.interactions.buttons;

import com.terransky.stuffnthings.dataSources.icanhazdadjoke.ICanHazDadJokeData;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interactions.commands.slashCommands.fun.GetDadJokes;
import com.terransky.stuffnthings.interfaces.interactions.ButtonInteraction;
import com.terransky.stuffnthings.utilities.apiHandlers.ICanHazDadJokeHandler;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.StandardResponse;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class GetMoreDadJokes extends ButtonInteraction {

    public GetMoreDadJokes() {
        super("get-dad-joke");
    }

    @Override
    public void execute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        ICanHazDadJokeData theJoke;

        try {
            theJoke = new ICanHazDadJokeHandler().getDadJoke();
        } catch (InterruptedException e) {
            event.replyComponents(
                StandardResponse.getResponseContainer(new GetDadJokes().getNameReadable(), Responses.NETWORK_OPERATION)
            ).setEphemeral(true).queue();
            return;
        }

        MessageEditData message = new MessageEditBuilder()
            .setComponents(StandardResponse.getResponseContainer(new GetDadJokes(), List.of(
                    TextDisplay.of(theJoke.getJoke()),
                    TextDisplay.ofFormat("ID#%s", theJoke.getId())
                )),
                ActionRow.of(new GetMoreDadJokes().getButton(ButtonStyle.SUCCESS, "Get a new Dad Joke!"))
            ).build();

        event.editMessage(message).queue();
    }
}
