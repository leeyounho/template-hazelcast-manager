package com.younho.hazelcast;

import com.hazelcast.map.IMap;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class DCOLDataHistCacheRepositoryTest extends AbstractDCOLDataHistRepositoryTest {

    @Autowired
    private DCOLDataHistCacheRepository cacheRepository;

    @Autowired
    private HazelcastManager hazelcastManager;

    private IMap<Long, DCOLDataHist> dcolHistMap;

    @Override
    protected DCOLDataHistRepository createRepository() {
        this.dcolHistMap = hazelcastManager.getInstance().getMap("dcolHist");
        return cacheRepository;
    }

    @Override
    protected void cleanup() {
        if (dcolHistMap != null) {
            dcolHistMap.clear();
        }
    }
}