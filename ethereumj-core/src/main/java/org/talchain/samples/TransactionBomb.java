package org.talchain.samples;

import org.talchain.core.Block;
import org.talchain.core.Transaction;
import org.talchain.core.TransactionReceipt;
import org.talchain.facade.Ethereum;
import org.talchain.facade.EthereumFactory;
import org.talchain.listener.EthereumListenerAdapter;
import org.spongycastle.util.encoders.Hex;
import org.talchain.crypto.HashUtil;

import java.util.Collections;
import java.util.List;

import static org.talchain.crypto.HashUtil.sha3;
import static org.talchain.util.ByteUtil.longToBytesNoLeadZeroes;

public class TransactionBomb extends EthereumListenerAdapter {


    Ethereum ethereum = null;
    boolean startedTxBomb = false;

    public TransactionBomb(Ethereum ethereum) {
        this.ethereum = ethereum;
    }

    public static void main(String[] args) {

        Ethereum ethereum = EthereumFactory.createEthereum();
        ethereum.addListener(new TransactionBomb(ethereum));
    }


    @Override
    public void onSyncDone(SyncState state) {

        // We will send transactions only
        // after we have the full chain syncs
        // - in order to prevent old nonce usage
        startedTxBomb = true;
        System.err.println(" ~~~ SYNC DONE ~~~ ");
    }

    @Override
    public void onBlock(Block block, List<TransactionReceipt> receipts) {

        if (startedTxBomb){
            byte[] sender = Hex.decode("cd2a3d9f938e13cd947ec05abc7fe734df8dd826");
            long nonce = ethereum.getRepository().getNonce(sender).longValue();;

            for (int i=0; i < 20; ++i){
                sendTx(nonce);
                ++nonce;
                sleep(10);
            }
        }
    }

    private void sendTx(long nonce){

        byte[] gasPrice = longToBytesNoLeadZeroes(1_000_000_000_000L);
        byte[] gasLimit = longToBytesNoLeadZeroes(21000);

        byte[] toAddress = Hex.decode("9f598824ffa7068c1f2543f04efb58b6993db933");
        byte[] value = longToBytesNoLeadZeroes(10_000);

        Transaction tx = new Transaction(longToBytesNoLeadZeroes(nonce),
                gasPrice,
                gasLimit,
                toAddress,
                value,
                null,
                ethereum.getChainIdForNextBlock());

        byte[] privKey = HashUtil.sha3("cow".getBytes());
        tx.sign(privKey);

        ethereum.getChannelManager().sendTransaction(Collections.singletonList(tx), null);
        System.err.println("Sending tx: " + Hex.toHexString(tx.getHash()));
    }

    private void sleep(int millis){
        try {Thread.sleep(millis);}
        catch (InterruptedException e) {e.printStackTrace();}
    }
}
