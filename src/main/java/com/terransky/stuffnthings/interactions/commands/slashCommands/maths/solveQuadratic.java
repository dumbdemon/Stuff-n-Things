package com.terransky.stuffnthings.interactions.commands.slashCommands.maths;

import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.ParseException;

public class solveQuadratic implements ICommandSlash {
    @Override
    public String getName() {
        return "solve-quadratic";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();

        return new Metadata(this.getName(), "Solve a Quadratic Equation.", """
            Given a, b, and c, solve for when the parabola intersects the x-axis.
            """, Mastermind.DEVELOPER,
            SlashModule.MATHS,
            format.parse("19-11-2022_13:09"),
            format.parse("21-12-2022_20:04")
        )
            .addOptions(
                new OptionData(OptionType.NUMBER, "value-a", "A value of Quadratic Formula"),
                new OptionData(OptionType.NUMBER, "value-b", "B value of Quadratic Formula"),
                new OptionData(OptionType.NUMBER, "value-c", "C value of Quadratic Formula")
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle(WordUtils.capitalize(getName().replace("-", " ")))
            .setColor(EmbedColors.getDefault());
        DecimalFormat prettyNum = new DecimalFormat("#.##");

        double a = event.getOption("value-a", 1d, OptionMapping::getAsDouble);
        double b = event.getOption("value-b", 1d, OptionMapping::getAsDouble);
        double c = event.getOption("value-c", 0d, OptionMapping::getAsDouble);
        double d = b * b - 4.0 * a * c;
        double r1, r2;
        if (d > 0.0) {
            r1 = (-b + Math.pow(d, 0.5)) / (2.0 * a);
            r2 = (-b - Math.pow(d, 0.5)) / (2.0 * a);

            event.replyEmbeds(eb.setDescription("""
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
                    """.formatted(prettyNum.format(a), prettyNum.format(b), prettyNum.format(c), prettyNum.format(r1), prettyNum.format(r2))
                ).build()
            ).queue();
            return;
        }

        if (d == 0.0) {
            r1 = -b / 2.0 * a;

            event.replyEmbeds(eb.setDescription("""
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
                    """.formatted(prettyNum.format(a), prettyNum.format(b), prettyNum.format(c), prettyNum.format(r1))
                ).build()
            ).queue();
            return;
        }

        event.replyEmbeds(eb.setDescription("""
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
                """.formatted(prettyNum.format(a), prettyNum.format(b), prettyNum.format(c)))
            .build()
        ).queue();
    }
}
