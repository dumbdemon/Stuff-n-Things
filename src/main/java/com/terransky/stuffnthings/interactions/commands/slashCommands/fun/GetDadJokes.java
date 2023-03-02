package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.dataSources.icanhazdadjoke.ICanHazDadJokeData;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.apiHandlers.ICanHazDadJokeHandler;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.Metadata;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GetDadJokes implements ICommandSlash {
    private final Logger log = LoggerFactory.getLogger(GetDadJokes.class);

    @Override
    public String getName() {
        return "dad-jokes";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(this.getName(), "Why was 6 afraid of 7? Because 7 was a registered 6 offender.", """
            An unoriginal or unfunny joke of a type supposedly told by middle-aged or older men.
            """, Mastermind.USER,
            CommandCategory.FUN,
            Metadata.parseDate("2022-08-25T20:53Z"),
            Metadata.parseDate("2023-03-02T10:12Z")
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        ICanHazDadJokeData theJoke = new ICanHazDadJokeHandler().getDadJoke();

        event.replyEmbeds(blob.getStandardEmbed()
            .setDescription(theJoke.getJoke())
            .setFooter("%s | ID #%s".formatted(blob.getMemberAsTag(), theJoke.getId()), blob.getMemberEffectiveAvatarUrl())
            .build()
        ).addActionRow(
            Button.primary("get-dad-joke", "Get new Dad Joke!")
        ).queue(msg -> {
            MessageEditData editData = new MessageEditBuilder()
                .setComponents(
                    ActionRow.of(Button.danger("expired-button", "Get new Dad Joke!"))
                )
                .build();
            msg.editOriginal(editData).queueAfter(10, java.util.concurrent.TimeUnit.MINUTES, msg2 ->
                log.debug("Button on message [{}] on server with ID [{}] has expired.", msg2.getId(), msg2.getGuild().getId())
            );
        });
    }
}
