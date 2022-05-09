package net.evilkingdom.commons.transmission.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.transmission.TransmissionImplementor;
import net.evilkingdom.commons.transmission.abstracts.TransmissionHandler;
import net.evilkingdom.commons.transmission.enums.TransmissionType;
import net.evilkingdom.commons.transmission.implementations.TransmissionTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.UUID;

public class TransmissionServer {

    private final TransmissionSite site;
    private final String name, pterodactylId;

    /**
     * Allows you to create a transmission server for a plugin.
     *
     * @param site ~ The transmission site of the transmission server.
     * @param name ~ The name of the transmission server.
     * @param pterodactylId ~ The pterodactyl server id of the transmission server.
     */
    public TransmissionServer(final TransmissionSite site, final String name, final String pterodactylId) {
        this.site = site;
        this.name = name;
        this.pterodactylId = pterodactylId;
    }

    /**
     * Allows you to retrieve the transmission server's name.
     *
     * @return ~ The transmission site's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Allows you to retrieve the transmission server's pterodactyl server id.
     *
     * @return ~ The transmission server's pterodactyl server id.
     */
    public String getPterodactylId() {
        return this.pterodactylId;
    }

    /**
     * Allows you to retrieve the transmission server's site.
     *
     * @return ~ The transmission server's site.
     */
    public TransmissionSite getSite() {
        return this.site;
    }

    /**
     * Allows you to register the transmission server.
     */
    public void register() {
        this.site.getServers().add(this);
    }

    /**
     * Allows you to unregister the transmission server.
     */
    public void unregister() {
        this.site.getServers().remove(this);
    }

}
