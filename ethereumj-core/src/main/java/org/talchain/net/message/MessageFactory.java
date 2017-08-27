package org.talchain.net.message;

import org.talchain.net.eth.message.EthMessageCodes;

/**
 * Factory interface to create messages
 *
 * @author Mikhail Kalinin
 * @since 20.08.2015
 */
public interface MessageFactory {

    /**
     * Creates message by absolute message codes
     * e.g. codes described in {@link EthMessageCodes}
     *
     * @param code message code
     * @param encoded encoded message bytes
     * @return created message
     *
     * @throws IllegalArgumentException if code is unknown
     */
    Message create(byte code, byte[] encoded);

}
