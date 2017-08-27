package org.talchain.util.blockchain;

import org.talchain.core.CallTransaction;

/**
 * Created by Anton Nashatyrev on 02.03.2017.
 */
public interface SolidityFunction {

    SolidityContract getContract();

    CallTransaction.Function getInterface();
}
