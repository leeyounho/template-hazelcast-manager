package com.younho.hazelcast.performance;

import com.younho.hazelcast.DCOLDataHist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractDCOLDataHistRepositoryPerformanceTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractDCOLDataHistRepositoryPerformanceTest.class);

    protected abstract String getModeName();

    protected abstract void performBulkSave(List<DCOLDataHist> records);

    protected abstract void performBulkDeleteByAttributes(Map<String, Object> attributes);

    protected abstract long getRecordCount();

    protected abstract void cleanup();

    protected DCOLDataHist createDummyData(String eqpId, String workId, String controlJobId, String processJobId, int dcolValueLength) {
        DCOLDataHist data = new DCOLDataHist();
        data.setEqpId(eqpId);
        data.setWorkId(workId);
        data.setControlJobId(controlJobId);
        data.setProcessJobId(processJobId);
        data.setDcolDate(new Date());
        data.setDcolName("TEST_PARAM");
        data.setDcolOrder(0L);
        data.setDcolValue(generateLongString(dcolValueLength));
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

    void bulk_save_performance_test() {
        // given
        int recordCount = 50000;
        List<DCOLDataHist> records = new ArrayList<>(recordCount);
        for (int i = 0; i < recordCount; i++) {
            records.add(createDummyData("TEST_EQP", "TEST_WORK", "TEST_CONTROL_JOB", "TEST_PROCESS_JOB", 4000));
        }

        StopWatch stopWatch = new StopWatch(getModeName() + " Bulk Save Test");

        // when
        stopWatch.start(recordCount + "건 저장");
        performBulkSave(records);
        stopWatch.stop();

        // then
        printStopWatchResult(stopWatch, recordCount);

        assertThat(getRecordCount()).isEqualTo(recordCount);
    }

    void bulk_delete_by_attribute_performance_test() {
        int recordCountToDelete = 50000;
        int recordCountToKeep = 50000;

        List<DCOLDataHist> recordsToDelete = new ArrayList<>(recordCountToDelete);
        for (int i = 0; i < recordCountToDelete; i++) {
            recordsToDelete.add(createDummyData("DELETE_EQP", "TEST_WORK", "TEST_CONTROL_JOB", "TEST_PROCESS_JOB", 4000));
        }

        List<DCOLDataHist> recordsToKeep = new ArrayList<>(recordCountToKeep);
        for (int i = 0; i < recordCountToKeep; i++) {
            recordsToKeep.add(createDummyData("KEEP_EQP", "TEST_WORK", "TEST_CONTROL_JOB", "TEST_PROCESS_JOB", 4000));
        }

        performBulkSave(recordsToDelete);
        performBulkSave(recordsToKeep);
        assertThat(getRecordCount()).isEqualTo(recordCountToDelete + recordCountToKeep);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("eqpId", "DELETE_EQP");
        StopWatch stopWatch = new StopWatch(getModeName() + " Bulk Delete by Attributes Test");

        // when
        stopWatch.start(recordCountToDelete + "건 속성 기반 삭제");
        performBulkDeleteByAttributes(attributes);
        stopWatch.stop();

        // then
        printStopWatchResult(stopWatch, recordCountToDelete);

        assertThat(getRecordCount()).isEqualTo(recordCountToKeep);
    }

    protected void runHeapUsageLimitTest(String scenarioName, int stringLength, int batchSize) {
        final List<Integer> TARGET_THRESHOLDS = Arrays.asList(30, 40, 50, 60, 70, 80, 90);
        final Map<Integer, Long> results = new TreeMap<>();
        long totalRecordCount = 0;

        logger.info("==================================================================");
        logger.info("[{}] Starting Heap Usage Limit Test...", scenarioName);
        logger.info("  - String Length per Record: {} chars", stringLength);
        logger.info("  - Batch Size: {}", batchSize);
        logHeapUsage("Test Start");

        try {
            for (int i = 0; i < 1000000; i++) { // 최대 100만 배치 (안전장치)
                List<DCOLDataHist> batchRecords = new ArrayList<>(batchSize);
                for (int j = 0; j < batchSize; j++) {
                    batchRecords.add(createDummyData("TEST_EQP", "TEST_WORK", "TEST_CONTROL_JOB", "TEST_PROCESS_JOB", stringLength));
                }
                performBulkSave(batchRecords);
                totalRecordCount += batchSize;

                MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
                long maxMemory = heapUsage.getMax();
                long usedMemory = heapUsage.getUsed();
                if (maxMemory == 0) continue;
                double currentUsagePercent = ((double) usedMemory / maxMemory) * 100;

                for (int threshold : TARGET_THRESHOLDS) {
                    if (!results.containsKey(threshold) && currentUsagePercent >= threshold) {
                        logger.info("[Threshold Reached] >> {}% usage at ~{} records.", threshold, String.format("%,d", totalRecordCount));
                        results.put(threshold, totalRecordCount);
                    }
                }

                if (currentUsagePercent >= 90) {
                    logger.warn("Heap usage exceeds 90%. Stopping test to prevent instability.");
                    break;
                }
            }
        } catch (Throwable t) {
            logger.error("Test stopped due to throwable (likely OutOfMemoryError): {}", t.getMessage());
        } finally {
            logger.info("====== [{} Test Summary] ======", scenarioName);
            results.forEach((threshold, count) ->
                    logger.info("- {}% usage at: ~{} records", threshold, String.format("%,d", count))
            );
            logger.info("Final total records before stop: {}", String.format("%,d", totalRecordCount));
            logger.info("==================================================================");
        }
    }

    public void limitTest_FewLargeObjects() {
        runHeapUsageLimitTest("Few Large Objects", 4000, 10000);
    }

    public void limitTest_ManySmallObjects() {
        runHeapUsageLimitTest("Many Small Objects", 40, 10000);
    }

    protected void logHeapUsage(String phase) {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();

        long usedMemory = heapUsage.getUsed() / (1024 * 1024);
        long committedMemory = heapUsage.getCommitted() / (1024 * 1024);
        long maxMemory = heapUsage.getMax() / (1024 * 1024);

        logger.info("[Heap Status at {}] Used: {} MB / Committed: {} MB / Max: {} MB",
                phase, usedMemory, committedMemory, maxMemory);
    }

    private void printStopWatchResult(StopWatch stopWatch, long recordCount) {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        String minHeapSetting = runtimeBean.getInputArguments().stream()
                .filter(arg -> arg.startsWith("-Xms"))
                .findFirst()
                .orElse("N/A");
        String maxHeapSetting = runtimeBean.getInputArguments().stream()
                .filter(arg -> arg.startsWith("-Xmx"))
                .findFirst()
                .orElse("N/A");
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        double totalTimeSeconds = stopWatch.getTotalTimeSeconds();
        double throughput = (totalTimeSeconds > 0) ? recordCount / totalTimeSeconds : 0;

        logger.info("====== [{} Performance Test Result (JVM Setting: {} {})] ======", getModeName(), minHeapSetting, maxHeapSetting);
        logger.info("Total Records : {}", String.format("%,d", recordCount));
        logger.info("Total Time    : {} ms", totalTimeMillis);
        logger.info("Throughput    : {} ops/sec", String.format("%,.2f", throughput));
        logger.info("==================================================================");
    }
}
