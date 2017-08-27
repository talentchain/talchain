package org.talchain.jsontestsuite.suite.builder;

import org.talchain.core.AccountState;
import org.talchain.core.Repository;
import org.talchain.datasource.inmem.HashMapDB;
import org.talchain.datasource.NoDeleteSource;
import org.talchain.jsontestsuite.suite.IterableTestRepository;
import org.talchain.db.RepositoryRoot;
import org.talchain.db.ByteArrayWrapper;
import org.talchain.db.ContractDetails;
import org.talchain.jsontestsuite.suite.ContractDetailsCacheImpl;
import org.talchain.jsontestsuite.suite.model.AccountTck;

import java.util.HashMap;
import java.util.Map;

import static org.talchain.jsontestsuite.suite.Utils.parseData;
import static org.talchain.util.ByteUtil.wrap;

public class RepositoryBuilder {

    public static Repository build(Map<String, AccountTck> accounts){
        HashMap<ByteArrayWrapper, AccountState> stateBatch = new HashMap<>();
        HashMap<ByteArrayWrapper, ContractDetails> detailsBatch = new HashMap<>();

        for (String address : accounts.keySet()) {

            AccountTck accountTCK = accounts.get(address);
            AccountBuilder.StateWrap stateWrap = AccountBuilder.build(accountTCK);

            AccountState state = stateWrap.getAccountState();
            ContractDetails details = stateWrap.getContractDetails();

            stateBatch.put(wrap(parseData(address)), state);

            ContractDetailsCacheImpl detailsCache = new ContractDetailsCacheImpl(details);
            detailsCache.setDirty(true);

            detailsBatch.put(wrap(parseData(address)), detailsCache);
        }

        Repository repositoryDummy = new IterableTestRepository(new RepositoryRoot(new NoDeleteSource<>(new HashMapDB<byte[]>())));
        Repository track = repositoryDummy.startTracking();

        track.updateBatch(stateBatch, detailsBatch);
        track.commit();
        repositoryDummy.commit();

        return repositoryDummy;
    }
}
