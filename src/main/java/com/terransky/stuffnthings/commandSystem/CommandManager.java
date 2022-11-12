package com.terransky.stuffnthings.commandSystem;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.commandSystem.commands.*;
import com.terransky.stuffnthings.commandSystem.commands.admin.checkPerms;
import com.terransky.stuffnthings.commandSystem.commands.admin.config;
import com.terransky.stuffnthings.commandSystem.commands.mtg.calculateRats;
import com.terransky.stuffnthings.commandSystem.commands.mtg.whatsInStandard;
import com.terransky.stuffnthings.commandSystem.metadata.Metadata;
import com.terransky.stuffnthings.database.SQLiteDataSource;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
        addCommand(new dictionary());
        addCommand(new fibonacciSequence());
        addCommand(new getDadJokes());
        addCommand(new kill());
        addCommand(new lmgtfy());
        addCommand(new meme());
        addCommand(new numbersAPI());
        addCommand(new robFailChance());
        addCommand(new say());
        addCommand(new whatsInStandard());

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

        if (iSlashCommandsList.size() > Commands.MAX_SLASH_COMMANDS)
            throw new IndexOutOfBoundsException("You can only have at most %d slash commands.".formatted(Commands.MAX_SLASH_COMMANDS));
        else iSlashCommandsList.add(iSlashCommand);
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

    public List<Command.Choice> getCommandsAsChoices() {
        List<Command.Choice> choices = new ArrayList<>();
        for (ISlashCommand iSlashCommand : iSlashCommandsList.stream().filter(it -> it.isGlobal() && it.isWorking()).sorted().toList()) {
            choices.add(new Command.Choice(WordUtils.capitalize(iSlashCommand.getName().replace("-", "\s")), iSlashCommand.getName()));
        }
        return choices;
    }

    public Optional<Metadata> getMetadata(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (ISlashCommand cmd : iSlashCommandsList) {
            if (cmd.getName().equals(toSearch)) {
                return Optional.of(cmd.getMetadata());
            }
        }
        return Optional.empty();
    }

    /**
     * Get the command data of all slash commands, message contexts, and user contexts.
     *
     * @return Returns a list of {@link CommandData}.
     */
    public List<CommandData> getCommandData() {
        final List<CommandData> commandData = new ArrayList<>();
        final List<CommandData> messageContext = new MessageContextManager().getCommandData();
        final List<CommandData> userContext = new UserContextManager().getCommandData();

        for (ISlashCommand command : iSlashCommandsList.stream().filter(it -> it.isGlobal() && it.isWorking()).sorted().toList()) {
            commandData.add(command.getCommandData());
        }

        if (!messageContext.isEmpty()) {
            commandData.addAll(messageContext);
            log.debug("%d message contexts added".formatted(messageContext.size()));
        } else log.debug("No message contexts were added.");
        if (!userContext.isEmpty()) {
            commandData.addAll(userContext);
            log.debug("%d user contexts added".formatted(userContext.size()));
        } else log.debug("No user contexts were added.");

        return commandData;
    }

    /**
     * Get the command data of all slash commands specifically for a server.
     *
     * @param serverId The ID of the server to check for.
     * @return Returns a list of {@link CommandData}. Could potentially return an empty list.
     */
    public List<CommandData> getCommandData(long serverId) {
        final List<CommandData> commandData = new ArrayList<>();

        for (ISlashCommand command : iSlashCommandsList.stream().filter(it -> !it.isGlobal() && it.isWorking()).sorted().toList()) {
            boolean addToServer = command.getServerRestrictions().stream().anyMatch(it -> it.equals(serverId)) || command.getServerRestrictions().size() == 0;

            if (addToServer) commandData.add(command.getCommandData());
        }

        return commandData;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getUser().isBot() || event.getGuild() == null) return;

        //Add user to database or ignore if exists
        if (Commons.ENABLE_DATABASE) {
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
        MessageEmbed cmdFailed = new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription("An error occurred while executing that command!\nPlease contact <@" + Commons.CONFIG.get("OWNER_ID") + "> with the command that you used and when.")
            .setColor(Commons.DEFAULT_EMBED_COLOR)
            .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl())
            .build();

        if (cmd != null) {
            log.debug("Command " + cmd.getName().toUpperCase() + " called on %s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()));
            try {
                cmd.execute(event);
            } catch (Exception e) {
                log.debug("Full command path that triggered error :: [" + event.getCommandPath() + "]");
                log.error("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
                e.printStackTrace();
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(cmdFailed).queue();
                } else event.replyEmbeds(cmdFailed).setEphemeral(true).queue();
            }
        }
    }
}
