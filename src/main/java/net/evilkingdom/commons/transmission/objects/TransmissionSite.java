package net.evilkingdom.commons.transmission.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.evilkingdom.commons.transmission.TransmissionImplementor;
import net.evilkingdom.commons.transmission.abstracts.TransmissionHandler;
import net.evilkingdom.commons.transmission.enums.TransmissionType;
import net.evilkingdom.commons.transmission.implementations.TransmissionTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitTask;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOption;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TransmissionSite {

    private final JavaPlugin plugin;

    private final String name, serverName;
    private final HashSet<TransmissionTask> tasks;
    private TransmissionHandler handler;

    /**
     * Allows you to create a transmission site for a plugin.
     *
     * @param plugin ~ The plugin the transmission site is for.
     * @param serverName ~ The name of the transmission site's server.
     * @param name ~ The name of the transmission site.
     */
    public TransmissionSite(final JavaPlugin plugin, final String serverName, final String name) {
        this.plugin = plugin;

        this.serverName = serverName;
        this.name = name;
        this.tasks = new HashSet<TransmissionTask>();
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
     * Allows you to register the transmission site.
     */
    public void register() {
        TransmissionImplementor transmissionImplementor = TransmissionImplementor.get(this.plugin);
        transmissionImplementor.getSites().add(this);
    }

    /**
     * Allows you to handle the BungeeCord message.
     * This should not be used inside your plugin whatsoever!
     *
     * @param serverName ~ The server name of the transmission.
     * @param siteName ~ The site name of the transmission.
     * @param type ~ The type of the transmission.
     * @param uuid ~ The uuid of the transmission.
     * @param data ~ The data of the transmission.
     */
    public void handleBungeeCordMessage(final String serverName, final String siteName, final TransmissionType type, final UUID uuid, final String data) {
        if (type == TransmissionType.RESPONSE) {
            final TransmissionTask task = this.tasks.stream().filter(transmissionTask -> transmissionTask.getTargetServerName().equals(serverName) && transmissionTask.getTargetSiteName().equals(siteName) && transmissionTask.getUUID() == uuid).findFirst().get();
            task.setResponseData(data);
            task.stop();
        } else {
            this.handler.onReceive(serverName, siteName, type, uuid, data);
        }
    }

    /**
     * Allows you to unregister the transmission site.
     */
    public void unregister() {
        TransmissionImplementor transmissionImplementor = TransmissionImplementor.get(this.plugin);
        transmissionImplementor.getSites().remove(this);
    }

}
