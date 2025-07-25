package com.younho.hazelcast.performance;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Disabled("DbRepository 구현 후 활성화 필요")
public class DbRepositoryDeleteByAttributesPerformanceTest extends AbstractDbModeTest {

    @Test
    @DisplayName("[Database Mode] 대량 데이터 삭제 성능 측정")
    public void bulk_delete_by_attribute_performance_test() {
        super.execute_bulk_delete_by_attribute_performance_test();
    }
}