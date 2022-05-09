package net.evilkingdom.commons.transmission.abstracts;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.transmission.enums.TransmissionType;
import net.evilkingdom.commons.transmission.objects.TransmissionServer;

import java.util.UUID;

public abstract class TransmissionHandler {

    /**
     * The receiving of a transmission.
     *
     * @param server ~ The transmission's transmission server.
     * @param siteName ~ The transmission's transmission site's name.
     * @param type ~ The transmission's transmission type.
     * @param uuid ~ The transmission's uuid.
     * @param data ~ The transmission's data.
     */
    public void onReceive(final TransmissionServer server, final String siteName, final TransmissionType type, final UUID uuid, final String data) {
        //It'll be overridden wherever it is used, therefore it is empty.
    }

}
