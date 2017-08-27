package org.talchain.jsontestsuite;

import org.talchain.config.SystemProperties;
import org.talchain.config.blockchain.DaoHFConfig;
import org.talchain.config.blockchain.Eip150HFConfig;
import org.talchain.config.blockchain.FrontierConfig;
import org.talchain.config.blockchain.HomesteadConfig;
import org.talchain.config.net.BaseNetConfig;
import org.talchain.config.net.MainNetConfig;
import org.talchain.jsontestsuite.suite.JSONReader;
import org.json.simple.parser.ParseException;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.Collections;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GitHubTestNetTest {

    //SHACOMMIT of tested commit, ethereum/tests.git
    public String shacommit = "9ed33d7440f13c09ce7f038f92abd02d23b26f0d";

    @Before
    public void setup() {
        SystemProperties.getDefault().setGenesisInfo("frontier.json");
        SystemProperties.getDefault().setBlockchainConfig(new BaseNetConfig() {{
            add(0, new FrontierConfig());
            add(5, new HomesteadConfig());
            add(8, new DaoHFConfig(new HomesteadConfig(), 8));
            add(10, new Eip150HFConfig(new DaoHFConfig(new HomesteadConfig(), 8)));

        }});
    }

    @After
    public void clean() {
        SystemProperties.getDefault().setBlockchainConfig(MainNetConfig.INSTANCE);
    }

    @Test
    public void bcEIP150Test() throws ParseException, IOException {
        String json = JSONReader.loadJSONFromCommit("BlockchainTests/TestNetwork/bcEIP150Test.json", shacommit);
        GitHubJSONTestSuite.runGitHubJsonBlockTest(json, Collections.EMPTY_SET);
    }
    @Test
    public void bcSimpleTransitionTest() throws ParseException, IOException {
        String json = JSONReader.loadJSONFromCommit("BlockchainTests/TestNetwork/bcSimpleTransitionTest.json", shacommit);
        GitHubJSONTestSuite.runGitHubJsonBlockTest(json, Collections.EMPTY_SET);
    }
    @Test
    public void bcTheDaoTest() throws ParseException, IOException {
        String json = JSONReader.loadJSONFromCommit("BlockchainTests/TestNetwork/bcTheDaoTest.json", shacommit);
        GitHubJSONTestSuite.runGitHubJsonBlockTest(json, Collections.EMPTY_SET);
    }
}
