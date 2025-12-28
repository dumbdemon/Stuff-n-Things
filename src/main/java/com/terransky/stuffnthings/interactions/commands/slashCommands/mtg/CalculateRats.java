package com.terransky.stuffnthings.interactions.commands.slashCommands.mtg;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CalculateRats extends SlashCommandInteraction {

    public CalculateRats() {
        super("not-enough-rats", "How many rats you have?", Mastermind.DEVELOPER, CommandCategory.MTG,
            parseDate(2022, 10, 5, 11, 48),
            parseDate(2025, 12, 27, 2, 26)
        );
        addOptions(
            new OptionData(OptionType.INTEGER, "start-count", "How many do you have right now?", true)
                .setMinValue(3),
            new OptionData(OptionType.INTEGER, "triggers", "How many triggers?", true)
                .setMinValue(1)
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        event.deferReply().queue();
        float startCNT = event.getOption("start-count", 3, OptionMapping::getAsInt);
        float triggers = event.getOption("triggers", 100, OptionMapping::getAsInt);
        float finalCNT = startCNT;
        DecimalFormat largeNumber = new DecimalFormat("##,###");
        List<ContainerChildComponent> children = new ArrayList<>();
        List<ContainerChildComponent> initialValues = new ArrayList<>() {{
            add(Separator.createDivider(Separator.Spacing.SMALL));
            add(TextDisplay.ofFormat("Starting total - %s rats", largeNumber.format(startCNT)));
            add(TextDisplay.ofFormat("Iterations - %s triggers", largeNumber.format(triggers)));
            add(Separator.createDivider(Separator.Spacing.SMALL));
        }};

        for (float i = 0; i < triggers; i++) {
            finalCNT = (finalCNT - 1f) * 2f;
        }

        if (Float.isInfinite(finalCNT)) {
            children.add(TextDisplay.of("Yes."));
            children.addAll(initialValues);
            children.add(TextDisplay.of("Final Count - INFINITE"));
        } else {
            children.add(TextDisplay.of("No."));
            children.addAll(initialValues);
            children.add(TextDisplay.of("Final Count (Short-Hand) - " + ("%s rats".formatted(Formatter.largeNumberFormat(finalCNT)).replace(".0 ", " "))));
            children.add(TextDisplay.of("Final Count (Full Number) - %s rats".formatted(largeNumber.format(finalCNT))));
        }

        event.getHook().sendMessageComponents(StandardResponse.getResponseContainer("Is there enough rats?", children)).queue();
    }
}
