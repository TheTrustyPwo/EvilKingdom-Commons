package net.evilkingdom.commons.utilities.pterodactyl;

/*
 * Made with love by https://kodirati.com/.
 */

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import joptsimple.OptionException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PterodactylUtilities {

    /**
     * Allows you to retrieve a server's file names in a directory.
     * Uses Pterodactyl's API, website magic, hella file magic, and runs asynchronously in order to keep the server from lagging.
     *
     * @param url ~ The panel's url.
     * @param token ~ A client token that has administrator rights on the panel.
     * @param id ~ The server's id.
     * @param path ~ The path.
     * @return The server's file name's from a path if all goes to plan- if it doesn't it will return an empty optional.
     */
    public static CompletableFuture<Optional<ArrayList<String>>> getFileNames(final String url, final String token, final String id, final String path) {
        final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        final HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("https://" + url + "/api/client/servers/" + id + "/list?directory=" + URLEncoder.encode(path, StandardCharsets.UTF_8))).header("Accept", "application/json").header("Content-Type", "application/json").header("Authorization", "Bearer " + token).GET().build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(httpResponse -> {
            if (httpResponse.body().isEmpty()) {
                return Optional.empty();
            }
            final JsonObject jsonObject = JsonParser.parseString(httpResponse.body()).getAsJsonObject();
            if (!jsonObject.has("data")) {
                return Optional.empty();
            }
            final ArrayList<String> fileNames = new ArrayList<String>();
            jsonObject.get("data").getAsJsonArray().forEach(jsonArrayedObject -> fileNames.add(jsonArrayedObject.getAsJsonObject().get("attributes").getAsJsonObject().get("name").getAsString()));
            return Optional.of(fileNames);
        });
    }

    /**
     * Allows you to retrieve a server's file.
     * Uses Pterodactyl's API, website magic, hella file magic, and runs asynchronously in order to keep the server from lagging.
     *
     * @param url ~ The panel's url.
     * @param token ~ A client token that has administrator rights on the panel.
     * @param id ~ The server's id.
     * @param path ~ The path of the file.
     * @return The server's file from a path if all goes to plan- if it doesn't it will return an empty optional.
     */
    public static CompletableFuture<Optional<File>> getFile(final String url, final String token, final String id, final Path path) {
        final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        final HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("https://" + url + "/api/client/servers/" + id + "/download?file=" + URLEncoder.encode(path.toString(), StandardCharsets.UTF_8))).header("Accept", "application/json").header("Content-Type", "application/json").header("Authorization", "Bearer " + token).GET().build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApplyAsync(httpResponse -> {
            if (httpResponse.body().isEmpty()) {
                return Optional.empty();
            }
            final JsonObject jsonObject = JsonParser.parseString(httpResponse.body()).getAsJsonObject();
            if (!jsonObject.has("attributes")) {
                return Optional.empty();
            }
            final String fileName = path.toString().substring(path.toString().lastIndexOf(File.separator) + 1);
            final File targetFile = new File("ptecom_downloads", fileName);
            try {
                FileUtils.copyURLToFile(new URL(jsonObject.get("attributes").getAsJsonObject().get("url").getAsString()), targetFile);
            } catch (final IOException ioException) {
                //Does nothing, just in case! :)
            }
            while (!targetFile.exists()) {
                //This halts the reply until the target file exists.
            }
            return Optional.of(targetFile);
        });
    }

    /**
     * Allows you to retrieve a server file's contents.
     * Uses Pterodactyl's API, website magic, hella file magic, and runs asynchronously in order to keep the server from lagging.
     *
     * @param url ~ The panel's url.
     * @param token ~ A client token that has administrator rights on the panel.
     * @param id ~ The server's id.
     * @param file ~ The file to get the contents of.
     * @return The server's file contents from a path if all goes to plan- if it doesn't it will return an empty optional.
     */
    public static CompletableFuture<Optional<String>> getFileContents(final String url, final String token, final String id, final File file) {
        final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        final HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("https://" + url + "/api/client/servers/" + id + "/contents?file=" + URLEncoder.encode(file.toPath().toString(), StandardCharsets.UTF_8))).header("Accept", "application/json").header("Content-Type", "application/json").header("Authorization", "Bearer " + token).GET().build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(httpResponse -> {
            if (httpResponse.body().isEmpty() || httpResponse.body().contains("error")) {
                return Optional.empty();
            }
            return Optional.of(httpResponse.body());
        });
    }

    /**
     * Allows you to delete a server's file.
     * Uses Pterodactyl's API, website magic, hella file magic, and runs asynchronously in order to keep the server from lagging.
     *
     * @param url ~ The panel's url.
     * @param token ~ A client token that has administrator rights on the panel.
     * @param id ~ The server's id.
     * @param file ~ The file to delete.
     * @return The file deletions success state if all goes to plan- if it doesn't it will return an empty optional.
     */
    public static CompletableFuture<Optional<Boolean>> deleteFile(final String url, final String token, final String id, final File file) {
        final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        final HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("https://" + url + "/api/client/servers/" + id + "/delete?file=" + URLEncoder.encode(file.toPath().toString(), StandardCharsets.UTF_8))).header("Accept", "application/json").header("Content-Type", "application/json").header("Authorization", "Bearer " + token).GET().build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(httpResponse -> {
            if (httpResponse.body().isEmpty() || httpResponse.body().contains("error")) {
                return Optional.empty();
            }
            return Optional.of(true);
        });
    }

    /**
     * Allows you to upload a file to a server.
     * Uses Pterodactyl's API, website magic, hella file magic, and runs asynchronously in order to keep the server from lagging.
     *
     * @param url ~ The panel's url.
     * @param token ~ A client token that has administrator rights on the panel.
     * @param id ~ The server's id.
     * @param file ~ The file to upload.
     * @param targetDirectory ~ The file's target directory.
     * @return The file uploads success state if all goes to plan- if it doesn't it will return an empty optional.
     */
    public static CompletableFuture<Optional<Boolean>> uploadFile(final String url, final String token, final String id, final File file, final File targetDirectory) {
        if (!file.exists()) {
            return CompletableFuture.supplyAsync(() -> Optional.empty());
        }
        final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        final HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("https://" + url + "/api/client/servers/" + id + "/files/upload?directory=" + URLEncoder.encode(targetDirectory.toPath().toString(), StandardCharsets.UTF_8))).header("Accept", "application/json").header("Content-Type", "application/json").header("Authorization", "Bearer " + token).GET().build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenCompose(httpResponse -> {
            if (httpResponse.body().isEmpty()) {
                return CompletableFuture.supplyAsync(() -> Optional.empty());
            }
            final JsonObject jsonObject = JsonParser.parseString(httpResponse.body()).getAsJsonObject();
            if (!jsonObject.has("attributes")) {
                return CompletableFuture.supplyAsync(() -> Optional.empty());
            }
            final String uploadURL = jsonObject.get("attributes").getAsJsonObject().get("url").getAsString().replace("\\", "");
            HttpRequest uploadHttpRequest = null;
            try {
                uploadHttpRequest = HttpRequest.newBuilder().uri(URI.create(uploadURL)).POST(HttpRequest.BodyPublishers.ofFile(file.toPath())).build();
            } catch (final IOException ioException) {
                //Does nothing, just in case! :)
            }
            return httpClient.sendAsync(uploadHttpRequest, HttpResponse.BodyHandlers.ofString()).thenApply((uploadHttpResponse) -> Optional.of(true));
        });
    }

    /**
     * Allows you to write to a server's file.
     * Uses Pterodactyl's API, website magic, hella file magic, and runs asynchronously in order to keep the server from lagging.
     *
     * @param url ~ The panel's url.
     * @param token ~ A client token that has administrator rights on the panel.
     * @param id ~ The server's id.
     * @param file ~ The file to write to.
     * @param message ~ The message to write.
     * @return The file writes success state if all goes to plan- if it doesn't it will return an empty optional.
     */
    public static CompletableFuture<Optional<Boolean>> writeFile(final String url, final String token, final String id, final File file, final String message) {
        if (!file.exists()) {
            return CompletableFuture.supplyAsync(() -> Optional.empty());
        }
        final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        final HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("https://" + url + "/api/client/servers/" + id + "/files/write?file=" + URLEncoder.encode(file.toPath().toString(), StandardCharsets.UTF_8))).header("Accept", "application/json").header("Content-Type", "application/json").header("Authorization", "Bearer " + token).POST(HttpRequest.BodyPublishers.ofString(message)).build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(httpResponse -> {
            if (httpResponse.body().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(true);
        });
    }

    /**
     * Allows you to retrieve a server's status.
     * Uses Pterodactyl's API, website magic, and runs asynchronously in order to keep the server from lagging.
     *
     * @param url ~ The panel's url.
     * @param token ~ A client token that has administrator rights on the panel.
     * @param id ~ The server's id.
     * @return The server's status if all goes to plan- if it doesn't it will return an empty optional.
     */
    public static CompletableFuture<Optional<String>> getStatus(final String url, final String token, final String id) {
        final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        final HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("https://" + url + "/api/client/servers/" + id + "/resources")).header("Accept", "application/json").header("Content-Type", "application/json").header("Authorization", "Bearer " + token).GET().build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(httpResponse -> {
            if (httpResponse.body().isEmpty()) {
                return Optional.empty();
            }
            final JsonObject jsonObject = JsonParser.parseString(httpResponse.body()).getAsJsonObject();
            if (!jsonObject.has("attributes")) {
                return Optional.empty();
            }
            return Optional.of(jsonObject.get("attributes").getAsJsonObject().get("current_state").getAsString());
        });
    }

}
