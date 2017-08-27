package org.talchain.datasource;

import org.talchain.datasource.leveldb.LevelDbDataSource;
import org.junit.Ignore;
import org.junit.Test;
import org.talchain.TestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore
public class LevelDbDataSourceTest {

    @Test
    public void testBatchUpdating() {
        LevelDbDataSource dataSource = new LevelDbDataSource("test");
        dataSource.init();

        final int batchSize = 100;
        Map<byte[], byte[]> batch = createBatch(batchSize);
        
        dataSource.updateBatch(batch);

        assertEquals(batchSize, dataSource.keys().size());
        
        dataSource.close();
    }

    @Test
    public void testPutting() {
        LevelDbDataSource dataSource = new LevelDbDataSource("test");
        dataSource.init();

        byte[] key = TestUtils.randomBytes(32);
        dataSource.put(key, TestUtils.randomBytes(32));

        assertNotNull(dataSource.get(key));
        assertEquals(1, dataSource.keys().size());
        
        dataSource.close();
    }

    private static Map<byte[], byte[]> createBatch(int batchSize) {
        HashMap<byte[], byte[]> result = new HashMap<>();
        for (int i = 0; i < batchSize; i++) {
            result.put(TestUtils.randomBytes(32), TestUtils.randomBytes(32));
        }
        return result;
    }

}