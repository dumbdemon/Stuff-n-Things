package com.terransky.stuffnthings.commandSystem.utilities;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChannelPermsController {

    private final GuildChannel guildChannel;

    public ChannelPermsController(@NotNull GuildChannel guildChannel) {
        this.guildChannel = guildChannel;
    }

    /**
     * Gets a {@link PermissionOverride} from a {@link List}.
     *
     * @param iPermissionHolder An {@link Role} or {@link net.dv8tion.jda.api.entities.Member Member}.
     * @param container         An {@link IPermissionContainer}.
     * @return A {@link PermissionOverride} or null.
     */
    @Nullable
    private <T extends IPermissionHolder> PermissionOverride getPermissionOverride(T iPermissionHolder, @NotNull IPermissionContainer container) {
        List<PermissionOverride> overrides = container.getPermissionOverrides();
        if (iPermissionHolder instanceof Role) {
            for (PermissionOverride override : overrides.stream().filter(PermissionOverride::isRoleOverride).toList()) {
                if (iPermissionHolder.equals(override.getRole())) {
                    return override;
                }
            }
        } else {
            for (PermissionOverride override : overrides.stream().filter(PermissionOverride::isMemberOverride).toList()) {
                if (iPermissionHolder.equals(override.getMember())) {
                    return override;
                }
            }
        }
        return null;
    }

    /**
     * Reset all provided permissions to the inherent state.
     * <p>
     * It is recommended that you check if the event has been acknowledged after this function.
     *
     * @param iPermissionHolder An {@link Role} or {@link net.dv8tion.jda.api.entities.Member Member}.
     * @param permissions       {@link Permission}s to reset
     * @return False if {@link PermissionOverride} is null; otherwise true.
     */
    public <T extends IPermissionHolder> boolean resetChannelPerms(T iPermissionHolder, Permission... permissions) {
        PermissionOverride permissionOverride = getPermissionOverride(iPermissionHolder, guildChannel.getPermissionContainer());

        if (permissionOverride == null)
            return false;

        PermissionOverrideAction permAction = permissionOverride.getManager();
        permAction.clear(permissions).queue();
        return true;
    }

    /**
     * Grant permissions on a channel to a role or member.
     * <p>
     * It is recommended that you check if the event has been acknowledged after this function.
     *
     * @param iPermissionHolder An {@link Role} or {@link net.dv8tion.jda.api.entities.Member Member}.
     * @param permissions       {@link Permission}s to grant.
     * @return False if target channel already has the provided permissions granted for {@link IPermissionHolder}; otherwise true.
     */
    public <T extends IPermissionHolder> boolean grantChannelPerms(@NotNull T iPermissionHolder, @NotNull Permission... permissions) {
        if (iPermissionHolder.hasPermission(guildChannel, permissions))
            return false;

        PermissionOverride permissionOverride = getPermissionOverride(iPermissionHolder, guildChannel.getPermissionContainer());

        if (permissionOverride == null) {
            permissionOverride = guildChannel.getPermissionContainer().upsertPermissionOverride(iPermissionHolder).complete();
        }

        PermissionOverrideAction permAction = permissionOverride.getManager();
        permAction.grant(permissions).queue();
        return true;
    }

    /**
     * Deny permissions on a channel to a role or member.
     * <p>
     * It is recommended that you check if the event has been acknowledged after this function.
     *
     * @param iPermissionHolder An {@link Role} or {@link net.dv8tion.jda.api.entities.Member Member}.
     * @param permissions       {@link Permission}s to deny.
     * @return False if target channel already has the provided permissions denied for {@link IPermissionHolder}; otherwise true.
     */
    public <T extends IPermissionHolder> boolean denyChannelPerms(@NotNull T iPermissionHolder, @NotNull Permission... permissions) {
        if (!iPermissionHolder.hasPermission(guildChannel, permissions))
            return false;

        PermissionOverride permissionOverride = getPermissionOverride(iPermissionHolder, guildChannel.getPermissionContainer());

        if (permissionOverride == null) {
            permissionOverride = guildChannel.getPermissionContainer().upsertPermissionOverride(iPermissionHolder).complete();
        }

        PermissionOverrideAction permAction = permissionOverride.getManager();
        permAction.deny(permissions).queue();
        return true;
    }
}
