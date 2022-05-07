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

    private BukkitTask task;
    private ServerSocket serverSocket;
    private final String name;
    private final int port;
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
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "Transmissions");
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, "Transmissions", (channel, player, message) -> {
            if (!channel.equals("Transmissions")) {
                return;
            }
            final DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(message));
            String messageData;
            try {
                messageData = inputStream.readUTF();
            } catch (final IOException ioException) {
                messageData = null;
                //Does nothing, just in case! :)
            }
            final String server = messageData.split("\\|")[0];
            final TransmissionType type = TransmissionType.valueOf(messageData.split("\\|")[1]);
            final UUID uuid = UUID.fromString(messageData.split("\\|")[2]);
            final String data = messageData.split("\\|")[3];;
            if (type == TransmissionType.RESPONSE) {
                final TransmissionTask task = this.tasks.stream().filter(transmissionTask -> transmissionTask.getTargetServer().equals(server) && transmissionTask.getUUID() == uuid).findFirst().get();
                task.setResponseData(data);
                task.stop();
            } else {
                this.handler.onReceive(server, type, uuid, data);
            }
        });
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
