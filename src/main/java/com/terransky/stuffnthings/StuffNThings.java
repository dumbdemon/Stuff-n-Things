package com.terransky.stuffnthings;

import com.terransky.stuffnthings.buttonSystem.ButtonManager;
import com.terransky.stuffnthings.commandSystem.CommandManager;
import com.terransky.stuffnthings.commandSystem.MessageContextManager;
import com.terransky.stuffnthings.commandSystem.UserContextManager;
import com.terransky.stuffnthings.database.SQLiteDataSource;
import com.terransky.stuffnthings.listeners.ListeningForEvents;
import com.terransky.stuffnthings.modalSystem.ModalManager;
import com.terransky.stuffnthings.selectMenuSystem.SelectMenuManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Random;

public class StuffNThings {
    private final Dotenv config;

    {
        config = Commons.CONFIG;
    }

    public StuffNThings() throws LoginException, SQLException, ParseException {
        if (Commons.ENABLE_DATABASE) SQLiteDataSource.getConnection();

        DefaultShardManagerBuilder shards = DefaultShardManagerBuilder.createDefault(config.get("TOKEN"))
            .enableIntents(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_PRESENCES
            )
            .enableCache(CacheFlag.ONLINE_STATUS)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .setChunkingFilter(ChunkingFilter.ALL);

        String[] whatAmIWatching = secretsAndLies.whatAmIWatching;

        if (Commons.IS_TESTING_MODE) {
            shards.setStatus(OnlineStatus.INVISIBLE);
        } else {
            Random random = new Random();
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
            new SelectMenuManager(),
            new UserContextManager()
        );
    }

    public static void main(String @NotNull [] args) throws LoginException, SQLException, ParseException {
        new StuffNThings();
    }
}
