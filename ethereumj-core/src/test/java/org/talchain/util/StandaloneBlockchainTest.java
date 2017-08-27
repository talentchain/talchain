package org.talchain.util;

import org.talchain.config.SystemProperties;
import org.talchain.crypto.ECKey;
import org.talchain.util.blockchain.SolidityContract;
import org.talchain.util.blockchain.StandaloneBlockchain;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.talchain.util.blockchain.EtherUtil;

import java.math.BigInteger;

/**
 * Created by Anton Nashatyrev on 06.07.2016.
 */
public class StandaloneBlockchainTest {

    @AfterClass
    public static void cleanup() {
        SystemProperties.resetToDefault();
    }

    @Test
    public void constructorTest() {
        StandaloneBlockchain sb = new StandaloneBlockchain().withAutoblock(true);
        SolidityContract a = sb.submitNewContract(
                "contract A {" +
                        "  uint public a;" +
                        "  uint public b;" +
                        "  function A(uint a_, uint b_) {a = a_; b = b_; }" +
                        "}",
                "A", 555, 777
        );
        Assert.assertEquals(BigInteger.valueOf(555), a.callConstFunction("a")[0]);
        Assert.assertEquals(BigInteger.valueOf(777), a.callConstFunction("b")[0]);

        SolidityContract b = sb.submitNewContract(
                "contract A {" +
                        "  string public a;" +
                        "  uint public b;" +
                        "  function A(string a_, uint b_) {a = a_; b = b_; }" +
                        "}",
                "A", "This string is longer than 32 bytes...", 777
        );
        Assert.assertEquals("This string is longer than 32 bytes...", b.callConstFunction("a")[0]);
        Assert.assertEquals(BigInteger.valueOf(777), b.callConstFunction("b")[0]);
    }

    @Test
    public void fixedSizeArrayTest() {
        StandaloneBlockchain sb = new StandaloneBlockchain().withAutoblock(true);
        {
            SolidityContract a = sb.submitNewContract(
                    "contract A {" +
                            "  uint public a;" +
                            "  uint public b;" +
                            "  address public c;" +
                            "  address public d;" +
                            "  function f(uint[2] arr, address[2] arr2) {a = arr[0]; b = arr[1]; c = arr2[0]; d = arr2[1];}" +
                            "}");
            ECKey addr1 = new ECKey();
            ECKey addr2 = new ECKey();
            a.callFunction("f", new Integer[]{111, 222}, new byte[][] {addr1.getAddress(), addr2.getAddress()});
            Assert.assertEquals(BigInteger.valueOf(111), a.callConstFunction("a")[0]);
            Assert.assertEquals(BigInteger.valueOf(222), a.callConstFunction("b")[0]);
            Assert.assertArrayEquals(addr1.getAddress(), (byte[])a.callConstFunction("c")[0]);
            Assert.assertArrayEquals(addr2.getAddress(), (byte[])a.callConstFunction("d")[0]);
        }

        {
            ECKey addr1 = new ECKey();
            ECKey addr2 = new ECKey();
            SolidityContract a = sb.submitNewContract(
                    "contract A {" +
                            "  uint public a;" +
                            "  uint public b;" +
                            "  address public c;" +
                            "  address public d;" +
                            "  function A(uint[2] arr, address a1, address a2) {a = arr[0]; b = arr[1]; c = a1; d = a2;}" +
                            "}", "A",
                    new Integer[]{111, 222}, addr1.getAddress(), addr2.getAddress());
            Assert.assertEquals(BigInteger.valueOf(111), a.callConstFunction("a")[0]);
            Assert.assertEquals(BigInteger.valueOf(222), a.callConstFunction("b")[0]);
            Assert.assertArrayEquals(addr1.getAddress(), (byte[]) a.callConstFunction("c")[0]);
            Assert.assertArrayEquals(addr2.getAddress(), (byte[]) a.callConstFunction("d")[0]);

            String a1 = "0x1111111111111111111111111111111111111111";
            String a2 = "0x2222222222222222222222222222222222222222";
        }
    }

    @Test
    public void encodeTest1() {
        StandaloneBlockchain sb = new StandaloneBlockchain().withAutoblock(true);
        SolidityContract a = sb.submitNewContract(
                "contract A {" +
                        "  uint public a;" +
                        "  function f(uint a_) {a = a_;}" +
                        "}");
        a.callFunction("f", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        BigInteger r = (BigInteger) a.callConstFunction("a")[0];
        System.out.println(r.toString(16));
        Assert.assertEquals(new BigInteger(Hex.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff")), r);
    }

    @Test
    public void invalidTxTest() {
        // check that invalid tx doesn't break implementation
        StandaloneBlockchain sb = new StandaloneBlockchain();
        ECKey alice = sb.getSender();
        ECKey bob = new ECKey();
        sb.sendEther(bob.getAddress(), BigInteger.valueOf(1000));
        sb.setSender(bob);
        sb.sendEther(alice.getAddress(), BigInteger.ONE);
        sb.setSender(alice);
        sb.sendEther(bob.getAddress(), BigInteger.valueOf(2000));

        sb.createBlock();
    }

    @Test
    public void initBalanceTest() {
        // check StandaloneBlockchain.withAccountBalance method
        StandaloneBlockchain sb = new StandaloneBlockchain();
        ECKey alice = sb.getSender();
        ECKey bob = new ECKey();
        sb.withAccountBalance(bob.getAddress(), EtherUtil.convert(123, EtherUtil.Unit.ETHER));

        BigInteger aliceInitBal = sb.getBlockchain().getRepository().getBalance(alice.getAddress());
        BigInteger bobInitBal = sb.getBlockchain().getRepository().getBalance(bob.getAddress());
        assert EtherUtil.convert(123, EtherUtil.Unit.ETHER).equals(bobInitBal);

        sb.setSender(bob);
        sb.sendEther(alice.getAddress(), BigInteger.ONE);

        sb.createBlock();

        assert EtherUtil.convert(123, EtherUtil.Unit.ETHER).compareTo(sb.getBlockchain().getRepository().getBalance(bob.getAddress())) > 0;
        assert aliceInitBal.add(BigInteger.ONE).equals(sb.getBlockchain().getRepository().getBalance(alice.getAddress()));
    }

}