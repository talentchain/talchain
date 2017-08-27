package org.talchain.jsontestsuite.suite;

import org.talchain.core.Block;
import org.talchain.core.Transaction;
import org.talchain.db.BlockStore;
import org.talchain.core.Repository;
import org.talchain.util.ByteUtil;
import org.talchain.vm.DataWord;
import org.talchain.vm.program.Program;
import org.talchain.vm.program.invoke.ProgramInvoke;
import org.talchain.vm.program.invoke.ProgramInvokeFactory;
import org.talchain.vm.program.invoke.ProgramInvokeImpl;

import java.math.BigInteger;

/**
 * @author Roman Mandeleil
 * @since 19.12.2014
 */
public class TestProgramInvokeFactory implements ProgramInvokeFactory {

    private final Env env;

    public TestProgramInvokeFactory(Env env) {
        this.env = env;
    }


    @Override
    public ProgramInvoke createProgramInvoke(org.talchain.core.Transaction tx, Block block, Repository repository, BlockStore blockStore) {
        return generalInvoke(tx, repository, blockStore);
    }

    @Override
    public ProgramInvoke createProgramInvoke(Program program, DataWord toAddress, DataWord callerAddress,
                                             DataWord inValue, DataWord inGas,
                                             BigInteger balanceInt, byte[] dataIn,
                                             Repository repository, BlockStore blockStore, boolean byTestingSuite) {
        return null;
    }


    private ProgramInvoke generalInvoke(Transaction tx, Repository repository, BlockStore blockStore) {

        /***         ADDRESS op       ***/
        // YP: Get address of currently executing account.
        byte[] address = tx.isContractCreation() ? tx.getContractAddress() : tx.getReceiveAddress();

        /***         ORIGIN op       ***/
        // YP: This is the sender of original transaction; it is never a contract.
        byte[] origin = tx.getSender();

        /***         CALLER op       ***/
        // YP: This is the address of the account that is directly responsible for this execution.
        byte[] caller = tx.getSender();

        /***         BALANCE op       ***/
        byte[] balance = repository.getBalance(address).toByteArray();

        /***         GASPRICE op       ***/
        byte[] gasPrice = tx.getGasPrice();

        /*** GAS op ***/
        byte[] gas = tx.getGasLimit();

        /***        CALLVALUE op      ***/
        byte[] callValue = tx.getValue() == null ? new byte[]{0} : tx.getValue();

        /***     CALLDATALOAD  op   ***/
        /***     CALLDATACOPY  op   ***/
        /***     CALLDATASIZE  op   ***/
        byte[] data = tx.isContractCreation() ? ByteUtil.EMPTY_BYTE_ARRAY :( tx.getData() == null ? ByteUtil.EMPTY_BYTE_ARRAY : tx.getData() );
//        byte[] data =  tx.getData() == null ? ByteUtil.EMPTY_BYTE_ARRAY : tx.getData() ;

        /***    PREVHASH  op  ***/
        byte[] lastHash = env.getPreviousHash();

        /***   COINBASE  op ***/
        byte[] coinbase = env.getCurrentCoinbase();

        /*** TIMESTAMP  op  ***/
        long timestamp = ByteUtil.byteArrayToLong(env.getCurrentTimestamp());

        /*** NUMBER  op  ***/
        long number = ByteUtil.byteArrayToLong(env.getCurrentNumber());

        /*** DIFFICULTY  op  ***/
        byte[] difficulty = env.getCurrentDifficulty();

        /*** GASLIMIT op ***/
        byte[] gaslimit = env.getCurrentGasLimit();

        return new ProgramInvokeImpl(address, origin, caller, balance,
                gasPrice, gas, callValue, data, lastHash, coinbase,
                timestamp, number, difficulty, gaslimit, repository, blockStore);
    }

}
