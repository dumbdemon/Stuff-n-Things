package com.terransky.stuffnthings.commandSystem.commands.fun;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.commandSystem.utilities.Mastermind;
import com.terransky.stuffnthings.commandSystem.utilities.Metadata;
import com.terransky.stuffnthings.commandSystem.utilities.SlashModule;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

public class lmgtfy implements ISlashCommand {
    @Override
    public String getName() {
        return "lmgtfy";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Commons.getFastDateFormat();
        var metadata = new Metadata(this.getName(), "Let me Google that for you!", """
            When a person is too lazy to search it up themselves, call this on 'em.
            """, Mastermind.DEVELOPER,
            SlashModule.FUN,
            format.parse("24-08-2022_11:10"),
            format.parse("30-11-2022_13:40")
        );

        metadata.addSubcommands(
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

        return metadata;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) {
        String search = "https://lmgtfy.app/?q=" + event.getOption("search", "", OptionMapping::getAsString).replace(" ", "+") + ("images".equals(event.getSubcommandName()) ? "&t=i" : "");
        User victim = event.getOption("victim", OptionMapping::getAsUser);

        event.reply((victim != null ? victim.getAsMention() + ", this is for you: " : "") + search).queue();
    }
}
