package net.evilkingdom.commons.utilities.mojang;

/*
 * Made with love by https://kodirati.com/.
 */

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MojangUtilities {

    /**
     * Allows you to retrieve a UUID from a player's name.
     * Uses Mojang's API, website magic, and runs asynchronously in order to keep the server from lagging.
     *
     * @param name ~ The player's name.
     * @return The player's name if the UUID exists- if it doesn't it will return an empty optional.
     */
    public static CompletableFuture<Optional<UUID>> getUUID(final String name) {
        final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        final HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + name)).GET().build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(httpResponse -> {
            if (httpResponse.body().isEmpty()) {
                return Optional.empty();
            }
            final JsonObject jsonObject = JsonParser.parseString(httpResponse.body()).getAsJsonObject();
            return Optional.of(UUID.fromString(jsonObject.get("id").getAsString().replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5")));
        });
    }

    /**
     * Allows you to retrieve a server's player count.
     * Uses MCSrvstat's API, website magic, and runs asynchronously in order to keep the server from lagging.
     *
     * @param ip ~ The server's ip.
     * @param port ~ The server's port.
     * @return If server's player count if the server is online- if it isn't it'll return an empty optional.
     */
    public static CompletableFuture<Optional<Integer>> getPlayerCount(final String ip, final int port) {
        final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        final HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("https://api.mcsrvstat.us/2/" + ip + ":" + port)).GET().build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(httpResponse -> {
            if (httpResponse.body().isEmpty()) {
                return Optional.empty();
            }
            final JsonObject jsonObject = JsonParser.parseString(httpResponse.body()).getAsJsonObject();
            if (!jsonObject.get("online").getAsBoolean()) {
                return Optional.empty();
            }
            return Optional.of(jsonObject.get("players").getAsJsonObject().get("online").getAsInt());
        });
    }

}
