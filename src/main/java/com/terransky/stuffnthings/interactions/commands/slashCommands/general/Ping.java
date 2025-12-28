package com.terransky.stuffnthings.interactions.commands.slashCommands.general;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.StandardResponse;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Ping extends SlashCommandInteraction {

    public Ping() {
        super("ping", "Pong!", Mastermind.DEFAULT, CommandCategory.GENERAL,
            parseDate(2022, 8, 24, 11, 10),
            parseDate(2025, 12, 26, 23, 44)
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        JDA jda = event.getJDA();
        jda.getRestPing().queue(ping -> event.replyComponents(
            StandardResponse.getResponseContainer(this,
                List.of(
                    TextDisplay.of(String.format("### Rest Ping - %sms", ping)),
                    TextDisplay.of(String.format("### Web Socket Ping - %sms", jda.getGatewayPing()))
                )
            )
        ).queue());
    }
}
