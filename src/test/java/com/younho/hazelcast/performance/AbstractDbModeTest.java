package com.younho.hazelcast.performance;

import com.younho.hazelcast.DCOLDataHist;
import com.younho.hazelcast.DCOLDataHistDbRepository;
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
public abstract class AbstractDbModeTest extends AbstractPerformanceTestLogic {

    @Autowired
    private DCOLDataHistDbRepository dbRepository;

    @BeforeEach
    void setUp() {
        cleanup();
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    @Override
    protected String getModeName() {
        return "Database Mode";
    }

    @Override
    protected List<DCOLDataHist> performGetByAttributes(Map<String, Object> attributes) {
        return dbRepository.getByAttributes(attributes);
    }

    @Override
    protected void performBulkSave(List<DCOLDataHist> records) {
        dbRepository.saveOrUpdateAll(records);
    }

    @Override
    protected void performBulkDeleteByAttributes(Map<String, Object> attributes) {
        dbRepository.deleteByAttributes(attributes);
    }

    @Override
    protected long getRecordCount() {
        // TODO
        return 0L;
    }

    @Override
    protected void cleanup() {
        // TODO
    }
}