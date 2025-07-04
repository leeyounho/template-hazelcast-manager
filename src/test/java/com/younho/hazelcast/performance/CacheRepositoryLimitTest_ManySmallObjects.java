package com.younho.hazelcast.performance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CacheRepositoryLimitTest_ManySmallObjects extends AbstractCacheModeTest {

    @Test
    @DisplayName("[Cache Mode] 용량 한계 시나리오 2: 다수의 소용량 데이터")
    public void limitTest_ManySmallObjects() {
        super.execute_limitTest_ManySmallObjects();
    }
}