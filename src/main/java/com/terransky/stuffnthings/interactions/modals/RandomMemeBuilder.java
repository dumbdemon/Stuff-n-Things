package com.terransky.stuffnthings.interactions.modals;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ModalInteraction;
import com.terransky.stuffnthings.utilities.apiHandlers.MemeGeneratorHandler;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class RandomMemeBuilder extends ModalInteraction {

    private final String TOP_TEXT = "top-text";
    private final String BOTTOM_TEXT = "bottom-text";

    public RandomMemeBuilder() {
        super("random-meme-builder", "Random Meme Builder");
    }

    //TODO: Research new Modal system
    @Override
    public Modal getContructedModal() {
        return null;
    }

    @Override
    public void execute(@NotNull ModalInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        String topText = Objects.requireNonNull(event.getValue(TOP_TEXT)).getAsString();
        String bottomText = Objects.requireNonNull(event.getValue(BOTTOM_TEXT)).getAsString();
        MemeGeneratorHandler handler = new MemeGeneratorHandler();

        try (FileUpload fileUpload = handler.generateMeme(handler.getRandomMeme(blob.getMemberIdLong() | new Date().getTime()), topText, bottomText)) {
            event.replyFiles(fileUpload).queue();
        }
    }

//    @Override
//    public Modal getConstructedModal() {
//        TextInput topText = TextInput.create(TOP_TEXT, "Top Text", TextInputStyle.SHORT)
//            .setRequired(true)
//            .setRequiredRange(1, 100)
//            .setPlaceholder("Top Text")
//            .build();
//        TextInput bottomText = TextInput.create(BOTTOM_TEXT, "Bottom Text", TextInputStyle.SHORT)
//            .setRequired(true)
//            .setRequiredRange(1, 100)
//            .setPlaceholder("Bottom Text")
//            .build();
//
//        return Modal.create(getName(), "Random Meme Builder")
//            .addActionRow(topText)
//            .addActionRow(bottomText)
//            .build();
//    }
}
