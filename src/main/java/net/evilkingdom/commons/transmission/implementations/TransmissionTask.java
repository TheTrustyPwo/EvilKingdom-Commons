package net.evilkingdom.commons.transmission.implementations;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.transmission.TransmissionImplementor;
import net.evilkingdom.commons.transmission.enums.TransmissionType;
import net.evilkingdom.commons.transmission.objects.TransmissionServer;
import net.evilkingdom.commons.transmission.objects.TransmissionSite;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TransmissionTask {

    private final UUID uuid;
    private final String data;
    private final TransmissionType type;
    private final TransmissionSite site;
    private final TransmissionServer targetServer;
    private String responseData;

    /**
     * Allows you to create a transmission task for a plugin.
     * This should not be used inside your plugin whatsoever!
     *
     * @param transmissionSite ~ The transmission site of the transmission.
     * @param targetTransmissionServer ~ The target transmission server of the transmission.
     * @param transmissionType ~ The type of the transmission.
     * @param uuid ~ The uuid of the transmission.
     * @param data ~ The data of the transmission.
     */
    public TransmissionTask(final TransmissionSite transmissionSite, final TransmissionType transmissionType, final TransmissionServer targetTransmissionServer, final UUID uuid, final String data) {
        this.site = transmissionSite;
        this.type = transmissionType;
        this.targetServer = targetTransmissionServer;
        this.uuid = uuid;
        this.data = data;
    }

    /**
     * Allows you to start the task.
     */
    public void start() {
        final String ip = this.targetServer.getAddress()[0];
        final int port = Integer.parseInt(this.targetServer.getAddress()[1]);
        CompletableFuture.runAsync(() -> {
            try {
                final Socket socket = new Socket(InetAddress.getByName(ip), port);
                if (socket.isClosed()) {
                    return;
                }
                final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(this.site.getName());
                out.writeUTF(this.type.toString());
                out.writeUTF(this.uuid.toString());
                out.writeUTF(this.data);
                out.writeUTF("evilKingdomAuthenticated-uW9ezXQECPL6aRgePG6ab5qS");
                if (this.type == TransmissionType.REQUEST) {
                    this.site.getTasks().add(this);
                }
            } catch (final IOException ioException) {
                //Does nothing, just in case :)
            }
        });
    }

    /**
     * Allows you to stop the task.
     */
    public void stop() {
        this.site.getTasks().remove(this);
    }

    /**
     * Allows you to retrieve if the task is running.
     * This will only not be false if the type is REQUEST.
     *
     * @return ~ If the task is running.
     */
    public boolean isRunning() {
        return this.site.getTasks().contains(this);
    }

    /**
     * Allows you to retrieve the task's response data.
     * This will only not be null if the type is REQUEST and the task is no longer running (as it received a response).
     *
     * @return ~ The task's response data.
     */
    public String getResponseData() {
        return this.responseData;
    }

    /**
     * Allows you to set the task's response data.
     *
     * @param responseData ~ The task's response data to set.
     */
    public void setResponseData(final String responseData) {
        this.responseData = responseData;
    }

    /**
     * Allows you to retrieve the task's target server.
     *
     * @return ~ The task's target server.
     */
    public TransmissionServer getTargetServer() {
        return this.targetServer;
    }

    /**
     * Allows you to retrieve the task's site.
     *
     * @return ~ The task's site.
     */
    public TransmissionSite getSite() {
        return this.site;
    }

    /**
     * Allows you to retrieve if the task's type.
     *
     * @return ~ The task's type.
     */
    public TransmissionType getType() {
        return this.type;
    }

    /**
     * Allows you to retrieve if the task's data.
     *
     * @return ~ The task's data.
     */
    public String getData() {
        return this.data;
    }

    /**
     * Allows you to retrieve if the task's uuid.
     *
     * @return ~ The task's uuid.
     */
    public UUID getUUID() {
        return this.uuid;
    }

}
