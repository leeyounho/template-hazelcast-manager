package com.younho.hazelcast.performance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CacheRepositoryLimitTest_FewLargeObjects extends AbstractCacheModeTest {

    @Test
    @DisplayName("[Cache Mode] 용량 한계 시나리오 1: 소수의 대용량 데이터")
    public void limitTest_FewLargeObjects() {
        super.execute_limitTest_FewLargeObjects();
    }
}