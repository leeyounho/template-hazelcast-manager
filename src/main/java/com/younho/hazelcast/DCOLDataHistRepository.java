package com.younho.hazelcast;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

interface DCOLDataHistRepository {
    DCOLDataHist get(Serializable id);
    List<DCOLDataHist> getAll();
    List<DCOLDataHist> getByAttribute(String attrName, Object attrValue);
    List<DCOLDataHist> getByAttributes(Map<String, Object> attributes);
    void update(DCOLDataHist dcolDataHist);
    void delete(DCOLDataHist data);
    void deleteAll(Collection<DCOLDataHist> records);
    void deleteByAttribute(String attrName, Object attrValue);
    void deleteByAttributes(Map<String, Object> attributes);
    void deleteByEqpWork(String eqpId, String workId);
    void deleteByEqpWorkProcJob(String eqpId, String workId, String processJobId);
    void save(DCOLDataHist dcolDataHist);
    void saveOrUpdate(DCOLDataHist dcolDataHist);
    void saveOrUpdateAll(Collection<DCOLDataHist> records);
}
