package org.talchain.net.p2p;

import org.talchain.config.SystemProperties;
import org.talchain.core.Block;
import org.talchain.core.Transaction;
import org.talchain.listener.EthereumListener;
import org.talchain.net.MessageQueue;
import org.talchain.net.client.Capability;
import org.talchain.net.client.ConfigCapabilities;
import org.talchain.net.eth.message.NewBlockMessage;
import org.talchain.net.eth.message.TransactionsMessage;
import org.talchain.net.message.ReasonCode;
import org.talchain.net.message.StaticMessages;
import org.talchain.net.server.Channel;
import org.talchain.net.shh.ShhHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.talchain.net.swarm.Util;
import org.talchain.net.swarm.bzz.BzzHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.talchain.net.eth.EthVersion.*;

/**
 * Process the basic protocol messages between every peer on the network.
 *
 * Peers can send/receive
 * <ul>
 *  <li>HELLO       :   Announce themselves to the network</li>
 *  <li>DISCONNECT  :   Disconnect themselves from the network</li>
 *  <li>GET_PEERS   :   Request a list of other knows peers</li>
 *  <li>PEERS       :   Send a list of known peers</li>
 *  <li>PING        :   Check if another peer is still alive</li>
 *  <li>PONG        :   Confirm that they themselves are still alive</li>
 * </ul>
 */
@Component
@Scope("prototype")
public class P2pHandler extends SimpleChannelInboundHandler<P2pMessage> {

    public final static byte VERSION = 4;

    public final static byte[] SUPPORTED_VERSIONS = {4, 5};

    private final static Logger logger = LoggerFactory.getLogger("net");

    private static ScheduledExecutorService pingTimer =
            Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    return new Thread(r, "P2pPingTimer");
                }
            });

    private MessageQueue msgQueue;

    private boolean peerDiscoveryMode = false;

    private HelloMessage handshakeHelloMessage = null;

    private int ethInbound;
    private int ethOutbound;

    @Autowired
    EthereumListener ethereumListener;

    @Autowired
    ConfigCapabilities configCapabilities;

    @Autowired
    SystemProperties config;

    private Channel channel;
    private ScheduledFuture<?> pingTask;


    public P2pHandler() {

        this.peerDiscoveryMode = false;
    }

    public P2pHandler(MessageQueue msgQueue, boolean peerDiscoveryMode) {
        this.msgQueue = msgQueue;
        this.peerDiscoveryMode = peerDiscoveryMode;
    }


    public void setPeerDiscoveryMode(boolean peerDiscoveryMode) {
        this.peerDiscoveryMode = peerDiscoveryMode;
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.debug("P2P protocol activated");
        msgQueue.activate(ctx);
        ethereumListener.trace("P2P protocol activated");
        startTimers();
    }


    @Override
    public void channelRead0(final ChannelHandlerContext ctx, P2pMessage msg) throws InterruptedException {

        if (P2pMessageCodes.inRange(msg.getCommand().asByte()))
            logger.trace("P2PHandler invoke: [{}]", msg.getCommand());

        ethereumListener.trace(String.format("P2PHandler invoke: [%s]", msg.getCommand()));

        switch (msg.getCommand()) {
            case HELLO:
                msgQueue.receivedMessage(msg);
                setHandshake((HelloMessage) msg, ctx);
//                sendGetPeers();
                break;
            case DISCONNECT:
                msgQueue.receivedMessage(msg);
                channel.getNodeStatistics().nodeDisconnectedRemote(((DisconnectMessage) msg).getReason());
                processDisconnect(ctx, (DisconnectMessage) msg);
                break;
            case PING:
                msgQueue.receivedMessage(msg);
                ctx.writeAndFlush(StaticMessages.PONG_MESSAGE);
                break;
            case PONG:
                msgQueue.receivedMessage(msg);
                channel.getNodeStatistics().lastPongReplyTime.set(Util.curTime());
                break;
            case PEERS:
                msgQueue.receivedMessage(msg);

                if (peerDiscoveryMode ||
                        !handshakeHelloMessage.getCapabilities().contains(Capability.ETH)) {
                    disconnect(ReasonCode.REQUESTED);
                    killTimers();
                    ctx.close().sync();
                    ctx.disconnect().sync();
                }
                break;
            default:
                ctx.fireChannelRead(msg);
                break;
        }
    }

    private void disconnect(ReasonCode reasonCode) {
        msgQueue.sendMessage(new DisconnectMessage(reasonCode));
        channel.getNodeStatistics().nodeDisconnectedLocal(reasonCode);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channel inactive: ", ctx.toString());
        this.killTimers();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("P2p handling failed", cause);
        ctx.close();
        killTimers();
    }

    private void processDisconnect(ChannelHandlerContext ctx, DisconnectMessage msg) {

        if (logger.isInfoEnabled() && msg.getReason() == ReasonCode.USELESS_PEER) {

            if (channel.getNodeStatistics().ethInbound.get() - ethInbound > 1 ||
                    channel.getNodeStatistics().ethOutbound.get() - ethOutbound > 1) {

                // it means that we've been disconnected
                // after some incorrect action from our peer
                // need to log this moment
                logger.debug("From: \t{}\t [DISCONNECT reason=BAD_PEER_ACTION]", channel);
            }
        }
        ctx.close();
        killTimers();
    }

    private void sendGetPeers() {
        msgQueue.sendMessage(StaticMessages.GET_PEERS_MESSAGE);
    }


    public void setHandshake(HelloMessage msg, ChannelHandlerContext ctx) {

        channel.getNodeStatistics().setClientId(msg.getClientId());
        channel.getNodeStatistics().capabilities.clear();
        channel.getNodeStatistics().capabilities.addAll(msg.getCapabilities());

        this.ethInbound = (int) channel.getNodeStatistics().ethInbound.get();
        this.ethOutbound = (int) channel.getNodeStatistics().ethOutbound.get();

        this.handshakeHelloMessage = msg;
        if (!isProtocolVersionSupported(msg.getP2PVersion())) {
            disconnect(ReasonCode.INCOMPATIBLE_PROTOCOL);
        }
        else {
            List<Capability> capInCommon = getSupportedCapabilities(msg);
            channel.initMessageCodes(capInCommon);
            for (Capability capability : capInCommon) {
                if (capability.getName().equals(Capability.ETH)) {

                    // Activate EthHandler for this peer
                    channel.activateEth(ctx, fromCode(capability.getVersion()));
                } else if
                   (capability.getName().equals(Capability.SHH) &&
                    capability.getVersion() == ShhHandler.VERSION) {

                    // Activate ShhHandler for this peer
                    channel.activateShh(ctx);
                } else if
                   (capability.getName().equals(Capability.BZZ) &&
                    capability.getVersion() == BzzHandler.VERSION) {

                    // Activate ShhHandler for this peer
                    channel.activateBzz(ctx);
                }
            }

            //todo calculate the Offsets
            ethereumListener.onHandShakePeer(channel, msg);

        }
    }

    /**
     * submit transaction to the network
     *
     * @param tx - fresh transaction object
     */
    public void sendTransaction(Transaction tx) {

        TransactionsMessage msg = new TransactionsMessage(tx);
        msgQueue.sendMessage(msg);
    }

    public void sendNewBlock(Block block) {

        NewBlockMessage msg = new NewBlockMessage(block, block.getDifficulty());
        msgQueue.sendMessage(msg);
    }

    public void sendDisconnect() {
        msgQueue.disconnect();
    }

    public HelloMessage getHandshakeHelloMessage() {
        return handshakeHelloMessage;
    }

    private void startTimers() {
        // sample for pinging in background
        pingTask = pingTimer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    msgQueue.sendMessage(StaticMessages.PING_MESSAGE);
                } catch (Throwable t) {
                    logger.error("Unhandled exception", t);
                }
            }
        }, 2, config.getProperty("peer.p2p.pingInterval", 5L), TimeUnit.SECONDS);
    }

    public void killTimers() {
        pingTask.cancel(false);
        msgQueue.close();
    }


    public void setMsgQueue(MessageQueue msgQueue) {
        this.msgQueue = msgQueue;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public static boolean isProtocolVersionSupported(byte ver) {
        for (byte v : SUPPORTED_VERSIONS) {
            if (v == ver) return true;
        }
        return false;
    }

    public List<Capability> getSupportedCapabilities(HelloMessage hello) {
        List<Capability> configCaps = configCapabilities.getConfigCapabilities();
        List<Capability> supported = new ArrayList<>();

        List<Capability> eths = new ArrayList<>();

        for (Capability cap : hello.getCapabilities()) {
            if (configCaps.contains(cap)) {
                if (cap.isEth()) {
                    eths.add(cap);
                } else {
                    supported.add(cap);
                }
            }
        }

        if (eths.isEmpty()) {
            return supported;
        }

        // we need to pick up
        // the most recent Eth version
        Capability highest = null;
        for (Capability eth : eths) {
            if (highest == null || highest.getVersion() < eth.getVersion()) {
                highest = eth;
            }
        }

        supported.add(highest);
        return supported;
    }

}