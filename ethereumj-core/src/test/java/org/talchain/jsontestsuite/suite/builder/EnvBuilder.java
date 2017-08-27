package org.talchain.jsontestsuite.suite.builder;

import org.talchain.jsontestsuite.suite.Env;
import org.talchain.jsontestsuite.suite.model.EnvTck;

import static org.talchain.jsontestsuite.suite.Utils.parseData;
import static org.talchain.jsontestsuite.suite.Utils.parseNumericData;
import static org.talchain.jsontestsuite.suite.Utils.parseVarData;

public class EnvBuilder {

    public static Env build(EnvTck envTck){
        byte[] coinbase = parseData(envTck.getCurrentCoinbase());
        byte[] difficulty = parseVarData(envTck.getCurrentDifficulty());
        byte[] gasLimit = parseVarData(envTck.getCurrentGasLimit());
        byte[] number = parseNumericData(envTck.getCurrentNumber());
        byte[] timestamp = parseNumericData(envTck.getCurrentTimestamp());
        byte[] hash = parseData(envTck.getPreviousHash());

        return new Env(coinbase, difficulty, gasLimit, number, timestamp, hash);
    }

}
