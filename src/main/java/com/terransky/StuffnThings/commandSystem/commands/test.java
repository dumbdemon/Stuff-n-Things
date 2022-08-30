package com.terransky.StuffnThings.commandSystem.commands;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.ISlash;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class test implements ISlash {
    private final Color embedColor = new Commons().defaultEmbedColor;
    private final Dotenv config = Dotenv.configure().load();

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(this.getName(), "A casual test");
    }

    @Override
    public boolean workingCommand() {
        return config.get("APP_ID").equals(config.get("TESTING_BOT"));
    }

    @Override
    public @Nullable List<Long> getServerRestrictions() {
        final List<Long> arrayList = new ArrayList<>();
        arrayList.add(Long.parseLong(config.get("SUPPORT_GUILD_ID")));
        return arrayList;
    }

    @Override
    public void slashExecute(@NotNull SlashCommandInteractionEvent event) {
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("A casual test.")
                .setColor(embedColor);

        event.replyEmbeds(eb.build()).queue();
    }
}
