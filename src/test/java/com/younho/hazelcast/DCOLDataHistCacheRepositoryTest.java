package com.younho.hazelcast;

import com.hazelcast.map.IMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class DCOLDataHistCacheRepositoryTest extends AbstractDCOLDataHistRepositoryTest {

    @Autowired
    private DCOLDataHistHazelcastRepository cacheRepository;

    @Autowired
    private HazelcastManager hazelcastManager;

    private IMap<Long, DCOLDataHist> dcolHistMap;

    @Override
    protected DCOLDataHistRepository createRepository() {
        this.dcolHistMap = hazelcastManager.getInstance().getMap("dcolHist");
        return cacheRepository;
    }

    @Override
    protected void cleanup() {
        if (dcolHistMap != null) {
            dcolHistMap.clear();
        }
    }

    @Test
    @DisplayName("[Cache Mode][get] 성공: ID로 데이터 정확히 조회")
    void get_Success_ShouldReturnCorrectData() {
        DCOLDataHist data = createDummyData("EQP_A", "WORK_1", "CJOB_1", "PJOB_1");
        repository.save(data);
        Long savedId = data.getId();

        DCOLDataHist foundData = repository.get(savedId);

        assertNotNull(foundData);
        assertThat(data).usingRecursiveComparison().isEqualTo(foundData);
    }

    @Test
    @DisplayName("[Cache Mode][get] Edge: 존재하지 않는 ID로 조회 시 null 반환")
    void get_Edge_NonExistentId_ShouldReturnNull() {
        assertNull(repository.get(9999L));
    }

    @Test
    @DisplayName("[Cache Mode][getAll] 성공: 모든 데이터 조회")
    void getAll_Success_ShouldReturnAllData() {
        DCOLDataHist data1 = createDummyData("EQP_A", "WORK_1", "CJOB_1", "PJOB_1");
        DCOLDataHist data2 = createDummyData("EQP_B", "WORK_2", "CJOB_2", "PJOB_2");

        repository.save(data1);
        repository.save(data2);

        List<DCOLDataHist> allData = repository.getAll();

        assertEquals(2, allData.size());
        assertThat(allData).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(data1, data2);
    }

    @Test
    @DisplayName("[Cache Mode][getAll] Edge: 데이터가 없을 때 빈 리스트 반환")
    void getAll_Edge_WhenEmpty_ShouldReturnEmptyList() {
        List<DCOLDataHist> allData = repository.getAll();
        assertNotNull(allData);
        assertTrue(allData.isEmpty());
    }

    @Test
    @DisplayName("[Cache Mode][getByAttribute] 성공: 단일 속성으로 데이터 조회")
    void getByAttribute_Success_ShouldReturnMatchingData() {
        DCOLDataHist data1 = createDummyData("EQP_X", "WORK_1", "CJOB_1", "PJOB_1");
        DCOLDataHist data2 = createDummyData("EQP_X", "WORK_2", "CJOB_2", "PJOB_2");

        repository.save(data1);
        repository.save(data2);

        List<DCOLDataHist> results = repository.getByAttribute("eqpId", "EQP_X");

        assertEquals(2, results.size());
        assertThat(results).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(data1, data2);
    }

    @Test
    @DisplayName("[Cache Mode][getByAttribute] Edge: 조회 결과가 없을 때 빈 리스트 반환")
    void getByAttribute_Edge_NoMatchingData_ShouldReturnEmptyList() {
        List<DCOLDataHist> results = repository.getByAttribute("eqpId", "NON_EXISTENT");
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("[Cache Mode][getByAttributes] 성공: 여러 속성으로 데이터 조회")
    void getByAttributes_Success_ShouldReturnMatchingData() {
        DCOLDataHist data1 = createDummyData("EQP_C", "WORK_3", "CJOB_3", "PJOB_3");
        DCOLDataHist data2 = createDummyData("EQP_C", "WORK_3", "CJOB_4", "PJOB_4");
        DCOLDataHist data3 = createDummyData("EQP_C", "WORK_3", "CJOB_4", "PJOB_5");

        repository.save(data1);
        repository.save(data2);
        repository.save(data3);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("eqpId", "EQP_C");
        attributes.put("controlJobId", "CJOB_4");

        List<DCOLDataHist> results = repository.getByAttributes(attributes);

        assertEquals(2, results.size());
        assertThat(results).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(data2, data3);
    }

    @Test
    @DisplayName("[Cache Mode][getByAttributes] 성공: 복합 정렬 기준에 따라 정확하게 정렬된 리스트를 반환한다")
    void getByAttributes_Success_ShouldReturnResultsSortedByComplexComparator() {
        DCOLDataHist data1 = createDummyData("EQP_B", "WORK_2", "CJOB_1", "PJOB_1"); // 4번째
        DCOLDataHist data2 = createDummyData("EQP_A", "WORK_2", "CJOB_1", "PJOB_1"); // 2번째
        DCOLDataHist data3 = createDummyData("EQP_B", "WORK_1", "CJOB_2", "PJOB_1"); // 3번째
        DCOLDataHist data4 = createDummyData("EQP_A", "WORK_1", "CJOB_1", "PJOB_1"); // 1번째

        repository.save(data1);
        repository.save(data2);
        repository.save(data3);
        repository.save(data4);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("processJobId", "PJOB_1");

        List<DCOLDataHist> results = repository.getByAttributes(attributes);

        assertEquals(4, results.size());
        assertThat(results).usingRecursiveFieldByFieldElementComparator().containsExactly(data4, data2, data3, data1);
    }

    @Test
    @DisplayName("[Cache Mode][getByAttributes] Edge: null 또는 빈 속성맵 전달 시 빈 리스트 반환")
    void getByAttributes_Edge_NullOrEmptyMap_ShouldReturnEmptyList() {
        assertTrue(repository.getByAttributes(null).isEmpty());
        assertTrue(repository.getByAttributes(Collections.emptyMap()).isEmpty());
    }

    @Test
    @DisplayName("[Cache Mode][update] 성공: 기존 데이터 수정")
    void update_Success_ShouldBeModified() {
        DCOLDataHist data = createDummyData("EQP_E", "WORK_5", "CJOB_5", "PJOB_5");
        repository.save(data);
        data.setDcolValue("UPDATED_VALUE");

        repository.update(data);

        DCOLDataHist updatedData = repository.get(data.getId());
        assertThat(data).usingRecursiveComparison().isEqualTo(updatedData);
    }

    @Test
    @DisplayName("[Cache Mode][update] Edge: ID가 없는 데이터 수정 시 예외 발생")
    void update_Edge_DataWithNullId_ShouldThrowException() {
        DCOLDataHist dataWithoutId = createDummyData("EQP_E", "WORK_5", "CJOB_5", "PJOB_5");
        assertThrows(IllegalArgumentException.class, () -> repository.update(dataWithoutId));
    }

    @Test
    @DisplayName("[Cache Mode][delete] 성공: 특정 데이터 삭제")
    void delete_Success_ShouldBeRemoved() {
        DCOLDataHist data = createDummyData("EQP_G", "WORK_7", "CJOB_7", "PJOB_7");
        repository.save(data);
        Long id = data.getId();

        repository.delete(data);

        assertNull(repository.get(id));
    }

    @Test
    @DisplayName("[Cache Mode][delete] Edge: null 데이터 삭제 시 에러 없이 종료")
    void delete_Edge_NullData_ShouldDoNothing() {
        repository.save(createDummyData("A", "B", "C", "D"));
        assertDoesNotThrow(() -> repository.delete(null));
        assertEquals(1, repository.getAll().size());
    }

    @Test
    @DisplayName("[Cache Mode][deleteByAttributes] 성공: 조건에 맞는 데이터 삭제")
    void deleteByAttributes_Success_ShouldRemoveMatchingData() {
        DCOLDataHist dataToKeep = createDummyData("EQP_H", "WORK_8", "CJOB_8", "PJOB_8");
        DCOLDataHist dataToDelete = createDummyData("EQP_I", "WORK_9", "CJOB_9", "PJOB_9");
        repository.save(dataToKeep);
        repository.save(dataToDelete);
        Map<String, Object> attributes = Collections.singletonMap("eqpId", "EQP_I");

        repository.deleteByAttributes(attributes);

        assertEquals(1, repository.getAll().size());
        assertNotNull(repository.get(dataToKeep.getId()));
    }

    @Test
    @DisplayName("[Cache Mode][save] 성공: 신규 데이터 저장 시 ID 자동 생성")
    void save_Success_ShouldBeSavedWithGeneratedId() {
        DCOLDataHist data = createDummyData("EQP_S", "WORK_S", "CJOB_S", "PJOB_S");

        repository.save(data);

        assertNotNull(data.getId());
        assertNotNull(repository.get(data.getId()));
    }

    @Test
    @DisplayName("[Cache Mode][saveOrUpdate] 성공: ID 없을 시 save, 있을 시 update 동작")
    void saveOrUpdate_Success_ShouldSaveNewAndUpdateExisting() {
        DCOLDataHist newData = createDummyData("EQP_F", "WORK_6", "CJOB_6", "PJOB_6");
        repository.saveOrUpdate(newData);
        Long newId = newData.getId();
        assertNotNull(newId);

        newData.setDcolValue("UPDATED_VALUE");
        repository.saveOrUpdate(newData);

        assertEquals("UPDATED_VALUE", repository.get(newId).getDcolValue());
        assertEquals(1, repository.getAll().size());
    }


    @Test
    @DisplayName("[Cache Mode][saveOrUpdateAll] 성공: 여러 데이터 한번에 저장 및 수정")
    void saveOrUpdateAll_Success_ShouldProcessBatchCorrectly() {
        DCOLDataHist existingData = createDummyData("EQP_J", "WORK_10", "CJOB_11", "PJOB_11");
        repository.save(existingData);

        existingData.setDcolValue("BATCH_UPDATED");
        DCOLDataHist newData = createDummyData("EQP_K", "WORK_11", "CJOB_12", "PJOB_12");

        repository.saveOrUpdateAll(Arrays.asList(existingData, newData));

        assertEquals(2, repository.getAll().size());
        assertThat(repository.getAll()).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(existingData, newData);
    }
    @Test

    @DisplayName("[Cache Mode][deleteAll] 성공: 여러 데이터 한번에 삭제")
    void deleteAll_Success_ShouldRemoveAllGivenData() {
        DCOLDataHist data1 = createDummyData("EQP_M", "WORK_13", "CJOB_14", "PJOB_14");
        DCOLDataHist data2 = createDummyData("EQP_N", "WORK_14", "CJOB_15", "PJOB_15");
        repository.save(data1);
        repository.save(data2);

        repository.deleteAll(Arrays.asList(data1, data2));

        assertEquals(0, repository.getAll().size());
    }
}