package net.evilkingdom.commons.utilities.luckperms;

/*
 * Made with love by https://kodirati.com/.
 */

import net.luckperms.api.LuckPermsProvider;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPermsUtilities {

    public static CompletableFuture<ArrayList<String>> getPermissions(final UUID uuid) {
        return LuckPermsProvider.get().getUserManager().loadUser(uuid).thenApply(user -> {
            final ArrayList<String> permissions = new ArrayList<String>();
            user.getCachedData().getPermissionData().getPermissionMap().forEach((permission, state) -> {
                if (state) {
                    permissions.add(permission);
                }
            });
            return permissions;
        });
    }

    public static CompletableFuture<ArrayList<String>> getRanks(final UUID uuid) {
        return LuckPermsProvider.get().getUserManager().loadUser(uuid).thenCompose(user -> getPermissions(uuid).thenApply(permissions -> {
            final ArrayList<String> ranks = new ArrayList<String>();
            permissions.forEach(permission -> {
                if (permission.startsWith("group.")) {
                    ranks.add(permission.replaceFirst("group.", ""));
                }
            });
            return ranks;
        }));
    }

}
