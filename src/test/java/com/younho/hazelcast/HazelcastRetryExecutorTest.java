package com.younho.hazelcast;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = HazelcastRetryExecutorTest.TestContextConfiguration.class)
@DisplayName("HazelcastRetryExecutor 기능 테스트 (Spring Framework)")
class HazelcastRetryExecutorTest {

    @Configuration
    @EnableRetry
    static class TestContextConfiguration {
        @Bean
        public HazelcastRetryExecutor hazelcastRetryExecutor() {
            return new HazelcastRetryExecutor();
        }
    }

    @Autowired
    private HazelcastRetryExecutor retryExecutor;

    @Mock
    private IMap<Long, DCOLDataHist> mockMap;

    @Mock
    private FlakeIdGenerator mockIdGenerator;

    @Mock
    private PagingPredicate<Long, DCOLDataHist> mockPagingPredicate;

    @Mock
    private Predicate<Long, DCOLDataHist> mockPredicate;

    @Mock
    private EntryProcessor<Long, DCOLDataHist, Void> mockProcessor;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("get() 실패 시, 3번 재시도 후 성공한 결과를 반환한다")
    void get_SuccessAfterRetries() {
        // Arrange
        long testId = 1L;
        DCOLDataHist expectedData = new DCOLDataHist();
        expectedData.setId(testId);

        when(mockMap.get(testId))
                .thenThrow(new HazelcastException("First attempt failed"))
                .thenThrow(new HazelcastException("Second attempt failed"))
                .thenReturn(expectedData);

        // Act
        DCOLDataHist actualData = retryExecutor.get(mockMap, testId);

        // Assert
        verify(mockMap, times(3)).get(testId);
        assertThat(actualData).isEqualTo(expectedData);
    }

    @Test
    @DisplayName("getValues() 실패 시, 3번 재시도 후 성공한다")
    void getValues_SuccessAfterRetries() {
        // Arrange
        List<DCOLDataHist> expectedValues = Collections.singletonList(new DCOLDataHist());
        when(mockMap.values(any(PagingPredicate.class)))
                .thenThrow(new HazelcastException("First attempt failed"))
                .thenThrow(new HazelcastException("Second attempt failed"))
                .thenReturn(expectedValues);

        // Act
        // 'var' 대신 명시적 타입 Collection<DCOLDataHist> 사용
        Collection<DCOLDataHist> actualValues = retryExecutor.getValues(mockMap, mockPagingPredicate);

        // Assert
        verify(mockMap, times(3)).values(mockPagingPredicate);
        assertThat(actualValues).isEqualTo(expectedValues);
    }

    @Test
    @DisplayName("put() 실패 시, 3번 재시도 후 성공한다")
    void put_SuccessAfterRetries() {
        // Arrange
        long testId = 1L;
        DCOLDataHist testData = new DCOLDataHist();
        testData.setId(testId);

        when(mockMap.put(eq(testId), any(DCOLDataHist.class)))
                .thenThrow(new HazelcastException("First attempt failed"))
                .thenThrow(new HazelcastException("Second attempt failed"))
                .thenReturn(null);

        // Act
        retryExecutor.put(mockMap, testId, testData);

        // Assert
        verify(mockMap, times(3)).put(eq(testId), any(DCOLDataHist.class));
    }

    @Test
    @DisplayName("putAll() 실패 시, 3번 재시도 후 성공한다")
    void putAll_SuccessAfterRetries() {
        // Arrange
        Map<Long, DCOLDataHist> testMap = new HashMap<>();
        testMap.put(1L, new DCOLDataHist());

        doThrow(new HazelcastException("First attempt failed"))
                .doThrow(new HazelcastException("Second attempt failed"))
                .doNothing()
                .when(mockMap).putAll(anyMap());

        // Act
        retryExecutor.putAll(mockMap, testMap);

        // Assert
        verify(mockMap, times(3)).putAll(testMap);
    }

    @Test
    @DisplayName("delete() 실패 시, 3번 재시도 후 성공한다")
    void delete_SuccessAfterRetries() {
        // Arrange
        long testId = 1L;
        doThrow(new HazelcastException("First attempt failed"))
                .doThrow(new HazelcastException("Second attempt failed"))
                .doNothing()
                .when(mockMap).delete(testId);

        // Act
        retryExecutor.delete(mockMap, testId);

        // Assert
        verify(mockMap, times(3)).delete(testId);
    }

    @Test
    @DisplayName("executeOnEntries() 실패 시, 3번 재시도 후 성공한다")
    void executeOnEntries_SuccessAfterRetries() {
        // Arrange
        doThrow(new HazelcastException("First attempt failed"))
                .doThrow(new HazelcastException("Second attempt failed"))
                .doReturn(null)
                .when(mockMap).executeOnEntries(any(EntryProcessor.class), any(Predicate.class));

        // Act
        retryExecutor.executeOnEntries(mockMap, mockPredicate, mockProcessor);

        // Assert
        verify(mockMap, times(3)).executeOnEntries(mockProcessor, mockPredicate);
    }


    @Test
    @DisplayName("newId() 실패 시, 3번 재시도 후 성공한다")
    void newId_SuccessAfterRetries() {
        // Arrange
        long expectedId = 12345L;
        when(mockIdGenerator.newId())
                .thenThrow(new HazelcastException("First attempt failed"))
                .thenThrow(new HazelcastException("Second attempt failed"))
                .thenReturn(expectedId);

        // Act
        long actualId = retryExecutor.newId(mockIdGenerator);

        // Assert
        verify(mockIdGenerator, times(3)).newId();
        assertThat(actualId).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("최대 재시도(3번) 횟수 초과 시, 최종적으로 예외를 던진다")
    void get_ThrowsExceptionAfterMaxRetries() {
        // Arrange
        long testId = 1L;
        when(mockMap.get(testId)).thenThrow(new HazelcastException("Always fail"));

        // Act & Assert
        assertThrows(HazelcastException.class, () -> retryExecutor.get(mockMap, testId));
        verify(mockMap, times(3)).get(testId);
    }
}