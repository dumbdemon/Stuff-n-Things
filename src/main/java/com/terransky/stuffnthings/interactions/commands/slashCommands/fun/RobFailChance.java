package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.Metadata;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;

@SuppressWarnings("SpellCheckingInspection")
public class RobFailChance implements ICommandSlash {

    private final String uBoatInvite = "https://discord.com/oauth2/authorize?client_id=292953664492929025&scope=bot%20applications.commands&permissions=829811966&response_type=code&redirect_uri=https://unbelievaboat.com/landing";

    @Override
    public String getName() {
        return "rob-fail-chance";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(this.getName(), "Calculate the fail chance to rob a member for the UnbelievaBoat bot!", """
            Returns the chance of failure of the `/rob` command of the bot UnbelievaBoat. If you don't have the bot, you can ask your admins to invite it [here](%s).
            """.formatted(uBoatInvite), Mastermind.DEVELOPER,
            CommandCategory.FUN,
            Metadata.parseDate(2022, 8, 24, 11, 10),
            Metadata.parseDate(2024, 2, 9, 16, 11)
        )
            .addOptions(
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

        EmbedBuilder eb = blob.getStandardEmbed("Your chance to fail is... " + failChance)
            .addField("Your Net-Worth", largeNumber.format(yourNetWorth), true)
            .addField("Their Cash", largeNumber.format(theirCash), true)
            .addField("Failure Chance", failChance, true);

        if (blob.getGuild().getRoleByBot(356950275044671499L) != null || blob.getGuild().getRoleByBot(292953664492929025L) != null) {
            event.replyEmbeds(eb.build()).queue();
        } else {
            eb.setDescription("**UnbelievaBoat is not on this sever!** " +
                "To get the **maximum** value out of this command, ask your admins to invite the bot [here](%s).".formatted(uBoatInvite));
            event.replyEmbeds(eb.build()).queue();
        }
    }
}
