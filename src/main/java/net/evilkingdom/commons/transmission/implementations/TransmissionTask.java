package net.evilkingdom.commons.transmission.implementations;

/*
 * Made with love by https://kodirati.com/.
 */

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.evilkingdom.commons.transmission.TransmissionImplementor;
import net.evilkingdom.commons.transmission.enums.TransmissionType;
import net.evilkingdom.commons.transmission.objects.TransmissionServer;
import net.evilkingdom.commons.transmission.objects.TransmissionSite;
import net.evilkingdom.commons.utilities.pterodactyl.PterodactylUtilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TransmissionTask {

    private final UUID uuid;
    private final String data, targetSiteName;
    private final TransmissionServer targetServer;
    private final TransmissionType type;
    private final TransmissionSite site;
    private String responseData;

    /**
     * Allows you to create a transmission task for a plugin.
     * This should not be used inside your plugin whatsoever!
     *
     * @param site ~ The transmission site of the transmission.
     * @param targetServer ~ The target server of the transmission.
     * @param targetSiteName ~ The target site of the transmission.
     * @param type ~ The type of the transmission.
     * @param uuid ~ The uuid of the transmission.
     * @param data ~ The data of the transmission.
     */
    public TransmissionTask(final TransmissionSite site, final TransmissionServer targetServer, final String targetSiteName, final TransmissionType type, final UUID uuid, final String data) {
        this.site = site;
        this.type = type;
        this.targetServer = targetServer;
        this.targetSiteName = targetSiteName;
        this.uuid = uuid;
        this.data = data;
    }

    /**
     * Allows you to start the task.
     */
    public void start() {
        Bukkit.getConsoleSender().sendMessage("starting");
        final JsonObject jsonObject = new JsonObject();
        if (this.type != TransmissionType.RESPONSE) {
            jsonObject.addProperty("serverName", this.site.getServerName());
            jsonObject.addProperty("siteName", this.site.getName());
        }
        jsonObject.addProperty("data", this.data);
        final File temptransnetFolder = new File("temp_transnet");
        if (!temptransnetFolder.exists()) {
            temptransnetFolder.mkdirs();
        }
        final File file = new File(this.getSite().getPlugin().getDataFolder() + File.separator + "transmissions" + File.separator + this.targetSiteName + File.separator + this.type.name() + "s", this.uuid + ".json");
        PterodactylUtilities.writeFile(this.site.getPterodactylURL(), this.site.getPterodactylToken(), this.targetServer.getPterodactylId(), file, new Gson().toJson(jsonObject));
        if (this.type == TransmissionType.REQUEST) {
            this.site.getTasks().add(this);
        }
    }

    /**
     * Allows you to delete the request.
     * This should only be used in external means.
     */
    public void delete() {
        final File file = new File(this.getSite().getPlugin().getDataFolder() + File.separator + "transmissions" + File.separator + this.targetSiteName + File.separator + this.type.name() + "s", this.uuid.toString() + ".json");
        PterodactylUtilities.deleteFile(this.site.getPterodactylURL(), this.site.getPterodactylToken(), this.targetServer.getPterodactylId(), file);
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
