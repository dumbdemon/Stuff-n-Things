package com.terransky.StuffnThings;

import com.terransky.StuffnThings.buttonSystem.ButtonManager;
import com.terransky.StuffnThings.commandSystem.CommandManager;
import com.terransky.StuffnThings.commandSystem.MessageContextManager;
import com.terransky.StuffnThings.commandSystem.UserContextManager;
import com.terransky.StuffnThings.database.SQLiteDataSource;
import com.terransky.StuffnThings.listeners.ListeningForEvents;
import com.terransky.StuffnThings.modalSystem.ModalManager;
import com.terransky.StuffnThings.selectMenuSystem.SelectMenuManager;
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
import java.util.Random;

public class core {
    private final Dotenv config;

    {
        config = Commons.config;
    }

    public core() throws LoginException, SQLException {
        if (config.get("TESTING_MODE").equals("true")) SQLiteDataSource.getConnection();

        DefaultShardManagerBuilder shards = DefaultShardManagerBuilder.createDefault(config.get("TOKEN"))
            .enableIntents(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
            )
            .enableCache(CacheFlag.ONLINE_STATUS)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .setChunkingFilter(ChunkingFilter.ALL);

        String[] whatAmIWatching = secretsAndLies.whatAmIWatching;

        if (config.get("TESTING_MODE").equals("true")) {
            shards.setStatus(OnlineStatus.INVISIBLE);
        } else {
            Random random = new Random();
            shards.setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.watching(whatAmIWatching[random.nextInt(whatAmIWatching.length)]));
        }
        ShardManager shardManager = shards.build();

        shardManager.addEventListener(
            new ListeningForEvents(),
            new CommandManager(),
            new ButtonManager(),
            new ModalManager(),
            new SelectMenuManager(),
            new MessageContextManager(),
            new UserContextManager()
        );
    }

    public static void main(String @NotNull [] args) throws LoginException, SQLException {
        new core();
    }
}
