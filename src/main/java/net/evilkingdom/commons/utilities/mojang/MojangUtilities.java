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

}
