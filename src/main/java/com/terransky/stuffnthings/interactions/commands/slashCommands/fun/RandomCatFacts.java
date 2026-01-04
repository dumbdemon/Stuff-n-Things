package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.apiHandlers.RandomCatFactsHandler;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.StandardResponse;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class RandomCatFacts extends SlashCommandInteraction {
    public RandomCatFacts() {
        super("random-cat-facts", "Get a random fact about cats!",
            Mastermind.DEVELOPER, CommandCategory.FUN,
            parseDate(2026, 1, 4, 1, 4),
            parseDate(2026, 1, 4, 1, 4)
        );
        addOptions(
            new OptionData(OptionType.INTEGER, "count", "How many facts? Defaults to one.")
                .setRequiredRange(1, 10)
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        event.deferReply().queue();
        RandomCatFactsHandler handler = new RandomCatFactsHandler();
        int count = event.getOption("count", 1, OptionMapping::getAsInt);

        List<ContainerChildComponent> children = new ArrayList<>();
        List<String> randomCatFacts = handler.getRandomCatFacts(count);

        for (String randomCatFact : randomCatFacts) {
            children.add(TextDisplay.ofFormat("### %s\n> %s", randomCatFacts.indexOf(randomCatFact) + 1, randomCatFact));
            if (!Objects.equals(randomCatFact, randomCatFacts.get(randomCatFacts.size() - 1))) {
                children.add(Separator.createDivider(Separator.Spacing.SMALL));
            }
        }

        event.getHook().sendMessageComponents(StandardResponse.getResponseContainer(this, children)).queue();
    }
}
