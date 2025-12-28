package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.StandardResponse;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class EightBall extends SlashCommandInteraction {

    private final List<String> RESPONSES = List.of(
        //affirmative answers
        "It is certain.",
        " It is decidedly so.",
        "Without a doubt.",
        "Yes definitely.",
        "You may rely on it.",
        "As I see it, yes.",
        "Most likely.",
        "Outlook good.",
        "Yes.",
        "Signs point to yes.",

        //non-committal answers
        "Reply hazy, try again.",
        "Ask again later.",
        "Better not tell you now.",
        "Cannot predict now.",
        "Concentrate and ask again.",

        //negative answers
        "Don't count on it.",
        "My reply is no.",
        "My sources say no.",
        "Outlook not so good.",
        "Very doubtful. "
    );

    public EightBall() {
        super("8ball", "Divinate a yes or no question to the 8Ball!",
            Mastermind.DEVELOPER, CommandCategory.FUN,
            parseDate(2023, 2, 27, 15, 17),
            parseDate(2025, 12, 28, 0, 5)
        );
        addOptions(
            new OptionData(OptionType.STRING, "query", "And what shall you ask the 8Ball?", true)
        );
    }

    @Override
    public String getName() {
        return "8ball";
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        String query = event.getOption("query", "", OptionMapping::getAsString);
        if (query.isEmpty()) throw new DiscordAPIException("No Query Received");
        String questionMark = query.charAt(query.length() - 1) != '?' ? "?" : "";

        Random random = new Random(OffsetDateTime.now().get(ChronoField.MILLI_OF_DAY) | blob.getMemberIdLong() | event.getChannel().getIdLong());
        event.replyComponents(
            StandardResponse.getResponseContainer(WordUtils.capitalize(query) + questionMark,
                TextDisplay.of(RESPONSES.get(random.nextInt(RESPONSES.size())))
            )
        ).queue();
    }
}
