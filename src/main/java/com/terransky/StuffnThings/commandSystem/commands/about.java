package com.terransky.StuffnThings.commandSystem.commands;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class about implements ISlashCommand {
    @Override
    public String getName() {
        return "about";
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(this.getName(), "What am I? Who am I?");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        event.replyEmbeds(new EmbedBuilder()
            .setColor(Commons.defaultEmbedColor)
            .setDescription("> *Who am I?*\n> *What am I?*\n> *I think I need help...*")
            .setTitle(event.getJDA().getSelfUser().getName())
            .setThumbnail("https://cdn.discordapp.com/attachments/1004779062658617346/1004782398564745318/StuffnThings_-_Main.png")
            .build()
        ).queue();
    }
}