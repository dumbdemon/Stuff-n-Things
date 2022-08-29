package com.terransky.StuffnThings.listeners;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.database.SQLiteDataSource;
import com.terransky.StuffnThings.slashSystem.CommandManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            log.info(globalCommandData.size() + " global commands loaded as guild commands on " + event.getGuild().getName() + " [" + event.getGuild().getIdLong() + "]!");
        } else {
            if (guildCommandData.size() != 0) {
                event.getGuild().updateCommands().addCommands(guildCommandData).queue();
                log.info(guildCommandData.size() + " guild commands loaded on " + event.getGuild().getName() + " [" + event.getGuild().getIdLong() + "]!");
            }
        }

        addGuildToDB(event.getGuild());

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

        log.info("I have left " + serverName + " [" + serverID + "]!");

        try {
            try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                    .prepareStatement("DELETE FROM guilds WHERE guild_id = ?")) {
                stmt.setString(1, event.getGuild().getId());
                stmt.execute();
                log.info("%s[%s] has been removed the database".formatted(event.getGuild().getName(), event.getGuild().getId()));
            }

            try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                    .prepareStatement("DROP TABLE users_" + event.getGuild().getId())) {
                stmt.execute();
                log.info("Users table for %s[%s] has been removed the database".formatted(event.getGuild().getName(), event.getGuild().getId()));
            }
        } catch (SQLException e) {
            log.error("%s : %s".formatted(e.getClass().getName(), e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        String userID = event.getUser().getId(),
                guildID = event.getGuild().getId();

        try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                .prepareStatement("DELETE FROM users_" + guildID + " WHERE user_id = ?")) {
            stmt.setString(1, userID);
            stmt.execute();
            log.info("Users table for %s[%s] has been removed the database".formatted(event.getGuild().getName(), event.getGuild().getId()));
        } catch (SQLException e) {
            log.error("%s : %s".formatted(e.getClass().getName(), e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        final List<CommandData> guildCommandData = new CommandManager().getCommandData(false, event.getGuild().getIdLong());
        if (Objects.equals(config.get("APP_ID"), config.get("TESTING_BOT"))) {
            event.getGuild().updateCommands().addCommands(globalCommandData).queue();
            log.info(globalCommandData.size() + " global commands loaded as guild commands on " + event.getGuild().getName() + " [" + event.getGuild().getIdLong() + "]!");
        } else {
            if (guildCommandData.size() != 0) {
                event.getGuild().updateCommands().addCommands(guildCommandData).queue();
                log.info(guildCommandData.size() + " guild commands loaded on " + event.getGuild().getName() + " [" + event.getGuild().getIdLong() + "]!");
            }
        }
        try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                .prepareStatement("SELECT id FROM guilds WHERE guild_id = ?")) {
            stmt.setString(1, event.getGuild().getId());
            try (final ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    addGuildToDB(event.getGuild());
                }
            }
        } catch (SQLException e) {
            log.error("%s : %s".formatted(e.getClass().getName(), e.getMessage()));
            e.printStackTrace();
        }
    }

    private void addGuildToDB(@NotNull Guild guild) {
        try (final PreparedStatement sStmt = SQLiteDataSource.getConnection()
                .prepareStatement("INSERT INTO guilds(guild_id) VALUES(?)")) {
            sStmt.setString(1, guild.getId());
            sStmt.execute();
            log.info("%s[%s] was added to the database".formatted(guild.getName(), guild.getId()));

            try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                    .prepareStatement("CREATE TABLE IF NOT EXISTS users_" + guild.getId() + " (" +
                            "user_id TEXT PRIMARY KEY," +
                            "kill_attempts INTEGER DEFAULT 0," +
                            "kill_under_to INTEGER DEFAULT 0" +
                            ");")) {
                stmt.execute();
                log.info("Users table for %s[%s] is ready".formatted(guild.getName(), guild.getId()));
            }
        } catch (SQLException e) {
            log.error("%s : %s".formatted(e.getClass().getName(), e.getMessage()));
            e.printStackTrace();
        }
    }
}
