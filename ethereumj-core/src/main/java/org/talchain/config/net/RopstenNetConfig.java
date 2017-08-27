package org.talchain.config.net;

import org.ethereum.config.blockchain.*;
import org.talchain.config.blockchain.HomesteadConfig;
import org.talchain.config.blockchain.RopstenConfig;

/**
 * Created by Anton Nashatyrev on 25.02.2016.
 */
public class RopstenNetConfig extends BaseNetConfig {

    public RopstenNetConfig() {
        add(0, new HomesteadConfig());
        add(10, new RopstenConfig(new HomesteadConfig()));
    }
}
