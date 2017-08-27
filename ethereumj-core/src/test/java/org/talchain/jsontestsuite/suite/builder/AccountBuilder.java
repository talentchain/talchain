package org.talchain.jsontestsuite.suite.builder;

import org.talchain.config.SystemProperties;
import org.talchain.core.AccountState;
import org.talchain.jsontestsuite.suite.ContractDetailsImpl;
import org.talchain.jsontestsuite.suite.model.AccountTck;
import org.talchain.vm.DataWord;
import org.talchain.crypto.HashUtil;
import org.talchain.util.Utils;

import java.util.HashMap;
import java.util.Map;

import static org.talchain.crypto.HashUtil.sha3;
import static org.talchain.jsontestsuite.suite.Utils.parseData;

public class AccountBuilder {

    public static StateWrap build(AccountTck account) {

        ContractDetailsImpl details = new ContractDetailsImpl();
        details.setCode(parseData(account.getCode()));
        details.setStorage(convertStorage(account.getStorage()));

        AccountState state = new AccountState(SystemProperties.getDefault())
                .withBalanceIncrement(Utils.unifiedNumericToBigInteger(account.getBalance()))
                .withNonce(Utils.unifiedNumericToBigInteger(account.getNonce()))
                .withStateRoot(details.getStorageHash())
                .withCodeHash(HashUtil.sha3(details.getCode()));

        return new StateWrap(state, details);
    }


    private static Map<DataWord, DataWord> convertStorage(Map<String, String> storageTck) {

        Map<DataWord, DataWord> storage = new HashMap<>();

        for (String keyTck : storageTck.keySet()) {
            String valueTck = storageTck.get(keyTck);

            DataWord key = new DataWord(parseData(keyTck));
            DataWord value = new DataWord(parseData(valueTck));

            storage.put(key, value);
        }

        return storage;
    }


    public static class StateWrap {

        AccountState accountState;
        ContractDetailsImpl contractDetails;

        public StateWrap(AccountState accountState, ContractDetailsImpl contractDetails) {
            this.accountState = accountState;
            this.contractDetails = contractDetails;
        }

        public AccountState getAccountState() {
            return accountState;
        }

        public ContractDetailsImpl getContractDetails() {
            return contractDetails;
        }
    }
}
