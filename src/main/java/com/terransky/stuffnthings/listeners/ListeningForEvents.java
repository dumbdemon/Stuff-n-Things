package com.terransky.stuffnthings.listeners;

import com.terransky.stuffnthings.ManagersManager;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ICommandMessage;
import com.terransky.stuffnthings.interfaces.interactions.ICommandUser;
import com.terransky.stuffnthings.managers.CommandIManager;
import com.terransky.stuffnthings.managers.SlashIManager;
import com.terransky.stuffnthings.utilities.command.EmbedColors;
import com.terransky.stuffnthings.utilities.general.Config;
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
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ListeningForEvents extends ListenerAdapter {
    private final Logger log = LoggerFactory.getLogger(ListeningForEvents.class);
    private final ManagersManager manager = new ManagersManager();
    private final SlashIManager slashManager = manager.getSlashManager();
    private final List<CommandData> globalCommandData = slashManager.getCommandData();
    private final CommandIManager<ICommandMessage> messageManager = manager.getMessageContextManager();
    private final CommandIManager<ICommandUser> userManager = manager.getUserContextManager();
    private final List<String> WATCHLIST = DatabaseManager.INSTANCE.getWatchList();

    {
        globalCommandData.addAll(messageManager.getCommandData());
        globalCommandData.addAll(userManager.getCommandData());
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (Config.isTestingMode()) {
            event.getJDA().updateCommands().queue();
            return;
        }

        event.getJDA().updateCommands()
            .addCommands(globalCommandData)
            .queue(commands -> log.info("{} global commands loaded!", commands.size()),
                DiscordAPIException::new);

        long timer = TimeUnit.MINUTES.toMillis(10);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            @SuppressWarnings("ConstantConditions")
            public void run() {
                Random random = new Random(new Date().getTime());
                event.getJDA().getShardManager().setActivity(Activity.playing(WATCHLIST.get(random.nextInt(WATCHLIST.size()))));
            }
        }, timer, timer);
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        User theBot = event.getJDA().getSelfUser();

        upsertGuildCommands(event);
        DatabaseManager.INSTANCE.addGuild(event.getGuild());

        EmbedBuilder eb = new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setAuthor(theBot.getName(), null, theBot.getAvatarUrl())
            .setDescription("> *What am I doing here?*\n> *Why am I here?*\n> *Am I supposed to be here?*");
        Objects.requireNonNull(event.getGuild().getDefaultChannel()).asTextChannel().sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        Guild guild = event.getGuild();

        log.info("I have left {} [{}]!", guild.getName(), guild.getId());
        DatabaseManager.INSTANCE.removeGuild(guild);
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        if (event.getUser().isBot()) return;
        DatabaseManager.INSTANCE.removeUser(event.getUser().getId(), event.getGuild());
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        upsertGuildCommands(event);
        DatabaseManager.INSTANCE.addGuild(event.getGuild());
    }

    private void upsertGuildCommands(@NotNull GenericGuildEvent event) {
        Guild guild = event.getGuild();
        CommandListUpdateAction updateAction = guild.updateCommands()
            .addCommands(slashManager.getCommandData(guild))
            .addCommands(messageManager.getCommandData(guild))
            .addCommands(userManager.getCommandData(guild));

        if (Config.isTestingMode()) {
            updateAction.addCommands(globalCommandData)
                .queue(commands -> log.info("{} global commands loaded as guild commands on {}[{}]", commands.size(), guild.getName(), guild.getId()),
                    DiscordAPIException::new);
            return;
        }

        updateAction.queue(commands -> log.info("{} guild commands loaded onto {}[{}]", commands.size(), guild.getName(), guild.getId()),
            DiscordAPIException::new);
    }
}
