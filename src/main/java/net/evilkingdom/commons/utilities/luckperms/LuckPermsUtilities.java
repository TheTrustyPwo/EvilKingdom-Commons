package net.evilkingdom.commons.utilities.luckperms;

/*
 * Made with love by https://kodirati.com/.
 */

import net.luckperms.api.LuckPermsProvider;

import java.security.Permission;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPermsUtilities {

    public static ArrayList<String> getPermissionsViaCache(final UUID uuid) {
        final ArrayList<String> permissions = new ArrayList<String>();
        LuckPermsProvider.get().getUserManager().getUser(uuid).getCachedData().getPermissionData().getPermissionMap().forEach((permission, state) -> {
            if (state) {
                permissions.add(permission);
            }
        });
        return permissions;
    }

    public static ArrayList<String> getRanksViaCache(final UUID uuid) {
        return new ArrayList<String>(getPermissionsViaCache(uuid).stream().filter(permission -> permission.startsWith("group.")).toList());
    }

    public static CompletableFuture<ArrayList<String>> getPermissions(final UUID uuid) {
        return LuckPermsProvider.get().getUserManager().loadUser(uuid).thenApply(user -> {
            final ArrayList<String> permissions = new ArrayList<String>();
            LuckPermsProvider.get().getUserManager().getUser(uuid).getCachedData().getPermissionData().getPermissionMap().forEach((permission, state) -> {
                if (state) {
                    permissions.add(permission);
                }
            });
            return permissions;
        });
    }

    public static CompletableFuture<ArrayList<String>> getRanks(final UUID uuid) {
        return LuckPermsProvider.get().getUserManager().loadUser(uuid).thenCompose(user -> getPermissions(uuid).thenApply(permissions -> new ArrayList<String>(permissions.stream().filter(permission -> permission.startsWith("group.")).toList())));
    }

}
