package com.terransky.StuffnThings.commandSystem.commands.mtg;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class calculateRats implements ISlashCommand {
    @Override
    public String getName() {
        return "ner";
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(this.getName(), "How many rats you have?")
            .addOptions(
                new OptionData(OptionType.INTEGER, "start-count", "How many do you have right now?", true)
                    .setRequiredRange(3, Integer.MAX_VALUE),
                new OptionData(OptionType.INTEGER, "iterations", "How many iterations?", true)
                    .setRequiredRange(1, Integer.MAX_VALUE)
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) throws Exception {
        event.deferReply().queue();
        float startCNT = event.getOption("start-count", 3, OptionMapping::getAsInt);
        float iterations = event.getOption("iterations", 100, OptionMapping::getAsInt);
        float finalCNT = startCNT;
        DecimalFormat largeNumber = new DecimalFormat("##,###");
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(Commons.defaultEmbedColor)
            .setTitle("Is there enough rats?")
            .addField("Starting total", "%s rats".formatted(largeNumber.format(startCNT)), true)
            .addField("Iterations", "%s triggers".formatted(largeNumber.format(iterations)), true)
            .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl());

        for (float i = 0; i < iterations; i++) {
            finalCNT = (finalCNT - 1f) * 2f;
        }

        if (Float.isInfinite(finalCNT)) {
            eb.setDescription("Yes.")
                .addField("Final Count", "INFINITE", false);
        } else eb.setDescription("No.")
            .addField("Final Count (Short-Hand)", "%s rats".formatted(Commons.largeNumberFormat(finalCNT).replace(".0\s", "\s")), false)
            .addField("Final Count (Full Number)", "%s rats".formatted(largeNumber.format(finalCNT)), false);

        event.getHook().sendMessageEmbeds(eb.build()).queue();
    }
}
