package com.terransky.stuffnthings.listeners;

import com.terransky.stuffnthings.commandSystem.CommandManager;
import com.terransky.stuffnthings.database.SQLiteDataSource;
import com.terransky.stuffnthings.secretsAndLies;
import com.terransky.stuffnthings.utilities.Config;
import com.terransky.stuffnthings.utilities.EmbedColors;
import com.terransky.stuffnthings.utilities.LogList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

//todo: Handle ParseException
public class ListeningForEvents extends ListenerAdapter {
    private final Logger log = LoggerFactory.getLogger(ListeningForEvents.class);
    private final List<CommandData> globalCommandData = new CommandManager().getCommandData();

    public ListeningForEvents() throws ParseException {
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        long timerInMS = 600000;
        if (!Config.isTestingMode()) {
            log.info(globalCommandData.size() + " global commands loaded!");
            event.getJDA().updateCommands().addCommands(globalCommandData).queue();
        } else event.getJDA().updateCommands().queue();
        log.info("Service started!");

        if (!Config.isTestingMode()) {
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                @SuppressWarnings("ConstantConditions")
                public void run() {
                    String[] watchList = secretsAndLies.whatAmIWatching;

                    event.getJDA().getShardManager().setActivity(Activity.playing(watchList[(new Random()).nextInt(watchList.length)]));
                }
            }, timerInMS, timerInMS);
        }
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        User theBot = event.getJDA().getSelfUser();

        upsertGuildCommands(event);
        addGuildToDB(event.getGuild());

        EmbedBuilder eb = new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setAuthor(theBot.getName(), null, theBot.getAvatarUrl())
            .setDescription("> *What am I doing here?*\n> *Why am I here?*\n> *Am I supposed to be here?*");
        Objects.requireNonNull(event.getGuild().getDefaultChannel()).asTextChannel().sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        String serverName = event.getGuild().getName();
        long serverID = event.getGuild().getIdLong();

        log.info("I have left %s [%s]!".formatted(serverName, serverID));

        if (Config.isDatabaseEnabled()) {
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
                LogList.error(Arrays.asList(e.getStackTrace()), ListeningForEvents.class);
            }
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        if (event.getUser().isBot()) return;

        if (Config.isDatabaseEnabled()) {
            String userID = event.getUser().getId(),
                guildID = event.getGuild().getId();

            try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                .prepareStatement("DELETE FROM users_" + guildID + " WHERE user_id = ?")) {
                stmt.setString(1, userID);
                stmt.execute();
                log.info("User %s left %s [%s]. Users table has been updated".formatted(userID, event.getGuild().getName(), guildID));
            } catch (SQLException e) {
                log.error("%s : %s".formatted(e.getClass().getName(), e.getMessage()));
                LogList.error(Arrays.asList(e.getStackTrace()), ListeningForEvents.class);
            }
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        upsertGuildCommands(event);
        addGuildToDB(event.getGuild());
    }

    private void upsertGuildCommands(@NotNull GenericGuildEvent event) {
        if (Config.isTestingMode()) {
            try {
                globalCommandData.addAll(new CommandManager().getCommandData(event.getGuild().getIdLong()));
            } catch (ParseException ignored) {
            }
            event.getGuild().updateCommands().addCommands(globalCommandData).queue();
            log.info(globalCommandData.size() + " global commands loaded as guild commands on " + event.getGuild().getName() + " [" + event.getGuild().getIdLong() + "]!");
        } else {
            try {
                if (new CommandManager().getCommandData(event.getGuild().getIdLong()).size() > 0) {
                    event.getGuild().updateCommands().addCommands(new CommandManager().getCommandData(event.getGuild().getIdLong())).queue();
                } else event.getGuild().updateCommands().queue();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void addGuildToDB(@NotNull Guild guild) {
        if (!Config.isDatabaseEnabled()) return;
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
            LogList.error(Arrays.asList(e.getStackTrace()), ListeningForEvents.class);
        }
    }
}
