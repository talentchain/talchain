package org.talchain.config;

import org.talchain.config.blockchain.DaoHFConfig;
import org.talchain.config.blockchain.DaoNoHFConfig;
import org.talchain.config.blockchain.FrontierConfig;
import org.talchain.config.net.BaseNetConfig;
import org.talchain.core.Block;
import org.talchain.util.blockchain.StandaloneBlockchain;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Stan Reshetnyk on 29.12.16.
 */
public class DaoLightMiningTest {

    // configure
    final int FORK_BLOCK = 20;
    final int FORK_BLOCK_AFFECTED = 10; // hardcoded in DAO config


    @Test
    public void testDaoExtraData() {
        final StandaloneBlockchain sb = createBlockchain(true);

        for (int i = 0; i < FORK_BLOCK + 30; i++) {
            Block b = sb.createBlock();
//            System.out.println("Created block " + b.getNumber() + " " + getData(b.getExtraData()));
        }

        assertEquals("EthereumJ powered", getData(sb, FORK_BLOCK - 1));
        assertEquals("dao-hard-fork", getData(sb, FORK_BLOCK));
        assertEquals("dao-hard-fork", getData(sb, FORK_BLOCK + FORK_BLOCK_AFFECTED - 1));
        assertEquals("EthereumJ powered", getData(sb, FORK_BLOCK + FORK_BLOCK_AFFECTED));
    }

    @Test
    public void testNoDaoExtraData() {
        final StandaloneBlockchain sb = createBlockchain(false);

        for (int i = 0; i < FORK_BLOCK + 30; i++) {
            Block b = sb.createBlock();
        }

        assertEquals("EthereumJ powered", getData(sb, FORK_BLOCK - 1));
        assertEquals("", getData(sb, FORK_BLOCK));
        assertEquals("", getData(sb, FORK_BLOCK + FORK_BLOCK_AFFECTED - 1));
        assertEquals("EthereumJ powered", getData(sb, FORK_BLOCK + FORK_BLOCK_AFFECTED));
    }

    private String getData(StandaloneBlockchain sb, long blockNumber) {
        return new String(sb.getBlockchain().getBlockByNumber(blockNumber).getExtraData());
    }

    private StandaloneBlockchain createBlockchain(boolean proFork) {
        final BaseNetConfig netConfig = new BaseNetConfig();
        final FrontierConfig c1 = StandaloneBlockchain.getEasyMiningConfig();
        netConfig.add(0, StandaloneBlockchain.getEasyMiningConfig());
        netConfig.add(FORK_BLOCK, proFork ? new DaoHFConfig(c1, FORK_BLOCK) : new DaoNoHFConfig(c1, FORK_BLOCK));

        // create blockchain
        return new StandaloneBlockchain()
                .withAutoblock(true)
                .withNetConfig(netConfig);
    }
}
