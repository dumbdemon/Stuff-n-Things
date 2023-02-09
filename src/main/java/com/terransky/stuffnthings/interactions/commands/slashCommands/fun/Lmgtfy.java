package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.Metadata;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Lmgtfy implements ICommandSlash {
    @Override
    public String getName() {
        return "lmgtfy";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(this.getName(), "Let me Google that for you!", """
            When a person is too lazy to search it up themselves, call this on 'em.
            """, Mastermind.DEVELOPER,
            CommandCategory.FUN,
            Metadata.parseDate("24-08-2022_11:10"),
            Metadata.parseDate("21-12-2022_20:00")
        )
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
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        String search = "https://lmgtfy.app/?q=" + event.getOption("search", "", OptionMapping::getAsString).replace(" ", "+") + ("images".equals(event.getSubcommandName()) ? "&t=i" : "");
        User victim = event.getOption("victim", OptionMapping::getAsUser);

        event.reply((victim != null ? victim.getAsMention() + ", this is for you: " : "") + search).queue();
    }
}
