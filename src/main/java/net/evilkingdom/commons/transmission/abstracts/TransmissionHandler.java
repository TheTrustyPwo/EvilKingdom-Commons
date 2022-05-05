package net.evilkingdom.commons.transmission.abstracts;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.transmission.enums.TransmissionType;
import net.evilkingdom.commons.transmission.objects.TransmissionServer;

public abstract class TransmissionHandler {

    /**
     * The receiving of a transmission.
     *
     * @param transmissionServer ~ The transmission's transmission server.
     * @param transmissionType ~ The transmission's transmission type.
     * @param data ~ The transmission's data.
     */
    public void onReceive(final TransmissionServer transmissionServer, final TransmissionType transmissionType, final String data) {
        //It'll be overridden wherever it is used, therefore it is empty.
    }

}
