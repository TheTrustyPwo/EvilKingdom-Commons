package net.evilkingdom.commons.transmission;

/*
 * Made with love by https://kodirati.com/.
 */

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.evilkingdom.commons.Commons;
import net.evilkingdom.commons.scoreboard.implementations.ScoreboardListener;
import net.evilkingdom.commons.scoreboard.implementations.ScoreboardRunnable;
import net.evilkingdom.commons.scoreboard.objects.Scoreboard;
import net.evilkingdom.commons.transmission.enums.TransmissionType;
import net.evilkingdom.commons.transmission.objects.Transmission;
import net.evilkingdom.commons.transmission.objects.TransmissionServer;
import net.evilkingdom.commons.transmission.objects.TransmissionSite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

public class TransmissionImplementor {

    private final JavaPlugin plugin;

    private final HashSet<TransmissionSite> sites;

    private static final HashSet<TransmissionImplementor> cache = new HashSet<TransmissionImplementor>();

    /**
     * Allows you to create a TransmissionImplementor.
     * This should not be used inside your plugin whatsoever!
     *
     * @param plugin ~ The plugin to create the TransmissionImplementor for.
     */
    public TransmissionImplementor(final JavaPlugin plugin) {
        this.plugin = plugin;

        this.sites = new HashSet<TransmissionSite>();
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this.plugin, "BungeeCord");
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this.plugin, "BungeeCord", (channel, player, message) -> {
            final DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(message));
            String preInternalSiteName = null;
            JsonObject jsonObject = null;
            try {
                preInternalSiteName = inputStream.readUTF().replace("Transmissions-", "");
                final short messageBytesLength = inputStream.readShort();
                final byte[] messageBytes = new byte[messageBytesLength];
                inputStream.readFully(messageBytes);
                final DataInputStream messageStream = new DataInputStream(new ByteArrayInputStream(messageBytes));
                jsonObject = JsonParser.parseString(messageStream.readUTF()).getAsJsonObject();
            } catch (final IOException ioException) {
                //Does nothing, just in case! :)
            }
            final long sentTime = jsonObject.get("sentTime").getAsLong();
            if ((System.currentTimeMillis() - sentTime) > 250) {
                return;
            }
            final String internalSiteName = preInternalSiteName;
            final TransmissionSite internalSite = this.sites.stream().filter(site -> site.getName().equals(internalSiteName)).findFirst().get();
            final String serverName = jsonObject.get("serverName").getAsString();
            final TransmissionServer server = internalSite.getServers().stream().filter(internalServer -> internalServer.getName().equals(serverName)).findFirst().get();
            final String siteName = jsonObject.get("siteName").getAsString();
            final TransmissionType type = TransmissionType.valueOf(jsonObject.get("type").getAsString());
            final UUID uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
            final String data = jsonObject.get("data").getAsString();
            internalSite.handleBungeeCordMessage(server, siteName, type, uuid, data);
        });

        cache.add(this);
    }

    /**
     * Allows you to retrieve the implementor's sites.
     *
     * @return The implementor's sites.
     */
    public HashSet<TransmissionSite> getSites() {
        return this.sites;
    }

    /**
     * Allows you to retrieve the implementor's plugin.
     *
     * @return The implementor's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to retrieve the TransmissionImplementor for a plugin.
     * It will either pull it out of the cache or look for one if it doesn't exist.
     *
     * @param plugin ~ The plugin the TransmissionImplementor is for.
     * @return The self class.
     */
    public static TransmissionImplementor get(final JavaPlugin plugin) {
        final Optional<TransmissionImplementor> optionalImplementor = cache.stream().filter(implementor -> implementor.getPlugin() == plugin).findFirst();
        return optionalImplementor.orElseGet(() -> new TransmissionImplementor(plugin));
    }

}
