package com.younho.hazelcast.performance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CacheRepositoryBulkSavePerformanceTest extends AbstractCacheModeTest {

    @Test
    @DisplayName("[Cache Mode] 대량 데이터 저장 성능 측정")
    public void bulk_save_performance_test() {
        super.execute_bulk_save_performance_test();
    }
}