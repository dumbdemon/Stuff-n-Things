package com.terransky.StuffnThings.commandSystem.commands;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.Metadata.Mastermind;
import com.terransky.StuffnThings.commandSystem.Metadata.Metadata;
import com.terransky.StuffnThings.exceptions.DiscordAPIException;
import com.terransky.StuffnThings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class colorInfo implements ISlashCommand {
    private final String HEX_TRIPLET_PATTERN = "^#(([0-9a-fA-F]{2}){3}|([0-9a-fA-F]){3})$";
    private final Pattern pHexTriplet = Pattern.compile(HEX_TRIPLET_PATTERN);

    @Contract(pure = true)
    private static int @NotNull [] cmykToRgb(int c, int m, int y, int k) {
        double r = 255 * (1 - (double) c / 100) * (1 - (double) k / 100);
        double g = 255 * (1 - (double) m / 100) * (1 - (double) k / 100);
        double b = 255 * (1 - (double) y / 100) * (1 - (double) k / 100);

        return new int[]{(int) r, (int) g, (int) b};
    }

    @Contract(pure = true)
    private static int @NotNull [] rgbToCmyk(int r, int g, int b) {
        double percentageR = r / 255.0 * 100;
        double percentageG = g / 255.0 * 100;
        double percentageB = b / 255.0 * 100;

        double k = 100 - Math.max(Math.max(percentageR, percentageG), percentageB);

        if (k == 100) {
            return new int[]{0, 0, 0, 100};
        }

        int c = (int) ((100 - percentageR - k) / (100 - k) * 100);
        int m = (int) ((100 - percentageG - k) / (100 - k) * 100);
        int y = (int) ((100 - percentageB - k) / (100 - k) * 100);

        return new int[]{c, m, y, (int) Math.floor(k)};
    }

    @Override
    public String getName() {
        return "color-info";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(this.getName(), """
            Given a hex triplet, RGB, or CMYK code, it will return the other values and give a link to more info.
            """, Mastermind.DEVELOPER);
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(this.getName(), "Get more info on a color.")
            .addSubcommands(
                new SubcommandData("hex-triplet", "Get more info on a hex triplet. EX: #663366")
                    .addOptions(
                        new OptionData(OptionType.STRING, "triplet", "Enter the Hex Triplet including the \"#\".", true)
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

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) throws Exception {
        DecimalFormat hsb = new DecimalFormat("##.##%");
        String subCommand = event.getSubcommandName();
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle(WordUtils.capitalize(this.getName().replace("-", "\s")))
            .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl());
        if (subCommand == null) throw new DiscordAPIException("No subcommand was given.");

        switch (subCommand) {
            case "hex-triplet" -> {
                String hexCode = event.getOption("triplet", "#636", OptionMapping::getAsString);
                Matcher matcher = pHexTriplet.matcher(hexCode);

                if (matcher.matches()) {
                    if (hexCode.length() == 4) {
                        String R = String.valueOf(hexCode.charAt(1));
                        String G = String.valueOf(hexCode.charAt(2));
                        String B = String.valueOf(hexCode.charAt(3));
                        hexCode = "#" + R + R + G + G + B + B;
                    }

                    Color color = Color.decode(hexCode);
                    int r = color.getRed(),
                        g = color.getGreen(),
                        b = color.getBlue();
                    int[] cmyk = rgbToCmyk(r, g, b);
                    int c = cmyk[0],
                        m = cmyk[1],
                        y = cmyk[2],
                        k = cmyk[3];
                    float[] hsv = Color.RGBtoHSB(r, g, b, null);
                    String h = hsb.format(hsv[0]).replace("%", "\u00B0"),
                        s = hsb.format(hsv[1]),
                        v = hsb.format(hsv[2]);

                    eb.addField("Hex Triplet", hexCode, true)
                        .addField("RGB", "rgb(**%d**, **%d**, **%d**)".formatted(r, g, b), true)
                        .addField("CMYK", "**%d**, **%d**, **%d**, **%d**".formatted(c, m, y, k), true)
                        .addField("HSB/HSV", "Hue **%s**\nSaturation **%s**\nBrightness **%s**".formatted(h, s, v), false)
                        .addField("More Info", "[link](https://www.colorhexa.com/%s)".formatted(hexCode.substring(1)), false)
                        .setColor(color);
                    event.replyEmbeds(eb.build()).queue();
                } else {
                    eb.setTitle("Invalid Hex Triplet!")
                        .setDescription("You've given an invalid hex triplet!\nCorrect example: `#663366` or `#636`")
                        .setColor(Commons.DEFAULT_EMBED_COLOR)
                        .addField("Provided", hexCode, false);
                    event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                }
            }

            case "rgb" -> {
                int r = event.getOption("red", 102, OptionMapping::getAsInt),
                    g = event.getOption("blue", 51, OptionMapping::getAsInt),
                    b = event.getOption("green", 102, OptionMapping::getAsInt);
                int[] cmyk = rgbToCmyk(r, g, b);
                int c = cmyk[0],
                    m = cmyk[1],
                    y = cmyk[2],
                    k = cmyk[3];
                float[] hsv = Color.RGBtoHSB(r, g, b, null);
                String h = hsb.format(hsv[0]).replace("%", "\u00B0"),
                    s = hsb.format(hsv[1]),
                    v = hsb.format(hsv[2]);
                String hexTriplet = "#%02X%02X%02X".formatted(r, g, b);

                eb.addField("Hex Triplet", hexTriplet, true)
                    .addField("RGB", "rgb(**%d**, **%d**, **%d**)".formatted(r, g, b), true)
                    .addField("CMYK", "**%d**, **%d**, **%d**, **%d**".formatted(c, m, y, k), true)
                    .addField("HSB/HSV", "Hue **%s**\nSaturation **%s**\nBrightness **%s**".formatted(h, s, v), false)
                    .addField("More Info", "[link](https://www.colorhexa.com/%s)".formatted(hexTriplet), false)
                    .setColor(new Color(r, g, b));
                event.replyEmbeds(eb.build()).queue();
            }

            case "cmyk" -> {
                int c = event.getOption("cyan", 0, OptionMapping::getAsInt),
                    m = event.getOption("magenta", 50, OptionMapping::getAsInt),
                    y = event.getOption("yellow", 0, OptionMapping::getAsInt),
                    k = event.getOption("black", 60, OptionMapping::getAsInt);
                int[] rgb = cmykToRgb(c, m, y, k);
                int r = rgb[0],
                    g = rgb[1],
                    b = rgb[2];
                float[] hsv = Color.RGBtoHSB(r, g, b, null);
                String h = hsb.format(hsv[0]).replace("%", "\u00B0"),
                    s = hsb.format(hsv[1]),
                    v = hsb.format(hsv[2]);
                String hexTriplet = "#%02X%02X%02X".formatted(r, g, b);

                eb.addField("Hex Triplet", hexTriplet, true)
                    .addField("RGB", "rgb(**%d**, **%d**, **%d**)".formatted(r, g, b), true)
                    .addField("CMYK", "**%d**, **%d**, **%d**, **%d**".formatted(c, m, y, k), true)
                    .addField("HSB/HSV", "Hue **%s**\nSaturation **%s**\nBrightness **%s**".formatted(h, s, v), false)
                    .addField("More Info", "[link](https://www.colorhexa.com/%s)".formatted(hexTriplet), false)
                    .setColor(new Color(r, g, b));
                event.replyEmbeds(eb.build()).queue();
            }
        }
    }
}
