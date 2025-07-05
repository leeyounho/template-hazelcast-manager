package com.younho.hazelcast;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.util.Date;

@DisplayName("DCOLDataHistRepository 공통 기능 테스트")
public abstract class AbstractDCOLDataHistRepositoryTest {

    protected DCOLDataHistRepository repository;

    protected abstract DCOLDataHistRepository createRepository();

    protected abstract void cleanup();

    @BeforeEach
    void setUp() {
        this.repository = createRepository();
        cleanup();
    }

    protected DCOLDataHist createDummyData(String eqpId, String workId, String controlJobId, String processJobId) {
        DCOLDataHist data = new DCOLDataHist();
        data.setEqpId(eqpId);
        data.setWorkId(workId);
        data.setControlJobId(controlJobId);
        data.setProcessJobId(processJobId);
        data.setDcolDate(new Date());
        data.setDcolName("TEST_PARAM");
        data.setDcolOrder(0L);
        data.setDcolValue(generateLongString(4000));
        return data;
    }

    private String generateLongString(int length) {
        if (length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char ch = (char) ('A' + (i % 26));
            sb.append(ch);
        }
        return sb.toString();
    }
}