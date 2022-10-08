package com.terransky.StuffnThings.commandSystem;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.commands.*;
import com.terransky.StuffnThings.commandSystem.commands.admin.checkPerms;
import com.terransky.StuffnThings.commandSystem.commands.admin.config;
import com.terransky.StuffnThings.commandSystem.commands.mtg.calculateRats;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
import com.terransky.StuffnThings.database.SQLiteDataSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CommandManager extends ListenerAdapter {
    private final List<ISlashCommand> iSlashCommandsList = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(CommandManager.class);

    public CommandManager() {
        //Admin Commands
        addCommand(new checkPerms());
        addCommand(new config());

        //Fun Commands
        addCommand(new about());
        addCommand(new calculateRats());
        addCommand(new colorInfo());
        addCommand(new getDadJokes());
        addCommand(new kill());
        addCommand(new lmgtfy());
        addCommand(new meme());
        addCommand(new robFailChance());
        addCommand(new say());

        //General Commands
        addCommand(new ping());
        addCommand(new suggest());

        //Dev commands
        addCommand(new getInvite());
        addCommand(new test());
        addCommand(new userInfo());
    }

    private void addCommand(ISlashCommand iSlashCommand) {
        boolean nameFound = iSlashCommandsList.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iSlashCommand.getName()));

        if (nameFound) throw new IllegalArgumentException("A command with this name already exists");

        iSlashCommandsList.add(iSlashCommand);
    }

    @Nullable
    private ISlashCommand getCommand(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (ISlashCommand cmd : iSlashCommandsList) {
            if (cmd.getName().equals(toSearch)) {
                return cmd;
            }
        }

        return null;
    }

    public List<CommandData> getCommandData(boolean globalCommands, Long... serverID) {
        final List<CommandData> commandData = new ArrayList<>();
        final List<CommandData> messageContext = new MessageContextManager().getCommandData();
        final List<CommandData> userContext = new UserContextManager().getCommandData();

        if (globalCommands) {
            for (ISlashCommand command : iSlashCommandsList) {
                if (command.isGlobalCommand() && command.workingCommand()) {
                    commandData.add(command.commandData());
                }
            }

            if (!messageContext.isEmpty()) {
                commandData.addAll(messageContext);
                log.debug("%d message contexts added".formatted(messageContext.size()));
            }
            if (!userContext.isEmpty()) {
                commandData.addAll(userContext);
                log.debug("%d user contexts added".formatted(userContext.size()));
            }
        } else {
            for (ISlashCommand command : iSlashCommandsList) {
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
        if (Commons.isTestingMode) {
            try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                .prepareStatement("INSERT OR IGNORE INTO users_" + event.getGuild().getId() + "(user_id) VALUES(?)")) {
                stmt.setString(1, event.getUser().getId());
                stmt.execute();
            } catch (SQLException e) {
                log.error("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
                e.printStackTrace();
            }
        }

        ISlashCommand cmd = new CommandManager().getCommand(event.getName());
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription("An error occurred while executing that command!\nPlease contact <@" + Commons.config.get("OWNER_ID") + "> with the command that you used and when.")
            .setColor(Commons.defaultEmbedColor)
            .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl());

        if (cmd != null) {
            String origins = event.isFromGuild() ? "%s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()) : event.getUser().getAsTag() + "'s private channel";
            log.debug("Command " + cmd.getName().toUpperCase() + " called on " + origins);
            try {
                cmd.execute(event);
            } catch (Exception e) {
                log.debug("Full command path that triggered error :: [" + event.getCommandPath() + "]");
                log.error("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
                e.printStackTrace();
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(eb.build()).queue();
                } else event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            }
        }
    }
}
