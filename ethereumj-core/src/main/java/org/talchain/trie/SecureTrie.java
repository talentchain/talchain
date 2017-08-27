package org.talchain.trie;

import org.talchain.datasource.Source;
import org.talchain.crypto.HashUtil;

import static org.talchain.crypto.HashUtil.sha3;
import static org.talchain.util.ByteUtil.EMPTY_BYTE_ARRAY;

public class SecureTrie extends TrieImpl {

    public SecureTrie(byte[] root) {
        super(root);
    }

    public SecureTrie(Source<byte[], byte[]> cache) {
        super(cache, null);
    }

    public SecureTrie(Source<byte[], byte[]> cache, byte[] root) {
        super(cache, root);
    }

    @Override
    public byte[] get(byte[] key) {
        return super.get(HashUtil.sha3(key));
    }

    @Override
    public void put(byte[] key, byte[] value) {
        super.put(HashUtil.sha3(key), value);
    }

    @Override
    public void delete(byte[] key) {
        put(key, EMPTY_BYTE_ARRAY);
    }
}
