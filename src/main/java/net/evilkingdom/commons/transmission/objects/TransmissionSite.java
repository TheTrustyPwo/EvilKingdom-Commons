package net.evilkingdom.commons.transmission.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.evilkingdom.commons.transmission.TransmissionImplementor;
import net.evilkingdom.commons.transmission.abstracts.TransmissionHandler;
import net.evilkingdom.commons.transmission.enums.TransmissionType;
import net.evilkingdom.commons.transmission.implementations.TransmissionTask;
import net.evilkingdom.commons.utilities.pterodactyl.PterodactylUtilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
     * Allows you to send a player to a server.
     *
     * @param player ~ The player to send.
     * @param server ~ The server to send the player to.
     */
    public void send(final Player player, final TransmissionServer server) {
        final ByteArrayDataOutput outputStream = ByteStreams.newDataOutput();
        outputStream.writeUTF("Connect");
        outputStream.writeUTF(server.getName());
        player.sendPluginMessage(this.plugin, "BungeeCord", outputStream.toByteArray());
    }

    /**
     * Allows you to handle the BungeeCord message.
     * This should not be used inside your plugin whatsoever!
     *
     * @param server ~ The server of the transmission.
     * @param siteName ~ The site name of the transmission.
     * @param type ~ The type of the transmission.
     * @param uuid ~ The uuid of the transmission.
     * @param data ~ The data of the transmission.
     */
    public void handleBungeeCordMessage(final TransmissionServer server, final String siteName, final TransmissionType type, final UUID uuid, final String data) {
        if (type == TransmissionType.RESPONSE) {
            final TransmissionTask task = this.tasks.stream().filter(transmissionTask -> transmissionTask.getUUID().toString().equals(uuid.toString())).findFirst().get();
            task.setResponseData(data);
            task.stop();
        } else {
            this.handler.onReceive(server, siteName, type, uuid, data);
        }
    }

    /**
     * Allows you to register the transmission site.
     */
    public void register() {
        TransmissionImplementor implementor = TransmissionImplementor.get(this.plugin);
        implementor.getSites().add(this);
    }

    /**
     * Allows you to unregister the transmission site.
     */
    public void unregister() {
        TransmissionImplementor implementor = TransmissionImplementor.get(this.plugin);
        implementor.getSites().remove(this);
    }

}
