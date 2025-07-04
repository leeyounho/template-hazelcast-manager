package com.younho.hazelcast.performance;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Disabled("DbRepository 구현 후 활성화 필요")
public class DbRepositoryLimitTest_FewLargeObjects extends AbstractDbModeTest {

    @Test
    @DisplayName("[Database Mode] 용량 한계 시나리오 1: 소수의 대용량 데이터")
    public void limitTest_FewLargeObjects() {
        super.execute_limitTest_FewLargeObjects();
    }
}