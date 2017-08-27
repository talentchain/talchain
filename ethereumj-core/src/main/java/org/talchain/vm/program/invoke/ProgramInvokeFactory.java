package org.talchain.vm.program.invoke;

import org.talchain.core.Block;
import org.talchain.core.Repository;
import org.talchain.core.Transaction;
import org.talchain.db.BlockStore;
import org.talchain.vm.DataWord;
import org.talchain.vm.program.Program;

import java.math.BigInteger;

/**
 * @author Roman Mandeleil
 * @since 19.12.2014
 */
public interface ProgramInvokeFactory {

    ProgramInvoke createProgramInvoke(Transaction tx, Block block,
                                      Repository repository, BlockStore blockStore);

    ProgramInvoke createProgramInvoke(Program program, DataWord toAddress, DataWord callerAddress,
                                      DataWord inValue, DataWord inGas,
                                      BigInteger balanceInt, byte[] dataIn,
                                      Repository repository, BlockStore blockStore, boolean byTestingSuite);


}
