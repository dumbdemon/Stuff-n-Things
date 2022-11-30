package com.terransky.stuffnthings.commandSystem.commands.mtg;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.commandSystem.utilities.Mastermind;
import com.terransky.stuffnthings.commandSystem.utilities.Metadata;
import com.terransky.stuffnthings.commandSystem.utilities.SlashModule;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class calculateRats implements ISlashCommand {
    private static final NavigableMap<Float, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1e3f, " Thousand");
        suffixes.put(1e6f, " Million");
        suffixes.put(1e9f, " Billion");
        suffixes.put(1e12f, " Trillion");
        suffixes.put(1e15f, " Quadrillion");
        suffixes.put(1e18f, " Quintillion");
        suffixes.put(1e21f, " Sextillion");
        suffixes.put(1e24f, " Septillion");
        suffixes.put(1e27f, " Octillion");
        suffixes.put(1e30f, " Nonillion");
        suffixes.put(1e33f, " Decillion");
        suffixes.put(1e36f, " Undecillion");
    }

    /**
     * Makes extra large numbers look nice~ <br />
     * Code Courtesy of <a href="https://stackoverflow.com/questions/4753251/how-to-go-about-formatting-1200-to-1-2k-in-java">assylias's answer</a> on stackoverflow.
     *
     * @return {@link String} that contains a two-point decimal and the scale of value.
     */
    public static String largeNumberFormat(float value) {
        DecimalFormat simpleNum = new DecimalFormat("#.##");

        if (value == Float.MIN_VALUE) return largeNumberFormat(Float.MIN_VALUE + 1);
        if (value < 0) return "-" + largeNumberFormat(-value);
        if (value < 1000) return Float.toString(value);

        Map.Entry<Float, String> e = suffixes.floorEntry(value);
        float divideBy = e.getKey();
        String suffix = e.getValue();

        float truncated = value / (divideBy / 10);
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? simpleNum.format(truncated / 10d) + suffix : simpleNum.format(truncated / 10) + suffix;
    }

    @Override
    public String getName() {
        return "ner";
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Commons.getFastDateFormat();
        var metadata = new Metadata(this.getName(), "How many rats you have?", """
            *M:tG Command*
            Returns an amount of 1/1 black Rat creature tokens after X triggers created by the interaction between [Marrow-Gnawer](%s) equipped with [Thornbite Staff](%s).
            """.formatted("https://scryfall.com/card/chk/124/marrow-gnawer", "https://scryfall.com/card/mor/145/thornbite-staff"),
            Mastermind.DEVELOPER,
            SlashModule.MTG,
            format.parse("5-10-2022_11:48"),
            format.parse("30-11-2022_13:43")
        );

        metadata.addOptions(
            new OptionData(OptionType.INTEGER, "start-count", "How many do you have right now?", true)
                .setMinValue(3),
            new OptionData(OptionType.INTEGER, "iterations", "How many iterations?", true)
                .setMinValue(1)
        );

        return metadata;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        event.deferReply().queue();
        float startCNT = event.getOption("start-count", 3, OptionMapping::getAsInt);
        float iterations = event.getOption("iterations", 100, OptionMapping::getAsInt);
        float finalCNT = startCNT;
        DecimalFormat largeNumber = new DecimalFormat("##,###");
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(Commons.getDefaultEmbedColor())
            .setTitle("Is there enough rats?")
            .addField("Starting total", "%s rats".formatted(largeNumber.format(startCNT)), true)
            .addField("Iterations", "%s triggers".formatted(largeNumber.format(iterations)), true)
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl());

        for (float i = 0; i < iterations; i++) {
            finalCNT = (finalCNT - 1f) * 2f;
        }

        if (Float.isInfinite(finalCNT)) {
            eb.setDescription("Yes.")
                .addField("Final Count", "INFINITE", false);
        } else eb.setDescription("No.")
            .addField("Final Count (Short-Hand)", ("%s rats".formatted(largeNumberFormat(finalCNT)).replace(".0 ", " ")), false)
            .addField("Final Count (Full Number)", "%s rats".formatted(largeNumber.format(finalCNT)), false);

        event.getHook().sendMessageEmbeds(eb.build()).queue();
    }
}
