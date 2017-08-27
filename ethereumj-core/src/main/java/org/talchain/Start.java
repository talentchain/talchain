package org.talchain;

import org.apache.commons.lang3.StringUtils;
import org.talchain.cli.CLIInterface;
import org.talchain.config.SystemProperties;
import org.talchain.facade.Ethereum;
import org.talchain.facade.EthereumFactory;
import org.talchain.mine.Ethash;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author Roman Mandeleil
 * @since 14.11.2014
 */
public class Start {

    public static void main(String args[]) throws IOException, URISyntaxException {
        CLIInterface.call(args);

        final SystemProperties config = SystemProperties.getDefault();
        final boolean actionBlocksLoader = !config.blocksLoader().equals("");
        final boolean actionGenerateDag = !StringUtils.isEmpty(System.getProperty("ethash.blockNumber"));

        if (actionBlocksLoader || actionGenerateDag) {
            config.setSyncEnabled(false);
            config.setDiscoveryEnabled(false);
        }

        if (actionGenerateDag) {
            new Ethash(config, Long.parseLong(System.getProperty("ethash.blockNumber"))).getFullDataset();
            // DAG file has been created, lets exit
            System.exit(0);
        } else {
            Ethereum ethereum = EthereumFactory.createEthereum();

            if (actionBlocksLoader) {
                ethereum.getBlockLoader().loadBlocks();
            }
        }
    }

}
