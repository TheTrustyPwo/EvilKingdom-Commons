package net.evilkingdom.commons.transmission.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.transmission.TransmissionImplementor;
import net.evilkingdom.commons.transmission.enums.TransmissionType;
import net.evilkingdom.commons.transmission.implementations.TransmissionTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Transmission {

    private final UUID uuid;
    private final TransmissionSite site;
    private final String targetServer;
    private final TransmissionType type;
    private final String data;

    /**
     * Allows you to create a transmission for a plugin.
     *
     * @param transmissionSite ~ The transmission site of the transmission.
     * @param targetServer ~ The target server of the transmission.
     * @param transmissionType ~ The type of the transmission.
     * @param data ~ The data of the transmission.
     */
    public Transmission(final TransmissionSite transmissionSite, final String targetServer, final TransmissionType transmissionType, final UUID uuid, final String data) {
        this.site = transmissionSite;
        this.targetServer = targetServer;
        this.type = transmissionType;
        this.uuid = uuid;
        this.data = data;
    }

    /**
     * Allows you to send the transmission.
     *
     * @return ~ The data of the response (if there will be one, if not it'll just be null).
     */
    public CompletableFuture<String> send() {
        final TransmissionTask task = new TransmissionTask(this.site, this.type, this.targetServer, this.uuid, this.data);
        task.start();
        if (this.type == TransmissionType.REQUEST) {
            return CompletableFuture.supplyAsync(() -> {
                while (task.isRunning()) {
                    //Does nothing, just to halt the response until the task is done running.
                }
                return task.getResponseData();
            });
        } else {
            return CompletableFuture.supplyAsync(() -> null);
        }
    }

    /**
     * Allows you to retrieve the task's target server.
     *
     * @return ~ The task's target server.
     */
    public String getTargetServer() {
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
     * Allows you to retrieve if the task's UUID.
     *
     * @return ~ The task's UUID.
     */
    public UUID getUUID() {
        return this.uuid;
    }

}
