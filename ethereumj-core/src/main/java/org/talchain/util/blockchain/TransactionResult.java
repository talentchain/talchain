package org.talchain.util.blockchain;

import org.talchain.core.TransactionExecutionSummary;
import org.talchain.core.TransactionReceipt;

/**
 * Created by Anton Nashatyrev on 26.07.2016.
 */
public class TransactionResult {
    TransactionReceipt receipt;
    TransactionExecutionSummary executionSummary;

    public boolean isIncluded() {
        return receipt != null;
    }

    public TransactionReceipt getReceipt() {
        return receipt;
    }

    public TransactionExecutionSummary getExecutionSummary() {
        return executionSummary;
    }
}
