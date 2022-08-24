package com.terransky.TestingBot.listeners;

import com.terransky.TestingBot.Commons;
import com.terransky.TestingBot.slashSystem.CommandManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class ListeningForEvents extends ListenerAdapter {
    private final Logger log = LoggerFactory.getLogger("Main Listener");
    private final Dotenv config = Dotenv.configure().load();
    private final Color embedColor = new Commons().defaultEmbedColor;
    private final List<CommandData> globalCommandData = new CommandManager().getCommandData(true);

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (Objects.equals(config.get("APP_ID"), config.get("PUBLIC_BOT"))) {
            log.info(globalCommandData.size() + " global commands loaded!");
            event.getJDA().updateCommands().addCommands(globalCommandData).queue();
        } else event.getJDA().updateCommands().queue();
        log.info("Service started!");
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        final List<CommandData> guildCommandData = new CommandManager().getCommandData(false, event.getGuild().getIdLong());
        User theBot = event.getJDA().getSelfUser();

        if (Objects.equals(config.get("APP_ID"), config.get("TESTING_BOT"))) {
            event.getGuild().updateCommands().addCommands(globalCommandData).queue();
            log.info(globalCommandData.size() + " global commands loaded on " + event.getGuild().getName() + " [" + event.getGuild().getIdLong() + "]!");
        }

        if (guildCommandData.size() != 0) {
            event.getGuild().updateCommands().addCommands(guildCommandData).queue();
            log.info(guildCommandData.size() + " guild commands loaded on " + event.getGuild().getName() + " [" + event.getGuild().getIdLong() + "]!");
        }

        /*
        Add guild to database code.
         */

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(embedColor)
                .setAuthor(theBot.getName(), null, theBot.getAvatarUrl())
                .setDescription("> *What am I doing here?*\n> *Why am I here?*\n> *Am I supposed to be here?*");
        Objects.requireNonNull(event.getGuild().getDefaultChannel()).asTextChannel().sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        String serverName = event.getGuild().getName();
        long serverID = event.getGuild().getIdLong();

        /*
        Remove guild from database code.
         */

        log.info("I have left " + serverName + " [" + serverID + "]!");
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        final List<CommandData> guildCommandData = new CommandManager().getCommandData(false, event.getGuild().getIdLong());
        if (Objects.equals(config.get("APP_ID"), config.get("TESTING_BOT"))) {
            event.getGuild().updateCommands().addCommands(globalCommandData).queue();

            log.info(globalCommandData.size() + " global commands loaded on " + event.getGuild().getName() + " [" + event.getGuild().getIdLong() + "]!");
        } else event.getGuild().updateCommands().queue();

        if (guildCommandData.size() != 0) {
            event.getGuild().updateCommands().addCommands(guildCommandData).queue();
            log.info(guildCommandData.size() + " guild commands loaded on " + event.getGuild().getName() + " [" + event.getGuild().getIdLong() + "]!");
        }
    }
}
