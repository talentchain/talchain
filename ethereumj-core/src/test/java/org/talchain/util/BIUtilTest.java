package org.talchain.util;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Mikhail Kalinin
 * @since 15.10.2015
 */
public class BIUtilTest {

    @Test
    public void testIsIn20PercentRange() {

        assertTrue(BIUtil.isIn20PercentRange(BigInteger.valueOf(20000), BigInteger.valueOf(24000)));

        assertTrue(BIUtil.isIn20PercentRange(BigInteger.valueOf(24000), BigInteger.valueOf(20000)));

        assertFalse(BIUtil.isIn20PercentRange(BigInteger.valueOf(20000), BigInteger.valueOf(25000)));

        assertTrue(BIUtil.isIn20PercentRange(BigInteger.valueOf(20), BigInteger.valueOf(24)));

        assertTrue(BIUtil.isIn20PercentRange(BigInteger.valueOf(24), BigInteger.valueOf(20)));

        assertFalse(BIUtil.isIn20PercentRange(BigInteger.valueOf(20), BigInteger.valueOf(25)));

        assertTrue(BIUtil.isIn20PercentRange(BigInteger.ZERO, BigInteger.ZERO));

        assertFalse(BIUtil.isIn20PercentRange(BigInteger.ZERO, BigInteger.ONE));

        assertTrue(BIUtil.isIn20PercentRange(BigInteger.ONE, BigInteger.ZERO));
    }

    @Test // test isIn20PercentRange
    public void test1() {
        assertFalse(BIUtil.isIn20PercentRange(BigInteger.ONE, BigInteger.valueOf(5)));
        assertTrue(BIUtil.isIn20PercentRange(BigInteger.valueOf(5), BigInteger.ONE));
        assertTrue(BIUtil.isIn20PercentRange(BigInteger.valueOf(5), BigInteger.valueOf(6)));
        assertFalse(BIUtil.isIn20PercentRange(BigInteger.valueOf(5), BigInteger.valueOf(7)));
    }
}
