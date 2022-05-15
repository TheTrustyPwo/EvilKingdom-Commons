package net.evilkingdom.commons.utilities.luckperms;

/*
 * Made with love by https://kodirati.com/.
 */

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;

import java.security.Permission;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LuckPermsUtilities {

    /**
     * Allows you to a player's rank from a uuid.
     * This should only be used if the player is online (they'll be cached).
     * Uses LuckPerms' API.
     *
     * @param uuid ~ The player's uuid.
     * @return The player's rank.
     */
    public static String getRankViaCache(final UUID uuid) {
        return LuckPermsProvider.get().getUserManager().getUser(uuid).getCachedData().getMetaData().getPrimaryGroup();
    }

    /**
     * Allows you to a player's prefix from a uuid.
     * This should only be used if the player is online (they'll be cached).
     * Uses LuckPerms' API.
     *
     * @param uuid ~ The player's uuid.
     * @return The player's prefix.
     */
    public static String getPrefixViaCache(final UUID uuid) {
        return LuckPermsProvider.get().getUserManager().getUser(uuid).getCachedData().getMetaData().getPrefix();
    }

    /**
     * Allows you to a player's suffix from a uuid.
     * This should only be used if the player is online (they'll be cached).
     * Uses LuckPerms' API.
     *
     * @param uuid ~ The player's uuid.
     * @return The player's suffix.
     */
    public static String getSuffixViaCache(final UUID uuid) {
        return LuckPermsProvider.get().getUserManager().getUser(uuid).getCachedData().getMetaData().getSuffix();
    }

    /**
     * Allows you to a player's permissions from a uuid.
     * This should only be used if the player is online (they'll be cached).
     * Uses LuckPerms' API.
     *
     * @param uuid ~ The player's uuid.
     * @return The player's permissions.
     */
    public static ArrayList<String> getPermissionsViaCache(final UUID uuid) {
        return new ArrayList<String>(LuckPermsProvider.get().getUserManager().getUser(uuid).getCachedData().getPermissionData().getPermissionMap().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList());
    }

    /**
     * Allows you to a player's rank from a uuid.
     * This should only be used if the is not cached (or you're just being safe).
     * Uses LuckPerms' API.
     *
     * @param uuid ~ The player's uuid.
     * @return The player's rank.
     */
    public static CompletableFuture<String> getRank(final UUID uuid) {
        return LuckPermsProvider.get().getUserManager().loadUser(uuid).thenApply(user -> user.getCachedData().getMetaData().getPrimaryGroup());
    }

    /**
     * Allows you to a player's prefix from a uuid.
     * This should only be used if the is not cached (or you're just being safe).
     * Uses LuckPerms' API.
     *
     * @param uuid ~ The player's uuid.
     * @return The player's prefix.
     */
    public static CompletableFuture<String> getPrefix(final UUID uuid) {
        return LuckPermsProvider.get().getUserManager().loadUser(uuid).thenApply(user -> user.getCachedData().getMetaData().getPrefix());
    }

    /**
     * Allows you to a player's suffix from a uuid.
     * This should only be used if the is not cached (or you're just being safe).
     * Uses LuckPerms' API.
     *
     * @param uuid ~ The player's uuid.
     * @return The player's suffix.
     */
    public static CompletableFuture<String> getSuffix(final UUID uuid) {
        return LuckPermsProvider.get().getUserManager().loadUser(uuid).thenApply(user -> user.getCachedData().getMetaData().getSuffix());
    }

    /**
     * Allows you to a player's permissions from a uuid.
     * This should only be used if the is not cached (or you're just being safe).
     * Uses LuckPerms' API.
     *
     * @param uuid ~ The player's uuid.
     * @return The player's permissions.
     */
    public static CompletableFuture<ArrayList<String>> getPermissions(final UUID uuid) {
        return LuckPermsProvider.get().getUserManager().loadUser(uuid).thenApply(user -> new ArrayList<String>(user.getCachedData().getPermissionData().getPermissionMap().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList()));
    }

}
