package com.younho.hazelcast.performance;

import com.younho.hazelcast.DCOLDataHist;
import com.younho.hazelcast.DCOLDataHistDbRepository;
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
public class DbRepositoryBulkSavePerformanceTest extends AbstractDCOLDataHistRepositoryPerformanceTest {

    @Autowired
    private DCOLDataHistDbRepository dbRepository;

    @Override
    protected String getModeName() {
        return "Database Mode";
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

    @BeforeEach
    void setUp() {
        cleanup();
    }

    @AfterEach
    void tearDown() {
        cleanup();
    }

    @Test
    @DisplayName("대량 데이터 저장 성능 측정")
    @Override
    public void bulk_save_performance_test() {
        super.bulk_save_performance_test();
    }

    @Test
    @DisplayName("대량 데이터 삭제 성능 측정")
    @Override
    public void bulk_delete_by_attribute_performance_test() {
        super.bulk_delete_by_attribute_performance_test();
    }
}
