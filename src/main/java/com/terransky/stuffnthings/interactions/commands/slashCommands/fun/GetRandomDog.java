package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.randomDog.RandomDog;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.BotColors;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;

public class GetRandomDog extends SlashCommandInteraction {

    public GetRandomDog() {
        super("random-dog", "Random Dogs! Go!",
            Mastermind.DEVELOPER, CommandCategory.FUN,
            parseDate(2023, 2, 6, 17, 34),
            parseDate(2025, 12, 28, 0, 36)
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        RandomDog randomDog = new ObjectMapper().readValue(new URL("https://random.dog/woof.json").openStream(), RandomDog.class);

        event.replyComponents(
            Container.of(
                MediaGallery.of(
                    MediaGalleryItem.fromUrl(randomDog.getUrl())
                )
            ).withAccentColor(BotColors.DEFAULT.getColor())
        ).queue();
    }
}
