package com.terransky.stuffnthings.interactions.commands.slashCommands.mtg;

import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.ParseException;

public class calculateRats implements ICommandSlash {

    @Override
    public String getName() {
        return "not-enough-rats";
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        return new Metadata(this.getName(), "How many rats you have?", """
            Returns an amount of 1/1 black Rat creature tokens after X triggers created by the interaction between [Marrow-Gnawer](%s) equipped with [Thornbite Staff](%s).
            """.formatted("https://scryfall.com/card/chk/124/marrow-gnawer", "https://scryfall.com/card/mor/145/thornbite-staff"),
            Mastermind.DEVELOPER,
            CommandCategory.MTG,
            format.parse("5-10-2022_11:48"),
            format.parse("29-12-2022_10:14")
        )
            .addOptions(
                new OptionData(OptionType.INTEGER, "start-count", "How many do you have right now?", true)
                    .setMinValue(3),
                new OptionData(OptionType.INTEGER, "triggers", "How many triggers?", true)
                    .setMinValue(1)
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        event.deferReply().queue();
        float startCNT = event.getOption("start-count", 3, OptionMapping::getAsInt);
        float triggers = event.getOption("triggers", 100, OptionMapping::getAsInt);
        float finalCNT = startCNT;
        DecimalFormat largeNumber = new DecimalFormat("##,###");
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setTitle("Is there enough rats?")
            .addField("Starting total", "%s rats".formatted(largeNumber.format(startCNT)), true)
            .addField("Iterations", "%s triggers".formatted(largeNumber.format(triggers)), true)
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl());

        for (float i = 0; i < triggers; i++) {
            finalCNT = (finalCNT - 1f) * 2f;
        }

        if (Float.isInfinite(finalCNT)) {
            eb.setDescription("Yes.")
                .addField("Final Count", "INFINITE", false);
        } else eb.setDescription("No.")
            .addField("Final Count (Short-Hand)", ("%s rats".formatted(Formatter.largeNumberFormat(finalCNT)).replace(".0 ", " ")), false)
            .addField("Final Count (Full Number)", "%s rats".formatted(largeNumber.format(finalCNT)), false);

        event.getHook().sendMessageEmbeds(eb.build()).queue();
    }
}
