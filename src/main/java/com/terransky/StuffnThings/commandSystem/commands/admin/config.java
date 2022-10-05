package com.terransky.StuffnThings.commandSystem.commands.admin;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
import com.terransky.StuffnThings.database.SQLiteDataSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class config implements ISlashCommand {
    private final Logger log = LoggerFactory.getLogger(config.class);

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(this.getName(), "The config manager.")
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER))
            .addSubcommandGroups(
                new SubcommandGroupData("kill", "Change the config settings for the kill command.")
                    .addSubcommands(
                        new SubcommandData("max-kills", "Get the max kills for the `/kill target` command.")
                            .addOptions(
                                new OptionData(OptionType.INTEGER, "set-max", "Set the max kills for the server.")
                                    .setRequiredRange(1, 99)
                            ),
                        new SubcommandData("timeout", "\"X\" amount of kills within \"Y\" amount of time\u2026 what's \"Y\"?")
                            .addOptions(
                                new OptionData(OptionType.INTEGER, "set-timeout", "Set the timeout of the kill command in whole minutes up to an hour.")
                                    .setRequiredRange(1, 60)
                            )
                    )
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) throws Exception {
        String subCommandGroup = event.getSubcommandGroup();
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(Commons.defaultEmbedColor);

        if (subCommandGroup == null) throw new Exception("Discord API Error: No subcommand group was given.");

        if ("kill".equals(subCommandGroup)) {
            //this.killConfig(event);
            eb.setTitle("Config setting not yet available.");
            event.replyEmbeds(eb.build()).queue();
        }
    }


    @SuppressWarnings({"ConstantConditions", "unused"})
    private void killConfig(@NotNull SlashCommandInteractionEvent event) throws Exception {
        String subCommand = event.getSubcommandName();
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(Commons.defaultEmbedColor)
            .setFooter(event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl());

        if (subCommand == null) throw new Exception("Discord API Error: No subcommand was given.");

        switch (subCommand) {
            case "max-kills" -> {
                event.deferReply(true).queue();
                int newMax = event.getOption("set-max", 0, OptionMapping::getAsInt),
                    oldMax;

                if (newMax != 0) {
                    try (final PreparedStatement stmt1 = SQLiteDataSource.getConnection()
                        .prepareStatement("SELECT killcmd_max FROM guilds WHERE guild_id = ?")) {
                        stmt1.setString(1, event.getGuild().getId());
                        try (final ResultSet rs1 = stmt1.executeQuery()) {
                            oldMax = rs1.getInt("killcmd_max");
                            log.debug("Old max kills for %s is %d".formatted(event.getGuild().getId(), oldMax));
                        }
                    } catch (SQLException e) {
                        event.getHook().sendMessageEmbeds(anErrorOccurred(e)).queue();
                        return;
                    }

                    if (newMax == oldMax) {
                        log.debug("No change for %s".formatted(event.getGuild().getId()));
                        event.getHook().sendMessageEmbeds(eb.setTitle("Config not Updated")
                            .setDescription("Currently set Max Kills is requested amount: `%d Max Kills`.".formatted(newMax))
                            .build()
                        ).queue();
                        return;
                    }

                    try (final PreparedStatement stmt2 = SQLiteDataSource.getConnection()
                        .prepareStatement("UPDATE guilds SET killcmd_max = ? WHERE guild_id = ?")) {
                        stmt2.setInt(1, newMax);
                        stmt2.setString(2, event.getGuild().getId());
                        stmt2.execute();
                        log.debug("[%s] Updated max kills from %d to %d".formatted(event.getGuild().getId(), oldMax, newMax));
                    } catch (SQLException e) {
                        event.getHook().sendMessageEmbeds(anErrorOccurred(e)).queue();
                        return;
                    }

                    eb.setTitle("Config Updated")
                        .addField("Old Max", String.valueOf(oldMax), true)
                        .addField("New Max", String.valueOf(newMax), true);

                    event.getHook().sendMessageEmbeds(eb.build()).queue();
                } else {
                    try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                        .prepareStatement("SELECT killcmd_max FROM guilds WHERE guild_id = ?")) {
                        stmt.setString(1, event.getGuild().getId());
                        try (final ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                log.debug("Retrieved max kills for %s".formatted(event.getGuild().getId()));
                                event.getHook().sendMessageEmbeds(eb.setTitle("Kill Config")
                                    .addField("Max Kills", String.valueOf(rs.getInt("killcmd_max")), false)
                                    .build()
                                ).queue();
                            }
                        }
                    } catch (SQLException e) {
                        event.getHook().sendMessageEmbeds(anErrorOccurred(e)).queue();
                    }
                }
            }

            case "timeout" -> {
                event.deferReply(true).queue();
                final int toMillis = 60000;
                int newTimeout = event.getOption("set-timeout", 0, OptionMapping::getAsInt) * toMillis,
                    oldTimeout = 0;

                if (newTimeout != 0) {
                    try (final PreparedStatement stmt1 = SQLiteDataSource.getConnection()
                        .prepareStatement("SELECT killcmd_timeout FROM guilds WHERE guild_id = ?")) {
                        stmt1.setString(1, event.getGuild().getId());
                        try (final ResultSet rs = stmt1.executeQuery()) {
                            if (rs.next()) {
                                oldTimeout = rs.getInt("killcmd_timeout");
                                log.debug("Old timeout for %s is %dms (%d mins)".formatted(event.getGuild().getId(), oldTimeout, oldTimeout / toMillis));
                            }
                        }
                    } catch (SQLException e) {
                        event.getHook().sendMessageEmbeds(anErrorOccurred(e)).queue();
                        event.getHook().sendMessageEmbeds(anErrorOccurred(e)).queue();
                        return;
                    }

                    if (newTimeout == oldTimeout) {
                        log.debug("No change for %s".formatted(event.getGuild().getId()));
                        event.getHook().sendMessageEmbeds(eb.setTitle("Config not Updated")
                            .setDescription("Currently set Timeout is requested amount: `%d minutes`.".formatted(newTimeout / toMillis))
                            .build()
                        ).queue();
                        return;
                    }

                    try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                        .prepareStatement("UPDATE guilds SET killcmd_timeout = ? WHERE guild_id = ?")) {
                        stmt.setInt(1, newTimeout);
                        stmt.setString(2, event.getGuild().getId());
                        stmt.execute();
                        log.debug("[%s] Updated timeout from %dms (%d mins) to %dms (%d mins)".formatted(event.getGuild().getId(), oldTimeout, oldTimeout / toMillis, newTimeout, newTimeout / toMillis));
                    } catch (SQLException e) {
                        event.getHook().sendMessageEmbeds(anErrorOccurred(e)).queue();
                        return;
                    }

                    eb.setTitle("Config Updated")
                        .addField("Old Timeout", String.valueOf(oldTimeout), true)
                        .addField("New Timeout", String.valueOf(newTimeout), true);

                    event.getHook().sendMessageEmbeds(eb.build()).queue();
                } else {
                    try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                        .prepareStatement("SELECT killcmd_timeout FROM guilds WHERE guild_id = ?")) {
                        stmt.setString(1, event.getGuild().getId());
                        try (final ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                log.debug("Retrieved timeout for %s".formatted(event.getGuild().getId()));
                                event.getHook().sendMessageEmbeds(eb.setTitle("Kill Config")
                                    .addField("Timeout", "%d minutes".formatted(rs.getInt("killcmd_timeout") / toMillis), false)
                                    .build()
                                ).queue();
                            }
                        }
                    } catch (SQLException e) {
                        event.getHook().sendMessageEmbeds(anErrorOccurred(e)).queue();
                    }
                }
            }
        }
    }

    protected MessageEmbed anErrorOccurred(@NotNull SQLException e) {
        EmbedBuilder eb = new EmbedBuilder().setColor(Commons.defaultEmbedColor);
        log.error("%s : %s".formatted(e.getClass().getName(), e.getMessage()));
        e.printStackTrace();
        SQLiteDataSource.restartConnection();
        eb.setTitle("Uh-oh")
            .setDescription("An error occurred while executing the command!\n Try again in a moment!");
        return eb.build();
    }
}