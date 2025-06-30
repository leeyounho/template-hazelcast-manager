package com.younho.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Component
public class DCOLDataHistCacheRepository implements DCOLDataHistRepository {
    private final HazelcastInstance hazelcastInstance;
    private IMap<Long, DCOLDataHist> dcolHistMap;
    private FlakeIdGenerator idGenerator;

    private static final Comparator<Map.Entry<Long, DCOLDataHist>> DCOL_DATA_HIST_COMPARATOR =
            Comparator.comparing((Map.Entry<Long, DCOLDataHist> entry) -> entry.getValue().getEqpId())
                    .thenComparing(entry -> entry.getValue().getWorkId())
                    .thenComparing(entry -> entry.getValue().getControlJobId())
                    .thenComparing(entry -> entry.getValue().getProcessJobId())
                    // no index
//                    .thenComparing(entry -> entry.getValue().getDcolDate())
                    .thenComparing(entry -> entry.getValue().getDcolName())
                    .thenComparing(entry -> entry.getValue().getDcolOrder());

    @Autowired
    public DCOLDataHistCacheRepository(HazelcastManager hazelcastManager) {
        this.hazelcastInstance = hazelcastManager.getInstance();
        if (hazelcastInstance != null) {
            this.dcolHistMap = hazelcastInstance.getMap("dcolHist");
            this.idGenerator = hazelcastInstance.getFlakeIdGenerator("dcolHistId");
        }
    }

    @Override
    public DCOLDataHist get(Serializable id) {
        return dcolHistMap.get(id);
    }

    @Override
    public List<DCOLDataHist> getAll() { // TODO 얘는 삭제해야겠다
        PagingPredicate<Long, DCOLDataHist> pagingPredicate = Predicates.pagingPredicate(
                Predicates.alwaysTrue(),
                DCOL_DATA_HIST_COMPARATOR,
                Integer.MAX_VALUE
        );

        Collection<DCOLDataHist> sortedValues = dcolHistMap.values(pagingPredicate);

        return new ArrayList<>(sortedValues);
    }

    @Override
    public List<DCOLDataHist> getByAttribute(String attrName, Object attrValue) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(attrName, attrValue);
        return getByAttributes(attributes);
    }

    @Override
    public List<DCOLDataHist> getByAttributes(Map<String, Object> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return Collections.emptyList(); // TODO getAll 하면 OOM 날 수도 있으니 null 또는 empty list return 필요
        }
        Predicate<Long, DCOLDataHist> predicate = buildPredicateFromAttributes(attributes);

        // TODO 성능 확인 필요
        // 모든 결과를 한 번에 가져오기 위해 페이지 크기를 큰 값(예: Integer.MAX_VALUE)으로 설정
        PagingPredicate<Long, DCOLDataHist> pagingPredicate = Predicates.pagingPredicate(predicate, DCOL_DATA_HIST_COMPARATOR, Integer.MAX_VALUE);

        Collection<DCOLDataHist> sortedValues = dcolHistMap.values(pagingPredicate);
        return new ArrayList<>(sortedValues);
    }

    @Override
    public void update(DCOLDataHist dcolDataHist) {
        if (dcolDataHist == null || dcolDataHist.getId() == null) {
            throw new IllegalArgumentException("ID cannot be null for an update operation."); // TODO DB랑 동작 동일하게 맞추자
        }
        dcolHistMap.put(dcolDataHist.getId(), dcolDataHist);
    }

    @Override
    public void delete(DCOLDataHist data) {
        if (data != null && data.getId() != null) {
            dcolHistMap.delete(data.getId());
        }
    }

    @Override
    public void deleteByAttribute(String attrName, Object attrValue) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(attrName, attrValue);
        deleteByAttributes(attributes);
    }

    @Override
    public void deleteByAttributes(Map<String, Object> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return;
        }
        Predicate<Long, DCOLDataHist> predicate = buildPredicateFromAttributes(attributes);

        dcolHistMap.executeOnEntries((EntryProcessor<Long, DCOLDataHist, Void>) entry -> {
            entry.setValue(null);
            return null;
        }, predicate);
    }

    @Override
    public void deleteByEqpWork(String eqpId, String workId) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("eqpId", eqpId);
        attributes.put("workId", workId);

        deleteByAttributes(attributes);
    }

    @Override
    public void deleteByEqpWorkProcJob(String eqpId, String workId, String processJobId) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("eqpId", eqpId);
        attributes.put("workId", workId);
        attributes.put("processJobId", processJobId);

        deleteByAttributes(attributes);
    }

    @Override
    public void save(DCOLDataHist dcolDataHist) {
        long newId = idGenerator.newId();
        dcolDataHist.setId(newId);
        dcolHistMap.put(newId, dcolDataHist);
    }

    @Override
    public void saveOrUpdate(DCOLDataHist dcolDataHist) {
        if (dcolDataHist.getId() == null) {
            long newId = idGenerator.newId();
            dcolDataHist.setId(newId);
        }
        dcolHistMap.put(dcolDataHist.getId(), dcolDataHist);
    }

    @Override
    public void saveOrUpdateAll(Collection<DCOLDataHist> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        Map<Long, DCOLDataHist> recordMap = new HashMap<>();
        for (DCOLDataHist record : records) {
            if (record.getId() == null) {
                record.setId(idGenerator.newId());
            }
            recordMap.put(record.getId(), record);
        }
        dcolHistMap.putAll(recordMap);
    }

    @Override
    public void deleteAll(Collection<DCOLDataHist> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        records.forEach(this::delete);
    }

    private Predicate<Long, DCOLDataHist> buildPredicateFromAttributes(Map<String, Object> attributes) {
        List<Predicate<Long, DCOLDataHist>> predicateList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            if (entry.getValue() instanceof Comparable) {
                predicateList.add(Predicates.equal(entry.getKey(), (Comparable) entry.getValue()));
            } else {
                throw new IllegalArgumentException("Attribute value for key '" + entry.getKey() + "' is not of a comparable type.");
            }
        }
        return Predicates.and(predicateList.toArray(new Predicate[0]));
    }
}
