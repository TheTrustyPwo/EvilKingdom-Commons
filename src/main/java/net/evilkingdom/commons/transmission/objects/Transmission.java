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
    private final String data, targetSiteName, targetServerName;
    private final TransmissionType type;
    private final TransmissionSite site;

    /**
     * Allows you to create a transmission for a plugin.
     *
     * @param site ~ The transmission site of the transmission.
     * @param targetServerName ~ The target server's name of the transmission.
     * @param targetSiteName ~ The target site's name of the transmission.
     * @param type ~ The type of the transmission.
     * @param uuid ~ The uuid of the transmission.
     * @param data ~ The data of the transmission.
     */
    public Transmission(final TransmissionSite site, final TransmissionType type, final String targetServerName, final String targetSiteName, final UUID uuid, final String data) {
        this.site = site;
        this.type = type;
        this.targetSiteName = targetSiteName;
        this.targetServerName = targetServerName;
        this.uuid = uuid;
        this.data = data;
    }

    /**
     * Allows you to send the transmission.
     *
     * @return ~ The data of the response- if it fails it'll just respond "response=request_failed" (if there will be one, if not it'll just be null).
     */
    public CompletableFuture<String> send() {
        final TransmissionTask task = new TransmissionTask(this.site, this.type, this.targetServerName, this.targetSiteName, this.uuid, this.data);
        task.start();
        if (this.type == TransmissionType.REQUEST) {
            final long stopTime = System.currentTimeMillis() + 100L;
            return CompletableFuture.supplyAsync(() -> {
                while (System.currentTimeMillis() < stopTime) {
                    //All this does is pause the thread until the time limit is reached.
                }
                if (task.getResponseData() == null) {
                    return "response=request_failed";
                }
                return task.getResponseData();
            });
        } else {
            return CompletableFuture.supplyAsync(() -> null);
        }
    }

    /**
     * Allows you to retrieve the transmission's target site's name.
     *
     * @return ~ The transmission's target site's name.
     */
    public String getTargetSiteName() {
        return this.targetSiteName;
    }

    /**
     * Allows you to retrieve the transmission's target server's name.
     *
     * @return ~ The transmission's target server's name.
     */
    public String getTargetServerName() {
        return this.targetServerName;
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
