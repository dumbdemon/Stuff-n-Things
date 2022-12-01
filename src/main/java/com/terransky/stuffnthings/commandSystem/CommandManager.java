package com.terransky.stuffnthings.commandSystem;

import com.terransky.stuffnthings.commandSystem.commands.admin.channelUnLock;
import com.terransky.stuffnthings.commandSystem.commands.admin.checkPerms;
import com.terransky.stuffnthings.commandSystem.commands.admin.config;
import com.terransky.stuffnthings.commandSystem.commands.devs.getInvite;
import com.terransky.stuffnthings.commandSystem.commands.devs.test;
import com.terransky.stuffnthings.commandSystem.commands.devs.userInfo;
import com.terransky.stuffnthings.commandSystem.commands.fun.*;
import com.terransky.stuffnthings.commandSystem.commands.general.about;
import com.terransky.stuffnthings.commandSystem.commands.general.ping;
import com.terransky.stuffnthings.commandSystem.commands.general.suggest;
import com.terransky.stuffnthings.commandSystem.commands.maths.fibonacciSequence;
import com.terransky.stuffnthings.commandSystem.commands.maths.numbersAPI;
import com.terransky.stuffnthings.commandSystem.commands.maths.solveQuadratic;
import com.terransky.stuffnthings.commandSystem.commands.mtg.calculateRats;
import com.terransky.stuffnthings.commandSystem.commands.mtg.whatsInStandard;
import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.commandSystem.utilities.Metadata;
import com.terransky.stuffnthings.database.SQLiteDataSource;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import com.terransky.stuffnthings.utilities.Config;
import com.terransky.stuffnthings.utilities.EmbedColors;
import com.terransky.stuffnthings.utilities.GuildOnly;
import com.terransky.stuffnthings.utilities.LogList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class CommandManager extends ListenerAdapter {
    private final List<ISlashCommand> iSlashCommandsList = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(CommandManager.class);

    public CommandManager() {
        //Admin Commands
        addCommand(new channelUnLock());
        addCommand(new checkPerms());
        addCommand(new config());

        //Math Commands
        addCommand(new fibonacciSequence());
        addCommand(new numbersAPI());
        addCommand(new solveQuadratic());

        //M:tG Commands
        addCommand(new calculateRats());
        addCommand(new whatsInStandard());

        //Fun Commands
        addCommand(new colorInfo());
        addCommand(new dictionary());
        addCommand(new getDadJokes());
        addCommand(new kill());
        addCommand(new lmgtfy());
        addCommand(new meme());
        addCommand(new robFailChance());
        addCommand(new say());

        //General Commands
        addCommand(new about());
        addCommand(new ping());
        addCommand(new suggest());

        //Dev commands
        addCommand(new getInvite());
        addCommand(new test());
        addCommand(new userInfo());
    }

    /**
     * Add a {@link ISlashCommand} object to be indexed and used.
     *
     * @param iSlashCommand An {@link ISlashCommand} object.
     * @throws IndexOutOfBoundsException If the {@code iSlashCommandsList} is more than the max slash commands.
     */
    private void addCommand(ISlashCommand iSlashCommand) {
        boolean nameFound = iSlashCommandsList.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iSlashCommand.getName()));

        if (nameFound) throw new IllegalArgumentException("A command with this name already exists");

        if (iSlashCommandsList.size() > Commands.MAX_SLASH_COMMANDS)
            throw new IndexOutOfBoundsException("You can only have at most %d slash commands.".formatted(Commands.MAX_SLASH_COMMANDS));
        else iSlashCommandsList.add(iSlashCommand);
    }

    /**
     * Get the {@link ISlashCommand} object for execution at {@code onSlashCommandInteraction()}.
     *
     * @param search The name of the command.
     * @return An {@link Optional} of {@link ISlashCommand}.
     */
    private Optional<ISlashCommand> getCommand(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (ISlashCommand cmd : iSlashCommandsList) {
            if (cmd.getName().equals(toSearch)) {
                return Optional.of(cmd);
            }
        }

        return Optional.empty();
    }

    /**
     * Get all command names as {@link Command.Choice Choises} for the {@link about} command.
     *
     * @return A {@link List} of {@link Command.Choice Choises}.
     */
    public List<Command.Choice> getCommandsAsChoices() {
        List<Command.Choice> choices = new ArrayList<>();
        for (ISlashCommand command : iSlashCommandsList.stream().filter(it -> it.isGlobal() && it.isWorking()).sorted().toList()) {
            choices.add(new Command.Choice(WordUtils.capitalize(command.getName().replace("-", " ")), command.getName()));
        }
        return choices;
    }

    /**
     * Get the {@link Metadata} of an {@link ISlashCommand}.
     *
     * @param search The name of the command to look for.
     * @return An {@link Optional} of {@link Metadata}.
     * @throws ParseException If the pattern used in {@code Metadata.getImplementationDate()} or {@code Metadata.getLastUpdated()} in a slash command class
     *                        is given an invalid date string.
     */
    public Optional<Metadata> getMetadata(@NotNull String search) throws ParseException {
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
     * @throws ParseException            If the pattern used in {@code Metadata.getImplementationDate()} or {@code Metadata.getLastUpdated()} in an {@link ISlashCommand}
     *                                   is given an invalid date string.
     * @throws IllegalArgumentException  If an {@link ISlashCommand} with that name is already indexed.
     * @throws IndexOutOfBoundsException If there are more than the combined total max slash commands, max message commands, and max user commands.
     */
    public List<CommandData> getCommandData() throws ParseException {
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

        int commandLimit = Commands.MAX_SLASH_COMMANDS + Commands.MAX_MESSAGE_COMMANDS + Commands.MAX_USER_COMMANDS;
        if (commandData.size() > commandLimit)
            throw new IndexOutOfBoundsException("CommandData List can not be more than %s. You have %s in the list.".formatted(commandLimit, commandData.size()));

        return commandData;
    }

    /**
     * Get the command data of all slash commands specifically for a server.
     *
     * @param serverId The ID of the server to check for.
     * @return Returns a list of {@link CommandData}. Could potentially return an empty list.
     * @throws ParseException If the pattern used in {@code Metadata.getImplementationDate()} or {@code Metadata.getLastUpdated()} in a slash command class
     *                        is given an invalid date string.
     */
    public List<CommandData> getCommandData(long serverId) throws ParseException {
        final List<CommandData> commandData = new ArrayList<>();

        for (ISlashCommand command : iSlashCommandsList.stream().filter(it -> !it.isGlobal() && it.isWorking()).sorted().toList()) {
            boolean addToServer = command.getServerRestrictions().stream().anyMatch(it -> it.equals(serverId)) || command.getServerRestrictions().isEmpty();

            if (addToServer) commandData.add(command.getCommandData());
        }

        return commandData;
    }

    /**
     * The main event handler for all slash commands.
     *
     * @param event The {@link SlashCommandInteractionEvent}
     */
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getUser().isBot()) return;
        else if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        //Add user to database or ignore if exists
        if (Config.isEnableDatabase()) {
            try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                .prepareStatement("INSERT OR IGNORE INTO users_" + blob.getGuildId() + "(user_id) VALUES(?)")) {
                stmt.setString(1, event.getUser().getId());
                stmt.execute();
            } catch (SQLException e) {
                log.error("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
                LogList.error(Arrays.asList(e.getStackTrace()), CommandManager.class);
            }
        }

        Optional<ISlashCommand> cmd = getCommand(event.getName());
        MessageEmbed cmdFailed = new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription("An error occurred while executing that command!\nPlease report this event [here](%s).".formatted(Config.getConfig().get("BOT_ERROR_REPORT")))
            .setColor(EmbedColors.getError())
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build();

        if (cmd.isPresent()) {
            ISlashCommand command = cmd.get();
            log.debug("Command " + command.getName().toUpperCase() + " called on %s [%d]".formatted(blob.getGuildName(), blob.getGuildIdLong()));
            try {
                command.execute(event, blob);
            } catch (Exception e) {
                log.debug("Full command path that triggered error :: [" + event.getFullCommandName() + "]");
                log.error("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
                LogList.error(Arrays.asList(e.getStackTrace()), CommandManager.class);
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(cmdFailed).queue();
                } else event.replyEmbeds(cmdFailed).setEphemeral(true).queue();
            }
        }
    }
}
