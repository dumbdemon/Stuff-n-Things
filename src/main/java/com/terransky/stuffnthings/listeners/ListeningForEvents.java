package com.terransky.stuffnthings.listeners;

import com.terransky.stuffnthings.Managers;
import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.utilities.command.BotColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ListeningForEvents extends ListenerAdapter {
    private final Logger log = LoggerFactory.getLogger(ListeningForEvents.class);

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        JDA jda = event.getJDA();
        if (StuffNThings.getConfig().getCore().getTestingMode()) {
            jda.updateCommands().queue();
            return;
        }

        jda.updateCommands()
            .addCommands((new Managers.SlashCommands()).getCommandData())
            .addCommands((new Managers.UserContextMenu()).getCommandData())
            .addCommands((new Managers.MessageContextMenu()).getCommandData())
            .queue(commands -> log.info("{} global commands loaded!", commands.size()), DiscordAPIException::new);

        long timer = TimeUnit.MINUTES.toMillis(10);
        new Timer().scheduleAtFixedRate(new SetWatcherTask(jda.getShardManager()), timer, timer);
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        User theBot = event.getJDA().getSelfUser();

        upsertGuildCommands(event);
        DatabaseManager.INSTANCE.addGuild(event.getGuild());

        EmbedBuilder eb = new EmbedBuilder()
            .setColor(BotColors.DEFAULT.getColor())
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
            .addCommands((new Managers.SlashCommands()).getCommandData(guild))
            .addCommands((new Managers.UserContextMenu()).getCommandData(guild))
            .addCommands((new Managers.MessageContextMenu()).getCommandData(guild));

        if (StuffNThings.getConfig().getCore().getTestingMode()) {
            updateAction.addCommands((new Managers.SlashCommands()).getCommandData())
                .addCommands((new Managers.UserContextMenu()).getCommandData())
                .addCommands((new Managers.MessageContextMenu()).getCommandData())
                .queue(commands -> log.info("{} global commands loaded as guild commands on {}[{}]", commands.size(), guild.getName(), guild.getId()),
                    DiscordAPIException::new);
            return;
        }

        updateAction.queue(commands -> log.info("{} guild commands loaded onto {}[{}]", commands.size(), guild.getName(), guild.getId()),
            DiscordAPIException::new);
    }

    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
        DatabaseManager.INSTANCE.botBan(event.getUser(), true);
    }

    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent event) {
        DatabaseManager.INSTANCE.removeBotBan(event.getUser());
    }

    public static class SetWatcherTask extends TimerTask {

        private final ShardManager shardManager;
        private final List<String> watchList;

        public SetWatcherTask(ShardManager shardManager) {
            this.shardManager = shardManager;
            this.watchList = DatabaseManager.INSTANCE.getWatchList();
        }

        @Override
        public void run() {
            Random random = new Random(new Date().getTime());
            shardManager.setActivity(Activity.watching(watchList.get(random.nextInt(watchList.size()))));
        }
    }
}
