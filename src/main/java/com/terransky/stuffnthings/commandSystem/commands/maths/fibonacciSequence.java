package com.terransky.stuffnthings.commandSystem.commands.maths;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.commandSystem.commands.mtg.calculateRats;
import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.commandSystem.utilities.Mastermind;
import com.terransky.stuffnthings.commandSystem.utilities.Metadata;
import com.terransky.stuffnthings.commandSystem.utilities.SlashModule;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

public class fibonacciSequence implements ISlashCommand {

    private static float[] fibonacciCache;

    @Override
    public String getName() {
        return "fibonacci";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Commons.getFastDateFormat();
        var metadata = new Metadata(this.getName(), "Get the nth number in the Fibonacci sequence.", """
            *From Oxford Languages*
            > a series of numbers in which each number (Fibonacci number) is the sum of the two preceding numbers.
                        
            This command returns the nth value in the *Fibonacci Sequence* or its whole sequence up to the nth value. Although the *Fibonacci Sequence* can go into infinity, this command has been limited to return up to the 186th value. Any higher and the command will return ∞ (infinity). This is due to the limitation of the Java data type Float. You can read more [here](https://www.w3schools.com/java/ref_keyword_float.asp).
            """, Mastermind.DEVELOPER,
            SlashModule.MATHS,
            format.parse("11-11-2022_20:50"),
            format.parse("30-11-2022_13:42")
        );

        metadata.addSubcommands(
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

        return metadata;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");

        int n = event.getOption("nth", 3, OptionMapping::getAsInt);
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle(WordUtils.capitalize(this.getName()))
            .setColor(Commons.getDefaultEmbedColor())
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl());
        MessageEditData messageEditData;

        fibonacciCache = new float[n + 1];
        fibonacciCache[1] = 1;
        float nthValue = getFibonacciAt(n);
        String numSuffix = n % 10 == 2 ? "nd" : (n % 10 == 3 ? "rd" : "th"), returnString;

        event.replyEmbeds(
            new EmbedBuilder()
                .setTitle(WordUtils.capitalize(this.getName()))
                .setDescription("Please wait. This may take a while...")
                .setColor(Commons.getDefaultEmbedColor())
                .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
                .build()
        ).queue();

        if (subcommand.equals("at-nth")) {
            if (nthValue < 1e3) {
                returnString = calculateRats.largeNumberFormat(getFibonacciAt(n)).replace(".0", "");
            } else returnString = calculateRats.largeNumberFormat(getFibonacciAt(n)).replace(".0 ", " ");

            messageEditData = new MessageEditBuilder()
                .setEmbeds(
                    eb.setDescription("The %s%s value of the Fibonacci sequence is:\n```%s```"
                            .formatted(n, numSuffix, returnString))
                        .build()
                )
                .build();
        } else {
            StringBuilder fibonacciString = new StringBuilder();

            for (float v : fibonacciCache) {
                fibonacciString.append(calculateRats.largeNumberFormat(v)).append(", ");
            }
            returnString = fibonacciString.substring(0, fibonacciString.length() - 2)
                .replaceAll(".0 ", " ")
                .replaceAll(".0,", ",");

            messageEditData = new MessageEditBuilder()
                .setEmbeds(
                    eb.setDescription("The Fibonacci sequence up to the %s%s value is:\n```%s```"
                            .formatted(n, numSuffix, returnString.replace(".0", "")))
                        .build()
                )
                .build();
        }

        event.getHook().editOriginal(messageEditData).queue();
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
