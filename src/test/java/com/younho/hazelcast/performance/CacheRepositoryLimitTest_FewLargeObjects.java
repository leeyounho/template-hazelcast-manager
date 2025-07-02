package com.younho.hazelcast.performance;

import com.younho.hazelcast.DCOLDataHist;
import com.younho.hazelcast.DCOLDataHistHazelcastRepository;
import com.younho.hazelcast.HazelcastManager;
import com.younho.hazelcast.TestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class CacheRepositoryLimitTest_FewLargeObjects extends AbstractDCOLDataHistRepositoryPerformanceTest {
    private String mapName = "dcolHist";

    @Autowired
    private DCOLDataHistHazelcastRepository cacheRepository;

    @Autowired
    private HazelcastManager hazelcastManager;

    @Override
    protected String getModeName() {
        return "Cache Mode";
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
        hazelcastManager.getInstance().getMap(mapName).clear();
    }

    @BeforeEach
    void setUp() {
        cleanup();
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    @Test
    @DisplayName("[Cache Mode] 용량 한계 시나리오 1: 소수의 대용량 데이터")
    @Override
    public void limitTest_FewLargeObjects() {
        super.limitTest_FewLargeObjects();
    }
}
