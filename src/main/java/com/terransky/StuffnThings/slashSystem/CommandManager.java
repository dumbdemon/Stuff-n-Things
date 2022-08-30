package com.terransky.StuffnThings.slashSystem;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.database.SQLiteDataSource;
import com.terransky.StuffnThings.slashSystem.commands.*;
import com.terransky.StuffnThings.slashSystem.commands.admin.config;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CommandManager extends ListenerAdapter {
    private final Color embedColor = new Commons().defaultEmbedColor;
    private final Dotenv config = Dotenv.configure().load();
    private final List<ISlash> iSlashList = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(CommandManager.class);

    public CommandManager() {
        addCommand(new about());
        addCommand(new config());
        addCommand(new getDadJokes());
        addCommand(new getInvite());
        addCommand(new kill());
        addCommand(new lmgtfy());
        addCommand(new meme());
        addCommand(new ping());
        addCommand(new robFailChance());
        addCommand(new say());
        addCommand(new suggest());
        addCommand(new test());
        addCommand(new userInfo());
    }

    private void addCommand(ISlash iSlash) {
        boolean nameFound = iSlashList.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iSlash.getName()));

        if (nameFound) throw new IllegalArgumentException("A command with this name already exists");

        iSlashList.add(iSlash);
    }

    @Nullable
    private ISlash getCommand(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (ISlash cmd : iSlashList) {
            if (cmd.getName().equals(toSearch)) {
                return cmd;
            }
        }

        return null;
    }

    public List<CommandData> getCommandData(boolean globalCommands, Long... serverID) {
        final List<CommandData> commandData = new ArrayList<>();

        if (globalCommands) {
            for (ISlash command : iSlashList) {
                if (command.isGlobalCommand() && command.workingCommand()) {
                    commandData.add(command.commandData());
                }
            }
        } else {
            for (ISlash command : iSlashList) {
                if (!command.isGlobalCommand() && command.workingCommand()) {
                    if (serverID.length != 0) {
                        for (Long id : serverID) {
                            boolean addToServer = command.getServerRestrictions().stream().anyMatch(it -> it.equals(id));

                            if (addToServer) commandData.add(command.commandData());
                        }
                    } else commandData.add(command.commandData());
                }
            }
        }

        return commandData;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getUser().isBot()) return;

        //Add user to database or ignore if exists
        try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                .prepareStatement("INSERT OR IGNORE INTO users_" + event.getGuild().getId() + "(user_id) VALUES(?)")) {
            stmt.setString(1, event.getUser().getId());
            stmt.execute();
        } catch (SQLException e) {
            log.error("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
            e.printStackTrace();
        }

        ISlash cmd = getCommand(event.getName());
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Oops!")
                .setDescription("An error occurred while executing that command!\nPlease contact <@" + config.get("OWNER_ID") + "> with the command that you used and when.")
                .setColor(embedColor)
                .setFooter(event.getUser().getAsTag());

        if (cmd != null) {
            String origins = event.isFromGuild() ? "%s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()) : event.getUser().getAsTag() + "'s private channel";
            log.debug("Command " + cmd.getName().toUpperCase() + " called on " + origins);
            try {
                cmd.slashExecute(event);
            } catch (Exception e) {
                log.debug("Full command path that triggered error :: [" + event.getCommandPath() + "]");
                log.error(String.valueOf(e));
                e.printStackTrace();
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                } else event.replyEmbeds(embed.build()).setEphemeral(true).queue();
            }
        }
    }
}
