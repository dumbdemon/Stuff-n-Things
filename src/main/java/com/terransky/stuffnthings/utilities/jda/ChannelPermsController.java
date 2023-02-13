package com.terransky.stuffnthings.utilities.jda;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class ChannelPermsController {

    private final GuildChannel guildChannel;

    public ChannelPermsController(@NotNull GuildChannel guildChannel) {
        this.guildChannel = guildChannel;
    }

    /**
     * Gets a {@link PermissionOverride} from a {@link List}.
     *
     * @param iPermissionHolder A {@link net.dv8tion.jda.api.entities.Role Role} or {@link net.dv8tion.jda.api.entities.Member Member}.
     * @param container         An {@link IPermissionContainer}.
     * @return A {@link Optional} of a {@link PermissionOverride}.
     */
    @NotNull
    private <T extends IPermissionHolder> Optional<PermissionOverride> getPermissionOverride(T iPermissionHolder, @NotNull IPermissionContainer container) {
        return container.getPermissionOverrides().stream()
            .filter(override -> iPermissionHolder.equals(override.getMember()) || iPermissionHolder.equals(override.getRole()))
            .findFirst();
    }

    /**
     * Reset all provided permissions to the inherent state.
     *
     * @param iPermissionHolder A {@link net.dv8tion.jda.api.entities.Role Role} or {@link net.dv8tion.jda.api.entities.Member Member}.
     * @param permissions       {@link Permission}s to reset
     * @return False if {@link PermissionOverride} is null; otherwise true.
     */
    public <T extends IPermissionHolder> boolean resetChannelPerms(T iPermissionHolder, Permission... permissions) {
        Optional<PermissionOverride> permissionOverride = getPermissionOverride(iPermissionHolder, guildChannel.getPermissionContainer());

        if (permissionOverride.isEmpty())
            return false;

        permissionOverride.get().getManager().clear(permissions).queue();
        return true;
    }

    /**
     * Grant permissions on a channel to a role or member.
     *
     * @param iPermissionHolder A {@link net.dv8tion.jda.api.entities.Role Role} or {@link net.dv8tion.jda.api.entities.Member Member}.
     * @param permissions       {@link Permission}s to grant.
     * @return False if target channel already has the provided permissions granted for {@link IPermissionHolder}; otherwise true.
     */
    public <T extends IPermissionHolder> boolean grantChannelPerms(@NotNull T iPermissionHolder, @NotNull Permission... permissions) throws ExecutionException, InterruptedException {
        if (iPermissionHolder.hasPermission(guildChannel, permissions))
            return false;

        PermissionOverride permissionOverride = getPermissionOverride(iPermissionHolder, guildChannel.getPermissionContainer())
            .orElse(guildChannel.getPermissionContainer().upsertPermissionOverride(iPermissionHolder).submit().get());

        permissionOverride.getManager().grant(permissions).queue();
        return true;
    }

    /**
     * Deny permissions on a channel to a role or member.
     *
     * @param iPermissionHolder A {@link net.dv8tion.jda.api.entities.Role Role} or {@link net.dv8tion.jda.api.entities.Member Member}.
     * @param permissions       {@link Permission}s to deny.
     * @return False if target channel already has the provided permissions denied for {@link IPermissionHolder}; otherwise true.
     */
    public <T extends IPermissionHolder> boolean denyChannelPerms(@NotNull T iPermissionHolder, @NotNull Permission... permissions) throws ExecutionException, InterruptedException {
        if (!iPermissionHolder.hasPermission(guildChannel, permissions))
            return false;

        PermissionOverride permissionOverride = getPermissionOverride(iPermissionHolder, guildChannel.getPermissionContainer())
            .orElse(guildChannel.getPermissionContainer().upsertPermissionOverride(iPermissionHolder).submit().get());

        permissionOverride.getManager().deny(permissions).queue();
        return true;
    }
}
