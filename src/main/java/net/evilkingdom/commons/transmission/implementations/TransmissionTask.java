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
import java.util.concurrent.CompletableFuture;

public class TransmissionTask {

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
     * @param data ~ The data of the transmission.
     */
    public TransmissionTask(final TransmissionSite transmissionSite, final TransmissionType transmissionType, final TransmissionServer targetTransmissionServer, final String data) {
        this.data = data;
        this.type = transmissionType;
        this.site = transmissionSite;
        this.targetServer = targetTransmissionServer;
    }

    /**
     * Allows you to start the task.
     */
    public void start() {
        this.sendSocketData();
        if (this.type == TransmissionType.REQUEST) {
            this.site.getTasks().add(this);
        }
    }

    /**
     * Allows you to send the socket data.
     */
    private void sendSocketData() {
        final String ip = this.targetServer.getAddress()[0];
        final int port = Integer.parseInt(this.targetServer.getAddress()[1]);
        try (final Socket socket = new Socket(InetAddress.getByName(ip), port)) {
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            if (socket.isClosed()) {
                return;
            }
            out.writeUTF(this.type.toString());
            out.writeUTF(this.site.getName());
            out.writeUTF(this.data);
            out.writeUTF("evilKingdomAuthenticated-uW9ezXQECPL6aRgePG6ab5qS");
        } catch (final IOException ioException) {
            //Does nothing, just in case :)
        }
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

}
