package org.talchain.core;


import org.talchain.config.NoAutoscan;
import org.talchain.config.SystemProperties;
import org.talchain.datasource.inmem.HashMapDB;
import org.talchain.db.BlockStore;
import org.talchain.db.IndexedBlockStore;
import org.talchain.manager.WorldManager;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@NoAutoscan
public class ImportTest {

    private static final Logger logger = LoggerFactory.getLogger("test");

    @Configuration
    @ComponentScan(basePackages = "org.ethereum")
    @NoAutoscan
    static class ContextConfiguration {

        @Bean
        public BlockStore blockStore(){

            IndexedBlockStore blockStore = new IndexedBlockStore();
            blockStore.init(new HashMapDB<byte[]>(), new HashMapDB<byte[]>());

            return blockStore;
        }
    }

    @Autowired
    WorldManager worldManager;

    @AfterClass
    public static void close(){
//        FileUtil.recursiveDelete(CONFIG.databaseDir());
    }


    @Ignore
    @Test
    public void testScenario1() throws URISyntaxException, IOException {

        BlockchainImpl blockchain = (BlockchainImpl) worldManager.getBlockchain();
        logger.info("Running as: {}", SystemProperties.getDefault().genesisInfo());

        URL scenario1 = ClassLoader
                .getSystemResource("blockload/scenario1.dmp");

        File file = new File(scenario1.toURI());
        List<String> strData = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

        byte[] root = Genesis.getInstance().getStateRoot();
        for (String blockRLP : strData) {
            Block block = new Block(
                    Hex.decode(blockRLP));
            logger.info("sending block.hash: {}", Hex.toHexString(block.getHash()));
            blockchain.tryToConnect(block);
            root = block.getStateRoot();
        }

        Repository repository = (Repository)worldManager.getRepository();
        logger.info("asserting root state is: {}", Hex.toHexString(root));
        assertEquals(Hex.toHexString(root),
                Hex.toHexString(repository.getRoot()));

    }

}