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
    private final String data;
    private final TransmissionServer targetServer;
    private final TransmissionType type;
    private final TransmissionSite site;

    /**
     * Allows you to create a transmission for a plugin.
     *
     * @param site ~ The transmission site of the transmission.
     * @param targetServer ~ The target server of the transmission.
     * @param type ~ The type of the transmission.
     * @param uuid ~ The uuid of the transmission.
     * @param data ~ The data of the transmission.
     */
    public Transmission(final TransmissionSite site, final TransmissionType type, final TransmissionServer targetServer, final UUID uuid, final String data) {
        this.site = site;
        this.type = type;
        this.targetServer = targetServer;
        this.uuid = uuid;
        this.data = data;
    }

    /**
     * Allows you to send the transmission.
     * Automatically fails if a server is empty and if a task takes too long.
     *
     * @return ~ The data of the response- if it fails it'll just respond "response=request_failed" (if there will be one, if not it'll just be null).
     */
    public CompletableFuture<String> send() {
        if (Bukkit.getOnlinePlayers().size() == 0) {
            if (this.type == TransmissionType.REQUEST) {
                return CompletableFuture.supplyAsync(() -> "response=request_failed");
            } else {
                return CompletableFuture.supplyAsync(() -> null);
            }
        }
        final TransmissionTask task = new TransmissionTask(this.site, this.type, this.targetServer, this.uuid, this.data);
        task.start();
        if (this.type == TransmissionType.REQUEST) {
            final long startTime = System.currentTimeMillis();
            return CompletableFuture.supplyAsync(() -> {
                while (task.isRunning()) {
                    if ((System.currentTimeMillis() - startTime) > 250) {
                        task.setResponseData("response=request_failed");
                        task.delete();
                        task.stop();
                    }
                }
                return task.getResponseData();
            });
        } else {
            return CompletableFuture.supplyAsync(() -> null);
        }
    }

    /**
     * Allows you to retrieve the transmission's target server.
     *
     * @return ~ The transmission's target server.
     */
    public TransmissionServer getTargetServer() {
        return this.targetServer;
    }

    /**
     * Allows you to retrieve the transmission's site.
     *
     * @return ~ The transmission's site.
     */
    public TransmissionSite getSite() {
        return this.site;
    }

    /**
     * Allows you to retrieve if the transmission's type.
     *
     * @return ~ The transmission's type.
     */
    public TransmissionType getType() {
        return this.type;
    }

    /**
     * Allows you to retrieve if the transmission's data.
     *
     * @return ~ The transmission's data.
     */
    public String getData() {
        return this.data;
    }

    /**
     * Allows you to retrieve if the transmission's UUID.
     *
     * @return ~ The transmission's UUID.
     */
    public UUID getUUID() {
        return this.uuid;
    }

}
