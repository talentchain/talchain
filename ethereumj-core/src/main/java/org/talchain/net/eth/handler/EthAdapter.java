package org.talchain.net.eth.handler;

import com.google.common.util.concurrent.ListenableFuture;
import org.ethereum.core.*;
import org.talchain.core.*;
import org.talchain.net.eth.EthVersion;
import org.talchain.sync.SyncStatistics;
import org.talchain.net.server.Channel;

import java.math.BigInteger;
import java.util.List;

import static org.talchain.net.eth.EthVersion.*;

/**
 * It's quite annoying to always check {@code if (eth != null)} before accessing it. <br>
 *
 * This adapter helps to avoid such checks. It provides meaningful answers to Eth client
 * assuming that Eth hasn't been initialized yet. <br>
 *
 * Check {@link Channel} for example.
 *
 * @author Mikhail Kalinin
 * @since 20.08.2015
 */
public class EthAdapter implements Eth {

    private final SyncStatistics syncStats = new SyncStatistics();

    @Override
    public boolean hasStatusPassed() {
        return false;
    }

    @Override
    public boolean hasStatusSucceeded() {
        return false;
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public String getSyncStats() {
        return "";
    }

    @Override
    public boolean isHashRetrievingDone() {
        return false;
    }

    @Override
    public boolean isHashRetrieving() {
        return false;
    }

    @Override
    public boolean isIdle() {
        return true;
    }

    @Override
    public SyncStatistics getStats() {
        return syncStats;
    }

    @Override
    public void disableTransactions() {
    }

    @Override
    public void enableTransactions() {
    }

    @Override
    public void sendTransaction(List<Transaction> tx) {
    }

    @Override
    public ListenableFuture<List<BlockHeader>> sendGetBlockHeaders(long blockNumber, int maxBlocksAsk, boolean reverse) {
        return null;
    }

    @Override
    public ListenableFuture<List<BlockHeader>> sendGetBlockHeaders(byte[] blockHash, int maxBlocksAsk, int skip, boolean reverse) {
        return null;
    }

    @Override
    public ListenableFuture<List<Block>> sendGetBlockBodies(List<BlockHeaderWrapper> headers) {
        return null;
    }

    @Override
    public void sendNewBlock(Block newBlock) {
    }

    @Override
    public void sendNewBlockHashes(Block block) {

    }

    @Override
    public EthVersion getVersion() {
        return fromCode(UPPER);
    }

    @Override
    public void onSyncDone(boolean done) {
    }

    @Override
    public void sendStatus() {
    }

    @Override
    public void dropConnection() {
    }

    @Override
    public void fetchBodies(List<BlockHeaderWrapper> headers) {
    }

    @Override
    public BlockIdentifier getBestKnownBlock() {
        return null;
    }

    @Override
    public BigInteger getTotalDifficulty() {
        return BigInteger.ZERO;
    }

}