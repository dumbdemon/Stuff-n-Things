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

import java.util.ArrayList;
import java.util.List;

public class ChannelPermsController<T extends IPermissionHolder> {

    private final T iPermissionHolder;
    private final GuildChannel guildChannel;
    private PermissionOverride permissionOverride;

    public ChannelPermsController(T iPermissionHolder, @NotNull GuildChannel guildChannel) {
        this.iPermissionHolder = iPermissionHolder;
        this.guildChannel = guildChannel;
        this.permissionOverride = getPermissionOverride(iPermissionHolder, guildChannel.getPermissionContainer());
    }

    /**
     * Gets a {@link PermissionOverride} from a {@link List}.
     *
     * @param iPermissionHolder An {@link Role} or {@link net.dv8tion.jda.api.entities.Member Member}.
     * @param container         An {@link IPermissionContainer}.
     * @return A {@link PermissionOverride} or null.
     */
    @Nullable
    private PermissionOverride getPermissionOverride(T iPermissionHolder, @NotNull IPermissionContainer container) {
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
     * @param permission      {@link Permission} to reset.
     * @param morePermissions Additional {@link Permission}s
     * @return False if {@link PermissionOverride} is null; otherwise true.
     */
    public boolean resetChannelPerms(Permission permission, Permission... morePermissions) {
        List<Permission> permissions = new ArrayList<>(List.of(permission));
        if (morePermissions != null) permissions.addAll(List.of(morePermissions));

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
     * @param permissions {@link Permission}s to grant.
     * @return False if target channel already has the provided permissions granted for {@link IPermissionHolder}; otherwise true.
     */
    public boolean grantChannelPerms(@NotNull Permission... permissions) {
        if (iPermissionHolder.hasPermission(guildChannel, permissions))
            return false;

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
     * @param permissions {@link Permission}s to deny.
     * @return False if target channel already has the provided permissions denied for {@link IPermissionHolder}; otherwise true.
     */
    public boolean denyChannelPerms(@NotNull Permission... permissions) {
        if (!iPermissionHolder.hasPermission(guildChannel, permissions))
            return false;

        if (permissionOverride == null) {
            permissionOverride = guildChannel.getPermissionContainer().upsertPermissionOverride(iPermissionHolder).complete();
        }

        PermissionOverrideAction permAction = permissionOverride.getManager();
        permAction.deny(permissions).queue();
        return true;
    }
}
