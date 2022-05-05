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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class TransmissionSite {

    private final JavaPlugin plugin;

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
     * Allows you to retrieve the transmission site's name.
     *
     * @return ~ The transmission site's name.
     */
    public String getName() {
        return this.name;
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
        try {
            this.createSocket();
        } catch (final IOException ioException) {
            //Does nothing, just in case :)
        }
    }

    /**
     * Allows you to create the socket.
     */
    private void createSocket() throws IOException {
        ServerSocket preServerSocket;
        try {
            preServerSocket = new ServerSocket(this.port);
        } catch (final IOException ioException) {
            preServerSocket = null;
            //Does nothing, just in case :)
        }
        final ServerSocket serverSocket = preServerSocket;
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            if (serverSocket.isClosed()) {
                return;
            }
            TransmissionServer server = null;
            TransmissionType type = null;
            String data = null;
            String authentication = null;
            try {
                final Socket socket = serverSocket.accept();
                final DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                type = TransmissionType.valueOf(inputStream.readUTF());
                final String serverName = inputStream.readUTF();
                server = this.servers.stream().filter(transmissionServer -> transmissionServer.getName().equals(serverName)).findFirst().get();
                data = inputStream.readUTF();
                authentication = inputStream.readUTF();
            } catch (final IOException ioException) {
                //Does nothing, just in case :)
            }
            if (!authentication.equals("evilKingdomAuthenticated-uW9ezXQECPL6aRgePG6ab5qS")) {
                return;
            }
            this.handler.onReceive(server, type, data);
        }, 0L, 1L);
    }

}
