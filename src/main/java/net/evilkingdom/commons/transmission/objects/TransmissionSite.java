package net.evilkingdom.commons.transmission.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.transmission.TransmissionImplementor;
import net.evilkingdom.commons.transmission.abstracts.TransmissionHandler;
import net.evilkingdom.commons.transmission.enums.TransmissionType;
import net.evilkingdom.commons.transmission.implementations.TransmissionTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

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

    private BukkitTask task;
    private ServerSocket serverSocket;
    private final String name;
    private final int port;
    private final HashSet<TransmissionServer> servers;
    private final HashSet<TransmissionTask> tasks;
    private TransmissionHandler handler;

    /**
     * Allows you to create a transmission site for a plugin.
     *
     * @param plugin ~ The plugin the transmission site is for.
     * @param name ~ The name of the transmission site.
     * @param port ~ The port of the transmission site.
     */
    public TransmissionSite(final JavaPlugin plugin, final String name, final int port) {
        this.plugin = plugin;

        this.name = name;
        this.port = port;
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
     * Allows you to retrieve the transmission site's handler.
     *
     * @return ~ The transmission site's handler.
     */
    public TransmissionHandler getHandler() {
        return this.handler;
    }

    /**
     * Allows you to retrieve the transmission site's port.
     *
     * @return ~ The transmission site's port.
     */
    public int getPort() {
        return this.port;
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
     * Allows you to retrieve the transmission site's servers.
     *
     * @return ~ The transmission site's servers.
     */
    public HashSet<TransmissionServer> getServers() {
        return this.servers;
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
        transmissionImplementor.getTransmissionSites().add(this);
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(this.port);
        } catch (final IOException ioException) {
            serverSocket = null;
            //Does nothing, just in case! :)
        }
        this.serverSocket = serverSocket;
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            if (this.serverSocket.isClosed()) {
                return;
            }
            TransmissionServer server = null;
            TransmissionType type = null;
            String data = null;
            String authentication = null;
            UUID uuid = null;
            try {
                final Socket socket = this.serverSocket.accept();
                if (socket.getInputStream().available() < 5) {
                    return;
                }
                final DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                final String serverName = inputStream.readUTF();
                server = this.servers.stream().filter(transmissionServer -> transmissionServer.getName().equals(serverName)).findFirst().get();
                type = TransmissionType.valueOf(inputStream.readUTF());
                uuid = UUID.fromString(inputStream.readUTF());
                data = inputStream.readUTF();
                authentication = inputStream.readUTF();
                inputStream.close();
            } catch (final IOException ioException) {
                //Does nothing, just in case :)
            }
            if (type == TransmissionType.RESPONSE) {
                final TransmissionServer finalServer = server;
                final UUID finalUUID = uuid;
                final TransmissionTask task = this.tasks.stream().filter(transmissionTask -> transmissionTask.getTargetServer() == finalServer && transmissionTask.getUUID() == finalUUID).findFirst().get();
                if (authentication.equals("evilKingdomAuthenticated-uW9ezXQECPL6aRgePG6ab5qS")) {
                    task.setResponseData(data);
                } else {
                    task.setResponseData("response=authentication_failed");
                }
                task.stop();
            } else {
                if (!authentication.equals("evilKingdomAuthenticated-uW9ezXQECPL6aRgePG6ab5qS")) {
                    return;
                }
                final TransmissionServer finalServer = server;
                final TransmissionType finalType = type;
                final UUID finalUUID = uuid;
                final String finalData = data;
                Bukkit.getScheduler().runTask(this.plugin, () -> this.handler.onReceive(finalServer, finalType, finalUUID, finalData));
            }
        }, 0L, 1L);
    }

    /**
     * Allows you to unregister the transmission site.
     */
    public void unregister() {
        this.task.cancel();
        try {
            this.serverSocket.close();
        } catch (final IOException ioException) {
            //Does nothing, just in case! :)
        }
    }

}
