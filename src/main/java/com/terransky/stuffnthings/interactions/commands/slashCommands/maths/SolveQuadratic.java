package com.terransky.stuffnthings.interactions.commands.slashCommands.maths;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.StandardResponse;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SolveQuadratic extends SlashCommandInteraction {

    public SolveQuadratic() {
        super("solve-quadratic", "Solve a Quadratic Equation.", Mastermind.DEVELOPER, CommandCategory.MATHS,
            parseDate(2022, 11, 19, 13, 9),
            parseDate(2025, 12, 27, 5, 4)
        );
        addOptions(
            new OptionData(OptionType.NUMBER, "value-a", "A value of Quadratic Formula"),
            new OptionData(OptionType.NUMBER, "value-b", "B value of Quadratic Formula"),
            new OptionData(OptionType.NUMBER, "value-c", "C value of Quadratic Formula")
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        String title = WordUtils.capitalize(getName().replace("-", " "));
        DecimalFormat prettyNum = new DecimalFormat("#.##");
        MediaGallery quadraticFormulaImage =
            MediaGallery.of(MediaGalleryItem.fromUrl("https://www.inchcalculator.com/wp-content/uploads/2022/11/quadratic-formula.png"));

        double a = event.getOption("value-a", 1.0, OptionMapping::getAsDouble);
        double b = event.getOption("value-b", 1.0, OptionMapping::getAsDouble);
        double c = event.getOption("value-c", 0.0, OptionMapping::getAsDouble);
        double d = b * b - 4.0 * a * c;
        double r1,
            r2;
        if (d > 0.0) {
            r1 = (-b + Math.sqrt(d)) / (2.0 * a);
            r2 = (-b - Math.sqrt(d)) / (2.0 * a);

            event.replyComponents(StandardResponse.getResponseContainer(title, List.of(TextDisplay.of("""
                        Given:
                        ```ini
                        a = %s
                        b = %s
                        c = %s
                        ```
                        Answer:
                        ```ini
                        x = [%s, %s]
                        ```
                        """.formatted(prettyNum.format(a), prettyNum.format(b), prettyNum.format(c), prettyNum.format(r1), prettyNum.format(r2))),
                    Separator.createDivider(Separator.Spacing.SMALL),
                    quadraticFormulaImage
                ))
            ).queue();
            return;
        }

        if (d == 0.0) {
            r1 = -b / (2.0 * a);

            event.replyComponents(StandardResponse.getResponseContainer(title, List.of(TextDisplay.of("""
                        Given:
                        ```ini
                        a = %s
                        b = %s
                        c = %s
                        ```
                        Answer:
                        ```ini
                        x = [%s]
                        ```
                        """.formatted(prettyNum.format(a), prettyNum.format(b), prettyNum.format(c), prettyNum.format(r1))),
                    Separator.createDivider(Separator.Spacing.SMALL),
                    quadraticFormulaImage
                ))
            ).queue();
            return;
        }

        event.replyComponents(StandardResponse.getResponseContainer(title, List.of(TextDisplay.of("""
                    Given:
                    ```ini
                    a = %s
                    b = %s
                    c = %s
                    ```
                    Answer:
                    ```ini
                    x = ["Roots are not real."]
                    ```
                    """.formatted(prettyNum.format(a), prettyNum.format(b), prettyNum.format(c))),
                Separator.createDivider(Separator.Spacing.SMALL),
                quadraticFormulaImage
            ))
        ).queue();
    }
}
