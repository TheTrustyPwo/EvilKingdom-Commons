package net.evilkingdom.commons.transmission.implementations;

/*
 * Made with love by https://kodirati.com/.
 */

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.evilkingdom.commons.transmission.TransmissionImplementor;
import net.evilkingdom.commons.transmission.enums.TransmissionType;
import net.evilkingdom.commons.transmission.objects.TransmissionSite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TransmissionTask {

    private final UUID uuid;
    private final String data, targetSiteName, targetServerName;
    private final TransmissionType type;
    private final TransmissionSite site;
    private String responseData;

    /**
     * Allows you to create a transmission task for a plugin.
     * This should not be used inside your plugin whatsoever!
     *
     * @param site ~ The transmission site of the transmission.
     * @param targetServerName ~ The target server's name of the transmission.
     * @param targetSiteName ~ The target site's name of the transmission.
     * @param type ~ The type of the transmission.
     * @param uuid ~ The uuid of the transmission.
     * @param data ~ The data of the transmission.
     */
    public TransmissionTask(final TransmissionSite site, final TransmissionType type, final String targetServerName, final String targetSiteName, final UUID uuid, final String data) {
        this.site = site;
        this.type = type;
        this.targetSiteName = targetSiteName;
        this.targetServerName = targetServerName;
        this.uuid = uuid;
        this.data = data;
    }

    /**
     * Allows you to start the task.
     */
    public void start() {
        Bukkit.getScheduler().runTaskAsynchronously(this.site.getPlugin(), () -> {
            Bukkit.getConsoleSender().sendMessage("sending that hoe async tbh");
            final ByteArrayDataOutput outputStream = ByteStreams.newDataOutput();
            outputStream.writeUTF("Forward");
            outputStream.writeUTF(this.targetServerName);
            outputStream.writeUTF("Transmissions-" + this.targetSiteName);
            outputStream.writeUTF(this.site.getServerName());
            outputStream.writeUTF(this.site.getName());
            outputStream.writeUTF(this.type.name());
            outputStream.writeUTF(this.uuid.toString());
            outputStream.writeUTF(this.data);
            Bukkit.getServer().sendPluginMessage(this.site.getPlugin(), "BungeeCord", outputStream.toByteArray());
            if (this.type == TransmissionType.REQUEST) {
                Bukkit.getConsoleSender().sendMessage("oh its a request lemme lowkey set it up");
                this.site.getTasks().add(this);
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
     * Allows you to retrieve the task's target server name.
     *
     * @return ~ The task's target server name.
     */
    public String getTargetServerName() {
        return this.targetServerName;
    }

    /**
     * Allows you to retrieve the task's target site name.
     *
     * @return ~ The task's target site name.
     */
    public String getTargetSiteName() {
        return this.targetSiteName;
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
