package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.dataSources.icanhazdadjoke.ICanHazDadJokeData;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.apiHandlers.ICanHazDadJokeHandler;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;

public class GetDadJokes implements ICommandSlash {
    private final Logger log = LoggerFactory.getLogger(GetDadJokes.class);

    @Override
    public String getName() {
        return "dad-jokes";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        return new Metadata(this.getName(), "Why was 6 afraid of 7? Because 7 was a registered 6 offender.", """
            An unoriginal or unfunny joke of a type supposedly told by middle-aged or older men.
            """, Mastermind.USER,
            CommandCategory.FUN,
            format.parse("25-8-2022_20:53"),
            format.parse("21-1-2023_16:05")
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        ICanHazDadJokeData theJoke = new ICanHazDadJokeHandler().getDadJoke();

        event.replyEmbeds(new EmbedBuilder()
            .setDescription(theJoke.getJoke())
            .setColor(EmbedColors.getDefault())
            .setFooter("Requested by %s | ID #%s".formatted(blob.getMemberAsTag(), theJoke.getId()))
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
