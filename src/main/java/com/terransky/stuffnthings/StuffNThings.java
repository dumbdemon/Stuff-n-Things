package com.terransky.stuffnthings;

import com.terransky.stuffnthings.listeners.InteractionListener;
import com.terransky.stuffnthings.listeners.ListeningForEvents;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.Date;
import java.util.Random;

public class StuffNThings {

    private static final Config.Credentials TOKEN = Config.Credentials.DISCORD;

    public static void main(String[] args) {
        if (TOKEN.isDefault())
            throw new IllegalArgumentException("Unable to start bot. No bot token was set.");

        DefaultShardManagerBuilder shards = DefaultShardManagerBuilder.createDefault(TOKEN.getPassword())
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
            new ListeningForEvents(),
            new InteractionListener()
        );
    }
}
