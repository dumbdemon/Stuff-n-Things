package com.terransky.stuffnthings.interactions.modals;

import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ModalInteraction;
import com.terransky.stuffnthings.utilities.command.BotColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.StandardResponse;
import com.terransky.stuffnthings.utilities.jda.DiscordWebhook;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class SuggestCommand extends ModalInteraction {

    private final String SUGGESTION = "suggestion";
    private final String IMPORTANCE = "importance";
    private final String ANONYMOUS = "anonymous";

    public SuggestCommand() {
        super("suggest-command", "Suggest Command");
    }

    @Override
    public Modal getContructedModal() {
        SelectOption sendUsername = SelectOption.of("Yes", "yes");
        return getBuilder()
            .addComponents(
                Label.of("Suggestion",
                    TextInput.create(SUGGESTION, TextInputStyle.PARAGRAPH)
                        .setRequired(true)
                        .setRequiredRange(3, 1000)
                        .setPlaceholder("Dad jokes")
                        .build()
                ),
                Label.of("Importance", "Please input a number between 1 and 100.",
                    TextInput.create(IMPORTANCE, TextInputStyle.SHORT)
                        .setRequired(true)
                        .setRequiredRange(1, 3)
                        .setPlaceholder("50")
                        .build()
                ),
                Label.of("Send Username", "Whether this form sends your username or not.",
                    StringSelectMenu.create(ANONYMOUS)
                        .addOptions(
                            sendUsername,
                            SelectOption.of("No", "no")
                        )
                        .setDefaultOptions(sendUsername)
                        .build()
                )
            )
            .build();
    }

    @Override
    public void execute(@NotNull ModalInteractionEvent event, EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        Optional<ModalMapping> optionalSuggestion = Optional.ofNullable(event.getValue(SUGGESTION));
        Optional<ModalMapping> optionalImportance = Optional.ofNullable(event.getValue(IMPORTANCE));
        Optional<ModalMapping> optionalAnonymous = Optional.ofNullable(event.getValue(ANONYMOUS));

        String suggestion = optionalSuggestion.orElseThrow(DiscordAPIException::new).getAsString();
        String importanceString = optionalImportance.orElseThrow(DiscordAPIException::new).getAsString();
        boolean anonymous = optionalAnonymous.orElseThrow(DiscordAPIException::new).getAsStringList().get(0).equals("no");
        int importance;

        try {
            importance = Integer.parseInt(importanceString);
        } catch (NumberFormatException e) {
            event.replyComponents(StandardResponse.getResponseContainer(getTitle(), "## Importance was not a number.", BotColors.ERROR))
                .setEphemeral(true)
                .queue();
            return;
        }

        EmbedBuilder callReply = new EmbedBuilder().setColor(BotColors.DEFAULT.getColor());
        String description = "```\n" + suggestion + "\n```";

        new DiscordWebhook("Suggestion")
            .sendMessage(new EmbedBuilder(callReply)
                .setTitle("Command Suggestion")
                .setDescription(description)
                .addField("Importance Value", "[" + importance + "/100]", false)
                .addField("From", "@" + (anonymous ? "<anonymous>" : blob.getMemberName()), false)
                .build()
            );

        callReply.setTitle("Your suggestion was sent successfully!")
            .setDescription(description)
            .addField("Importance Value", "[" + importance + "/100]", false);

        event.replyComponents(
            StandardResponse.getResponseContainer("Your suggestion was sent successfully!",
                List.of(
                    TextDisplay.of(description),
                    TextDisplay.of(String.format("Importance Value - %s/100", importance))
                )
            )
        ).setEphemeral(true).queue();
    }
}
