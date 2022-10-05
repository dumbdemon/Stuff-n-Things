package com.terransky.StuffnThings.listeners;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.CommandManager;
import com.terransky.StuffnThings.database.SQLiteDataSource;
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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ListeningForEvents extends ListenerAdapter {
    private final Logger log = LoggerFactory.getLogger("Main Listener");
    private final Dotenv config = Commons.config;
    private final List<CommandData> globalCommandData = new CommandManager().getCommandData(true);

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (config.get("TESTING_MODE").equals("false")) {
            log.info(globalCommandData.size() + " global commands loaded!");
            event.getJDA().updateCommands().addCommands(globalCommandData).queue();
        } else event.getJDA().updateCommands().queue();
        log.info("Service started!");
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        User theBot = event.getJDA().getSelfUser();

        if (config.get("TESTING_MODE").equals("true")) {
            event.getGuild().updateCommands().addCommands(globalCommandData).queue();
            log.info(globalCommandData.size() + " global commands loaded as guild commands on " + event.getGuild().getName() + " [" + event.getGuild().getIdLong() + "]!");
        }
        addGuildToDB(event.getGuild());

        EmbedBuilder eb = new EmbedBuilder()
            .setColor(Commons.defaultEmbedColor)
            .setAuthor(theBot.getName(), null, theBot.getAvatarUrl())
            .setDescription("> *What am I doing here?*\n> *Why am I here?*\n> *Am I supposed to be here?*");
        Objects.requireNonNull(event.getGuild().getDefaultChannel()).asTextChannel().sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        String serverName = event.getGuild().getName();
        long serverID = event.getGuild().getIdLong();

        log.info("I have left %s [%s]!".formatted(serverName, serverID));

        if (config.get("TESTING_MODE").equals("true")) {
            try {
                try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                    .prepareStatement("DELETE FROM guilds WHERE guild_id = ?")) {
                    stmt.setString(1, event.getGuild().getId());
                    stmt.execute();
                    log.info("%s [%s] has been removed from the guilds table".formatted(event.getGuild().getName(), event.getGuild().getId()));
                }

                try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                    .prepareStatement("DROP TABLE users_" + event.getGuild().getId())) {
                    stmt.execute();
                    log.info("Users table for %s [%s] has been removed from the database".formatted(event.getGuild().getName(), event.getGuild().getId()));
                }
            } catch (SQLException e) {
                log.error("%s : %s".formatted(e.getClass().getName(), e.getMessage()));
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        if (event.getUser().isBot()) return;

        String userID = event.getUser().getId(),
            guildID = event.getGuild().getId();

        if (config.get("TESTING_MODE").equals("true")) {
            try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                .prepareStatement("DELETE FROM users_" + guildID + " WHERE user_id = ?")) {
                stmt.setString(1, userID);
                stmt.execute();
                log.info("User %s left %s [%s]. Users table has been updated".formatted(userID, event.getGuild().getName(), guildID));
            } catch (SQLException e) {
                log.error("%s : %s".formatted(e.getClass().getName(), e.getMessage()));
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        if (config.get("TESTING_MODE").equals("true")) {
            event.getGuild().updateCommands().addCommands(globalCommandData).queue();
            log.info(globalCommandData.size() + " global commands loaded as guild commands on " + event.getGuild().getName() + " [" + event.getGuild().getIdLong() + "]!");
        }

        //If bot is already in a guild add them.
        addGuildToDB(event.getGuild());
    }

    private void addGuildToDB(@NotNull Guild guild) {
        if (config.get("TESTING_MODE").equals("true")) return;
        String guildName = guild.getName(), guildId = guild.getId();
        try (final PreparedStatement sStmt = SQLiteDataSource.getConnection()
            .prepareStatement("INSERT OR IGNORE INTO guilds(guild_id) VALUES(?)")) {
            sStmt.setString(1, guildId);
            sStmt.execute();
            log.info("%s[%s] was added to the database".formatted(guildName, guildId));

            try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                .prepareStatement("CREATE TABLE IF NOT EXISTS users_" + guildId + " (" +
                    "user_id TEXT PRIMARY KEY," +
                    "kill_attempts INTEGER DEFAULT 0," +
                    "kill_under_to INTEGER DEFAULT 0" +
                    ");")) {
                stmt.execute();
                log.info("Users table for %s[%s] is ready".formatted(guildName, guildId));
            }
        } catch (SQLException e) {
            log.error("%s : %s".formatted(e.getClass().getName(), e.getMessage()));
            e.printStackTrace();
        }
    }
}
