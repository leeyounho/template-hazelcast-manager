package com.younho.hazelcast.performance;

import com.younho.hazelcast.DCOLDataHist;
import com.younho.hazelcast.DCOLDataHistHazelcastRepository;
import com.younho.hazelcast.HazelcastManager;
import com.younho.hazelcast.TestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public abstract class AbstractCacheModeTest extends AbstractPerformanceTestLogic {
    private final String mapName = "dcolHist";

    @Autowired
    private DCOLDataHistHazelcastRepository cacheRepository;

    @Autowired
    private HazelcastManager hazelcastManager;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    @Override
    protected String getModeName() {
        return "Cache Mode";
    }

    @Override
    protected List<DCOLDataHist> performGetByAttributes(Map<String, Object> attributes) {
        return cacheRepository.getByAttributes(attributes);
    }

    @Override
    protected void performBulkSave(List<DCOLDataHist> records) {
        cacheRepository.saveOrUpdateAll(records);
    }

    @Override
    protected void performBulkDeleteByAttributes(Map<String, Object> attributes) {
        cacheRepository.deleteByAttributes(attributes);
    }

    @Override
    protected long getRecordCount() {
        return hazelcastManager.getInstance().getMap(mapName).size();
    }

    @Override
    protected void cleanup() {
        if (hazelcastManager.isInitialized()) {
            hazelcastManager.getInstance().shutdown();
        }
    }
}