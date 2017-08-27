package org.talchain.net.rlpx.discover;


import org.talchain.net.rlpx.Message;

import java.net.InetSocketAddress;

public class DiscoveryEvent {
    private Message message;
    private InetSocketAddress address;

    public DiscoveryEvent(Message m, InetSocketAddress a) {
        message = m;
        address = a;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }
}
