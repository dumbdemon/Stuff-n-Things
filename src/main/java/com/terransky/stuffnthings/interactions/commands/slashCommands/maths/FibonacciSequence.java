package com.terransky.stuffnthings.interactions.commands.slashCommands.maths;

import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class FibonacciSequence extends SlashCommandInteraction {

    private static float[] fibonacciCache;

    public FibonacciSequence() {
        super("fibonacci", "Get the nth number in the Fibonacci sequence.", Mastermind.DEVELOPER, CommandCategory.MATHS,
            parseDate(2022, 11, 11, 20, 50),
            parseDate(2025, 12, 28, 12, 12)
        );
        addSubcommands(
            new SubcommandData("at-nth", "Get a specific value.")
                .addOptions(
                    new OptionData(OptionType.INTEGER, "nth", "Which value to return.", true)
                        .setRequiredRange(1, 186)
                ),
            new SubcommandData("whole-sequence", "Get the whole sequence until the nth value")
                .addOptions(
                    new OptionData(OptionType.INTEGER, "nth", "Which value to return up to.", true)
                        .setRequiredRange(1, 186)
                )
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");
        event.deferReply().queue();
        TextDisplay textDisplay;

        int nth = event.getOption("nth", 3, OptionMapping::getAsInt);

        fibonacciCache = new float[nth + 1];
        fibonacciCache[1] = 1;
        float nthValue = getFibonacciAt(nth);
        String nthSuffix = nth % 10 == 2 && nth != 12 ? "nd" : (nth % 10 == 3 && nth != 13 ? "rd" : "th"),
            returnString;

        if (subcommand.equals("at-nth")) {
            String replace;
            if (nthValue < 1e3) {
                replace = "";
            } else replace = " ";

            returnString = Formatter.largeNumberFormat(nthValue).replace(".0" + replace, replace);
            textDisplay = TextDisplay.of(String.format("### The %s%s value is%n```%s```", nth, nthSuffix, returnString));
        } else {
            StringBuilder fibonacciString = new StringBuilder();

            for (float v : fibonacciCache) {
                fibonacciString.append(Formatter.largeNumberFormat(v)).append(", ");
            }
            returnString = fibonacciString.substring(0, fibonacciString.length() - 2)
                .replaceAll(".0 ", " ")
                .replaceAll(".0,", ",");

            textDisplay = TextDisplay.of(String.format("### The Fibonacci sequence up to the %s%s value is%n```%s```",
                nth,
                nthSuffix,
                returnString.replace(".0", "")
            ));
        }

        event.getHook().sendMessageComponents(StandardResponse.getResponseContainer(this, textDisplay)).queue();
    }

    private float getFibonacciAt(int n) {
        if (n <= 1) {
            return n;
        }

        if (fibonacciCache[n] != 0) {
            return fibonacciCache[n];
        }

        float nthFibonacciNumber = (getFibonacciAt(n - 1) + getFibonacciAt(n - 2));
        fibonacciCache[n] = nthFibonacciNumber;

        return nthFibonacciNumber;
    }
}
