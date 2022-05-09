package net.evilkingdom.commons.transmission;

/*
 * Made with love by https://kodirati.com/.
 */

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.evilkingdom.commons.Commons;
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
            final DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(message));
            String preInternalSiteName = null;
            String[] preData = null;
            try {
                preInternalSiteName = inputStream.readUTF().replace("Transmissions-", "");
                final short messageBytesLength = inputStream.readShort();
                final byte[] messageBytes = new byte[messageBytesLength];
                inputStream.readFully(messageBytes);
                final DataInputStream messageStream = new DataInputStream(new ByteArrayInputStream(messageBytes));
                preData = messageStream.readUTF().split("\\|");
            } catch (final IOException ioException) {
                //Does nothing, just in case! :)
            }
            final long timeSent = Long.parseLong(preData[0]);
            if ((System.currentTimeMillis() - timeSent) > 250) {
                return;
            }
            final String internalSiteName = preInternalSiteName;
            final TransmissionSite internalSite = this.sites.stream().filter(site -> site.getName().equals(internalSiteName)).findFirst().get();
            final String serverName = preData[1];
            final String siteName = preData[2];
            final TransmissionType type = TransmissionType.valueOf(preData[3]);
            final UUID uuid = UUID.fromString(preData[4]);
            final String data = preData[5];
            internalSite.handleBungeeCordMessage(serverName, siteName, type, uuid, data);
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
