package com.terransky.stuffnthings;

import com.terransky.stuffnthings.buttonSystem.ButtonManager;
import com.terransky.stuffnthings.commandSystem.CommandManager;
import com.terransky.stuffnthings.commandSystem.MessageContextManager;
import com.terransky.stuffnthings.commandSystem.UserContextManager;
import com.terransky.stuffnthings.database.SQLiteDataSource;
import com.terransky.stuffnthings.listeners.ListeningForEvents;
import com.terransky.stuffnthings.modalSystem.ModalManager;
import com.terransky.stuffnthings.selectMenuSystem.EntitySelectMenuManager;
import com.terransky.stuffnthings.selectMenuSystem.StringSelectMenuManager;
import com.terransky.stuffnthings.utilities.Config;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

public class StuffNThings {
    public static void main(String[] args) throws SQLException {
        if (Config.isDatabaseEnabled()) SQLiteDataSource.getConnection();

        DefaultShardManagerBuilder shards = DefaultShardManagerBuilder.createDefault(Config.getToken())
            .enableIntents(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_PRESENCES
            )
            .enableCache(CacheFlag.ONLINE_STATUS)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .setChunkingFilter(ChunkingFilter.ALL);

        String[] whatAmIWatching = secretsAndLies.whatAmIWatching;

        if (Config.isTestingMode()) {
            shards.setStatus(OnlineStatus.INVISIBLE);
        } else {
            Random random = new Random(new Date().getTime());
            shards.setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.watching(whatAmIWatching[random.nextInt(whatAmIWatching.length)]));
        }
        ShardManager shardManager = shards.build();

        shardManager.addEventListener(
            new ButtonManager(),
            new CommandManager(),
            new ListeningForEvents(),
            new MessageContextManager(),
            new ModalManager(),
            new StringSelectMenuManager(),
            new EntitySelectMenuManager(),
            new UserContextManager()
        );
    }
}
