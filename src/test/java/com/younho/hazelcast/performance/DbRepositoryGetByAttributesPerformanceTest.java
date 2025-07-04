package com.younho.hazelcast.performance;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Disabled("DbRepository 구현 후 활성화 필요")
public class DbRepositoryGetByAttributesPerformanceTest extends AbstractDbModeTest {

    @Test
    @DisplayName("[Database Mode] 복합 인덱스 속성 기반 조회 성능 측정")
    public void get_by_attributes_performance_test() {
        super.execute_get_by_attributes_performance_test();
    }
}