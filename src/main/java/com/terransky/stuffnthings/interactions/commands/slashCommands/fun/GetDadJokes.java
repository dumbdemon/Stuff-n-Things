package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.dataSources.icanhazdadjoke.ICanHazDadJokeData;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interactions.buttons.GetMoreDadJokes;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.apiHandlers.ICanHazDadJokeHandler;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.StandardResponse;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class GetDadJokes extends SlashCommandInteraction {
    private final Logger log = LoggerFactory.getLogger(GetDadJokes.class);

    public GetDadJokes() {
        super("dad-jokes", "Why was 6 afraid of 7? Because 7 was a registered 6 offender.",
            Mastermind.USER,
            CommandCategory.FUN,
            parseDate(2022, 8, 25, 20, 53),
            parseDate(2026, 2, 4, 23, 30));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        ICanHazDadJokeData theJoke;

        try {
            theJoke = new ICanHazDadJokeHandler().getDadJoke();
        } catch (InterruptedException e) {
            event.replyComponents(
                StandardResponse.getResponseContainer(this, Responses.NETWORK_OPERATION)
            ).setEphemeral(true).queue();
            return;
        }

        event.replyComponents(
                StandardResponse.getResponseContainer(this, List.of(
                    TextDisplay.of(theJoke.getJoke()),
                    TextDisplay.ofFormat("ID #%s", theJoke.getId())
                ))
            ).addComponents(ActionRow.of(new GetMoreDadJokes().getButton(ButtonStyle.SUCCESS, "Get a new Dad Joke!")))
            .queue(msg -> msg.editOriginalComponents(
                StandardResponse.getResponseContainer(this, "Action Expired! No more Dad jokes! Call the command again to get more!")
            ).queueAfter(10, java.util.concurrent.TimeUnit.MINUTES, msg2 ->
                log.debug("Button on message [{}] on server with ID [{}] has expired.", msg2.getId(), msg2.getGuild().getId())
            ));
    }
}
