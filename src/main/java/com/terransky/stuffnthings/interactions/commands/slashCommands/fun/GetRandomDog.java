package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.randomDog.RandomDog;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.Metadata;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;

public class GetRandomDog implements ICommandSlash {
    @Override
    public String getName() {
        return "random-dog";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(getName(), "Random Dogs! Go!",
            Mastermind.DEVELOPER, CommandCategory.FUN,
            Metadata.parseDate(2023, 2, 6, 17, 34),
            Metadata.parseDate(2024, 2, 9, 16, 11)
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        RandomDog randomDog = new ObjectMapper().readValue(new URL("https://random.dog/woof.json"), RandomDog.class);

        event.reply(randomDog.getUrl()).queue();
    }
}
