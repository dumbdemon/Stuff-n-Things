package com.terransky.StuffnThings.commandSystem.commands;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

@SuppressWarnings("SpellCheckingInspection")
public class robFailChance implements ISlashCommand {
    @Override
    public String getName() {
        return "rob-fail-chance";
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(this.getName(), "Calculate the fail chance to rob a member for the UnbelievaBoat bot!")
            .addOptions(
                new OptionData(OptionType.INTEGER, "your-net-worth", "Your net-worth.", true),
                new OptionData(OptionType.INTEGER, "their-cash", "The amount of cash for the person you are trying to rob", true)
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(Commons.DEFAULT_EMBED_COLOR);
        DecimalFormat largeNumber = new DecimalFormat("##,###");
        String uBoatInvite = "https://discord.com/oauth2/authorize?client_id=292953664492929025&scope=bot%20applications.commands&permissions=829811966&response_type=code&redirect_uri=https://unbelievaboat.com/landing";
        if (event.getGuild() == null) return;

        double yourNetWorth = event.getOption("your-net-worth", 0d, OptionMapping::getAsDouble),
            theirCash = event.getOption("their-cash", 0d, OptionMapping::getAsDouble);
        String failChance = String.format("%.2f", (yourNetWorth / (theirCash + yourNetWorth)) * 100) + "%";

        eb.setAuthor("Your chance to fail is...")
            .addField("Your Net-Worth", largeNumber.format(yourNetWorth), true)
            .addField("Their Cash", largeNumber.format(theirCash), true)
            .addField("Failure Chance", failChance, true);

        if (event.getGuild().getRoleByBot(356950275044671499L) != null || event.getGuild().getRoleByBot(292953664492929025L) != null) {
            event.replyEmbeds(eb.build()).queue();
        } else {
            eb.setDescription("**UnbelievaBoat is not on this sever!**\s" +
                "To get the **maximum** value out of this command, ask your admins to invite the bot [here](%s).".formatted(uBoatInvite));
            event.replyEmbeds(eb.build()).queue();
        }
    }
}
