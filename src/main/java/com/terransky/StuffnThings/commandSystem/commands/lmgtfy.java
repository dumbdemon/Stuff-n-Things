package com.terransky.StuffnThings.commandSystem.commands;

import com.terransky.StuffnThings.commandSystem.ISlash;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class lmgtfy implements ISlash {
    @Override
    public String getName() {
        return "lmgtfy";
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(this.getName(), "Let me Google that for you!")
                .addSubcommands(
                        new SubcommandData("web", "Let me Google that for you!")
                                .addOptions(
                                        new OptionData(OptionType.STRING, "search", "What to search for.", true),
                                        new OptionData(OptionType.USER, "victim", "Ping this person to victimize them!")
                                ),
                        new SubcommandData("images", "Let me Google an image for you!")
                                .addOptions(
                                        new OptionData(OptionType.STRING, "search", "What to search for.", true),
                                        new OptionData(OptionType.USER, "victim", "Ping this person to victimize them!")
                                )
                );
    }

    @Override
    public void slashExecute(@NotNull SlashCommandInteractionEvent event) {
        String search = "https://lmgtfy.app/?q=" + event.getOption("search", "", OptionMapping::getAsString).replace("\s", "+") + (Objects.equals(event.getSubcommandName(), "images") ? "&t=i" : "");
        User victim = event.getOption("victim", OptionMapping::getAsUser);

        event.reply((victim != null ? victim.getAsMention() + ", this is for you: " : "") + search).queue();
    }
}
