package com.terransky.StuffnThings;

import com.terransky.StuffnThings.buttonSystem.ButtonManager;
import com.terransky.StuffnThings.database.SQLiteDataSource;
import com.terransky.StuffnThings.listeners.ListeningForEvents;
import com.terransky.StuffnThings.modalSystem.ModalManager;
import com.terransky.StuffnThings.selectMenuSystem.SelectMenuManager;
import com.terransky.StuffnThings.slashSystem.CommandManager;
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
import java.util.Objects;

@SuppressWarnings("ALL")
public class core {
    private final Dotenv config = Dotenv.configure().load();

    public core() throws LoginException, SQLException {
        SQLiteDataSource.getConnection();

        DefaultShardManagerBuilder shards = DefaultShardManagerBuilder.createDefault(config.get("TOKEN"))
                .enableIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.DIRECT_MESSAGES
                )
                .enableCache(CacheFlag.ONLINE_STATUS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL);

        String[] whatAmIWatching = new secretsAndLies().whatAmIWatching;

        if (Objects.equals(config.get("APP_ID"), config.get("TESTING_BOT"))) {
            shards.setStatus(OnlineStatus.INVISIBLE);
        } else {
            shards.setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setActivity(Activity.watching(whatAmIWatching[(int) (Math.random() * whatAmIWatching.length)]));
        }
        ShardManager shardManager = shards.build();

        shardManager.addEventListener(
                new ListeningForEvents(),
                new CommandManager(),
                new ButtonManager(),
                new ModalManager(),
                new SelectMenuManager()
        );
    }

    public static void main(String @NotNull [] args) throws LoginException, SQLException {
        new core();
    }
}
