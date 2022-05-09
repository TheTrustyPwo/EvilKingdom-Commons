package net.evilkingdom.commons.transmission.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.evilkingdom.commons.transmission.TransmissionImplementor;
import net.evilkingdom.commons.transmission.abstracts.TransmissionHandler;
import net.evilkingdom.commons.transmission.enums.TransmissionType;
import net.evilkingdom.commons.transmission.implementations.TransmissionTask;
import net.evilkingdom.commons.utilities.pterodactyl.PterodactylUtilities;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOption;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TransmissionSite {

    private final JavaPlugin plugin;

    private final String name, serverName, pterodactylURL, pterodactylToken;
    private final HashSet<TransmissionTask> tasks;
    private final HashSet<TransmissionServer> servers;
    private TransmissionHandler handler;
    private final HashMap<String, ArrayList<File>> directoryFiles;

    /**
     * Allows you to create a transmission site for a plugin.
     * Abuses Pterodactyl's API, file magic, and much more.
     *
     * @param plugin ~ The plugin the transmission site is for.
     * @param serverName ~ The name of the transmission site's server.
     * @param name ~ The name of the transmission site.
     * @param pterodactylURL ~ The pterodactyl panel's url.
     * @param pterodactylToken ~ A client token that has administrator rights on the pterodactyl panel.
     */
    public TransmissionSite(final JavaPlugin plugin, final String serverName, final String name, final String pterodactylURL, final String pterodactylToken) {
        this.plugin = plugin;

        this.serverName = serverName;
        this.name = name;
        this.pterodactylURL = pterodactylURL;
        this.pterodactylToken = pterodactylToken;
        this.tasks = new HashSet<TransmissionTask>();
        this.servers = new HashSet<TransmissionServer>();
        this.directoryFiles = new HashMap<String, ArrayList<File>>();
    }

    /**
     * Allows you to set the transmission site's handler.
     *
     * @param transmissionHandler ~ The transmission site's handler to set.
     */
    public void setHandler(final TransmissionHandler transmissionHandler) {
        this.handler = transmissionHandler;
    }

    /**
     * Allows you to retrieve the transmission site's name.
     *
     * @return ~ The transmission site's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Allows you to retrieve the transmission site's server name.
     *
     * @return ~ The transmission site's server name.
     */
    public String getServerName() {
        return this.serverName;
    }

    /**
     * Allows you to retrieve the transmission site's handler.
     *
     * @return ~ The transmission site's handler.
     */
    public TransmissionHandler getHandler() {
        return this.handler;
    }

    /**
     * Allows you to retrieve the transmission site's plugin.
     *
     * @return ~ The transmission site's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to retrieve the transmission site's tasks.
     *
     * @return ~ The transmission site's tasks.
     */
    public HashSet<TransmissionTask> getTasks() {
        return this.tasks;
    }

    /**
     * Allows you to retrieve the transmission site's servers.
     *
     * @return ~ The transmission site's servers.
     */
    public HashSet<TransmissionServer> getServers() {
        return this.servers;
    }

    /**
     * Allows you to retrieve the transmission site's pterodactyl token.
     *
     * @return ~ The transmission site' pterodactyl token.
     */
    public String getPterodactylToken() {
        return this.pterodactylToken;
    }

    /**
     * Allows you to retrieve the transmission site's pterodactyl url.
     *
     * @return ~ The transmission site's pterodactyl url.
     */
    public String getPterodactylURL() {
        return this.pterodactylURL;
    }

    /**
     * Allows you to register the transmission site.
     */
    public void register() {
        TransmissionImplementor implementor = TransmissionImplementor.get(this.plugin);
        implementor.getSites().add(this);
        final File dataFolder = this.plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        final File transmissionsFolder = new File(dataFolder, "transmissions");
        if (!transmissionsFolder.exists()) {
            transmissionsFolder.mkdirs();
        }
        final File siteFolder = new File(transmissionsFolder, this.name);
        if (!siteFolder.exists()) {
            siteFolder.mkdirs();
        }
        final File messagesFolder = new File(siteFolder, "messages");
        if (!messagesFolder.exists()) {
            messagesFolder.mkdirs();
        }
        final File requestsFolder = new File(siteFolder, "requests");
        if (!requestsFolder.exists()) {
            requestsFolder.mkdirs();
        }
        final File responsesFolder = new File(siteFolder, "responses");
        if (!responsesFolder.exists()) {
            responsesFolder.mkdirs();
        }
        Arrays.stream(messagesFolder.listFiles()).filter(file -> file.delete());
        Arrays.stream(requestsFolder.listFiles()).filter(file -> file.delete());
        Arrays.stream(responsesFolder.listFiles()).filter(file -> file.delete());
        this.directoryFiles.clear();
        this.directoryFiles.put("messages", new ArrayList<File>());
        this.directoryFiles.put("requests", new ArrayList<File>());
        this.directoryFiles.put("responses", new ArrayList<File>());
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            final HashMap<String, ArrayList<File>> previousDirectoryFiles = new HashMap<String, ArrayList<File>>(this.directoryFiles);
            this.directoryFiles.clear();
            this.directoryFiles.put("messages", new ArrayList<File>());
            this.directoryFiles.put("requests", new ArrayList<File>());
            this.directoryFiles.put("responses", new ArrayList<File>());
            Arrays.stream(messagesFolder.listFiles()).forEach(file -> {
                final ArrayList<File> previousMessagesFolderFiles = this.directoryFiles.get("messages");
                previousMessagesFolderFiles.add(file);
                this.directoryFiles.put("messages", previousMessagesFolderFiles);
            });
            Arrays.stream(requestsFolder.listFiles()).forEach(file -> {
                final ArrayList<File> previousRequestsFolderFiles = this.directoryFiles.get("requests");
                previousRequestsFolderFiles.add(file);
                this.directoryFiles.put("requests", previousRequestsFolderFiles);
            });
            Arrays.stream(responsesFolder.listFiles()).forEach(file -> {
                final ArrayList<File> previousResponsesFolderFiles = this.directoryFiles.get("responses");
                previousResponsesFolderFiles.add(file);
                this.directoryFiles.put("responses", previousResponsesFolderFiles);
            });
            final ArrayList<File> messagesFolderFiles = this.directoryFiles.get("messages");
            messagesFolderFiles.removeAll(previousDirectoryFiles.get("messages"));
            final ArrayList<File> requestsFolderFiles = this.directoryFiles.get("requests");
            requestsFolderFiles.removeAll(previousDirectoryFiles.get("requests"));
            final ArrayList<File> responsesFolderFiles = this.directoryFiles.get("responses");
            responsesFolderFiles.removeAll(previousDirectoryFiles.get("responses"));
            messagesFolderFiles.forEach(file -> {
                final UUID uuid = UUID.fromString(file.getName().replaceFirst(".json", ""));
                JsonObject jsonObject = null;
                try {
                    jsonObject = JsonParser.parseString(Files.readString(file.toPath())).getAsJsonObject();
                } catch (final IOException ioException) {
                    //Does nothing, just in case! :)
                }
                file.delete();
                final String serverName = jsonObject.get("serverName").getAsString();
                final TransmissionServer server = this.getServers().stream().filter(innerServer -> innerServer.getName().equals(serverName)).findFirst().get();
                final String siteName = jsonObject.get("siteName").getAsString();
                final String data = jsonObject.get("data").getAsString();
                this.handler.onReceive(server, siteName, TransmissionType.MESSAGE, uuid, data);
            });
            requestsFolderFiles.forEach(file -> {
                final UUID uuid = UUID.fromString(file.getName().replaceFirst(".json", ""));
                JsonObject jsonObject = null;
                try {
                    jsonObject = JsonParser.parseString(Files.readString(file.toPath())).getAsJsonObject();
                } catch (final IOException ioException) {
                    //Does nothing, just in case! :)
                }
                file.delete();
                final String serverName = jsonObject.get("serverName").getAsString();
                final TransmissionServer server = this.getServers().stream().filter(innerServer -> innerServer.getName().equals(serverName)).findFirst().get();
                final String siteName = jsonObject.get("siteName").getAsString();
                final String data = jsonObject.get("data").getAsString();
                this.handler.onReceive(server, siteName, TransmissionType.REQUEST, uuid, data);
            });
            responsesFolderFiles.forEach(file -> {
                final UUID uuid = UUID.fromString(file.getName().replaceFirst(".json", ""));
                JsonObject jsonObject = null;
                try {
                    jsonObject = JsonParser.parseString(Files.readString(file.toPath())).getAsJsonObject();
                } catch (final IOException ioException) {
                    //Does nothing, just in case! :)
                }
                file.delete();
                final String data = jsonObject.get("data").getAsString();
                final TransmissionTask task = this.tasks.stream().filter(innerTask -> innerTask.getUUID().toString().equals(uuid.toString())).findFirst().get();
                task.setResponseData(data);
                task.stop();
            });
        }, 0L, 1L);
    }

    /**
     * Allows you to unregister the transmission site.
     */
    public void unregister() {
        TransmissionImplementor implementor = TransmissionImplementor.get(this.plugin);
        implementor.getSites().remove(this);
        final File dataFolder = this.plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        final File transmissionsFolder = new File(dataFolder, "transmissions");
        if (!transmissionsFolder.exists()) {
            try {
                Files.walk(transmissionsFolder.toPath()).sorted(Comparator.reverseOrder()).map(path -> path.toFile()).forEach(file -> file.delete());
            } catch (final IOException ioException) {
                //Does nothing, just in case! :)
            }
        }
    }

}
