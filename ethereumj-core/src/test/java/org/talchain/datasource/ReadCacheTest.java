package org.talchain.datasource;

import org.talchain.datasource.inmem.HashMapDB;
import org.talchain.vm.DataWord;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.talchain.crypto.HashUtil;

import static org.talchain.crypto.HashUtil.sha3;
import static org.talchain.util.ByteUtil.longToBytes;
import static org.junit.Assert.*;

/**
 * Testing {@link ReadCache}
 */
public class ReadCacheTest {

    private byte[] intToKey(int i) {
        return HashUtil.sha3(longToBytes(i));
    }

    private byte[] intToValue(int i) {
        return (new DataWord(i)).getData();
    }

    private String str(Object obj) {
        if (obj == null) return null;
        return Hex.toHexString((byte[]) obj);
    }

    @Test
    public void test1() {
        Source<byte[], byte[]> src = new HashMapDB<>();
        ReadCache<byte[], byte[]> readCache = new ReadCache.BytesKey<>(src);
        for (int i = 0; i < 10_000; ++i) {
            src.put(intToKey(i), intToValue(i));
        }
        // Nothing is cached
        assertNull(readCache.getCached(intToKey(0)));
        assertNull(readCache.getCached(intToKey(9_999)));

        for (int i = 0; i < 10_000; ++i) {
            readCache.get(intToKey(i));
        }
        // Everything is cached
        assertEquals(str(intToValue(0)), str(readCache.getCached(intToKey(0)).value()));
        assertEquals(str(intToValue(9_999)), str(readCache.getCached(intToKey(9_999)).value()));

        // Source changes doesn't affect cache
        src.delete(intToKey(13));
        assertEquals(str(intToValue(13)), str(readCache.getCached(intToKey(13)).value()));

        // Flush is not implemented
        assertFalse(readCache.flush());
    }

    @Test
    public void testMaxCapacity() {
        Source<byte[], byte[]> src = new HashMapDB<>();
        ReadCache<byte[], byte[]> readCache = new ReadCache.BytesKey<>(src).withMaxCapacity(100);
        for (int i = 0; i < 10_000; ++i) {
            src.put(intToKey(i), intToValue(i));
            readCache.get(intToKey(i));
        }

        // Only 100 latest are cached
        assertNull(readCache.getCached(intToKey(0)));
        assertEquals(str(intToValue(0)), str(readCache.get(intToKey(0))));
        assertEquals(str(intToValue(0)), str(readCache.getCached(intToKey(0)).value()));
        assertEquals(str(intToValue(9_999)), str(readCache.getCached(intToKey(9_999)).value()));
        // 99_01 - 99_99 and 0 (totally 100)
        assertEquals(str(intToValue(9_901)), str(readCache.getCached(intToKey(9_901)).value()));
        assertNull(readCache.getCached(intToKey(9_900)));
    }
}
