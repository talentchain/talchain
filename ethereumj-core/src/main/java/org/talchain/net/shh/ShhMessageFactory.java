package org.talchain.net.shh;

import org.ethereum.net.message.*;
import org.talchain.net.message.Message;
import org.talchain.net.message.MessageFactory;

/**
 * @author Mikhail Kalinin
 * @since 20.08.2015
 */
public class ShhMessageFactory implements MessageFactory {

    @Override
    public Message create(byte code, byte[] encoded) {

        ShhMessageCodes receivedCommand = ShhMessageCodes.fromByte(code);
        switch (receivedCommand) {
            case STATUS:
                return new ShhStatusMessage(encoded);
            case MESSAGE:
                return new ShhEnvelopeMessage(encoded);
            case FILTER:
                return new ShhFilterMessage(encoded);
            default:
                throw new IllegalArgumentException("No such message");
        }
    }
}
