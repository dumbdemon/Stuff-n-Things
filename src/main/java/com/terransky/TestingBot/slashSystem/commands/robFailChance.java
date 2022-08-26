package com.terransky.TestingBot.slashSystem.commands;

import com.terransky.TestingBot.Commons;
import com.terransky.TestingBot.slashSystem.ISlash;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class robFailChance implements ISlash {
    @Override
    public String getName() {
        return "rob-fail-chance";
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(this.getName(), "Calculate the fail chance to rob a member for the UnbelievaBoat bot!")
                .addOptions(
                        new OptionData(OptionType.INTEGER, "your-net-worth", "Your net-worth.", true),
                        new OptionData(OptionType.INTEGER, "their-cash", "The amount of cash for the person you are trying to rob", true)
                );
    }

    @SuppressWarnings({"ConstantConditions", "SpellCheckingInspection"})
    @Override
    public void slashExecute(@NotNull SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(new Commons().defaultEmbedColor);
        DecimalFormat largeNumber = new DecimalFormat("##,###");
        String uBoatInvite = "https://discord.com/oauth2/authorize?client_id=292953664492929025&scope=bot%20applications.commands&permissions=829811966&response_type=code&redirect_uri=https://unbelievaboat.com/landing";

        double yourNetWorth = event.getOption("your-net-worth", OptionMapping::getAsDouble),
                theirCash = event.getOption("their-cash", OptionMapping::getAsDouble),
                failChance = yourNetWorth / (theirCash + yourNetWorth);

        eb.setAuthor("Your chance to fail is...")
                .addField(new MessageEmbed.Field("Your Net-Worth", largeNumber.format(yourNetWorth), true))
                .addField(new MessageEmbed.Field("Their Cash", largeNumber.format(theirCash), true))
                .addField(new MessageEmbed.Field("Failure Chance", String.format("%.2f", failChance * 100) + "%", true));

        if (event.getGuild().getRoleByBot(356950275044671499L) != null || event.getGuild().getRoleByBot(292953664492929025L) != null) {
            event.replyEmbeds(eb.build()).queue();
        } else {
            eb.setDescription("**UnbelievaBoat is not on this sever!**\s" +
                    "To get the **maximum** value out of this command, ask your admins to invite the bot [here](%s).".formatted(uBoatInvite));
            event.replyEmbeds(eb.build()).queue();
        }
    }
}
