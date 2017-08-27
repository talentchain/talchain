package org.talchain.core;

import org.talchain.util.RLP;
import org.talchain.util.RLPList;
import org.spongycastle.util.encoders.Hex;
import org.talchain.net.eth.message.EthMessageCodes;

import java.math.BigInteger;

import static org.talchain.util.ByteUtil.byteArrayToLong;

/**
 * Block identifier holds block hash and number <br>
 * This tuple is used in some places of the core,
 * like by {@link EthMessageCodes#NEW_BLOCK_HASHES} message wrapper
 *
 * @author Mikhail Kalinin
 * @since 04.09.2015
 */
public class BlockIdentifier {

    /**
     * Block hash
     */
    private byte[] hash;

    /**
     * Block number
     */
    private long number;

    public BlockIdentifier(RLPList rlp) {
        this.hash = rlp.get(0).getRLPData();
        this.number = byteArrayToLong(rlp.get(1).getRLPData());
    }

    public BlockIdentifier(byte[] hash, long number) {
        this.hash = hash;
        this.number = number;
    }

    public byte[] getHash() {
        return hash;
    }

    public long getNumber() {
        return number;
    }

    public byte[] getEncoded() {
        byte[] hash = RLP.encodeElement(this.hash);
        byte[] number = RLP.encodeBigInteger(BigInteger.valueOf(this.number));

        return RLP.encodeList(hash, number);
    }

    @Override
    public String toString() {
        return "BlockIdentifier {" +
                "hash=" + Hex.toHexString(hash) +
                ", number=" + number +
                '}';
    }
}
