package net.evilkingdom.commons.transmission;

/*
 * Made with love by https://kodirati.com/.
 */

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.evilkingdom.commons.scoreboard.implementations.ScoreboardListener;
import net.evilkingdom.commons.scoreboard.implementations.ScoreboardRunnable;
import net.evilkingdom.commons.scoreboard.objects.Scoreboard;
import net.evilkingdom.commons.transmission.enums.TransmissionType;
import net.evilkingdom.commons.transmission.objects.Transmission;
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
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                final DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(message));
                String currentSiteName = null;
                String serverName = null;
                String siteName = null;
                TransmissionType type = null;
                UUID uuid = null;
                String data = null;
                try {
                    currentSiteName = inputStream.readUTF().replace("Transmissions-", "");
                    serverName = inputStream.readUTF();
                    siteName = inputStream.readUTF();
                    type = TransmissionType.valueOf(inputStream.readUTF());
                    uuid = UUID.fromString(inputStream.readUTF());
                    data = inputStream.readUTF();
                } catch (final IOException ioException) {
                    //Does nothing, just in case! :)
                }
                final String finalCurrentSiteName = currentSiteName;
                final String finalServerName = serverName;
                final String finalSiteName = siteName;
                final TransmissionType finalType = type;
                final UUID finalUUID = uuid;
                final String finalData = data;
                final TransmissionSite currentSite = this.sites.stream().filter(site -> site.getName().equals(finalCurrentSiteName)).findFirst().get();
                Bukkit.getScheduler().runTask(this.plugin, () -> currentSite.handleBungeeCordMessage(finalServerName, finalSiteName, finalType, finalUUID, finalData));
            });
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
     * Allows you to send a player to a server.
     *
     * @param player ~ The player to send.
     * @param serverName ~ The server's name to send the player to.
     */
    public void send(final Player player, final String serverName) {
        final ByteArrayDataOutput outputStream = ByteStreams.newDataOutput();
        outputStream.writeUTF("Connect");
        outputStream.writeUTF(serverName);
        player.sendPluginMessage(this.plugin, "BungeeCord", outputStream.toByteArray());
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
