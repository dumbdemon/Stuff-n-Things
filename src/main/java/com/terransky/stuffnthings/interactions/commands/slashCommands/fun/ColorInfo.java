package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorInfo extends SlashCommandInteraction {
    private static final DecimalFormat HSB = new DecimalFormat("##.##%");
    private final String HEX_TRIPLET_REGEX = "^#?(([0-9a-fA-F]{2}){3}|([0-9a-fA-F]){3})$";
    private final Pattern HEX_TRIPLET_PATTERN = Pattern.compile(HEX_TRIPLET_REGEX);

    public ColorInfo() {
        super("color-info", "Get more info on a color.", Mastermind.DEVELOPER,
            CommandCategory.FUN,
            parseDate(2022, 9, 20, 12, 10),
            parseDate(2024, 2, 9, 16, 11)
        );
        addSubcommands(
            new SubcommandData("hex-triplet", "Get more info on a hex triplet. EX: #663366")
                .addOptions(
                    new OptionData(OptionType.STRING, "triplet", "Enter a Hex Triplet.", true)
                        .setRequiredLength(3, 7)
                ),
            new SubcommandData("rgb", "Get more info on an RGB code. EX: R 102, G 51, B 102")
                .addOptions(
                    new OptionData(OptionType.INTEGER, "red", "The red value.", true)
                        .setRequiredRange(0, 255),
                    new OptionData(OptionType.INTEGER, "blue", "The blue value.", true)
                        .setRequiredRange(0, 255),
                    new OptionData(OptionType.INTEGER, "green", "The green value", true)
                        .setRequiredRange(0, 255)
                ),
            new SubcommandData("cmyk", "Get more info on a CMYK code. EX: C 0, M 50, Y 0, K 60")
                .addOptions(
                    new OptionData(OptionType.INTEGER, "cyan", "The cyan percentage.", true)
                        .setRequiredRange(0, 100),
                    new OptionData(OptionType.INTEGER, "magenta", "The magenta percentage", true)
                        .setRequiredRange(0, 100),
                    new OptionData(OptionType.INTEGER, "yellow", "The yellow percentage", true)
                        .setRequiredRange(0, 100),
                    new OptionData(OptionType.INTEGER, "black", "The black percentage", true)
                        .setRequiredRange(0, 100)
                )
        );
    }

    @NotNull
    @Contract(pure = true)
    private static int[] cmykToRgb(@NotNull int[] cmyk) {
        double r = 255 * (1 - (double) cmyk[0] / 100) * (1 - (double) cmyk[3] / 100);
        double g = 255 * (1 - (double) cmyk[1] / 100) * (1 - (double) cmyk[3] / 100);
        double b = 255 * (1 - (double) cmyk[2] / 100) * (1 - (double) cmyk[3] / 100);

        return new int[]{(int) r, (int) g, (int) b};
    }

    @NotNull
    @Contract(pure = true)
    private static int[] rgbToCmyk(@NotNull int[] rgb) {
        double percentageR = rgb[0] / 255.0 * 100;
        double percentageG = rgb[1] / 255.0 * 100;
        double percentageB = rgb[2] / 255.0 * 100;

        double k = 100 - Math.max(Math.max(percentageR, percentageG), percentageB);

        if (k == 100) {
            return new int[]{0, 0, 0, 100};
        }

        int c = (int) ((100 - percentageR - k) / (100 - k) * 100);
        int m = (int) ((100 - percentageG - k) / (100 - k) * 100);
        int y = (int) ((100 - percentageB - k) / (100 - k) * 100);

        return new int[]{c, m, y, (int) Math.floor(k)};
    }

    private void runHexTriplet(@NotNull SlashCommandInteractionEvent event) {
        String hexCode = event.getOption("triplet", "#636", OptionMapping::getAsString);
        Matcher matcher = HEX_TRIPLET_PATTERN.matcher(hexCode);

        if (!matcher.matches()) {
            DecimalFormat bigNumber = new DecimalFormat("##,###");
            event.replyComponents(
                StandardResponse.getResponseContainer("Invalid Hex Triplet!", List.of(
                    TextDisplay.ofFormat("""
                        You've given an invalid hex triplet!
                        Correct example: `#663366` or `#636`
                        
                        **NOTE:** Hex triplets are 3 or 6 characters that uses 0-9 and A-F in various combinations. On another note, `#0055FF` is equivalent to `#05F`.
                        
                        FUN FACT!
                        > There are %s combinations for 6 character hex triplets and of those combinations, %s are for 3 character hex triplets!
                        """, bigNumber.format(Math.pow(16, 6)), bigNumber.format(Math.pow(16, 3))),
                    TextDisplay.ofFormat("Provided: `%s`", hexCode.toUpperCase())
                ), BotColors.ERROR)
            ).setEphemeral(true).queue();
            return;
        }

        Color color;
        switch (hexCode.length()) {
            case 3, 4 -> {
                int offset = 0;
                if (hexCode.charAt(0) == '#') offset++;
                String R = String.valueOf(hexCode.charAt(offset));
                String G = String.valueOf(hexCode.charAt(1 + offset));
                String B = String.valueOf(hexCode.charAt(2 + offset));
                color = Color.decode("#" + R + R + G + G + B + B);
            }
            case 6 -> color = Color.decode('#' + hexCode);
            default -> color = Color.decode(hexCode);
        }

        int[] rgb = new int[]{
            color.getRed(),
            color.getGreen(),
            color.getBlue()
        };

        event.replyComponents(getResponse(rgbToCmyk(rgb), rgb)).queue();
    }

    private void runRGB(@NotNull SlashCommandInteractionEvent event) {
        int[] rgb = {
            event.getOption("red", 102, OptionMapping::getAsInt),
            event.getOption("blue", 51, OptionMapping::getAsInt),
            event.getOption("green", 102, OptionMapping::getAsInt)
        };
        event.replyComponents(getResponse(rgbToCmyk(rgb), rgb)).queue();
    }

    private void runCMYK(@NotNull SlashCommandInteractionEvent event) {
        int[] cmyk = {
            event.getOption("cyan", 0, OptionMapping::getAsInt),
            event.getOption("magenta", 50, OptionMapping::getAsInt),
            event.getOption("yellow", 0, OptionMapping::getAsInt),
            event.getOption("black", 60, OptionMapping::getAsInt)
        };
        event.replyComponents(getResponse(cmyk, cmykToRgb(cmyk))).queue();
    }

    @NotNull
    private Container getResponse(@NotNull int[] cmyk, @NotNull int[] rgb) {
        int r = rgb[0];
        int g = rgb[1];
        int b = rgb[2];
        float[] hsv = Color.RGBtoHSB(r, g, b, null);
        String hexTriplet = "#%02X%02X%02X".formatted(r, g, b);

        return StandardResponse.getResponseContainer(this, List.of(
            TextDisplay.ofFormat("## Hex Triplet\n%s", hexTriplet),
            TextDisplay.ofFormat("## RGB\nrgb(**%d**, **%d**, **%d**)", r, g, b),
            TextDisplay.ofFormat("## CMYK\n**%d**, **%d**, **%d**, **%d**", cmyk[0], cmyk[1], cmyk[2], cmyk[3]),
            TextDisplay.ofFormat("## HSB/HSV\nHue **%s**\nSaturation **%s**\nBrightness **%s**",
                HSB.format(hsv[0]).replace("%", "°"),
                HSB.format(hsv[1]),
                HSB.format(hsv[2])
            ),
            Separator.createDivider(Separator.Spacing.SMALL),
            Formatter.getLinkButtonSection(String.format("https://www.colorhexa.com/%s", hexTriplet.substring(1)), "Learn more on ColorHexa")
        ), new Color(r, g, b));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");

        switch (subcommand) {
            case "hex-triplet" -> runHexTriplet(event);
            case "rgb" -> runRGB(event);
            case "cmyk" -> runCMYK(event);
        }
    }
}
