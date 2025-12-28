package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RobFailChance extends SlashCommandInteraction {

    public RobFailChance() {
        super("rob-fail-chance", "Calculate the fail chance to rob a member for the UnbelievaBoat bot!",
            Mastermind.DEVELOPER,
            CommandCategory.FUN,
            parseDate(2022, 8, 24, 11, 10),
            parseDate(2024, 2, 9, 16, 11)
        );
        addOptions(
            new OptionData(OptionType.INTEGER, "your-net-worth", "Your net-worth.", true)
                .setMaxValue(1),
            new OptionData(OptionType.INTEGER, "their-cash", "The amount of cash for the person you are trying to rob", true)
                .setMinValue(1)
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        DecimalFormat largeNumber = new DecimalFormat("##,###");

        double yourNetWorth = event.getOption("your-net-worth", 0d, OptionMapping::getAsDouble),
            theirCash = event.getOption("their-cash", 0d, OptionMapping::getAsDouble);
        String failChance = String.format("%.2f%%", (yourNetWorth / (theirCash + yourNetWorth)) * 100);

        List<ContainerChildComponent> children = new ArrayList<>();
        String title = "Your chance to fail is... " + failChance;

        children.add(TextDisplay.ofFormat("## Your Net-Worth\n%s", largeNumber.format(yourNetWorth)));
        children.add(TextDisplay.ofFormat("## Their Cash\n%s", largeNumber.format(theirCash)));
        children.add(TextDisplay.ofFormat("## Failure Chance\n%s", failChance));

        if (blob.getGuild().getRoleByBot(356950275044671499L) != null || blob.getGuild().getRoleByBot(292953664492929025L) != null) {
            event.replyComponents(StandardResponse.getResponseContainer(title, children)).queue();
        } else {
            children.add(Formatter.getLinkButtonSection(
                "https://discord.com/oauth2/authorize?client_id=292953664492929025&scope=bot%20applications.commands&permissions=829811966&response_type=code&redirect_uri=https://unbelievaboat.com/landing",
                "**UnbelievaBoat is not on this sever!** " +
                    "To get the **maximum** value out of this command, ask your admins to invite the bot.",
                Emoji.fromUnicode("✉️")
            ));
            event.replyComponents(StandardResponse.getResponseContainer(title, children)).queue();
        }
    }
}
