package net.evilkingdom.commons.transmission.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.transmission.TransmissionImplementor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TransmissionServer {

    private final String name;
    private final String[] address;
    private final TransmissionSite transmissionSite;

    /**
     * Allows you to create a transmission server for a plugin.
     *
     * @param transmissionSite ~ The transmission site the transmission server is for.
     * @param name ~ The name of the transmission server.
     * @param address ~ The address of the transmission server.
     */
    public TransmissionServer(final TransmissionSite transmissionSite, final String name, final String[] address) {
        this.transmissionSite = transmissionSite;
        this.name = name;
        this.address = address;
    }

    /**
     * Allows you to retrieve the transmission server's name.
     *
     * @return ~ The transmission server's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Allows you to retrieve the transmission server's address.
     *
     * @return ~ The transmission server's address.
     */
    public String[] getAddress() {
        return this.address;
    }

    /**
     * Allows you to register the transmission server.
     */
    public void register() {
        this.transmissionSite.getServers().add(this);
    }

}
