package org.talchain.facade;

import org.ethereum.core.*;
import org.talchain.core.Repository;
import org.talchain.core.Transaction;

import java.util.List;

/**
 * @author Mikhail Kalinin
 * @since 28.09.2015
 */
public interface PendingState {

    /**
     * @return pending state repository
     */
    Repository getRepository();

    /**
     * @return list of pending transactions
     */
    List<Transaction> getPendingTransactions();
}
